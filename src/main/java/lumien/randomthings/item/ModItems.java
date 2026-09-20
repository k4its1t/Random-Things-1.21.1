package lumien.randomthings.item;

import lumien.randomthings.RandomThings;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
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

    private ModItems() {
    }
}
