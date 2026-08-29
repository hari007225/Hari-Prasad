package com.example.data.repository

import com.example.data.local.AuthPacketDao
import com.example.data.local.AuthPacketEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.UUID

class AuthRepository(private val authPacketDao: AuthPacketDao) {

    val allPackets: Flow<List<AuthPacketEntity>> = authPacketDao.getAllPackets()

    suspend fun saveAuthPacket(
        provider: String,
        identifier: String,
        displayName: String,
        rawPassword: String,
        phoneNumber: String? = null,
        avatarColor: String = "#6366F1",
        customJsonPacket: String? = null
    ): AuthPacketEntity = withContext(Dispatchers.IO) {
        val cleanIdentifier = identifier.trim().lowercase()
        val packetId = "PKT-TL-" + UUID.randomUUID().toString().take(6).uppercase()
        val passwordHash = hashString(rawPassword)
        val maskedPass = "•••••••• (${passwordHash.take(6)}...)"
        val sessionToken = "TL-TOK-" + UUID.randomUUID().toString().replace("-", "").take(24)

        val jsonPacket = customJsonPacket ?: """
        {
          "packet_header": {
            "packet_id": "$packetId",
            "protocol": "TL-COMM-v2.4",
            "provider": "$provider",
            "timestamp": ${System.currentTimeMillis()},
            "checksum": "0x${packetId.hashCode().toUInt().toString(16)}"
          },
          "identity_payload": {
            "identifier": "$cleanIdentifier",
            "display_name": "$displayName",
            "phone_number": "${phoneNumber ?: "N/A"}",
            "avatar_accent": "$avatarColor"
          },
          "security_metadata": {
            "session_token": "$sessionToken",
            "hash_algorithm": "SHA-256",
            "password_hash": "$passwordHash",
            "encryption_cipher": "AES-256-GCM"
          },
          "network_state": {
            "status": "AUTHENTICATED",
            "channel": "talkloop_secure_stream",
            "latency_target": "<15ms"
          }
        }
        """.trimIndent()

        val existing = authPacketDao.getPacketByIdentifier(cleanIdentifier)
        val entity = if (existing != null) {
            existing.copy(
                displayName = displayName,
                passwordMasked = maskedPass,
                phoneNumber = phoneNumber ?: existing.phoneNumber,
                authProvider = provider,
                avatarColorHex = avatarColor,
                sessionStatus = "ACTIVE",
                packetJson = jsonPacket,
                timestamp = System.currentTimeMillis()
            ).also { authPacketDao.updatePacket(it) }
        } else {
            val newEntity = AuthPacketEntity(
                packetId = packetId,
                authProvider = provider,
                identifier = cleanIdentifier,
                displayName = displayName,
                passwordMasked = maskedPass,
                phoneNumber = phoneNumber,
                avatarColorHex = avatarColor,
                sessionStatus = "ACTIVE",
                packetJson = jsonPacket,
                timestamp = System.currentTimeMillis()
            )
            val generatedId = authPacketDao.insertPacket(newEntity)
            newEntity.copy(id = generatedId.toInt())
        }

        entity
    }

    suspend fun getPacketByIdentifier(identifier: String): AuthPacketEntity? = withContext(Dispatchers.IO) {
        authPacketDao.getPacketByIdentifier(identifier.trim().lowercase())
    }

    suspend fun deletePacket(packetId: Int) = withContext(Dispatchers.IO) {
        authPacketDao.deletePacketById(packetId)
    }

    suspend fun clearDatabase() = withContext(Dispatchers.IO) {
        authPacketDao.clearAllPackets()
    }

    private fun hashString(input: String): String {
        return try {
            val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
            bytes.joinToString("") { "%02x".format(it) }
        } catch (e: Exception) {
            "hash_${input.hashCode()}"
        }
    }
}
