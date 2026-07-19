package com.breakingchains.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MilitaryTech
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
import com.breakingchains.app.data.model.Milestone
import com.breakingchains.app.data.model.MilestoneIcon
import com.breakingchains.app.ui.theme.DeepTeal
import com.breakingchains.app.ui.theme.MintLight

@Composable
fun MilestoneCard(
    milestone: Milestone,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(130.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (milestone.isUnlocked) Color.White else Color(0xFFF1F5F9)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (milestone.isUnlocked) 1.dp else 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (milestone.isUnlocked) MintLight else Color(0xFFE2E8F0)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when {
                        !milestone.isUnlocked -> Icons.Default.Lock
                        milestone.iconType == MilestoneIcon.MEDAL -> Icons.Default.EmojiEvents
                        else -> Icons.Default.MilitaryTech
                    },
                    contentDescription = milestone.title,
                    tint = if (milestone.isUnlocked) DeepTeal else Color(0xFF94A3B8),
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = milestone.title,
                style = MaterialTheme.typography.titleSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = if (milestone.isUnlocked) DeepTeal else Color(0xFF64748B),
                textAlign = TextAlign.Center
            )

            Text(
                text = milestone.subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = if (milestone.isUnlocked) Color(0xFF64748B) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )
        }
    }
}
