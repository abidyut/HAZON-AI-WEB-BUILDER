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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProjectTemplate
import com.example.data.TemplatesData
import com.example.ui.theme.HazonBackground
import com.example.ui.theme.HazonBorder
import com.example.ui.theme.HazonCyan
import com.example.ui.theme.HazonPanel
import com.example.ui.theme.HazonPanel2
import com.example.ui.theme.HazonPurple
import com.example.ui.theme.HazonPurpleDark
import com.example.ui.theme.HazonText
import com.example.ui.theme.HazonTextMuted

@Composable
fun TemplatesScreen(
    onSelectTemplate: (ProjectTemplate) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(HazonBackground)
            .padding(16.dp)
            .testTag("templates_screen")
    ) {
        item {
            Text(
                text = "Templates",
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold,
                color = HazonText
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Start from a proven layout and customize it with Hazon AI.",
                fontSize = 12.sp,
                color = HazonTextMuted
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        items(TemplatesData.templates) { template ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(HazonPanel)
                    .border(1.dp, HazonBorder, RoundedCornerShape(14.dp))
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
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        when (template.category) {
                                            "CREATIVE" -> Color(0x288B5CF6)
                                            "PRODUCTIVITY" -> Color(0x283B82F6)
                                            "NEXT-GEN AI" -> Color(0x28A78BFA)
                                            else -> Color(0x2854DBA4)
                                        }
                                    )
                            ) {
                                Icon(
                                    imageVector = when (template.category) {
                                        "CREATIVE" -> Icons.Default.Palette
                                        "PRODUCTIVITY" -> Icons.Default.BarChart
                                        "NEXT-GEN AI" -> Icons.Default.AutoAwesome
                                        else -> Icons.Default.ShoppingBag
                                    },
                                    contentDescription = null,
                                    tint = HazonPurple,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = template.title,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HazonText
                                )
                                Text(
                                    text = template.category,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = HazonCyan,
                                    letterSpacing = 1.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = template.description,
                        fontSize = 12.sp,
                        color = HazonTextMuted,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "3 Files included · HTML, CSS, JS",
                            fontSize = 10.sp,
                            color = Color(0xFF77758E)
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(HazonPurpleDark)
                                .clickable { onSelectTemplate(template) }
                                .padding(horizontal = 12.dp, vertical = 7.dp)
                        ) {
                            Text(
                                text = "Use template",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }
    }
}
