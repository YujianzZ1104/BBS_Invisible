/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.class_4184;
import net.minecraft.class_758;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * BackgroundRenderer（class_758）：关闭雾，否则远处的方块会被染成雾色。
 */
@Mixin(value = class_758.class, priority = 2000)
public class BackgroundRendererMixin
{
    /* applyFog */
    @Inject(method = "method_3211(Lnet/minecraft/class_4184;Lnet/minecraft/class_758$class_4596;FZF)V", at = @At("HEAD"), cancellable = true)
    private static void bbsfs$cancelFog(class_4184 camera, class_758.class_4596 fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci)
    {
        if (Transparency.fogAmount() >= 1F)
        {
            class_758.method_23792();

            ci.cancel();
        }
    }

    /* 0 < 透明度 < 1：把雾的起止距离按 1/(1-透明度) 拉远，雾随透明度平滑淡出 */
    @Inject(method = "method_3211(Lnet/minecraft/class_4184;Lnet/minecraft/class_758$class_4596;FZF)V", at = @At("RETURN"), require = 0)
    private static void bbsfs$fadeFog(class_4184 camera, class_758.class_4596 fogType, float viewDistance, boolean thickFog, float tickDelta, CallbackInfo ci)
    {
        float amount = Transparency.fogAmount();

        if (amount > 0F && amount < 1F)
        {
            float scale = 1F / Math.max(0.001F, 1F - amount);

            RenderSystem.setShaderFogStart(RenderSystem.getShaderFogStart() * scale);
            RenderSystem.setShaderFogEnd(RenderSystem.getShaderFogEnd() * scale);
        }
    }
}
