/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.clip;

import bbs.invisible.render.Transparency;

import java.util.ArrayList;
import java.util.List;
import mchorse.bbs_mod.camera.clips.misc.CurveClip;
import mchorse.bbs_mod.l10n.L10n;
import mchorse.bbs_mod.ui.UIKeys;
import mchorse.bbs_mod.ui.film.IUIClipsDelegate;
import mchorse.bbs_mod.ui.film.clips.UICurveClip;
import mchorse.bbs_mod.ui.framework.elements.IUIElement;
import mchorse.bbs_mod.ui.framework.elements.overlay.UILabelListOverlayPanel;
import mchorse.bbs_mod.ui.framework.elements.overlay.UIOverlay;
import mchorse.bbs_mod.ui.framework.elements.input.keyframes.UIKeyframeSheet;
import mchorse.bbs_mod.ui.utils.Label;
import mchorse.bbs_mod.ui.utils.context.ContextMenuManager;
import mchorse.bbs_mod.ui.utils.context.MenuVerb;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;
import mchorse.bbs_mod.utils.keyframes.factories.KeyframeFactories;

/**
 * 「透明背景」剪辑的编辑面板：和曲线剪辑一样编辑关键帧，但「添加曲线」只能选透明背景自己的曲线，
 * 不会出现天气、太阳旋转、光影曲线等；可以删除不需要的曲线。
 */
public class UITransparentClip extends UICurveClip
{
    private static final String[][] KEYS = {
        {Transparency.CURVE_KEY, "bbs_invisible.curves.transparent_background"},
        {Transparency.CURVE_CLOUDS, "bbs_invisible.curves.transparent_clouds"},
        {Transparency.CURVE_TERRAIN, "bbs_invisible.curves.transparent_terrain"},
        {Transparency.CURVE_ENTITIES, "bbs_invisible.curves.transparent_entities"},
        {Transparency.CURVE_PARTICLES, "bbs_invisible.curves.transparent_particles"},
        {Transparency.CURVE_MODEL_BLOCKS, "bbs_invisible.curves.transparent_model_blocks"},
        {Transparency.CURVE_FILTER, "bbs_invisible.curves.transparent_filter"},
        {Transparency.CURVE_FOG, "bbs_invisible.curves.transparent_fog"}
    };

    public UITransparentClip(TransparentClip clip, IUIClipsDelegate editor)
    {
        super(clip, editor);
    }

    @Override
    protected void registerUI()
    {
        super.registerUI();

        /* 曲线剪辑刚注册的那一项右键菜单（最后一项）去掉，换成自己的：添加曲线只列透明背景的曲线 */
        try
        {
            java.lang.reflect.Field options = mchorse.bbs_mod.ui.framework.elements.UIElement.class.getDeclaredField("contextOptions");

            options.setAccessible(true);

            List<?> list = (List<?>) options.get(this.keyframes.view);

            if (list != null && !list.isEmpty())
            {
                list.remove(list.size() - 1);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }

        this.keyframes.view.context((ContextMenuManager menu) ->
        {
            menu.icon(MenuVerb.ADD, () ->
            {
                List<String> existing = new ArrayList<String>();

                for (KeyframeChannel<?> channel : ((CurveClip) this.clip).channels.getAllKeyframeChannels())
                {
                    existing.add(channel.getId());
                }

                ArrayList<Label<String>> list = new ArrayList<Label<String>>();

                for (String[] key : KEYS)
                {
                    if (!existing.contains(key[0]))
                    {
                        list.add(new Label<String>(L10n.lang(key[1]), key[0]));
                    }
                }

                UILabelListOverlayPanel panel = new UILabelListOverlayPanel(UIKeys.CAMERA_PANELS_PICK_KEY, list, (String s) ->
                {
                    ((CurveClip) this.clip).channels.addChannel(s, KeyframeFactories.DOUBLE);
                    this.fillData();
                });

                UIOverlay.addOverlay(this.getContext(), panel, 0.9F, 0.5F);
            }).label(UIKeys.CAMERA_PANELS_CURVE_ADD);

            UIKeyframeSheet sheet = this.keyframes.view.getDopeSheet().getSheet(this.getContext().mouseY);

            menu.icon(MenuVerb.REMOVE, () ->
            {
                ((CurveClip) this.clip).channels.removeChannel(sheet.channel);
                this.fillData();
            }).label(UIKeys.CAMERA_PANELS_CURVE_REMOVE).enabled(sheet != null);
        });
    }

    @Override
    protected void registerPanels()
    {
        super.registerPanels();

        List<IUIElement> children = this.panels.getChildren();

        if (!children.isEmpty())
        {
            this.panels.remove(children.get(children.size() - 1));
            this.panels.add(this.section(L10n.lang("bbs_invisible.clip.title"), this.edit));
        }
    }
}
