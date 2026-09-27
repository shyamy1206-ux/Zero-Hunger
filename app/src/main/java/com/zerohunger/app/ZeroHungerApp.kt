package com.zerohunger.app

import android.app.Application
import com.zerohunger.app.data.AppRepository

import com.zerohunger.app.data.AppDatabase

class ZeroHungerApp : Application() {
    val database by lazy { AppDatabase.getDatabase(this) }
    val repository by lazy { 
        AppRepository(
            foodDao = database.foodItemDao(),
            queueDao = database.beneficiaryQueueDao(),
            historyDao = database.actionHistoryDao()
        ) 
    }
}
