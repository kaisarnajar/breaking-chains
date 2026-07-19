package com.breakingchains.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.breakingchains.app.ui.theme.DeepTeal
import com.breakingchains.app.ui.theme.MintLight

enum class NavItem(val title: String, val icon: ImageVector) {
    HOME("Home", Icons.Default.Home),
    HISTORY("History", Icons.Default.CalendarMonth),
    COMMUNITY("Community", Icons.Default.Group),
    SETTINGS("Settings", Icons.Default.Settings)
}

@Composable
fun BottomNavBar(
    selectedItem: NavItem = NavItem.HISTORY,
    onItemSelected: (NavItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .height(72.dp),
        color = Color.White,
        tonalElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem.values().forEach { item ->
                val isSelected = item == selectedItem

                val bgColor = animateColorAsState(
                    targetValue = if (isSelected) MintLight else Color.Transparent,
                    label = "navBg"
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(bgColor.value)
                        .clickable { onItemSelected(item) }
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = DeepTeal,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            ),
                            color = DeepTeal
                        )
                    }
                }
            }
        }
    }
}
