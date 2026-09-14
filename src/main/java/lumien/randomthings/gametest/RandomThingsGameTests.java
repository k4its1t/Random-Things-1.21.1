package lumien.randomthings.gametest;

import java.util.List;

import lumien.randomthings.RandomThings;
import lumien.randomthings.block.FertilizedDirtBlock;
import lumien.randomthings.block.ModBlocks;
import lumien.randomthings.block.RainbowLampBlock;
import lumien.randomthings.block.StickBlock;
import lumien.randomthings.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.neoforged.neoforge.common.util.TriState;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(RandomThings.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RandomThingsGameTests {
    private static final BlockPos TEST_POS = new BlockPos(2, 2, 2);

    private RandomThingsGameTests() {
    }

    @GameTest(template = "empty", timeoutTicks = 220)
    public static void returningBlockItemPlacesReturningState(GameTestHelper helper) {
        BlockPos supportPos = TEST_POS.below();
        helper.setBlock(supportPos, Blocks.STONE);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absoluteSupportPos = helper.absolutePos(supportPos);
        player.setPos(absoluteSupportPos.getX() + 0.5, absoluteSupportPos.getY() + 1.0,
                absoluteSupportPos.getZ() + 0.5);
        ItemStack stack = new ItemStack(ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absoluteSupportPos), Direction.UP,
                absoluteSupportPos, false);
        helper.assertTrue(stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, hit)).consumesAction(),
                "Returning item placement was rejected");

        BlockState placed = helper.getBlockState(TEST_POS);
        helper.assertTrue(placed.is(ModBlocks.BLOCK_OF_STICKS.get()), "Returning item did not place the sticks block");
        helper.assertTrue(placed.getValue(StickBlock.RETURNING), "Placed sticks block did not keep the returning state");
        helper.assertTrue(ModBlocks.BLOCK_OF_STICKS_ITEM.get().getDescriptionId()
                        .equals("block.randomthings.blockofsticks"),
                "Normal sticks block description id changed");
        helper.assertTrue(ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get().getDescriptionId()
                        .equals("item.randomthings.returningblockofsticks"),
                "Returning sticks block did not use its item description id");
        helper.assertTrue(
                placed.getBlock().getCloneItemStack(helper.getLevel(), helper.absolutePos(TEST_POS), placed)
                        .is(ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get()),
                "Pick block did not return the returning item");

        helper.succeedWhen(() -> helper.assertBlockNotPresent(ModBlocks.BLOCK_OF_STICKS.get(), TEST_POS));
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void blockDropsMatchPlacedVariants(GameTestHelper helper) {
        List<Block> blocks = List.of(
                ModBlocks.PLATFORM.get(),
                ModBlocks.PLATFORM_SPRUCE.get(),
                ModBlocks.PLATFORM_BIRCH.get(),
                ModBlocks.PLATFORM_JUNGLE.get(),
                ModBlocks.PLATFORM_ACACIA.get(),
                ModBlocks.PLATFORM_DARK_OAK.get(),
                ModBlocks.RAINBOW_LAMP.get(),
                ModBlocks.FERTILIZED_DIRT.get(),
                ModBlocks.SUPER_LUBRICENT_STONE.get(),
                ModBlocks.SUPER_LUBRICENT_PLATFORM.get(),
                ModBlocks.SUPER_LUBRICENT_ICE.get());

        BlockPos absolutePos = helper.absolutePos(TEST_POS);
        for (Block block : blocks) {
            BlockState state = block.defaultBlockState();
            helper.setBlock(TEST_POS, state);
            List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), absolutePos, null);
            helper.assertTrue(drops.size() == 1 && drops.getFirst().is(block.asItem()),
                    "Incorrect drop for " + block);
        }

        BlockState normalSticks = ModBlocks.BLOCK_OF_STICKS.get().defaultBlockState();
        helper.setBlock(TEST_POS, normalSticks);
        helper.assertTrue(Block.getDrops(normalSticks, helper.getLevel(), absolutePos, null).getFirst()
                .is(ModBlocks.BLOCK_OF_STICKS_ITEM.get()), "Normal sticks block dropped the wrong item");

        BlockState returningSticks = normalSticks.setValue(StickBlock.RETURNING, true);
        helper.setBlock(TEST_POS, returningSticks);
        helper.assertTrue(Block.getDrops(returningSticks, helper.getLevel(), absolutePos, null).getFirst()
                .is(ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get()), "Returning sticks block dropped the wrong item");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 30)
    public static void rainbowLampTracksRedstonePower(GameTestHelper helper) {
        BlockPos lampPos = TEST_POS;
        BlockPos powerPos = TEST_POS.east();
        helper.setBlock(lampPos, ModBlocks.RAINBOW_LAMP.get());
        helper.assertTrue(RainbowLampBlock.COLOR.getPossibleValues().size() == 16,
                "Rainbow lamp does not expose all 16 colors");

        helper.startSequence()
                .thenExecute(() -> helper.setBlock(powerPos, Blocks.REDSTONE_BLOCK))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(lampPos, RainbowLampBlock.COLOR, DyeColor.BLACK))
                .thenExecute(() -> helper.setBlock(powerPos, Blocks.AIR))
                .thenIdle(5)
                .thenExecute(() -> helper.assertBlockProperty(lampPos, RainbowLampBlock.COLOR, DyeColor.WHITE))
                .thenSucceed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void platformsAndLubricentBlocksKeepMovementProperties(GameTestHelper helper) {
        BlockPos absolutePos = helper.absolutePos(TEST_POS);
        BlockState platform = ModBlocks.PLATFORM.get().defaultBlockState();
        helper.setBlock(TEST_POS, platform);

        ItemEntity below = new ItemEntity(helper.getLevel(), absolutePos.getX() + 0.5,
                absolutePos.getY() + 0.5, absolutePos.getZ() + 0.5, ItemStack.EMPTY);
        ItemEntity above = new ItemEntity(helper.getLevel(), absolutePos.getX() + 0.5,
                absolutePos.getY() + 1.1, absolutePos.getZ() + 0.5, ItemStack.EMPTY);
        helper.assertTrue(platform.getCollisionShape(helper.getLevel(), absolutePos, CollisionContext.of(below)).isEmpty(),
                "Platform blocked an entity approaching from below");
        helper.assertFalse(platform.getCollisionShape(helper.getLevel(), absolutePos, CollisionContext.of(above)).isEmpty(),
                "Platform did not support an entity above it");

        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(absolutePos.getX() + 0.5, absolutePos.getY() + 1.1, absolutePos.getZ() + 0.5);
        player.setShiftKeyDown(true);
        helper.assertTrue(platform.getCollisionShape(helper.getLevel(), absolutePos, CollisionContext.of(player)).isEmpty(),
                "Sneaking player could not descend through platform");

        BlockState lubricent = ModBlocks.SUPER_LUBRICENT_STONE.get().defaultBlockState();
        helper.assertValueEqual(lubricent.getFriction(helper.getLevel(), absolutePos, player), 1.0F / 0.91F,
                "Living entity friction is incorrect");
        helper.assertValueEqual(lubricent.getFriction(helper.getLevel(), absolutePos, above), 1.0F / 0.98F,
                "Item friction is incorrect");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void blazeAndSteelPlacesFireAndConsumesDurability(GameTestHelper helper) {
        BlockPos supportPos = TEST_POS.below();
        helper.setBlock(supportPos, Blocks.STONE);
        BlockPos absoluteSupportPos = helper.absolutePos(supportPos);
        var player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.setPos(absoluteSupportPos.getX() + 0.5, absoluteSupportPos.getY() + 1.0,
                absoluteSupportPos.getZ() + 0.5);
        ItemStack stack = new ItemStack(ModItems.BLAZE_AND_STEEL.get());
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);

        BlockHitResult validHit = new BlockHitResult(Vec3.atCenterOf(absoluteSupportPos), Direction.UP,
                absoluteSupportPos, false);
        helper.assertTrue(stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, validHit)).consumesAction(),
                "Blaze and Steel rejected a valid fire placement");
        helper.assertTrue(helper.getBlockState(TEST_POS).is(Blocks.FIRE),
                "Blaze and Steel did not place normal fire");
        helper.assertTrue(stack.getDamageValue() == 1,
                "Blaze and Steel did not consume one durability");

        helper.setBlock(TEST_POS, Blocks.STONE);
        int damageAfterSuccess = stack.getDamageValue();
        helper.assertFalse(stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, validHit)).consumesAction(),
                "Blaze and Steel acted on an occupied target");
        helper.assertTrue(stack.getDamageValue() == damageAfterSuccess,
                "Blaze and Steel lost durability on an occupied target");

        BlockPos invalidTarget = TEST_POS.east();
        helper.setBlock(invalidTarget, Blocks.AIR);
        BlockHitResult invalidHit = new BlockHitResult(Vec3.atCenterOf(helper.absolutePos(TEST_POS)), Direction.EAST,
                helper.absolutePos(TEST_POS), false);
        helper.assertFalse(stack.useOn(new UseOnContext(player, InteractionHand.MAIN_HAND, invalidHit)).consumesAction(),
                "Blaze and Steel acted where fire cannot survive");
        helper.assertTrue(stack.getDamageValue() == damageAfterSuccess,
                "Blaze and Steel lost durability on an invalid target");
        helper.assertTrue(helper.getBlockState(invalidTarget).is(Blocks.AIR),
                "Invalid fire target was unexpectedly changed");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void fertilizedDirtAndToolTagsMatchSupportedUses(GameTestHelper helper) {
        BlockState dirt = ModBlocks.FERTILIZED_DIRT.get().defaultBlockState();
        BlockState tilled = dirt.setValue(FertilizedDirtBlock.TILLED, true);
        BlockPos absolutePos = helper.absolutePos(TEST_POS);

        helper.assertTrue(dirt.is(BlockTags.DIRT), "Fertilized dirt is missing the dirt tag");
        helper.assertTrue(dirt.is(BlockTags.MINEABLE_WITH_SHOVEL), "Fertilized dirt is missing the shovel tag");
        helper.assertTrue(ModBlocks.PLATFORM.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_AXE),
                "Platform is missing the axe tag");
        helper.assertTrue(ModBlocks.SUPER_LUBRICENT_STONE.get().defaultBlockState().is(BlockTags.MINEABLE_WITH_PICKAXE),
                "Super lubricent stone is missing the pickaxe tag");

        TriState dirtForWheat = dirt.canSustainPlant(helper.getLevel(), absolutePos, Direction.UP,
                Blocks.WHEAT.defaultBlockState());
        TriState tilledForWheat = tilled.canSustainPlant(helper.getLevel(), absolutePos, Direction.UP,
                Blocks.WHEAT.defaultBlockState());
        TriState dirtForSapling = dirt.canSustainPlant(helper.getLevel(), absolutePos, Direction.UP,
                Blocks.OAK_SAPLING.defaultBlockState());
        TriState tilledForSapling = tilled.canSustainPlant(helper.getLevel(), absolutePos, Direction.UP,
                Blocks.OAK_SAPLING.defaultBlockState());
        helper.assertTrue(dirtForWheat.isFalse() && tilledForWheat.isTrue(),
                "Crop support does not follow the tilled state");
        helper.assertTrue(dirtForSapling.isTrue() && tilledForSapling.isFalse(),
                "Plains plant support does not follow the tilled state");

        assertPlantSupport(helper, dirt, tilled, Blocks.BEETROOTS.defaultBlockState(), true, true, "beetroots");
        assertPlantSupport(helper, dirt, tilled, Blocks.CACTUS.defaultBlockState(), true, false, "cactus");
        assertPlantSupport(helper, dirt, tilled, Blocks.DEAD_BUSH.defaultBlockState(), true, false, "dead bush");
        assertPlantSupport(helper, dirt, tilled, Blocks.BROWN_MUSHROOM.defaultBlockState(), true, false, "brown mushroom");
        assertPlantSupport(helper, dirt, tilled, Blocks.VINE.defaultBlockState(), true, false, "vine");
        assertPlantSupport(helper, dirt, tilled, Blocks.SUGAR_CANE.defaultBlockState(), true, false, "sugar cane");
        assertPlantSupport(helper, dirt, tilled, Blocks.LILY_PAD.defaultBlockState(), false, false, "lily pad");
        assertPlantSupport(helper, dirt, tilled, Blocks.NETHER_WART.defaultBlockState(), false, false, "nether wart");
        helper.assertTrue(dirt.canSustainPlant(helper.getLevel(), absolutePos, Direction.UP,
                        Blocks.STONE.defaultBlockState()).isDefault(),
                "Unknown plants should defer to their own survival rules");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void allPortedRecipesMatchDefinitions(GameTestHelper helper) {
        assertRecipe(helper, "blazeandsteel", ModItems.BLAZE_AND_STEEL.get(), 1,
                Items.IRON_INGOT, Items.BLAZE_POWDER);
        assertRecipe(helper, "blockofsticks", ModBlocks.BLOCK_OF_STICKS_ITEM.get(), 16,
                Items.STICK, Items.STICK, Items.STICK, Items.STICK,
                Items.STICK, Items.STICK, Items.STICK, Items.STICK);
        assertRecipe(helper, "returningblockofsticks", ModBlocks.RETURNING_BLOCK_OF_STICKS_ITEM.get(), 8,
                ModBlocks.BLOCK_OF_STICKS_ITEM.get(), ModBlocks.BLOCK_OF_STICKS_ITEM.get(),
                ModBlocks.BLOCK_OF_STICKS_ITEM.get(), ModBlocks.BLOCK_OF_STICKS_ITEM.get(),
                ModBlocks.BLOCK_OF_STICKS_ITEM.get(), ModBlocks.BLOCK_OF_STICKS_ITEM.get(),
                ModBlocks.BLOCK_OF_STICKS_ITEM.get(), ModBlocks.BLOCK_OF_STICKS_ITEM.get(), Items.ENDER_PEARL);
        assertRecipe(helper, "fertilizeddirt", ModBlocks.FERTILIZED_DIRT_ITEM.get(), 2,
                Items.ROTTEN_FLESH, Items.ROTTEN_FLESH, Items.ROTTEN_FLESH, Items.ROTTEN_FLESH,
                Items.BLACK_DYE, Items.BLACK_DYE, Items.BLACK_DYE, Items.BLACK_DYE, Items.DIRT);
        assertRecipe(helper, "platform", ModBlocks.PLATFORM_ITEM.get(), 6,
                Items.OAK_PLANKS, Items.OAK_PLANKS, Items.OAK_PLANKS, Items.ENDER_PEARL);
        assertRecipe(helper, "platform_spruce", ModBlocks.PLATFORM_SPRUCE_ITEM.get(), 6,
                Items.SPRUCE_PLANKS, Items.SPRUCE_PLANKS, Items.SPRUCE_PLANKS, Items.ENDER_PEARL);
        assertRecipe(helper, "platform_birch", ModBlocks.PLATFORM_BIRCH_ITEM.get(), 6,
                Items.BIRCH_PLANKS, Items.BIRCH_PLANKS, Items.BIRCH_PLANKS, Items.ENDER_PEARL);
        assertRecipe(helper, "platform_jungle", ModBlocks.PLATFORM_JUNGLE_ITEM.get(), 6,
                Items.JUNGLE_PLANKS, Items.JUNGLE_PLANKS, Items.JUNGLE_PLANKS, Items.ENDER_PEARL);
        assertRecipe(helper, "platform_acacia", ModBlocks.PLATFORM_ACACIA_ITEM.get(), 6,
                Items.ACACIA_PLANKS, Items.ACACIA_PLANKS, Items.ACACIA_PLANKS, Items.ENDER_PEARL);
        assertRecipe(helper, "platform_darkoak", ModBlocks.PLATFORM_DARK_OAK_ITEM.get(), 6,
                Items.DARK_OAK_PLANKS, Items.DARK_OAK_PLANKS, Items.DARK_OAK_PLANKS, Items.ENDER_PEARL);
        assertRecipe(helper, "rainbowlamp", ModBlocks.RAINBOW_LAMP_ITEM.get(), 1,
                Items.GREEN_DYE, Items.RED_DYE, Items.REDSTONE_LAMP, Items.BLUE_DYE);
        assertRecipe(helper, "superlubricentstone", ModBlocks.SUPER_LUBRICENT_STONE_ITEM.get(), 8,
                Items.STONE, Items.STONE, Items.STONE, Items.STONE, Items.STONE, Items.STONE, Items.STONE,
                Items.STONE, ModBlocks.SUPER_LUBRICENT_ICE_ITEM.get());
        assertRecipe(helper, "superlubricentplatform", ModBlocks.SUPER_LUBRICENT_PLATFORM_ITEM.get(), 6,
                ModBlocks.SUPER_LUBRICENT_ICE_ITEM.get(), ModBlocks.SUPER_LUBRICENT_ICE_ITEM.get(),
                ModBlocks.SUPER_LUBRICENT_ICE_ITEM.get(), Items.ENDER_PEARL);
        assertRecipe(helper, "superlubricentice", ModBlocks.SUPER_LUBRICENT_ICE_ITEM.get(), 16,
                Items.SLIME_BALL, Items.ICE, Items.WATER_BUCKET);
        helper.succeed();
    }

    private static void assertPlantSupport(GameTestHelper helper, BlockState dirt, BlockState tilled,
            BlockState plant, boolean normalExpected, boolean tilledExpected, String name) {
        BlockPos pos = helper.absolutePos(TEST_POS);
        TriState normal = dirt.canSustainPlant(helper.getLevel(), pos, Direction.UP, plant);
        TriState tilledState = tilled.canSustainPlant(helper.getLevel(), pos, Direction.UP, plant);
        helper.assertTrue(normal == (normalExpected ? TriState.TRUE : TriState.FALSE)
                        && tilledState == (tilledExpected ? TriState.TRUE : TriState.FALSE),
                "Unexpected fertilized dirt support for " + name);
    }

    private static void assertRecipe(GameTestHelper helper, String recipeName, ItemLike output, int outputCount,
            ItemLike... ingredients) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(RandomThings.MOD_ID, recipeName);
        var holder = helper.getLevel().getServer().getRecipeManager().byKey(id).orElseThrow(
                () -> new AssertionError("Missing recipe " + id));
        helper.assertTrue(holder.value() instanceof CraftingRecipe && holder.value() instanceof ShapedRecipe,
                "Recipe is not a shaped crafting recipe: " + id);
        if (!(holder.value() instanceof CraftingRecipe recipe)) {
            return;
        }

        ItemStack result = recipe.getResultItem(helper.getLevel().registryAccess());
        helper.assertTrue(result.is(output.asItem()) && result.getCount() == outputCount,
                "Unexpected output for " + id + ": " + result);
        helper.assertTrue(recipe.getType() == RecipeType.CRAFTING,
                "Unexpected recipe type for " + id);

        List<Ingredient> actualIngredients = recipe.getIngredients().stream()
                .filter(ingredient -> !ingredient.isEmpty())
                .toList();
        helper.assertTrue(actualIngredients.size() == ingredients.length,
                "Unexpected ingredient count for " + id);
        boolean[] matched = new boolean[actualIngredients.size()];
        for (ItemLike expected : ingredients) {
            ItemStack probe = new ItemStack(expected.asItem());
            int match = -1;
            for (int i = 0; i < actualIngredients.size(); i++) {
                if (!matched[i] && actualIngredients.get(i).test(probe)) {
                    match = i;
                    break;
                }
            }
            helper.assertTrue(match >= 0, "Missing ingredient " + expected.asItem() + " for " + id);
            if (match >= 0) {
                matched[match] = true;
            }
        }
    }
}
