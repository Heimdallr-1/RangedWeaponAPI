package net.fabric_extras.ranged_weapon.api;

import net.fabric_extras.ranged_weapon.internal.ScalingUtil;

/**
 * Represents the configurable properties of ranged weapons.
 * @param pull_time_bonus - the time (in seconds) added to standard pull time (1 sec)
 * @param damage - the amount of damage the weapon deals
 * @param velocity - customized velocity of the projectile, only applied if greater than 0
 *                 Does not affect the projectile damage!
 */
public record RangedConfig(float damage, float pull_time_bonus, float velocity) {
    public static final RangedConfig EMPTY = new RangedConfig(0, 0, 0);
    public static final RangedConfig BOW = new RangedConfig((float) ScalingUtil.BOW_BASELINE.damage(), 0, ScalingUtil.STANDARD_BOW_VELOCITY);
    public static final RangedConfig CROSSBOW = new RangedConfig( (float) ScalingUtil.CROSSBOW_BASELINE.damage(), 0.25F, ScalingUtil.STANDARD_CROSSBOW_VELOCITY);
}
