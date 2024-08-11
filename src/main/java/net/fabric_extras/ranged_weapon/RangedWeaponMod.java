package net.fabric_extras.ranged_weapon;

import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.api.StatusEffects_RangedWeapon;
import net.fabric_extras.ranged_weapon.internal.CustomRangedWeaponInternal;
import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class RangedWeaponMod implements ModInitializer {

    public static final String NAMESPACE = "ranged_weapon";

    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        ((CustomRangedWeaponInternal) Items.BOW).setRangedWeaponConfig(RangedConfig.BOW);
        ((CustomRangedWeaponInternal) Items.CROSSBOW).setRangedWeaponConfig(RangedConfig.CROSSBOW);

        var boostEffectBonusPerLevel = 0.1;

        StatusEffects_RangedWeapon.DAMAGE.effect.addAttributeModifier(
                EntityAttributes_RangedWeapon.DAMAGE.entry,
                Identifier.of(NAMESPACE, "effect.damage"),
                boostEffectBonusPerLevel,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
        StatusEffects_RangedWeapon.HASTE.effect.addAttributeModifier(
                EntityAttributes_RangedWeapon.HASTE.entry,
                Identifier.of(NAMESPACE, "effect.haste"),
                boostEffectBonusPerLevel,
                EntityAttributeModifier.Operation.ADD_MULTIPLIED_BASE);
    }

    public static void registerAttributes() {
        for (var entry : EntityAttributes_RangedWeapon.all) {
            entry.register();
        }
    }

    public static void registerStatusEffects() {
        for (var entry : StatusEffects_RangedWeapon.all) {
            entry.register();
        }
    }
}
