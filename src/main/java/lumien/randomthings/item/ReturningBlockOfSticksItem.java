package lumien.randomthings.item;

import java.util.Map;

import org.jetbrains.annotations.Nullable;

import lumien.randomthings.block.StickBlock;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public final class ReturningBlockOfSticksItem extends BlockItem {
    public ReturningBlockOfSticksItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    protected @Nullable BlockState getPlacementState(BlockPlaceContext context) {
        BlockState state = super.getPlacementState(context);
        return state == null ? null : state.setValue(StickBlock.RETURNING, true);
    }

    @Override
    public void registerBlocks(Map<Block, Item> blockToItemMap, Item item) {
        // Keep the normal block item as Block#asItem while still placing the same block with another state.
    }
}
