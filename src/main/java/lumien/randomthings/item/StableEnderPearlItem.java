package lumien.randomthings.item;

import java.util.List;
import java.util.UUID;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public final class StableEnderPearlItem extends Item {
    private static final String BOUND_PLAYER_UUID = "player-uuid";
    private static final String BOUND_PLAYER_NAME = "player-name";
    private static final String ENTITY_COUNTER = "randomthings_stable_ender_pearl_counter";
    private static final int TELEPORT_DELAY = 140;

    public StableEnderPearlItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide) {
            CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> {
                tag.putUUID(BOUND_PLAYER_UUID, player.getUUID());
                tag.putString(BOUND_PLAYER_NAME, player.getGameProfile().getName());
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip,
            TooltipFlag flag) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }

        String playerName = customData.copyTag().getString(BOUND_PLAYER_NAME);
        if (!playerName.isEmpty()) {
            tooltip.add(Component.translatable("tooltip.randomthings.stableenderpearl.boundto", playerName)
                    .withStyle(style -> style.withColor(0xAA00AA)));
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        Level level = entity.level();
        if (level.isClientSide) {
            if (entity.getAge() > 50) {
                for (int i = 0; i < 2; i++) {
                    level.addParticle(ParticleTypes.PORTAL,
                            entity.getX() + (level.random.nextDouble() - 0.5D),
                            entity.getY() + level.random.nextDouble() * 2.0D - 0.25D,
                            entity.getZ() + (level.random.nextDouble() - 0.5D),
                            (level.random.nextDouble() - 0.5D) * 2.0D,
                            -level.random.nextDouble(),
                            (level.random.nextDouble() - 0.5D) * 2.0D);
                }
            }
            return false;
        }

        CompoundTag entityData = entity.getPersistentData();
        int counter = entityData.getInt(ENTITY_COUNTER);
        if (counter < TELEPORT_DELAY) {
            entityData.putInt(ENTITY_COUNTER, counter + 1);
            return false;
        }

        teleportBoundPlayerOrNearbyEntity(stack, entity, (ServerLevel) level);
        level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
        entity.discard();
        return true;
    }

    private static void teleportBoundPlayerOrNearbyEntity(ItemStack stack, ItemEntity itemEntity,
            ServerLevel destination) {
        ServerPlayer boundPlayer = findBoundPlayer(stack, destination);
        if (boundPlayer != null) {
            boundPlayer.level().playSound(null, boundPlayer.getX(), boundPlayer.getY(), boundPlayer.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            boundPlayer.teleportTo(destination, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(),
                    boundPlayer.getYRot(), boundPlayer.getXRot());
            return;
        }

        List<LivingEntity> nearby = destination.getEntitiesOfClass(LivingEntity.class,
                itemEntity.getBoundingBox().inflate(10.0D), LivingEntity::isAlive);
        if (!nearby.isEmpty()) {
            LivingEntity target = nearby.get(destination.random.nextInt(nearby.size()));
            destination.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
            target.teleportTo(itemEntity.getX(), itemEntity.getY(), itemEntity.getZ());
        }
    }

    private static ServerPlayer findBoundPlayer(ItemStack stack, ServerLevel level) {
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return null;
        }

        CompoundTag tag = customData.copyTag();
        if (!tag.hasUUID(BOUND_PLAYER_UUID)) {
            return null;
        }
        UUID playerUuid = tag.getUUID(BOUND_PLAYER_UUID);
        return level.getServer().getPlayerList().getPlayer(playerUuid);
    }
}
