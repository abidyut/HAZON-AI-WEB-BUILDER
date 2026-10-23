package com.example.ui.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.ui.components.SyntaxHighlighter
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBlue
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonBorderHover
import com.example.ui.theme.HazonCodeLineNum
import com.example.ui.theme.HazonGreen
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPanel3
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted
import com.example.ui.theme.HazonViolet
import com.example.viewmodel.WorkspaceTab

@Composable
fun WorkspaceScreen(
    project: ProjectEntity,
    activeTab: WorkspaceTab,
    onTabChange: (WorkspaceTab) -> Unit,
    files: List<ProjectFileEntity>,
    activeFile: ProjectFileEntity?,
    activeFileContent: String,
    onSelectFile: (ProjectFileEntity) -> Unit,
    onUpdateFileContent: (String) -> Unit,
    onSaveFile: () -> Unit,
    onAddNewFile: (String) -> Unit,
    onDeleteFile: (String) -> Unit,
    messages: List<ChatMessageEntity>,
    thinkingState: com.example.viewmodel.ThinkingState,
    onSendMessage: (String) -> Unit,
    previewHtml: String,
    consoleLogs: List<String>,
    onAddConsoleLog: (String) -> Unit,
    onBackToBuilder: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onBackToBuilder()
    }

    var showNewFileDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(HazonBackground)
            .testTag("workspace_screen")
    ) {
        // Workspace Toolbar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(HazonPanel)
                .border(1.dp, HazonBorder)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            // Tabs
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBackToBuilder, modifier = Modifier.size(34.dp)) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = HazonTextMuted
                    )
                }

                Spacer(modifier = Modifier.width(4.dp))

                WorkspaceTabButton(
                    icon = Icons.Default.ChatBubbleOutline,
                    label = "Discussion",
                    isSelected = activeTab == WorkspaceTab.DISCUSSION,
                    onClick = { onTabChange(WorkspaceTab.DISCUSSION) }
                )
                Spacer(modifier = Modifier.width(6.dp))
                WorkspaceTabButton(
                    icon = Icons.Default.Folder,
                    label = "Files",
                    isSelected = activeTab == WorkspaceTab.FILES,
                    onClick = { onTabChange(WorkspaceTab.FILES) }
                )
                Spacer(modifier = Modifier.width(6.dp))
                WorkspaceTabButton(
                    icon = Icons.Default.Computer,
                    label = "Preview",
                    isSelected = activeTab == WorkspaceTab.PREVIEW,
                    onClick = { onTabChange(WorkspaceTab.PREVIEW) }
                )
            }

            // Status chip
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0x1954DBA4))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(HazonGreen)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${project.model.uppercase()} Ready",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = HazonGreen
                )
            }
        }

        // Active Workspace View
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            when (activeTab) {
                WorkspaceTab.DISCUSSION -> {
                    DiscussionSection(
                        messages = messages,
                        thinkingState = thinkingState,
                        onSendMessage = onSendMessage
                    )
                }
                WorkspaceTab.FILES -> {
                    FilesSection(
                        files = files,
                        activeFile = activeFile,
                        content = activeFileContent,
                        onSelectFile = onSelectFile,
                        onContentChange = onUpdateFileContent,
                        onSave = onSaveFile,
                        onNewFileClick = { showNewFileDialog = true },
                        onDeleteFile = onDeleteFile
                    )
                }
                WorkspaceTab.PREVIEW -> {
                    PreviewSection(
                        previewHtml = previewHtml,
                        consoleLogs = consoleLogs,
                        onAddConsoleLog = onAddConsoleLog
                    )
                }
            }
        }
    }

    if (showNewFileDialog) {
        AlertDialog(
            onDismissRequest = { showNewFileDialog = false },
            title = { Text(text = "Create New File", color = HazonText, fontSize = 16.sp) },
            text = {
                OutlinedTextField(
                    value = newFileName,
                    onValueChange = { newFileName = it },
                    placeholder = { Text("e.g. components.js or utils.css", color = HazonTextDim) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HazonText,
                        unfocusedTextColor = HazonText,
                        focusedBorderColor = HazonPurple,
                        unfocusedBorderColor = HazonBorder
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (newFileName.isNotBlank()) {
                            onAddNewFile(newFileName.trim())
                            newFileName = ""
                            showNewFileDialog = false
                        }
                    }
                ) {
                    Text("Create", color = HazonPurple, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewFileDialog = false }) {
                    Text("Cancel", color = HazonTextMuted)
                }
            },
            containerColor = HazonPanel
        )
    }
}

