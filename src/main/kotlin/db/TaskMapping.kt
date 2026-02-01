package com.example.db

import com.example.domain.Task
import com.example.domain.TaskPriority
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.timestamp

// Persistence details - how data is stored

// `TaskTable` defines the basic mapping
object TaskTable : IntIdTable("tasks") { // Db schema
    val userId = uuid("user_id")
    val name = varchar("name", 50)
    val description = varchar("description", 50)
    val priority = pgEnum<TaskPriority>("task_priority", "priority")
    val createdAt = timestamp("created_at")
}

// `TaskDao` adds the helper methods to create, find, update, and delete tasks.
class TaskDao(id: EntityID<Int>) : IntEntity(id) { // ORM entity
    // The companion object provides helper methods such as `.find()`, `.all()`, and
    // `.new()` to query/create tasks
    companion object : IntEntityClass<TaskDao>(TaskTable)

    // The by keyword delegates properties to the table columns
    var userId by TaskTable.userId
    var name by TaskTable.name
    var description by TaskTable.description
    var priority by TaskTable.priority
    var createdAt by TaskTable.createdAt
}

// `TaskDao.toDomain()` transforms an instance of the `TaskDao` type to the `Task` domain:
// TaskDao (database representation) → Task domain (the API model)
fun TaskDao.toDomain() = Task(
    this.id.value,
    this.userId,
    this.name,
    this.description,
    this.priority,
    this.createdAt
)