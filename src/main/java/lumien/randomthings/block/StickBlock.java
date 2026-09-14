package lumien.randomthings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

public class StickBlock extends Block {
    public static final BooleanProperty RETURNING = BooleanProperty.create("returning");

    public StickBlock(BlockBehaviour.Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(RETURNING, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(RETURNING);
    }

    @Override
    public ItemStack getCloneItemStack(LevelReader level, BlockPos pos, BlockState state) {
        return state.getValue(RETURNING)
                ? ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get().getDefaultInstance()
                : ModBlocks.BLOCK_OF_STICKS_ITEM.get().getDefaultInstance();
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, net.minecraft.world.entity.LivingEntity placer, net.minecraft.world.item.ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 20 * 10);
        }
    }

    @Override
    protected void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        level.removeBlock(pos, false);
        if (state.getValue(RETURNING)) {
            level.playSound(null, pos, SoundEvents.ENDERMAN_TELEPORT, SoundSource.BLOCKS, 0.6F, 1.2F);
            Player closest = level.getNearestPlayer(pos.getX(), pos.getY(), pos.getZ(), 50.0D,
                    entity -> entity instanceof Player player && !player.isCreative());
            if (closest != null) {
                closest.getInventory().placeItemBackInInventory(
                        ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get().getDefaultInstance());
            }
        } else {
            level.playSound(null, pos, SoundEvents.WOOD_BREAK, SoundSource.BLOCKS, 0.6F, 1.2F);
            level.levelEvent(2001, pos, Block.getId(state));
        }
    }
}
