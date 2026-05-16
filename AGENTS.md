# EmoCycle Watch — Agent Guide

## Architecture

- **Single-activity** (`MainActivity.kt`), manual navigation via `AppScreen` enum + `rememberSaveable`
- **MVVM**: `MoodLogViewModel` holds `MoodLogUiState`; ViewModel is created via custom `ViewModelProvider.Factory`
- **Local DB**: Room with KSP (`ksp libs.androidx.room.compiler`). `exportSchema = false` — no migration path yet, schema changes require `fallbackToDestructiveMigration` or manual handling
- **Sync layer**: `MoodLogSyncer` interface + `NoOpMoodLogSyncer` stub; extend to add cloud sync. `MoodLogSyncContract.kt` defines the batch format
- **TypeConverters** (`Converters.kt`) are **not registered** in `AppDatabase` — Room will not use them. Entity stores triggers as plain `String` instead

## Build & Run

```bash
# debug APK
./gradlew :app:assembleDebug

# install to connected device/emulator
./gradlew :app:installDebug
```

## Project quirks

- **Wear OS**: UI uses `androidx.wear.compose.material3.*`, not standard Compose Material 3. Scrolling uses `TransformingLazyColumn` (Wear-specific)
- **Room + KSP**: after adding/removing Room entities, a clean build (`./gradlew clean`) may be needed
- **No tests**: `ExampleUnitTest.kt` / `ExampleInstrumentedTest.kt` are template stubs; no real test suite exists
- **ProGuard**: disabled for debug; `proguard-rules.pro` is empty (release also disabled via `isMinifyEnabled = false`)
- **Gradle**: version catalog at `gradle/libs.versions.toml`; AGP 9.2.1, Kotlin 2.2.10

## Key files

| File | Purpose |
|------|---------|
| `MainActivity.kt` | Entry point, screen routing |
| `MoodLogViewModel.kt` | All UI state & business logic |
| `MoodLogFormScreen.kt` | Record form with score/phase/trigger/note |
| `MoodHistoryScreen.kt` | History list with detail dialog |
| `MoodLogEntity.kt` | Room entity |
| `MoodLogSyncContract.kt` | Batch sync record format |
| `Color.kt` | Dark-theme Wear color scheme |
