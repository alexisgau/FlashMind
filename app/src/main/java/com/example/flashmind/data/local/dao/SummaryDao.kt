package com.example.flashmind.data.local.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.flashmind.data.local.entities.SummaryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SummaryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSummary(summary: SummaryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(summaries: List<SummaryEntity>)

    @Update
    suspend fun updateSummary(summary: SummaryEntity)

    @Query("UPDATE summaries SET isDeleted = 1, isSynced = 0 WHERE summaryId = :summaryId")
    suspend fun markSummaryForDeletion(summaryId: Int)

    @Query("SELECT * FROM summaries WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedSummaries(userId: String): List<SummaryEntity>

    @Query("UPDATE summaries SET isSynced = 1 WHERE summaryId = :summaryId")
    suspend fun markSummaryAsSynced(summaryId: Int)

    @Query("DELETE FROM summaries WHERE summaryId = :summaryId")
    suspend fun deleteSummaryById(summaryId: Int)

    @Query("SELECT * FROM summaries WHERE lessonId = :lessonId AND isDeleted = 0 ORDER BY summaryId DESC")
    fun getSummariesByLessonId(lessonId: Int): Flow<List<SummaryEntity>>

    @Query("SELECT * FROM summaries WHERE summaryId = :summaryId")
    suspend fun getSummaryById(summaryId: Int): SummaryEntity?
}