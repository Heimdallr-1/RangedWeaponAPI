package net.fabric_extras.ranged_weapon.mixin.item;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.internal.DamageUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.BowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BowItem.class)
public class BowItemMixin {
    private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    public float getPullProgress_RWA(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 20;
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
    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
    private float applyCustomPullTime(int ticks, Operation<Float> original) {
        if (config().pull_time() > 0) {
            return getPullProgress_RWA(ticks);
        } else {
            return original.call(ticks);
        }
    }

    /**
     * Apply custom velocity
     */
    @WrapWithCondition(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean applyCustomVelocity(World world, Entity entity) {
        if (entity instanceof PersistentProjectileEntity projectile) {
            if (config().velocity() > 0F) {
                // 3.0F is the default hardcoded velocity of bows
                projectile.setVelocity(projectile.getVelocity().multiply(config().velocity() / 3.0F));
            }
        }
        return true;
    }

    /**
     * Apply custom damage
     */
    private static final float STANDARD_DAMAGE = 6;
    private static final float STANDARD_VELOCITY = 3.0F;
    @WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
    private boolean applyCustomDamage(
            // Mixin parameters
            World instance, Entity entity, Operation<Boolean> original,
            // Context parameters
            ItemStack stack, World world, LivingEntity user, int remainingUseTicks) {
        if (entity instanceof PersistentProjectileEntity projectile) {
            var rangedDamage = user.getAttributeValue(EntityAttributes_RangedWeapon.DAMAGE.attribute);
            var multiplier = DamageUtil.arrowDamageMultiplier(STANDARD_DAMAGE, rangedDamage, STANDARD_VELOCITY, config().velocity());
            var finalDamage = projectile.getDamage() * multiplier;
            projectile.setDamage(finalDamage);
        }
        return original.call(world, entity);
    }
}
