package lumien.randomthings.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class PlatformBlock extends Block {
    private static final VoxelShape PLATFORM_SHAPE = Block.box(0, 14, 0, 16, 16, 16);

    public PlatformBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    protected VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return PLATFORM_SHAPE;
    }

    @Override
    protected VoxelShape getCollisionShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (context instanceof EntityCollisionContext entityContext) {
            Entity entity = entityContext.getEntity();
            if (entity != null) {
                if (entity.getY() < pos.getY() + 14.0D / 16.0D) {
                    return Shapes.empty();
                }
                if (entity instanceof Player player && player.isShiftKeyDown() && entity.getDeltaMovement().y <= 0.0D) {
                    return Shapes.empty();
                }
            }
        }
        return PLATFORM_SHAPE;
    }
}
