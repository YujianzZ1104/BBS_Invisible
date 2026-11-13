/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible;

import bbs.invisible.clip.TransparentClip;
import bbs.invisible.render.Transparency;

import mchorse.bbs_mod.api.BBSAddonMod;
import mchorse.bbs_mod.api.Subscribe;
import mchorse.bbs_mod.api.events.RegisterCameraClipsEvent;
import mchorse.bbs_mod.api.events.RegisterSourcePacksEvent;
import mchorse.bbs_mod.camera.clips.ClipFactoryData;
import mchorse.bbs_mod.resources.Link;
import mchorse.bbs_mod.ui.utils.icons.Icons;

/**
 * "bbs-addon" 入口：把本模组的资源（语言文件）注册为 BBS 的资源包。
 * 必须在 BBS 主初始化阶段注册，所以和 client 入口分开。
 */
public class BbsInvisibleAddon implements BBSAddonMod
{
    @Subscribe
    public void onRegisterSourcePacks(RegisterSourcePacksEvent event)
    {
        event.registerAddon(Transparency.MOD_ID, BbsInvisibleAddon.class);
    }

    /* 电影编辑器里新增一种剪辑：透明背景 */
    @Subscribe
    public void onRegisterCameraClips(RegisterCameraClipsEvent event)
    {
        event.factory.register(new Link(Transparency.MOD_ID, Transparency.CLIP_ID), TransparentClip.class, new ClipFactoryData(Icons.VISIBLE, 0x66CCFF));
    }
}
