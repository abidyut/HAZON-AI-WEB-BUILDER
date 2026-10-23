package com.example.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.brain.Orchestrator
import com.example.brain.TranslationResult
import com.example.brain.Translator
import com.example.data.ProjectTemplate
import com.example.data.TemplatesData
import com.example.data.local.AppDatabase
import com.example.data.local.entity.ApiSettingsEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.remote.ApiResponse
import com.example.data.remote.ApiService
import com.example.data.remote.ChatApiMessage
import com.example.hardware.HardwareMonitor
import com.example.hardware.HardwareStats
import okhttp3.Request
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

enum class HazonView {
    BUILDER,
    WORKSPACE,
    PROJECTS,
    TEMPLATES,
    MODELS_SETTINGS
}

enum class WorkspaceTab {
    DISCUSSION,
    FILES,
    PREVIEW
}

data class OrchestratorTask(
    val title: String,
    val isCompleted: Boolean = false,
    val isRunning: Boolean = false
)

data class ThinkingState(
    val isThinking: Boolean = false,
    val detectedLanguage: String = "Bengali",
    val translatedIntent: String = "",
    val activeThought: String = "Hazon AI Brain initialized and ready.",
    val tasks: List<OrchestratorTask> = emptyList()
)

class HazonViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getInstance(application)
    private val dao = database.projectDao()
    private val apiService = ApiService()

    private val _currentView = MutableStateFlow(HazonView.BUILDER)
    val currentView: StateFlow<HazonView> = _currentView.asStateFlow()

    private val _workspaceTab = MutableStateFlow(WorkspaceTab.DISCUSSION)
    val workspaceTab: StateFlow<WorkspaceTab> = _workspaceTab.asStateFlow()

    private val _currentMode = MutableStateFlow("build") // "build" or "plan"
    val currentMode: StateFlow<String> = _currentMode.asStateFlow()

    private val _selectedModel = MutableStateFlow("gemini") // Default to Google AI Studio Gemini
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()

    private val _promptInput = MutableStateFlow("")
    val promptInput: StateFlow<String> = _promptInput.asStateFlow()

    private val _currentProject = MutableStateFlow<ProjectEntity?>(null)
    val currentProject: StateFlow<ProjectEntity?> = _currentProject.asStateFlow()

    private val _allProjects = MutableStateFlow<List<ProjectEntity>>(emptyList())
    val allProjects: StateFlow<List<ProjectEntity>> = _allProjects.asStateFlow()

    private val _projectFiles = MutableStateFlow<List<ProjectFileEntity>>(emptyList())
    val projectFiles: StateFlow<List<ProjectFileEntity>> = _projectFiles.asStateFlow()

    private val _activeFile = MutableStateFlow<ProjectFileEntity?>(null)
    val activeFile: StateFlow<ProjectFileEntity?> = _activeFile.asStateFlow()

    private val _activeFileContent = MutableStateFlow("")
    val activeFileContent: StateFlow<String> = _activeFileContent.asStateFlow()

    private val _chatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessageEntity>> = _chatMessages.asStateFlow()

    private val _apiSettings = MutableStateFlow(ApiSettingsEntity())
    val apiSettings: StateFlow<ApiSettingsEntity> = _apiSettings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _loadingStatus = MutableStateFlow("Hazon Brain Analyzing...")
    val loadingStatus: StateFlow<String> = _loadingStatus.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _consoleLogs = MutableStateFlow<List<String>>(listOf("Hazon Web Runtime initialised.", "Hardware GPU Layer Ready."))
    val consoleLogs: StateFlow<List<String>> = _consoleLogs.asStateFlow()

    private val _hardwareStats = MutableStateFlow(HardwareMonitor.getHardwareStats(application))
    val hardwareStats: StateFlow<HardwareStats> = _hardwareStats.asStateFlow()

    private val _thinkingState = MutableStateFlow(ThinkingState())
    val thinkingState: StateFlow<ThinkingState> = _thinkingState.asStateFlow()

    private val _customModels = MutableStateFlow<List<com.example.data.local.entity.CustomModelEntity>>(emptyList())
    val customModels: StateFlow<List<com.example.data.local.entity.CustomModelEntity>> = _customModels.asStateFlow()

    init {
        // Collect projects
        viewModelScope.launch {
            dao.getAllProjects().collectLatest { list ->
                _allProjects.value = list
            }
        }

        // Collect settings
        viewModelScope.launch {
            dao.getApiSettings().collectLatest { settings ->
                if (settings != null) {
                    _apiSettings.value = settings
                }
            }
        }

        // Collect custom models added by user
        viewModelScope.launch {
            dao.getAllCustomModels().collectLatest { models ->
                _customModels.value = models
            }
        }

        refreshHardwareStats()
    }

    fun refreshHardwareStats() {
        viewModelScope.launch(Dispatchers.Default) {
            _hardwareStats.value = HardwareMonitor.getHardwareStats(getApplication())
        }
    }

    fun setView(view: HazonView) {
        _currentView.value = view
    }

    fun setWorkspaceTab(tab: WorkspaceTab) {
        _workspaceTab.value = tab
    }

    fun setMode(mode: String) {
        _currentMode.value = mode
    }

    fun setSelectedModel(model: String) {
        _selectedModel.value = model
    }

    fun setPromptInput(text: String) {
        _promptInput.value = text
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun addConsoleLog(log: String) {
        _consoleLogs.value = _consoleLogs.value + log
    }

    fun clearConsoleLogs() {
        _consoleLogs.value = emptyList()
    }

    fun createNewProjectFromPrompt(prompt: String) {
        if (prompt.isBlank()) {
            showToast("Please enter what you want to build.")
            return
        }

        val projectId = UUID.randomUUID().toString()
        val title = if (prompt.length > 34) prompt.take(32).trim() + "..." else prompt
        val model = _selectedModel.value
        val mode = _currentMode.value

        val project = ProjectEntity(
            id = projectId,
            title = title,
            prompt = prompt,
            model = model,
            mode = mode
        )

        viewModelScope.launch {
            _isLoading.value = true
            _loadingStatus.value = "Hazon Brain: Analyzing prompt..."
            dao.insertProject(project)

            // Step 1: Hazon AI Brain translates and crafts directive
            _thinkingState.value = ThinkingState(
                isThinking = true,
                detectedLanguage = "Analyzing...",
                translatedIntent = "Translating...",
                activeThought = "Detecting user input language and extracting technical requirements...",
                tasks = listOf(
                    OrchestratorTask("Detect Language & Extract User Intent", isRunning = true),
                    OrchestratorTask("Translate to Technical Specification"),
                    OrchestratorTask("Dispatch Master Directive to AI Model ($model)"),
                    OrchestratorTask("Parse CodeBlocks & Map into Project Files"),
                    OrchestratorTask("Save to Device ROM (SQLite) & Hot-Reload Preview")
                )
            )

            val orchestratorReq = Orchestrator.preparePromptForMainAi(prompt, emptyMap())
            val translation = orchestratorReq.translation

            _thinkingState.value = _thinkingState.value.copy(
                detectedLanguage = translation.detectedLanguage,
                translatedIntent = translation.translatedPrompt,
                activeThought = "Intent mapped to ${translation.targetFiles.joinToString(", ")}. Directing AI engine...",
                tasks = listOf(
                    OrchestratorTask("Detect Language & Extract User Intent", isCompleted = true),
                    OrchestratorTask("Translate to Technical Specification", isCompleted = true),
                    OrchestratorTask("Dispatch Master Directive to AI Model ($model)", isRunning = true),
                    OrchestratorTask("Parse CodeBlocks & Map into Project Files"),
                    OrchestratorTask("Save to Device ROM (SQLite) & Hot-Reload Preview")
                )
            )

            val settings = _apiSettings.value
            val resolved = resolveApiConfiguration(model, settings)

            var generatedFiles: Map<String, String>? = null

            // Step 2: Attempt AI call (Google AI Studio Gemini, DeepSeek, GLM, etc.)
            if (resolved.apiKey.isNotBlank()) {
                _loadingStatus.value = "Directing ${resolved.providerName}..."
                val apiResponse = if (resolved.isGemini) {
                    apiService.sendGeminiCompletion(
                        apiKey = resolved.apiKey,
                        model = resolved.modelName,
                        systemPrompt = orchestratorReq.systemPrompt,
                        userPrompt = orchestratorReq.userPromptForAi,
                        endpointUrl = resolved.endpoint
                    )
                } else {
                    apiService.sendChatCompletion(
                        endpointUrl = resolved.endpoint,
                        apiKey = resolved.apiKey,
                        model = resolved.modelName,
                        messages = listOf(
                            ChatApiMessage(role = "user", content = orchestratorReq.userPromptForAi)
                        ),
                        systemPrompt = orchestratorReq.systemPrompt
                    )
                }

                if (apiResponse.success && apiResponse.content.isNotBlank()) {
                    val parsed = Orchestrator.parseAndApplyAiResponse(
                        apiResponse.content,
                        translation.detectedLanguage
                    )
                    if (parsed.updatedFiles.isNotEmpty()) {
                        generatedFiles = parsed.updatedFiles
                    }
                } else {
                    addConsoleLog("API Notice: ${apiResponse.error}")
                }
            }

            // Step 3: If no API key or remote failed, use intelligent dynamic scaffolder
            if (generatedFiles == null || generatedFiles.isEmpty()) {
                _loadingStatus.value = "Synthesizing custom project files on CPU..."
                generatedFiles = withContext(Dispatchers.Default) {
                    Orchestrator.scaffoldDynamicProject(translation)
                }
            }

            // Step 4: Save files into device ROM (Room Database)
            val fileEntities = generatedFiles.map { (name, content) ->
                val ext = name.substringAfterLast('.', "txt")
                ProjectFileEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    fileName = name,
                    fileType = ext,
                    content = content
                )
            } + ProjectFileEntity(
                id = UUID.randomUUID().toString(),
                projectId = projectId,
                fileName = "README.md",
                fileType = "markdown",
                content = "# $title\n\nGenerated with Hazon AI Mobile Studio.\n\n- Detected Language: ${translation.detectedLanguage}\n- Translated Specification: ${translation.translatedPrompt}\n- Model: $model ($mode mode)\n"
            )

            dao.insertFiles(fileEntities)

            _thinkingState.value = _thinkingState.value.copy(
                isThinking = false,
                activeThought = "Project successfully built. Code files synced with live Preview.",
                tasks = listOf(
                    OrchestratorTask("Detect Language & Extract User Intent", isCompleted = true),
                    OrchestratorTask("Translate to Technical Specification", isCompleted = true),
                    OrchestratorTask("Dispatch Master Directive to AI Model ($model)", isCompleted = true),
                    OrchestratorTask("Parse CodeBlocks & Map into Project Files", isCompleted = true),
                    OrchestratorTask("Save to Device ROM (SQLite) & Hot-Reload Preview", isCompleted = true)
                )
            )

            // Step 5: Log the Orchestrator's execution plan in Discussion
            val langLabel = if (translation.detectedLanguage == "Bengali") "বাংলা (Bengali)" else translation.detectedLanguage
            val welcomeMsg = """
                🤖 **হাজোন এআই অর্কেস্ট্রেটর (Hazon AI Brain):**
                
                🌐 **শনাক্তকৃত ভাষা:** $langLabel
                🎯 **অনূদিত টেকনিক্যাল স্পেসিফিকেশন:**
                *"${translation.translatedPrompt}"*
                
                📋 **মেইন এআই এজেন্টের প্রতি ডিরেক্টিভ:**
                ${translation.technicalDirective}
                
                📁 **প্রস্তুতকৃত ফাইলসমূহ:**
                ${fileEntities.joinToString("\n") { "• `${it.fileName}` (${it.fileType.uppercase()})" }}
                
                ⚡ **ডিভাইস রিসোর্স:** ${hardwareStats.value.cpuCores} CPU Cores · ${hardwareStats.value.availableRamMb} MB RAM মুক্ত · SQLite ROM স্টোরেজ সক্রিয়।
            """.trimIndent()

            dao.insertMessage(
                ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = projectId,
                    role = "assistant",
                    content = welcomeMsg
                )
            )

            _isLoading.value = false
            loadProject(projectId)
            showToast("Project created with Hazon AI Brain.")
        }
    }

    fun loadProject(projectId: String) {
        viewModelScope.launch {
            val project = dao.getProjectById(projectId)
            if (project != null) {
                _currentProject.value = project
                _workspaceTab.value = WorkspaceTab.DISCUSSION
                _currentView.value = HazonView.WORKSPACE

                // Observe files for this project
                launch {
                    dao.getFilesForProject(projectId).collectLatest { files ->
                        _projectFiles.value = files
                        if (_activeFile.value == null && files.isNotEmpty()) {
                            setActiveFile(files.first())
                        } else if (_activeFile.value != null) {
                            val updated = files.find { it.id == _activeFile.value?.id }
                            if (updated != null) {
                                _activeFile.value = updated
                                _activeFileContent.value = updated.content
                            }
                        }
                    }
                }

                // Observe messages
                launch {
                    dao.getMessagesForProject(projectId).collectLatest { msgs ->
                        _chatMessages.value = msgs
                    }
                }
            } else {
                showToast("Project not found.")
            }
        }
    }

    fun setActiveFile(file: ProjectFileEntity) {
        _activeFile.value = file
        _activeFileContent.value = file.content
    }

    fun updateActiveFileContent(newContent: String) {
        _activeFileContent.value = newContent
    }

    fun saveActiveFile() {
        val file = _activeFile.value ?: return
        val updated = file.copy(
            content = _activeFileContent.value,
            updatedAt = System.currentTimeMillis()
        )
        viewModelScope.launch {
            dao.insertFile(updated)
            _activeFile.value = updated
            showToast("Saved ${updated.fileName}")
        }
    }

    fun addNewFile(fileName: String) {
        val project = _currentProject.value ?: return
        val ext = fileName.substringAfterLast('.', "txt")
        val newFile = ProjectFileEntity(
            id = UUID.randomUUID().toString(),
            projectId = project.id,
            fileName = fileName,
            fileType = ext,
            content = "// New file: $fileName\n"
        )
        viewModelScope.launch {
            dao.insertFile(newFile)
            setActiveFile(newFile)
            showToast("Added $fileName")
        }
    }

    fun deleteFile(fileId: String) {
        viewModelScope.launch {
            dao.deleteFileById(fileId)
            if (_activeFile.value?.id == fileId) {
                _activeFile.value = null
                _activeFileContent.value = ""
            }
            showToast("File deleted.")
        }
    }

    fun deleteProject(projectId: String) {
        viewModelScope.launch {
            dao.deleteFilesForProject(projectId)
            dao.deleteMessagesForProject(projectId)
            dao.deleteProjectById(projectId)
            if (_currentProject.value?.id == projectId) {
                _currentProject.value = null
                _currentView.value = HazonView.BUILDER
            }
            showToast("Project deleted.")
        }
    }

    fun sendDiscussionMessage(userText: String) {
        if (userText.isBlank()) return
        val project = _currentProject.value ?: return

        val userMsg = ChatMessageEntity(
            id = UUID.randomUUID().toString(),
            projectId = project.id,
            role = "user",
            content = userText
        )

        viewModelScope.launch {
            dao.insertMessage(userMsg)

            // Step 1: Thinking process starts
            _thinkingState.value = ThinkingState(
                isThinking = true,
                detectedLanguage = "Detecting...",
                translatedIntent = "Translating...",
                activeThought = "Analyzing discussion prompt and planning targeted code changes...",
                tasks = listOf(
                    OrchestratorTask("Detect Language & Extract User Intent", isRunning = true),
                    OrchestratorTask("Translate to Technical Specification"),
                    OrchestratorTask("Send Directive to AI Agent (${project.model})"),
                    OrchestratorTask("Parse & Map CodeBlocks to Files"),
                    OrchestratorTask("Save to SQLite ROM & Hot-Reload Preview")
                )
            )

            val currentFilesMap = _projectFiles.value.associate { it.fileName to it.content }
            val orchestratorReq = Orchestrator.preparePromptForMainAi(userText, currentFilesMap)
            val translation = orchestratorReq.translation

            _thinkingState.value = _thinkingState.value.copy(
                detectedLanguage = translation.detectedLanguage,
                translatedIntent = translation.translatedPrompt,
                activeThought = "Translating user intent. Querying model for targeted file diffs...",
                tasks = listOf(
                    OrchestratorTask("Detect Language & Extract User Intent", isCompleted = true),
                    OrchestratorTask("Translate to Technical Specification", isCompleted = true),
                    OrchestratorTask("Send Directive to AI Agent (${project.model})", isRunning = true),
                    OrchestratorTask("Parse & Map CodeBlocks to Files"),
                    OrchestratorTask("Save to SQLite ROM & Hot-Reload Preview")
                )
            )

            val settings = _apiSettings.value
            val resolved = resolveApiConfiguration(project.model, settings)

            var replyContent = ""
            var codeApplied = false

            // Step 2: Call AI API (Gemini, DeepSeek, GLM, etc.)
            if (resolved.apiKey.isNotBlank()) {
                val apiResponse = if (resolved.isGemini) {
                    apiService.sendGeminiCompletion(
                        apiKey = resolved.apiKey,
                        model = resolved.modelName,
                        systemPrompt = orchestratorReq.systemPrompt,
                        userPrompt = orchestratorReq.userPromptForAi,
                        endpointUrl = resolved.endpoint
                    )
                } else {
                    val apiMessages = _chatMessages.value.takeLast(4).map {
                        ChatApiMessage(role = it.role, content = it.content)
                    } + ChatApiMessage(role = "user", content = orchestratorReq.userPromptForAi)

                    apiService.sendChatCompletion(
                        endpointUrl = resolved.endpoint,
                        apiKey = resolved.apiKey,
                        model = resolved.modelName,
                        messages = apiMessages,
                        systemPrompt = orchestratorReq.systemPrompt
                    )
                }

                if (apiResponse.success && apiResponse.content.isNotBlank()) {
                    val parsed = Orchestrator.parseAndApplyAiResponse(
                        apiResponse.content,
                        translation.detectedLanguage
                    )

                    // Automatically update files in Room DB!
                    if (parsed.updatedFiles.isNotEmpty()) {
                        applyUpdatedFiles(project.id, parsed.updatedFiles)
                        codeApplied = true
                    }
                    replyContent = parsed.explanation + "\n\n" + apiResponse.content
                } else {
                    replyContent = "⚠️ Network/API Notice: ${apiResponse.error ?: "Endpoint unavailable"}.\n\n"
                }
            }

            // Step 3: Local intelligent fallback and code update if no API key or API call didn't update files
            if (!codeApplied) {
                val localResult = applyLocalCodeModifications(project.id, userText, translation)
                replyContent += localResult
            }

            _thinkingState.value = _thinkingState.value.copy(
                isThinking = false,
                activeThought = "Execution completed. Files and preview synchronized.",
                tasks = listOf(
                    OrchestratorTask("Detect Language & Extract User Intent", isCompleted = true),
                    OrchestratorTask("Translate to Technical Specification", isCompleted = true),
                    OrchestratorTask("Send Directive to AI Agent (${project.model})", isCompleted = true),
                    OrchestratorTask("Parse & Map CodeBlocks to Files", isCompleted = true),
                    OrchestratorTask("Save to SQLite ROM & Hot-Reload Preview", isCompleted = true)
                )
            )

            dao.insertMessage(
                ChatMessageEntity(
                    id = UUID.randomUUID().toString(),
                    projectId = project.id,
                    role = "assistant",
                    content = replyContent
                )
            )
        }
    }

    private suspend fun applyUpdatedFiles(projectId: String, files: Map<String, String>) {
        files.forEach { (fileName, newContent) ->
            val existing = dao.getFileByName(projectId, fileName)
            if (existing != null) {
                dao.insertFile(existing.copy(content = newContent, updatedAt = System.currentTimeMillis()))
            } else {
                val ext = fileName.substringAfterLast('.', "txt")
                dao.insertFile(
                    ProjectFileEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        fileName = fileName,
                        fileType = ext,
                        content = newContent
                    )
                )
            }
        }
        addConsoleLog("Orchestrator hot-reloaded files: ${files.keys.joinToString(", ")}")
    }

    private suspend fun applyLocalCodeModifications(
        projectId: String,
        userText: String,
        translation: TranslationResult
    ): String {
        val lower = userText.lowercase()
        val stylesFile = dao.getFileByName(projectId, "styles.css")
        val jsFile = dao.getFileByName(projectId, "app.js")

        return when {
            lower.contains("কালার") || lower.contains("রং") || lower.contains("রঙ") || lower.contains("color") || lower.contains("purple") -> {
                if (stylesFile != null) {
                    val updatedCss = stylesFile.content + "\n/* Hazon AI Dynamic Update */\nbutton, .cta-btn, .primary-btn { background: #8b5cf6 !important; color: white !important; box-shadow: 0 0 16px rgba(139,92,246,0.5) !important; }\n"
                    dao.insertFile(stylesFile.copy(content = updatedCss))
                    addConsoleLog("Updated styles.css with custom color rules.")
                }
                "🤖 **হাজোন এআই অর্কেস্ট্রেটর:**\nআপনার প্রম্পট (\"$userText\") অনুযায়ী `styles.css` ফাইলে বাটনের কালার ও গ্লো ইফেক্ট পরিবর্তন করা হয়েছে! **Preview** ট্যাবে গিয়ে চেক করুন।"
            }

            lower.contains("বাটন") && (lower.contains("ক্লিক") || lower.contains("action") || lower.contains("বার্তা")) -> {
                if (jsFile != null) {
                    val updatedJs = jsFile.content + "\n// Added by Hazon AI Orchestrator\nfunction onCustomClick() { alert('Hazon AI: আপনার ক্লিক গৃহীত হয়েছে!'); }\n"
                    dao.insertFile(jsFile.copy(content = updatedJs))
                }
                "🤖 **হাজোন এআই অর্কেস্ট্রেটর:**\n`app.js` ফাইলে ক্লিক ইভেন্ট হ্যান্ডলার যুক্ত করা হয়েছে।"
            }

            else -> {
                val isBn = translation.detectedLanguage == "Bengali"
                if (isBn) {
                    "🤖 **হাজোন এআই অর্কেস্ট্রেটর:**\n" +
                    "আপনার বাংলা প্রম্পটটি বিশ্লেষণ করা হয়েছে: *\"${translation.translatedPrompt}\"*\n" +
                    "লক্ষ্য ফাইল: `index.html`, `styles.css`, `app.js`। আপনি সরাসরি **Files** ট্যাবে কোড পরিবর্তন করতে পারেন অথবা সেটিংস থেকে DeepSeek/GLM/Gemini API কি যুক্ত করে সম্পূর্ণ এআই পাওয়ার উপভোগ করতে পারেন।"
                } else {
                    "🤖 **Hazon AI Orchestrator:**\n" +
                    "Directive analyzed: *\"${translation.translatedPrompt}\"*\n" +
                    "Targeted code files: `index.html`, `styles.css`, `app.js`. You can edit directly in **Files** tab or enter API keys in **Settings**."
                }
            }
        }
    }

    fun compilePreviewHtml(): String {
        val files = _projectFiles.value
        val htmlFile = files.find { it.fileName == "index.html" } ?: files.find { it.fileType == "html" }
        val cssFile = files.find { it.fileName == "styles.css" } ?: files.find { it.fileType == "css" }
        val jsFile = files.find { it.fileName == "app.js" } ?: files.find { it.fileType == "js" }

        val rawHtml = htmlFile?.content ?: "<html><body style='background:#090911;color:#fff;font-family:sans-serif;padding:20px'><h1>Hazon AI</h1><p>No index.html found.</p></body></html>"
        val rawCss = cssFile?.content ?: ""
        val rawJs = jsFile?.content ?: ""

        val withCss = if (rawCss.isNotBlank()) {
            if (rawHtml.contains("</head>")) {
                rawHtml.replace("</head>", "<style>\n$rawCss\n</style>\n</head>")
            } else {
                "<style>\n$rawCss\n</style>\n$rawHtml"
            }
        } else {
            rawHtml
        }

        val fullHtml = if (rawJs.isNotBlank()) {
            if (withCss.contains("</body>")) {
                withCss.replace("</body>", "<script>\n$rawJs\n</script>\n</body>")
            } else {
                "$withCss\n<script>\n$rawJs\n</script>"
            }
        } else {
            withCss
        }

        return fullHtml
    }

    fun addCustomModel(name: String, endpointUrl: String, apiKey: String, modelTag: String) {
        viewModelScope.launch {
            val custom = com.example.data.local.entity.CustomModelEntity(
                id = UUID.randomUUID().toString(),
                name = name.trim(),
                endpointUrl = endpointUrl.trim(),
                apiKey = apiKey.trim(),
                modelTag = modelTag.trim()
            )
            dao.insertCustomModel(custom)
            showToast("Added model: ${custom.name}")
        }
    }

    fun deleteCustomModel(id: String) {
        viewModelScope.launch {
            dao.deleteCustomModelById(id)
            showToast("Model deleted.")
        }
    }

    fun testCustomModelConnection(
        endpointUrl: String,
        apiKey: String,
        modelTag: String,
        onResult: (ApiResponse) -> Unit
    ) {
        viewModelScope.launch {
            val res = apiService.testConnection(
                endpointUrl = endpointUrl,
                apiKey = apiKey,
                model = if (modelTag.isNotBlank()) modelTag else "default"
            )
            onResult(res)
        }
    }

    fun saveApiSettings(settings: ApiSettingsEntity) {
        viewModelScope.launch {
            dao.insertOrUpdateSettings(settings)
            _apiSettings.value = settings
            showToast("Settings saved to local storage.")
        }
    }

    fun testApiConnection(provider: String, onResult: (ApiResponse) -> Unit) {
        viewModelScope.launch {
            val settings = _apiSettings.value
            val res = when (provider) {
                "gemini" -> {
                    val key = resolveGeminiKey(settings)
                    if (key.isBlank()) {
                        ApiResponse(false, "", "No Gemini API Key provided. Enter key in Settings or AI Studio Secrets.")
                    } else {
                        apiService.testGeminiConnection(key, settings.geminiModel, settings.geminiEndpoint)
                    }
                }
                "deepseek" -> apiService.testConnection(
                    settings.deepSeekEndpoint,
                    settings.deepSeekKey,
                    settings.deepSeekModel
                )
                "glm" -> apiService.testConnection(
                    settings.glmEndpoint,
                    settings.glmKey,
                    settings.glmModel
                )
                "qwen" -> apiService.testConnection(
                    settings.qwenEndpoint,
                    settings.qwenKey,
                    settings.qwenModel
                )
                "qwen_image" -> apiService.testConnection(
                    settings.qwenImageEndpoint,
                    settings.qwenImageKey.ifBlank { settings.qwenKey },
                    settings.qwenImageModel
                )
                "zai" -> apiService.testConnection(
                    settings.zaiEndpoint,
                    settings.zaiKey,
                    settings.zaiModel
                )
                "custom" -> apiService.testConnection(
                    settings.customEndpoint,
                    settings.customKey,
                    "custom"
                )
                else -> ApiResponse(false, "", "Unknown provider")
            }
            onResult(res)
        }
    }

    fun importTemplate(template: ProjectTemplate) {
        createNewProjectFromPrompt(template.prompt)
    }

    fun importProjectFromZip(uri: Uri, context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingStatus.value = "Reading & unzipping project files..."
            try {
                val extractedFiles = mutableMapOf<String, String>()
                var guessedProjectName = "Imported Web Project"

                // Extract filename from Uri if possible
                try {
                    val cursor = context.contentResolver.query(uri, null, null, null, null)
                    cursor?.use {
                        if (it.moveToFirst()) {
                            val nameIndex = it.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
                            if (nameIndex >= 0) {
                                val fileName = it.getString(nameIndex)
                                if (!fileName.isNullOrBlank()) {
                                    guessedProjectName = fileName
                                        .removeSuffix(".zip")
                                        .replace('-', ' ')
                                        .replace('_', ' ')
                                        .split(" ")
                                        .filter { w -> w.isNotBlank() }
                                        .joinToString(" ") { w ->
                                            w.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() }
                                        }
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    // Ignore cursor errors
                }

                withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        java.util.zip.ZipInputStream(inputStream).use { zipStream ->
                            var entry = zipStream.nextEntry
                            while (entry != null) {
                                val entryName = entry.name
                                val isMacOs = entryName.contains("__MACOSX") || entryName.startsWith(".")
                                val isGit = entryName.contains(".git/")
                                if (!entry.isDirectory && !isMacOs && !isGit) {
                                    val targetName = entryName.substringAfterLast('/')
                                    val ext = targetName.substringAfterLast('.', "").lowercase()
                                    val isCode = ext in listOf("html", "htm", "css", "js", "json", "ts", "jsx", "tsx", "svg", "txt", "md")
                                    if (isCode && targetName.isNotBlank()) {
                                        val content = zipStream.bufferedReader(Charsets.UTF_8).readText()
                                        extractedFiles[targetName] = content
                                    }
                                }
                                zipStream.closeEntry()
                                entry = zipStream.nextEntry
                            }
                        }
                    }
                }

                if (extractedFiles.isEmpty()) {
                    showToast("No valid web files (.html, .css, .js, etc.) found in the ZIP archive.")
                    _isLoading.value = false
                    return@launch
                }

                if (!extractedFiles.containsKey("index.html")) {
                    val firstHtml = extractedFiles.keys.find { it.endsWith(".html") }
                    if (firstHtml != null) {
                        extractedFiles["index.html"] = extractedFiles[firstHtml]!!
                    } else {
                        extractedFiles["index.html"] = "<!DOCTYPE html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <title>$guessedProjectName</title>\n  <link rel=\"stylesheet\" href=\"styles.css\">\n</head>\n<body>\n  <h1>$guessedProjectName</h1>\n  <p>Imported project workspace.</p>\n  <script src=\"app.js\"></script>\n</body>\n</html>"
                    }
                }

                val projectId = UUID.randomUUID().toString()
                val project = ProjectEntity(
                    id = projectId,
                    title = guessedProjectName,
                    prompt = "Imported from local ZIP archive ($guessedProjectName)",
                    model = _selectedModel.value,
                    mode = "Full-Stack Web"
                )
                dao.insertProject(project)

                val fileEntities = extractedFiles.map { (fName, content) ->
                    val ext = fName.substringAfterLast('.', "txt")
                    ProjectFileEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        fileName = fName,
                        fileType = ext,
                        content = content
                    )
                }
                dao.insertFiles(fileEntities)

                _currentProject.value = project
                _projectFiles.value = fileEntities
                val chosenFile = fileEntities.find { it.fileName == "index.html" } ?: fileEntities.firstOrNull()
                _activeFile.value = chosenFile
                _activeFileContent.value = chosenFile?.content ?: ""
                _chatMessages.value = listOf(
                    ChatMessageEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        role = "assistant",
                        content = "📦 **Project Imported Successfully!**\n\nExtracted ${fileEntities.size} files into SQLite ROM database:\n" +
                                fileEntities.joinToString("\n") { "• `${it.fileName}` (${it.content.length} chars)" } +
                                "\n\nYou can chat with Hazon AI in any language (বাংলা, English, etc.) to modify this project or view the live preview."
                    )
                )
                _currentView.value = HazonView.WORKSPACE
                _workspaceTab.value = WorkspaceTab.PREVIEW
                showToast("Successfully imported $guessedProjectName (${fileEntities.size} files)!")
            } catch (e: Exception) {
                showToast("Import failed: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importProjectFromGitHub(repoUrlOrSlug: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _loadingStatus.value = "Fetching repository from GitHub..."
            try {
                val cleanSlug = repoUrlOrSlug.trim()
                    .removePrefix("https://github.com/")
                    .removePrefix("http://github.com/")
                    .removeSuffix(".git")
                    .trim('/')

                val parts = cleanSlug.split('/')
                if (parts.size < 2) {
                    showToast("Please enter a valid GitHub repository (e.g. owner/repo).")
                    _isLoading.value = false
                    return@launch
                }
                val owner = parts[0]
                val repo = parts[1]
                val projectName = repo
                    .replace('-', ' ')
                    .replace('_', ' ')
                    .split(" ")
                    .filter { it.isNotBlank() }
                    .joinToString(" ") { w ->
                        w.replaceFirstChar { c -> if (c.isLowerCase()) c.titlecase() else c.toString() }
                    }

                val extractedFiles = mutableMapOf<String, String>()

                withContext(Dispatchers.IO) {
                    val zipUrl = "https://api.github.com/repos/$owner/$repo/zipball/main"
                    val request = Request.Builder()
                        .url(zipUrl)
                        .addHeader("User-Agent", "Hazon-AI-Studio-Android")
                        .build()

                    var response = apiService.client.newCall(request).execute()
                    if (!response.isSuccessful) {
                        val masterZipUrl = "https://api.github.com/repos/$owner/$repo/zipball/master"
                        val masterReq = Request.Builder()
                            .url(masterZipUrl)
                            .addHeader("User-Agent", "Hazon-AI-Studio-Android")
                            .build()
                        response = apiService.client.newCall(masterReq).execute()
                    }

                    if (response.isSuccessful) {
                        response.body?.byteStream()?.use { stream ->
                            java.util.zip.ZipInputStream(stream).use { zipStream ->
                                var entry = zipStream.nextEntry
                                while (entry != null) {
                                    val entryName = entry.name
                                    val isMacOs = entryName.contains("__MACOSX") || entryName.startsWith(".")
                                    val isGit = entryName.contains(".git/")
                                    if (!entry.isDirectory && !isMacOs && !isGit) {
                                        val targetName = entryName.substringAfterLast('/')
                                        val ext = targetName.substringAfterLast('.', "").lowercase()
                                        val isCode = ext in listOf("html", "htm", "css", "js", "json", "ts", "jsx", "tsx", "svg", "txt", "md")
                                        if (isCode && targetName.isNotBlank()) {
                                            val content = zipStream.bufferedReader(Charsets.UTF_8).readText()
                                            extractedFiles[targetName] = content
                                        }
                                    }
                                    zipStream.closeEntry()
                                    entry = zipStream.nextEntry
                                }
                            }
                        }
                    }
                }

                if (extractedFiles.isEmpty()) {
                    showToast("Downloaded repository or created starter scaffolding from GitHub $cleanSlug")
                    val scaffolded = Orchestrator.scaffoldDynamicProject(
                        TranslationResult(
                            detectedLanguage = "English",
                            originalPrompt = "Cloned from GitHub $cleanSlug",
                            translatedPrompt = "Web application cloned from GitHub repository $cleanSlug",
                            technicalDirective = "Build full-stack web application structure with index.html, styles.css, app.js",
                            targetFiles = listOf("index.html", "styles.css", "app.js")
                        )
                    )
                    extractedFiles.putAll(scaffolded)
                }

                if (!extractedFiles.containsKey("index.html")) {
                    extractedFiles["index.html"] = "<!DOCTYPE html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <title>$projectName</title>\n  <link rel=\"stylesheet\" href=\"styles.css\">\n</head>\n<body>\n  <h1>$projectName</h1>\n  <p>Cloned from GitHub: $owner/$repo</p>\n  <script src=\"app.js\"></script>\n</body>\n</html>"
                }

                val projectId = UUID.randomUUID().toString()
                val project = ProjectEntity(
                    id = projectId,
                    title = projectName,
                    prompt = "Cloned from GitHub: $owner/$repo",
                    model = _selectedModel.value,
                    mode = "Full-Stack Web"
                )
                dao.insertProject(project)

                val fileEntities = extractedFiles.map { (fName, content) ->
                    val ext = fName.substringAfterLast('.', "txt")
                    ProjectFileEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        fileName = fName,
                        fileType = ext,
                        content = content
                    )
                }
                dao.insertFiles(fileEntities)

                _currentProject.value = project
                _projectFiles.value = fileEntities
                val chosenGitFile = fileEntities.find { it.fileName == "index.html" } ?: fileEntities.firstOrNull()
                _activeFile.value = chosenGitFile
                _activeFileContent.value = chosenGitFile?.content ?: ""
                _chatMessages.value = listOf(
                    ChatMessageEntity(
                        id = UUID.randomUUID().toString(),
                        projectId = projectId,
                        role = "assistant",
                        content = "🐙 **GitHub Project Cloned!**\n\nCloned `$owner/$repo` into local SQLite ROM storage.\n" +
                                "Files extracted:\n" +
                                fileEntities.joinToString("\n") { "• `${it.fileName}`" } +
                                "\n\nYou can chat with Hazon AI to modify any file, add features, or view live output."
                    )
                )
                _currentView.value = HazonView.WORKSPACE
                _workspaceTab.value = WorkspaceTab.PREVIEW
                showToast("Successfully cloned $projectName (${fileEntities.size} files)!")
            } catch (e: Exception) {
                showToast("GitHub clone error: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun importProjectFromRawCode(projectName: String, html: String, css: String = "", js: String = "") {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val title = projectName.ifBlank { "Custom Web Project" }
                val projectId = UUID.randomUUID().toString()
                val project = ProjectEntity(
                    id = projectId,
                    title = title,
                    prompt = "Imported raw code project ($title)",
                    model = _selectedModel.value,
                    mode = "Full-Stack Web"
                )
                dao.insertProject(project)

                val files = mutableListOf<ProjectFileEntity>()
                val indexHtml = html.ifBlank {
                    "<!DOCTYPE html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <title>$title</title>\n  <link rel=\"stylesheet\" href=\"styles.css\">\n</head>\n<body>\n  <h1>$title</h1>\n  <script src=\"app.js\"></script>\n</body>\n</html>"
                }
                files.add(ProjectFileEntity(id = UUID.randomUUID().toString(), projectId = projectId, fileName = "index.html", fileType = "html", content = indexHtml))

                if (css.isNotBlank()) {
                    files.add(ProjectFileEntity(id = UUID.randomUUID().toString(), projectId = projectId, fileName = "styles.css", fileType = "css", content = css))
                } else {
                    files.add(ProjectFileEntity(id = UUID.randomUUID().toString(), projectId = projectId, fileName = "styles.css", fileType = "css", content = "/* Styles for $title */\nbody { font-family: system-ui, sans-serif; margin: 0; padding: 20px; background: #0f172a; color: #f8fafc; }"))
                }

                if (js.isNotBlank()) {
                    files.add(ProjectFileEntity(id = UUID.randomUUID().toString(), projectId = projectId, fileName = "app.js", fileType = "js", content = js))
                } else {
                    files.add(ProjectFileEntity(id = UUID.randomUUID().toString(), projectId = projectId, fileName = "app.js", fileType = "js", content = "// App logic for $title\nconsole.log('$title loaded');"))
                }

                dao.insertFiles(files)

                _currentProject.value = project
                _projectFiles.value = files
                _activeFile.value = files.firstOrNull()
                _activeFileContent.value = files.firstOrNull()?.content ?: ""
                _currentView.value = HazonView.WORKSPACE
                _workspaceTab.value = WorkspaceTab.PREVIEW
                showToast("Created $title with ${files.size} files!")
            } catch (e: Exception) {
                showToast("Failed to create project: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private data class ResolvedApi(
        val isGemini: Boolean,
        val apiKey: String,
        val endpoint: String,
        val modelName: String,
        val providerName: String
    )

    private fun resolveGeminiKey(settings: ApiSettingsEntity): String {
        return if (settings.geminiKey.isNotBlank()) {
            settings.geminiKey
        } else {
            val injected = try {
                BuildConfig.GEMINI_API_KEY
            } catch (e: Throwable) {
                ""
            }
            if (injected.isNotBlank() && injected != "MY_GEMINI_API_KEY") injected else ""
        }
    }

    private fun resolveApiConfiguration(
        chosenModel: String,
        settings: ApiSettingsEntity
    ): ResolvedApi {
        val geminiKey = resolveGeminiKey(settings)
        val customModel = _customModels.value.find { it.id == chosenModel || it.modelTag == chosenModel }

        return when {
            // Check dynamically added custom models first
            customModel != null -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = customModel.apiKey,
                    endpoint = customModel.endpointUrl,
                    modelName = customModel.modelTag,
                    providerName = customModel.name
                )
            }
            // 1. Explicit Gemini or Auto with Gemini available
            (chosenModel == "gemini" || chosenModel == "auto") && geminiKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = true,
                    apiKey = geminiKey,
                    endpoint = settings.geminiEndpoint,
                    modelName = settings.geminiModel,
                    providerName = "Google AI Studio (Gemini)"
                )
            }
            // 2. DeepSeek
            chosenModel == "deepseek" && settings.deepSeekKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.deepSeekKey,
                    endpoint = settings.deepSeekEndpoint,
                    modelName = settings.deepSeekModel,
                    providerName = "DeepSeek"
                )
            }
            // 3. GLM
            chosenModel == "glm" && settings.glmKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.glmKey,
                    endpoint = settings.glmEndpoint,
                    modelName = settings.glmModel,
                    providerName = "GLM (Zhipu AI)"
                )
            }
            // 4. Qwen (Language & Code)
            chosenModel == "qwen" && settings.qwenKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.qwenKey,
                    endpoint = settings.qwenEndpoint,
                    modelName = settings.qwenModel,
                    providerName = "Qwen"
                )
            }
            // 4b. Qwen Image / Vision (Alibaba Cloud DashScope)
            (chosenModel == "qwen_image" || chosenModel == "qwen-image") && (settings.qwenImageKey.isNotBlank() || settings.qwenKey.isNotBlank()) -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.qwenImageKey.ifBlank { settings.qwenKey },
                    endpoint = settings.qwenImageEndpoint,
                    modelName = settings.qwenImageModel,
                    providerName = "Qwen Image (Vision & Media)"
                )
            }
            // 5. Z.ai
            chosenModel == "zai" && settings.zaiKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.zaiKey,
                    endpoint = settings.zaiEndpoint,
                    modelName = settings.zaiModel,
                    providerName = "Z.ai"
                )
            }
            // 6. Custom generic
            chosenModel == "custom" && settings.customKey.isNotBlank() && settings.customEndpoint.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.customKey,
                    endpoint = settings.customEndpoint,
                    modelName = "custom",
                    providerName = "Custom Endpoint"
                )
            }
            // Fallbacks if any key is provided
            geminiKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = true,
                    apiKey = geminiKey,
                    endpoint = settings.geminiEndpoint,
                    modelName = settings.geminiModel,
                    providerName = "Google AI Studio (Gemini)"
                )
            }
            settings.deepSeekKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.deepSeekKey,
                    endpoint = settings.deepSeekEndpoint,
                    modelName = settings.deepSeekModel,
                    providerName = "DeepSeek"
                )
            }
            settings.glmKey.isNotBlank() -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = settings.glmKey,
                    endpoint = settings.glmEndpoint,
                    modelName = settings.glmModel,
                    providerName = "GLM (Zhipu AI)"
                )
            }
            else -> {
                ResolvedApi(
                    isGemini = false,
                    apiKey = "",
                    endpoint = "",
                    modelName = "",
                    providerName = "Offline / Local Engine"
                )
            }
        }
    }
}
