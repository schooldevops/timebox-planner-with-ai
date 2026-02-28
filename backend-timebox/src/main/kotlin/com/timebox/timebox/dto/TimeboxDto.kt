// File: backend-timebox/src/main/kotlin/com/timebox/timebox/dto/TimeboxDto.kt
package com.timebox.timebox.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import java.time.Instant
import java.time.LocalDate

enum class TimeboxStatus { PLANNED, RUNNING, PAUSED, COMPLETED, CANCELLED }

data class CreateTimeboxRequest(
    val taskId: Long? = null,

    @field:NotNull(message = "날짜는 필수입니다")
    val date: LocalDate,

    @field:NotBlank(message = "시작 시간은 필수입니다")
    @field:Pattern(regexp = "^([01]\\d|2[0-3]):(00|30)$", message = "시작 시간은 HH:mm 형식(30분 단위)이어야 합니다")
    val startTime: String,

    @field:NotBlank(message = "종료 시간은 필수입니다")
    @field:Pattern(regexp = "^([01]\\d|2[0-3]):(00|30)$", message = "종료 시간은 HH:mm 형식(30분 단위)이어야 합니다")
    val endTime: String
)

data class UpdateTimeboxRequest(
    @field:Pattern(regexp = "^([01]\\d|2[0-3]):(00|30)$")
    val startTime: String? = null,
    @field:Pattern(regexp = "^([01]\\d|2[0-3]):(00|30)$")
    val endTime: String? = null,
    val taskId: Long? = null
)

data class TimeboxResponse(
    val timeboxId: Long,
    val taskId: Long?,
    val taskTitle: String?,
    val date: LocalDate,
    val startTime: String,
    val endTime: String,
    val status: String,
    val createdAt: Instant
)
