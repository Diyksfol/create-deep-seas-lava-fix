package com.submarinefix.mixin;

import com.submarinefix.SubmarineChecker;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerRenderer.class)
public abstract class MixinPlayerRenderer {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/LivingEntity;isOnFire()Z"
            ),
            require = 0
    )
    private boolean submarinefix_suppressFireOnModel(LivingEntity entity) {
        if (entity instanceof Player player
                && SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            return false;
        }
        return entity.isOnFire();
    }
}
