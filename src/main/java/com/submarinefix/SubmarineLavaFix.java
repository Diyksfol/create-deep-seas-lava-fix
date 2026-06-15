package com.submarinefix;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Submarine Lava Fix — NeoForge 1.21.1 port.
 *
 * Fixes the bug in Create: Deep Seas where players inside a hermetically
 * sealed submarine take fire/lava damage even though no lava is actually
 * present in the sublevel (interior) space.
 *
 * Root cause: Minecraft's Entity#baseTick() checks the fluid state at the
 * player's REAL-WORLD position. When the submarine is submerged in lava, the
 * real-world block at the player's translated coordinate is still lava, so the
 * vanilla lava hurt logic fires regardless of the culled interior.
 *
 * Fix (three layers, in order of reliability):
 *   1. NeoForge {@code LivingIncomingDamageEvent} — guaranteed cancellation of
 *      fire/lava damage for players detected inside a sealed sub. This is the
 *      primary, version-stable layer and is sufficient on its own.
 *   2. Mixin on {@code LivingEntity#hurt} — cancels fire/lava damage one step
 *      earlier (belt-and-suspenders).
 *   3. Mixin on {@code Entity#baseTick} — swallows the {@code lavaHurt()} call
 *      so ignition never happens in the first place.
 *
 * The mixin layers are declared non-fatal ({@code require = 0}); if a target
 * shifts in a future Minecraft revision the mod still loads and the event layer
 * keeps working.
 */
@Mod(SubmarineLavaFix.MOD_ID)
public class SubmarineLavaFix {

    public static final String MOD_ID = "submarinefix";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    // NeoForge injects the mod-specific event bus into the constructor.
    public SubmarineLavaFix(IEventBus modEventBus) {
        modEventBus.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("[SubmarineLavaFix] Loaded. Lava damage inside sealed submarines is suppressed.");
    }
}
