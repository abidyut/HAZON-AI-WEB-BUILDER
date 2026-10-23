package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonGreen
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted
import com.example.viewmodel.HazonView

@Composable
fun HazonTopAppBar(
    currentView: HazonView,
    projectTitle: String?,
    onOpenMenu: () -> Unit,
    onShowHelp: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(HazonPanel.copy(alpha = 0.95f))
            .border(width = 1.dp, color = HazonBorder)
            .padding(horizontal = 12.dp)
    ) {
        IconButton(
            onClick = onOpenMenu,
            modifier = Modifier.size(38.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = HazonTextMuted
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Breadcrumb
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Workspace",
                fontSize = 12.sp,
                color = HazonTextDim
            )
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = HazonTextDim,
                modifier = Modifier.size(14.dp)
            )
            val title = when (currentView) {
                HazonView.BUILDER -> "AI Builder"
                HazonView.WORKSPACE -> projectTitle ?: "Project Workspace"
                HazonView.PROJECTS -> "My projects"
                HazonView.TEMPLATES -> "Templates"
                HazonView.MODELS_SETTINGS -> "AI Models & Settings"
            }
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = HazonText,
                maxLines = 1
            )
        }

        // Live system status
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0x1954DBA4))
                .border(1.dp, Color(0x3354DBA4), RoundedCornerShape(20.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .clip(CircleShape)
                    .background(HazonGreen)
                    .shadow(elevation = 6.dp, shape = CircleShape, ambientColor = HazonGreen)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "All systems ready",
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = HazonGreen
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        IconButton(
            onClick = onShowHelp,
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.HelpOutline,
                contentDescription = "Help & Architecture",
                tint = HazonTextMuted
            )
        }
    }
}
