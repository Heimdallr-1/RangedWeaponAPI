package net.fabric_extras.ranged_weapon.api;

public interface RangedWeaponDamage {
    void setRangedWeaponDamage(double value, boolean mainHand, boolean offHand);
    default void setRangedWeaponDamage(double value) {
        setRangedWeaponDamage(value, true, true);
    }

    double getRangedWeaponDamage();
}
