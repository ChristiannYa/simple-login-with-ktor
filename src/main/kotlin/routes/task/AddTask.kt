package com.example.routes.task

import com.example.config.taskRepository
import com.example.domain.TaskAdd
import com.example.domain.TaskPriority
import com.example.dto.DtoRes
import com.example.dto.TaskAddRequestDto
import com.example.dto.toDto
import com.example.utils.enumContainsIgnoreCase
import io.ktor.http.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import java.util.*

fun Route.addTask() {
    post {
        val req = call.receive<TaskAddRequestDto>()

        val taskToAdd = TaskAdd(
            userId = UUID.fromString("some-user-id"), // obtained from context request
            name = req.name.lowercase(),
            description = req.description,
            priority = enumValueOf<TaskPriority>(req.priority.uppercase())
        )

        val taskCreated = call.taskRepository.addTask(taskToAdd)

        call.respond(
            HttpStatusCode.Created,
            DtoRes.success(
                "task added successfully",
                mapOf("task_added" to taskCreated.toDto())
            ),
        )
    }
}

private fun validateRequest(req: TaskAddRequestDto): ValidationResult = when {
    req.name.isEmpty() ->
        ValidationResult.Invalid("name cannot be empty")

    req.name.length < 3 ->
        ValidationResult.Invalid("name must be at least 3 characters")

    req.name.length > 16 ->
        ValidationResult.Invalid("name must be at most 16 characters")

    req.description.isEmpty() ->
        ValidationResult.Invalid("description cannot be empty")

    !enumContainsIgnoreCase<TaskPriority>(req.priority) ->
        ValidationResult.Invalid("'${req.priority}' is an invalid priority")

    else -> ValidationResult.Valid
}