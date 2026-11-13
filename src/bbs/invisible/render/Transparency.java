/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.render;

import mchorse.bbs_mod.BBSModClient;
import java.lang.reflect.Method;
import java.util.function.Function;
import mchorse.bbs_mod.camera.clips.misc.CurveClip;
import mchorse.bbs_mod.client.BBSRendering;
import mchorse.bbs_mod.graphics.texture.Texture;
import mchorse.bbs_mod.graphics.texture.TextureFormat;
import mchorse.bbs_mod.settings.SettingsBuilder;
import mchorse.bbs_mod.settings.values.core.ValueString;
import mchorse.bbs_mod.settings.values.numeric.ValueBoolean;
import mchorse.bbs_mod.settings.values.numeric.ValueFloat;
import mchorse.bbs_mod.settings.values.numeric.ValueInt;
import mchorse.bbs_mod.ui.framework.elements.utils.Batcher2D;
import mchorse.bbs_mod.ui.utils.icons.Icons;
import mchorse.bbs_mod.utils.clips.ClipContext;

/**
 * 透明背景的设置与公共逻辑（所有 mixin 都只读这里）。
 */
public final class Transparency
{
    public static final String MOD_ID = "bbs_invisible";

    /** 曲线剪辑里的键值 id：天空 / 云层 / 雾，各自独立 */
    public static final String CLIP_ID = "transparent_background";
    public static final String CURVE_KEY = "transparent_background";
    public static final String CURVE_CLOUDS = "transparent_clouds";
    public static final String CURVE_TERRAIN = "transparent_terrain";
    public static final String CURVE_ENTITIES = "transparent_entities";
    public static final String CURVE_PARTICLES = "transparent_particles";
    public static final String CURVE_MODEL_BLOCKS = "transparent_model_blocks";
    public static final String CURVE_FILTER = "transparent_filter";
    public static final String CURVE_FOG = "transparent_fog";

    /* %WIDTH% %HEIGHT% %FPS% %FILTERS% %NAME% %AUDIO_TRACK% 是 BBS 自己的占位符 */
    private static final String IN = "-f rawvideo -pix_fmt bgra -s %WIDTH%x%HEIGHT% -r %FPS% -i - ";
    private static final String IN_AUDIO = IN + "-i %AUDIO_TRACK% ";

    public static final String PRORES = "-vf %FILTERS% -c:v prores_ks -profile:v 4444 -vendor apl0 -pix_fmt yuva444p10le ";
    public static final String PNG = "-vf %FILTERS% -c:v png -pix_fmt rgba %NAME%_%05d.png";
    public static final String WEBM = "-vf %FILTERS% -c:v libvpx-vp9 -pix_fmt yuva420p -b:v 0 -crf 20 -auto-alt-ref 0 -row-mt 1 ";

    public static final String DEFAULT_CUSTOM = IN + PRORES + "%NAME%.mov";

    public static ValueFloat defaultAmount;
    public static ValueBoolean enabled;
    public static ValueBoolean filmOnly;
    public static ValueBoolean hideClouds;
    public static ValueBoolean disableFog;
    public static ValueBoolean opaqueFix;
    public static ValueBoolean checkerboard;
    public static ValueBoolean hideTerrain;
    public static ValueBoolean hideBlockEntities;
    public static ValueBoolean shaderAlpha;
    public static ValueBoolean hideEntities;
    public static ValueBoolean hideParticles;
    public static ValueBoolean hideModelBlocks;
    public static ValueBoolean useFilter;
    public static ValueInt filterMode;
    public static ValueString filterIds;

    public static ValueInt videoFormat;
    public static ValueString customArguments;

    /** 开始录制时锁定：录制过程中即使在设置里切换开关，也不会让像素格式变化 */
    private static boolean latchedAlpha;

    private Transparency()
    {}

