/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.AlphaFix;
import bbs.invisible.render.CloudFade;
import bbs.invisible.render.TerrainFade;
import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.BBSSettings;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.forms.FormTranslucentQueue;
import net.minecraft.class_1921;
import net.minecraft.class_4184;
import net.minecraft.class_4587;
import net.minecraft.class_761;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * WorldRenderer（class_761）：去掉天空 / 云 / 地形。
 * 方法名使用 intermediary 名（与 BBS 自己的 mixin 一致），因此不需要 refmap。
 * priority 调高，让这里先于 BBS 的「色度天空」处理。
 */
@Mixin(value = class_761.class, priority = 2000)
public class WorldRendererMixin
{
    /* renderSky */
    @Inject(method = "method_3257(Lnet/minecraft/class_4587;Lorg/joml/Matrix4f;FLnet/minecraft/class_4184;ZLjava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void bbsfs$cancelSky(class_4587 matrices, Matrix4f projectionMatrix, float tickDelta, class_4184 camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci)
    {
        if (Transparency.amount() >= 1F)
        {
            /* 此时世界里还什么都没画：把颜色缓冲（含 alpha）清成全透明 */
            GL11.glClearColor(0F, 0F, 0F, 0F);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

            ci.cancel();
        }
    }

    /* 透明度在 0~1 之间：天空照常画，画完把整个画面的 alpha 设成 1 - 透明度 */
    @Inject(method = "method_3257(Lnet/minecraft/class_4587;Lorg/joml/Matrix4f;FLnet/minecraft/class_4184;ZLjava/lang/Runnable;)V", at = @At("RETURN"), require = 0)
    private void bbsfs$fadeSky(class_4587 matrices, Matrix4f projectionMatrix, float tickDelta, class_4184 camera, boolean thickFog, Runnable fogCallback, CallbackInfo ci)
    {
        if (!Transparency.isActive())
        {
            return;
        }

        float amount = Transparency.amount();

        if (amount < 1F)
        {
            GL11.glColorMask(false, false, false, true);
            GL11.glClearColor(0F, 0F, 0F, 1F - amount);
            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glColorMask(true, true, true, true);
        }
    }

    /* renderClouds：去除程度 >= 1 时不渲染 */
    @Inject(method = "method_3259(Lnet/minecraft/class_4587;Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void bbsfs$cancelClouds(class_4587 matrices, Matrix4f projectionMatrix, float tickDelta, double cameraX, double cameraY, double cameraZ, CallbackInfo ci)
    {
        if (Transparency.cloudAmount() >= 1F)
        {
            ci.cancel();
        }
    }

    /* 0 < 去除程度 < 1：在 render 里调用云层前后拷贝 / 混合画面，实现渐隐（与 Sodium 等无关） */
    @Inject(method = "method_22710(Lnet/minecraft/class_4587;FJZLnet/minecraft/class_4184;Lnet/minecraft/class_757;Lnet/minecraft/class_765;Lorg/joml/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_761;method_3259(Lnet/minecraft/class_4587;Lorg/joml/Matrix4f;FDDD)V"), require = 0)
    private void bbsfs$cloudsBefore(CallbackInfo ci)
    {
        float amount = Transparency.cloudAmount();

        if (amount > 0F && amount < 1F)
        {
            CloudFade.begin();
        }
    }

    @Inject(method = "method_22710(Lnet/minecraft/class_4587;FJZLnet/minecraft/class_4184;Lnet/minecraft/class_757;Lnet/minecraft/class_765;Lorg/joml/Matrix4f;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/class_761;method_3259(Lnet/minecraft/class_4587;Lorg/joml/Matrix4f;FDDD)V", shift = At.Shift.AFTER), require = 0)
    private void bbsfs$cloudsAfter(CallbackInfo ci)
    {
        float amount = Transparency.cloudAmount();

        if (amount > 0F && amount < 1F)
        {
            CloudFade.end(amount);
        }
    }

    /* renderLayer（地形） */
    @Inject(method = "method_3251(Lnet/minecraft/class_1921;Lnet/minecraft/class_4587;DDDLorg/joml/Matrix4f;)V", at = @At("HEAD"), cancellable = true)
    private void bbsfs$cancelTerrain(class_1921 layer, class_4587 matrices, double x, double y, double z, Matrix4f projectionMatrix, CallbackInfo ci)
    {
        /* 半透明地形（水等）之前：把已经画好的不透明内容（树叶、方块、实体）的 alpha 补成 1 */
        float terrain = Transparency.terrainAmount();
        boolean fading = terrain > 0F && terrain < 1F && !BBSRendering.isIrisShadowPass();

        if (Transparency.isActive() && Transparency.opaqueFix.get() && !fading && layer == class_1921.method_23583() && !BBSRendering.isIrisShadowPass())
        {
            AlphaFix.run();
        }

        /* 地形渐隐：每个地形层画之前先拷贝画面，画完之后（见 WorldRendererFadeMixin）按比例混回去 */
        if (fading && TerrainFade.isFadeLayer(layer))
        {
            CloudFade.begin();
        }

        if (Transparency.terrainAmount() < 1F)
        {
            return;
        }

        /* 下面两步和 BBS 自己隐藏地形时做的一样，保证 BBS 的模型 / 光影兼容渲染不受影响 */
        if (layer == class_1921.method_23583() && !BBSRendering.isIrisShadowPass())
        {
            FormTranslucentQueue.flush();
        }

        boolean bbsDoesIt = BBSSettings.chromaSkyEnabled.get() && !BBSSettings.chromaSkyTerrain.get();

        if (layer == class_1921.method_23577() && !bbsDoesIt)
        {
            BBSRendering.onRenderChunkLayer(matrices);
        }

        ci.cancel();
    }
}
