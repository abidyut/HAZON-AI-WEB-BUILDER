package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_files")
data class ProjectFileEntity(
    @PrimaryKey
    val id: String,
    val projectId: String,
    val fileName: String,
    val fileType: String,
    val content: String,
    val isBinary: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
