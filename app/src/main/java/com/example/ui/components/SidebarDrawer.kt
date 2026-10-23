package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProjectEntity
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBlue
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonBorderHover
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonPurpleDeep
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted
import com.example.ui.theme.HazonViolet
import com.example.viewmodel.HazonView

@Composable
fun SidebarContent(
    currentView: HazonView,
    recentProjects: List<ProjectEntity>,
    onSelectView: (HazonView) -> Unit,
    onNewProject: () -> Unit,
    onImportProject: () -> Unit,
    onOpenProject: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .width(280.dp)
            .background(HazonPanel)
            .border(width = 1.dp, color = HazonBorder)
            .padding(16.dp)
            .testTag("sidebar_content")
    ) {
        // Brand Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { onSelectView(HazonView.BUILDER) }
                .padding(vertical = 8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF9C70FF), Color(0xFF5E65ED))
                        )
                    )
                    .shadow(elevation = 12.dp, shape = RoundedCornerShape(10.dp), ambientColor = HazonViolet)
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = "Hazon Logo",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = "hazon",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = HazonText,
                    letterSpacing = (-1).sp
                )
                Text(
                    text = " AI",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = HazonPurple,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // New Project Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.horizontalGradient(
                        listOf(HazonPurpleDark, HazonPurpleDeep)
                    )
                )
                .border(1.dp, Color(0x3BC4B5FD), RoundedCornerShape(10.dp))
                .clickable { onNewProject() }
                .padding(horizontal = 14.dp, vertical = 11.dp)
                .testTag("new_project_btn")
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "New project",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color(0x40140F2D))
                    .padding(horizontal = 5.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⌘ K",
                    fontSize = 10.sp,
                    color = Color(0xFFE4DCFF),
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Import Project Button
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(HazonPanel2)
                .border(1.dp, HazonBorder, RoundedCornerShape(10.dp))
                .clickable { onImportProject() }
                .padding(vertical = 10.dp)
                .testTag("import_project_btn")
        ) {
            Icon(
                imageVector = Icons.Default.CloudDownload,
                contentDescription = null,
                tint = HazonPurple,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Import Project",
                color = Color(0xFFC4B5FD),
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Navigation Section
        Text(
            text = "WORKSPACE",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HazonTextDim,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )

        SidebarNavItem(
            icon = Icons.Default.AutoAwesome,
            label = "AI Builder",
            isSelected = currentView == HazonView.BUILDER,
            onClick = { onSelectView(HazonView.BUILDER) }
        )
        SidebarNavItem(
            icon = Icons.Default.Folder,
            label = "My projects",
            isSelected = currentView == HazonView.PROJECTS,
            onClick = { onSelectView(HazonView.PROJECTS) }
        )
        SidebarNavItem(
            icon = Icons.Default.Dashboard,
            label = "Templates",
            isSelected = currentView == HazonView.TEMPLATES,
            onClick = { onSelectView(HazonView.TEMPLATES) }
        )
        SidebarNavItem(
            icon = Icons.Default.Memory,
            label = "AI Models",
            isSelected = currentView == HazonView.MODELS_SETTINGS,
            onClick = { onSelectView(HazonView.MODELS_SETTINGS) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Recent Projects Section
        Text(
            text = "RECENT PROJECTS",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = HazonTextDim,
            letterSpacing = 1.2.sp,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (recentProjects.isEmpty()) {
                item {
                    Text(
                        text = "Your projects will appear here.",
                        fontSize = 11.sp,
                        color = Color(0xFF706E83),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    )
                }
            } else {
                items(recentProjects.take(6)) { project ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onOpenProject(project.id) }
                            .padding(horizontal = 8.dp, vertical = 7.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = HazonPurple,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = project.title,
                            fontSize = 12.sp,
                            color = HazonTextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
            }
        }

        // Sidebar Bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                    Brush.linearGradient(
                        listOf(Color(0x1F8B5CF6), Color(0x0C60A5FA))
                    )
                )
                .border(1.dp, HazonBorder, RoundedCornerShape(10.dp))
                .padding(10.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0x2E8B5CF6))
                ) {
                    Icon(
                        imageVector = Icons.Default.Bolt,
                        contentDescription = null,
                        tint = HazonPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "Personal workspace",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = HazonText
                    )
                    Text(
                        text = "Build something extraordinary.",
                        fontSize = 9.sp,
                        color = HazonTextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Settings Button
        SidebarNavItem(
            icon = Icons.Default.Settings,
            label = "Settings",
            isSelected = currentView == HazonView.MODELS_SETTINGS,
            onClick = { onSelectView(HazonView.MODELS_SETTINGS) }
        )

        HorizontalDivider(color = HazonBorder, modifier = Modifier.padding(vertical = 8.dp))

        // Profile Avatar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 4.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF7C3AED), Color(0xFF3B82F6))
                        )
                    )
            ) {
                Text(
                    text = "H",
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hazon Developer",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HazonText
                )
                Text(
                    text = "Pro Studio Workspace",
                    fontSize = 10.sp,
                    color = HazonTextMuted
                )
            }
        }
    }
}

@Composable
fun SidebarNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0x248B5CF6) else Color.Transparent
            )
            .border(
                width = 1.dp,
                color = if (isSelected) Color(0x2EA78BFA) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 9.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) HazonPurple else HazonTextMuted,
            modifier = Modifier.size(17.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFD8CCFF) else HazonTextMuted
        )
    }
}
