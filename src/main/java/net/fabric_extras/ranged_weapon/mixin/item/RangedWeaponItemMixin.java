package net.fabric_extras.ranged_weapon.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.internal.ScalingUtil;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(RangedWeaponItem.class)
abstract class RangedWeaponItemMixin extends Item implements CustomRangedWeapon {
    private RangedConfig rangedWeaponConfig = RangedConfig.BOW;

    RangedWeaponItemMixin(Settings settings) {
        super(settings);
    }

    public RangedConfig getRangedWeaponConfig() {
        return this.rangedWeaponConfig;
    }

    public void setRangedWeaponConfig(RangedConfig config) {
        this.rangedWeaponConfig = config;
    }

    private RangedConfig typeBaseLine = RangedConfig.BOW;

    public void setTypeBaseline(RangedConfig config) {
        this.typeBaseLine = config;
    }

    public RangedConfig getTypeBaseline() {
        return this.typeBaseLine;
    }

    @WrapOperation(
            method = "shootAll",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/RangedWeaponItem;shoot(Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/entity/projectile/ProjectileEntity;IFFFLnet/minecraft/entity/LivingEntity;)V"))
    private void applyCustomVelocity_RWA(
            RangedWeaponItem instance, LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence, float yaw, @Nullable LivingEntity target,
            Operation<Void> original) {
        var velocity = getRangedWeaponConfig().velocity();
        if (velocity > 0) {
            var velocityMultiplier = (float) ScalingUtil.arrowVelocityMultiplier(getTypeBaseline().velocity(), getRangedWeaponConfig().velocity());
            speed *= velocityMultiplier;
        }
        original.call(instance, shooter, projectile, index, speed, divergence, yaw, target);


        CustomRangedWeapon weapon = this;
        if (projectile instanceof PersistentProjectileEntity projectileEntity) {
            var rangedDamage = shooter.getAttributeValue(EntityAttributes_RangedWeapon.DAMAGE.entry);
            if (rangedDamage > 0) {
                var multiplier = ScalingUtil.arrowDamageMultiplier(getTypeBaseline().damage(), rangedDamage, getTypeBaseline().velocity(), weapon.getRangedWeaponConfig().velocity());
                var finalDamage = projectileEntity.getDamage() * multiplier;
                projectileEntity.setDamage(finalDamage);
            }
        }
    }
}