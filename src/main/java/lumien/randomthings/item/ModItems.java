package lumien.randomthings.item;

import lumien.randomthings.RandomThings;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RandomThings.MOD_ID);

    public static final DeferredItem<Item> BLAZE_AND_STEEL = ITEMS.register("blazeandsteel", () ->
            new BlazeAndSteelItem(new Item.Properties().stacksTo(1).durability(64)));

    private ModItems() {
    }
}
