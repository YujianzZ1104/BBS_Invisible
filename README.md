# BBS_Invisible

**BBSFS 扩展插件:透明背景**
**Transparent background addon for BBSFS**

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A)
![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4)
![Side](https://img.shields.io/badge/Side-Client-blue)
![License](https://img.shields.io/badge/License-MIT-green)

[中文](#中文) · [English](#english)

---

## 中文

BBS_Invisible 是 BBSFS 的扩展插件,让你在 BBSFS 里直接录制和截图带透明背景的画面,方便做后期合成。

- **去掉背景元素**:可以把天空、云层、雾、地形、实体、粒子和 BBS 模型方块从画面里拿掉,只留下你要的画面。
- **可以关键帧编辑**:以上每一项都有对应曲线,拖动时间线就能让它们逐渐变淡或出现。
- **适用环境**:Minecraft 1.20.1、Fabric、BBSFS 2.7,仅客户端需要安装。

### 安装与基本信息

**固定构建 / 验证版本**:以下是作者实际运行并取得日志的环境,其他版本组合没有验证过。

| 项目 | 内容 |
| --- | --- |
| 名称 / id | BBS_Invisible / bbs_invisible |
| 当前版本 | 1.1.0 |
| 作者 | 遇见 |
| 游戏版本 | Minecraft 1.20.1 |
| 加载器 | Fabric,Fabric Loader ≥ 0.15.0,Java 17+ |
| 前置 | BBSFS 2.7 |
| 安装位置 | 仅客户端,放进 mods 文件夹 |
| 录像依赖 | ffmpeg |

验证环境的具体版本:

| 项目 | 版本 |
| --- | --- |
| BBS_Invisible | 1.1.0 |
| Minecraft | 1.20.1 |
| Fabric Loader | ≥ 0.15.0 |
| Fabric API | 0.92.12+1.20.1 |
| BBSFS | 2.7-1.20.1 |
| Iris | 1.7.6+mc1.20.1(光影透明按此版本对照) |
| Sodium | 0.5.13+mc1.20.1 |
| Java | 17+ |

**安装步骤**

1. 安装 Fabric Loader 和 BBSFS。
2. 从 [Releases](../../releases) 下载最新的 `BBS_Invisible-x.x.x.jar`。
3. 放入 `.minecraft/mods` 文件夹。
4. 启动游戏,在 BBSFS 设置中找到「透明背景」。

### 透明截图与录像

截图输出带透明通道的 PNG;录像的格式由「导出」设置中的「格式」决定,编码依赖 ffmpeg,所以 BBSFS 里的 ffmpeg 要能正常使用。

开启透明背景后,影片编辑器预览里会显示棋盘格,方便看哪里是透明的。这些格式是否真的带透明通道,还要看后期软件能否读取。

### 设置项

设置面板里有「透明背景」和「透明导出」两个分类。在影片编辑器里,右键「录制视频」按钮,菜单末尾的「透明背景设置」可直接打开「透明背景」分类。

#### 「透明背景」分类

| 选项 | 默认 | 作用 |
| --- | --- | --- |
| 启用透明背景 | 关 | 总开关。开启后天空、云、雾被移除,截图保存为带 alpha 的 PNG,录制自动改用带透明通道的编码 |
| 仅在影片编辑器中生效 | 开 | 只有 BBSFS 影片面板(预览、截图、录制)里才去掉天空,平时玩游戏不受影响;关闭后游戏里也一直去掉 |
| 默认透明度 | 1 | 没有「透明背景」曲线时的天空透明程度:1 = 完全透明,0 = 正常天空 |
| 隐藏云层 | 开 | 没有云层曲线时的默认行为:开 = 不渲染云层 |
| 关闭雾 | 开 | 没有雾曲线时的默认行为:开 = 关闭雾 |
| 预览显示棋盘格 | 开 | 影片面板预览里用棋盘格代替黑色背景 |
| 修复树叶变半透明 | 开 | 树叶等镂空方块远处会因贴图缩小而变半透明,开启后把不透明方块的 alpha 补满(水和玻璃不受影响) |
| 隐藏地形 | 关 | 没有地形曲线时的默认行为:只留实体、粒子和 BBS 模型 |
| 同时隐藏方块实体 | 开 | 隐藏地形时,是否也隐藏箱子、告示牌等方块实体(BBS 模型方块不受影响) |
| 隐藏实体 / 隐藏粒子 / 隐藏 BBS 模型方块 | 关 | 没有对应曲线时的默认行为;BBS 自己的演员实体不受影响 |
| 启用 ID 过滤 / 过滤模式 / 过滤 ID | 关 / 0 / 空 | 见「实体、粒子、模型方块与 ID 过滤」 |
| 光影透明(实验) | 关 | 见「光影透明与已知限制」 |

#### 「透明导出」分类

| 选项 | 默认 | 作用 |
| --- | --- | --- |
| 视频格式(0-3) | 0 | 0 = ProRes 4444,1 = PNG 序列,2 = WebM VP9,3 = 自定义 |
| 自定义编码器参数 | 内置模板 | 格式为 3 时使用的 FFmpeg 参数;输入必须是 `-pix_fmt bgra`,输出必须是支持透明的编码,占位符与 BBSFS 视频设置相同:`%WIDTH%` `%HEIGHT%` `%FPS%` `%FILTERS%` |

视频格式说明:

| 格式值 | 输出 | 适合 |
| --- | --- | --- |
| 0(默认) | ProRes 4444 的 .mov | Premiere、DaVinci Resolve、After Effects 等专业后期,画质最好,文件很大 |
| 1 | PNG 序列(一帧一张) | 最通用、最稳,也最占空间 |
| 2 | WebM(VP9,带透明) | 文件较小,部分剪辑软件不支持 |
| 3 | 自定义 | 在「参数」里自己写 ffmpeg 参数 |

### 曲线与「透明背景」剪辑

所有透明效果都能用关键帧控制:曲线值 0 = 正常显示,1 = 完全去掉,中间是渐隐。拖动时间线,背景就会慢慢变透明。控制方式有两种,可以根据个人需求或喜好任选其一。

**选择一:在「曲线」剪辑里添加**

在 BBSFS 原有的曲线剪辑里点「添加曲线」,「选择曲线键值」列表里会多出下面 8 条「透明背景」曲线。

**选择二:使用独立的「透明背景」剪辑**

在影片编辑器的添加剪辑菜单里选「透明背景」,创建时就带好这 8 条曲线。它和曲线剪辑用的是同一套曲线 id,效果一致,但有两点不同:

- 「添加曲线」只能选透明背景自己的曲线,不会出现天气、太阳旋转、光影曲线。
- 不需要的曲线可以右键「移除曲线」。

两种方式不要在同一段时间里同时控制同一项,否则会互相覆盖。

**8 条曲线**

编辑时,曲线标题在时间线和选择列表里都显示简体中文名称,不会显示原始 id。

| 曲线 | 控制什么 | 渐隐方式 |
| --- | --- | --- |
| 透明背景 | 天空的透明程度 | 天空像素的 alpha = 1 − 曲线值 |
| 透明背景:云层 | 云层 | 画云前后拷贝并混合画面,不依赖 Sodium 等渲染器 |
| 透明背景:雾 | 雾 | 调整雾的起止距离 |
| 透明背景:地形 | 地形方块(含水等半透明层) | 每个地形层画完后混合画面;BBS 模型不会跟着变淡 |
| 透明背景:实体 | 实体 | 每个实体画完后混合画面 |
| 透明背景:粒子 | 粒子 | 整个粒子渲染前后混合 |
| 透明背景:BBS 模型方块 | BBS 模型方块 | 每个模型方块的前后混合 |
| 透明背景:ID 过滤 | ID 过滤是否生效 | 被过滤到的实体按曲线值渐隐 |

### 实体、粒子、模型方块与 ID 过滤

录制给后期用的素材时,往往只想留下一部分内容。这些选项让你按类别把东西从画面里拿掉,都能用曲线渐隐。

- **隐藏实体**:不渲染生物、掉落物、盔甲架等实体。BBS 自己的演员实体(命名空间为 `bbs`)不受影响。
- **隐藏粒子**:整体不渲染粒子。
- **隐藏 BBS 模型方块**:不渲染 BBS 的模型方块。
- **ID 过滤**:按实体 ID 筛选,只对实体生效,粒子只能整体隐藏。

**ID 过滤怎么填**

- 在「过滤 ID」里填实体 ID,用逗号、分号或空格分隔,例如 `minecraft:zombie, creeper, mymod:*`。
- 不写命名空间时默认按 `minecraft` 处理,所以 `creeper` 等于 `minecraft:creeper`。
- 写成 `命名空间:*` 可以匹配整个模组的实体。
- 「过滤模式」填 0 = 隐藏列表里的实体,填 1 = 只显示列表里的实体。
- 过滤是否生效由「启用 ID 过滤」或「透明背景:ID 过滤」曲线决定,所以可以按时间点开关。

**渐隐效果的范围**

- 渐隐是把「画这个物体前的画面」和「画完后的画面」按曲线值混合得到的,不是真正的物体透明度。
- 渲染器被光影包接管时(见下一节),渐隐会变成显示 / 隐藏二选一:曲线值 0.5 以下显示,0.5 及以上隐藏。
- 被地形挡在后面的实体,在地形渐隐时仍然会被地形的深度遮住。

### 光影透明与已知限制

#### 光影透明(实验)

开启光影包时,光影最终输出的画面 alpha 通常是 1,所以普通的透明背景去不掉天空。开启设置里的「光影透明(实验)」后,会在世界和光影合成画完、原版为画手而清深度之前,用深度缓冲判断哪里是天空:画过东西的像素 alpha = 1,没画过的像素按「透明背景」曲线变透明。

- 天空透明度跟随「透明背景」曲线,曲线值 0 时保持原样。
- 读不到深度时会自动跳过,并在日志里写一行提示,不会把整张画面变全透明。
- 依赖 Iris(已用 Iris 1.7.6 对照过代码)。

#### 已知限制

- **光影包开启时不支持渐隐**:实体、粒子、模型方块、ID 过滤、云、地形的曲线值会被吸附成显示 / 隐藏二选一(以 0.5 为界),因为光影的延迟光照和拷贝混合会冲突。天空的渐变不受影响。
- **光影下模型周围可能有光晕**:光影的泛光、雾、抗锯齿会把颜色晕到模型周围。建议在光影设置里关掉泛光和雾。
- **开启 TAA 时边缘可能抖动**:建议关掉 TAA 或改用 FXAA。
- **光影下模型方块**:BBS 在开光影时会用自己的路径画模型方块,「隐藏 BBS 模型方块」可能对光影下的画面无效。
- **渐隐不是真透明**:是拷贝混合得到的视觉效果,性能开销不大,但与半透明实体、粒子重叠时边缘可能有差异。
- **渲染器调用名依赖**:部分渲染调用用的是 1.20.1 的 intermediary 名称,版本变化后可能失效,失效时会静默跳过,不会崩溃。

### 兼容性与反馈

- **Sodium / Embeddium**:云层渐隐不依赖原版云渲染,设计上预期与它们兼容(未逐一验证)。
- **Iris**:已针对 Iris 包装渲染缓冲的情况做了处理,光影透明见上一节。

本扩展插件目前只在作者自己的环境里反复试过,渲染相关的改动很容易和其他渲染类模组产生差异。如果遇到问题,请到 [Issues](../../issues) 反馈,并把游戏日志里带 `bbs_invisible` 的几行一起附上。

### 目录结构

```
src/bbs/invisible/          入口类 BbsInvisibleAddon、BbsInvisibleClientAddon
src/bbs/invisible/render/   透明背景核心:Transparency、CloudFade、AlphaFix、TerrainFade、IdFilter
src/bbs/invisible/clip/     「透明背景」剪辑:TransparentClip、UITransparentClip
src/bbs/invisible/mixin/    Mixin(含 BBSFS 界面与渲染的注入)
res/                        fabric.mod.json、mixin 配置、语言文件(zh_cn / zh_tw / en_us)、图标
```

### 构建

本仓库只包含源码,不含构建脚本。编译需要:

- BBSFS 2.7 的 jar
- Minecraft 1.20.1 / Fabric 的映射环境

Mixin 使用 intermediary 名称,未使用 refmap。

### 许可证

MIT,详见 [LICENSE](LICENSE)。

Copyright (C) 2026 遇见

---

## English

BBS_Invisible is an addon for BBSFS that lets you record video and take screenshots with a transparent background directly inside BBSFS, making compositing in post-production easy.

- **Remove background elements**: take the sky, clouds, fog, terrain, entities, particles and BBS model blocks out of the frame, leaving only what you want.
- **Keyframe-editable**: every item above has its own curve, so you can fade them in or out gradually along the timeline.
- **Environment**: Minecraft 1.20.1, Fabric, BBSFS 2.7. Client-side only.

### Installation and basic info

**Pinned / verified versions**: this is the environment the author actually ran and collected logs from. Other version combinations have not been verified.

| Item | Details |
| --- | --- |
| Name / id | BBS_Invisible / bbs_invisible |
| Current version | 1.1.0 |
| Author | YujianzZ |
| Game version | Minecraft 1.20.1 |
| Loader | Fabric, Fabric Loader ≥ 0.15.0, Java 17+ |
| Required | BBSFS 2.7 |
| Install location | Client only, put in the mods folder |
| Recording dependency | ffmpeg |

Exact versions in the verified environment:

| Item | Version |
| --- | --- |
| BBS_Invisible | 1.1.0 |
| Minecraft | 1.20.1 |
| Fabric Loader | ≥ 0.15.0 |
| Fabric API | 0.92.12+1.20.1 |
| BBSFS | 2.7-1.20.1 |
| Iris | 1.7.6+mc1.20.1 (shader transparency was checked against this version) |
| Sodium | 0.5.13+mc1.20.1 |
| Java | 17+ |

**How to install**

1. Install Fabric Loader and BBSFS.
2. Download the latest `BBS_Invisible-x.x.x.jar` from [Releases](../../releases).
3. Put it into your `.minecraft/mods` folder.
4. Launch the game and find "Transparent Background" in the BBSFS settings.

### Transparent screenshots and recording

Screenshots are saved as PNG with an alpha channel. The video format is decided by the "Format" option in the export settings. Encoding depends on ffmpeg, so the ffmpeg set up in BBSFS must work.

With transparent background on, the film editor preview shows a checkerboard so you can see what is transparent. Whether a format really carries alpha also depends on whether your editing software can read it.

### Settings

The settings panel has two categories: "Transparent Background" and "Transparent Export". In the film editor, right-click the "Record video" button and choose "Transparent Background Settings" at the end of the menu to open the "Transparent Background" category directly.

#### "Transparent Background" category

| Option | Default | Effect |
| --- | --- | --- |
| Enable transparent background | Off | Master switch. When on, sky, clouds and fog are removed, screenshots are saved as PNG with alpha, and recording automatically uses an encoder with an alpha channel |
| Only in film editor | On | The sky is removed only inside the BBSFS film panel (preview, screenshot, recording); normal gameplay is unaffected. When off, it is removed in-game as well |
| Default opacity | 1 | Sky transparency when there is no "Transparent Background" curve: 1 = fully transparent, 0 = normal sky |
| Hide clouds | On | Default when there is no cloud curve: on = clouds are not rendered |
| Disable fog | On | Default when there is no fog curve: on = fog is disabled |
| Show checkerboard in preview | On | Uses a checkerboard instead of a black background in the film panel preview |
| Fix semi-transparent leaves | On | Cutout blocks such as leaves turn semi-transparent at a distance because textures shrink. When on, the alpha of opaque blocks is filled to full (water and glass are unaffected) |
| Hide terrain | Off | Default when there is no terrain curve: only entities, particles and BBS models remain |
| Also hide block entities | On | When hiding terrain, whether to also hide chests, signs and other block entities (BBS model blocks are unaffected) |
| Hide entities / particles / BBS model blocks | Off | Default when there is no matching curve; BBS's own actor entities are unaffected |
| Enable ID filter / filter mode / filter IDs | Off / 0 / empty | See "Entities, particles, model blocks and ID filter" |
| Shader transparency (experimental) | Off | See "Shader transparency and known limitations" |

#### "Transparent Export" category

| Option | Default | Effect |
| --- | --- | --- |
| Video format (0-3) | 0 | 0 = ProRes 4444, 1 = PNG sequence, 2 = WebM VP9, 3 = custom |
| Custom encoder arguments | Built-in template | FFmpeg arguments used when the format is 3. Input must be `-pix_fmt bgra` and output must be an encoding that supports transparency. Placeholders are the same as BBSFS video settings: `%WIDTH%` `%HEIGHT%` `%FPS%` `%FILTERS%` |

Video formats:

| Value | Output | Good for |
| --- | --- | --- |
| 0 (default) | ProRes 4444 `.mov` | Professional post tools such as Premiere, DaVinci Resolve and After Effects; best quality, very large files |
| 1 | PNG sequence (one image per frame) | Most compatible and most reliable, but takes the most space |
| 2 | WebM (VP9 with alpha) | Smaller files; not supported by some editors |
| 3 | Custom | Write your own ffmpeg arguments in "Arguments" |

### Curves and the "Transparent Background" clip

Every transparency effect can be keyframed: curve value 0 = shown normally, 1 = fully removed, with a fade in between. Drag along the timeline and the background gradually becomes transparent. There are two ways to control it; pick whichever suits you.

**Option 1: add them in a "Curve" clip**

In BBSFS's existing curve clip, click "Add curve". The "Select curve key" list gains the 8 "Transparent Background" curves below.

**Option 2: use the standalone "Transparent Background" clip**

Choose "Transparent Background" in the film editor's add-clip menu; it is created with these 8 curves already in place. It uses the same curve ids as the curve clip, so the result is identical, with two differences:

- "Add curve" only offers the Transparent Background curves, not weather, sun rotation or shader curves.
- Curves you do not need can be removed with right-click "Remove curve".

Do not control the same item with both methods during the same time range, or they will override each other.

**The 8 curves**

While editing, curve titles are shown in Simplified Chinese in both the timeline and the selection list, not as raw ids.

| Curve | Controls | Fade method |
| --- | --- | --- |
| Transparent Background | Sky transparency | Sky pixel alpha = 1 − curve value |
| Transparent Background: Clouds | Clouds | Copies and blends the frame before and after drawing clouds; does not depend on Sodium or other renderers |
| Transparent Background: Fog | Fog | Adjusts the start and end distance of fog |
| Transparent Background: Terrain | Terrain blocks (including semi-transparent layers such as water) | Blends the frame after each terrain layer is drawn; BBS models do not fade with it |
| Transparent Background: Entities | Entities | Blends the frame after each entity is drawn |
| Transparent Background: Particles | Particles | Blends before and after the whole particle pass |
| Transparent Background: BBS Model Blocks | BBS model blocks | Blends before and after each model block |
| Transparent Background: ID Filter | Whether the ID filter is active | Filtered entities fade by the curve value |

### Entities, particles, model blocks and ID filter

When recording footage for post-production you often want to keep only part of the scene. These options let you remove things from the frame by category, and all of them can fade with curves.

- **Hide entities**: do not render mobs, dropped items, armor stands and other entities. BBS's own actor entities (namespace `bbs`) are unaffected.
- **Hide particles**: do not render particles at all.
- **Hide BBS model blocks**: do not render BBS model blocks.
- **ID filter**: filter by entity ID. It only affects entities; particles can only be hidden as a whole.

**How to fill in the ID filter**

- Enter entity IDs in "Filter IDs", separated by commas, semicolons or spaces, for example `minecraft:zombie, creeper, mymod:*`.
- Without a namespace, `minecraft` is assumed, so `creeper` equals `minecraft:creeper`.
- Write `namespace:*` to match every entity from a mod.
- "Filter mode": 0 = hide the entities in the list, 1 = show only the entities in the list.
- Whether filtering is active is decided by "Enable ID filter" or the "Transparent Background: ID Filter" curve, so it can be switched on and off over time.

**Scope of the fade effect**

- A fade blends "the frame before this object is drawn" with "the frame after it is drawn" by the curve value. It is not true object transparency.
- When a shader pack takes over the renderer (see next section), fades become show / hide only: shown below 0.5, hidden at 0.5 and above.
- Entities hidden behind terrain are still occluded by the terrain's depth while the terrain fades.

### Shader transparency and known limitations

#### Shader transparency (experimental)

With a shader pack on, the final alpha output by the shader is usually 1, so the normal transparent background cannot remove the sky. After enabling "Shader transparency (experimental)", the mod uses the depth buffer to decide where the sky is, after the world and shader compositing is drawn and before vanilla clears depth for the hand: pixels that were drawn get alpha = 1, and pixels that were not drawn become transparent according to the "Transparent Background" curve.

- Sky transparency follows the "Transparent Background" curve; at curve value 0 it stays unchanged.
- If depth cannot be read, the step is skipped automatically and one line is written to the log; the whole frame will not turn fully transparent.
- Depends on Iris (the code was checked against Iris 1.7.6).

#### Known limitations

- **No gradual fades with shader packs on**: curve values for entities, particles, model blocks, ID filter, clouds and terrain snap to show / hide (threshold 0.5), because shader deferred lighting conflicts with copy-and-blend. The sky gradient is unaffected.
- **Halos around models under shaders**: bloom, fog and anti-aliasing in shaders can bleed color around models. Turning off bloom and fog in the shader settings is recommended.
- **Edges may jitter with TAA**: turn TAA off or use FXAA.
- **Model blocks under shaders**: BBS draws model blocks through its own path when shaders are on, so "Hide BBS model blocks" may have no effect on shader frames.
- **Fades are not true transparency**: they are a visual effect from copy-and-blend. The performance cost is small, but edges may differ where semi-transparent entities and particles overlap.
- **Renderer call names**: some render calls use 1.20.1 intermediary names and may break on other versions; when that happens they are skipped silently without crashing.

### Compatibility and feedback

- **Sodium / Embeddium**: cloud fading does not rely on vanilla cloud rendering and is expected to be compatible by design (not verified one by one).
- **Iris**: Iris's wrapped render buffers are handled; see the previous section for shader transparency.

This addon has only been tested repeatedly in the author's own environment, and rendering changes can easily differ with other rendering mods. If you run into problems, please report them in [Issues](../../issues) and attach the lines containing `bbs_invisible` from your game log.

### Project layout

```
src/bbs/invisible/          Entrypoints: BbsInvisibleAddon, BbsInvisibleClientAddon
src/bbs/invisible/render/   Core: Transparency, CloudFade, AlphaFix, TerrainFade, IdFilter
src/bbs/invisible/clip/     Transparent clip: TransparentClip, UITransparentClip
src/bbs/invisible/mixin/    Mixins (injections into BBSFS UI and rendering)
res/                        fabric.mod.json, mixin config, lang files (zh_cn / zh_tw / en_us), icon
```

### Building

This repository contains source code only, without build scripts. To compile you need:

- The BBSFS 2.7 jar
- A Minecraft 1.20.1 / Fabric mappings environment

Mixins use intermediary names; no refmap is used.

### License

MIT. See [LICENSE](LICENSE).

Copyright (C) 2026 YujianzZ
