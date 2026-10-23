package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.ApiSettingsEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    // Projects
    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM projects ORDER BY updatedAt DESC")
    suspend fun getAllProjectsSync(): List<ProjectEntity>

    @Query("SELECT * FROM projects WHERE id = :id LIMIT 1")
    suspend fun getProjectById(id: String): ProjectEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Query("DELETE FROM projects WHERE id = :id")
    suspend fun deleteProjectById(id: String)

    // Project Files
    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY fileName ASC")
    fun getFilesForProject(projectId: String): Flow<List<ProjectFileEntity>>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId ORDER BY fileName ASC")
    suspend fun getFilesForProjectSync(projectId: String): List<ProjectFileEntity>

    @Query("SELECT * FROM project_files WHERE projectId = :projectId AND fileName = :fileName LIMIT 1")
    suspend fun getFileByName(projectId: String, fileName: String): ProjectFileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: ProjectFileEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFiles(files: List<ProjectFileEntity>)

    @Query("DELETE FROM project_files WHERE id = :id")
    suspend fun deleteFileById(id: String)

    @Query("DELETE FROM project_files WHERE projectId = :projectId")
    suspend fun deleteFilesForProject(projectId: String)

    // Chat Messages
    @Query("SELECT * FROM chat_messages WHERE projectId = :projectId ORDER BY timestamp ASC")
    fun getMessagesForProject(projectId: String): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity)

    @Query("DELETE FROM chat_messages WHERE projectId = :projectId")
    suspend fun deleteMessagesForProject(projectId: String)

    // API Settings
    @Query("SELECT * FROM api_settings WHERE id = 1 LIMIT 1")
    fun getApiSettings(): Flow<ApiSettingsEntity?>

    @Query("SELECT * FROM api_settings WHERE id = 1 LIMIT 1")
    suspend fun getApiSettingsSync(): ApiSettingsEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateSettings(settings: ApiSettingsEntity)

    // Dynamic Custom Models
    @Query("SELECT * FROM custom_models ORDER BY createdAt ASC")
    fun getAllCustomModels(): Flow<List<com.example.data.local.entity.CustomModelEntity>>

    @Query("SELECT * FROM custom_models ORDER BY createdAt ASC")
    suspend fun getAllCustomModelsSync(): List<com.example.data.local.entity.CustomModelEntity>

    @Query("SELECT * FROM custom_models WHERE id = :id LIMIT 1")
    suspend fun getCustomModelById(id: String): com.example.data.local.entity.CustomModelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomModel(model: com.example.data.local.entity.CustomModelEntity)

    @Query("DELETE FROM custom_models WHERE id = :id")
    suspend fun deleteCustomModelById(id: String)
}
