package lumien.randomthings.item;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

import org.joml.Vector3f;

public final class EscapeRopeItem extends Item {
    private static final int MAX_SEARCHED_POSITIONS = 10_000;
    private static final int SEARCHES_PER_TICK = 4;
    private static final String SEARCHING = "searching";
    private static final DustParticleOptions SEARCH_PARTICLE =
            new DustParticleOptions(new Vector3f(1.0F, 1.0F, 0.0F), 1.0F);
    private static final Direction[] SEARCH_DIRECTIONS = {
            Direction.DOWN, Direction.NORTH, Direction.SOUTH,
            Direction.WEST, Direction.EAST, Direction.UP
    };

    private final Map<Player, SearchTask> searches = new WeakHashMap<>();

    public EscapeRopeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        BlockPos start = player.blockPosition();
        if (!level.dimensionType().hasSkyLight() || hasOpenSky(level, start) || !isPassable(level, start)) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            searches.put(player, new SearchTask(serverLevel, start));
            setSearching(stack, true);
        }
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int remainingUseDuration) {
        if (level.isClientSide) {
            spawnSearchParticles(level, livingEntity, remainingUseDuration);
            return;
        }
        if (!(livingEntity instanceof Player player)) {
            return;
        }

        SearchTask task = searches.get(player);
        if (task == null || task.level != level) {
            cancelSearch(player, stack);
            return;
        }

        for (int i = 0; i < SEARCHES_PER_TICK; i++) {
            BlockPos next = task.nextUnvisited();
            if (next == null || task.visited.size() > MAX_SEARCHED_POSITIONS) {
                failSearch(player, stack);
                return;
            }

            if (!level.hasChunkAt(next) || !isPassable(level, next)) {
                continue;
            }
            if (hasOpenSky(level, next)) {
                finishSearch(player, stack, task.level, next);
                return;
            }

            for (Direction direction : SEARCH_DIRECTIONS) {
                BlockPos neighbor = next.relative(direction);
                if (!task.visited.contains(neighbor)) {
                    task.pending.addLast(neighbor.immutable());
                }
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeCharged) {
        if (livingEntity instanceof Player player) {
            searches.remove(player);
            setSearching(stack, false);
        }
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 20 * 60;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.BOW;
    }

    @Override
    public boolean isFoil(ItemStack stack) {
        CustomData data = stack.get(DataComponents.CUSTOM_DATA);
        return data != null && data.copyTag().getBoolean(SEARCHING);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.randomthings.escaperope"));
    }

    private void finishSearch(Player player, ItemStack stack, ServerLevel level, BlockPos openPos) {
        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 0.5F, 1.0F);

        BlockPos landing = findLanding(level, openPos);
        player.teleportTo(landing.getX() + 0.5D, landing.getY() + 1.0D, landing.getZ() + 0.5D);
        searches.remove(player);
        setSearching(stack, false);
        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
        player.stopUsingItem();

        level.playSound(null, player.blockPosition(), SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.PLAYERS, 0.5F, 1.0F);
    }

    private static BlockPos findLanding(ServerLevel level, BlockPos openPos) {
        for (int y = openPos.getY(); y >= level.getMinBuildHeight(); y--) {
            BlockPos candidate = new BlockPos(openPos.getX(), y, openPos.getZ());
            BlockState state = level.getBlockState(candidate);
            if (state.isFaceSturdy(level, candidate, Direction.UP) || state.blocksMotion()) {
                return candidate;
            }
        }
        return openPos.below();
    }

    private void failSearch(Player player, ItemStack stack) {
        setSearching(stack, false);
        ItemStack dropped = stack.copy();
        stack.setCount(0);
        player.drop(dropped, false);
        searches.remove(player);
        player.stopUsingItem();
    }

    private void cancelSearch(Player player, ItemStack stack) {
        searches.remove(player);
        setSearching(stack, false);
        player.stopUsingItem();
    }

    private static boolean isPassable(Level level, BlockPos pos) {
        return level.getBlockState(pos).getCollisionShape(level, pos).isEmpty();
    }

    private static boolean hasOpenSky(Level level, BlockPos pos) {
        return level.getHeight(Heightmap.Types.MOTION_BLOCKING, pos.getX(), pos.getZ()) <= pos.getY();
    }

    private static void spawnSearchParticles(Level level, LivingEntity entity, int remainingUseDuration) {
        int elapsed = 20 * 60 - remainingUseDuration;
        for (int i = 0; i < 7; i++) {
            for (int offset = 0; offset <= 10; offset += 10) {
                double angle = (elapsed + i * 20.0D) / (10.0D + offset);
                double x = entity.getX() + Math.sin(angle);
                double y = entity.getY() + 1.0D + Math.sin((elapsed + i * 20.0D) / (15.0D + offset));
                double z = entity.getZ() + Math.cos(angle);
                level.addParticle(SEARCH_PARTICLE, x, y, z, 0.0D, 0.0D, 0.0D);
            }
        }
    }

    private static void setSearching(ItemStack stack, boolean searching) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.putBoolean(SEARCHING, searching));
    }

    private static final class SearchTask {
        private final ServerLevel level;
        private final ArrayDeque<BlockPos> pending = new ArrayDeque<>();
        private final Set<BlockPos> visited = new HashSet<>();

        private SearchTask(ServerLevel level, BlockPos start) {
            this.level = level;
            pending.add(start.immutable());
        }

        private BlockPos nextUnvisited() {
            while (!pending.isEmpty()) {
                BlockPos next = pending.removeLast();
                if (visited.add(next)) {
                    return next;
                }
            }
            return null;
        }
    }
}
