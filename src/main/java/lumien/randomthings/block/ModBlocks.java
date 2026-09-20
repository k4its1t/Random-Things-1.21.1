package lumien.randomthings.block;

import lumien.randomthings.RandomThings;
import lumien.randomthings.item.ReturningBlockOfSticksItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(RandomThings.MOD_ID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(RandomThings.MOD_ID);

    private static BlockBehaviour.Properties woodProperties() {
        return BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(1.5F).sound(SoundType.WOOD).noOcclusion();
    }

    public static final DeferredBlock<Block> PLATFORM = BLOCKS.register("platform", () -> new PlatformBlock(woodProperties()));
    public static final DeferredBlock<Block> PLATFORM_SPRUCE = BLOCKS.register("platform_spruce", () -> new PlatformBlock(woodProperties()));
    public static final DeferredBlock<Block> PLATFORM_BIRCH = BLOCKS.register("platform_birch", () -> new PlatformBlock(woodProperties()));
    public static final DeferredBlock<Block> PLATFORM_JUNGLE = BLOCKS.register("platform_jungle", () -> new PlatformBlock(woodProperties()));
    public static final DeferredBlock<Block> PLATFORM_ACACIA = BLOCKS.register("platform_acacia", () -> new PlatformBlock(woodProperties()));
    public static final DeferredBlock<Block> PLATFORM_DARK_OAK = BLOCKS.register("platform_darkoak", () -> new PlatformBlock(woodProperties()));

    public static final DeferredBlock<Block> RAINBOW_LAMP = BLOCKS.register("rainbowlamp", () -> new RainbowLampBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_BLUE).strength(0.3F)
                    .sound(SoundType.GLASS).lightLevel(state -> 15)));

    public static final DeferredBlock<Block> BLOCK_OF_STICKS = BLOCKS.register("blockofsticks", () -> new StickBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD).strength(0.2F)
                    .sound(SoundType.WOOD).noOcclusion()));

    public static final DeferredBlock<Block> FERTILIZED_DIRT = BLOCKS.register("fertilizeddirt", () -> new FertilizedDirtBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.DIRT).strength(0.6F)
                    .sound(SoundType.GRAVEL).randomTicks()));

    public static final DeferredBlock<Block> SUPER_LUBRICENT_STONE = BLOCKS.register("superlubricentstone", () -> new SuperLubricentIceBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.STONE).strength(1.5F).friction(1.0F / 0.98F)));

    public static final DeferredBlock<Block> SUPER_LUBRICENT_PLATFORM = BLOCKS.register("superlubricentplatform", () -> new SuperLubricentPlatformBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(0.5F).friction(1.0F / 0.98F)
                    .sound(SoundType.GLASS).noOcclusion()));

    public static final DeferredBlock<Block> SUPER_LUBRICENT_ICE = BLOCKS.register("superlubricentice", () -> new SuperLubricentIceBlock(
            BlockBehaviour.Properties.of().mapColor(MapColor.ICE).strength(0.5F).friction(1.0F / 0.98F)
                    .sound(SoundType.GLASS).noOcclusion()));

    public static final DeferredBlock<Block> BLAZING_FIRE = BLOCKS.register("blazingfire", () -> new BlazingFireBlock(
            BlockBehaviour.Properties.ofFullCopy(Blocks.FIRE).noLootTable()));

    public static final DeferredBlock<Block> BASIC_REDSTONE_INTERFACE = BLOCKS.register("basicredstoneinterface", () ->
            new BasicRedstoneInterfaceBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE)
                    .strength(2.0F).sound(SoundType.STONE)));

    public static final DeferredItem<BlockItem> PLATFORM_ITEM = blockItem("platform", PLATFORM);
    public static final DeferredItem<BlockItem> PLATFORM_SPRUCE_ITEM = blockItem("platform_spruce", PLATFORM_SPRUCE);
    public static final DeferredItem<BlockItem> PLATFORM_BIRCH_ITEM = blockItem("platform_birch", PLATFORM_BIRCH);
    public static final DeferredItem<BlockItem> PLATFORM_JUNGLE_ITEM = blockItem("platform_jungle", PLATFORM_JUNGLE);
    public static final DeferredItem<BlockItem> PLATFORM_ACACIA_ITEM = blockItem("platform_acacia", PLATFORM_ACACIA);
    public static final DeferredItem<BlockItem> PLATFORM_DARK_OAK_ITEM = blockItem("platform_darkoak", PLATFORM_DARK_OAK);
    public static final DeferredItem<BlockItem> RAINBOW_LAMP_ITEM = blockItem("rainbowlamp", RAINBOW_LAMP);
    public static final DeferredItem<BlockItem> BLOCK_OF_STICKS_ITEM = blockItem("blockofsticks", BLOCK_OF_STICKS);
    public static final DeferredItem<ReturningBlockOfSticksItem> RETURNING_BLOCK_OF_STICKS_ITEM = ITEMS.register(
            "returningblockofsticks",
            () -> new ReturningBlockOfSticksItem(BLOCK_OF_STICKS.get(), new Item.Properties()));
    public static final DeferredItem<BlockItem> FERTILIZED_DIRT_ITEM = blockItem("fertilizeddirt", FERTILIZED_DIRT);
    public static final DeferredItem<BlockItem> SUPER_LUBRICENT_STONE_ITEM = blockItem("superlubricentstone", SUPER_LUBRICENT_STONE);
    public static final DeferredItem<BlockItem> SUPER_LUBRICENT_PLATFORM_ITEM = blockItem("superlubricentplatform", SUPER_LUBRICENT_PLATFORM);
    public static final DeferredItem<BlockItem> SUPER_LUBRICENT_ICE_ITEM = blockItem("superlubricentice", SUPER_LUBRICENT_ICE);
    public static final DeferredItem<BlockItem> BASIC_REDSTONE_INTERFACE_ITEM = blockItem(
            "basicredstoneinterface", BASIC_REDSTONE_INTERFACE);

    private static DeferredItem<BlockItem> blockItem(String name, DeferredBlock<Block> block) {
        return ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    private ModBlocks() {
    }
}
