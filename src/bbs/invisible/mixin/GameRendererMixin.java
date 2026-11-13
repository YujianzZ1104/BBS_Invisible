/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import net.minecraft.class_757;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 光影透明：世界（含光影最终合成）画完、原版为画手而清深度之前，根据深度重写 alpha。
 */
@Mixin(value = class_757.class, priority = 2000)
public class GameRendererMixin
{
    @Inject(method = "method_3188", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_761;method_22710(Lnet/minecraft/class_4587;FJZLnet/minecraft/class_4184;Lnet/minecraft/class_757;Lnet/minecraft/class_765;Lorg/joml/Matrix4f;)V", shift = At.Shift.AFTER), require = 0)
    private void bbsfs$shaderAlpha(CallbackInfo ci)
    {
        Transparency.applyShaderAlpha();
    }
}
