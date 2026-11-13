/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.render;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体 ID 过滤：把过滤列表（逗号 / 空格 / 分号分隔）和实体的注册 ID 对比。
 * 支持 minecraft:zombie、zombie（默认 minecraft）、modid:*（整个命名空间）。
 * 所有 Minecraft 调用都走反射并捕获异常，取不到 ID 时当作「不匹配」。
 */
public final class IdFilter
{
    private static boolean failed;
    private static Object registry;
    private static Method getType;
    private static Method getId;

    private static String lastRaw;
    private static List<String> lastList = new ArrayList<String>();

    private IdFilter()
    {}

    public static String entityId(Object entity)
    {
        if (failed || entity == null)
        {
            return null;
        }

        try
        {
            if (registry == null)
            {
                Class<?> registries = Class.forName("net.minecraft.class_7923");
                Field field = registries.getDeclaredField("field_41177");

                field.setAccessible(true);
                registry = field.get(null);

                for (Method m : registry.getClass().getMethods())
                {
                    if (m.getName().equals("method_10221") && m.getParameterCount() == 1 && !m.isBridge())
                    {
                        getId = m;
                        break;
                    }
                }

                if (getId == null)
                {
                    for (Method m : registry.getClass().getMethods())
                    {
                        if (m.getName().equals("method_10221") && m.getParameterCount() == 1)
                        {
                            getId = m;
                            break;
                        }
                    }
                }
            }

            if (getType == null)
            {
                getType = Class.forName("net.minecraft.class_1297").getMethod("method_5864");
            }

            Object type = getType.invoke(entity);

            return String.valueOf(getId.invoke(registry, type));
        }
        catch (Throwable e)
        {
            failed = true;
            e.printStackTrace();

            return null;
        }
    }

    public static boolean matches(String raw, String id)
    {
        if (id == null)
        {
            return false;
        }

        if (raw == null)
        {
            raw = "";
        }

        if (!raw.equals(lastRaw))
        {
            lastRaw = raw;
            lastList = new ArrayList<String>();

            for (String part : raw.split("[,;\\s]+"))
            {
                part = part.trim().toLowerCase();

                if (part.isEmpty())
                {
                    continue;
                }

                if (part.indexOf(':') < 0)
                {
                    part = "minecraft:" + part;
                }

                lastList.add(part);
            }
        }

        for (String entry : lastList)
        {
            if (entry.endsWith(":*") ? id.startsWith(entry.substring(0, entry.length() - 1)) : entry.equals(id))
            {
                return true;
            }
        }

        return false;
    }
}