    public static void register(SettingsBuilder builder)
    {
        builder.category("transparent", Icons.GLOBE);

        enabled = builder.getBoolean("enabled", false);
        defaultAmount = builder.getFloat("amount", 1F, 0F, 1F);
        filmOnly = builder.getBoolean("film_only", true);
        hideClouds = builder.getBoolean("hide_clouds", true);
        disableFog = builder.getBoolean("disable_fog", true);
        opaqueFix = builder.getBoolean("opaque_fix", true);
        checkerboard = builder.getBoolean("checkerboard", true);
        hideTerrain = builder.getBoolean("hide_terrain", false);
        hideBlockEntities = builder.getBoolean("hide_block_entities", true);
        shaderAlpha = builder.getBoolean("shader_alpha", false);
        hideEntities = builder.getBoolean("hide_entities", false);
        hideParticles = builder.getBoolean("hide_particles", false);
        hideModelBlocks = builder.getBoolean("hide_model_blocks", false);
        useFilter = builder.getBoolean("use_filter", false);
        filterMode = builder.getInt("filter_mode", 0, 0, 1);
        filterIds = builder.getString("filter_ids", "");

        builder.category("export", Icons.FILM);

        videoFormat = builder.getInt("format", 0, 0, 3);
        customArguments = builder.getString("arguments", DEFAULT_CUSTOM);
    }

    /** 透明背景总开关（导出管线用：纹理格式 / 视频编码） */
    public static boolean isEnabled()
    {
        return enabled != null && enabled.get();
    }

    /** 当前这一帧世界渲染是否要去掉天空等背景元素 */
    public static boolean isActive()
    {
        return isEnabled() && (!filmOnly.get() || BBSRendering.isCustomSize());
    }

    private static Method curveMethod;
    private static boolean curveFailed;

    /** 曲线剪辑里「透明背景」键当前的值；没有曲线 / 读取失败返回 null */
    public static Double curveValue(String key)
    {
        if (curveFailed)
        {
            return null;
        }

        try
        {
            if (curveMethod == null)
            {
                curveMethod = BBSRendering.class.getDeclaredMethod("getCurveValue", String.class, Function.class);
                curveMethod.setAccessible(true);
            }

            Function<Object, Object> values = (context) -> CurveClip.getValues((ClipContext) context);

            return (Double) curveMethod.invoke(null, key, values);
        }
        catch (Throwable e)
        {
            curveFailed = true;
            e.printStackTrace();

            return null;
        }
    }

    private static final java.util.Map<String, float[]> LAST = new java.util.HashMap<String, float[]>();
    private static final java.util.Map<String, Long> LAST_STAMP = new java.util.HashMap<String, Long>();

    /**
     * 曲线值偶尔会在某一帧读不到（剪辑数据重建的间隙），这时如果直接用「没有曲线」的默认值，
     * 画面就会在曲线值和默认值之间来回闪。所以 250 毫秒内读不到时沿用上一次读到的值。
     */
    private static float resolve(String key, float fallback)
    {
        Double curve = curveValue(key);
        long now = System.nanoTime();
        float value;

        if (curve != null)
        {
            value = curve.floatValue();
            LAST.put(key, new float[] {value});
            LAST_STAMP.put(key, now);
        }
        else
        {
            float[] last = LAST.get(key);
            Long stamp = LAST_STAMP.get(key);

            value = last != null && stamp != null && now - stamp < 250_000_000L ? last[0] : fallback;
        }

        return Math.max(0F, Math.min(1F, value));
    }

    /** 天空的透明程度：0 = 正常天空，1 = 完全透明。没有曲线时用「默认透明度」 */
    public static float amount()
    {
        return isActive() ? resolve(CURVE_KEY, defaultAmount.get()) : 0F;
    }

