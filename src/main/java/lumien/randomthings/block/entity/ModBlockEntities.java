package lumien.randomthings.block.entity;

import lumien.randomthings.RandomThings;
import lumien.randomthings.block.ModBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, RandomThings.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BasicRedstoneInterfaceBlockEntity>>
            BASIC_REDSTONE_INTERFACE = BLOCK_ENTITY_TYPES.register("basicredstoneinterface", () ->
                    BlockEntityType.Builder.of(BasicRedstoneInterfaceBlockEntity::new,
                            ModBlocks.BASIC_REDSTONE_INTERFACE.get()).build(null));

    private ModBlockEntities() {
    }
}
