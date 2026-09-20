package lumien.randomthings;

import lumien.randomthings.block.ModBlocks;
import lumien.randomthings.item.ModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(RandomThings.MOD_ID)
public final class RandomThings {
    public static final String MOD_ID = "randomthings";

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = CREATIVE_TABS.register("main", () ->
            CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.randomthings"))
                    .icon(() -> new ItemStack(Items.BLAZE_POWDER))
                    .displayItems((parameters, output) -> {
                        output.accept(ModBlocks.PLATFORM_ITEM.get());
                        output.accept(ModBlocks.PLATFORM_SPRUCE_ITEM.get());
                        output.accept(ModBlocks.PLATFORM_BIRCH_ITEM.get());
                        output.accept(ModBlocks.PLATFORM_JUNGLE_ITEM.get());
                        output.accept(ModBlocks.PLATFORM_ACACIA_ITEM.get());
                        output.accept(ModBlocks.PLATFORM_DARK_OAK_ITEM.get());
                        output.accept(ModBlocks.RAINBOW_LAMP_ITEM.get());
                        output.accept(ModBlocks.BLOCK_OF_STICKS_ITEM.get());
                        output.accept(ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get());
                        output.accept(ModBlocks.FERTILIZED_DIRT_ITEM.get());
                        output.accept(ModBlocks.SUPER_LUBRICENT_STONE_ITEM.get());
                        output.accept(ModBlocks.SUPER_LUBRICENT_PLATFORM_ITEM.get());
                        output.accept(ModBlocks.SUPER_LUBRICENT_ICE_ITEM.get());
                        output.accept(ModItems.BLAZE_AND_STEEL.get());
                        output.accept(ModItems.BOTTLE_OF_AIR.get());
                        output.accept(ModItems.STABLE_ENDER_PEARL.get());
                    })
                    .build());

    public RandomThings(IEventBus modEventBus) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModBlocks.ITEMS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        CREATIVE_TABS.register(modEventBus);
    }
}