    /** 云层被去掉的程度：0 = 正常，1 = 不渲染。没有曲线时由「隐藏云层」开关决定 */
    /**
     * 光影下渐隐（先拷贝画面再混合）会和光影的延迟光照冲突，做不出真正的半透明，
     * 所以开着光影时把 0~1 之间的值吸附成「显示 / 隐藏」，避免出现怪异的中间状态。
     */
    private static float snap(float amount)
    {
        if (amount > 0F && amount < 1F && BBSRendering.isIrisShadersEnabled())
        {
            return amount >= 0.5F ? 1F : 0F;
        }

        return amount;
    }

    public static float cloudAmount()
    {
        return isActive() ? snap(resolve(CURVE_CLOUDS, hideClouds.get() ? 1F : 0F)) : 0F;
    }

    /** 地形被去掉的程度：0 = 正常，1 = 不渲染。没有曲线时由「隐藏地形」开关决定 */
    public static float terrainAmount()
    {
        return isActive() ? snap(resolve(CURVE_TERRAIN, hideTerrain.get() ? 1F : 0F)) : 0F;
    }

    /** 雾被去掉的程度：0 = 正常，1 = 完全无雾。没有曲线时由「关闭雾」开关决定 */
    public static float fogAmount()
    {
        return isActive() ? resolve(CURVE_FOG, disableFog.get() ? 1F : 0F) : 0F;
    }

    /* ---- 实体 / 粒子 / 模型方块 / ID 过滤：曲线值 0 = 显示，1 = 隐藏，中间 = 渐隐 ---- */

    private static long flagStamp;
    private static float aEntities;
    private static float aParticles;
    private static float aModelBlocks;
    private static float aFilter;

    /** 一帧里会被问很多次，所以 2 毫秒内复用上次读到的曲线值 */
    private static void refreshFlags()
    {
        long now = System.nanoTime();

        if (now - flagStamp < 2_000_000L)
        {
            return;
        }

        flagStamp = now;

        if (!isActive())
        {
            aEntities = aParticles = aModelBlocks = aFilter = 0F;

            return;
        }

        aEntities = snap(resolve(CURVE_ENTITIES, hideEntities.get() ? 1F : 0F));
        aParticles = snap(resolve(CURVE_PARTICLES, hideParticles.get() ? 1F : 0F));
        aModelBlocks = snap(resolve(CURVE_MODEL_BLOCKS, hideModelBlocks.get() ? 1F : 0F));
        aFilter = snap(resolve(CURVE_FILTER, useFilter.get() ? 1F : 0F));
    }

    public static float particlesAmount()
    {
        refreshFlags();

        return aParticles;
    }

    public static float modelBlocksAmount()
    {
        refreshFlags();

        return aModelBlocks;
    }

    /** 这个实体被去掉的程度：0 = 正常，1 = 不渲染，中间 = 渐隐 */
    public static float entityAmount(Object entity)
    {
        refreshFlags();

        if (aEntities <= 0F && aFilter <= 0F)
        {
            return 0F;
        }

        String id = IdFilter.entityId(entity);

        /* BBS 自己的演员实体就是模型本体，不在这里隐藏 */
        if (id != null && id.startsWith("bbs:"))
        {
            return 0F;
        }

        float amount = aEntities;

        if (aFilter > 0F)
        {
            boolean match = IdFilter.matches(filterIds.get(), id);

            if (filterMode.get() == 0 ? match : !match)
            {
                amount = Math.max(amount, aFilter);
            }
        }

        return amount;
    }

    private static final java.util.Map<Class<?>, java.lang.reflect.Method> FLUSH = new java.util.HashMap<Class<?>, java.lang.reflect.Method>();
    private static final java.util.Map<Class<?>, java.lang.reflect.Field> INNER = new java.util.HashMap<Class<?>, java.lang.reflect.Field>();
    private static final java.util.Set<String> FLUSH_WARNED = new java.util.HashSet<String>();
    private static boolean flushFailed;

