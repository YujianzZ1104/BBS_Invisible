/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL14;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * 与渲染器无关的云层渐隐：画云之前先把当前画面拷贝一份，画完云之后
 * 按「去除程度」把拷贝混回去（结果 = 云画面 * (1 - a) + 原画面 * a）。
 * 不依赖原版 / Sodium / Embeddium 自己怎么画云，所以任何情况下都能平滑过渡。
 */
public final class CloudFade
{
    private static final String VERTEX = "#version 150\n"
        + "void main() {\n"
        + "    vec2 p = vec2(float((gl_VertexID << 1) & 2), float(gl_VertexID & 2));\n"
        + "    gl_Position = vec4(p * 2.0 - 1.0, 0.0, 1.0);\n"
        + "}\n";
    private static final String FRAGMENT = "#version 150\n"
        + "uniform sampler2D Snapshot;\n"
        + "out vec4 fragColor;\n"
        + "void main() {\n"
        + "    fragColor = texelFetch(Snapshot, ivec2(gl_FragCoord.xy), 0);\n"
        + "}\n";

    private static boolean ready;
    private static boolean failed;
    private static boolean captured;
    private static int program;
    private static int vao;
    private static int texture;
    private static int texW;
    private static int texH;

    private static String owner;
    private static float pending;

    private CloudFade()
    {}

    /** 带「持有者」的快照：begin 和 endFor 用同一个持有者配对，避免互相串扰 */
    public static void beginFor(String who, float amount)
    {
        begin();

        owner = captured ? who : null;
        pending = amount;
    }

    public static boolean holds(String who)
    {
        return captured && who.equals(owner);
    }

    public static void endFor(String who)
    {
        if (holds(who))
        {
            owner = null;
            end(pending);
        }
    }

    /** 画云之前调用：拷贝当前画面 */
    public static void begin()
    {
        captured = false;

        if (failed)
        {
            return;
        }

        try
        {
            if (!ready)
            {
                init();
            }

            if (failed)
            {
                return;
            }

            int[] vp = new int[4];

            GL11.glGetIntegerv(2978, vp);                     /* GL_VIEWPORT */

            int w = vp[0] + vp[2];
            int h = vp[1] + vp[3];

            if (vp[2] <= 0 || vp[3] <= 0)
            {
                return;
            }

            int prevActive = GL11.glGetInteger(34016);
            GL13.glActiveTexture(33984);

            int prevTex = GL11.glGetInteger(32873);

            GL11.glBindTexture(3553, texture);

            if (w != texW || h != texH)
            {
                GL11.glTexImage2D(3553, 0, 32856, w, h, 0, 6408, 5121, 0L);
                texW = w;
                texH = h;
            }

            GL11.glCopyTexSubImage2D(3553, 0, vp[0], vp[1], vp[0], vp[1], vp[2], vp[3]);

            GL11.glBindTexture(3553, prevTex);
            GL13.glActiveTexture(prevActive);

            captured = true;
        }
        catch (Throwable e)
        {
            failed = true;
            e.printStackTrace();
        }
    }

    /** 画云之后调用：把拷贝按 amount 混回去 */
    public static void end(float amount)
    {
        if (!captured || failed)
        {
            return;
        }

        captured = false;

        try
        {
            int prevProgram = GL11.glGetInteger(35725);
            int prevVao = GL11.glGetInteger(34229);
            int prevActive = GL11.glGetInteger(34016);
            int prevSrcRgb = GL11.glGetInteger(32969);
            int prevDstRgb = GL11.glGetInteger(32968);
            int prevSrcA = GL11.glGetInteger(32971);
            int prevDstA = GL11.glGetInteger(32970);
            boolean prevDepthMask = GL11.glGetBoolean(2930);
            boolean depthTest = GL11.glIsEnabled(2929);
            boolean blend = GL11.glIsEnabled(3042);
            boolean cull = GL11.glIsEnabled(2884);
            boolean scissor = GL11.glIsEnabled(3089);

            GL13.glActiveTexture(33984);

            int prevTex = GL11.glGetInteger(32873);

            GL11.glBindTexture(3553, texture);
            GL20.glUseProgram(program);
            GL20.glUniform1i(GL20.glGetUniformLocation(program, "Snapshot"), 0);
            GL30.glBindVertexArray(vao);

            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glDisable(2884);
            GL11.glDisable(3089);
            GL11.glEnable(3042);
            GL14.glBlendColor(0F, 0F, 0F, amount);
            GL14.glBlendFuncSeparate(32771, 32772, 32771, 32772);   /* CONSTANT_ALPHA, ONE_MINUS_CONSTANT_ALPHA */

            GL11.glDrawArrays(4, 0, 3);

            GL14.glBlendFuncSeparate(prevSrcRgb, prevDstRgb, prevSrcA, prevDstA);
            set(3042, blend);
            set(2929, depthTest);
            set(2884, cull);
            set(3089, scissor);
            GL11.glDepthMask(prevDepthMask);
            GL30.glBindVertexArray(prevVao);
            GL20.glUseProgram(prevProgram);
            GL11.glBindTexture(3553, prevTex);
            GL13.glActiveTexture(prevActive);
        }
        catch (Throwable e)
        {
            failed = true;
            e.printStackTrace();
        }
    }

    private static void set(int cap, boolean enabled)
    {
        if (enabled)
        {
            GL11.glEnable(cap);
        }
        else
        {
            GL11.glDisable(cap);
        }
    }

    private static void init()
    {
        ready = true;

        int vs = compile(35633, VERTEX);
        int fs = compile(35632, FRAGMENT);

        if (vs == 0 || fs == 0)
        {
            failed = true;

            return;
        }

        program = GL20.glCreateProgram();

        GL20.glAttachShader(program, vs);
        GL20.glAttachShader(program, fs);
        GL20.glLinkProgram(program);
        GL20.glDeleteShader(vs);
        GL20.glDeleteShader(fs);

        if (GL20.glGetProgrami(program, 35714) == 0)
        {
            System.err.println("[bbs_invisible] cloud fade program failed to link");
            failed = true;

            return;
        }

        vao = GL30.glGenVertexArrays();
        texture = GL11.glGenTextures();

        int prevActive = GL11.glGetInteger(34016);
        GL13.glActiveTexture(33984);

        int prevTex = GL11.glGetInteger(32873);

        GL11.glBindTexture(3553, texture);
        GL11.glTexParameteri(3553, 10241, 9728);
        GL11.glTexParameteri(3553, 10240, 9728);
        GL11.glBindTexture(3553, prevTex);
        GL13.glActiveTexture(prevActive);
    }

    private static int compile(int type, String source)
    {
        int shader = GL20.glCreateShader(type);

        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, 35713) == 0)
        {
            System.err.println("[bbs_invisible] cloud fade shader failed to compile");

            return 0;
        }

        return shader;
    }
}
