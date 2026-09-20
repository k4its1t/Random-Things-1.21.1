package lumien.randomthings.block;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FireBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.portal.PortalShape;
import net.neoforged.neoforge.event.EventHooks;

public final class BlazingFireBlock extends FireBlock {
    private static final int MAX_AGE = 15;

    public BlazingFireBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected BlockState updateShape(BlockState state, Direction direction, BlockState neighborState,
            LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        return canSurvive(state, level, pos)
                ? stateWithAge(level, pos, state.getValue(AGE))
                : Blocks.AIR.defaultBlockState();
    }

    @Override
    protected BlockState getStateForPlacement(BlockGetter level, BlockPos pos) {
        return stateWithAge(level, pos, 0);
    }

    @Override
    protected void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        if (!oldState.is(state.getBlock())) {
            if (level.dimension() == Level.OVERWORLD || level.dimension() == Level.NETHER) {
                Optional<PortalShape> portal = PortalShape.findEmptyPortalShape(level, pos, Direction.Axis.X);
                portal = EventHooks.onTrySpawnPortal(level, pos, portal);
                if (portal.isPresent()) {
                    portal.get().createPortalBlocks();
                    return;
                }
            }
            if (!state.canSurvive(level, pos)) {
                level.removeBlock(pos, false);
                return;
            }
        }
        level.scheduleTick(pos, this, 15 + level.random.nextInt(10));
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.scheduleTick(pos, this, 15 + random.nextInt(10));
        if (!level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            return;
        }
        if (!state.canSurvive(level, pos)) {
            level.removeBlock(pos, false);
            return;
        }

        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        boolean fireSource = belowState.isFireSource(level, belowPos, Direction.UP);
        int age = state.getValue(AGE);

        if (!fireSource && level.isRaining() && isNearRain(level, pos)
                && random.nextFloat() < 0.2F + age * 0.03F) {
            level.removeBlock(pos, false);
            return;
        }

        int newAge = Math.min(MAX_AGE, age + random.nextInt(3) / 2);
        if (newAge != age) {
            state = state.setValue(AGE, newAge);
            level.setBlock(pos, state, Block.UPDATE_INVISIBLE);
        }

        if (!fireSource) {
            if (!hasFlammableNeighbor(level, pos)) {
                if (!belowState.isFaceSturdy(level, belowPos, Direction.UP) || age > 3) {
                    level.removeBlock(pos, false);
                }
                return;
            }
            if (!canCatchFire(level, belowPos, Direction.UP) && age == MAX_AGE && random.nextInt(4) == 0) {
                level.removeBlock(pos, false);
                return;
            }
        }

        boolean increasedBurnout = level.getBiome(pos).is(BiomeTags.INCREASED_FIRE_BURNOUT);
        int humidityModifier = increasedBurnout ? -50 : 0;
        checkBurnOut(level, pos.east(), 300 + humidityModifier, random, age, Direction.WEST);
        checkBurnOut(level, pos.west(), 300 + humidityModifier, random, age, Direction.EAST);
        checkBurnOut(level, pos.below(), 250 + humidityModifier, random, age, Direction.UP);
        checkBurnOut(level, pos.above(), 250 + humidityModifier, random, age, Direction.DOWN);
        checkBurnOut(level, pos.north(), 300 + humidityModifier, random, age, Direction.SOUTH);
        checkBurnOut(level, pos.south(), 300 + humidityModifier, random, age, Direction.NORTH);

        BlockPos.MutableBlockPos spreadPos = new BlockPos.MutableBlockPos();
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = -1; y <= 4; y++) {
                    if (x == 0 && y == 0 && z == 0) {
                        continue;
                    }

                    int spreadChance = 100 + Math.max(0, y - 1) * 100;
                    spreadPos.setWithOffset(pos, x, y, z);
                    int encouragement = getNeighborEncouragement(level, spreadPos);
                    if (encouragement <= 0) {
                        continue;
                    }

                    int chance = (encouragement + 40 + level.getDifficulty().getId() * 7) / (age + 30);
                    if (increasedBurnout) {
                        chance /= 2;
                    }
                    if (chance > 0 && random.nextInt(spreadChance) <= chance
                            && (!level.isRaining() || !isNearRain(level, spreadPos))) {
                        int spreadAge = Math.min(MAX_AGE, age + random.nextInt(5) / 4);
                        level.setBlock(spreadPos, stateWithAge(level, spreadPos, spreadAge), Block.UPDATE_ALL);
                    }
                }
            }
        }
    }

    private void checkBurnOut(Level level, BlockPos pos, int chance, RandomSource random, int age,
            Direction face) {
        BlockState target = level.getBlockState(pos);
        int flammability = target.getFlammability(level, pos, face);
        if (random.nextInt(chance) >= flammability * 4) {
            return;
        }

        target.onCaughtFire(level, pos, face, null);
        if (random.nextInt(age / 2 + 1) < 5 && !level.isRainingAt(pos)) {
            int nextAge = Math.min(MAX_AGE, age + random.nextInt(2));
            level.setBlock(pos, stateWithAge(level, pos, nextAge), Block.UPDATE_ALL);
        } else {
            level.removeBlock(pos, false);
        }
    }

    private boolean hasFlammableNeighbor(BlockGetter level, BlockPos pos) {
        for (Direction direction : Direction.values()) {
            if (canCatchFire(level, pos.relative(direction), direction.getOpposite())) {
                return true;
            }
        }
        return false;
    }

    private int getNeighborEncouragement(LevelReader level, BlockPos pos) {
        if (!level.isEmptyBlock(pos)) {
            return 0;
        }

        int encouragement = 0;
        for (Direction direction : Direction.values()) {
            BlockPos neighborPos = pos.relative(direction);
            encouragement = Math.max(encouragement, level.getBlockState(neighborPos)
                    .getFireSpreadSpeed(level, neighborPos, direction.getOpposite()));
        }
        return encouragement;
    }

    private BlockState stateWithAge(BlockGetter level, BlockPos pos, int age) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        BlockState state = defaultBlockState().setValue(AGE, age);
        if (canCatchFire(level, belowPos, Direction.UP)
                || belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
            return state;
        }

        return state
                .setValue(NORTH, canCatchFire(level, pos.north(), Direction.SOUTH))
                .setValue(EAST, canCatchFire(level, pos.east(), Direction.WEST))
                .setValue(SOUTH, canCatchFire(level, pos.south(), Direction.NORTH))
                .setValue(WEST, canCatchFire(level, pos.west(), Direction.EAST))
                .setValue(UP, canCatchFire(level, pos.above(), Direction.DOWN));
    }
}
