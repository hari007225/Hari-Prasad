package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_packets")
data class AuthPacketEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val packetId: String,
    val authProvider: String, // "EMAIL", "PHONE", "FACEBOOK", "GOOGLE"
    val identifier: String,   // email, phone number, or social ID
    val displayName: String,
    val passwordMasked: String,
    val phoneNumber: String? = null,
    val avatarColorHex: String = "#6366F1",
    val sessionStatus: String = "ACTIVE", // "ACTIVE", "VERIFIED", "STORED"
    val packetJson: String,
    val timestamp: Long = System.currentTimeMillis()
)
