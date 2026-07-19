package com.breakingchains.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakingchains.app.ui.theme.DeepTeal
import com.breakingchains.app.ui.theme.MintLight

@Composable
fun StreakAnalyticsCard(
    longestStreakDays: Int,
    monthlyAverageDays: Double,
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Longest Streak",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$longestStreakDays Days",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepTeal
                        )
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Monthly Avg",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$monthlyAverageDays Days",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = DeepTeal
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Monthly Bar Chart (Aug, Sep, Oct, Nov, Dec)
            val months = listOf("Aug", "Sep", "Oct", "Nov", "Dec")
            val barHeights = listOf(0.4f, 0.7f, 1.0f, 0.15f, 0.15f)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                months.forEachIndexed { index, month ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.weight(1f)
                    ) {
                        Box(
                            modifier = Modifier
                                .width(36.dp)
                                .height((40 * barHeights[index]).dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    if (month == "Oct") DeepTeal else Color(0xFFE2E8F0)
                                )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = month,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (month == "Oct") FontWeight.Bold else FontWeight.Normal
                            ),
                            color = if (month == "Oct") DeepTeal else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
