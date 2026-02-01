package com.example.db

import com.example.domain.User
import com.example.domain.UserType
import org.jetbrains.exposed.dao.UUIDEntity
import org.jetbrains.exposed.dao.UUIDEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.javatime.timestamp
import java.util.*

object UserTable : UUIDTable("users") {
    val name = varchar("name", 50)
    val email = varchar("email", 120)
    val passwordHash = varchar("password_hash", 64)
    val userType = pgEnum<UserType>("user_type", "user_type")
    val isPremium = bool("is_premium")
    val createdAt = timestamp("created_at")
}

class UserDao(id: EntityID<UUID>) : UUIDEntity(id) {
    companion object : UUIDEntityClass<UserDao>(UserTable)

    var name by UserTable.name
    var email by UserTable.email
    var passwordHash by UserTable.passwordHash
    var userType by UserTable.userType
    val isPremium by UserTable.isPremium
    var createdAt by UserTable.createdAt
}

 fun UserDao.toDomain() = User(
     this.id.value,
     this.name,
     this.email,
     this.passwordHash,
     this.userType,
     this.isPremium,
     this.createdAt
 )