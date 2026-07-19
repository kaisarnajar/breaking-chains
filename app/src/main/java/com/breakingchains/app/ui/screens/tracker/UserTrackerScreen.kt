package com.breakingchains.app.ui.screens.tracker

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakingchains.app.ui.components.BottomNavBar
import com.breakingchains.app.ui.components.CalendarMonthView
import com.breakingchains.app.ui.components.MilestoneCard
import com.breakingchains.app.ui.components.NavItem
import com.breakingchains.app.ui.components.PersonalNoteCard
import com.breakingchains.app.ui.components.StreakAnalyticsCard
import com.breakingchains.app.ui.theme.DeepTeal
import com.breakingchains.app.ui.theme.MintLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTrackerScreen(
    state: TrackerUiState,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    onNavigateToLogRelapse: () -> Unit,
    onNavigateToScheduleCall: () -> Unit,
    onNavigateToAdmin: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(MintLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Profile",
                                tint = DeepTeal,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Serenity Path",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = DeepTeal,
                                fontSize = 18.sp
                            )
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = DeepTeal
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavBar(
                selectedItem = NavItem.HISTORY,
                onItemSelected = { item ->
                    if (item == NavItem.HOME) onNavigateToScheduleCall()
                }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 12.dp)
        ) {
            // Subtitle & Header
            Text(
                text = "YOUR JOURNEY",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Text(
                text = "History & Progress",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 26.sp
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Calendar View Card
            CalendarMonthView(
                monthName = state.monthName,
                soberDays = state.soberDaysSet,
                relapseDays = state.relapseDaysSet,
                todayDay = state.todayDay,
                onPreviousMonth = onPreviousMonth,
                onNextMonth = onNextMonth
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Milestones Reached Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Milestones Reached",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface
                )
                TextButton(onClick = { }) {
                    Text(
                        text = "View All",
                        style = MaterialTheme.typography.labelMedium,
                        color = DeepTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2x2 Grid of Milestones
            val milestones = state.milestones
            if (milestones.size >= 4) {
                Row(modifier = Modifier.fillMaxWidth()) {
                    MilestoneCard(milestone = milestones[0], modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    MilestoneCard(milestone = milestones[1], modifier = Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    MilestoneCard(milestone = milestones[2], modifier = Modifier.weight(1f))
                    Spacer(modifier = Modifier.width(12.dp))
                    MilestoneCard(milestone = milestones[3], modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 3. Streak Analytics Section
            Text(
                text = "Streak Analytics",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(12.dp))

            StreakAnalyticsCard(
                longestStreakDays = state.longestStreakDays,
                monthlyAverageDays = state.monthlyAverageDays
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 4. Motivational Quote Card with Floating Action Button (+)
            PersonalNoteCard(
                onAddClick = onNavigateToLogRelapse
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
