// File: backend-timebox/src/main/kotlin/com/timebox/common/util/JwtTokenProvider.kt
package com.timebox.common.util

import com.timebox.common.config.JwtProperties
import io.jsonwebtoken.*
import io.jsonwebtoken.security.Keys
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.stereotype.Component
import java.util.*
import javax.crypto.SecretKey

@Component
@EnableConfigurationProperties(JwtProperties::class)
class JwtTokenProvider(private val jwtProperties: JwtProperties) {

    private val logger = LoggerFactory.getLogger(javaClass)

    private val signingKey: SecretKey by lazy {
        Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())
    }

    fun generateAccessToken(userId: Long): String = generateToken(userId, jwtProperties.expiration)

    fun generateRefreshToken(userId: Long): String = generateToken(userId, jwtProperties.refreshExpiration)

    private fun generateToken(userId: Long, expiration: Long): String {
        val now = Date()
        return Jwts.builder()
            .subject(userId.toString())
            .issuedAt(now)
            .expiration(Date(now.time + expiration))
            .signWith(signingKey)
            .compact()
    }

    fun getUserIdFromToken(token: String): Long =
        parseClaims(token).subject.toLong()

    fun validateToken(token: String): Boolean = try {
        parseClaims(token)
        true
    } catch (ex: ExpiredJwtException) {
        logger.warn("만료된 JWT 토큰: {}", ex.message)
        false
    } catch (ex: Exception) {
        logger.error("유효하지 않은 JWT 토큰: {}", ex.message)
        false
    }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(signingKey)
            .build()
            .parseSignedClaims(token)
            .payload
}
