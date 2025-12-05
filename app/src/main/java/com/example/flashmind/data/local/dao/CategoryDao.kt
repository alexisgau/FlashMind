package com.example.flashmind.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.flashmind.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Upsert
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("DELETE FROM category WHERE id = :categoryId")
    suspend fun deleteCategoryById(categoryId: Int)


    @Query("UPDATE category SET isDeleted = 1, isSynced = 0 WHERE id = :categoryId")
    suspend fun markCategoryForDeletion(categoryId: Int)

    @Query("SELECT * FROM category WHERE isDeleted = 0 AND userId = :userId")
    fun getAllCategories(userId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE isSynced = false AND userId = :userId")
    suspend fun getUnsyncedCategories(userId: String): List<CategoryEntity>


    @Query("UPDATE category SET isSynced = true WHERE id = :categoryId")
    fun markAsSynced(categoryId: Int)

    @Query("SELECT COUNT(*) FROM lessons WHERE categoryId = :categoryId")
    fun getLessonCountByCategory(categoryId: Int): Flow<Int>
}

