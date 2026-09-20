# Random Things 1.21.1 移植路线

本清单以 `origin/1.12.2` 为功能基准，以可独立构建、测试和发布的小版本为单位推进。
它不是旧存档迁移承诺；注册名是否能直接沿用，需要在实现每一项时再次核对。

依赖标记：

- `BE`：方块实体与持久化数据
- `GUI`：菜单、屏幕和容器同步
- `NET`：自定义网络包
- `WORLD`：战利品、结构、特征、维度或其他世界数据
- `CLIENT`：特殊渲染、粒子、声音过滤或客户端状态
- `COMPAT`：依赖其他模组或可选 API

## 已完成：0.1–0.8

- [x] 六种木材平台
- [x] 彩虹灯
- [x] 普通木棍块、返回型木棍块
- [x] 肥沃泥土及耕作行为
- [x] 超级润滑冰、石头和平台
- [x] Blaze and Steel、炽烈火焰
- [x] 瓶装空气
- [x] 稳态末影珍珠
- [x] 基础红石接口、红石工具、红石激活器
- [x] 逃生绳
- [x] 接触式按钮、接触式拉杆、单向红石块

## 下一版：0.9，相位玻璃与触发玻璃

- [ ] 青金石玻璃：只与玩家发生碰撞
- [ ] 石英玻璃：只与非玩家实体发生碰撞
- [ ] 青金石灯：恢复“客户端可见、服务端不参与判定”的光照语义
- [ ] 石英灯：恢复“服务端参与判定、客户端不显示”的光照语义
- [ ] 触发玻璃：红石触发后 60 tick 内关闭碰撞，并可连锁触发相邻玻璃
- [ ] 注册、创造栏、模型、透明渲染、纹理、语言、掉落和配方
- [ ] GameTest 覆盖玩家/非玩家碰撞、60 tick 复位、连锁传播和服务端光照判定
- [ ] 客户端人工验收两种灯的显示差异

注意：1.21.1 的光照引擎与 1.12.2 不同。如果无法安全表达客户端/服务端分离光照，优先保持玩法判定，显示差异需在 0.9 发布说明中明确记录。

## 近期候选：低到中等依赖

### 彩色与装饰方块

- [ ] 16 色发光方块
- [ ] 16 色透明发光方块 `CLIENT`
- [ ] 16 色染色砖
- [ ] 16 色发光染色砖 `CLIENT`
- [ ] 幻隐方块 Block Diaphanous `CLIENT`
- [ ] 彩色草方块与 16 色草籽
- [ ] 发光蘑菇
- [ ] 古代砖

### 运动与实体交互

- [ ] 压缩史莱姆块：三级高度和弹力
- [ ] Slime Cube `BE`
- [ ] 超级润滑靴
- [ ] 过滤型超级润滑平台 `BE GUI NET`
- [ ] 方块失稳器 Block Destabilizer `BE GUI NET`

### 植物与基础物品

- [ ] 莲花、莲花种子和莲花花朵
- [ ] 捕虫草/瓶子草 Pitcher Plant
- [ ] 魔法豆、豆芽、豆荚、豆茎与豆汤 `WORLD`
- [ ] 天气蛋：晴天、雨天、雷暴
- [ ] 金指南针、绿宝石指南针
- [ ] 末影桶、强化末影桶
- [ ] 位置过滤器、实体过滤器、物品过滤器（使用 Data Components）
- [ ] 合成配方物品（使用 Data Components）
- [ ] ID 卡（使用 Data Components）

## 红石与自动化阶段

### 红石控制

- [ ] 模拟信号发射器 `BE GUI NET`
- [ ] 高级红石中继器 `BE GUI NET`
- [ ] 高级红石火把 `BE GUI NET`
- [ ] 红石观察者 `BE`
- [ ] 高级红石接口 `BE GUI NET`
- [ ] 红石遥控器 `GUI NET`
- [ ] Link Orb `BE`

### 检测器与接口

- [ ] 实体检测器 `BE GUI NET`
- [ ] 在线玩家检测器 `BE`
- [ ] 聊天检测器 `BE NET`
- [ ] 全局聊天检测器 `BE GUI NET`
- [ ] 通知接口 `BE GUI NET`
- [ ] 玩家接口、创造玩家接口 `BE GUI NET COMPAT`

### 自动化方块

- [ ] 方块破坏器 `BE`
- [ ] 点火器 `BE GUI NET`
- [ ] 铁制投掷器 `BE GUI`
- [ ] 物品收集器、高级物品收集器 `BE GUI NET`
- [ ] 库存重定向器 `BE`
- [ ] 库存测试器 `BE GUI`
- [ ] 染色机 `BE GUI`
- [ ] 自定义工作台 `BE GUI`
- [ ] 药水蒸发器 `BE GUI`
- [ ] 流体显示器 `BE CLIENT`

### 功能板系列

