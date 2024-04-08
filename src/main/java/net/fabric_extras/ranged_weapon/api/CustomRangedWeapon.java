package net.fabric_extras.ranged_weapon.api;

public interface CustomRangedWeapon {
    RangedConfig getRangedWeaponConfig();
    void setRangedWeaponConfig(RangedConfig config);
    default void configure(RangedConfig config) {
        setRangedWeaponConfig(config);
    }
}
