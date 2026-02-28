// File: backend-timebox/src/test/kotlin/com/timebox/auth/AuthServiceTest.kt
package com.timebox.auth

import com.timebox.auth.dto.LoginRequest
import com.timebox.auth.dto.RegisterRequest
import com.timebox.auth.repository.UserRepository
import com.timebox.auth.repository.RefreshTokenRepository
import com.timebox.auth.service.AuthService
import com.timebox.common.exception.ConflictException
import com.timebox.common.exception.UnauthorizedException
import com.timebox.common.util.JwtTokenProvider
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldNotBeEmpty
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

/**
 * TC-001: 정상 회원가입
 * TC-002: 중복 이메일 회원가입
 * TC-003: 정상 로그인
 * TC-004: 잘못된 비밀번호 로그인
 */
class AuthServiceTest : BehaviorSpec({

    val userRepository = mockk<UserRepository>()
    val refreshTokenRepository = mockk<RefreshTokenRepository>(relaxed = true)
    val passwordEncoder = BCryptPasswordEncoder()
    val jwtTokenProvider = mockk<JwtTokenProvider>()

    val authService = AuthService(userRepository, refreshTokenRepository, passwordEncoder, jwtTokenProvider)

    // ── TC-001: 정상 회원가입 ─────────────────────────────
    given("TC-001: 유효한 이메일/비밀번호/이름으로 회원가입 요청 시") {
        val request = RegisterRequest(
            email = "test@example.com",
            password = "Test1234!",
            name = "홍길동"
        )

        `when`("이메일이 중복되지 않은 경우") {
            every { userRepository.existsByEmail("test@example.com") } returns false
            every { userRepository.save(any(), any(), "홍길동") } returns mockUserResponse()

            val result = authService.register(request)

            then("201 응답과 UserResponse 반환") {
                result.email shouldBe "test@example.com"
                result.name shouldBe "홍길동"
                verify { userRepository.save(any(), any(), "홍길동") }
            }
        }
    }

    // ── TC-002: 이메일 중복 ───────────────────────────────
    given("TC-002: 이미 가입된 이메일로 회원가입 요청 시") {
        val request = RegisterRequest(
            email = "duplicate@example.com",
            password = "Test1234!",
            name = "중복사용자"
        )

        `when`("이메일이 이미 존재하는 경우") {
            every { userRepository.existsByEmail("duplicate@example.com") } returns true

            then("ConflictException(E001) 발생") {
                val ex = shouldThrow<ConflictException> {
                    authService.register(request)
                }
                ex.errorCode shouldBe "E001"
            }
        }
    }

    // ── TC-003: 정상 로그인 ───────────────────────────────
    given("TC-003: 올바른 이메일/비밀번호로 로그인 요청 시") {
        val storedPassword = passwordEncoder.encode("Test1234!")
        val loginRequest = LoginRequest(email = "test@example.com", password = "Test1234!")

        `when`("사용자가 존재하고 비밀번호가 일치하는 경우") {
            every { userRepository.findByEmail("test@example.com") } returns mockUserRecord(storedPassword)
            every { jwtTokenProvider.generateAccessToken(1L) } returns "access_token"
            every { jwtTokenProvider.generateRefreshToken(1L) } returns "refresh_token"

            val result = authService.login(loginRequest)

            then("AccessToken, RefreshToken 포함된 TokenResponse 반환") {
                result.accessToken.shouldNotBeEmpty()
                result.refreshToken.shouldNotBeEmpty()
                result.tokenType shouldBe "Bearer"
            }
        }
    }

    // ── TC-004: 잘못된 비밀번호 ───────────────────────────
    given("TC-004: 올바르지 않은 비밀번호로 로그인 요청 시") {
        val storedPassword = passwordEncoder.encode("Test1234!")
        val loginRequest = LoginRequest(email = "test@example.com", password = "WrongPassword!")

        `when`("비밀번호가 불일치하는 경우") {
            every { userRepository.findByEmail("test@example.com") } returns mockUserRecord(storedPassword)

            then("UnauthorizedException(E002) 발생 - 어느 쪽이 틀렸는지 미공개") {
                val ex = shouldThrow<UnauthorizedException> {
                    authService.login(loginRequest)
                }
                ex.errorCode shouldBe "E002"
                ex.message shouldNotBe null
            }
        }
    }
})

private data class UserRecord(val userId: Long, val email: String, val password: String, val name: String)

private fun mockUserResponse() = com.timebox.auth.dto.UserResponse(
    userId = 1L,
    email = "test@example.com",
    name = "홍길동",
    createdAt = java.time.Instant.now()
)

private fun mockUserRecord(password: String) = UserRecord(
    userId = 1L,
    email = "test@example.com",
    password = password,
    name = "홍길동"
)
