@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class, kotlinx.serialization.InternalSerializationApi::class)

package com.zerohunger.app.data

import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.delay
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName

import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import io.github.jan.supabase.realtime.channel
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.launchIn

// ===== TOP-LEVEL DTOs (must be outside any class for @Serializable to work reliably) =====

@Serializable
data class FoodItemDto(
    val name: String,
    val quantity: Int,
    val unit: String,
    val servings: Int,
    @SerialName("expiry_time") val expiryTime: Long,
    @SerialName("prepared_time") val preparedTime: Long,
    val status: String,
    @SerialName("donor_name") val donorName: String,
    @SerialName("pickup_or_delivery") val pickupOrDelivery: String,
    val location: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    @SerialName("created_at") val createdAt: Long,
    @SerialName("image_url") val imageUrl: String? = null,
    val vegetarian: Boolean = true,
    val allergens: String = "None",
    @SerialName("storage_condition") val storageCondition: String = "Room Temperature",
    val packed: Boolean = true,
    @SerialName("admin_safety_approval") val adminSafetyApproval: Boolean = false,
    @SerialName("is_recurring") val isRecurring: Boolean = false,
    @SerialName("voice_note_url") val voiceNoteUrl: String? = null
)

@Serializable
data class QueueDto(
    val name: String,
    @SerialName("people_count") val peopleCount: Int,
    @SerialName("requested_quantity") val requestedQuantity: Int,
    val urgency: String,
    val location: String,
    @SerialName("phone_number") val phoneNumber: String,
    val status: String,
    val timestamp: Long,
    val urgent: Boolean = false,
    @SerialName("pickup_date") val pickupDate: Long? = null,
    @SerialName("pickup_time") val pickupTime: String = "",
    val otp: String = "",
    @SerialName("request_status") val requestStatus: String = "RECEIVED"
)

@Serializable
data class HistoryDto(
    @SerialName("action_type") val actionType: String,
    @SerialName("entity_id") val entityId: Long,
    val timestamp: Long,
    val description: String,
    @SerialName("can_undo") val canUndo: Boolean
)

// ===== REPOSITORY =====

