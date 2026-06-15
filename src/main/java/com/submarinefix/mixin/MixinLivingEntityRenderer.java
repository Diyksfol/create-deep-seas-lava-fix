package com.submarinefix.mixin;

import com.submarinefix.SubmarineChecker;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Suppresses fire rendering on the player model while inside a sealed submarine.
 *
 * EntityRenderDispatcher.render() calls entity.displayFireAnimation() (NOT isOnFire())
 * before calling renderFlame(). We redirect that specific call to return false
 * for players inside a sealed sub, preventing the flame model from being drawn.
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class MixinLivingEntityRenderer {

    @Redirect(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/Entity;displayFireAnimation()Z"
            ),
            require = 0
    )
    private boolean submarinefix_suppressFireOnModel(Entity entity) {
        if (entity instanceof Player player
                && SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            return false;
        }
        return entity.displayFireAnimation();
    }
}
