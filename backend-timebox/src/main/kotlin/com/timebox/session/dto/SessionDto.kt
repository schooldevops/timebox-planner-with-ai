// File: backend-timebox/src/main/kotlin/com/timebox/session/dto/SessionDto.kt
package com.timebox.session.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.Instant

enum class SessionStatus { RUNNING, PAUSED, COMPLETED, CANCELLED }

data class SessionResponse(
    val sessionId: Long,
    val timeboxId: Long,
    val status: String,
    val plannedMinutes: Int,
    val focusedMinutes: Int?,
    val startedAt: Instant,
    val pausedAt: Instant?,
    val completedAt: Instant?
)

data class CompleteSessionRequest(
    @field:NotNull(message = "confirmDone은 필수입니다")
    val confirmDone: Boolean
)

data class CreateRetrospectiveRequest(
    @field:NotNull(message = "평점은 필수입니다")
    @field:Min(1) @field:Max(5)
    val rating: Int,

    @field:Size(max = 500, message = "메모는 500자 이하여야 합니다")
    val memo: String? = null
)

data class RetrospectiveResponse(
    val retrospectiveId: Long,
    val rating: Int,
    val memo: String?,
    val createdAt: Instant
)
