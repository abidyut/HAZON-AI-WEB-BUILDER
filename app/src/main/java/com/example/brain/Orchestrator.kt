package com.example.brain

import java.util.regex.Pattern

data class OrchestratorRequest(
    val translation: TranslationResult,
    val systemPrompt: String,
    val userPromptForAi: String
)

data class OrchestratorParseResult(
    val explanation: String,
    val updatedFiles: Map<String, String>,
    val targetFileNames: List<String>
)

object Orchestrator {
    fun preparePromptForMainAi(
        userPrompt: String,
        existingFiles: Map<String, String>
    ): OrchestratorRequest {
        val translation = Translator.analyzeAndTranslate(userPrompt)

        val filesContext = if (existingFiles.isNotEmpty()) {
            buildString {
                append("\n\n--- EXISTING PROJECT FILES ---\n")
                existingFiles.forEach { (name, content) ->
                    append("\n--- FILE: $name ---\n")
                    append(content.take(800)) // summarize to keep prompt concise
                    if (content.length > 800) append("\n... (truncated for brevity) ...")
                }
            }
        } else ""

        val systemPrompt = """
            You are the Master Coding Engine for Hazon AI Mobile Studio.
            Hazon AI's internal brain has processed the user's intent.
            You must output the exact code modifications or new code files needed.
            
            FORMAT REQUIREMENTS:
            Every file must be explicitly demarcated with:
            FILE: filename.ext
            ```language
            ... complete updated code ...
            ```
            
            Theme: Hazon AI deep dark palette (#090911 background, #10101c panels, #8b5cf6 accents).
            Output clean, valid, responsive code.
        """.trimIndent()

        val userPromptForAi = """
            [HAZON AI ORCHESTRATOR DIRECTIVE]
            Original User Language: ${translation.detectedLanguage}
            Original Prompt: "${translation.originalPrompt}"
            Translated Technical Specification: "${translation.translatedPrompt}"
            Action Plan: ${translation.technicalDirective}
            Target Files: ${translation.targetFiles.joinToString(", ")}
            $filesContext
            
            Generate the code according to the directive with FILE: <filename> markers.
        """.trimIndent()

        return OrchestratorRequest(
            translation = translation,
            systemPrompt = systemPrompt,
            userPromptForAi = userPromptForAi
        )
    }