@Composable
fun WorkspaceTabButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(
                if (isSelected) Color(0x2E8B5CF6) else Color.Transparent
            )
            .border(
                1.dp,
                if (isSelected) Color(0x3EA78BFA) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) Color(0xFFDED5FF) else HazonTextMuted,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFDED5FF) else HazonTextMuted
        )
    }
}

// -------------------------------------------------------------
// 1. Discussion Section
// -------------------------------------------------------------
@Composable
fun DiscussionSection(
    messages: List<ChatMessageEntity>,
    thinkingState: com.example.viewmodel.ThinkingState,
    onSendMessage: (String) -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size, thinkingState.isThinking) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(HazonBackground)
            .padding(14.dp)
    ) {
        // Chat messages and Thinking & Tasks
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            item {
                ThinkingAndTaskCard(thinkingState = thinkingState)
            }

            items(messages) { message ->
                ChatMessageItem(message = message)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Suggestion Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DiscussionChip("✨ Add animations") { onSendMessage("Add smooth CSS transitions and keyframe animations to cards.") }
            DiscussionChip("🌓 Dark theme") { onSendMessage("Ensure full dark mode palette using #090911 and purple accents.") }
            DiscussionChip("📱 Mobile layout") { onSendMessage("Optimize CSS media queries for responsive handheld screens.") }
            DiscussionChip("⚡ Optimize JS") { onSendMessage("Refactor Javascript logic for maximum rendering performance.") }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Chat Input Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(HazonPanel2)
                .border(1.dp, HazonBorder, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            BasicTextField(
                value = inputText,
                onValueChange = { inputText = it },
                textStyle = TextStyle(color = HazonText, fontSize = 13.sp),
                cursorBrush = SolidColor(HazonPurple),
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 8.dp)
                    .testTag("discussion_input"),
                decorationBox = { innerTextField ->
                    if (inputText.isEmpty()) {
                        Text(
                            text = "Reply to Hazon AI...",
                            color = HazonTextDim,
                            fontSize = 13.sp
                        )
                    }
                    innerTextField()
                }
            )

            IconButton(
                onClick = {
                    if (inputText.isNotBlank()) {
                        onSendMessage(inputText.trim())
                        inputText = ""
                    }
                },
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(HazonPurpleDark)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ThinkingAndTaskCard(
    thinkingState: com.example.viewmodel.ThinkingState
) {
    var isExpanded by remember { mutableStateOf(true) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(
                androidx.compose.ui.graphics.Brush.linearGradient(
                    listOf(Color(0xFF151428), Color(0xFF10101C))
                )
            )
            .border(
                1.dp,
                if (thinkingState.isThinking) HazonPurple else HazonBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(14.dp)
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
                            .size(28.dp)
                            .clip(RoundedCornerShape(7.dp))
                            .background(Color(0x338B5CF6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = HazonPurple,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Hazon Brain: Thinking & Tasks",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = HazonText
                            )
                            if (thinkingState.isThinking) {
                                Spacer(modifier = Modifier.width(6.dp))
                                CircularProgressIndicator(
                                    color = HazonPurple,
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(11.dp)
                                )
                            }
                        }
                        Text(
                            text = if (thinkingState.isThinking) "Reasoning & generating in progress..." else "Plan executed & synchronized",
                            fontSize = 10.sp,
                            color = if (thinkingState.isThinking) HazonPurple else HazonGreen
                        )
                    }
                }

                Text(
                    text = if (isExpanded) "Hide" else "Show",
                    fontSize = 10.sp,
                    color = HazonTextMuted,
                    modifier = Modifier.clickable { isExpanded = !isExpanded }
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = HazonBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Language & Intent row
                if (thinkingState.translatedIntent.isNotBlank()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x288B5CF6))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "LANG: ${thinkingState.detectedLanguage.uppercase()}",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = HazonPurple
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = thinkingState.activeThought,
                            fontSize = 11.sp,
                            color = Color(0xFFD1CDDE),
                            maxLines = 2
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Task items
                if (thinkingState.tasks.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        thinkingState.tasks.forEach { task ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0x15FFFFFF))
                                    .padding(horizontal = 8.dp, vertical = 5.dp)
                            ) {
                                if (task.isCompleted) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Completed",
                                        tint = HazonGreen,
                                        modifier = Modifier.size(13.dp)
                                    )
                                } else if (task.isRunning) {
                                    CircularProgressIndicator(
                                        color = HazonPurple,
                                        strokeWidth = 1.5.dp,
                                        modifier = Modifier.size(12.dp)
                                    )
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(HazonTextDim)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = task.title,
                                    fontSize = 11.sp,
                                    color = if (task.isCompleted) Color(0xFFE6E0FF) else if (task.isRunning) HazonPurple else HazonTextMuted,
                                    fontWeight = if (task.isRunning) FontWeight.SemiBold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiscussionChip(label: String, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(HazonPanel2)
            .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(text = label, fontSize = 11.sp, color = HazonTextMuted)
    }
}

@Composable
fun ChatMessageItem(message: ChatMessageEntity) {
    val isUser = message.role == "user"

    Row(
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        modifier = Modifier.fillMaxWidth()
    ) {
        if (!isUser) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0x268B5CF6))
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFC4B5FD),
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .background(if (isUser) Color(0xFF1F1B33) else HazonPanel2)
                .border(
                    1.dp,
                    if (isUser) Color(0x33A78BFA) else HazonBorder,
                    RoundedCornerShape(12.dp)
                )
                .padding(14.dp)
                .fillMaxWidth(if (isUser) 0.85f else 0.9f)
        ) {
            Text(
                text = message.content,
                fontSize = 13.sp,
                color = if (isUser) Color(0xFFE4DCFF) else Color(0xFFD1CDDE),
                lineHeight = 20.sp
            )
        }

        if (isUser) {
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF28233F))
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = Color(0xFFB8A4FF),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Files Section
// -------------------------------------------------------------
@Composable
fun FilesSection(
    files: List<ProjectFileEntity>,
    activeFile: ProjectFileEntity?,
    content: String,
    onSelectFile: (ProjectFileEntity) -> Unit,
    onContentChange: (String) -> Unit,
    onSave: () -> Unit,
    onNewFileClick: () -> Unit,
    onDeleteFile: (String) -> Unit
) {
    var isEditing by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Files Horizontal Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .background(HazonPanel)
                .border(1.dp, HazonBorder)
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                files.forEach { file ->
                    val isSelected = activeFile?.id == file.id
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(
                                if (isSelected) Color(0x2E8B5CF6) else HazonPanel2
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0x40A78BFA) else HazonBorder,
                                RoundedCornerShape(6.dp)
                            )
                            .clickable { onSelectFile(file) }
                            .padding(horizontal = 8.dp, vertical = 5.dp)
                    ) {
                        Icon(
                            imageVector = when (file.fileType) {
                                "html" -> Icons.Default.Code
                                "css" -> Icons.Default.Palette
                                "js" -> Icons.Default.Terminal
                                else -> Icons.Default.InsertDriveFile
                            },
                            contentDescription = null,
                            tint = if (isSelected) HazonPurple else HazonTextMuted,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = file.fileName,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFFE6E0FF) else HazonTextMuted
                        )
                    }
                }
            }

            IconButton(onClick = onNewFileClick, modifier = Modifier.size(30.dp)) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New file",
                    tint = HazonPurple,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        // Editor Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(HazonPanel2)
                .border(1.dp, HazonBorder)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Code,
                    contentDescription = null,
                    tint = HazonPurple,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = activeFile?.fileName ?: "No file selected",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = HazonText
                )
            }

            if (activeFile != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isEditing) Color(0x3354DBA4) else HazonPanel3)
                            .clickable { isEditing = !isEditing }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = if (isEditing) "Edit Mode" else "View Mode",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium,
                            color = if (isEditing) HazonGreen else HazonTextMuted
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onSave, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Save,
                            contentDescription = "Save",
                            tint = HazonPurple,
                            modifier = Modifier.size(15.dp)
                        )
                    }

                    IconButton(
                        onClick = { onDeleteFile(activeFile.id) },
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFF87171),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }
        }

        // Code Content Area
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B0B14))
                .padding(12.dp)
        ) {
            if (activeFile == null) {
                Text(
                    text = "Select a file above to view or edit code.",
                    color = HazonTextDim,
                    fontSize = 12.sp
                )
            } else if (isEditing) {
                BasicTextField(
                    value = content,
                    onValueChange = onContentChange,
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFC3C0D9),
                        lineHeight = 20.sp
                    ),
                    cursorBrush = SolidColor(HazonPurple),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("code_editor")
                )
            } else {
                val lines = content.lines()
                val highlighted = remember(content, activeFile.fileType) {
                    SyntaxHighlighter.highlight(content, activeFile.fileType)
                }

                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(rememberScrollState())
                ) {
                    // Line numbers
                    Column(
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .border(width = 0.dp, color = Color.Transparent)
                    ) {
                        lines.indices.forEach { idx ->
                            Text(
                                text = "${idx + 1}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                color = HazonCodeLineNum,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    // Highlighted code text
                    Text(
                        text = highlighted,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFC3C0D9),
                        lineHeight = 20.sp
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Preview Section
// -------------------------------------------------------------
@SuppressLint("SetJavaScriptEnabled")
@Composable
fun PreviewSection(
    previewHtml: String,
    consoleLogs: List<String>,
    onAddConsoleLog: (String) -> Unit
) {
    var webViewRef by remember { mutableStateOf<WebView?>(null) }
    var viewportMode by remember { mutableStateOf("full") } // "full", "mobile", "tablet"
    var showLogsDrawer by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        // Preview Header
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .background(HazonPanel)
                .border(1.dp, HazonBorder)
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Window dots
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                    Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color(0xFF10B981)))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "localhost:3000 / preview",
                    fontSize = 11.sp,
                    color = HazonTextDim
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Viewport buttons
                IconButton(onClick = { viewportMode = "mobile" }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Smartphone,
                        contentDescription = "Mobile Viewport",
                        tint = if (viewportMode == "mobile") HazonPurple else HazonTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
                IconButton(onClick = { viewportMode = "tablet" }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Tablet,
                        contentDescription = "Tablet Viewport",
                        tint = if (viewportMode == "tablet") HazonPurple else HazonTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }
                IconButton(onClick = { viewportMode = "full" }, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Computer,
                        contentDescription = "Full Viewport",
                        tint = if (viewportMode == "full") HazonPurple else HazonTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { showLogsDrawer = !showLogsDrawer },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Terminal,
                        contentDescription = "Console Logs",
                        tint = if (showLogsDrawer) HazonGreen else HazonTextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                }

                IconButton(
                    onClick = {
                        webViewRef?.loadDataWithBaseURL(null, previewHtml, "text/html", "utf-8", null)
                        onAddConsoleLog("Reloaded preview container.")
                    },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reload",
                        tint = HazonPurple,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }

        // Live WebView Viewport Box
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
                .background(HazonBackground)
        ) {
            val widthModifier = when (viewportMode) {
                "mobile" -> Modifier.width(360.dp).fillMaxHeight()
                "tablet" -> Modifier.width(600.dp).fillMaxHeight()
                else -> Modifier.fillMaxSize()
            }

            Box(
                modifier = widthModifier
                    .background(HazonBackground)
                    .border(
                        if (viewportMode != "full") 1.dp else 0.dp,
                        HazonBorderHover
                    )
            ) {
                AndroidView(
                    factory = { context ->
                        WebView(context).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            settings.cacheMode = WebSettings.LOAD_NO_CACHE
                            settings.loadWithOverviewMode = true
                            settings.useWideViewPort = true
                            setBackgroundColor(0xFF090911.toInt())

                            webChromeClient = object : WebChromeClient() {
                                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                                    if (consoleMessage != null) {
                                        onAddConsoleLog("[${consoleMessage.messageLevel()}] ${consoleMessage.message()} (line ${consoleMessage.lineNumber()})")
                                    }
                                    return super.onConsoleMessage(consoleMessage)
                                }
                            }

                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                }
                            }

                            loadDataWithBaseURL(null, previewHtml, "text/html", "utf-8", null)
                            webViewRef = this
                        }
                    },
                    update = { view ->
                        view.loadDataWithBaseURL(null, previewHtml, "text/html", "utf-8", null)
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Console Logs Drawer
            if (showLogsDrawer) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .align(Alignment.BottomCenter)
                        .background(HazonPanel)
                        .border(1.dp, HazonBorder)
                        .padding(10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Runtime Console Output",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonGreen
                        )
                        Text(
                            text = "Hide",
                            fontSize = 10.sp,
                            color = HazonTextMuted,
                            modifier = Modifier.clickable { showLogsDrawer = false }
                        )
                    }
                    HorizontalDivider(color = HazonBorder, modifier = Modifier.padding(vertical = 6.dp))
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(consoleLogs) { log ->
                            Text(
                                text = log,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = if ("ERROR" in log) Color(0xFFF87171) else Color(0xFFC4B5FD),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}
