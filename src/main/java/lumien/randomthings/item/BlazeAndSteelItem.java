package lumien.randomthings.item;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;

public class BlazeAndSteelItem extends Item {
    public BlazeAndSteelItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos target = context.getClickedPos().relative(context.getClickedFace());
        ItemStack stack = context.getItemInHand();
        @Nullable Player player = context.getPlayer();

        if (player == null || !player.mayUseItemAt(target, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }
        if (level.isEmptyBlock(target) && Blocks.FIRE.defaultBlockState().canSurvive(level, target)) {
            level.playSound(player, target, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS,
                    1.0F, level.getRandom().nextFloat() * 0.8F + 0.8F);
            level.setBlock(target, Blocks.FIRE.defaultBlockState(), 11);
            if (!level.isClientSide) {
                stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(context.getHand()));
            }
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        return InteractionResult.PASS;
    }
}
