package lumien.randomthings.block;

import net.minecraft.world.level.block.state.BlockBehaviour;

public class SuperLubricentPlatformBlock extends PlatformBlock {
    @Override
    public float getFriction(net.minecraft.world.level.block.state.BlockState state,
            net.minecraft.world.level.LevelReader level, net.minecraft.core.BlockPos pos,
            net.minecraft.world.entity.Entity entity) {
        // Living entities apply 0.91 horizontal drag; dropped items apply 0.98.
        return entity instanceof net.minecraft.world.entity.LivingEntity ? 1.0F / 0.91F : 1.0F / 0.98F;
    }

    public SuperLubricentPlatformBlock(BlockBehaviour.Properties properties) {
        super(properties);
    }
}
