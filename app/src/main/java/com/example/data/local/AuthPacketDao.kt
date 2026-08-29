package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthPacketDao {

    @Query("SELECT * FROM auth_packets ORDER BY timestamp DESC")
    fun getAllPackets(): Flow<List<AuthPacketEntity>>

    @Query("SELECT * FROM auth_packets WHERE identifier = :identifier LIMIT 1")
    suspend fun getPacketByIdentifier(identifier: String): AuthPacketEntity?

    @Query("SELECT * FROM auth_packets WHERE packetId = :packetId LIMIT 1")
    suspend fun getPacketByPacketId(packetId: String): AuthPacketEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPacket(packet: AuthPacketEntity): Long

    @Update
    suspend fun updatePacket(packet: AuthPacketEntity)

    @Delete
    suspend fun deletePacket(packet: AuthPacketEntity)

    @Query("DELETE FROM auth_packets WHERE id = :id")
    suspend fun deletePacketById(id: Int)

    @Query("DELETE FROM auth_packets")
    suspend fun clearAllPackets()
}
