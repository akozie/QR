package com.woleapp.netpos.qrgenerator.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.woleapp.netpos.qrgenerator.model.DomainQREntity
import com.woleapp.netpos.qrgenerator.model.GenerateQRResponse
import com.woleapp.netpos.qrgenerator.model.pay.QrTransactionResponseModel

@Database(entities = [DomainQREntity::class, QrTransactionResponseModel::class], version = 8, exportSchema = false)
@TypeConverters(AppTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getQrDao(): QrDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabaseInstance(context: Context) = INSTANCE ?: synchronized(this) {
            INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
        }

        private fun buildDatabase(context: Context) =
            Room.databaseBuilder(context, AppDatabase::class.java, "tally-db")
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
    }
}
