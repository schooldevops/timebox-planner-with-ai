// File: backend-timebox/src/main/kotlin/com/timebox/auth/controller/AuthController.kt
package com.timebox.auth.controller

import com.timebox.auth.dto.*
import com.timebox.auth.service.AuthService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(private val authService: AuthService) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<UserResponse> {
        val response = authService.register(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<TokenResponse> {
        val response = authService.login(request)
        return ResponseEntity.ok(response)
    }

    @PostMapping("/logout")
    fun logout(): ResponseEntity<Map<String, String>> {
        val userId = SecurityContextHolder.getContext().authentication.principal as Long
        authService.logout(userId)
        return ResponseEntity.ok(mapOf("message" to "로그아웃 완료"))
    }

    @PostMapping("/refresh")
    fun refresh(@Valid @RequestBody request: RefreshTokenRequest): ResponseEntity<AccessTokenResponse> {
        val response = authService.refreshToken(request)
        return ResponseEntity.ok(response)
    }
}
