// File: backend-timebox/src/main/kotlin/com/timebox/task/dto/TaskDto.kt
package com.timebox.task.dto

import com.timebox.tag.dto.TagResponse
import jakarta.validation.constraints.*
import java.time.Instant

enum class Priority { HIGH, MEDIUM, LOW }
enum class TaskStatus { TODO, IN_PROGRESS, DONE, CANCELLED }

data class CreateTaskRequest(
    @field:NotBlank(message = "제목은 필수입니다")
    @field:Size(min = 1, max = 200, message = "제목은 1~200자여야 합니다")
    val title: String,

    @field:Size(max = 2000, message = "설명은 2000자 이하여야 합니다")
    val description: String? = null,

    val priority: Priority = Priority.MEDIUM,

    @field:Min(value = 5, message = "예상 시간은 최소 5분입니다")
    @field:Max(value = 480, message = "예상 시간은 최대 480분입니다")
    val estimatedMinutes: Int? = null,

    @field:Size(max = 5, message = "태그는 최대 5개까지 선택 가능합니다")
    val tagIds: List<Long> = emptyList()
)

data class TaskResponse(
    val taskId: Long,
    val title: String,
    val description: String?,
    val priority: String,
    val estimatedMinutes: Int?,
    val status: String,
    val tags: List<TagResponse>,
    val createdAt: Instant,
    val updatedAt: Instant
)

data class TaskPageResponse(
    val content: List<TaskResponse>,
    val totalElements: Long,
    val totalPages: Int,
    val size: Int,
    val number: Int
)
