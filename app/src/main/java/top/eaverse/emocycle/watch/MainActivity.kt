package top.eaverse.emocycle.watch

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import androidx.wear.compose.material3.AppScaffold
import top.eaverse.emocycle.watch.data.local.AppDatabase
import top.eaverse.emocycle.watch.data.repository.MoodLogRepository
import top.eaverse.emocycle.watch.sync.NoOpMoodLogSyncer
import top.eaverse.emocycle.watch.ui.screen.AboutScreen
import top.eaverse.emocycle.watch.ui.screen.HomeMenuScreen
import top.eaverse.emocycle.watch.ui.screen.MoodHistoryScreen
import top.eaverse.emocycle.watch.ui.screen.MoodLogFormScreen
import top.eaverse.emocycle.watch.ui.theme.WearAppTheme
import top.eaverse.emocycle.watch.viewmodel.MoodLogViewModel

class MainActivity : ComponentActivity() {

    private enum class AppScreen {
        HOME,
        RECORD,
        HISTORY,
        ABOUT
    }

    private val viewModel: MoodLogViewModel by viewModels {
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "mood_logs.db"
        ).build()

        val repository = MoodLogRepository(db.moodLogDao())
        val syncer = NoOpMoodLogSyncer()

        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MoodLogViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MoodLogViewModel(repository, syncer) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var screen by rememberSaveable { mutableStateOf(AppScreen.HOME) }

            WearAppTheme {
                BackHandler(enabled = screen != AppScreen.HOME) {
                    screen = AppScreen.HOME
                }

                AppScaffold {
                    when (screen) {
                        AppScreen.HOME -> HomeMenuScreen(
                            onRecordNowClick = { screen = AppScreen.RECORD },
                            onViewHistoryClick = { screen = AppScreen.HISTORY },
                            onAboutClick = { screen = AppScreen.ABOUT }
                        )
                        AppScreen.RECORD -> MoodLogFormScreen(
                            viewModel = viewModel,
                            onBack = { screen = AppScreen.HOME }
                        )
                        AppScreen.HISTORY -> MoodHistoryScreen(
                            viewModel = viewModel,
                            onBack = { screen = AppScreen.HOME }
                        )
                        AppScreen.ABOUT -> AboutScreen(
                            onBack = { screen = AppScreen.HOME }
                        )
                    }
                }
            }
        }
    }
}
