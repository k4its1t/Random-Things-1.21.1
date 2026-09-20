package lumien.randomthings.mixin;

import lumien.randomthings.block.entity.BasicRedstoneInterfaceBlockEntity;
import lumien.randomthings.redstone.RedstoneSignalSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.SignalGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SignalGetter.class)
public interface SignalGetterMixin {
    @Inject(method = "getSignal", at = @At("HEAD"), cancellable = true)
    private void randomthings$getRemoteSignal(BlockPos pos, Direction direction,
            CallbackInfoReturnable<Integer> callback) {
        int power = BasicRedstoneInterfaceBlockEntity.getRemoteSignal(
                (SignalGetter) (Object) this, pos, direction, false);
        power = Math.max(power, RedstoneSignalSavedData.getRemoteSignal(
                (SignalGetter) (Object) this, pos, direction));
        if (power > 0) {
            callback.setReturnValue(power);
        }
    }

    @Inject(method = "getDirectSignal", at = @At("HEAD"), cancellable = true)
    private void randomthings$getRemoteDirectSignal(BlockPos pos, Direction direction,
            CallbackInfoReturnable<Integer> callback) {
        int power = BasicRedstoneInterfaceBlockEntity.getRemoteSignal(
                (SignalGetter) (Object) this, pos, direction, true);
        power = Math.max(power, RedstoneSignalSavedData.getRemoteSignal(
                (SignalGetter) (Object) this, pos, direction));
        if (power > 0) {
            callback.setReturnValue(power);
        }
    }
}
