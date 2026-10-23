package com.example.brain

data class TranslationResult(
    val detectedLanguage: String,
    val originalPrompt: String,
    val translatedPrompt: String,
    val technicalDirective: String,
    val targetFiles: List<String>
)

object Translator {
    fun analyzeAndTranslate(userPrompt: String): TranslationResult {
        val trimmed = userPrompt.trim()
        val lang = detectLanguage(trimmed)

        val (translated, directive, files) = when (lang) {
            "Bengali" -> translateBengali(trimmed)
            "Hindi" -> translateHindi(trimmed)
            "Spanish" -> translateSpanish(trimmed)
            else -> translateGeneral(trimmed)
        }

        return TranslationResult(
            detectedLanguage = lang,
            originalPrompt = trimmed,
            translatedPrompt = translated,
            technicalDirective = directive,
            targetFiles = files
        )
    }

    private fun detectLanguage(text: String): String {
        return when {
            text.any { it in '\u0980'..'\u09FF' } -> "Bengali"
            text.any { it in '\u0900'..'\u097F' } -> "Hindi"
            text.any { it in '\u0600'..'\u06FF' } -> "Arabic"
            text.any { it in '\u4E00'..'\u9FFF' } -> "Chinese"
            listOf("crear", "hacer", "sitio", "pagina", "cambiar", "color").any { text.contains(it, ignoreCase = true) } -> "Spanish"
            else -> "English / Universal"
        }
    }

    private fun translateBengali(text: String): Triple<String, String, List<String>> {
        val lower = text.lowercase()
        val isUpdate = listOf("পরিবর্তন", "বদল", "কালার", "রং", "বাটন", "যোগ", "করো", "চেঞ্জ", "এডিট", "সরাও").any { lower.contains(it) }

        val translated = when {
            lower.contains("রেস্তোরাঁ") || lower.contains("খাবার") || lower.contains("মেনু") || lower.contains("ফুড") || lower.contains("রেস্টুরেন্ট") ->
                "Build a premium dark-themed modern restaurant website with interactive food categories, item cards with prices, responsive order drawer, and reservations."
            lower.contains("পোর্টফোলিও") || lower.contains("সিভি") || lower.contains("জীবনবৃত্তান্ত") ->
                "Build a cutting-edge creative developer portfolio with animated hero banner, featured project showcase, skills radar, and contact form."
            lower.contains("ড্যাশবোর্ড") || lower.contains("এনালিটিক্স") || lower.contains("গ্রাফ") || lower.contains("চার্ট") ->
                "Build a high-performance SaaS analytics telemetry dashboard with live chart metrics, activity logs, and responsive layout."
            lower.contains("দোকান") || lower.contains("ই-কমার্স") || lower.contains("কেনাকাটা") || lower.contains("শপ") ->
                "Build a responsive e-commerce web storefront with product grid, price calculation, shopping bag modal, and checkout workflow."
            lower.contains("বাটন") && (lower.contains("রং") || lower.contains("কালার")) ->
                "Change button colors and interactive states to vivid purple (#8b5cf6) with glowing hover shadow and smooth transitions."
            lower.contains("ডার্ক") || lower.contains("কালো") || lower.contains("থিম") ->
                "Update CSS to use #090911 deep dark background, #10101c panels, and #8b5cf6 violet accents."
            else ->
                "Implement a modern, high-fidelity responsive web application matching request: \"$text\" with clean HTML5 semantic layout, modern CSS3 animations, and interactive JavaScript."
        }

        val directive = if (isUpdate) {
            "DIRECTIVE TO MAIN AI AGENT: The user requested a specific update in Bengali (\"$text\"). Identify which code file needs modification (styles.css for styles, app.js for interactions, or index.html for DOM elements) and generate the updated code with clear file headers."
        } else {
            "DIRECTIVE TO MAIN AI AGENT: The user requested a new project in Bengali (\"$text\"). Synthesize complete, runnable code across index.html, styles.css, and app.js. Apply Hazon dark studio aesthetics (#090911 background, #8b5cf6 accents)."
        }

        val files = if (isUpdate && (lower.contains("রং") || lower.contains("কালার") || lower.contains("ডিজাইন"))) {
            listOf("styles.css")
        } else if (isUpdate && (lower.contains("ক্লিক") || lower.contains("ফাংশন") || lower.contains("লজিক"))) {
            listOf("app.js")
        } else {
            listOf("index.html", "styles.css", "app.js")
        }

        return Triple(translated, directive, files)
    }

    private fun translateHindi(text: String): Triple<String, String, List<String>> {
        val translated = "Build a responsive web studio project based on request: \"$text\" with dark theme (#090911) and modern UI."
        val directive = "DIRECTIVE TO MAIN AI AGENT: Translate user's Hindi prompt into modular files (index.html, styles.css, app.js)."
        return Triple(translated, directive, listOf("index.html", "styles.css", "app.js"))
    }

    private fun translateSpanish(text: String): Triple<String, String, List<String>> {
        val translated = "Build a responsive modern web app matching Spanish request: \"$text\" with dark theme and interactive features."
        val directive = "DIRECTIVE TO MAIN AI AGENT: Generate production-ready HTML, CSS, and JS files."
        return Triple(translated, directive, listOf("index.html", "styles.css", "app.js"))
    }

    private fun translateGeneral(text: String): Triple<String, String, List<String>> {
        val directive = "DIRECTIVE TO MAIN AI AGENT: Synthesize clean, structured HTML5, CSS3, and JavaScript files for: \"$text\"."
        return Triple(text, directive, listOf("index.html", "styles.css", "app.js"))
    }
}
