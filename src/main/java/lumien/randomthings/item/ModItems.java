package lumien.randomthings.item;

import lumien.randomthings.RandomThings;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomModelData;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RandomThings.MOD_ID);

    public static final DeferredItem<Item> BLAZE_AND_STEEL = ITEMS.register("blazeandsteel", () ->
            new BlazeAndSteelItem(new Item.Properties().stacksTo(1).durability(64)));
    public static final DeferredItem<BottleOfAirItem> BOTTLE_OF_AIR = ITEMS.register("bottleofair", () ->
            new BottleOfAirItem(new Item.Properties().rarity(Rarity.RARE)));
    public static final DeferredItem<StableEnderPearlItem> STABLE_ENDER_PEARL = ITEMS.register("stableenderpearl", () ->
            new StableEnderPearlItem(new Item.Properties()));
    public static final DeferredItem<RedstoneToolItem> REDSTONE_TOOL = ITEMS.register("redstonetool", () ->
            new RedstoneToolItem(new Item.Properties().stacksTo(1)));
    public static final DeferredItem<RedstoneActivatorItem> REDSTONE_ACTIVATOR = ITEMS.register("redstoneactivator", () ->
            new RedstoneActivatorItem(new Item.Properties().stacksTo(1)
                    .component(DataComponents.CUSTOM_MODEL_DATA, new CustomModelData(1))));
    public static final DeferredItem<EscapeRopeItem> ESCAPE_ROPE = ITEMS.register("escaperope", () ->
            new EscapeRopeItem(new Item.Properties().stacksTo(1).durability(20)));

    private ModItems() {
    }
}