    /**
     * 把批处理里还没画的顶点立刻画出来（渐隐需要「画完这个实体」才能混合）。
     * Iris 会把缓冲包一层（BufferSourceWrapper），所以找不到 draw() 时逐层拆开里面的 bufferSource。
     * 失败返回 false（这一次不渐隐，但不会永久关闭）。
     */
    public static boolean flush(Object provider)
    {
        if (flushFailed || provider == null)
        {
            return false;
        }

        Object current = provider;

        try
        {
            for (int depth = 0; depth < 4 && current != null; depth++)
            {
                Class<?> type = current.getClass();
                java.lang.reflect.Method method = FLUSH.get(type);

                if (method == null && !FLUSH.containsKey(type))
                {
                    try
                    {
                        method = type.getMethod("method_22993");
                    }
                    catch (NoSuchMethodException e)
                    {
                        method = null;
                    }

                    FLUSH.put(type, method);
                }

                if (method != null)
                {
                    method.invoke(current);

                    return true;
                }

                java.lang.reflect.Field field = INNER.get(type);

                if (field == null && !INNER.containsKey(type))
                {
                    try
                    {
                        field = type.getDeclaredField("bufferSource");
                        field.setAccessible(true);
                    }
                    catch (NoSuchFieldException e)
                    {
                        field = null;
                    }

                    INNER.put(type, field);
                }

                if (field == null)
                {
                    break;
                }

                current = field.get(current);
            }
        }
        catch (Throwable e)
        {
            e.printStackTrace();

            return false;
        }

        if (FLUSH_WARNED.add(provider.getClass().getName()))
        {
            System.err.println("[bbs_invisible] entity fade skipped: vertex consumer " + provider.getClass().getName() + " has no draw()");
        }

        return false;
    }

    public static boolean canFlush()
    {
        return !flushFailed;
    }

    /**
     * 光影透明（实验）：光影包最后输出的画面 alpha 通常是 1，所以改用「深度」判断：
     * 画面里深度 < 1 的像素（画过东西）alpha = 1，其余（天空）alpha = 0。
     * 依赖光影渲染后 BBS 帧缓冲里还留着世界的深度，没有的话会得到全透明画面，所以默认关闭。
     */
    private static boolean shaderAlphaWarned;

    public static void applyShaderAlpha()
    {
        if (shaderAlpha == null || !shaderAlpha.get() || !isActive() || !BBSRendering.isIrisShadersEnabled())
        {
            return;
        }

        /* 天空「透明背景」曲线：0 = 保持原样（alpha 1），1 = 完全透明（alpha 0），中间渐变 */
        float sky = amount();

        if (sky <= 0F)
        {
            return;
        }

        try
        {
            net.minecraft.class_276 fb = net.minecraft.class_310.method_1551().method_1522();

            if (fb == null)
            {
                return;
            }

            int prevDraw = org.lwjgl.opengl.GL11.glGetInteger(36006);
            int prevRead = org.lwjgl.opengl.GL11.glGetInteger(36010);

            /* 先确认这张帧缓冲里真的有世界的深度；全是 1.0 说明光影管线没有留下深度，这时不能动 alpha，否则整张画面会变全透明 */
            org.lwjgl.opengl.GL30.glBindFramebuffer(36008, fb.field_1476);

            boolean hasDepth = false;
            float[] sample = new float[1];

            for (int i = 0; i < 64 && !hasDepth; i++)
            {
                int sx = (int) ((i % 8 + 0.5F) / 8F * fb.field_1482);
                int sy = (int) ((i / 8 + 0.5F) / 8F * fb.field_1481);

                org.lwjgl.opengl.GL11.glReadPixels(sx, sy, 1, 1, 6402, 5126, sample);

                hasDepth = sample[0] < 0.9999F;
            }

            org.lwjgl.opengl.GL30.glBindFramebuffer(36008, prevRead);

            if (!hasDepth)
            {
                if (!shaderAlphaWarned)
                {
                    shaderAlphaWarned = true;
                    System.err.println("[bbs_invisible] shader transparency skipped: no depth in the BBS framebuffer after the shader pipeline");
                }

                return;
            }

            int[] vp = new int[4];
            boolean scissor = org.lwjgl.opengl.GL11.glIsEnabled(3089);

            org.lwjgl.opengl.GL11.glGetIntegerv(2978, vp);
            org.lwjgl.opengl.GL30.glBindFramebuffer(36160, fb.field_1476);
            org.lwjgl.opengl.GL11.glViewport(0, 0, fb.field_1482, fb.field_1481);
            org.lwjgl.opengl.GL11.glDisable(3089);
            org.lwjgl.opengl.GL11.glColorMask(false, false, false, true);
            org.lwjgl.opengl.GL11.glClearColor(0F, 0F, 0F, 1F - sky);
            org.lwjgl.opengl.GL11.glClear(org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT);
            org.lwjgl.opengl.GL11.glColorMask(true, true, true, true);

            AlphaFix.run();

            if (scissor)
            {
                org.lwjgl.opengl.GL11.glEnable(3089);
            }

            org.lwjgl.opengl.GL11.glViewport(vp[0], vp[1], vp[2], vp[3]);
            org.lwjgl.opengl.GL30.glBindFramebuffer(36160, prevDraw);
        }
        catch (Throwable e)
        {
            e.printStackTrace();
        }
    }

