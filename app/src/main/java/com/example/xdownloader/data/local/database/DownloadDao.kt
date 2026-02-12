package com.example.xdownloader.data.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.xdownloader.data.local.database.entities.DownloadTaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM download_tasks ORDER BY createdAt DESC")
    fun getAllDownloads(): Flow<List<DownloadTaskEntity>>

    @Query("SELECT * FROM download_tasks WHERE status = 'DOWNLOADING' OR status = 'PENDING' ORDER BY createdAt ASC")
    fun getActiveDownloads(): Flow<List<DownloadTaskEntity>>

    @Query("SELECT * FROM download_tasks WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadTaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadTaskEntity): Long

    @Update
    suspend fun updateDownload(download: DownloadTaskEntity)

    @Query("UPDATE download_tasks SET status = :status, progress = :progress, downloadedBytes = :downloadedBytes WHERE id = :id")
    suspend fun updateDownloadProgress(
        id: Long,
        status: String,
        progress: Int,
        downloadedBytes: Long
    )

    @Query("UPDATE download_tasks SET status = :status, filePath = :filePath, thumbnailPath = :thumbnailPath, completedAt = :completedAt WHERE id = :id")
    suspend fun markAsCompleted(
        id: Long,
        status: String,
        filePath: String,
        thumbnailPath: String,
        completedAt: Long
    )

    @Query("DELETE FROM download_tasks WHERE id = :id")
    suspend fun deleteDownload(id: Long)

    @Query("DELETE FROM download_tasks")
    suspend fun deleteAllDownloads()

    @Query("DELETE FROM download_tasks WHERE status = 'COMPLETED' OR status = 'FAILED' OR status = 'CANCELLED'")
    suspend fun deleteInactiveDownloads()
}
