package lumien.randomthings.item;

import java.util.List;

import lumien.randomthings.redstone.RedstoneSignalSavedData;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomModelData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class RedstoneActivatorItem extends Item {
    private static final int[] DURATIONS = {2, 20, 100};

    public RedstoneActivatorItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        if (context.getLevel() instanceof ServerLevel serverLevel) {
            RedstoneSignalSavedData.addSignal(serverLevel, context.getClickedPos(),
                    getDuration(stack), 15);
        }
        return InteractionResult.sidedSuccess(context.getLevel().isClientSide);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        int change = player.isShiftKeyDown() ? -1 : 1;
        int nextIndex = Math.floorMod(getDurationIndex(stack) + change, DURATIONS.length);
        setDurationIndex(stack, nextIndex);
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.randomthings.redstoneactivator.duration", getDuration(stack)));
    }

    public int getDuration(ItemStack stack) {
        return DURATIONS[getDurationIndex(stack)];
    }

    public int getDurationIndex(ItemStack stack) {
        int index = stack.getOrDefault(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1)).value();
        return index >= 0 && index < DURATIONS.length ? index : 1;
    }

    public void setDurationIndex(ItemStack stack, int index) {
        if (index < 0 || index >= DURATIONS.length) {
            throw new IllegalArgumentException("Invalid Redstone Activator duration index: " + index);
        }
        stack.set(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(index));
    }
}
