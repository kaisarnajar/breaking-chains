package com.breakingchains.app.ui.screens.relapse

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breakingchains.app.data.repository.AuthRepository
import com.breakingchains.app.data.repository.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RelapseLogUiState(
    val selectedTrigger: String = "Stress",
    val availableTriggers: List<String> = listOf("Stress", "Social Urge", "Boredom", "Fatigue", "Emotional Low", "Environmental", "Other"),
    val selectedMood: String = "Stressed",
    val availableMoods: List<String> = listOf("Anxious", "Stressed", "Sad", "Angry", "Tired", "Neutral"),
    val noteInput: String = "",
    val severity: Int = 1,
    val isLoading: Boolean = false,
    val isSubmittedSuccessfully: Boolean = false,
    val errorMessage: String? = null
)

class RelapseLogViewModel(
    private val authRepository: AuthRepository,
    private val trackerRepository: TrackerRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(RelapseLogUiState())
    val uiState: StateFlow<RelapseLogUiState> = _uiState.asStateFlow()

    fun onTriggerSelected(trigger: String) {
        _uiState.update { it.copy(selectedTrigger = trigger) }
    }

    fun onMoodSelected(mood: String) {
        _uiState.update { it.copy(selectedMood = mood) }
    }

    fun onNoteChanged(note: String) {
        _uiState.update { it.copy(noteInput = note) }
    }

    fun onSeverityChanged(severity: Int) {
        _uiState.update { it.copy(severity = severity) }
    }

    fun submitRelapseLog(onSuccess: () -> Unit) {
        val currentUser = authRepository.currentUser.value
        val userId = currentUser?.id ?: "u_guest"
        val state = _uiState.value

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                trackerRepository.logRelapse(
                    userId = userId,
                    trigger = state.selectedTrigger,
                    note = state.noteInput.ifBlank { "Personal reflection log" },
                    moodBefore = state.selectedMood,
                    severity = state.severity
                )
                _uiState.update { it.copy(isLoading = false, isSubmittedSuccessfully = true) }
                onSuccess()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Failed to log relapse.") }
            }
        }
    }
}
