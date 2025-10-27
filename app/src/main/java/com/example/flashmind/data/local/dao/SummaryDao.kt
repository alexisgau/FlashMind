package com.example.flashmind.data.local.dao


import androidx.room.*
import com.example.flashmind.data.local.entities.SummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity): Long // Returns the inserted ID

    @Update
    suspend fun updateSummary(summary: SummaryEntity) // If summaries can be edited

    @Query("UPDATE summaries SET isDeleted = 1, isSynced = 0 WHERE summaryId = :summaryId")
    suspend fun markSummaryForDeletion(summaryId: Int)

    @Query("SELECT * FROM summaries WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedSummaries(userId: String): List<SummaryEntity>

    @Query("UPDATE summaries SET isSynced = 1 WHERE summaryId = :summaryId")
    suspend fun markSummaryAsSynced(summaryId: Int)

    @Query("DELETE FROM summaries WHERE summaryId = :summaryId")
    suspend fun deleteSummaryById(summaryId: Int) // Physical delete

    @Query("SELECT * FROM summaries WHERE lessonId = :lessonId AND isDeleted = 0 ORDER BY summaryId DESC") // Show newest first
    fun getSummariesByLessonId(lessonId: Int): Flow<List<SummaryEntity>>

    @Query("SELECT * FROM summaries WHERE summaryId = :summaryId")
    suspend fun getSummaryById(summaryId: Int): SummaryEntity? // Optional: If needed
}