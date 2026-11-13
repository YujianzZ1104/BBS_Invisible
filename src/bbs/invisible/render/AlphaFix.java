/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.render;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

/**
 * 把「已经画了东西」的像素的 alpha 补成 1。
 *
 * 带 mipmap 的镂空贴图（树叶等）在远处 alpha 会被平均成小数，原版不看 alpha 所以没问题，
 * 但透明背景导出时它们就会变成半透明。这里用一个全屏三角形 + 深度测试
 * （只在深度 < 1 的地方通过）把 alpha 写成 1，不改颜色，也不改深度。
 * 只用 LWJGL 的 GL 调用，运行前后还原所有改动的 GL 状态。
 */
public final class AlphaFix
{
    private static final String VERTEX = "#version 150\n"
        + "void main() {\n"
        + "    vec2 p = vec2(float((gl_VertexID << 1) & 2), float(gl_VertexID & 2));\n"
        + "    gl_Position = vec4(p * 2.0 - 1.0, 1.0, 1.0);\n"
        + "}\n";
    private static final String FRAGMENT = "#version 150\n"
        + "out vec4 fragColor;\n"
        + "void main() {\n"
        + "    fragColor = vec4(0.0, 0.0, 0.0, 1.0);\n"
        + "}\n";

    private static boolean ready;
    private static boolean failed;
    private static int program;
    private static int vao;

    private AlphaFix()
    {}

    public static void run()
    {
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

            int prevProgram = GL11.glGetInteger(35725);      /* GL_CURRENT_PROGRAM */
            int prevVao = GL11.glGetInteger(34229);          /* GL_VERTEX_ARRAY_BINDING */
            int prevDepthFunc = GL11.glGetInteger(2932);     /* GL_DEPTH_FUNC */
            boolean prevDepthMask = GL11.glGetBoolean(2930); /* GL_DEPTH_WRITEMASK */
            boolean depthTest = GL11.glIsEnabled(2929);
            boolean blend = GL11.glIsEnabled(3042);
            boolean cull = GL11.glIsEnabled(2884);
            boolean scissor = GL11.glIsEnabled(3089);

            GL20.glUseProgram(program);
            GL30.glBindVertexArray(vao);

            GL11.glEnable(2929);
            GL11.glDepthFunc(516);                            /* GL_GREATER：只在已有几何的地方通过 */
            GL11.glDepthMask(false);
            GL11.glDisable(3042);
            GL11.glDisable(2884);
            GL11.glDisable(3089);
            GL11.glColorMask(false, false, false, true);

            GL11.glDrawArrays(4, 0, 3);

            GL11.glColorMask(true, true, true, true);
            GL11.glDepthMask(prevDepthMask);
            GL11.glDepthFunc(prevDepthFunc);
            set(2929, depthTest);
            set(3042, blend);
            set(2884, cull);
            set(3089, scissor);
            GL30.glBindVertexArray(prevVao);
            GL20.glUseProgram(prevProgram);
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
            System.err.println("[bbs_invisible] alpha fix program failed to link");
            failed = true;

            return;
        }

        vao = GL30.glGenVertexArrays();
    }

    private static int compile(int type, String source)
    {
        int shader = GL20.glCreateShader(type);

        GL20.glShaderSource(shader, source);
        GL20.glCompileShader(shader);

        if (GL20.glGetShaderi(shader, 35713) == 0)
        {
            System.err.println("[bbs_invisible] alpha fix shader failed to compile");

            return 0;
        }

        return shader;
    }
}