    fun parseAndApplyAiResponse(
        rawAiResponse: String,
        originalLanguage: String
    ): OrchestratorParseResult {
        val updatedFiles = mutableMapOf<String, String>()

        // Match FILE: filename.ext followed by code block
        val fileBlockPattern = Pattern.compile(
            "FILE:\\s*([a-zA-Z0-9._\\-]+)[\\r\\n]+```(?:[a-zA-Z0-9]*)[\\r\\n]([\\s\\S]*?)```",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = fileBlockPattern.matcher(rawAiResponse)

        while (matcher.find()) {
            val fileName = matcher.group(1)?.trim() ?: ""
            val fileContent = matcher.group(2)?.trim() ?: ""
            if (fileName.isNotEmpty() && fileContent.isNotEmpty()) {
                updatedFiles[fileName] = fileContent
            }
        }

        // Secondary fallback: check for standard markdown code blocks if no FILE: markers
        if (updatedFiles.isEmpty()) {
            val codeBlockPattern = Pattern.compile("```([a-zA-Z0-9]*)[\\r\\n]([\\s\\S]*?)```")
            val blockMatcher = codeBlockPattern.matcher(rawAiResponse)
            var count = 0
            while (blockMatcher.find()) {
                val lang = blockMatcher.group(1)?.lowercase() ?: ""
                val code = blockMatcher.group(2)?.trim() ?: ""
                count++
                when {
                    lang in listOf("html", "xml") || code.contains("<!DOCTYPE") || code.contains("<html") -> {
                        updatedFiles["index.html"] = code
                    }
                    lang in listOf("css") || code.contains("{") && code.contains(":") && !code.contains("function") -> {
                        updatedFiles["styles.css"] = code
                    }
                    lang in listOf("js", "javascript", "ts") || code.contains("function") || code.contains("console.log") -> {
                        updatedFiles["app.js"] = code
                    }
                    else -> {
                        if (count == 1 && !updatedFiles.containsKey("index.html")) updatedFiles["index.html"] = code
                    }
                }
            }
        }

        val explanation = when (originalLanguage) {
            "Bengali" -> {
                val filesList = if (updatedFiles.isNotEmpty()) updatedFiles.keys.joinToString(", ") else "index.html, styles.css, app.js"
                "🤖 **হাজোন এআই অর্কেস্ট্রেটর (Hazon AI Brain):**\n" +
                "আপনার বাংলা প্রম্পটটি বিশ্লেষণ করে প্রধান এআই এজেন্টকে নির্দেশ পাঠানো হয়েছে।\n\n" +
                "📁 **হালনাগাদ ফাইলসমূহ:** `$filesList`\n" +
                "⚡ কোডটি প্রজেক্ট ফাইলে যুক্ত করা হয়েছে এবং **Preview** ট্যাবে লাইভ দেখা যাচ্ছে।"
            }
            else -> {
                val filesList = if (updatedFiles.isNotEmpty()) updatedFiles.keys.joinToString(", ") else "index.html, styles.css, app.js"
                "🤖 **Hazon AI Orchestrator:**\n" +
                "User intent analyzed, translated to technical specification, and dispatched to the main AI agent.\n\n" +
                "📁 **Target files updated:** `$filesList`\n" +
                "⚡ Code synced to internal ROM storage and compiled in live Preview."
            }
        }

        return OrchestratorParseResult(
            explanation = explanation,
            updatedFiles = updatedFiles,
            targetFileNames = updatedFiles.keys.toList()
        )
    }

    fun scaffoldDynamicProject(translation: TranslationResult): Map<String, String> {
        val prompt = translation.translatedPrompt.lowercase()
        return when {
            prompt.contains("restaurant") || prompt.contains("food") || prompt.contains("menu") ->
                scaffoldRestaurantProject(translation.originalPrompt)
            prompt.contains("portfolio") || prompt.contains("resume") ->
                scaffoldPortfolioProject(translation.originalPrompt)
            prompt.contains("dashboard") || prompt.contains("analytics") ->
                scaffoldDashboardProject(translation.originalPrompt)
            prompt.contains("store") || prompt.contains("commerce") || prompt.contains("shop") ->
                scaffoldStoreProject(translation.originalPrompt)
            else ->
                scaffoldGeneralProject(translation.originalPrompt)
        }
    }

    private fun scaffoldRestaurantProject(title: String): Map<String, String> {
        val html = """
            <!DOCTYPE html>
            <html lang="bn">
            <head>
              <meta charset="UTF-8">
              <meta name="viewport" content="width=device-width, initial-scale=1.0">
              <title>Spicy Orchid — Gourmet Dining</title>
              <link rel="stylesheet" href="styles.css">
            </head>
            <body>
              <nav class="nav">
                <div class="logo">🍴 Spicy Orchid</div>
                <div class="cart-pill" onclick="toggleCart()">অর্ডার ব্যাগ (<span id="cartCount">0</span>)</div>
              </nav>

              <header class="hero">
                <span class="badge">সেরা স্বাদের সমাহার · 100% FRESH</span>
                <h1>অসাধারণ স্বাদের <span class="accent">অভিজ্ঞতা নিন</span></h1>
                <p>বিশ্বমানের খাঁটি মশলা এবং প্রিমিয়াম খাদ্য উপাদানে তৈরি আমাদের স্পেশাল মেনু।</p>
                <button class="cta-btn" onclick="scrollToMenu()">মেনু দেখুন</button>
              </header>

              <section id="menuSection" class="menu-section">
                <h2>জনপ্রিয় ফুড মেনু</h2>
                <div class="menu-grid" id="menuGrid">
                  <!-- Loaded via JS -->
                </div>
              </section>

              <div id="cartModal" class="cart-modal hidden">
                <div class="cart-box">
                  <h3>আপনার অর্ডার তালিকা</h3>
                  <div id="cartList">ব্যাগ খালি আছে।</div>
                  <div class="cart-total">মোট বিল: <strong id="totalBill">৳ 0</strong></div>
                  <button class="order-confirm-btn" onclick="confirmOrder()">অর্ডার নিশ্চিত করুন</button>
                  <button class="close-btn" onclick="toggleCart()">বন্ধ করুন</button>
                </div>
              </div>

              <script src="app.js"></script>
            </body>
            </html>
        """.trimIndent()

        val css = """
            * { box-sizing: border-box; margin: 0; padding: 0; }
            body { background: #090911; color: #f5f3ff; font-family: -apple-system, sans-serif; padding: 16px; }
            .nav { display: flex; justify-content: space-between; align-items: center; padding-bottom: 16px; border-bottom: 1px solid rgba(180,170,255,0.12); }
            .logo { font-size: 18px; font-weight: 800; color: #a78bfa; }
            .cart-pill { background: #151525; color: #c4b5fd; border: 1px solid #8b5cf6; padding: 6px 14px; border-radius: 20px; font-size: 12px; cursor: pointer; }
            .hero { text-align: center; padding: 36px 10px; }
            .badge { font-size: 10px; font-weight: 700; color: #54dba4; background: rgba(84,219,164,0.1); padding: 5px 12px; border-radius: 20px; border: 1px solid rgba(84,219,164,0.3); }
            h1 { font-size: 28px; margin: 16px 0 10px; line-height: 1.25; }
            .accent { color: #8b5cf6; }
            p { color: #9694ad; font-size: 13px; max-width: 440px; margin: 0 auto 20px; }
            .cta-btn { background: linear-gradient(110deg, #8859ef, #5e67e9); color: white; border: none; padding: 10px 22px; border-radius: 8px; font-weight: 700; cursor: pointer; }
            .menu-section { margin-top: 32px; }
            h2 { font-size: 18px; margin-bottom: 16px; color: #e9e5ff; }
            .menu-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; }
            .food-card { background: #10101c; border: 1px solid rgba(180,170,255,0.12); border-radius: 12px; padding: 14px; display: flex; flex-direction: column; justify-content: space-between; }
            .food-emoji { font-size: 36px; text-align: center; margin: 8px 0; }
            .food-name { font-size: 14px; font-weight: 700; margin-bottom: 4px; }
            .food-desc { font-size: 11px; color: #88859e; margin-bottom: 8px; }
            .food-price { font-size: 14px; color: #54dba4; font-weight: 700; margin-bottom: 10px; }
            .add-food-btn { background: #1b1b30; color: #c4b5fd; border: 1px solid rgba(180,170,255,0.2); padding: 8px; border-radius: 8px; font-size: 11px; font-weight: 600; cursor: pointer; }
            .add-food-btn:hover { background: #8b5cf6; color: white; }
            .cart-modal { position: fixed; inset: 0; background: rgba(0,0,0,0.7); display: flex; align-items: center; justify-content: center; z-index: 100; }
            .cart-box { background: #151525; border: 1px solid rgba(180,170,255,0.2); border-radius: 14px; padding: 20px; width: 85%; max-width: 320px; }
            .cart-box h3 { font-size: 16px; margin-bottom: 12px; }
            .cart-total { margin: 16px 0; font-size: 14px; border-top: 1px solid rgba(180,170,255,0.1); padding-top: 10px; }
            .order-confirm-btn { width: 100%; background: #54dba4; color: #090911; border: none; padding: 10px; border-radius: 8px; font-weight: bold; cursor: pointer; margin-bottom: 8px; }
            .close-btn { width: 100%; background: transparent; color: #888; border: 1px solid rgba(180,170,255,0.1); padding: 8px; border-radius: 8px; cursor: pointer; }
            .hidden { display: none; }
        """.trimIndent()

        val js = """
            const items = [
              { id: 1, name: "মোরগ পোলাও", price: 260, emoji: "🍗", desc: "ঐতিহ্যবাহী জাফরানি পোলাও" },
              { id: 2, name: "বিফ কাচ্চি", price: 380, emoji: "🍛", desc: "বাসমতি চাল ও নরম মাংস" },
              { id: 3, name: "গ্রিল চিকেন সেট", price: 220, emoji: "🍖", desc: "স্পেশাল নান ও মেয়োনিজ" },
              { id: 4, name: "কুলফি ফালুদা", price: 140, emoji: "🍧", desc: "বাদাম ও পিস্তা কুচি" }
            ];

            let cart = [];

            function renderMenu() {
              const grid = document.getElementById("menuGrid");
              grid.innerHTML = items.map(item => `
                <div class="food-card">
                  <div class="food-emoji">${'$'}{item.emoji}</div>
                  <div class="food-name">${'$'}{item.name}</div>
                  <div class="food-desc">${'$'}{item.desc}</div>
                  <div class="food-price">৳ ${'$'}{item.price}</div>
                  <button class="add-food-btn" onclick="addToCart(${'$'}{item.id})">অর্ডার করুন +</button>
                </div>
              `).join("");
            }

            function addToCart(id) {
              const item = items.find(i => i.id === id);
              if (item) {
                cart.push(item);
                updateCart();
              }
            }

            function updateCart() {
              document.getElementById("cartCount").textContent = cart.length;
              const list = document.getElementById("cartList");
              if (cart.length === 0) {
                list.innerHTML = "ব্যাগ খালি আছে।";
                document.getElementById("totalBill").textContent = "৳ 0";
                return;
              }
              list.innerHTML = cart.map(i => `<div>${'$'}{i.emoji} ${'$'}{i.name} — ৳ ${'$'}{i.price}</div>`).join("");
              const total = cart.reduce((acc, c) => acc + c.price, 0);
              document.getElementById("totalBill").textContent = "৳ " + total;
            }

            function toggleCart() {
              document.getElementById("cartModal").classList.toggle("hidden");
            }

            function scrollToMenu() {
              document.getElementById("menuSection").scrollIntoView({ behavior: "smooth" });
            }

            function confirmOrder() {
              alert("ধন্যবাদ! আপনার অর্ডারটি গ্রহণ করা হয়েছে। মোট বিল: " + document.getElementById("totalBill").textContent);
              cart = [];
              updateCart();
              toggleCart();
            }

            renderMenu();
        """.trimIndent()

        return mapOf(
            "index.html" to html,
            "styles.css" to css,
            "app.js" to js
        )
    }

    private fun scaffoldPortfolioProject(title: String): Map<String, String> {
        return com.example.data.TemplatesData.templates[0].let {
            mapOf("index.html" to it.htmlContent, "styles.css" to it.cssContent, "app.js" to it.jsContent)
        }
    }

    private fun scaffoldDashboardProject(title: String): Map<String, String> {
        return com.example.data.TemplatesData.templates[1].let {
            mapOf("index.html" to it.htmlContent, "styles.css" to it.cssContent, "app.js" to it.jsContent)
        }
    }

    private fun scaffoldStoreProject(title: String): Map<String, String> {
        return com.example.data.TemplatesData.templates[3].let {
            mapOf("index.html" to it.htmlContent, "styles.css" to it.cssContent, "app.js" to it.jsContent)
        }
    }

    private fun scaffoldGeneralProject(title: String): Map<String, String> {
        return com.example.data.TemplatesData.templates[0].let {
            mapOf("index.html" to it.htmlContent, "styles.css" to it.cssContent, "app.js" to it.jsContent)
        }
    }
}
