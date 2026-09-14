package lumien.randomthings.gametest;

import java.util.List;

import lumien.randomthings.RandomThings;
import lumien.randomthings.block.FertilizedDirtBlock;
import lumien.randomthings.block.ModBlocks;
import lumien.randomthings.block.RainbowLampBlock;
import lumien.randomthings.block.StickBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
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
        helper.assertTrue(dirtForSapling.isDefault() && tilledForSapling.isFalse(),
                "Non-crop plant support does not follow the tilled state");
        helper.succeed();
    }

    @GameTest(template = "empty", timeoutTicks = 20)
    public static void allPortedRecipesLoad(GameTestHelper helper) {
        String[] recipeNames = {
                "blazeandsteel", "blockofsticks", "returningblockofsticks", "fertilizeddirt",
                "platform", "platform_spruce", "platform_birch", "platform_jungle", "platform_acacia",
                "platform_darkoak", "rainbowlamp", "superlubricentstone", "superlubricentplatform",
                "superlubricentice"
        };
        for (String recipeName : recipeNames) {
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath(RandomThings.MOD_ID, recipeName);
            helper.assertTrue(helper.getLevel().getServer().getRecipeManager().byKey(id).isPresent(),
                    "Missing recipe " + id);
        }
        helper.succeed();
    }
}
