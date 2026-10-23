package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Web
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TemplatesData
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBlue
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonBorderHover
import com.example.ui.theme.HazonCyan
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonPurpleDeep
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted
import com.example.data.local.entity.CustomModelEntity
import com.example.ui.theme.HazonViolet

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BuilderScreen(
    promptText: String,
    onPromptChange: (String) -> Unit,
    selectedModel: String,
    onModelChange: (String) -> Unit,
    currentMode: String,
    onModeChange: (String) -> Unit,
    isLoading: Boolean,
    loadingStatus: String,
    onBuild: (String) -> Unit,
    customModels: List<CustomModelEntity> = emptyList(),
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    var showModelMenu by remember { mutableStateOf(false) }

    val baseModelNames = mapOf(
        "gemini" to "Google AI Studio (Gemini)",
        "deepseek" to "DeepSeek (Coder)",
        "glm" to "GLM (Zhipu AI)",
        "qwen" to "Qwen (Alibaba)",
        "qwen_image" to "Qwen Image (Vision & Media)",
        "zai" to "Z.ai Coding",
        "custom" to "Custom Endpoint"
    )

    val customMap = customModels.associate { it.id to "${it.name} (${it.modelTag})" }
    val allModelNames = baseModelNames + customMap

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HazonBackground)
            .padding(horizontal = 16.dp, vertical = 20.dp)
            .testTag("builder_screen")
    ) {
        // Hero Badge
        item {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(100.dp))
                        .background(Color(0x178B5CF6))
                        .border(1.dp, Color(0x33A78BFA), RoundedCornerShape(100.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(HazonPurple.copy(alpha = pulseAlpha))
                            .shadow(8.dp, CircleShape, ambientColor = HazonPurple)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "YOUR AI-POWERED DEVELOPMENT STUDIO",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFC4B5FD),
                        letterSpacing = 1.1.sp
                    )
                }
            }
        }

        // Headline & Subtitle
        item {
            Spacer(modifier = Modifier.height(18.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Ideas become ",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = HazonText,
                        letterSpacing = (-1).sp
                    )
                    Text(
                        text = "reality.",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-1).sp,
                        style = TextStyle(
                            brush = Brush.linearGradient(
                                listOf(Color(0xFFC4B5FD), HazonPurple, Color(0xFF67B9FF))
                            )
                        )
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Describe what you imagine. Hazon helps you turn it into beautiful websites and powerful applications.",
                    fontSize = 13.sp,
                    color = HazonTextMuted,
                    textAlign = TextAlign.Center,
                    lineHeight = 19.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
            }
        }

        // Prompt Composer Box
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xF918172A), Color(0xF90E0E19))
                        )
                    )
                    .border(1.dp, HazonBorderHover, RoundedCornerShape(18.dp))
                    .shadow(elevation = 20.dp, shape = RoundedCornerShape(18.dp), ambientColor = Color(0x228B5CF6))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = HazonPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "What do you want to build?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = HazonText
                            )
                        }
                        Text(
                            text = "Be as creative as you like",
                            fontSize = 10.sp,
                            color = HazonTextDim
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Text input
                    BasicTextField(
                        value = promptText,
                        onValueChange = onPromptChange,
                        textStyle = TextStyle(
                            color = HazonText,
                            fontSize = 14.sp,
                            lineHeight = 22.sp
                        ),
                        cursorBrush = SolidColor(HazonPurple),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp)
                            .testTag("prompt_input"),
                        decorationBox = { innerTextField ->
                            if (promptText.isEmpty()) {
                                Text(
                                    text = if (currentMode == "plan")
                                        "Describe your idea. Hazon will help organize the architecture and implementation steps..."
                                    else
                                        "Build a stunning portfolio website with smooth animations, dark theme, and project gallery...",
                                    color = Color(0xFF77758F),
                                    fontSize = 13.sp,
                                    lineHeight = 20.sp
                                )
                            }
                            innerTextField()
                        }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Chips
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ComposerChip(
                            icon = Icons.Default.Palette,
                            label = "Portfolio",
                            onClick = {
                                onPromptChange("Build a modern creative portfolio website with animations and a project gallery.")
                            }
                        )
                        ComposerChip(
                            icon = Icons.Default.BarChart,
                            label = "Dashboard",
                            onClick = {
                                onPromptChange("Build a SaaS analytics dashboard with charts, statistics, and a responsive sidebar.")
                            }
                        )
                        ComposerChip(
                            icon = Icons.Default.ShoppingBag,
                            label = "E-commerce",
                            onClick = {
                                onPromptChange("Build a responsive online store with product cards, category filters, and a shopping cart.")
                            }
                        )
                        ComposerChip(
                            icon = Icons.Default.Web,
                            label = "Landing page",
                            onClick = {
                                onPromptChange("Build a modern AI landing page with a hero section, features, testimonials, and pricing.")
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0x1EB4AAFF))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Options and Build Button
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Model Selector
                        Box {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(HazonPanel2)
                                    .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
                                    .clickable { showModelMenu = true }
                                    .padding(horizontal = 8.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Memory,
                                    contentDescription = null,
                                    tint = HazonPurple,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = allModelNames[selectedModel] ?: (customModels.find { it.id == selectedModel }?.name ?: "Model"),
                                    fontSize = 11.sp,
                                    color = Color(0xFFD9D4F3),
                                    maxLines = 1
                                )
                            }

                            DropdownMenu(
                                expanded = showModelMenu,
                                onDismissRequest = { showModelMenu = false },
                                modifier = Modifier.background(HazonPanel2)
                            ) {
                                allModelNames.forEach { (key, name) ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(text = name, fontSize = 12.sp, color = HazonText)
                                        },
                                        onClick = {
                                            onModelChange(key)
                                            showModelMenu = false
                                        }
                                    )
                                }
                            }
                        }

                        // Mode toggles
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            ModeToggleButton(
                                icon = Icons.Default.Build,
                                label = "Build",
                                isSelected = currentMode == "build",
                                onClick = { onModeChange("build") }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            ModeToggleButton(
                                icon = Icons.Default.Checklist,
                                label = "Plan",
                                isSelected = currentMode == "plan",
                                onClick = { onModeChange("plan") }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Build Project Button
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color(0xFF8859EF), Color(0xFF5E67E9))
                                    )
                                )
                                .border(1.dp, Color(0x38C9B9FF), RoundedCornerShape(10.dp))
                                .shadow(12.dp, ambientColor = HazonViolet)
                                .clickable(enabled = !isLoading) { onBuild(promptText) }
                                .padding(vertical = 12.dp)
                                .testTag("build_button")
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = loadingStatus,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White
                                )
                            } else {
                                Text(
                                    text = if (currentMode == "plan") "Create plan" else "Build project",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Suggestions Heading
        item {
            Spacer(modifier = Modifier.height(32.dp))
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Text(
                        text = "Start with inspiration",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = HazonText,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "Choose a starting point. Make it your own.",
                        fontSize = 11.sp,
                        color = HazonTextMuted
                    )
                }
                Text(
                    text = "04 IDEAS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = HazonTextDim,
                    letterSpacing = 1.2.sp
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Template Cards
        items(TemplatesData.templates.size) { index ->
            val template = TemplatesData.templates[index]
            TemplateCardItem(
                category = template.category,
                title = template.title,
                description = template.description,
                onClick = { onBuild(template.prompt) }
            )
            Spacer(modifier = Modifier.height(12.dp))
        }

        // Footer
        item {
            Spacer(modifier = Modifier.height(28.dp))
            HorizontalDivider(color = Color(0x14B4AAFF))
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = HazonPurple,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Your projects, your control.",
                        fontSize = 10.sp,
                        color = Color(0xFF8E8BA5)
                    )
                }
                Text(
                    text = "HAZON AI · IMAGINE PLAN BUILD",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = HazonTextDim
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun ComposerChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0x12FFFFFF))
            .border(1.dp, Color(0x21B4AAFF), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 9.dp, vertical = 5.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HazonPurple,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color(0xFFB5B1CA)
        )
    }
}

