package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ProjectEntity
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ProjectsScreen(
    projects: List<ProjectEntity>,
    onOpenProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onCreateNewProject: () -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val filteredProjects = remember(projects, searchQuery) {
        if (searchQuery.isBlank()) {
            projects
        } else {
            projects.filter {
                it.title.contains(searchQuery, ignoreCase = true) ||
                it.prompt.contains(searchQuery, ignoreCase = true)
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HazonBackground)
            .padding(16.dp)
            .testTag("projects_screen")
    ) {
        item {
            Text(
                text = "My projects",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HazonText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Projects saved locally in your phone's internal storage via Room Database.",
                fontSize = 12.sp,
                color = HazonTextMuted
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = HazonTextMuted
                    )
                },
                placeholder = { Text("Search projects...", color = HazonTextDim, fontSize = 13.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = HazonText,
                    unfocusedTextColor = HazonText,
                    focusedBorderColor = HazonPurple,
                    unfocusedBorderColor = HazonBorder,
                    focusedContainerColor = HazonPanel,
                    unfocusedContainerColor = HazonPanel
                ),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(18.dp))
        }

        if (filteredProjects.isEmpty()) {
            item {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(HazonPanel)
                        .border(1.dp, HazonBorder, RoundedCornerShape(14.dp))
                        .padding(32.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = HazonPurple,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isBlank()) "No projects yet" else "No matching projects",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (searchQuery.isBlank())
                                "Describe an idea in AI Builder to create your first project."
                            else
                                "Try searching for a different keyword.",
                            fontSize = 12.sp,
                            color = HazonTextMuted
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HazonPurpleDark)
                                .clickable { onCreateNewProject() }
                                .padding(horizontal = 14.dp, vertical = 9.dp)
                        ) {
                            Text(
                                text = "Create a project",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredProjects) { project ->
                ProjectCardItem(
                    project = project,
                    onOpen = { onOpenProject(project.id) },
                    onDelete = { onDeleteProject(project.id) }
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ProjectCardItem(
    project: ProjectEntity,
    onOpen: () -> Unit,
    onDelete: () -> Unit
) {
    val dateStr = remember(project.updatedAt) {
        val sdf = SimpleDateFormat("MMM dd, yyyy · HH:mm", Locale.getDefault())
        sdf.format(Date(project.updatedAt))
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(HazonPanel)
            .border(1.dp, HazonBorder, RoundedCornerShape(12.dp))
            .clickable { onOpen() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0x2E8B5CF6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Code,
                            contentDescription = null,
                            tint = HazonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = project.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = dateStr,
                            fontSize = 10.sp,
                            color = HazonTextDim
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(30.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open",
                        tint = HazonPurple,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.prompt,
                fontSize = 12.sp,
                color = HazonTextMuted,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(HazonPanel2)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MODEL: ${project.model.uppercase()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = HazonPurple
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(HazonPanel2)
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "MODE: ${project.mode.uppercase()}",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF67E8F9)
                    )
                }
            }
        }
    }
}
