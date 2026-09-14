# Random Things 0.2 — NeoForge 1.21.1 部分移植

这是从本地 `Random-Things` 的 `origin/1.12.2` 分支移植的第一阶段工程。

当前包含：

- 六种木材平台
- 彩虹灯
- 普通木棍块与返回型木棍块
- 沃土及其耕作状态
- 超级滑滑石、超级润滑平台、超级润滑冰
- 灼炎打火石（本阶段使用原版火焰；自定义 Blazing Fire 留待后续）

当前不包含旧版的 Baubles、OpenComputers、JEI、ASM/Coremod、GUI、方块实体、世界生成和复杂传送系统。

## 构建

本工程使用 ARM64 Java 21 和 NeoForge 21.1.250。将 JAVA_HOME 指向自己的 JDK 21 安装目录，然后执行：

```sh
./gradlew build
```

开发客户端和专用服务器分别使用：

```sh
./gradlew runClient
./gradlew runServer
```

旧版参考源码保留在同级目录的 `Random-Things`，没有在本工程中覆盖。

## 来源与许可证

非官方实验性部分移植，参考 https://github.com/lumien231/Random-Things 的
`1.12.2` 提交 `27c32df89682325d2fe6a95032892645b2c259de`。
原版 README 内含 MIT 许可证，全文及原作者声明保留在 LICENSE 中。
TEMPLATE_LICENSE.txt 属于 NeoForge MDK 模板。

## 验证边界及已知差异

`./gradlew build` 已通过；6 个 NeoForge 游戏测试全部通过，覆盖返回型木棍块、方块掉落、彩虹灯红石变化、平台碰撞、润滑动力学、肥沃泥土和配方加载。
客户端资源加载和开发专用服务器启动已通过；本机快速连接测试没有形成稳定的自动化连接证据，仍建议在客户端手动加入专用服务器验收。
本轮补齐了 12 个方块掉落表、普通及返回型木棍块配方、透明渲染、工具标签，并修复肥沃泥土的植物更新循环。

- 六种平台采用独立 ID，不支持旧存档迁移。
- 返回型木棍块的物品 ID 为 `randomthings:returningblockofsticks`；放置后会进入返回状态，计时结束后掉落该物品。
- 肥沃泥土通过额外随机 tick 加速可施骨粉植物；旧版所有植物类型的兼容性未全部恢复。
- Blaze and Steel 暂用原版火焰；超润滑石配方以超润滑冰替代尚未移植的原料。
- 未实现旧存档数据迁移、客户端自动化连接验收和复杂功能；这些留待后续版本。
- 发布时应标记为实验性部分移植，不能宣称完整兼容旧版行为。
