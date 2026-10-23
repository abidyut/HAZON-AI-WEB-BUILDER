package com.example

import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.HazonTopAppBar
import com.example.ui.components.ImportProjectModal
import com.example.ui.components.SidebarContent
import com.example.ui.screens.BuilderScreen
import com.example.ui.screens.ModelsSettingsScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextMuted
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.HazonView
import com.example.viewmodel.HazonViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: HazonViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                HazonApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun HazonApp(viewModel: HazonViewModel) {
    val currentView by viewModel.currentView.collectAsState()
    val workspaceTab by viewModel.workspaceTab.collectAsState()
    val currentMode by viewModel.currentMode.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val promptInput by viewModel.promptInput.collectAsState()
    val currentProject by viewModel.currentProject.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val projectFiles by viewModel.projectFiles.collectAsState()
    val activeFile by viewModel.activeFile.collectAsState()
    val activeFileContent by viewModel.activeFileContent.collectAsState()
    val chatMessages by viewModel.chatMessages.collectAsState()
    val apiSettings by viewModel.apiSettings.collectAsState()
    val hardwareStats by viewModel.hardwareStats.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val loadingStatus by viewModel.loadingStatus.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()
    val consoleLogs by viewModel.consoleLogs.collectAsState()
    val thinkingState by viewModel.thinkingState.collectAsState()
    val customModels by viewModel.customModels.collectAsState()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val context = LocalContext.current
    var showImportModal by remember { mutableStateOf(false) }
    var showHelpDialog by remember { mutableStateOf(false) }

    val zipPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            viewModel.importProjectFromZip(uri, context)
            showImportModal = false
        }
    }

    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearToast()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = HazonPanel,
                modifier = Modifier.padding(end = 40.dp)
            ) {
                SidebarContent(
                    currentView = currentView,
                    recentProjects = allProjects,
                    onSelectView = { view ->
                        viewModel.setView(view)
                        scope.launch { drawerState.close() }
                    },
                    onNewProject = {
                        viewModel.setView(HazonView.BUILDER)
                        scope.launch { drawerState.close() }
                    },
                    onImportProject = {
                        showImportModal = true
                        scope.launch { drawerState.close() }
                    },
                    onOpenProject = { id ->
                        viewModel.loadProject(id)
                        scope.launch { drawerState.close() }
                    }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                HazonTopAppBar(
                    currentView = currentView,
                    projectTitle = currentProject?.title,
                    onOpenMenu = {
                        scope.launch { drawerState.open() }
                    },
                    onShowHelp = { showHelpDialog = true }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = HazonBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(HazonBackground)
            ) {
                when (currentView) {
                    HazonView.BUILDER -> {
                        BuilderScreen(
                            promptText = promptInput,
                            onPromptChange = { viewModel.setPromptInput(it) },
                            selectedModel = selectedModel,
                            onModelChange = { viewModel.setSelectedModel(it) },
                            currentMode = currentMode,
                            onModeChange = { viewModel.setMode(it) },
                            isLoading = isLoading,
                            loadingStatus = loadingStatus,
                            customModels = customModels,
                            onBuild = { prompt ->
                                viewModel.createNewProjectFromPrompt(prompt)
                            }
                        )
                    }

                    HazonView.WORKSPACE -> {
                        if (currentProject != null) {
                            WorkspaceScreen(
                                project = currentProject!!,
                                activeTab = workspaceTab,
                                onTabChange = { viewModel.setWorkspaceTab(it) },
                                files = projectFiles,
                                activeFile = activeFile,
                                activeFileContent = activeFileContent,
                                onSelectFile = { viewModel.setActiveFile(it) },
                                onUpdateFileContent = { viewModel.updateActiveFileContent(it) },
                                onSaveFile = { viewModel.saveActiveFile() },
                                onAddNewFile = { viewModel.addNewFile(it) },
                                onDeleteFile = { viewModel.deleteFile(it) },
                                messages = chatMessages,
                                thinkingState = thinkingState,
                                onSendMessage = { viewModel.sendDiscussionMessage(it) },
                                previewHtml = viewModel.compilePreviewHtml(),
                                consoleLogs = consoleLogs,
                                onAddConsoleLog = { viewModel.addConsoleLog(it) },
                                onBackToBuilder = { viewModel.setView(HazonView.BUILDER) }
                            )
                        } else {
                            viewModel.setView(HazonView.BUILDER)
                        }
                    }

                    HazonView.PROJECTS -> {
                        ProjectsScreen(
                            projects = allProjects,
                            onOpenProject = { viewModel.loadProject(it) },
                            onDeleteProject = { viewModel.deleteProject(it) },
                            onCreateNewProject = { viewModel.setView(HazonView.BUILDER) }
                        )
                    }

                    HazonView.TEMPLATES -> {
                        TemplatesScreen(
                            onSelectTemplate = { template ->
                                viewModel.importTemplate(template)
                            }
                        )
                    }

                    HazonView.MODELS_SETTINGS -> {
                        ModelsSettingsScreen(
                            settings = apiSettings,
                            hardwareStats = hardwareStats,
                            customModels = customModels,
                            onSaveSettings = { viewModel.saveApiSettings(it) },
                            onTestConnection = { provider, callback ->
                                viewModel.testApiConnection(provider, callback)
                            },
                            onAddCustomModel = { name, endpoint, key, tag ->
                                viewModel.addCustomModel(name, endpoint, key, tag)
                            },
                            onDeleteCustomModel = { id ->
                                viewModel.deleteCustomModel(id)
                            },
                            onTestCustomModel = { endpoint, key, tag, callback ->
                                viewModel.testCustomModelConnection(endpoint, key, tag, callback)
                            }
                        )
                    }
                }
            }
        }
    }

    if (showImportModal) {
        ImportProjectModal(
            onDismiss = { showImportModal = false },
            onPickZip = {
                try {
                    zipPickerLauncher.launch("application/zip")
                } catch (e: Exception) {
                    zipPickerLauncher.launch("*/*")
                }
            },
            onImportGitHub = { repoUrl ->
                viewModel.importProjectFromGitHub(repoUrl)
                showImportModal = false
            },
            onImportRawCode = { title, html, css, js ->
                viewModel.importProjectFromRawCode(title, html, css, js)
                showImportModal = false
            },
            onSelectTemplate = {
                viewModel.setView(HazonView.TEMPLATES)
                showImportModal = false
            }
        )
    }

    if (showHelpDialog) {
        AlertDialog(
            onDismissRequest = { showHelpDialog = false },
            title = {
                Text(
                    text = "Hazon AI — Mobile Studio",
                    color = HazonText,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = "Hazon AI is an intelligent mobile coding studio with its own internal AI brain:\n\n" +
                            "• Multilingual AI Brain & Translator: Prompt in ANY language (বাংলা, English, etc.). The internal orchestrator translates your intent, formulates developer directives, and instructs the main AI models (DeepSeek, GLM, Qwen, Z.ai).\n" +
                            "• Automatic File Targeting: Analyzes code modifications and applies them to index.html, styles.css, or app.js automatically.\n" +
                            "• Full Hardware Utilization: Real device CPU multi-threading, RAM caching, SQLite ROM database, and GPU-accelerated WebView live preview.\n\n" +
                            "Configure your API keys in Settings to connect cloud AI endpoints.",
                    color = HazonTextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            },
            confirmButton = {
                TextButton(onClick = { showHelpDialog = false }) {
                    Text("Got it", color = HazonPurple)
                }
            },
            containerColor = HazonPanel
        )
    }
}
