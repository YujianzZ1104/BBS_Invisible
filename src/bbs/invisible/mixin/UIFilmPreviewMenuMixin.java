/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.settings.ui.UISettingsOverlayPanel;
import mchorse.bbs_mod.ui.film.UIFilmPreview;
import mchorse.bbs_mod.ui.framework.elements.UIElement;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.utils.context.ContextMenuManager;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在「录制视频」按钮的右键菜单末尾加入透明背景的快捷开关和设置入口。
 * 目标是 UIFilmPreview 构造函数里为录制按钮生成的菜单回调（BBS 2.7 里的 lambda$new$31）。
 */
@Mixin(value = UIFilmPreview.class, remap = false)
public class UIFilmPreviewMenuMixin
{
    @Inject(method = "lambda$new$31", at = @At("TAIL"), require = 0)
    private void bbsfs$menu(ContextMenuManager menu, CallbackInfo ci)
    {
        menu.action(Icons.GEAR, L10n.lang("bbs_invisible.menu.settings"), () ->
        {
            UISettingsOverlayPanel panel = new UISettingsOverlayPanel();

            panel.showCategory(Transparency.MOD_ID, "transparent");
            UIOverlay.addOverlay(((UIElement) (Object) this).getContext(), panel, 430, 400);
        });
    }
}
