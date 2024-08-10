package net.fabric_extras.ranged_weapon.mixin.item;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.authlib.GameProfile;
import net.fabric_extras.ranged_weapon.api.CrossbowMechanics;
import net.fabric_extras.ranged_weapon.api.CustomRangedWeapon;
import net.fabric_extras.ranged_weapon.api.EntityAttributes_RangedWeapon;
import net.fabric_extras.ranged_weapon.api.RangedConfig;
import net.fabric_extras.ranged_weapon.internal.ItemSettingsExtension;
import net.fabric_extras.ranged_weapon.internal.ScalingUtil;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

    /**
     * Apply custom velocity
     */
//    @ModifyVariable(method = "shoot", at = @At("HEAD"), ordinal = 1, argsOnly = true)
//    private static float applyCustomVelocity_RWA(float speed, World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectile, float soundPitch, boolean creative, float speed1, float divergence, float simulated) {
//        var item = crossbow.getItem();
//        if (item instanceof CustomRangedWeapon weapon) {
//            var customVelocity = weapon.getRangedWeaponConfig().velocity();
//            if (customVelocity > 0) {
//                return speed * (customVelocity / DEFAULT_SPEED);
//            }
//        }
//        return speed;
//    }


    /**
     * Apply custom damage
     */
//    @WrapOperation(method = "shoot", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;spawnEntity(Lnet/minecraft/entity/Entity;)Z"))
//    private static boolean applyCustomDamage(
//            // Mixin Parameters
//            World instance, Entity entity, Operation<Boolean> original,
//            // Context Parameters
//            World world, LivingEntity shooter, Hand hand, ItemStack crossbow, ItemStack projectileStack, float soundPitch, boolean creative, float speed, float divergence, float simulated
//    ) {
//        var item = crossbow.getItem();
//        if (entity instanceof PersistentProjectileEntity projectileEntity && item instanceof CustomRangedWeapon weapon) {
//            var rangedDamage = shooter.getAttributeValue(EntityAttributes_RangedWeapon.DAMAGE.attribute);
//            if (rangedDamage > 0) {
//                var multiplier = ScalingUtil.arrowDamageMultiplier(STANDARD_DAMAGE, rangedDamage, STANDARD_VELOCITY, weapon.getRangedWeaponConfig().velocity());
//                var finalDamage = projectileEntity.getDamage() * multiplier;
//                projectileEntity.setDamage(finalDamage);
//            }
//        }
//        return original.call(world, entity);
//    }
}