    public static boolean isLatchedAlpha()
    {
        return latchedAlpha;
    }

    public static void latch()
    {
        latchedAlpha = isEnabled();

        if (latchedAlpha)
        {
            syncExportTexture(true);
        }
    }

    public static void syncExportTexture()
    {
        syncExportTexture(false);
    }

    /**
     * BBS 把世界画面拷进一张 RGB 贴图再导出，alpha 就是在这里丢的。
     * 开启透明背景时把它换成 RGBA，关闭时换回 RGB（与原版 BBS 行为一致）。
     */
    private static void syncExportTexture(boolean force)
    {
        Texture texture = BBSRendering.getTexture();

        if (texture == null)
        {
            return;
        }

        TextureFormat wanted = isEnabled() ? TextureFormat.RGBA_U8 : TextureFormat.RGB_U8;

        if (texture.getFormat() == wanted || (!force && BBSModClient.getVideoRecorder().isRecording()))
        {
            return;
        }

        texture.bind();
        texture.setFormat(wanted);
        texture.setSize(texture.width, texture.height);
        texture.unbind();
    }

    /** 生成带透明通道的 ffmpeg 参数（BBS 会再替换占位符） */
    public static String encoderArguments(boolean withAudio)
    {
        int format = videoFormat == null ? 0 : videoFormat.get();

        switch (format)
        {
            case 1:
                return IN + PNG;
            case 2:
                return (withAudio ? IN_AUDIO : IN) + WEBM + (withAudio ? "-c:a libopus -b:a 128k -shortest " : "") + "%NAME%.webm";
            case 3:
                return customArguments.get();
            default:
                return (withAudio ? IN_AUDIO : IN) + PRORES + (withAudio ? "-c:a pcm_s16le -shortest " : "") + "%NAME%.mov";
        }
    }

    /** 电影面板预览背景：棋盘格代替纯黑，一眼看出哪里是透明的 */
    public static boolean shouldDrawCheckerboard()
    {
        return isEnabled() && checkerboard.get();
    }

    public static void drawCheckerboard(Batcher2D batcher, float x1, float y1, float x2, float y2)
    {
        final int size = 24;

        batcher.box(x1, y1, x2, y2, 0xffb8b8b8);

        int row = 0;

        for (float y = y1; y < y2; y += size, row ++)
        {
            int col = 0;

            for (float x = x1; x < x2; x += size, col ++)
            {
                if (((row + col) & 1) == 1)
                {
                    batcher.box(x, y, Math.min(x + size, x2), Math.min(y + size, y2), 0xff8a8a8a);
                }
            }
        }
    }
}
