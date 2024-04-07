package net.fabric_extras.ranged_weapon;

import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedWeaponDamage;
import net.fabric_extras.ranged_weapon.api.StatusEffects_RangedWeapon;
import net.fabric_extras.ranged_weapon.internal.CustomStatusEffect;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class RangedWeaponMod implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ((CustomRangedWeapon) Items.BOW).setPullTime_RWA(20);
        ((RangedWeaponDamage) Items.BOW).setRangedWeaponDamage(6);
        ((CustomRangedWeapon) Items.CROSSBOW).setPullTime_RWA(25);

        var boostEffectBonusPerLevel = 0.1;
        StatusEffects_RangedWeapon.IMPACT.effect.addAttributeModifier(
                EntityAttributes_RangedWeapon.DAMAGE.attribute,
                CustomStatusEffect.uuid,
                boostEffectBonusPerLevel,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
        StatusEffects_RangedWeapon.RAPID_FIRE.effect.addAttributeModifier(
                EntityAttributes_RangedWeapon.HASTE.attribute,
                CustomStatusEffect.uuid,
                boostEffectBonusPerLevel,
                EntityAttributeModifier.Operation.MULTIPLY_BASE);
    }

    public static void registerAttributes() {
        for (var entry : EntityAttributes_RangedWeapon.all) {
            Registry.register(Registries.ATTRIBUTE, entry.id, entry.attribute);
        }
    }

    public static void registerStatusEffects() {
        for (var entry : StatusEffects_RangedWeapon.all) {
            Registry.register(Registries.STATUS_EFFECT, entry.id, entry.effect);
        }
    }
}
