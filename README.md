BBS_Invisible 1.1.0 — 源码 / source
作者 / Author: 遇见
Minecraft 1.20.1 · Fabric · BBS 2.7 附属模组 / addon（仅客户端 / client-only）

目录 / Layout
  src/bbs/invisible/            入口类 BbsInvisibleAddon、BbsInvisibleClientAddon
  src/bbs/invisible/render/     透明背景核心：Transparency、CloudFade、AlphaFix、TerrainFade、IdFilter
  src/bbs/invisible/clip/       “透明背景”剪辑：TransparentClip、UITransparentClip
  src/bbs/invisible/mixin/      Mixin（含 BBS 界面与渲染的注入）
  res/                          fabric.mod.json、mixin 配置、语言文件（zh_cn / zh_tw / en_us）、图标

构建 / Build
  编译需要 BBS 2.7 的 jar 以及 Minecraft 1.20.1 / Fabric 的映射环境；
  Mixin 使用 intermediary 名称，未使用 refmap。

许可证 / License
  LGPL-3.0-or-later（见 LICENSE，并附 COPYING 即 GPL-3.0 全文）
  Copyright (C) 2026 遇见
