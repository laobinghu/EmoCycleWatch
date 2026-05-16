package top.eaverse.emocycle.watch.ui.screen

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import top.eaverse.emocycle.watch.model.MoodPhase
import top.eaverse.emocycle.watch.model.Trigger
import top.eaverse.emocycle.watch.viewmodel.MoodLogViewModel

@Composable
fun MoodHistoryScreen(
    viewModel: MoodLogViewModel,
    onBack: () -> Unit
) {
    val logs = viewModel.uiState.historyLogs
    val scrollState = rememberTransformingLazyColumnState()
    val format = remember { SimpleDateFormat("MM-dd HH:mm", Locale.getDefault()) }

    LaunchedEffect(Unit) {
        viewModel.loadLatest()
    }

    ScreenScaffold(
        scrollState = scrollState,
        timeText = { TimeText() },
        edgeButton = {
            EdgeButton(
                onClick = onBack,
                buttonSize = EdgeButtonSize.Small
            ) {
                Text("返回")
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "title") {
                ListHeader {
                    Text("历史记录")
                }
            }

            if (logs.isEmpty()) {
                item(key = "empty") {
                    Text("暂无记录", modifier = Modifier.fillMaxWidth())
                }
            } else {
                items(
                    items = logs,
                    key = { it.id }
                ) { log ->
                    Button(
                        onClick = {},
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val timeText = format.format(Date(log.createdAt))
                        Text(
                            text = buildString {
                                append(timeText)
                                append("\n情绪：")
                                append(log.moodScore)
                                append("/10")
                                append("\n阶段：")
                                append(
                                    when (runCatching { MoodPhase.valueOf(log.phase) }.getOrNull()) {
                                        MoodPhase.NORMAL -> "正常"
                                        MoodPhase.LOW_TROUGH -> "低谷"
                                        MoodPhase.RELIEF -> "缓解"
                                        null -> log.phase
                                    }
                                )
                                val triggerText = log.triggers
                                    .split(",")
                                    .filter { it.isNotBlank() }
                                    .map { name ->
                                        when (runCatching { Trigger.valueOf(name) }.getOrNull()) {
                                            Trigger.STAY_UP_LATE -> "熬夜"
                                            Trigger.SLEEP_WORSE -> "睡眠变差"
                                            Trigger.WORK_STUDY_STRESS -> "工作/学习压力大"
                                            Trigger.INTERPERSONAL_CONFLICT -> "人际/争吵"
                                            Trigger.LESS_EXERCISE -> "运动减少"
                                            Trigger.OTHER -> "其他"
                                            null -> name
                                        }
                                    }
                                    .joinToString("、")
                                if (triggerText.isNotBlank()) {
                                    append("\n诱因：")
                                    append(triggerText)
                                }
                                if (!log.note.isNullOrBlank()) {
                                    append("\n备注：")
                                    append(log.note)
                                }
                            },
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}
