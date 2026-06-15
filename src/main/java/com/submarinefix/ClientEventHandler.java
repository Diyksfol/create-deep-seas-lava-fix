package com.submarinefix;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Clears the fire flag on the client-side local player every tick while inside
 * a sealed submarine. This removes the burning texture on the player model
 * (visible in inventory / third-person view) without affecting gameplay.
 *
 * The server already suppresses fire damage and clears fire ticks server-side.
 * However the ON_FIRE shared flag is synced from server to client via DataWatcher
 * packets, and the client renders fire on the model based on that flag.
 * Calling clearFire() locally here resets it before the next render frame.
 */
@EventBusSubscriber(modid = SubmarineLavaFix.MOD_ID, value = Dist.CLIENT)
public class ClientEventHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;
        if (!player.isOnFire()) return;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            player.clearFire();
        }
    }
}
