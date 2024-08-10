package net.fabric_extras.ranged_weapon.mixin.item;

import net.fabric_extras.ranged_weapon.internal.ItemSettingsExtension;
import net.minecraft.component.type.AttributeModifiersComponent;
import net.minecraft.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.Settings.class)
public class ItemSettingMixin implements ItemSettingsExtension {
    private boolean hasAttributeModifiers = false;

    @Override
    public boolean hasAttributeModifiers() {
        return hasAttributeModifiers;
    }

    @Inject(method = "attributeModifiers", at = @At("HEAD"))
    private void setHasAttributeModifiers(AttributeModifiersComponent attributeModifiersComponent, CallbackInfoReturnable<Item.Settings> cir) {
        hasAttributeModifiers = true;
    }
}
