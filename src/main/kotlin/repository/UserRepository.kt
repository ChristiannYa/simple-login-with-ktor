package com.example.repository

import com.example.db.UserDao
import com.example.db.UserTable
import com.example.db.suspendTransaction
import com.example.db.toDomain
import com.example.domain.User
import com.example.domain.UserCreate
import com.example.domain.UserType
import java.time.Instant

interface IUserRepository {
    suspend fun findByEmail(email: String): User?
    suspend fun createUser(user: UserCreate): User
}

class UserRepository : IUserRepository {
    override suspend fun findByEmail(email: String): User? = suspendTransaction {
        UserDao.Companion
            .find { (UserTable.email eq email) }
            .singleOrNull() // Email is UNIQUE so `singleOrNull` can be used
            ?.toDomain()
    }

    override suspend fun createUser(user: UserCreate): User = suspendTransaction {
        // The default values are still provided because batch inserting, Exposed can't
        // rely on Database default because it does the following:
        // `Batch INSERT: INSERT INTO users (name, email, password_hash) VALUES (...), (...), (...)`
        // but it has missing columns with defaults: `user_type`, `is_premium`, `created_at`
        UserDao.Companion
            .new {
                name = user.name
                email = user.email
                passwordHash = user.passwordHash
                userType = UserType.USER
                isPremium = false
                createdAt = Instant.now()
            }
            .toDomain()
    }
}