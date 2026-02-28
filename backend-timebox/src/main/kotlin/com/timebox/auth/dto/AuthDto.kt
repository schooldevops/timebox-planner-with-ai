// File: backend-timebox/src/main/kotlin/com/timebox/auth/dto/AuthDto.kt
package com.timebox.auth.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import java.time.Instant

// ── 요청 DTO ─────────────────────────────────────────────
data class RegisterRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:NotBlank(message = "이메일은 필수입니다")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    @field:Pattern(
        regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}\$",
        message = "비밀번호는 8자 이상, 영문+숫자+특수문자 조합이어야 합니다"
    )
    val password: String,

    @field:NotBlank(message = "이름은 필수입니다")
    @field:Size(min = 1, max = 50, message = "이름은 1~50자여야 합니다")
    val name: String
)

data class LoginRequest(
    @field:Email(message = "올바른 이메일 형식이 아닙니다")
    @field:NotBlank(message = "이메일은 필수입니다")
    val email: String,

    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String
)

data class RefreshTokenRequest(
    @field:NotBlank(message = "Refresh Token은 필수입니다")
    val refreshToken: String
)

// ── 응답 DTO ─────────────────────────────────────────────
data class UserResponse(
    val userId: Long,
    val email: String,
    val name: String,
    val createdAt: Instant
)

data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresIn: Long = 3600
)

data class AccessTokenResponse(
    val accessToken: String,
    val expiresIn: Long = 3600
)
