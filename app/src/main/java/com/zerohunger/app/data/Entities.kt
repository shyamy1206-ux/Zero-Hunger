@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class, kotlinx.serialization.InternalSerializationApi::class)

package com.zerohunger.app.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@Entity(tableName = "food_items")
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    val quantity: Int,
    val unit: String,
    val servings: Int,
    @ColumnInfo(name = "expiry_time")
    @SerialName("expiry_time") val expiryTime: Long,
    @ColumnInfo(name = "prepared_time")
    @SerialName("prepared_time") val preparedTime: Long,
    val status: String,
    @ColumnInfo(name = "donor_name")
    @SerialName("donor_name") val donorName: String,
    @ColumnInfo(name = "pickup_or_delivery")
    @SerialName("pickup_or_delivery") val pickupOrDelivery: String = "Pickup",
    val location: String = "Unknown",
    @SerialName("latitude")
    val latitude: Double? = null,
    @SerialName("longitude")
    val longitude: Double? = null,
    @ColumnInfo(name = "created_at")
    @SerialName("created_at")
    val createdAt: Long = System.currentTimeMillis(),
    @ColumnInfo(name = "image_url")
    @SerialName("image_url")
    var imageUrl: String? = null,
    
    // V3 Food Safety Expansion
    val vegetarian: Boolean = true,
    val allergens: String = "None",
    @ColumnInfo(name = "storage_condition")
    @SerialName("storage_condition") val storageCondition: String = "Room Temperature",
    val packed: Boolean = true,
    @ColumnInfo(name = "admin_safety_approval")
    @SerialName("admin_safety_approval") val adminSafetyApproval: Boolean = false,
    
    @ColumnInfo(name = "is_recurring")
    @SerialName("is_recurring") val isRecurring: Boolean = false,
    
    @ColumnInfo(name = "voice_note_url")
    @SerialName("voice_note_url") val voiceNoteUrl: String? = null,

    // V3 Offline Sync tracking
    @ColumnInfo(name = "is_synced")
    @Transient // Ignore in Supabase JSON
    val isSynced: Boolean = true
)

@Serializable
@Entity(tableName = "beneficiary_queue")
data class BeneficiaryQueue(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    val name: String,
    @ColumnInfo(name = "people_count")
    @SerialName("people_count") val peopleCount: Int,
    @ColumnInfo(name = "requested_quantity")
    @SerialName("requested_quantity") val requestedQuantity: Int,
    val urgency: String,
    val location: String,
    @ColumnInfo(name = "phone_number")
    @SerialName("phone_number") val phoneNumber: String,
    val status: String = "WAITING",
    val timestamp: Long = System.currentTimeMillis(),
    
    // V3 Expansion
    val urgent: Boolean = false,
    @ColumnInfo(name = "pickup_date")
    @SerialName("pickup_date") val pickupDate: Long? = null,
    @ColumnInfo(name = "pickup_time")
    @SerialName("pickup_time") val pickupTime: String = "",
    val otp: String = "",
    
    @ColumnInfo(name = "request_status")
    @SerialName("request_status") val requestStatus: String = "RECEIVED",

    // V3 Offline Sync tracking
    @ColumnInfo(name = "is_synced")
    @Transient // Ignore in Supabase JSON
    val isSynced: Boolean = true
)

@Serializable
@Entity(tableName = "action_history")
data class ActionHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long? = null,
    @ColumnInfo(name = "action_type")
    @SerialName("action_type") val actionType: String,
    @ColumnInfo(name = "entity_id")
    @SerialName("entity_id") val entityId: Long,
    val timestamp: Long,
    val description: String,
    @ColumnInfo(name = "can_undo")
    @SerialName("can_undo") val canUndo: Boolean,
    
    // V3 Offline Sync tracking
    @ColumnInfo(name = "is_synced")
    @Transient // Ignore in Supabase JSON
    val isSynced: Boolean = true
)
