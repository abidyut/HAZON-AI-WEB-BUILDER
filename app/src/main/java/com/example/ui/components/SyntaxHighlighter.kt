package com.example.ui.components

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.example.ui.theme.SyntaxAttribute
import com.example.ui.theme.SyntaxComment
import com.example.ui.theme.SyntaxKeyword
import com.example.ui.theme.SyntaxNumber
import com.example.ui.theme.SyntaxString
import com.example.ui.theme.SyntaxTag
import java.util.regex.Pattern

object SyntaxHighlighter {
    private val jsKeywords = setOf(
        "const", "let", "var", "function", "return", "if", "else", "for", "while",
        "import", "export", "from", "default", "class", "extends", "new", "this",
        "async", "await", "try", "catch", "throw", "switch", "case", "break"
    )

    fun highlight(code: String, fileExtension: String): AnnotatedString {
        return buildAnnotatedString {
            when (fileExtension.lowercase()) {
                "html", "htm" -> highlightHtml(code, this)
                "css" -> highlightCss(code, this)
                "js", "ts", "json" -> highlightJs(code, this)
                else -> append(code)
            }
        }
    }

    private fun highlightHtml(code: String, builder: AnnotatedString.Builder) {
        val pattern = Pattern.compile("<!--[\\s\\S]*?-->|<(\\/?[a-zA-Z0-9\\-]+)([^>]*)>|([^<]+)")
        val matcher = pattern.matcher(code)

        while (matcher.find()) {
            val comment = matcher.group(0)
            if (comment != null && comment.startsWith("<!--")) {
                builder.withStyle(SpanStyle(color = SyntaxComment)) {
                    append(comment)
                }
            } else if (matcher.group(1) != null) {
                // Opening or closing tag
                val tagName = matcher.group(1) ?: ""
                val rest = matcher.group(2) ?: ""

                builder.withStyle(SpanStyle(color = SyntaxTag)) {
                    append("<$tagName")
                }

                // Attributes within tag
                if (rest.isNotEmpty()) {
                    val attrPattern = Pattern.compile("([a-zA-Z0-9\\-_:]+)(=(\"[^\"]*\"|'[^']*'))?")
                    val attrMatcher = attrPattern.matcher(rest)
                    var lastIdx = 0
                    while (attrMatcher.find()) {
                        val space = rest.substring(lastIdx, attrMatcher.start())
                        builder.append(space)

                        val attrName = attrMatcher.group(1) ?: ""
                        builder.withStyle(SpanStyle(color = SyntaxAttribute)) {
                            append(attrName)
                        }

                        val eqVal = attrMatcher.group(2)
                        if (eqVal != null) {
                            builder.append("=")
                            val strVal = attrMatcher.group(3) ?: ""
                            builder.withStyle(SpanStyle(color = SyntaxString)) {
                                append(strVal)
                            }
                        }
                        lastIdx = attrMatcher.end()
                    }
                    if (lastIdx < rest.length) {
                        builder.append(rest.substring(lastIdx))
                    }
                }

                builder.withStyle(SpanStyle(color = SyntaxTag)) {
                    append(">")
                }
            } else {
                builder.append(matcher.group(3) ?: "")
            }
        }
    }

    private fun highlightCss(code: String, builder: AnnotatedString.Builder) {
        val lines = code.lines()
        lines.forEachIndexed { i, line ->
            val trimmed = line.trim()
            if (trimmed.startsWith("/*")) {
                builder.withStyle(SpanStyle(color = SyntaxComment)) { append(line) }
            } else if (trimmed.contains("{")) {
                val parts = line.split("{", limit = 2)
                builder.withStyle(SpanStyle(color = SyntaxTag)) { append(parts[0]) }
                builder.append("{")
                if (parts.size > 1) builder.append(parts[1])
            } else if (trimmed.contains(":")) {
                val parts = line.split(":", limit = 2)
                builder.withStyle(SpanStyle(color = SyntaxAttribute)) { append(parts[0]) }
                builder.append(":")
                if (parts.size > 1) {
                    val value = parts[1]
                    if (value.contains("#") || value.matches(Regex(".*\\d.*"))) {
                        builder.withStyle(SpanStyle(color = SyntaxNumber)) { append(value) }
                    } else {
                        builder.withStyle(SpanStyle(color = SyntaxString)) { append(value) }
                    }
                }
            } else {
                builder.append(line)
            }
            if (i < lines.size - 1) builder.append("\n")
        }
    }

    private fun highlightJs(code: String, builder: AnnotatedString.Builder) {
        val tokens = code.split(Regex("(?<=[\\s(),;{}[\\]=+\\-*/<>])|(?=[\\s(),;{}[\\]=+\\-*/<>])"))
        tokens.forEach { token ->
            val trimmed = token.trim()
            when {
                trimmed in jsKeywords -> {
                    builder.withStyle(SpanStyle(color = SyntaxKeyword)) { append(token) }
                }
                trimmed.startsWith("\"") && trimmed.endsWith("\"") ||
                trimmed.startsWith("'") && trimmed.endsWith("'") ||
                trimmed.startsWith("`") && trimmed.endsWith("`") -> {
                    builder.withStyle(SpanStyle(color = SyntaxString)) { append(token) }
                }
                trimmed.matches(Regex("^-?\\d+(\\.\\d+)?$")) -> {
                    builder.withStyle(SpanStyle(color = SyntaxNumber)) { append(token) }
                }
                trimmed.startsWith("//") -> {
                    builder.withStyle(SpanStyle(color = SyntaxComment)) { append(token) }
                }
                trimmed.startsWith("<") && trimmed.endsWith(">") -> {
                    builder.withStyle(SpanStyle(color = SyntaxTag)) { append(token) }
                }
                else -> builder.append(token)
            }
        }
    }
}
