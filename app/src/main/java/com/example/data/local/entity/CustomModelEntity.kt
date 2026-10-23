package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "custom_models")
data class CustomModelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val endpointUrl: String,
    val apiKey: String,
    val modelTag: String,
    val providerType: String = "openai_compatible",
    val createdAt: Long = System.currentTimeMillis()
)
