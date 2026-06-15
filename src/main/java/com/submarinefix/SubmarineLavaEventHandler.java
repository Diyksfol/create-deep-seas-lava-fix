package com.submarinefix;

import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = SubmarineLavaFix.MOD_ID)
public class SubmarineLavaEventHandler {

    /**
     * Layer 1 — cancels any incoming fire/lava damage for players inside a sealed sub.
     * This is the primary reliable layer that covers all damage sources.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onIncomingDamage(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        var src = event.getSource();
        boolean isFireOrLava =
                src.is(DamageTypes.LAVA)
                || src.is(DamageTypes.IN_FIRE)
                || src.is(DamageTypes.ON_FIRE)
                || src.is(DamageTypes.HOT_FLOOR);

        if (!isFireOrLava) return;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            event.setCanceled(true);
            player.clearFire();
            SubmarineLavaFix.LOGGER.debug(
                    "[SubmarineLavaFix] Blocked {} damage for {} inside sub.",
                    src.typeHolder().unwrapKey().map(k -> k.location().toString()).orElse("?"),
                    player.getName().getString());
        }
    }

    /**
     * Layer 4 — runs every server tick for each player.
     * Clears fire ticks on players inside a sealed sub regardless of cause.
     * This fixes пункт 2: infinite fire when hull was breached then re-sealed —
     * fire ticks accumulated before sealing are cleared on the next tick.
     * Also clears fire from seated players (пункт 4) who aren't in lava themselves.
     */
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) return;
        if (!player.isOnFire()) return;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            player.clearFire();
            SubmarineLavaFix.LOGGER.debug(
                    "[SubmarineLavaFix] Cleared fire ticks for {} inside sealed sub (PlayerTick).",
                    player.getName().getString());
        }
    }
}
