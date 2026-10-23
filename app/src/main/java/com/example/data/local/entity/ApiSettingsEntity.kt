package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_settings")
data class ApiSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    // Google AI Studio (Gemini)
    val geminiKey: String = "",
    val geminiModel: String = "gemini-2.5-flash",
    val geminiEndpoint: String = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent",

    // DeepSeek
    val deepSeekKey: String = "",
    val deepSeekModel: String = "deepseek-chat",
    val deepSeekEndpoint: String = "https://api.deepseek.com/chat/completions",

    // GLM (Zhipu AI)
    val glmKey: String = "",
    val glmModel: String = "glm-4",
    val glmEndpoint: String = "https://open.bigmodel.cn/api/paas/v4/chat/completions",

    // Qwen (Alibaba Cloud / DashScope - Language & Coding)
    val qwenKey: String = "",
    val qwenModel: String = "qwen-turbo",
    val qwenEndpoint: String = DEFAULT_QWEN_ENDPOINT,

    // Qwen Image / Vision (Alibaba Cloud / DashScope - Multimodal & Image)
    val qwenImageKey: String = "",
    val qwenImageModel: String = "qwen-vl-plus",
    val qwenImageEndpoint: String = DEFAULT_QWEN_IMAGE_ENDPOINT,

    // Z.ai
    val zaiKey: String = "",
    val zaiModel: String = "z-ai-coding",
    val zaiEndpoint: String = DEFAULT_ZAI_ENDPOINT,

    // Custom / Self-hosted
    val customEndpoint: String = "",
    val customKey: String = "",
    val defaultModel: String = "gemini"
) {
    companion object {
        const val DEFAULT_GEMINI_ENDPOINT = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent"
        const val DEFAULT_DEEPSEEK_ENDPOINT = "https://api.deepseek.com/chat/completions"
        const val DEFAULT_GLM_ENDPOINT = "https://open.bigmodel.cn/api/paas/v4/chat/completions"
        const val DEFAULT_QWEN_ENDPOINT = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
        const val DEFAULT_QWEN_IMAGE_ENDPOINT = "https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions"
        const val DEFAULT_ZAI_ENDPOINT = "https://api.z.ai/v1/chat/completions"
    }
}
