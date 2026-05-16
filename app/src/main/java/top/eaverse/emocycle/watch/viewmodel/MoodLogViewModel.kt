package top.eaverse.emocycle.watch.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import top.eaverse.emocycle.watch.data.local.MoodLogEntity
import top.eaverse.emocycle.watch.data.repository.MoodLogRepository
import top.eaverse.emocycle.watch.model.MoodPhase
import top.eaverse.emocycle.watch.model.Trigger
import top.eaverse.emocycle.watch.sync.MoodLogSyncer

data class MoodLogUiState(
    val moodScore: Int = 5,
    val phase: MoodPhase = MoodPhase.NORMAL,
    val selectedTriggers: Set<Trigger> = emptySet(),
    val note: String = "",
    val uiMessage: String? = null,
    val historyLogs: List<MoodLogEntity> = emptyList()
)

class MoodLogViewModel(
    private val repository: MoodLogRepository,
    private val syncer: MoodLogSyncer
) : ViewModel() {

    var uiState by mutableStateOf(MoodLogUiState())
        private set

    init {
        loadLatest()
    }

    fun onMoodScoreChange(value: Int) {
        uiState = uiState.copy(moodScore = value, uiMessage = null)
    }

    fun onPhaseChange(phase: MoodPhase) {
        uiState = uiState.copy(phase = phase, uiMessage = null)
    }

    fun toggleTrigger(trigger: Trigger) {
        val updated = uiState.selectedTriggers.toMutableSet().apply {
            if (contains(trigger)) remove(trigger) else add(trigger)
        }
        uiState = uiState.copy(selectedTriggers = updated, uiMessage = null)
    }

    fun onNoteChange(note: String) {
        uiState = uiState.copy(note = note, uiMessage = null)
    }

    fun onNoteInputResult(note: String?) {
        if (note == null) return
        uiState = uiState.copy(note = note, uiMessage = null)
    }

    fun clearMessage() {
        uiState = uiState.copy(uiMessage = null)
    }

    fun loadLatest(limit: Int = 30) {
        viewModelScope.launch {
            val logs = repository.latest(limit)
            uiState = uiState.copy(historyLogs = logs)
        }
    }

    fun save() {
        val current = uiState

        if (current.moodScore !in 0..10) {
            uiState = current.copy(uiMessage = "MoodScore 必须在 0..10")
            return
        }

        viewModelScope.launch {
            val entity = MoodLogEntity(
                createdAt = System.currentTimeMillis(),
                moodScore = current.moodScore,
                phase = current.phase.name,
                triggers = current.selectedTriggers.joinToString(",") { it.name },
                note = current.note.takeIf { it.isNotBlank() }
            )

            val newId = repository.insert(entity)
            syncer.sync(entity.copy(id = newId))
            val logs = repository.latest(30)
            uiState = current.copy(uiMessage = "保存成功", historyLogs = logs)
        }
    }
}
