# BBS_Invisible

**BBSFS 扩展插件:透明背景**
**Transparent background addon for BBSFS**

![Minecraft](https://img.shields.io/badge/Minecraft-1.20.1-62B47A)
![Fabric](https://img.shields.io/badge/Loader-Fabric-DBD0B4)
![Side](https://img.shields.io/badge/Side-Client-blue)
![License](https://img.shields.io/badge/License-LGPL--3.0--or--later-orange)

[中文](#中文) · [English](#english)

---

## 中文

BBS_Invisible 是 BBSFS 的客户端扩展插件,在 BBSFS 设置里新增「透明背景」功能,方便把角色、场景单独抠出来做后期合成。

### 功能

- 在 BBSFS 设置里新增「透明背景」选项
- 去掉天空、云和雾
- 截图输出带 alpha 通道的 PNG
- 录像输出带透明通道的视频,支持 ProRes 4444 / PNG 序列 / WebM
- 新增「透明背景」剪辑(Clip),可在时间线中使用

### 运行环境

| 项目 | 要求 |
| --- | --- |
| Minecraft | 1.20.1 |
| 加载器 | Fabric Loader ≥ 0.15.0 |
| Java | 17 或更高 |
| 前置 | BBSFS |
| 运行端 | 仅客户端 |

### 安装

1. 安装 Fabric Loader 和 BBSFS。
2. 从 [Releases](../../releases) 下载最新的 `BBS_Invisible-x.x.x.jar`。
3. 放入 `.minecraft/mods` 文件夹。
4. 启动游戏,在 BBSFS 设置中找到「透明背景」。

### 目录结构

```
src/bbs/invisible/          入口类 BbsInvisibleAddon、BbsInvisibleClientAddon
src/bbs/invisible/render/   透明背景核心:Transparency、CloudFade、AlphaFix、TerrainFade、IdFilter
src/bbs/invisible/clip/     「透明背景」剪辑:TransparentClip、UITransparentClip
src/bbs/invisible/mixin/    Mixin(含 BBS 界面与渲染的注入)
res/                        fabric.mod.json、mixin 配置、语言文件(zh_cn / zh_tw / en_us)、图标
```

### 构建

本仓库只包含源码,不含构建脚本。编译需要:

- BBS 2.7 的 jar
- Minecraft 1.20.1 / Fabric 的映射环境

Mixin 使用 intermediary 名称,未使用 refmap。

### 许可证

LGPL-3.0-or-later,详见 [LICENSE](LICENSE),并附 [COPYING](COPYING)(GPL-3.0 全文)。

Copyright (C) 2026 遇见

---

## English

BBS_Invisible is a client-side addon for BBSFS that adds a **Transparent Background** option to the BBSFS settings, making it easy to isolate characters and scenes for compositing.

### Features

- Adds a "Transparent Background" option in the BBSFS settings
- Removes sky, clouds and fog
- Exports PNG screenshots with an alpha channel
- Records video with an alpha channel: ProRes 4444 / PNG sequence / WebM
- Adds a "Transparent Background" clip for the timeline

### Requirements

| Item | Requirement |
| --- | --- |
| Minecraft | 1.20.1 |
| Loader | Fabric Loader ≥ 0.15.0 |
| Java | 17 or newer |
| Dependency | BBSFS |
| Side | Client-only |

### Installation

1. Install Fabric Loader and BBSFS.
2. Download the latest `BBS_Invisible-x.x.x.jar` from [Releases](../../releases).
3. Put it into your `.minecraft/mods` folder.
4. Launch the game and find "Transparent Background" in the BBSFS settings.

### Project layout

```
src/bbs/invisible/          Entrypoints: BbsInvisibleAddon, BbsInvisibleClientAddon
src/bbs/invisible/render/   Core: Transparency, CloudFade, AlphaFix, TerrainFade, IdFilter
src/bbs/invisible/clip/     Transparent clip: TransparentClip, UITransparentClip
src/bbs/invisible/mixin/    Mixins (injections into BBS UI and rendering)
res/                        fabric.mod.json, mixin config, lang files (zh_cn / zh_tw / en_us), icon
```

### Building

This repository contains source code only, without build scripts. To compile you need:

- The BBS 2.7 jar
- A Minecraft 1.20.1 / Fabric mappings environment

Mixins use intermediary names; no refmap is used.

### License

LGPL-3.0-or-later. See [LICENSE](LICENSE), with [COPYING](COPYING) (full GPL-3.0 text) attached.

Copyright (C) 2026 遇见
