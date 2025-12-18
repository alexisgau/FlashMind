package com.alexisgau.synapai.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.alexisgau.synapai.data.local.entities.CategoryEntity
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


    @Query("""
    UPDATE category 
    SET isDeleted = :isDeleted, isSynced = :isSynced 
    WHERE id = :categoryId
""")
    suspend fun markCategoryForDeletion(
        categoryId: Int,
        isDeleted: Boolean = true,
        isSynced: Boolean = false
    )


    @Query("SELECT * FROM category WHERE isDeleted = 0 AND userId = :userId")
    fun getAllCategories(userId: String): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM category WHERE isSynced = 0 AND userId = :userId")
    suspend fun getUnsyncedCategories(userId: String): List<CategoryEntity>


    @Query("""
    UPDATE category 
    SET isSynced = :isSynced 
    WHERE id = :categoryId
""")
    suspend fun markAsSynced(
        categoryId: Int,
        isSynced: Boolean = true
    )


    @Query("SELECT COUNT(*) FROM lessons WHERE categoryId = :categoryId")
    fun getLessonCountByCategory(categoryId: Int): Flow<Int>
}

