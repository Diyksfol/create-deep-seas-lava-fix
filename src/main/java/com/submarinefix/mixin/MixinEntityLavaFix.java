package com.submarinefix.mixin;

import com.submarinefix.SubmarineChecker;
import com.submarinefix.SubmarineLavaFix;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Server-side suppression of lavaHurt() in baseTick(). */
@Mixin(Entity.class)
public abstract class MixinEntityLavaFix {

    @Inject(
            method = "baseTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;lavaHurt()V"),
            cancellable = true,
            require = 0
    )
    private void submarinefix_suppressLavaHurt(CallbackInfo ci) {
        Entity self = (Entity) (Object) this;
        if (!(self instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            player.clearFire();
            ci.cancel();
            SubmarineLavaFix.LOGGER.debug(
                    "[SubmarineLavaFix] Suppressed lavaHurt() for {} (server).",
                    player.getName().getString());
        }
    }
}
