package net.fabric_extras.ranged_weapon.mixin.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.fabric_extras.ranged_weapon.RangedWeaponMod;
import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.internal.ScalingUtil;
import net.minecraft.component.ComponentChanges;
import net.minecraft.component.ComponentMapImpl;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.List;
import java.util.UUID;

@Mixin(RangedWeaponItem.class)
abstract class RangedWeaponItemMixin extends Item implements CustomRangedWeapon {
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = null;
    private List<EquipmentSlot> allowedSlots = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    private RangedConfig rangedWeaponConfig = RangedConfig.BOW;

    RangedWeaponItemMixin(Settings settings) {
        super(settings);
    }

//    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
//        return (allowedSlots.contains(slot) && this.attributeModifiers != null) ? this.attributeModifiers : super.getAttributeModifiers(slot);
//    }

    public RangedConfig getRangedWeaponConfig() {
        return this.rangedWeaponConfig;
    }

    public void setRangedWeaponConfig(RangedConfig config) {
        this.rangedWeaponConfig = config;
//        var components = (ComponentMapImpl) this.getComponents();
//        components.applyChanges(ComponentChanges.builder()
//                .add(DataComponentTypes.ATTRIBUTE_MODIFIERS,
//                        CustomRangedWeapon.createAttributeModifiers(config))
//                .build()
//        );
//        this.getComponents().
//        // Update attributes
//        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
//        var damage = config.damage();
//        if (damage > 0) {
//            builder.put(EntityAttributes_RangedWeapon.DAMAGE.attribute, new EntityAttributeModifier(
//                    Identifier.of(RangedWeaponMod.NAMESPACE, "weapon_damage"),
//                    damage,
//                    EntityAttributeModifier.Operation.ADD_VALUE));
//        }
//        this.attributeModifiers = builder.build();
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