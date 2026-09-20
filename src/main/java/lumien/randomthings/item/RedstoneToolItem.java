package lumien.randomthings.item;

import lumien.randomthings.block.ModBlocks;
import lumien.randomthings.block.entity.BasicRedstoneInterfaceBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public final class RedstoneToolItem extends Item {
    private static final String LINKING = "linking";
    private static final String ORIGIN = "origin";

    public RedstoneToolItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Level level = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (tag.getBoolean(LINKING)) {
            BlockPos origin = tag.contains(ORIGIN) ? BlockPos.of(tag.getLong(ORIGIN)) : clickedPos;
            if (!level.isClientSide && !origin.equals(clickedPos)
                    && level.getBlockEntity(origin) instanceof BasicRedstoneInterfaceBlockEntity blockEntity) {
                blockEntity.setTarget(clickedPos);
            }
            CustomData.update(DataComponents.CUSTOM_DATA, stack, data -> data.putBoolean(LINKING, false));
            return InteractionResult.sidedSuccess(level.isClientSide);
        }

        if (!level.getBlockState(clickedPos).is(ModBlocks.BASIC_REDSTONE_INTERFACE.get())) {
            return InteractionResult.FAIL;
        }

        CustomData.update(DataComponents.CUSTOM_DATA, stack, data -> {
            data.putBoolean(LINKING, true);
            data.putLong(ORIGIN, clickedPos.asLong());
        });
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getBoolean(LINKING);
    }
}
