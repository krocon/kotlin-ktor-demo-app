package com.example.plugins

import com.example.tasks.CreateTaskRequest
import com.example.tasks.UpdateTaskRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

private const val MAX_TITLE_LENGTH = 100

/** Validates request bodies right after they are received. */
fun Application.configureValidation() {
    install(RequestValidation) {
        validate<CreateTaskRequest> { request ->
            validateTitle(request.title)
        }
        validate<UpdateTaskRequest> { request ->
            request.title?.let(::validateTitle) ?: ValidationResult.Valid
        }
    }
}

private fun validateTitle(title: String): ValidationResult = when {
    title.isBlank() -> ValidationResult.Invalid("title must not be blank")
    title.length > MAX_TITLE_LENGTH -> ValidationResult.Invalid("title must be at most $MAX_TITLE_LENGTH characters")
    else -> ValidationResult.Valid
}
