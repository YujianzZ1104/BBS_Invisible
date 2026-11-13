/*
 * BBS_Invisible - Copyright (C) 2026 遇见
 * Licensed under the GNU Lesser General Public License v3.0 or later.
 * See the LICENSE file in the project root.
 */
package bbs.invisible.render;

import java.util.IdentityHashMap;
import java.util.Map;
import net.minecraft.class_1921;

/** 判断一个渲染层是不是需要参与地形渐隐的地形层（solid / cutout / cutout_mipped / translucent） */
public final class TerrainFade
{
    private static final Map<Object, Integer> CACHE = new IdentityHashMap<Object, Integer>();

    private TerrainFade()
    {}

    /** 0 = 不参与，1 = 不透明地形层，2 = 半透明地形层 */
    private static int kind(class_1921 layer)
    {
        Integer cached = CACHE.get(layer);

        if (cached == null)
        {
            String name = String.valueOf(layer);
            int kind = 0;

            if (name.contains("translucent") && !name.contains("moving") && !name.contains("entity") && !name.contains("item") && !name.contains("armor"))
            {
                kind = 2;
            }
            else if ((name.contains("solid") || name.contains("cutout")) && !name.contains("entity") && !name.contains("item") && !name.contains("armor") && !name.contains("eyes") && !name.contains("beacon"))
            {
                kind = 1;
            }

            cached = kind;
            CACHE.put(layer, cached);
        }

        return cached;
    }

    public static boolean isFadeLayer(class_1921 layer)
    {
        return kind(layer) != 0;
    }

    public static boolean isOpaqueLayer(class_1921 layer)
    {
        return kind(layer) == 1;
    }
}
