/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.blocks.entities.ModelBlockEntity;
import net.minecraft.class_2586;
import net.minecraft.class_4587;
import net.minecraft.class_4597;
import net.minecraft.class_824;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 隐藏地形时一并隐藏方块实体（箱子、告示牌……）。BBS 的模型方块不受影响。
 */
@Mixin(class_824.class)
public class BlockEntityRenderDispatcherMixin
{
    @Inject(method = "method_3555(Lnet/minecraft/class_2586;FLnet/minecraft/class_4587;Lnet/minecraft/class_4597;)V", at = @At("HEAD"), cancellable = true)
    private void bbsfs$cancelBlockEntity(class_2586 blockEntity, float tickDelta, class_4587 matrices, class_4597 vertexConsumers, CallbackInfo ci)
    {
        if (blockEntity instanceof ModelBlockEntity)
        {
            if (Transparency.modelBlocksAmount() >= 1F)
            {
                ci.cancel();
            }

            return;
        }

        if (Transparency.isActive() && Transparency.terrainAmount() >= 1F && Transparency.hideBlockEntities.get())
        {
            ci.cancel();
        }
    }
}