- [ ] 重定向板、过滤重定向板 `BE GUI`
- [ ] 纠正板
- [ ] 物品封印板
- [ ] 物品修复板
- [ ] 加速板、定向加速板
- [ ] 弹跳板
- [ ] 收集板
- [ ] 红石板
- [ ] 加工板 `BE GUI NET`
- [ ] 提取板 `BE GUI`

## 声音、显示与复杂客户端功能

- [ ] Sound Box `BE CLIENT`
- [ ] Sound Dampener `BE GUI CLIENT`
- [ ] 便携声音抑制器 `GUI CLIENT`
- [ ] 声音记录器、声音图样 `GUI NET CLIENT`
- [ ] 光线重定向器 `BE NET CLIENT`
- [ ] Voxel Projector `BE GUI NET CLIENT`
- [ ] 荧光效果与旧版发光渲染替代方案 `CLIENT`
- [ ] Eclipsed Clock `NET CLIENT`
- [ ] Chunk Analyzer `GUI NET CLIENT`
- [ ] Divining Rod 系列 `CLIENT COMPAT`

## 生物群系、结构与世界内容

- [ ] Biome Stone 全变体
- [ ] Biome Glass
- [ ] Biome Crystal（使用 Data Components）
- [ ] Biome Radar `BE WORLD`
- [ ] Nature Core `BE WORLD`
- [ ] Rain Shield `BE WORLD`
- [ ] Sakanade `WORLD`
- [ ] Ancient Furnace `BE WORLD`
- [ ] Special Chest `BE WORLD`
- [ ] 海底宝箱与 Bottle of Air 获取方式 `WORLD`
- [ ] 和平蜡烛及怪物生成限制 `BE WORLD CLIENT`
- [ ] Imbuing Station、药水与 Imbue 系统 `BE GUI NET`

## 传送与网络系统

- [ ] Ender Bridge、Prismarine Ender Bridge `BE`
- [ ] Ender Anchor `BE`
- [ ] Ender Mailbox、Ender Letter `BE GUI NET WORLD`
- [ ] Floo Brick、Floo Pouch、Floo Sign、Floo Token 与 Floo 网络 `BE GUI NET WORLD CLIENT`
- [ ] Port Key `NET WORLD`
- [ ] Summoning Pendulum `WORLD`
- [ ] Rez Stone `WORLD`

## Rune 与装备阶段

- [ ] 16 色 Rune Dust、Rune Pattern 与 Rune Base `BE WORLD CLIENT`
- [ ] Water Walking Boots
- [ ] Lava Charm、Lava Wader
- [ ] Obsidian Skull、Skull Ring、Water Walking Boots 组合装备
- [ ] Magic Hood
- [ ] Time in a Bottle（使用 Data Components）
- [ ] 饰品槽支持 `COMPAT`：需要先决定是否引入 Curios

## Spectre 大型阶段

- [ ] Spectre Ingot、String 和基础材料
- [ ] Spectre Plank、Log、Leaf、Sapling 与树木生成 `WORLD`
- [ ] Spectre Sword、Pickaxe、Axe、Shovel
- [ ] Spectre Charger、Illuminator、Anchor、Lens
- [ ] Spectre Coil 全变体 `BE NET`
- [ ] Spectre Energy Injector、Spectre Core `BE NET`
- [ ] Spectre Key 与私人空间/维度 `BE NET WORLD CLIENT`
- [ ] Spectre 世界存储、区块分配、传送和旧数据替代方案 `WORLD`

Spectre 系统应作为独立里程碑处理，不与普通方块版本混合；现代维度、区块票据和跨维度持久化都需要重新设计。

## 外部兼容与最后阶段

- [ ] JEI 配方说明与 Imbuing 分类 `COMPAT`
- [ ] Curios 饰品兼容（替代 Baubles）`COMPAT`
- [ ] OpenComputers/后继项目兼容性评估 `COMPAT`
- [ ] Thermal 系列兼容性评估 `COMPAT`
- [ ] Tinkers' Construct 材料与冶炼兼容性评估 `COMPAT`
- [ ] 旧版 ASM/Coremod 功能逐项改写为事件、Data Components 或最小 Mixin
- [ ] 旧存档迁移方案：注册名、方块状态、物品 metadata、NBT 到 Data Components

外部兼容不应成为核心移植的硬依赖。没有活跃的 1.21.1 对应 API 时，明确标记为不支持，而不是加入不可维护的兼容层。

## 每个版本的统一验收

- [ ] `./gradlew build`
- [ ] `./gradlew runGameTestServer --console=plain`
- [ ] 开发客户端完成资源加载，无本模组缺失模型或纹理
- [ ] 专用服务器启动到 `Done`，不加载客户端专用类
- [ ] 新增内容均可从创造模式取得，或有明确的生存获取途径
- [ ] 放置、破坏、掉落、配方、持久化和网络行为有相称的测试
- [ ] README、版本号和已知差异同步更新
- [ ] 原始 1.12.2 参考目录保持不变
