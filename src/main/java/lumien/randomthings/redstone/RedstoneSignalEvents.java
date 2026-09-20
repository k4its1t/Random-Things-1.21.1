package lumien.randomthings.redstone;

import lumien.randomthings.RandomThings;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = RandomThings.MOD_ID)
public final class RedstoneSignalEvents {
    private RedstoneSignalEvents() {
    }

    @SubscribeEvent
    public static void afterLevelTick(LevelTickEvent.Post event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            RedstoneSignalSavedData.tick(serverLevel);
        }
    }
}
