package lumien.randomthings.block.entity;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

import javax.annotation.Nullable;

import lumien.randomthings.block.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class BasicRedstoneInterfaceBlockEntity extends BlockEntity {
    private static final String TARGET = "target";
    private static final Set<BasicRedstoneInterfaceBlockEntity> LOADED =
            Collections.synchronizedSet(Collections.newSetFromMap(new WeakHashMap<>()));

    private final int[] weakPower = new int[Direction.values().length];
    private final int[] strongPower = new int[Direction.values().length];
    @Nullable
    private BlockPos target;

    public BasicRedstoneInterfaceBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BASIC_REDSTONE_INTERFACE.get(), pos, state);
    }

    @Override
    public void onLoad() {
        super.onLoad();
        LOADED.add(this);
        if (level != null && !level.isClientSide) {
            refreshPower();
        }
    }

    @Override
    public void onChunkUnloaded() {
        LOADED.remove(this);
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        LOADED.remove(this);
        super.setRemoved();
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (target != null) {
            tag.putLong(TARGET, target.asLong());
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        target = tag.contains(TARGET, Tag.TAG_LONG) ? BlockPos.of(tag.getLong(TARGET)) : null;
    }

    public void setTarget(BlockPos newTarget) {
        BlockPos immutableTarget = newTarget.immutable();
        if (immutableTarget.equals(target)) {
            return;
        }

        LOADED.add(this);
        refreshPower();
        BlockPos oldTarget = target;
        target = immutableTarget;
        setChanged();
        notifyTarget(oldTarget);
        notifyTarget(target);
    }

    public void clearTarget() {
        if (target == null) {
            return;
        }
        BlockPos oldTarget = target;
        target = null;
        setChanged();
        notifyTarget(oldTarget);
    }

    @Nullable
    public BlockPos getTarget() {
        return target;
    }

    public void refreshPower() {
        if (level == null || level.isClientSide) {
            return;
        }

        boolean changed = false;
        for (Direction direction : Direction.values()) {
            BlockPos sourcePos = worldPosition.relative(direction);
            int index = direction.ordinal();
            int newWeakPower = level.getSignal(sourcePos, direction);
            int newStrongPower = level.getDirectSignal(sourcePos, direction);
            if (weakPower[index] != newWeakPower || strongPower[index] != newStrongPower) {
                weakPower[index] = newWeakPower;
                strongPower[index] = newStrongPower;
                changed = true;
            }
        }

        if (changed) {
            setChanged();
            notifyTarget(target);
        }
    }

    public static int getRemoteSignal(SignalGetter signalGetter, BlockPos queriedPos, Direction direction,
            boolean direct) {
        if (!(signalGetter instanceof Level queriedLevel)) {
            return 0;
        }

        BlockPos poweredPos = queriedPos.relative(direction.getOpposite());
        int power = 0;
        synchronized (LOADED) {
            for (BasicRedstoneInterfaceBlockEntity blockEntity : LOADED) {
                if (!blockEntity.isRemoved() && blockEntity.level == queriedLevel
                        && poweredPos.equals(blockEntity.target)) {
                    int[] powers = direct ? blockEntity.strongPower : blockEntity.weakPower;
                    power = Math.max(power, powers[direction.ordinal()]);
                }
            }
        }
        return power;
    }

    private void notifyTarget(@Nullable BlockPos targetPos) {
        if (level == null || level.isClientSide || targetPos == null || !level.isLoaded(targetPos)) {
            return;
        }
        BlockState targetState = level.getBlockState(targetPos);
        level.neighborChanged(targetState, targetPos, ModBlocks.BASIC_REDSTONE_INTERFACE.get(), worldPosition, false);
        level.updateNeighborsAt(targetPos, ModBlocks.BASIC_REDSTONE_INTERFACE.get());
    }
}
