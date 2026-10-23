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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.ApiSettingsEntity
import com.example.data.remote.ApiResponse
import com.example.hardware.HardwareStats
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBlue
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonCyan
import com.example.ui.theme.HazonGreen
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted

import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import com.example.data.local.entity.CustomModelEntity

@Composable
fun ModelsSettingsScreen(
    settings: ApiSettingsEntity,
    hardwareStats: HardwareStats,
    customModels: List<CustomModelEntity> = emptyList(),
    onSaveSettings: (ApiSettingsEntity) -> Unit,
    onTestConnection: (String, (ApiResponse) -> Unit) -> Unit,
    onAddCustomModel: (String, String, String, String) -> Unit = { _, _, _, _ -> },
    onDeleteCustomModel: (String) -> Unit = {},
    onTestCustomModel: (String, String, String, (ApiResponse) -> Unit) -> Unit = { _, _, _, _ -> },
    modifier: Modifier = Modifier
) {
    var geminiKey by remember(settings) { mutableStateOf(settings.geminiKey) }
    var geminiModel by remember(settings) { mutableStateOf(settings.geminiModel) }
    var geminiEndpoint by remember(settings) { mutableStateOf(settings.geminiEndpoint) }

    var deepSeekKey by remember(settings) { mutableStateOf(settings.deepSeekKey) }
    var deepSeekModel by remember(settings) { mutableStateOf(settings.deepSeekModel) }
    var deepSeekEndpoint by remember(settings) { mutableStateOf(settings.deepSeekEndpoint) }

    var glmKey by remember(settings) { mutableStateOf(settings.glmKey) }
    var glmModel by remember(settings) { mutableStateOf(settings.glmModel) }
    var glmEndpoint by remember(settings) { mutableStateOf(settings.glmEndpoint) }

    var qwenKey by remember(settings) { mutableStateOf(settings.qwenKey) }
    var qwenModel by remember(settings) { mutableStateOf(settings.qwenModel) }
    var qwenEndpoint by remember(settings) { mutableStateOf(settings.qwenEndpoint) }

    var qwenImageKey by remember(settings) { mutableStateOf(settings.qwenImageKey) }
    var qwenImageModel by remember(settings) { mutableStateOf(settings.qwenImageModel) }
    var qwenImageEndpoint by remember(settings) { mutableStateOf(settings.qwenImageEndpoint) }

    var zaiKey by remember(settings) { mutableStateOf(settings.zaiKey) }
    var zaiModel by remember(settings) { mutableStateOf(settings.zaiModel) }
    var zaiEndpoint by remember(settings) { mutableStateOf(settings.zaiEndpoint) }

    var customEndpoint by remember(settings) { mutableStateOf(settings.customEndpoint) }
    var customKey by remember(settings) { mutableStateOf(settings.customKey) }

    // New custom model input fields
    var newModelName by remember { mutableStateOf("") }
    var newModelEndpoint by remember { mutableStateOf("") }
    var newModelKey by remember { mutableStateOf("") }
    var newModelTag by remember { mutableStateOf("") }
    var showNewModelForm by remember { mutableStateOf(false) }

    var testingProvider by remember { mutableStateOf<String?>(null) }
    var testResult by remember { mutableStateOf<Pair<String, String>?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HazonBackground)
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        item {
            Text(
                text = "AI Models & Endpoints",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HazonText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "যেকোনো AI Endpoint (AI Studio, GLM, DeepSeek, Qwen image, Z.ai ইত্যাদি) ইচ্ছামতো পরিবর্তন করুন। Custom Proxy বা Local সার্ভারও ব্যবহার করতে পারেন।",
                fontSize = 12.sp,
                color = HazonTextMuted
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // 1. Google AI Studio (Gemini) Card - ACTIVE TESTING PROVIDER
        item {
            ProviderSettingsCard(
                icon = Icons.Default.AutoAwesome,
                name = "Google AI Studio (Gemini)",
                purpose = "Active Engine · Multimodal code generation & reasoning",
                endpoint = geminiEndpoint,
                onEndpointChange = { geminiEndpoint = it },
                defaultEndpoint = ApiSettingsEntity.DEFAULT_GEMINI_ENDPOINT,
                apiKey = geminiKey,
                onApiKeyChange = { geminiKey = it },
                model = geminiModel,
                onModelChange = { geminiModel = it },
                placeholder = "AIzaSy... (or auto-detect from AI Studio)",
                isTesting = testingProvider == "gemini",
                isDefaultActive = true,
                onTestClick = {
                    testingProvider = "gemini"
                    onTestConnection("gemini") { res ->
                        testingProvider = null
                        testResult = "gemini" to if (res.success) "Connected successfully! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                    }
                },
                testFeedback = if (testResult?.first == "gemini") testResult?.second else null
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 2. DeepSeek Card
        item {
            ProviderSettingsCard(
                icon = Icons.Default.Code,
                name = "DeepSeek API",
                purpose = "Technical code synthesis, architecture logic, and debugging",
                endpoint = deepSeekEndpoint,
                onEndpointChange = { deepSeekEndpoint = it },
                defaultEndpoint = ApiSettingsEntity.DEFAULT_DEEPSEEK_ENDPOINT,
                apiKey = deepSeekKey,
                onApiKeyChange = { deepSeekKey = it },
                model = deepSeekModel,
                onModelChange = { deepSeekModel = it },
                placeholder = "sk-...",
                isTesting = testingProvider == "deepseek",
                onTestClick = {
                    testingProvider = "deepseek"
                    onTestConnection("deepseek") { res ->
                        testingProvider = null
                        testResult = "deepseek" to if (res.success) "Connected! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                    }
                },
                testFeedback = if (testResult?.first == "deepseek") testResult?.second else null
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 3. GLM (Zhipu AI) Card
        item {
            ProviderSettingsCard(
                icon = Icons.Default.Bolt,
                name = "GLM (Zhipu AI) API",
                purpose = "General discussion, conversational planning, and step-by-step guidance",
                endpoint = glmEndpoint,
                onEndpointChange = { glmEndpoint = it },
                defaultEndpoint = ApiSettingsEntity.DEFAULT_GLM_ENDPOINT,
                apiKey = glmKey,
                onApiKeyChange = { glmKey = it },
                model = glmModel,
                onModelChange = { glmModel = it },
                placeholder = "glm-key-...",
                isTesting = testingProvider == "glm",
                onTestClick = {
                    testingProvider = "glm"
                    onTestConnection("glm") { res ->
                        testingProvider = null
                        testResult = "glm" to if (res.success) "Connected! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                    }
                },
                testFeedback = if (testResult?.first == "glm") testResult?.second else null
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4. Qwen (Alibaba Cloud / DashScope - Language & Coding) Card
        item {
            ProviderSettingsCard(
                icon = Icons.Default.Code,
                name = "Qwen (Alibaba Cloud / DashScope)",
                purpose = "Full-stack code generation, algorithms, and technical reasoning",
                endpoint = qwenEndpoint,
                onEndpointChange = { qwenEndpoint = it },
                defaultEndpoint = ApiSettingsEntity.DEFAULT_QWEN_ENDPOINT,
                apiKey = qwenKey,
                onApiKeyChange = { qwenKey = it },
                model = qwenModel,
                onModelChange = { qwenModel = it },
                placeholder = "sk-...",
                isTesting = testingProvider == "qwen",
                onTestClick = {
                    testingProvider = "qwen"
                    onTestConnection("qwen") { res ->
                        testingProvider = null
                        testResult = "qwen" to if (res.success) "Connected! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                    }
                },
                testFeedback = if (testResult?.first == "qwen") testResult?.second else null
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 4b. Qwen Image / Vision (Alibaba Cloud DashScope) Card
        item {
            ProviderSettingsCard(
                icon = Icons.Default.Palette,
                name = "Qwen Image (Vision & Media)",
                purpose = "UI asset generation, visual mockups, and multimodal reasoning",
                endpoint = qwenImageEndpoint,
                onEndpointChange = { qwenImageEndpoint = it },
                defaultEndpoint = ApiSettingsEntity.DEFAULT_QWEN_IMAGE_ENDPOINT,
                apiKey = qwenImageKey,
                onApiKeyChange = { qwenImageKey = it },
                model = qwenImageModel,
                onModelChange = { qwenImageModel = it },
                placeholder = "sk-... (leave blank to reuse Qwen key)",
                isTesting = testingProvider == "qwen_image",
                onTestClick = {
                    testingProvider = "qwen_image"
                    onTestConnection("qwen_image") { res ->
                        testingProvider = null
                        testResult = "qwen_image" to if (res.success) "Connected! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                    }
                },
                testFeedback = if (testResult?.first == "qwen_image") testResult?.second else null
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 5. Z.ai Card
        item {
            ProviderSettingsCard(
                icon = Icons.Default.Layers,
                name = "Z.ai Coding API",
                purpose = "Autonomous full-stack web and code scaffolding",
                endpoint = zaiEndpoint,
                onEndpointChange = { zaiEndpoint = it },
                defaultEndpoint = ApiSettingsEntity.DEFAULT_ZAI_ENDPOINT,
                apiKey = zaiKey,
                onApiKeyChange = { zaiKey = it },
                model = zaiModel,
                onModelChange = { zaiModel = it },
                placeholder = "zai-key-...",
                isTesting = testingProvider == "zai",
                onTestClick = {
                    testingProvider = "zai"
                    onTestConnection("zai") { res ->
                        testingProvider = null
                        testResult = "zai" to if (res.success) "Connected! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                    }
                },
                testFeedback = if (testResult?.first == "zai") testResult?.second else null
            )
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 6. Custom Endpoint Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HazonPanel)
                    .border(1.dp, HazonBorder, RoundedCornerShape(14.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(34.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0x2860A5FA))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Dns,
                                contentDescription = null,
                                tint = HazonBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Custom / Self-hosted LLM",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HazonText
                            )
                            Text(
                                text = "OpenAI-compatible v1 chat completions endpoint",
                                fontSize = 11.sp,
                                color = HazonTextMuted
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    OutlinedTextField(
                        value = customEndpoint,
                        onValueChange = { customEndpoint = it },
                        label = { Text("Endpoint URL", fontSize = 11.sp, color = HazonTextMuted) },
                        placeholder = { Text("https://api.openai.com/v1/chat/completions", color = HazonTextDim, fontSize = 12.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HazonText,
                            unfocusedTextColor = HazonText,
                            focusedBorderColor = HazonPurple,
                            unfocusedBorderColor = HazonBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customKey,
                        onValueChange = { customKey = it },
                        label = { Text("Bearer Token / Key (Optional)", fontSize = 11.sp, color = HazonTextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = HazonText,
                            unfocusedTextColor = HazonText,
                            focusedBorderColor = HazonPurple,
                            unfocusedBorderColor = HazonBorder
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // 7. Dynamic Custom Models Section (নতুন মডেল অ্যাড করুন)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HazonPanel)
                    .border(1.dp, HazonPurpleDark, RoundedCornerShape(14.dp))
                    .padding(18.dp)
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
                                    .size(34.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color(0x288B5CF6))
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = HazonPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Add Custom AI Model (নতুন মডেল)",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HazonText
                                )
                                Text(
                                    text = "Connect any custom endpoint & API key",
                                    fontSize = 11.sp,
                                    color = HazonTextMuted
                                )
                            }
                        }

                        Text(
                            text = if (showNewModelForm) "Close" else "+ Add Model",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonPurple,
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0x228B5CF6))
                                .clickable { showNewModelForm = !showNewModelForm }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    if (showNewModelForm) {
                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = newModelName,
                            onValueChange = { newModelName = it },
                            label = { Text("Model Display Name", fontSize = 11.sp, color = HazonTextMuted) },
                            placeholder = { Text("e.g. Claude 3.5 Sonnet, Llama 3 70B, Grok", color = HazonTextDim, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = HazonText,
                                unfocusedTextColor = HazonText,
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newModelEndpoint,
                            onValueChange = { newModelEndpoint = it },
                            label = { Text("API Endpoint URL", fontSize = 11.sp, color = HazonTextMuted) },
                            placeholder = { Text("https://api.example.com/v1/chat/completions", color = HazonTextDim, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = HazonText,
                                unfocusedTextColor = HazonText,
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newModelKey,
                            onValueChange = { newModelKey = it },
                            label = { Text("API Key", fontSize = 11.sp, color = HazonTextMuted) },
                            placeholder = { Text("sk-...", color = HazonTextDim, fontSize = 12.sp) },
                            visualTransformation = PasswordVisualTransformation(),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = HazonText,
                                unfocusedTextColor = HazonText,
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = newModelTag,
                            onValueChange = { newModelTag = it },
                            label = { Text("Model ID / Tag", fontSize = 11.sp, color = HazonTextMuted) },
                            placeholder = { Text("e.g. gpt-4o, claude-3-5-sonnet, llama-3", color = HazonTextDim, fontSize = 12.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = HazonText,
                                unfocusedTextColor = HazonText,
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            horizontalArrangement = Arrangement.End,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HazonPurpleDark)
                                    .clickable {
                                        if (newModelName.isNotBlank() && newModelEndpoint.isNotBlank()) {
                                            onAddCustomModel(
                                                newModelName.trim(),
                                                newModelEndpoint.trim(),
                                                newModelKey.trim(),
                                                if (newModelTag.isNotBlank()) newModelTag.trim() else newModelName.trim().lowercase().replace(" ", "-")
                                            )
                                            newModelName = ""
                                            newModelEndpoint = ""
                                            newModelKey = ""
                                            newModelTag = ""
                                            showNewModelForm = false
                                        }
                                    }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                            ) {
                                Text(
                                    text = "Save & Activate Model",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // Display saved custom models
                    if (customModels.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Text(
                            text = "Your Added Models (${customModels.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        customModels.forEach { model ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HazonPanel2)
                                    .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
                                    .padding(10.dp)
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = model.name,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HazonText
                                    )
                                    Text(
                                        text = "Tag: ${model.modelTag} · ${model.endpointUrl}",
                                        fontSize = 10.sp,
                                        color = HazonTextDim,
                                        maxLines = 1
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0x288B5CF6))
                                            .clickable {
                                                testingProvider = model.id
                                                onTestCustomModel(model.endpointUrl, model.apiKey, model.modelTag) { res ->
                                                    testingProvider = null
                                                    testResult = model.id to if (res.success) "Connected! Latency: ${res.latencyMs}ms ✓" else "Failed: ${res.error}"
                                                }
                                            }
                                            .padding(horizontal = 8.dp, vertical = 6.dp)
                                    ) {
                                        if (testingProvider == model.id) {
                                            CircularProgressIndicator(
                                                color = HazonPurple,
                                                strokeWidth = 2.dp,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        } else {
                                            Text(
                                                text = "Test",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = HazonPurple
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(6.dp))

                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color(0x28F87171))
                                            .clickable { onDeleteCustomModel(model.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFF87171),
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }

                            if (testResult?.first == model.id) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = testResult?.second ?: "",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if ("Failed" in (testResult?.second ?: "")) Color(0xFFF87171) else HazonGreen
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Hardware Utilization Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HazonPanel)
                    .border(1.dp, HazonBorder, RoundedCornerShape(14.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = HazonCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Local Hardware Utilization",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Device: ${hardwareStats.deviceModel}",
                        fontSize = 11.sp,
                        color = HazonTextMuted,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HardwareMetricBadge(
                            icon = Icons.Default.Memory,
                            label = "CPU Processor",
                            value = "${hardwareStats.cpuCores} Cores Active",
                            color = HazonPurple
                        )
                        HardwareMetricBadge(
                            icon = Icons.Default.Storage,
                            label = "ROM Storage",
                            value = "${hardwareStats.availableStorageGb} GB Free (SQLite)",
                            color = HazonGreen
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HardwareMetricBadge(
                            icon = Icons.Default.Speed,
                            label = "Device RAM",
                            value = "${hardwareStats.availableRamMb} MB Free / ${hardwareStats.totalRamMb} MB",
                            color = HazonBlue
                        )
                        HardwareMetricBadge(
                            icon = Icons.Default.CheckCircle,
                            label = "GPU Acceleration",
                            value = "Hardware Layer 60FPS",
                            color = Color(0xFFC4B5FD)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Save Button
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(HazonPurpleDark)
                    .clickable {
                        onSaveSettings(
                            settings.copy(
                                geminiKey = geminiKey.trim(),
                                geminiModel = geminiModel.trim(),
                                geminiEndpoint = geminiEndpoint.trim(),
                                deepSeekKey = deepSeekKey.trim(),
                                deepSeekModel = deepSeekModel.trim(),
                                deepSeekEndpoint = deepSeekEndpoint.trim(),
                                glmKey = glmKey.trim(),
                                glmModel = glmModel.trim(),
                                glmEndpoint = glmEndpoint.trim(),
                                qwenKey = qwenKey.trim(),
                                qwenModel = qwenModel.trim(),
                                qwenEndpoint = qwenEndpoint.trim(),
                                qwenImageKey = qwenImageKey.trim(),
                                qwenImageModel = qwenImageModel.trim(),
                                qwenImageEndpoint = qwenImageEndpoint.trim(),
                                zaiKey = zaiKey.trim(),
                                zaiModel = zaiModel.trim(),
                                zaiEndpoint = zaiEndpoint.trim(),
                                customEndpoint = customEndpoint.trim(),
                                customKey = customKey.trim()
                            )
                        )
                    }
                    .padding(vertical = 14.dp)
                    .testTag("save_settings_btn")
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Save API Keys & Preferences",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ProviderSettingsCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    name: String,
    purpose: String,
    endpoint: String,
    onEndpointChange: (String) -> Unit = {},
    defaultEndpoint: String = "",
    apiKey: String,
    onApiKeyChange: (String) -> Unit,
    model: String,
    onModelChange: (String) -> Unit,
    placeholder: String,
    isTesting: Boolean,
    isDefaultActive: Boolean = false,
    onTestClick: () -> Unit,
    testFeedback: String?
) {
    var showKey by remember { mutableStateOf(false) }
    var isEditingEndpoint by remember { mutableStateOf(false) }
    val isCustomEndpoint = defaultEndpoint.isNotBlank() && endpoint.trim() != defaultEndpoint.trim()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HazonPanel)
            .border(
                1.dp,
                if (isDefaultActive) HazonPurple else HazonBorder,
                RoundedCornerShape(14.dp)
            )
            .padding(18.dp)
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
                            .size(34.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isDefaultActive) Color(0x388B5CF6) else Color(0x288B5CF6))
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = if (isDefaultActive) Color(0xFFC4B5FD) else HazonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = name,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = HazonText
                            )
                            if (isDefaultActive) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(Color(0x2854DBA4))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "ACTIVE",
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = HazonGreen
                                    )
                                }
                            }
                        }
                        Text(
                            text = purpose,
                            fontSize = 11.sp,
                            color = HazonTextMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Editable Endpoint Card Container
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(HazonPanel2)
                    .border(
                        1.dp,
                        if (isCustomEndpoint) Color(0xFF8B5CF6) else HazonBorder,
                        RoundedCornerShape(8.dp)
                    )
                    .padding(10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = if (isCustomEndpoint) HazonGreen else HazonPurple,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Endpoint URL",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )
                        if (isCustomEndpoint) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color(0x2854DBA4))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "MODIFIED",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HazonGreen
                                )
                            }
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (isCustomEndpoint && defaultEndpoint.isNotBlank()) {
                            Text(
                                text = "Reset Default",
                                fontSize = 10.sp,
                                color = Color(0xFFF87171),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier
                                    .clickable { onEndpointChange(defaultEndpoint) }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = if (isEditingEndpoint) "Done" else "Change URL",
                            fontSize = 10.sp,
                            color = HazonPurple,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { isEditingEndpoint = !isEditingEndpoint }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                if (isEditingEndpoint) {
                    OutlinedTextField(
                        value = endpoint,
                        onValueChange = onEndpointChange,
                        placeholder = { Text("https://...", fontSize = 11.sp, color = HazonTextDim) },
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace,
                            color = Color(0xFF82B8FF)
                        ),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color(0xFF82B8FF),
                            unfocusedTextColor = Color(0xFF82B8FF),
                            focusedBorderColor = HazonPurple,
                            unfocusedBorderColor = HazonBorder,
                            focusedContainerColor = HazonBackground,
                            unfocusedContainerColor = HazonBackground
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "যেকোনো custom endpoint, proxy, বা local IP দিতে পারেন।",
                        fontSize = 9.sp,
                        color = HazonTextMuted
                    )
                } else {
                    Text(
                        text = endpoint,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF82B8FF),
                        maxLines = 1,
                        modifier = Modifier
                            .clickable { isEditingEndpoint = true }
                            .padding(vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = apiKey,
                onValueChange = onApiKeyChange,
                label = { Text("API Key", fontSize = 11.sp, color = HazonTextMuted) },
                placeholder = { Text(placeholder, color = HazonTextDim, fontSize = 12.sp) },
                visualTransformation = if (showKey) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    Text(
                        text = if (showKey) "Hide" else "Show",
                        fontSize = 10.sp,
                        color = HazonPurple,
                        modifier = Modifier
                            .clickable { showKey = !showKey }
                            .padding(horizontal = 8.dp)
                    )
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = HazonText,
                    unfocusedTextColor = HazonText,
                    focusedBorderColor = HazonPurple,
                    unfocusedBorderColor = HazonBorder
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = model,
                    onValueChange = onModelChange,
                    label = { Text("Model Tag", fontSize = 10.sp, color = HazonTextMuted) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = HazonText,
                        unfocusedTextColor = HazonText,
                        focusedBorderColor = HazonPurple,
                        unfocusedBorderColor = HazonBorder
                    ),
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(HazonPanel2)
                        .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
                        .clickable(enabled = !isTesting) { onTestClick() }
                        .padding(horizontal = 12.dp, vertical = 12.dp)
                ) {
                    if (isTesting) {
                        CircularProgressIndicator(
                            color = HazonPurple,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(14.dp)
                        )
                    } else {
                        Text(
                            text = "Test Endpoint",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            color = HazonPurple
                        )
                    }
                }
            }

            if (testFeedback != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = testFeedback,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = if ("Failed" in testFeedback) Color(0xFFF87171) else HazonGreen
                )
            }
        }
    }
}

@Composable
fun HardwareMetricBadge(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    color: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(HazonPanel2)
            .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, fontSize = 9.sp, color = HazonTextDim)
                Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = HazonText)
            }
        }
    }
}
