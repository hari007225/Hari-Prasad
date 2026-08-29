package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [AuthPacketEntity::class],
    version = 1,
    exportSchema = false
)
abstract class TalkLoopDatabase : RoomDatabase() {

    abstract fun authPacketDao(): AuthPacketDao

    companion object {
        @Volatile
        private var INSTANCE: TalkLoopDatabase? = null

        fun getDatabase(context: Context): TalkLoopDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TalkLoopDatabase::class.java,
                    "talkloop_auth_packets.db"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                instance
            }
        }
    }
}
