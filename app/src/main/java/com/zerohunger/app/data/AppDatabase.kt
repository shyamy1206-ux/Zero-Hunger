package com.zerohunger.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [FoodItem::class, BeneficiaryQueue::class, ActionHistory::class], version = 3, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun foodItemDao(): FoodItemDao
    abstract fun beneficiaryQueueDao(): BeneficiaryQueueDao
    abstract fun actionHistoryDao(): ActionHistoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE food_items ADD COLUMN is_recurring INTEGER NOT NULL DEFAULT 0")
                db.execSQL("ALTER TABLE food_items ADD COLUMN voice_note_url TEXT")
                db.execSQL("ALTER TABLE beneficiary_queue ADD COLUMN request_status TEXT NOT NULL DEFAULT 'RECEIVED'")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "zerohunger_database"
                )
                .addMigrations(MIGRATION_2_3)
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
