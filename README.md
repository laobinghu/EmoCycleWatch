# 情绪周期仪 (EmoCycle Watch)

一款基于 **Wear OS** 的情绪追踪应用，帮助用户记录情绪波动、识别诱因，辅助双相情感障碍等情绪问题的自我管理。

## 功能

- **情绪记录** — 记录当下情绪评分（0–10）、情绪阶段（正常/低谷/缓解）
- **诱因标记** — 多选标记可能的触发因素（熬夜、睡眠变差、压力、争吵、运动减少等）
- **备注输入** — 支持 Wear OS 语音/键盘输入补充说明
- **历史回顾** — 按时间倒序查看所有记录，点击查看详情
- **数据持久化** — 使用 Room 本地存储，数据完全离线可用
- **同步扩展** — 预留 `MoodLogSyncer` 接口，可对接云端同步

## 技术栈

| 技术 | 用途 |
|------|------|
| Kotlin | 开发语言 |
| Jetpack Compose for Wear OS | UI 框架 |
| Wear Material 3 | 组件库 |
| Room + KSP | 本地数据库 |
| MVVM (ViewModel) | 架构模式 |
| Kotlin Serialization | 序列化 |

## 项目结构

```
app/src/main/java/top/eaverse/emocycle/watch/
├── MainActivity.kt              # 主入口，导航控制
├── data/
│   ├── local/                   # Room 数据库层
│   │   ├── AppDatabase.kt
│   │   ├── MoodLogDao.kt
│   │   ├── MoodLogEntity.kt
│   │   └── Converters.kt
│   └── repository/
│       └── MoodLogRepository.kt # 数据仓库
├── model/
│   ├── MoodPhase.kt             # 情绪阶段枚举
│   └── Trigger.kt               # 诱因枚举
├── sync/                        # 同步层（预留）
│   ├── MoodLogSyncer.kt
│   ├── MoodLogSyncContract.kt
│   └── NoOpMoodLogSyncer.kt
├── ui/
│   ├── screen/
│   │   ├── HomeMenuScreen.kt    # 首页菜单
│   │   ├── MoodLogFormScreen.kt # 记录表单
│   │   └── MoodHistoryScreen.kt # 历史记录
│   └── theme/
│       ├── Color.kt
│       ├── Theme.kt
│       └── Type.kt
└── viewmodel/
    └── MoodLogViewModel.kt      # 全局状态管理
```

## 环境要求

- Android Studio Ladybug (2024.2.1) 或更高
- Gradle 9.4+
- Kotlin 2.2+
- Android SDK 36
- 一台 Wear OS 模拟器或真机（API 29+）

## 构建与运行

```bash
# 调试 APK
./gradlew assembleDebug

# 安装到设备
./gradlew installDebug
```

## License

MIT
