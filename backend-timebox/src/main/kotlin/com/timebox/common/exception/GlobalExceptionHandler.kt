// File: backend-timebox/src/main/kotlin/com/timebox/common/exception/GlobalExceptionHandler.kt
package com.timebox.common.exception

import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.Instant

// 공통 에러 응답 형식
data class ErrorResponse(
    val error: String,
    val message: String,
    val timestamp: String = Instant.now().toString(),
    val path: String = ""
)

// 비즈니스 예외 클래스 정의
class ConflictException(val errorCode: String, message: String) : RuntimeException(message)
class NotFoundException(val errorCode: String, message: String) : RuntimeException(message)
class ForbiddenException(val errorCode: String, message: String) : RuntimeException(message)
class UnauthorizedException(val errorCode: String, message: String) : RuntimeException(message)
class UnprocessableEntityException(val errorCode: String, message: String) : RuntimeException(message)

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(javaClass)

    @ExceptionHandler(ConflictException::class)
    fun handleConflict(ex: ConflictException): ResponseEntity<ErrorResponse> {
        logger.warn("충돌 예외: {}", ex.message)
        return ResponseEntity.status(HttpStatus.CONFLICT)
            .body(ErrorResponse(error = ex.errorCode, message = ex.message ?: "충돌이 발생했습니다"))
    }

    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException): ResponseEntity<ErrorResponse> {
        logger.warn("리소스 없음: {}", ex.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ErrorResponse(error = ex.errorCode, message = ex.message ?: "리소스를 찾을 수 없습니다"))
    }

    @ExceptionHandler(ForbiddenException::class)
    fun handleForbidden(ex: ForbiddenException): ResponseEntity<ErrorResponse> {
        logger.warn("권한 없음: {}", ex.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(ErrorResponse(error = ex.errorCode, message = ex.message ?: "권한이 없습니다"))
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorized(ex: UnauthorizedException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(ErrorResponse(error = ex.errorCode, message = ex.message ?: "인증이 필요합니다"))
    }

    @ExceptionHandler(UnprocessableEntityException::class)
    fun handleUnprocessable(ex: UnprocessableEntityException): ResponseEntity<ErrorResponse> {
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body(ErrorResponse(error = ex.errorCode, message = ex.message ?: "처리할 수 없는 요청입니다"))
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidation(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.fieldErrors
            .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(ErrorResponse(error = "VALIDATION_ERROR", message = message))
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneric(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("예상치 못한 에러 발생", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(ErrorResponse(error = "INTERNAL_ERROR", message = "일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."))
    }
}
