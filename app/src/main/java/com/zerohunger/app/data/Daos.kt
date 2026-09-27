package com.zerohunger.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE status = 'AVAILABLE' ORDER BY created_at ASC")
    fun getAvailableFood(): Flow<List<FoodItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFoodItem(item: FoodItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<FoodItem>)

    @Query("SELECT * FROM food_items WHERE is_synced = 0")
    suspend fun getUnsyncedItems(): List<FoodItem>

    @Query("UPDATE food_items SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Update
    suspend fun update(item: FoodItem)
    
    @Query("DELETE FROM food_items WHERE is_synced = 1")
    suspend fun clearSyncedItems()
    
    @Query("DELETE FROM food_items")
    suspend fun clearAll()
}

@Dao
interface BeneficiaryQueueDao {
    @Query("SELECT * FROM beneficiary_queue WHERE request_status != 'COMPLETED' ORDER BY timestamp ASC")
    fun getWaitingQueue(): Flow<List<BeneficiaryQueue>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQueueItem(item: BeneficiaryQueue): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<BeneficiaryQueue>)

    @Query("SELECT * FROM beneficiary_queue WHERE is_synced = 0")
    suspend fun getUnsyncedItems(): List<BeneficiaryQueue>

    @Query("UPDATE beneficiary_queue SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Update
    suspend fun update(item: BeneficiaryQueue)
    
    @Query("DELETE FROM beneficiary_queue WHERE is_synced = 1")
    suspend fun clearSyncedItems()
    
    @Query("DELETE FROM beneficiary_queue")
    suspend fun clearAll()
}

@Dao
interface ActionHistoryDao {
    @Query("SELECT * FROM action_history ORDER BY timestamp DESC")
    fun getHistory(): Flow<List<ActionHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(item: ActionHistory): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<ActionHistory>)

    @Query("SELECT * FROM action_history WHERE is_synced = 0")
    suspend fun getUnsyncedItems(): List<ActionHistory>
    
    @Query("UPDATE action_history SET is_synced = 1 WHERE id = :id")
    suspend fun markAsSynced(id: Long)

    @Update
    suspend fun update(item: ActionHistory)
    
    @Query("DELETE FROM action_history WHERE is_synced = 1")
    suspend fun clearSyncedItems()
    
    @Query("DELETE FROM action_history")
    suspend fun clearAll()
}
