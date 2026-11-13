/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.clip;

import bbs.invisible.render.Transparency;

import java.util.ArrayList;
import mchorse.bbs_mod.camera.clips.misc.CurveClientClip;
import mchorse.bbs_mod.utils.clips.Clip;
import mchorse.bbs_mod.utils.keyframes.KeyframeChannel;

/**
 * 「透明背景」剪辑：本质是一个预置好三条曲线（天空 / 云层 / 雾）的曲线剪辑，
 * 曲线 id 与曲线剪辑里手动添加的「透明背景」键值相同，所以两种方式可以互换使用。
 */
public class TransparentClip extends CurveClientClip
{
    public TransparentClip()
    {
        super();

        /* 去掉曲线剪辑默认带的「太阳旋转」 */
        for (KeyframeChannel<?> channel : new ArrayList<KeyframeChannel<?>>(this.channels.getAllKeyframeChannels()))
        {
            this.channels.removeChannel(channel);
        }

        this.channels.addChannel(Transparency.CURVE_KEY);
        this.channels.addChannel(Transparency.CURVE_CLOUDS);
        this.channels.addChannel(Transparency.CURVE_TERRAIN);
        this.channels.addChannel(Transparency.CURVE_FOG);
        this.channels.addChannel(Transparency.CURVE_ENTITIES);
        this.channels.addChannel(Transparency.CURVE_PARTICLES);
        this.channels.addChannel(Transparency.CURVE_MODEL_BLOCKS);
        this.channels.addChannel(Transparency.CURVE_FILTER);

    }

    @Override
    protected Clip create()
    {
        return new TransparentClip();
    }
}
