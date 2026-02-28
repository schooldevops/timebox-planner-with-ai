// File: backend-timebox/src/main/kotlin/com/timebox/auth/service/AuthService.kt
package com.timebox.auth.service

import com.timebox.auth.dto.*
import com.timebox.auth.repository.UserRepository
import com.timebox.auth.repository.RefreshTokenRepository
import com.timebox.common.exception.ConflictException
import com.timebox.common.exception.UnauthorizedException
import com.timebox.common.util.JwtTokenProvider
import org.slf4j.LoggerFactory
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val refreshTokenRepository: RefreshTokenRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Transactional
    fun register(request: RegisterRequest): UserResponse {
        val normalizedEmail = request.email.lowercase()

        // EC-001: 이메일 중복 검사
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw ConflictException("E001", "이미 사용 중인 이메일입니다")
        }

        val encryptedPassword = passwordEncoder.encode(request.password)
        val user = userRepository.save(
            email = normalizedEmail,
            password = encryptedPassword,
            name = request.name
        )

        logger.info("회원가입 완료: userId={}", user.userId)
        return user
    }

    @Transactional
    fun login(request: LoginRequest): TokenResponse {
        val normalizedEmail = request.email.lowercase()
        val user = userRepository.findByEmail(normalizedEmail)
            ?: throw UnauthorizedException("E002", "이메일 또는 비밀번호가 올바르지 않습니다")

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password, user.password)) {
            throw UnauthorizedException("E002", "이메일 또는 비밀번호가 올바르지 않습니다")
        }

        val accessToken = jwtTokenProvider.generateAccessToken(user.userId)
        val refreshToken = jwtTokenProvider.generateRefreshToken(user.userId)

        // Refresh Token 저장
        refreshTokenRepository.save(
            userId = user.userId,
            token = refreshToken,
            expiresAt = Instant.now().plusMillis(604800000) // 7일
        )

        return TokenResponse(accessToken = accessToken, refreshToken = refreshToken)
    }

    @Transactional
    fun logout(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
        logger.info("로그아웃: userId={}", userId)
    }

    @Transactional
    fun refreshToken(request: RefreshTokenRequest): AccessTokenResponse {
        if (!jwtTokenProvider.validateToken(request.refreshToken)) {
            throw UnauthorizedException("E002", "인증 토큰이 만료되었습니다. 다시 로그인해주세요.")
        }

        val userId = jwtTokenProvider.getUserIdFromToken(request.refreshToken)
        val storedToken = refreshTokenRepository.findByToken(request.refreshToken)
            ?: throw UnauthorizedException("E002", "유효하지 않은 Refresh Token입니다")

        if (storedToken.expiresAt.isBefore(Instant.now())) {
            throw UnauthorizedException("E002", "인증 토큰이 만료되었습니다. 다시 로그인해주세요.")
        }

        val newAccessToken = jwtTokenProvider.generateAccessToken(userId)
        return AccessTokenResponse(accessToken = newAccessToken)
    }
}
