// File: backend-timebox/src/main/kotlin/com/timebox/tag/dto/TagDto.kt
package com.timebox.tag.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class CreateTagRequest(
    @field:NotBlank(message = "태그 이름은 필수입니다")
    @field:Size(min = 1, max = 30, message = "태그 이름은 1~30자여야 합니다")
    val name: String,

    @field:Pattern(
        regexp = "^#[0-9A-Fa-f]{6}$",
        message = "색상은 HEX 형식(#RRGGBB)이어야 합니다"
    )
    val color: String? = null
)

data class TagResponse(
    val tagId: Long,
    val name: String,
    val color: String?
)
