package net.fabric_extras.ranged_weapon.api;

import net.minecraft.entity.attribute.ClampedEntityAttribute;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class EntityAttributes_RangedWeapon {
    public static final String NAMESPACE = "ranged_weapon";
    public static final ArrayList<Entry> all = new ArrayList<>();
    private static Entry entry(String name, double baseValue, boolean tracked) {
        var entry = new Entry(name, baseValue, tracked);
        all.add(entry);
        return entry;
    }

    public static class Entry {
        public final Identifier id;
        public final String translationKey;
        public final EntityAttribute attribute;
        private final double baseValue;
        public Entry(String name, double baseValue, boolean tracked) {
            this.id = new Identifier(NAMESPACE, name);
            this.translationKey = "attribute.name." + NAMESPACE + "." + name;
            this.attribute = new ClampedEntityAttribute(translationKey, baseValue, 0, 2048).setTracked(tracked);
            this.baseValue = baseValue;
        }

        public double asMultiplier(double attributeValue) {
            return attributeValue / baseValue;
        }
    }

    public static final Entry DAMAGE = entry("damage", 0, true);
    public static final Entry HASTE = entry("haste", 100, true);
}
