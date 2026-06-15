package com.submarinefix.mixin;

import com.submarinefix.SubmarineChecker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Blocks the fire overlay rendering when the player is inside a sealed submarine.
 *
 * This is the most reliable fix for the visual fire effect (пламя внизу экрана).
 * The root cause is complex: fire ticks may be set server-side before clearFire()
 * runs, then synced to the client via network packets — producing a brief (or
 * persistent) flame overlay even when no actual fire damage is taken.
 *
 * Rather than chasing all sources of the fire flag, we suppress the overlay directly
 * at the render call. This is purely cosmetic and has no gameplay side-effects.
 *
 * renderFire(Minecraft, PoseStack) is the method that draws the fire overlay HUD.
 * We cancel it when the local player is confirmed inside a sealed compartment.
 */
@Mixin(ScreenEffectRenderer.class)
public abstract class MixinScreenEffectRenderer {

    @Inject(
            method = "renderFire",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private static void submarinefix_suppressFireOverlay(
            net.minecraft.client.Minecraft mc,
            com.mojang.blaze3d.vertex.PoseStack poseStack,
            org.spongepowered.asm.mixin.injection.callback.CallbackInfo ci) {

        Player player = mc.player;
        if (player == null) return;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            ci.cancel();
        }
    }
}
