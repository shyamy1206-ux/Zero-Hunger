package com.zerohunger.app.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.zerohunger.app.data.AppRepository
import com.zerohunger.app.data.BeneficiaryQueue
import com.zerohunger.app.data.FoodItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CommunityViewModel(private val repository: AppRepository) : ViewModel() {

    val availableFood = repository.availableFood.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val myRequests = repository.waitingQueue.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    suspend fun donateFood(item: FoodItem, imageBytes: ByteArray? = null) {
        try {
            Log.d("ZeroHunger", "Attempting to donate food: ${item.name}")
            var finalItem = item
            if (imageBytes != null) {
                val imageUrl = repository.uploadImage(imageBytes)
                if (imageUrl != null) {
                    finalItem = item.copy(imageUrl = imageUrl)
                }
            }
            repository.addDonation(finalItem)
            Log.d("ZeroHunger", "Donation successful!")
        } catch (e: Exception) {
            Log.e("ZeroHunger", "Failed to donate food", e)
        }
    }

    suspend fun requestFood(request: BeneficiaryQueue) {
        try {
            Log.d("ZeroHunger", "Attempting to request food: ${request.name}")
            repository.addBeneficiary(request)
            Log.d("ZeroHunger", "Request successful!")
        } catch (e: Exception) {
            Log.e("ZeroHunger", "Failed to request food", e)
        }
    }
}

class CommunityViewModelFactory(private val repository: AppRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommunityViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommunityViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
