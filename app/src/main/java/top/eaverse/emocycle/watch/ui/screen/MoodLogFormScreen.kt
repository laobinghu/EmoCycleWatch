package top.eaverse.emocycle.watch.ui.screen

import android.app.RemoteInput
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.material3.ButtonGroup
import androidx.wear.compose.material3.AlertDialog
import androidx.wear.compose.material3.AlertDialogDefaults
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.CheckboxButton
import androidx.wear.compose.material3.CheckboxButtonDefaults
import androidx.wear.compose.material3.EdgeButton
import androidx.wear.compose.material3.EdgeButtonSize
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.RadioButton
import androidx.wear.compose.material3.RadioButtonDefaults
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.TimeText
import androidx.wear.input.RemoteInputIntentHelper
import top.eaverse.emocycle.watch.model.MoodPhase
import top.eaverse.emocycle.watch.model.Trigger
import top.eaverse.emocycle.watch.viewmodel.MoodLogViewModel

private const val NOTE_INPUT_KEY = "note_input"

@Composable
fun MoodLogFormScreen(
    viewModel: MoodLogViewModel,
    onBack: (() -> Unit)? = null
) {
    val uiState = viewModel.uiState
    val scrollState = rememberTransformingLazyColumnState()
    val phaseItems = remember { MoodPhase.entries.toList() }
    val triggerItems = remember { Trigger.entries.toList() }
    val noteInputLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            val results = RemoteInput.getResultsFromIntent(result.data)
            val text = results?.getCharSequence(NOTE_INPUT_KEY)?.toString()
            viewModel.onNoteInputResult(text)
        }

    ScreenScaffold(
        scrollState = scrollState,
        timeText = { TimeText() },
        edgeButton = {
            if (onBack != null) {
                EdgeButton(
                    onClick = onBack,
                    buttonSize = EdgeButtonSize.Small
                ) {
                    Text("返回")
                }
            } else {
                EdgeButton(
                    onClick = viewModel::save,
                    buttonSize = EdgeButtonSize.Small
                ) {
                    Text("保存")
                }
            }
        }
    ) { contentPadding ->
        TransformingLazyColumn(
            state = scrollState,
            contentPadding = contentPadding,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item(key = "score_header") {
                ListHeader {
                    Text("情绪：${uiState.moodScore}/10")
                }
            }

            item(key = "score_group") {
                ButtonGroup(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { viewModel.onMoodScoreChange((uiState.moodScore - 1).coerceAtLeast(0)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("降低")
                    }
                    Button(
                        onClick = { viewModel.onMoodScoreChange((uiState.moodScore + 1).coerceAtMost(10)) },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("提高")
                    }
                }
            }

            item(key = "phase_header") {
                ListHeader {
                    Text("阶段（单选）")
                }
            }

            items(
                items = phaseItems,
                key = { it.name }
            ) { phase ->
                PhaseItem(
                    phase = phase,
                    selected = uiState.phase == phase,
                    onSelect = viewModel::onPhaseChange
                )
            }

            item(key = "trigger_header") {
                ListHeader {
                    Text("诱因（多选）")
                }
            }

            items(
                items = triggerItems,
                key = { it.name }
            ) { trigger ->
                TriggerItem(
                    trigger = trigger,
                    checked = uiState.selectedTriggers.contains(trigger),
                    onToggle = viewModel::toggleTrigger
                )
            }

            item(key = "note") {
                CheckboxButton(
                    checked = uiState.note.isNotBlank(),
                    onCheckedChange = {
                        val remoteInput = RemoteInput.Builder(NOTE_INPUT_KEY)
                            .setLabel("请输入备注")
                            .build()
                        val intent: Intent = RemoteInputIntentHelper.createActionRemoteInputIntent()
                        RemoteInputIntentHelper.putRemoteInputsExtra(intent, listOf(remoteInput))
                        noteInputLauncher.launch(intent)
                    },
                    colors = CheckboxButtonDefaults.checkboxButtonColors(),
                    label = {},
                    secondaryLabel = {
                        val text = if (uiState.note.isBlank()) "备注（可空）：点击输入" else "备注：${uiState.note}"
                        Text(text)
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item(key = "save_button") {
                Button(
                    onClick = viewModel::save,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("保存记录")
                }
            }

        }
    }

    val message = uiState.uiMessage
    if (message != null) {
        AlertDialog(
            visible = true,
            onDismissRequest = viewModel::clearMessage,
            confirmButton = {
                AlertDialogDefaults.EdgeButton(
                    onClick = viewModel::clearMessage
                ) {
                    Text("知道了")
                }
            },
            title = { Text("提示") },
            text = { Text(message) }
        )
    }
}

@Composable
private fun PhaseItem(
    phase: MoodPhase,
    selected: Boolean,
    onSelect: (MoodPhase) -> Unit
) {
    RadioButton(
        selected = selected,
        onSelect = { onSelect(phase) },
        colors = RadioButtonDefaults.radioButtonColors(),
        label = {},
        secondaryLabel = {
            Text(
                when (phase) {
                    MoodPhase.NORMAL -> "正常"
                    MoodPhase.LOW_TROUGH -> "低谷"
                    MoodPhase.RELIEF -> "缓解"
                }
            )
        },
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun TriggerItem(
    trigger: Trigger,
    checked: Boolean,
    onToggle: (Trigger) -> Unit
) {
    CheckboxButton(
        checked = checked,
        onCheckedChange = { onToggle(trigger) },
        colors = CheckboxButtonDefaults.checkboxButtonColors(),
        label = {},
        secondaryLabel = { Text(trigger.label) },
        modifier = Modifier.fillMaxWidth()
    )
}
