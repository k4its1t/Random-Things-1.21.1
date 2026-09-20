package lumien.randomthings.item;

import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public final class BottleOfAirItem extends Item {
    private static final int AIR_PER_TICK = 20;

    public BottleOfAirItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!isUnderwater(level, player)) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if ((isUnderwater(level, livingEntity) || livingEntity.getAirSupply() < 270)
                && remainingUseDuration % 5 == 0) {
            livingEntity.setAirSupply(Math.min(livingEntity.getMaxAirSupply(),
                    livingEntity.getAirSupply() + AIR_PER_TICK));
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72_000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    private static boolean isUnderwater(Level level, LivingEntity entity) {
        BlockPos eyePos = BlockPos.containing(entity.getX(), entity.getEyeY(), entity.getZ());
        return level.getFluidState(eyePos).is(FluidTags.WATER);
    }
}
