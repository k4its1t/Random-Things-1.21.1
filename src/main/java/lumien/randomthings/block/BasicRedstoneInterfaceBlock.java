package lumien.randomthings.block;

import com.mojang.serialization.MapCodec;
import lumien.randomthings.block.entity.BasicRedstoneInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;

public final class BasicRedstoneInterfaceBlock extends BaseEntityBlock {
    public static final MapCodec<BasicRedstoneInterfaceBlock> CODEC = simpleCodec(BasicRedstoneInterfaceBlock::new);

    public BasicRedstoneInterfaceBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BasicRedstoneInterfaceBlockEntity(pos, state);
    }

    @Override
    protected void neighborChanged(BlockState state, Level level, BlockPos pos, net.minecraft.world.level.block.Block neighborBlock,
            BlockPos neighborPos, boolean movedByPiston) {
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, movedByPiston);
        if (!level.isClientSide && level.getBlockEntity(pos) instanceof BasicRedstoneInterfaceBlockEntity blockEntity) {
            blockEntity.refreshPower();
        }
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (!state.is(newState.getBlock())
                && level.getBlockEntity(pos) instanceof BasicRedstoneInterfaceBlockEntity blockEntity) {
            blockEntity.clearTarget();
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
