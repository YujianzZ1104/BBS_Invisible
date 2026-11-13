/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.mixin;

import bbs.invisible.render.Transparency;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.ui.film.clips.UICurveClip;
import mchorse.bbs_mod.ui.framework.UIContext;
import mchorse.bbs_mod.ui.utils.Label;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 曲线剪辑的「选择曲线键值」列表里加一项「透明背景」。
 */
@Mixin(value = UICurveClip.class, remap = false)
public class UICurveClipMixin
{
    private static List<String> bbsfs$existing;

    @Inject(method = "offerCurveKeyList", at = @At("HEAD"), remap = false, require = 0)
    private static void bbsfs$capture(UIContext context, List<String> existing, Consumer<String> callback, CallbackInfo ci)
    {
        bbsfs$existing = existing;
    }

    @ModifyArg(method = "offerCurveKeyList", at = @At(value = "INVOKE", target = "Lmchorse/bbs_mod/ui/framework/elements/overlay/UILabelListOverlayPanel;<init>(Lmchorse/bbs_mod/l10n/keys/IKey;Ljava/util/Collection;Ljava/util/function/Consumer;)V"), index = 1, remap = false, require = 0)
    private static Collection<Label<String>> bbsfs$addKey(Collection<Label<String>> list)
    {
        bbsfs$offer(list, Transparency.CURVE_KEY, "bbs_invisible.curves.transparent_background");
        bbsfs$offer(list, Transparency.CURVE_CLOUDS, "bbs_invisible.curves.transparent_clouds");
        bbsfs$offer(list, Transparency.CURVE_TERRAIN, "bbs_invisible.curves.transparent_terrain");
        bbsfs$offer(list, Transparency.CURVE_ENTITIES, "bbs_invisible.curves.transparent_entities");
        bbsfs$offer(list, Transparency.CURVE_PARTICLES, "bbs_invisible.curves.transparent_particles");
        bbsfs$offer(list, Transparency.CURVE_MODEL_BLOCKS, "bbs_invisible.curves.transparent_model_blocks");
        bbsfs$offer(list, Transparency.CURVE_FILTER, "bbs_invisible.curves.transparent_filter");
        bbsfs$offer(list, Transparency.CURVE_FOG, "bbs_invisible.curves.transparent_fog");

        return list;
    }

    private static void bbsfs$offer(Collection<Label<String>> list, String key, String lang)
    {
        if (bbsfs$existing == null || !bbsfs$existing.contains(key))
        {
            list.add(new Label<String>(L10n.lang(lang), key));
        }
    }
}
