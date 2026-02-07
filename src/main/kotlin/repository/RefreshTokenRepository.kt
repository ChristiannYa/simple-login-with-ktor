package com.example.repository

import com.example.db.RefreshTokenDao
import com.example.db.RefreshTokenTable
import com.example.db.suspendTransaction
import com.example.db.toDomain
import com.example.domain.RefreshToken
import com.example.domain.RefreshTokenCreate
import com.example.domain.TokenValidationResult
import org.jetbrains.exposed.sql.SqlExpressionBuilder.less
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere
import java.time.Instant
import java.util.*

interface IRefreshTokenRepository {
    /**
     * Save a new refresh token
     *
     * @return `RefreshToken`
     */
    suspend fun save(refreshTokenCreate: RefreshTokenCreate): RefreshToken

    /**
     * Checks token validity (exists, not revoked, not expired, belongs to user)
     *
     * @return `TokenValidationResult` resulting in one of the following:
     * - `Valid`
     * - `NotFound`
     * - `Revoked`
     * - `Expired`
     */
    suspend fun validate(tokenHash: String, userId: UUID): TokenValidationResult

    /**
     * Find a specific token
     *
     * @return `RefreshToken` **if** found
     */
    suspend fun findByHash(tokenHash: String): RefreshToken?

    /**
     * Revoke a specific token
     *
     * @return `true` if revoked, `false` if it didn't exist
     */
    suspend fun revokeByHash(tokenHash: String): Boolean

    /**
     * Revoke **all** tokens by user Id
     *
     * @return tokens revoked count
     */
    suspend fun revokeByUserId(userId: UUID): Int

    /**
     * Update last used timestamp
     *
     * *Not critical if it fails*
     */
    suspend fun updateLastUsedTime(tokenHash: String)

    /**
     * Delete expired tokens (cleanup job)
     *
     * @return tokens deleted count
     */
    suspend fun deleteExpired(): Int
}

class RefreshTokenRepository : IRefreshTokenRepository {
    override suspend fun save(refreshTokenCreate: RefreshTokenCreate): RefreshToken = suspendTransaction {
        RefreshTokenDao.Companion
            .new {
                userId = refreshTokenCreate.userId
                hash = refreshTokenCreate.hash
                expiresAt = refreshTokenCreate.expiresAt
                createdAt = Instant.now()
                lastUsedAt = null
                revokedAt = null
            }
            .toDomain()
    }

    override suspend fun validate(tokenHash: String, userId: UUID): TokenValidationResult = suspendTransaction {
        val token = RefreshTokenDao.Companion
            .find {
                (RefreshTokenTable.hash eq tokenHash) and
                        (RefreshTokenTable.userId eq userId)
            }
            .firstOrNull()

        when {
            token == null -> TokenValidationResult.NotFound
            token.revokedAt != null -> TokenValidationResult.Revoked
            token.expiresAt.isBefore(Instant.now()) -> TokenValidationResult.Expired
            else -> TokenValidationResult.Valid(token.toDomain())
        }
    }

    override suspend fun findByHash(tokenHash: String): RefreshToken? = suspendTransaction {
        RefreshTokenDao.Companion
            .find { RefreshTokenTable.hash eq tokenHash }
            .firstOrNull()
            ?.toDomain()
    }

    override suspend fun revokeByHash(tokenHash: String): Boolean = suspendTransaction {
        RefreshTokenDao.Companion
            .find { RefreshTokenTable.hash eq tokenHash }
            .firstOrNull()?.let { token ->
                token.revokedAt = Instant.now()
                true
            } ?: false
    }

    override suspend fun revokeByUserId(userId: UUID): Int = suspendTransaction {
        val tokens = RefreshTokenDao.Companion
            .find {
                (RefreshTokenTable.userId eq userId) and
                        (RefreshTokenTable.revokedAt.isNull())
            }

        val now = Instant.now()
        tokens.forEach { it.revokedAt = now }

        tokens.count().toInt()
    }

    override suspend fun updateLastUsedTime(tokenHash: String): Unit = suspendTransaction {
        RefreshTokenDao.Companion
            .find { RefreshTokenTable.hash eq tokenHash }
            .firstOrNull()?.let { it.lastUsedAt = Instant.now() }
    }

    override suspend fun deleteExpired(): Int = suspendTransaction {
        RefreshTokenTable.deleteWhere {
            expiresAt less Instant.now()
        }
    }
}