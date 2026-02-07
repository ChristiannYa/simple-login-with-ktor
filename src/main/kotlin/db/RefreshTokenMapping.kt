package com.example.db

import com.example.domain.RefreshToken
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.*

object RefreshTokenTable : UUIDTable("refresh_tokens") {
    val userId = uuid("user_id")
    val hash = varchar("hash", 64)
    val expiresAt = timestamp("expires_at")
    val createdAt = timestamp("created_at")
    val lastUsedAt = timestamp("last_used_at").nullable()
    val revokedAt = timestamp("revoked_at").nullable()
}

class RefreshTokenDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<RefreshTokenDao>(RefreshTokenTable)

    var userId by RefreshTokenTable.userId
    var hash by RefreshTokenTable.hash
    var expiresAt by RefreshTokenTable.expiresAt
    var createdAt by RefreshTokenTable.createdAt
    var lastUsedAt by RefreshTokenTable.lastUsedAt
    var revokedAt by RefreshTokenTable.revokedAt
}

fun RefreshTokenDao.toDomain() = RefreshToken(
    this.id.value,
    this.userId,
    this.hash,
    this.expiresAt,
    this.createdAt,
    this.lastUsedAt,
    this.revokedAt
)