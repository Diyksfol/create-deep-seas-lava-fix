package com.submarinefix.mixin;

import com.submarinefix.SubmarineChecker;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Layer 2 — intercepts {@code LivingEntity#hurt(DamageSource, float)} at HEAD
 * and cancels fire/lava sources for players inside a sealed submarine.
 *
 * Note: {@code hurt(DamageSource, float)} still exists in 1.21.1. (It is only
 * split into {@code hurtServer}/{@code hurtClient} from 1.21.2 onward, so this
 * mixin would need retargeting if the mod is later updated past 1.21.1.)
 *
 * {@code require = 0} keeps the injection non-fatal.
 */
@Mixin(LivingEntity.class)
public abstract class MixinLivingEntityHurtFix {

    @Inject(
            method = "hurt",
            at = @At("HEAD"),
            cancellable = true,
            require = 0
    )
    private void submarinefix_cancelFireAndLavaDamage(
            DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {

        LivingEntity self = (LivingEntity) (Object) this;

        if (!(self instanceof Player player)) return;
        if (player.level().isClientSide()) return;

        boolean isFireLavaDamage =
                source.is(DamageTypes.LAVA)
                || source.is(DamageTypes.IN_FIRE)
                || source.is(DamageTypes.ON_FIRE)
                || source.is(DamageTypes.HOT_FLOOR);

        if (!isFireLavaDamage) return;

        if (SubmarineChecker.isPlayerInsideSealedSubmarine(player)) {
            player.clearFire();
            cir.setReturnValue(false);
            cir.cancel();
        }
    }
}
