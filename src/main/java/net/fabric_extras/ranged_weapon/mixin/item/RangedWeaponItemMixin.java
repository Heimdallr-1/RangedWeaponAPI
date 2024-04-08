package net.fabric_extras.ranged_weapon.mixin.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.RangedWeaponItem;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.UUID;

@Mixin(RangedWeaponItem.class)
abstract class RangedWeaponItemMixin extends Item implements CustomRangedWeapon {
    private Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers = null;
    private List<EquipmentSlot> allowedSlots = List.of(EquipmentSlot.MAINHAND, EquipmentSlot.OFFHAND);
    private RangedConfig rangedWeaponConfig = RangedConfig.EMPTY;

    RangedWeaponItemMixin(Settings settings) {
        super(settings);
    }

    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        return (allowedSlots.contains(slot) && this.attributeModifiers != null) ? this.attributeModifiers : super.getAttributeModifiers(slot);
    }

    public RangedConfig getRangedWeaponConfig() {
        return this.rangedWeaponConfig;
    }

    public void setRangedWeaponConfig(RangedConfig config) {
        this.rangedWeaponConfig = config;
        // Update attributes
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        var damage = config.damage();
        if (damage > 0) {
            builder.put(EntityAttributes_RangedWeapon.DAMAGE.attribute, new EntityAttributeModifier(
                    RANGED_DAMAGE_MODIFIER_UUID,
                    "Ranged Weapon Damage",
                    damage,
                    EntityAttributeModifier.Operation.ADDITION));
        }
        this.attributeModifiers = builder.build();
    }

    private static final UUID RANGED_DAMAGE_MODIFIER_UUID = UUID.fromString("e5d0a858-012b-11ed-b939-0242ac120002");
}