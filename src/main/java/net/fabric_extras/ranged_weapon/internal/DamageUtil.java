package net.fabric_extras.ranged_weapon.internal;

public class DamageUtil {
    public static double arrowDamageMultiplier(double standardDamage, double attributeDamage, double standardVelocity, double customVelocity) {
        // Boost damage based on the attribute
        var multiplier = (attributeDamage / standardDamage);
        if (customVelocity > 0) {
            // Counteract the damage boost by caused by non-standard velocity
            multiplier *= (standardVelocity / customVelocity);
        }
        return multiplier;
    }
}
