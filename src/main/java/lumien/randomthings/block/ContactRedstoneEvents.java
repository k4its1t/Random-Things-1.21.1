package lumien.randomthings.block;

import lumien.randomthings.RandomThings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = RandomThings.MOD_ID)
public final class ContactRedstoneEvents {
    private ContactRedstoneEvents() {
    }

    @SubscribeEvent
    public static void rightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() == InteractionHand.MAIN_HAND && !event.getLevel().isClientSide) {
            activateNeighbor(event.getLevel(), event.getPos());
        }
    }

    public static boolean activateNeighbor(Level level, BlockPos clickedPos) {
        for (Direction direction : Direction.values()) {
            BlockPos contactPos = clickedPos.relative(direction);
            BlockState state = level.getBlockState(contactPos);
            if (state.getBlock() instanceof ContactRedstoneBlock contactBlock
                    && state.getValue(ContactRedstoneBlock.FACING).getOpposite() == direction) {
                contactBlock.activate(level, contactPos, direction.getOpposite());
                return true;
            }
        }
        return false;
    }
}
