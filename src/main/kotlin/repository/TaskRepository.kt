package com.example.repository

import com.example.db.TaskDao
import com.example.db.TaskTable
import com.example.db.suspendTransaction
import com.example.db.toDomain
import com.example.domain.Task
import com.example.domain.TaskAdd
import com.example.domain.TaskPriority
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import java.util.*

interface ITaskRepository {
    suspend fun allTasks(userId: UUID): List<Task>
    suspend fun tasksByPriority(taskPriority: TaskPriority): List<Task>
    suspend fun taskByName(name: String): Task?
    suspend fun addTask(task: TaskAdd): Task
    suspend fun removeTask(name: String): Boolean
}

class TaskRepository : ITaskRepository {
    override suspend fun allTasks(userId: UUID): List<Task> = suspendTransaction {
        TaskDao.Companion
            .find { TaskTable.userId eq userId }
            .map { it.toDomain() }
    }

    override suspend fun tasksByPriority(
        taskPriority: TaskPriority
    ): List<Task> = suspendTransaction {
        TaskDao.Companion
            .find { (TaskTable.priority eq taskPriority) }
            .map { it.toDomain() }
    }

    override suspend fun taskByName(name: String): Task? = suspendTransaction {
        TaskDao.Companion
            .find { (TaskTable.name eq name) }
            .limit(1)
            .map { it.toDomain() }
            .firstOrNull() // Name is not UNIQUE -> `limit(1) + `firstOrNull` combination
    }

    override suspend fun addTask(task: TaskAdd): Task = suspendTransaction {
        TaskDao.Companion
            .new {
                userId = task.userId
                name = task.name
                description = task.description
                priority = task.priority
                // id and createdAt automatically set
            }
            .toDomain() // Return the created task with all fields
    }

    override suspend fun removeTask(name: String): Boolean = suspendTransaction {
        val rowsDeleted = TaskTable.deleteWhere { TaskTable.name eq name }
        rowsDeleted == 1
    }
}