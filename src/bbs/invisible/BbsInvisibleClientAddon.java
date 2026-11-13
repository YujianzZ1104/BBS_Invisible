/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible;

import bbs.invisible.clip.TransparentClip;
import bbs.invisible.clip.UITransparentClip;
import bbs.invisible.render.Transparency;

import mchorse.bbs_mod.api.BBSAddonMod;
import mchorse.bbs_mod.api.Subscribe;
import mchorse.bbs_mod.api.client.events.RegisterClientSettingsEvent;
import mchorse.bbs_mod.api.client.events.RegisterClipPanelsEvent;
import mchorse.bbs_mod.api.client.events.RegisterL10nEvent;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.utils.icons.Icons;

/**
 * "bbs-client-addon" 入口：在 BBS 设置面板里加一个「透明背景」模块，并加载它的语言文件。
 */
public class BbsInvisibleClientAddon implements BBSAddonMod
{
    @Subscribe
    public void onRegisterL10n(RegisterL10nEvent event)
    {
        /* 没有对应翻译的语言回退到英文，避免 BBS 打印“找不到语言文件”的错误 */
        event.l10n.registerOne((lang) ->
        {
            String file = lang.equals("zh_cn") || lang.equals("zh_tw") ? lang : "en_us";

            return new Link(Transparency.MOD_ID, "strings/" + file + ".json");
        });
    }

    @Subscribe
    public void onRegisterSettings(RegisterClientSettingsEvent event)
    {
        event.register(Icons.VISIBLE, Transparency.MOD_ID, Transparency::register);
    }

    @Subscribe
    public void onRegisterClipPanels(RegisterClipPanelsEvent event)
    {
        event.register(TransparentClip.class, UITransparentClip::new);
    }
}