@Composable
fun ModeToggleButton(
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
                if (isSelected) Color(0x248B5CF6) else Color.Transparent
            )
            .border(
                1.dp,
                if (isSelected) Color(0x2EA78BFA) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) Color(0xFFD5C7FF) else Color(0xFF85829C),
            modifier = Modifier.size(13.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) Color(0xFFD5C7FF) else Color(0xFF85829C)
        )
    }
}

@Composable
fun TemplateCardItem(
    category: String,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(HazonPanel)
            .border(1.dp, HazonBorder, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        when (category) {
                            "CREATIVE" -> Color(0x248B5CF6)
                            "PRODUCTIVITY" -> Color(0x243B82F6)
                            "NEXT-GEN AI" -> Color(0x24A78BFA)
                            else -> Color(0x2454DBA4)
                        }
                    )
            ) {
                Icon(
                    imageVector = when (category) {
                        "CREATIVE" -> Icons.Default.Palette
                        "PRODUCTIVITY" -> Icons.Default.BarChart
                        "NEXT-GEN AI" -> Icons.Default.AutoAwesome
                        else -> Icons.Default.ShoppingBag
                    },
                    contentDescription = null,
                    tint = when (category) {
                        "CREATIVE" -> HazonPurple
                        "PRODUCTIVITY" -> HazonBlue
                        "NEXT-GEN AI" -> HazonCyan
                        else -> Color(0xFF54DBA4)
                    },
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = HazonPurple,
                    letterSpacing = 1.sp
                )
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = HazonText
                )
                Text(
                    text = description,
                    fontSize = 11.sp,
                    color = HazonTextMuted,
                    maxLines = 2
                )
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Select",
                tint = HazonPurple,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
