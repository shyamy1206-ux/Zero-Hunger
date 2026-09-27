package com.zerohunger.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.tasks.await
import java.util.Locale

data class LocationData(val address: String, val latitude: Double, val longitude: Double)

object LocationHelper {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentAddress(context: Context): String? {
        val data = getCurrentLocationData(context)
        return data?.address
    }
    
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocationData(context: Context): LocationData? {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        return try {
            val location = fusedLocationClient.getCurrentLocation(
                Priority.PRIORITY_HIGH_ACCURACY,
                CancellationTokenSource().token
            ).await()
            
            if (location != null) {
                val geocoder = Geocoder(context, Locale.getDefault())
                @Suppress("DEPRECATION")
                val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                val addressString = if (!addresses.isNullOrEmpty()) {
                    addresses[0].getAddressLine(0) ?: "${location.latitude}, ${location.longitude}"
                } else {
                    "${location.latitude}, ${location.longitude}"
                }
                LocationData(addressString, location.latitude, location.longitude)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
