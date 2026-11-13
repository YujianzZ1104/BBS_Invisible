/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.ui.film.UIFilmPanel;
import mchorse.bbs_mod.ui.framework.elements.utils.Batcher2D;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 电影面板预览：背景原本是一整块黑色，开启透明背景后换成棋盘格。
 */
@Mixin(value = UIFilmPanel.class, remap = false)
public class UIFilmPanelMixin
{
    @Redirect(method = "renderPanelBackground", at = @At(value = "INVOKE", target = "Lmchorse/bbs_mod/ui/framework/elements/utils/Batcher2D;box(FFFFI)V", ordinal = 0), remap = false, require = 0)
    private void bbsfs$checkerboard(Batcher2D batcher, float x1, float y1, float x2, float y2, int color)
    {
        if (Transparency.shouldDrawCheckerboard())
        {
            Transparency.drawCheckerboard(batcher, x1, y1, x2, y2);
        }
        else
        {
            batcher.box(x1, y1, x2, y2, color);
        }
    }
}
