package lumien.randomthings.block;

import lumien.randomthings.RandomThings;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = RandomThings.MOD_ID)
public final class FertilizedDirtEvents {
    private FertilizedDirtEvents() {
    }

    @SubscribeEvent
    public static void tillWithHoe(PlayerInteractEvent.RightClickBlock event) {
        if (!event.getItemStack().is(ItemTags.HOES)) {
            return;
        }

        var level = event.getLevel();
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        if (!state.is(ModBlocks.FERTILIZED_DIRT.get()) || state.getValue(FertilizedDirtBlock.TILLED)) {
            return;
        }

        if (event.getFace() == null || event.getFace() == net.minecraft.core.Direction.DOWN
                || !level.isEmptyBlock(pos.above())
                || !event.getEntity().mayUseItemAt(pos, event.getFace(), event.getItemStack())) {
            return;
        }

        if (!level.isClientSide) {
            level.setBlock(pos, state.setValue(FertilizedDirtBlock.TILLED, true), 11);
            event.getItemStack().hurtAndBreak(1, event.getEntity(), LivingEntity.getSlotForHand(event.getHand()));
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
    }
}
