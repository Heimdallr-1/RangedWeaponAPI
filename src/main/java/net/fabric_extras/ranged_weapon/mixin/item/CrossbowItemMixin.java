package net.fabric_extras.ranged_weapon.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabric_extras.ranged_weapon.api.CrossbowMechanics;
import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.internal.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    @Shadow @Final private static float DEFAULT_SPEED;
    private static final float STANDARD_VELOCITY = DEFAULT_SPEED;
    private static final float STANDARD_DAMAGE = 9;

    private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    public float getPullProgress_RWA(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 25;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    /**
     * Apply custom pull time
     */
    @Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
    private static void applyCustomPullTime_RWA(ItemStack stack, CallbackInfoReturnable<Integer> cir) {
        var item = stack.getItem();
        if (item instanceof CustomRangedWeapon weapon) {
            var pullTime = weapon.getRangedWeaponConfig().pull_time();
            if (pullTime > 0) {
                pullTime = CrossbowMechanics.PullTime.modifier.getPullTime(pullTime, stack);
                cir.setReturnValue(pullTime);
                cir.cancel();
            }
        }
    }

    /**
     * Apply custom velocity
     */
    @ModifyVariable(method = "shoot", at = @At("HEAD"), ordinal = 1, argsOnly = true)
    private static float applyCustomVelocity_RWA(float speed, World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed1, float divergence, float simulated) {
        var item = crossbow.getItem();
        if (item instanceof CustomRangedWeapon weapon) {
            var customVelocity = weapon.getRangedWeaponConfig().velocity();
            if (customVelocity > 0) {
                return speed * (customVelocity / DEFAULT_SPEED);
            }
        }
        return speed;
    }

    /**
     * Apply custom damage
     */
    @WrapOperation(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private static boolean applyCustomDamage(
            // Mixin Parameters
            World instance, Entity entity, Operation<Boolean> original,
            // Context Parameters
            World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectileStack, float soundPitch, boolean creative, float speed, float divergence, float simulated
    ) {
        var item = crossbow.getItem();
        if (entity instanceof PersistentProjectileEntity projectileEntity && item instanceof CustomRangedWeapon weapon) {
            var rangedDamage = shooter.getAttributeValue(EntityAttributes_RangedWeapon.DAMAGE.attribute);
            System.out.println("Ranged Damage: " + rangedDamage);
            var multiplier = DamageUtil.arrowDamageMultiplier(STANDARD_DAMAGE, rangedDamage, STANDARD_VELOCITY, weapon.getRangedWeaponConfig().velocity());
            System.out.println("Multiplier: " + multiplier);
            var finalDamage = projectileEntity.getDamage() * multiplier;
            System.out.println("Final Damage: " + finalDamage);
            projectileEntity.setDamage(finalDamage);
        }
        return original.call(world, entity);
    }
}
