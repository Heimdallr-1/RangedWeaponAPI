package net.fabric_extras.ranged_weapon.mixin.item;

import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.internal.ItemSettingsExtension;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {
    private RangedConfig config() {
        return ((CustomRangedWeapon) this).getRangedWeaponConfig();
    }

    public float getPullProgress_RWA(int useTicks) {
        float pullTime = config().pull_time() > 0 ? config().pull_time() : 25;
        float f = (float)useTicks / pullTime;
        f = (f * f + f * 2.0F) / 3.0F;
        if (f > 1.0F) {
            f = 1.0F;
        }
        return f;
    }

    @ModifyVariable(method = "<init>", at = @At("HEAD"), ordinal = 0)
    private static Item.Settings applyDefaultAttributes(Item.Settings settings) {
        if ((ItemSettingsExtension) settings instanceof ItemSettingsExtension extension && !extension.hasAttributeModifiers()) {
            return settings.attributeModifiers(CustomRangedWeapon.createAttributeModifiers(RangedConfig.CROSSBOW));
        } else {
            return settings;
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void postInit(Item.Settings settings, CallbackInfo ci) {
        ((CustomRangedWeapon)this).setTypeBaseline(RangedConfig.CROSSBOW);
    }

    /**
     * Apply custom pull time
     */
    @Inject(method = "getPullTime", at = @At("HEAD"), cancellable = true)
    private static void applyCustomPullTime_RWA(ItemStack stack, LivingEntity user, CallbackInfoReturnable<Integer> cir) {
        var item = stack.getItem();
        if (item instanceof CustomRangedWeapon weapon) {
            float pullTime = weapon.getRangedWeaponConfig().pull_time() / 20f;
            float f = EnchantmentHelper.getCrossbowChargeTime(stack, user, pullTime);
            pullTime = MathHelper.floor(f * 20.0F);
//            if (pullTime > 0) {
//                pullTime = CrossbowMechanics.PullTime.modifier.getPullTime(pullTime, stack, user);
                cir.setReturnValue((int) pullTime);
                cir.cancel();
//            }
        }
    }
}
