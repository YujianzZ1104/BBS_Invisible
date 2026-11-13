/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.l10n.keys.IKey;
import mchorse.bbs_mod.ui.film.clips.UICurveClip;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

/**
 * 曲线剪辑时间线里的曲线标题：把三个透明背景曲线的 id 换成本地化名称。
 */
@Mixin(value = UICurveClip.class, remap = false)
public class UIKeyframeSheetTitleMixin
{
    @ModifyArg(method = "addKeyframeSheet", at = @At(value = "INVOKE", target = "Lmchorse/bbs_mod/ui/framework/elements/input/keyframes/UIKeyframeSheet;<init>(Ljava/lang/String;Lmchorse/bbs_mod/l10n/keys/IKey;ILmchorse/bbs_mod/utils/keyframes/KeyframeChannel;Lmchorse/bbs_mod/settings/values/base/BaseValueBasic;)V"), index = 1, remap = false, require = 0)
    private IKey bbsfs$title(IKey title)
    {
        String id = title.get();

        if (Transparency.CURVE_KEY.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_background");
        }
        else if (Transparency.CURVE_CLOUDS.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_clouds");
        }
        else if (Transparency.CURVE_TERRAIN.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_terrain");
        }
        else if (Transparency.CURVE_ENTITIES.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_entities");
        }
        else if (Transparency.CURVE_PARTICLES.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_particles");
        }
        else if (Transparency.CURVE_MODEL_BLOCKS.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_model_blocks");
        }
        else if (Transparency.CURVE_FILTER.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_filter");
        }
        else if (Transparency.CURVE_FOG.equals(id))
        {
            return L10n.lang("bbs_invisible.curves.transparent_fog");
        }

        return title;
    }
}
