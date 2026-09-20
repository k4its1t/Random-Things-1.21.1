package lumien.randomthings.redstone;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.SignalGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.SavedData;

public final class RedstoneSignalSavedData extends SavedData {
    private static final String ID = "randomthings_redstone_signals";
    private static final String SIGNALS = "signals";
    private static final SavedData.Factory<RedstoneSignalSavedData> FACTORY =
            new SavedData.Factory<>(RedstoneSignalSavedData::new, RedstoneSignalSavedData::load);

    private final List<Signal> signals = new ArrayList<>();

    public static void addSignal(ServerLevel level, BlockPos pos, int duration, int strength) {
        if (!level.isLoaded(pos)) {
            return;
        }
        RedstoneSignalSavedData data = level.getDataStorage().computeIfAbsent(FACTORY, ID);
        data.signals.add(new Signal(pos.immutable(), duration, strength));
        data.setDirty();
        notifyPosition(level, pos);
    }

    public static int getRemoteSignal(SignalGetter signalGetter, BlockPos queriedPos, Direction direction) {
        if (!(signalGetter instanceof ServerLevel level)) {
            return 0;
        }
        RedstoneSignalSavedData data = getExisting(level);
        if (data == null) {
            return 0;
        }
        return data.getSignal(queriedPos.relative(direction.getOpposite()));
    }

    public static void tick(ServerLevel level) {
        RedstoneSignalSavedData data = getExisting(level);
        if (data == null || data.signals.isEmpty()) {
            return;
        }

        Iterator<Signal> iterator = data.signals.iterator();
        while (iterator.hasNext()) {
            Signal signal = iterator.next();
            if (!level.isLoaded(signal.pos)) {
                continue;
            }
            signal.remainingTicks--;
            data.setDirty();
            if (signal.remainingTicks <= 0) {
                iterator.remove();
                notifyPosition(level, signal.pos);
            }
        }
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        ListTag signalList = new ListTag();
        for (Signal signal : signals) {
            CompoundTag signalTag = new CompoundTag();
            signalTag.putLong("position", signal.pos.asLong());
            signalTag.putInt("remainingTicks", signal.remainingTicks);
            signalTag.putInt("strength", signal.strength);
            signalList.add(signalTag);
        }
        tag.put(SIGNALS, signalList);
        return tag;
    }

    private static RedstoneSignalSavedData load(CompoundTag tag, HolderLookup.Provider registries) {
        RedstoneSignalSavedData data = new RedstoneSignalSavedData();
        ListTag signalList = tag.getList(SIGNALS, Tag.TAG_COMPOUND);
        for (Tag entry : signalList) {
            CompoundTag signalTag = (CompoundTag) entry;
            int remainingTicks = signalTag.getInt("remainingTicks");
            if (remainingTicks > 0) {
                data.signals.add(new Signal(BlockPos.of(signalTag.getLong("position")),
                        remainingTicks, signalTag.getInt("strength")));
            }
        }
        return data;
    }

    @Nullable
    private static RedstoneSignalSavedData getExisting(ServerLevel level) {
        return level.getDataStorage().get(FACTORY, ID);
    }

    private int getSignal(BlockPos pos) {
        int strength = 0;
        for (Signal signal : signals) {
            if (signal.pos.equals(pos)) {
                strength = Math.max(strength, signal.strength);
            }
        }
        return strength;
    }

    private static void notifyPosition(ServerLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        level.neighborChanged(state, pos, Blocks.REDSTONE_BLOCK, pos, false);
        level.updateNeighborsAt(pos, Blocks.REDSTONE_BLOCK);
    }

    private static final class Signal {
        private final BlockPos pos;
        private int remainingTicks;
        private final int strength;

        private Signal(BlockPos pos, int remainingTicks, int strength) {
            this.pos = pos;
            this.remainingTicks = remainingTicks;
            this.strength = strength;
        }
    }
}
