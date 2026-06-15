package com.submarinefix;

import com.maxenonyme.createsubmarine.submarine.compartment.CompartmentDetector;
import com.maxenonyme.createsubmarine.submarine.compartment.CompartmentTracker;
import dev.ryanhcode.sable.companion.SubLevelAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.fml.ModList;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Checks whether a player is inside or climbing out of a sealed submarine compartment.
 *
 * Two-mode check:
 *
 * A) Normal (not climbable): checks feet and eye positions exactly against
 *    internal()+hull() of sealed compartments within WORLD_AABB.
 *    No vertical scan — avoids false positives for players above the sub in lava.
 *
 * B) Climbable (ladder/vine): additionally scans MAX_CLIMB_DISTANCE blocks
 *    downward from feet and upward from eyes. This covers ladder shafts that
 *    extend above the hull top (outside WORLD_AABB and outside compartment blocks).
 *    Also uses an inflated AABB to reach positions above the hull.
 *    Vertical scan is safe here because real lava above sub is unlikely when
 *    the player is actively climbing a ladder connected to the interior.
 *
 * Works on both server and client sides — CDS maintains CompartmentTracker on both.
 */
public class SubmarineChecker {

    private static final String CDS_MOD_ID = "create_submarine";
    static final boolean CDS_PRESENT = ModList.get().isLoaded(CDS_MOD_ID);

    private static final int MAX_CLIMB_DISTANCE = 8;

    static {
        if (CDS_PRESENT) {
            SubmarineLavaFix.LOGGER.info("[SubmarineLavaFix] Create Deep Seas detected.");
        } else {
            SubmarineLavaFix.LOGGER.info("[SubmarineLavaFix] Create Deep Seas not found — lava fix inactive.");
        }
    }

    public static boolean isPlayerInsideSealedSubmarine(Player player) {
        if (!CDS_PRESENT) return false;

        Vec3 feetPos   = player.position();
        Vec3 eyePos    = new Vec3(player.getX(), player.getEyeY(), player.getZ());
        boolean climbing = player.onClimbable();

        for (Map.Entry<UUID, SubLevelAccess> entry
                : CompartmentTracker.getSubsSnapshot().entrySet()) {

            UUID uuid = entry.getKey();
            SubLevelAccess sub = entry.getValue();

            AABB worldAABB = CompartmentTracker.getWorldAABB(uuid);
            if (worldAABB == null) continue;

            // Use expanded AABB only when climbing — prevents false positive above sub
            AABB checkAABB = climbing
                    ? worldAABB.inflate(0, MAX_CLIMB_DISTANCE, 0)
                    : worldAABB;

            if (!checkAABB.contains(feetPos) && !checkAABB.contains(eyePos)) continue;
            if (!CompartmentTracker.hasAnySealed(uuid)) continue;

            List<CompartmentDetector.Component> compartments =
                    CompartmentTracker.getCompartments(uuid);
            if (compartments == null || compartments.isEmpty()) continue;

            Vec3 localFeet = null;
            Vec3 localEyes = null;
            try {
                var pose = sub.logicalPose();
                if (checkAABB.contains(feetPos)) localFeet = pose.transformPositionInverse(feetPos);
                if (checkAABB.contains(eyePos))  localEyes = pose.transformPositionInverse(eyePos);
            } catch (Exception e) {
                SubmarineLavaFix.LOGGER.debug(
                    "[SubmarineLavaFix] transformPositionInverse failed: {}", e.getMessage());
                continue;
            }

            if (climbing) {
                // Full check with vertical scan for ladder shafts
                if (isInOrConnectedToSealed(compartments, localFeet, localEyes)) return true;
            } else {
                // Exact check only — no vertical scan to avoid false positives above sub
                if (isExactlyInSealed(compartments, localFeet, localEyes)) return true;
            }
        }

        return false;
    }

    /** Exact position check — internal() or hull() only. */
    private static boolean isExactlyInSealed(
            List<CompartmentDetector.Component> compartments,
            Vec3 localFeet, Vec3 localEyes) {

        BlockPos feetBlock = localFeet != null
                ? BlockPos.containing(localFeet.x, localFeet.y, localFeet.z) : null;
        BlockPos eyeBlock  = localEyes != null
                ? BlockPos.containing(localEyes.x, localEyes.y, localEyes.z) : null;

        for (CompartmentDetector.Component comp : compartments) {
            if (!comp.sealed()) continue;
            var internal = comp.internal();
            var hull     = comp.hull();
            boolean feetIn = feetBlock != null && (internal.contains(feetBlock) || hull.contains(feetBlock));
            boolean eyesIn = eyeBlock  != null && (internal.contains(eyeBlock)  || hull.contains(eyeBlock));
            if (feetIn || eyesIn) return true;
        }
        return false;
    }

    /** Exact + vertical scan — used only when climbing. */
    private static boolean isInOrConnectedToSealed(
            List<CompartmentDetector.Component> compartments,
            Vec3 localFeet, Vec3 localEyes) {

        // Exact first
        if (isExactlyInSealed(compartments, localFeet, localEyes)) return true;

        // Scan downward from feet — ladder shaft above hull
        if (localFeet != null) {
            int bx = (int) Math.floor(localFeet.x);
            int by = (int) Math.floor(localFeet.y);
            int bz = (int) Math.floor(localFeet.z);
            for (int dy = 1; dy <= MAX_CLIMB_DISTANCE; dy++) {
                if (isBlockInSealed(compartments, new BlockPos(bx, by - dy, bz))) return true;
            }
        }

        // Scan upward from eyes — entering from below
        if (localEyes != null) {
            int bx = (int) Math.floor(localEyes.x);
            int by = (int) Math.floor(localEyes.y);
            int bz = (int) Math.floor(localEyes.z);
            for (int dy = 1; dy <= MAX_CLIMB_DISTANCE; dy++) {
                if (isBlockInSealed(compartments, new BlockPos(bx, by + dy, bz))) return true;
            }
        }

        return false;
    }

    private static boolean isBlockInSealed(
            List<CompartmentDetector.Component> compartments, BlockPos b) {
        for (CompartmentDetector.Component comp : compartments) {
            if (!comp.sealed()) continue;
            if (comp.internal().contains(b) || comp.hull().contains(b)) return true;
        }
        return false;
    }
}