class AppRepository(
    private val foodDao: FoodItemDao,
    private val queueDao: BeneficiaryQueueDao,
    private val historyDao: ActionHistoryDao
) {

    private val postgrest = SupabaseClient.client.postgrest
    private val storage = SupabaseClient.client.storage
    private val realtime = SupabaseClient.client.realtime
    private val repositoryScope = CoroutineScope(Dispatchers.IO)

    private val _syncErrorMessage = MutableStateFlow<String?>(null)
    val syncErrorMessage: Flow<String?> = _syncErrorMessage

    // Exposed flows now come directly from Room (Offline-First)
    val availableFood: Flow<List<FoodItem>> = foodDao.getAvailableFood()
    val waitingQueue: Flow<List<BeneficiaryQueue>> = queueDao.getWaitingQueue()
    val actionHistory: Flow<List<ActionHistory>> = historyDao.getHistory()

    init {
        // 1. Initial manual sync
        repositoryScope.launch {
            try {
                syncWithRemote()
            } catch (e: Exception) {
                android.util.Log.e("SupabaseSync", "Initial sync failed: ${e.message}", e)
            }
        }

        // 2. Start realtime listeners
        setupRealtimeSubscriptions()

        // 3. Periodic fallback sync every 30 seconds (in case realtime disconnects)
        repositoryScope.launch {
            while (true) {
                delay(30_000)
                try {
                    syncWithRemote()
                } catch (e: Exception) {
                    android.util.Log.e("SupabaseSync", "Periodic sync failed: ${e.message}", e)
                }
            }
        }
    }

    private fun setupRealtimeSubscriptions() {
        repositoryScope.launch {
            try {
                realtime.connect()
                val channel = realtime.channel("public-tables-changes")

                channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "food_items"
                }.onEach {
                    syncFoodItemsFromRemote()
                }.launchIn(this)

                channel.postgresChangeFlow<PostgresAction>(schema = "public") {
                    table = "beneficiary_queue"
                }.onEach {
                    syncQueueFromRemote()
                }.launchIn(this)

                channel.subscribe()
            } catch (e: Exception) {
                android.util.Log.e("SupabaseRealtime", "Realtime setup failed: ${e.message}")
            }
        }
    }

    // ===== SYNC ENGINE =====

    private suspend fun syncWithRemote() {
        // STEP 1: Push unsynced local data to Supabase
        pushUnsyncedFood()
        pushUnsyncedQueue()
        pushUnsyncedHistory()

        // STEP 2: Pull fresh data from Supabase (only overwrites synced records)
        syncFoodItemsFromRemote()
        syncQueueFromRemote()
        syncHistoryFromRemote()
    }

    private suspend fun pushUnsyncedFood() {
        val unsyncedFood = foodDao.getUnsyncedItems()
        for (item in unsyncedFood) {
            val dto = FoodItemDto(
                name = item.name,
                quantity = item.quantity,
                unit = item.unit,
                servings = item.servings,
                expiryTime = item.expiryTime,
                preparedTime = item.preparedTime,
                status = item.status,
                donorName = item.donorName,
                pickupOrDelivery = item.pickupOrDelivery,
                location = item.location,
                latitude = item.latitude,
                longitude = item.longitude,
                createdAt = item.createdAt,
                imageUrl = item.imageUrl,
                vegetarian = item.vegetarian,
                allergens = item.allergens,
                storageCondition = item.storageCondition,
                packed = item.packed,
                adminSafetyApproval = item.adminSafetyApproval,
                isRecurring = item.isRecurring,
                voiceNoteUrl = item.voiceNoteUrl
            )
            try {
                postgrest["food_items"].insert(dto)
                foodDao.update(item.copy(isSynced = true))
                _syncErrorMessage.value = null
                android.util.Log.d("SupabaseSync", "Pushed food: ${item.name}")
            } catch (e: Exception) {
                _syncErrorMessage.value = "Food sync error: ${e.message}"
                android.util.Log.e("SupabaseSync", "Failed to push food '${item.name}': ${e.message}", e)
            }
        }
    }

    private suspend fun pushUnsyncedQueue() {
        val unsyncedQueue = queueDao.getUnsyncedItems()
        for (item in unsyncedQueue) {
            val dto = QueueDto(
                name = item.name,
                peopleCount = item.peopleCount,
                requestedQuantity = item.requestedQuantity,
                urgency = item.urgency,
                location = item.location,
                phoneNumber = item.phoneNumber,
                status = item.status,
                timestamp = item.timestamp,
                urgent = item.urgent,
                pickupDate = item.pickupDate,
                pickupTime = item.pickupTime,
                otp = item.otp,
                requestStatus = item.requestStatus
            )
            try {
                postgrest["beneficiary_queue"].insert(dto)
                queueDao.update(item.copy(isSynced = true))
                _syncErrorMessage.value = null
                android.util.Log.d("SupabaseSync", "Pushed queue: ${item.name}")
            } catch (e: Exception) {
                _syncErrorMessage.value = "Queue sync error: ${e.message}"
                android.util.Log.e("SupabaseSync", "Failed to push queue '${item.name}': ${e.message}", e)
            }
        }
    }

    private suspend fun pushUnsyncedHistory() {
        val unsyncedHistory = historyDao.getUnsyncedItems()
        for (item in unsyncedHistory) {
            val dto = HistoryDto(
                actionType = item.actionType,
                entityId = item.entityId,
                timestamp = item.timestamp,
                description = item.description,
                canUndo = item.canUndo
            )
            try {
                postgrest["action_history"].insert(dto)
                historyDao.update(item.copy(isSynced = true))
                android.util.Log.d("SupabaseSync", "Pushed history: ${item.description}")
            } catch (e: Exception) {
                android.util.Log.e("SupabaseSync", "Failed to push history: ${e.message}", e)
            }
        }
    }

    private suspend fun syncFoodItemsFromRemote() {
        try {
            val remoteFood = postgrest["food_items"].select { order("created_at", Order.ASCENDING) }.decodeList<FoodItem>()
            foodDao.clearSyncedItems()
            foodDao.insertAll(remoteFood.map { it.copy(isSynced = true) })
            android.util.Log.d("SupabaseSync", "Pulled ${remoteFood.size} food items from cloud")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSync", "Failed to pull food: ${e.message}", e)
        }
    }

    private suspend fun syncQueueFromRemote() {
        try {
            val remoteQueue = postgrest["beneficiary_queue"].select { order("timestamp", Order.ASCENDING) }.decodeList<BeneficiaryQueue>()
            queueDao.clearSyncedItems()
            queueDao.insertAll(remoteQueue.map { it.copy(isSynced = true) })
            android.util.Log.d("SupabaseSync", "Pulled ${remoteQueue.size} queue items from cloud")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSync", "Failed to pull queue: ${e.message}", e)
        }
    }

    private suspend fun syncHistoryFromRemote() {
        try {
            val remoteHistory = postgrest["action_history"].select { order("timestamp", Order.DESCENDING) }.decodeList<ActionHistory>()
            historyDao.clearSyncedItems()
            historyDao.insertAll(remoteHistory.map { it.copy(isSynced = true) })
            android.util.Log.d("SupabaseSync", "Pulled ${remoteHistory.size} history items from cloud")
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSync", "Failed to pull history: ${e.message}", e)
        }
    }

    // ===== PUBLIC API (used by ViewModels) =====

    suspend fun uploadImage(bytes: ByteArray): String? {
        return try {
            val fileName = "${System.currentTimeMillis()}.jpg"
            storage["food_images"].upload(fileName, bytes, upsert = true)
            storage["food_images"].publicUrl(fileName)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun addDonation(item: FoodItem) {
        // Save locally first (isSynced = false)
        val newItem = item.copy(isSynced = false)
        val localId = foodDao.insertFoodItem(newItem)

        historyDao.insertHistory(
            ActionHistory(
                actionType = "DONATION",
                entityId = localId,
                timestamp = System.currentTimeMillis(),
                description = "Added donation: ${item.name} (${item.quantity} ${item.unit})",
                canUndo = true,
                isSynced = false
            )
        )

        // Trigger immediate sync
        repositoryScope.launch {
            try {
                syncWithRemote()
            } catch (e: Exception) {
                android.util.Log.e("SupabaseSync", "Immediate sync on donation failed: ${e.message}", e)
            }
        }
    }

    suspend fun addBeneficiary(beneficiary: BeneficiaryQueue) {
        // Save locally first
        val newItem = beneficiary.copy(isSynced = false)
        val localId = queueDao.insertQueueItem(newItem)

        historyDao.insertHistory(
            ActionHistory(
                actionType = "REQUEST",
                entityId = localId,
                timestamp = System.currentTimeMillis(),
                description = "Added request for ${beneficiary.name}",
                canUndo = true,
                isSynced = false
            )
        )

        // Trigger immediate sync
        repositoryScope.launch {
            try {
                syncWithRemote()
            } catch (e: Exception) {
                android.util.Log.e("SupabaseSync", "Immediate sync on request failed: ${e.message}", e)
            }
        }
    }

    suspend fun distributeFood(beneficiaryId: Long, foodItemId: Long, quantityToDistribute: Int) {
        try {
            val food = postgrest["food_items"].select { filter { eq("id", foodItemId) } }.decodeSingleOrNull<FoodItem>() ?: return
            val beneficiary = postgrest["beneficiary_queue"].select { filter { eq("id", beneficiaryId) } }.decodeSingleOrNull<BeneficiaryQueue>() ?: return

            val newQuantity = food.quantity - quantityToDistribute
            val status = if (newQuantity == 0) "DISTRIBUTED" else "AVAILABLE"

            postgrest["food_items"].update({
                set("quantity", newQuantity)
                set("status", status)
            }) { filter { eq("id", foodItemId) } }

            postgrest["beneficiary_queue"].update({
                set("status", "SERVED")
                set("request_status", "COMPLETED")
            }) { filter { eq("id", beneficiaryId) } }

            postgrest["action_history"].insert(
                ActionHistory(
                    actionType = "DISTRIBUTION",
                    entityId = foodItemId,
                    timestamp = System.currentTimeMillis(),
                    description = "Distributed $quantityToDistribute ${food.unit} of ${food.name} to ${beneficiary.name}",
                    canUndo = false
                )
            )
            // Trigger sync to pull the edits down immediately
            syncWithRemote()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun undoLastAction() {
        try {
            val lastAction = postgrest["action_history"].select {
                filter { eq("can_undo", true) }
                order("timestamp", Order.DESCENDING)
                limit(1)
            }.decodeSingleOrNull<ActionHistory>() ?: return

            when (lastAction.actionType) {
                "DONATION" -> postgrest["food_items"].delete { filter { eq("id", lastAction.entityId) } }
                "REQUEST" -> postgrest["beneficiary_queue"].delete { filter { eq("id", lastAction.entityId) } }
            }

            postgrest["action_history"].update({
                set("can_undo", false)
                set("description", "${lastAction.description} (UNDONE)")
            }) { filter { eq("id", lastAction.id!!) } }

            syncWithRemote()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun loadSampleData() {}

    suspend fun toggleSafetyApproval(foodItemId: Long, newApproval: Boolean) {
        try {
            postgrest["food_items"].update({
                set("admin_safety_approval", newApproval)
            }) { filter { eq("id", foodItemId) } }
            syncWithRemote()
        } catch (e: Exception) {
            android.util.Log.e("SupabaseSync", "Failed to toggle safety: ${e.message}", e)
        }
    }
}
