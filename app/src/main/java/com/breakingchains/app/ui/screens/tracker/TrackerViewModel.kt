package com.breakingchains.app.ui.screens.tracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.breakingchains.app.data.model.Milestone
import com.breakingchains.app.data.model.RelapseLog
import com.breakingchains.app.data.model.User
import com.breakingchains.app.data.repository.AuthRepository
import com.breakingchains.app.data.repository.TrackerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class TrackerUiState(
    val currentUser: User? = null,
    val selectedYear: Int = 2023,
    val selectedMonth: Int = 9, // 0-indexed: 9 = October
    val monthName: String = "October 2023",
    val activeStreakDays: Int = 124,
    val longestStreakDays: Int = 22,
    val monthlyAverageDays: Double = 11.4,
    val milestones: List<Milestone> = emptyList(),
    val relapseLogs: List<RelapseLog> = emptyList(),
    val soberDaysSet: Set<Int> = (1..14).toSet() - setOf(10), // Matching Image 1
    val relapseDaysSet: Set<Int> = setOf(10),                 // Day 10 in red
    val todayDay: Int = 14
)

class TrackerViewModel(
    private val authRepository: AuthRepository,
    private val trackerRepository: TrackerRepository
) : ViewModel() {

    val currentUser = authRepository.currentUser

    private val _selectedMonth = MutableStateFlow(9) // October
    private val _selectedYear = MutableStateFlow(2023)

    val uiState: StateFlow<TrackerUiState> = combine(
        currentUser,
        _selectedMonth,
        _selectedYear
    ) { user, month, year ->
        val streak = user?.activeStreakDays ?: 124
        val milestones = trackerRepository.getMilestones(streak)
        
        TrackerUiState(
            currentUser = user,
            selectedYear = year,
            selectedMonth = month,
            monthName = getMonthName(month, year),
            activeStreakDays = streak,
            longestStreakDays = maxOf(22, streak),
            monthlyAverageDays = 11.4,
            milestones = milestones,
            soberDaysSet = (1..14).toSet() - setOf(10),
            relapseDaysSet = setOf(10),
            todayDay = 14
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrackerUiState()
    )

    fun onPreviousMonth() {
        if (_selectedMonth.value == 0) {
            _selectedMonth.value = 11
            _selectedYear.value -= 1
        } else {
            _selectedMonth.value -= 1
        }
    }

    fun onNextMonth() {
        if (_selectedMonth.value == 11) {
            _selectedMonth.value = 0
            _selectedYear.value += 1
        } else {
            _selectedMonth.value += 1
        }
    }

    private fun getMonthName(month: Int, year: Int): String {
        val monthNames = arrayOf(
            "January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"
        )
        return "${monthNames[month]} $year"
    }
}
