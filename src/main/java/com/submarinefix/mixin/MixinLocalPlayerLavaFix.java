package com.submarinefix.mixin;

import com.submarinefix.SubmarineChecker;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Client-side suppression of lavaHurt() for LocalPlayer.
 * Prevents the fire overlay from appearing when the player is inside a sealed sub.
 * CDS maintains CompartmentTracker on the client side as well, so the check works.
 *
 * This mixin is in the "client" section of mixins.submarinefix.json and is
 * never loaded on a dedicated server, avoiding ClassNotFoundException for LocalPlayer.
 */
@Mixin(LocalPlayer.class)
public abstract class MixinLocalPlayerLavaFix {

    @Inject(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;lavaHurt()V"),
            cancellable = true,
            require = 0
    )
    private void submarinefix_suppressLavaHurtClient(CallbackInfo ci) {
        LocalPlayer self = (LocalPlayer) (Object) this;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(self)) {
            self.clearFire();
            ci.cancel();
        }
    }
}
