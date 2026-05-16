package top.eaverse.emocycle.watch.ui.screen

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.DatePicker
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import top.eaverse.emocycle.watch.data.local.MoodLogEntity
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
    var selectedLog by remember { mutableStateOf<MoodLogEntity?>(null) }
    var selectedPhase by remember { mutableStateOf<MoodPhase?>(null) }
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadLatest()
    }

    if (showDatePicker) {
        BackHandler {
            showDatePicker = false
        }
        DatePicker(
            initialDate = selectedDate ?: LocalDate.now(),
            onDatePicked = {
                selectedDate = it
                showDatePicker = false
            }
        )
        return
    }

    val filteredLogs = remember(logs, selectedPhase, selectedDate) {
        logs.filter { log ->
            val phaseMatched = selectedPhase == null || log.phase == selectedPhase?.name
            val dateMatched = selectedDate == null || log.createdAt.toLocalDate() == selectedDate
            phaseMatched && dateMatched
        }
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

            item(key = "phase_filter") {
                Button(
                    onClick = {
                        selectedPhase = when (selectedPhase) {
                            null -> MoodPhase.NORMAL
                            MoodPhase.NORMAL -> MoodPhase.LOW_TROUGH
                            MoodPhase.LOW_TROUGH -> MoodPhase.RELIEF
                            MoodPhase.RELIEF -> null
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("阶段：${selectedPhase.toDisplayNameOrAll()}")
                }
            }

            item(key = "date_filter") {
                Button(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("日期：${selectedDate?.toFilterLabel() ?: "全部"}")
                }
            }

            if (selectedDate != null || selectedPhase != null) {
                item(key = "clear_filters") {
                    Button(
                        onClick = {
                            selectedDate = null
                            selectedPhase = null
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("清除筛选")
                    }
                }
            }

            if (filteredLogs.isEmpty()) {
                item(key = "empty") {
                    Text("没有符合条件的记录", modifier = Modifier.fillMaxWidth())
                }
            } else {
                items(
                    items = filteredLogs,
                    key = { it.id }
                ) { log ->
                    Button(
                        onClick = { selectedLog = log },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = buildHistorySummary(
                                log = log,
                                timeText = format.format(Date(log.createdAt)),
                                includeNote = false
                            ),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }

    val detailLog = selectedLog
    if (detailLog != null) {
        AlertDialog(
            visible = true,
            onDismissRequest = { selectedLog = null },
            confirmButton = {
                AlertDialogDefaults.EdgeButton(
                    onClick = { selectedLog = null }
                ) {
                    Text("关闭")
                }
            },
            title = { Text("记录详情") },
            text = {
                Text(
                    buildHistorySummary(
                        log = detailLog,
                        timeText = format.format(Date(detailLog.createdAt)),
                        includeNote = true
                    )
                )
            }
        )
    }
}

private fun buildHistorySummary(
    log: MoodLogEntity,
    timeText: String,
    includeNote: Boolean
): String {
    return buildString {
        append(timeText)
        append("\n情绪：")
        append(log.moodScore)
        append("/10")
        append("\n阶段：")
        append(log.phase.toPhaseDisplayName())
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
        if (includeNote && !log.note.isNullOrBlank()) {
            append("\n备注：")
            append(log.note)
        }
    }
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
}

private fun LocalDate.toFilterLabel(): String {
    return "%04d-%02d-%02d".format(year, monthValue, dayOfMonth)
}

private fun MoodPhase?.toDisplayNameOrAll(): String {
    return when (this) {
        null -> "全部"
        MoodPhase.NORMAL -> "正常"
        MoodPhase.LOW_TROUGH -> "低谷"
        MoodPhase.RELIEF -> "缓解"
    }
}

private fun String.toPhaseDisplayName(): String {
    return when (runCatching { MoodPhase.valueOf(this) }.getOrNull()) {
        MoodPhase.NORMAL -> "正常"
        MoodPhase.LOW_TROUGH -> "低谷"
        MoodPhase.RELIEF -> "缓解"
        null -> this
    }
}
