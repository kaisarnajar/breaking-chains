package com.breakingchains.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakingchains.app.ui.theme.DeepTeal
import com.breakingchains.app.ui.theme.MintLight
import com.breakingchains.app.ui.theme.SoftRedBg

@Composable
fun CalendarMonthView(
    monthName: String,
    soberDays: Set<Int>,
    relapseDays: Set<Int>,
    todayDay: Int,
    onPreviousMonth: () -> Unit,
    onNextMonth: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Month Header with arrows
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthName,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )

                Row {
                    IconButton(
                        onClick = onPreviousMonth,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Month",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(
                        onClick = onNextMonth,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Month",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Day of Week Header (S M T W T F S)
            val daysOfWeek = listOf("S", "M", "T", "W", "T", "F", "S")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                daysOfWeek.forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.width(36.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Calendar Days Grid (Sample month starting on Sunday)
            val totalDays = 31
            val leadingOffset = 0 // October 2023 starts on Sunday
            val daysList = (1..totalDays).toList()

            val rows = (daysList.size + leadingOffset + 6) / 7

            for (rowIndex in 0 until rows) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (colIndex in 0 until 7) {
                        val dayNumber = rowIndex * 7 + colIndex + 1 - leadingOffset
                        if (dayNumber in 1..totalDays) {
                            val isSober = soberDays.contains(dayNumber)
                            val isRelapse = relapseDays.contains(dayNumber)
                            val isToday = dayNumber == todayDay

                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .then(
                                        when {
                                            isSober -> Modifier.background(MintLight)
                                            isRelapse -> Modifier.background(SoftRedBg)
                                            else -> Modifier
                                        }
                                    )
                                    .then(
                                        if (isToday) Modifier.border(2.dp, DeepTeal, CircleShape) else Modifier
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$dayNumber",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isToday || isSober || isRelapse) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 14.sp
                                    ),
                                    color = when {
                                        isRelapse -> Color(0xFFEF4444)
                                        isSober -> DeepTeal
                                        isToday -> DeepTeal
                                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.size(36.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Divider(color = Color(0xFFF1F5F9))
            Spacer(modifier = Modifier.height(12.dp))

            // Bottom Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                LegendItem(color = MintLight, text = "Sober")
                Spacer(modifier = Modifier.width(20.dp))
                LegendItem(color = SoftRedBg, text = "Relapse")
                Spacer(modifier = Modifier.width(20.dp))
                LegendOutlineItem(borderColor = DeepTeal, text = "Today")
            }
        }
    }
}

@Composable
fun LegendItem(color: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun LegendOutlineItem(borderColor: Color, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .border(2.dp, borderColor, CircleShape)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
