package net.fabric_extras.ranged_weapon.mixin.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedWeaponDamage;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(RangedWeaponItem.class)
abstract class RangedWeaponItemMixin extends Item implements RangedWeaponDamage {
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = null;
    private List<EquipmentSlot> allowedSlots = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    // private RangedWeaponKind rangedWeaponKind = RangedWeaponKind.from(this);

    // Helper, not actual source of truth
    private double rangedWeaponDamage = 0;

    RangedWeaponItemMixin(Settings settings) {
        super(settings);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return (allowedSlots.contains(slot) && this.attributeModifiers != null) ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    public void setRangedWeaponDamage(double value, boolean mainHand, boolean offHand) {
        allowedSlots = new ArrayList<>();
        if (mainHand) {
            allowedSlots.add(EquipmentSlot.MAINHAND);
        }
        if (offHand) {
            allowedSlots.add(EquipmentSlot.OFFHAND);
        }
        rangedWeaponDamage = value;
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes_RangedWeapon.DAMAGE.attribute, new EntityAttributeModifier(
                RANGED_DAMAGE_MODIFIER_UUID,
                "Ranged Weapon Damage",
                value,
                EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    private static final UUID RANGED_DAMAGE_MODIFIER_UUID = UUID.fromString("e5d0a858-012b-11ed-b939-0242ac120002");

    public double getRangedWeaponDamage() {
        return rangedWeaponDamage;
    }
}