package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonGreen
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextDim
import com.example.ui.theme.HazonTextMuted

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ImportProjectModal(
    onDismiss: () -> Unit,
    onPickZip: () -> Unit,
    onImportGitHub: (String) -> Unit,
    onImportRawCode: (title: String, html: String, css: String, js: String) -> Unit,
    onSelectTemplate: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    var gitHubRepoInput by remember { mutableStateOf("") }

    var rawProjectTitle by remember { mutableStateOf("My Custom Web App") }
    var rawHtmlInput by remember {
        mutableStateOf(
            "<!DOCTYPE html>\n<html>\n<head>\n  <meta charset=\"UTF-8\">\n  <title>Imported App</title>\n  <link rel=\"stylesheet\" href=\"styles.css\">\n</head>\n<body>\n  <div class=\"container\">\n    <h1>Welcome to My App</h1>\n    <p>Custom imported web project.</p>\n    <button id=\"btn\">Click Me</button>\n  </div>\n  <script src=\"app.js\"></script>\n</body>\n</html>"
        )
    }
    var rawCssInput by remember {
        mutableStateOf(
            "body { font-family: system-ui, sans-serif; background: #0b0f19; color: #f8fafc; padding: 2rem; }\n.container { max-width: 600px; margin: 0 auto; background: #161b2a; padding: 24px; border-radius: 12px; border: 1px solid #2a3349; }\nh1 { color: #8b5cf6; margin-top: 0; }\nbutton { background: #8b5cf6; color: white; border: none; padding: 10px 18px; border-radius: 8px; font-weight: bold; cursor: pointer; }"
        )
    }
    var rawJsInput by remember {
        mutableStateOf(
            "document.getElementById('btn')?.addEventListener('click', () => {\n  alert('Hazon AI: Interactive click handled!');\n});\nconsole.log('App loaded successfully in SQLite ROM storage.');"
        )
    }

    BasicAlertDialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(HazonPanel)
                .border(1.dp, HazonBorder, RoundedCornerShape(16.dp))
                .padding(20.dp)
        ) {
            // Header
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
                            .background(Color(0x288B5CF6))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = HazonPurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Import Project",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )
                        Text(
                            text = "প্রজেক্ট ফাইল ইমপোর্ট করুন",
                            fontSize = 11.sp,
                            color = HazonTextMuted
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = HazonTextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Source Tabs: ZIP, GitHub, Paste Code, Template
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = HazonPanel2,
                contentColor = HazonText,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = HazonPurple,
                        height = 2.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("ZIP File", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("GitHub", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Paste Code", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Tab Content
            when (selectedTab) {
                0 -> {
                    // TAB 0: ZIP File Upload
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(HazonPanel2)
                            .border(1.dp, HazonBorder, RoundedCornerShape(10.dp))
                            .padding(20.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0x288B5CF6))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Archive,
                                contentDescription = null,
                                tint = HazonPurple,
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Import from Local ZIP Archive",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "আপনার ফোনের ডিভাইস স্টোরেজ থেকে যেকোনো ওয়েব প্রজেক্টের .zip ফাইল সিলেক্ট করুন। সব HTML, CSS, JS ফাইল স্বয়ংক্রিয়ভাবে SQLite ROM-এ সংরক্ষিত হবে।",
                            fontSize = 11.sp,
                            color = HazonTextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {
                                onPickZip()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HazonPurpleDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.FolderOpen,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Choose ZIP from Phone Storage",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(HazonPanel2)
                            .clickable {
                                onDismiss()
                                onSelectTemplate()
                            }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Code,
                                contentDescription = null,
                                tint = HazonGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Or Browse Starter Bundles",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = HazonText
                                )
                                Text(
                                    text = "E-Commerce, Portfolio, Dashboard templates",
                                    fontSize = 10.sp,
                                    color = HazonTextDim
                                )
                            }
                        }
                        Text(
                            text = "Open",
                            fontSize = 11.sp,
                            color = HazonPurple,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                1 -> {
                    // TAB 1: GitHub Repository Clone
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(HazonPanel2)
                            .border(1.dp, HazonBorder, RoundedCornerShape(10.dp))
                            .padding(16.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = HazonPurple,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Clone GitHub Repository",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = HazonText
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "GitHub URL বা owner/repo লিখুন (যেমন: facebook/react বা https://github.com/owner/repo):",
                            fontSize = 11.sp,
                            color = HazonTextMuted
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = gitHubRepoInput,
                            onValueChange = { gitHubRepoInput = it },
                            placeholder = { Text("e.g. facebook/react or owner/repo", fontSize = 11.sp, color = HazonTextDim) },
                            singleLine = true,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace,
                                color = HazonText
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder,
                                focusedContainerColor = HazonBackground,
                                unfocusedContainerColor = HazonBackground
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Quick Starter Repositories:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = HazonTextDim
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            listOf(
                                "alhaz-bidyut/web-starter",
                                "animate-css/animate.css",
                                "tailwindlabs/heroicons"
                            ).forEach { sample ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(Color(0x288B5CF6))
                                        .clickable { gitHubRepoInput = sample }
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = sample,
                                        fontSize = 10.sp,
                                        color = HazonPurple,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                if (gitHubRepoInput.isNotBlank()) {
                                    onImportGitHub(gitHubRepoInput)
                                }
                            },
                            enabled = gitHubRepoInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = HazonPurpleDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Public,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Clone & Open in Workspace",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }

                2 -> {
                    // TAB 2: Direct Code Paste
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(HazonPanel2)
                            .border(1.dp, HazonBorder, RoundedCornerShape(10.dp))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = "Direct Code & Scaffold Import",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = HazonText
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "কাস্টম HTML/CSS/JS কোড সরাসরি পেস্ট করে নতুন প্রজেক্ট তৈরি করুন:",
                            fontSize = 11.sp,
                            color = HazonTextMuted
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rawProjectTitle,
                            onValueChange = { rawProjectTitle = it },
                            label = { Text("Project Title", fontSize = 10.sp, color = HazonTextMuted) },
                            singleLine = true,
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder,
                                focusedContainerColor = HazonBackground,
                                unfocusedContainerColor = HazonBackground
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = rawHtmlInput,
                            onValueChange = { rawHtmlInput = it },
                            label = { Text("index.html Content", fontSize = 10.sp, color = HazonTextMuted) },
                            maxLines = 6,
                            textStyle = androidx.compose.ui.text.TextStyle(
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                color = HazonText
                            ),
                            shape = RoundedCornerShape(6.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = HazonPurple,
                                unfocusedBorderColor = HazonBorder,
                                focusedContainerColor = HazonBackground,
                                unfocusedContainerColor = HazonBackground
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            onClick = {
                                onImportRawCode(rawProjectTitle, rawHtmlInput, rawCssInput, rawJsInput)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = HazonPurpleDark),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Storage,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Save to ROM & Launch Preview",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(HazonPanel2)
                    .border(1.dp, HazonBorder, RoundedCornerShape(8.dp))
                    .clickable { onDismiss() }
                    .padding(vertical = 10.dp)
            ) {
                Text(
                    text = "Close",
                    color = HazonTextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
