package net.fabric_extras.ranged_weapon.api;

import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.util.Identifier;

public interface CustomRangedWeapon {
    RangedConfig getRangedWeaponConfig();

    // Sets the baseline configuration for the weapon, multipliers are calculated compared to this
    // Already configured for known weapon types, such as BOW and CROSSBOW
    // Should only be used for custom RangedWeaponItem subclasses
    void setTypeBaseline(RangedConfig config);
    // Returns the baseline configuration for the weapon, representing the default value for a weapon type
    // Already configured for known weapon types, such as BOW and CROSSBOW
    RangedConfig getTypeBaseline();

    static AttributeModifiersComponent createAttributeModifiers(RangedConfig config) {

        var damage = new EntityAttributeModifier(
                Identifier.of("ranged_weapon", "weapon_damage"),
                config.damage(),
                EntityAttributeModifier.Operation.ADD_VALUE);

        var pullTime = new EntityAttributeModifier(
                Identifier.of("ranged_weapon", "weapon_pull_time"),
                config.pull_time_diff(),
                EntityAttributeModifier.Operation.ADD_VALUE);

        return AttributeModifiersComponent.builder()
                .add(
                        EntityAttributes_RangedWeapon.DAMAGE.entry,
                        damage,
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes_RangedWeapon.DAMAGE.entry,
                        damage,
                        AttributeModifierSlot.OFFHAND
                )
                .add(
                        EntityAttributes_RangedWeapon.PULL_TIME.entry,
                        pullTime,
                        AttributeModifierSlot.MAINHAND
                )
                .add(
                        EntityAttributes_RangedWeapon.PULL_TIME.entry,
                        pullTime,
                        AttributeModifierSlot.OFFHAND
                )
                .build();
    }
}
