package com.example.repository

import com.example.db.UserDao
import com.example.db.UserTable
import com.example.db.suspendTransaction
import com.example.db.toDomain
import com.example.domain.User

interface IUserRepository {
    suspend fun findByEmail(email: String): User?
}

class UserRepository : IUserRepository {
    override suspend fun findByEmail(email: String): User? = suspendTransaction {
        UserDao.Companion
            .find { (UserTable.email eq email) }
            .singleOrNull() // Email is UNIQUE so `singleOrNull` can be used
            ?.toDomain()
    }
}