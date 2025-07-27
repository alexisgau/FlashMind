package com.example.flashmind.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Query
import androidx.room.Upsert
import com.example.flashmind.data.local.entities.CategoryEntity
import com.example.flashmind.domain.model.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Upsert
    suspend fun insertCategory(categoryEntity: CategoryEntity)

    @Delete
    suspend fun deleteCategory(categoryEntity: CategoryEntity)

    @Query("SELECT * FROM category")
     fun getAllCategories(): Flow<List<CategoryEntity>>

     @Query("SELECT * FROM category WHERE isSynced = false AND userId = :userId")
     fun getUnsyncedCategories(userId: String) : List<Category>


     @Query("UPDATE category SET isSynced = true WHERE id = :categoryId")
     fun markAsSynced(categoryId: Int)
}

