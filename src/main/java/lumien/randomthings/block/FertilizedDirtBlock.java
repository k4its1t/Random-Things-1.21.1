package lumien.randomthings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.CactusBlock;
import net.minecraft.world.level.block.DeadBushBlock;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.SugarCaneBlock;
import net.minecraft.world.level.block.VineBlock;
import net.minecraft.world.level.block.WaterlilyBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.util.TriState;

public class FertilizedDirtBlock extends Block {
    public static final BooleanProperty TILLED = BooleanProperty.create("tilled");
    private static final VoxelShape TILLED_SHAPE = Block.box(0, 0, 0, 16, 15, 16);

    private enum PlantType {
        DESERT,
        NETHER,
        CROP,
        CAVE,
        PLAINS,
        WATER,
        BEACH,
        BEETROOT,
        UNKNOWN
    }

    public FertilizedDirtBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(TILLED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(TILLED);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(TILLED) ? TILLED_SHAPE : super.getShape(state, level, pos, context);
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return true;
    }

    @Override
    public TriState canSustainPlant(BlockState state, BlockGetter level, BlockPos pos, Direction facing, BlockState plant) {
        if (facing != Direction.UP) {
            return TriState.DEFAULT;
        }

        boolean tilled = state.getValue(TILLED);
        return switch (getPlantType(plant)) {
            case DESERT, CAVE, PLAINS, BEACH -> tilled ? TriState.FALSE : TriState.TRUE;
            case NETHER, WATER -> TriState.FALSE;
            case CROP -> tilled ? TriState.TRUE : TriState.FALSE;
            case BEETROOT -> TriState.TRUE;
            case UNKNOWN -> TriState.DEFAULT;
        };
    }

    @Override
    protected void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockPos abovePos = pos.above();
        for (int i = 0; i < 3; i++) {
            BlockState above = level.getBlockState(abovePos);
            if (!above.isRandomlyTicking() || getPlantType(above) == PlantType.UNKNOWN) {
                break;
            }
            above.randomTick(level, abovePos, random);
        }
    }

    private static PlantType getPlantType(BlockState plant) {
        Block block = plant.getBlock();
        if (block == Blocks.BEETROOTS) {
            return PlantType.BEETROOT;
        }
        if (plant.is(BlockTags.CROPS)) {
            return PlantType.CROP;
        }
        if (block instanceof CactusBlock || block instanceof DeadBushBlock) {
            return PlantType.DESERT;
        }
        if (block instanceof NetherWartBlock) {
            return PlantType.NETHER;
        }
        if (block instanceof MushroomBlock || block instanceof VineBlock) {
            return PlantType.CAVE;
        }
        if (block instanceof SugarCaneBlock) {
            return PlantType.BEACH;
        }
        if (block instanceof WaterlilyBlock) {
            return PlantType.WATER;
        }
        if (block instanceof BushBlock) {
            return PlantType.PLAINS;
        }
        return PlantType.UNKNOWN;
    }
}
