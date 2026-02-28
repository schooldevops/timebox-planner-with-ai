// File: backend-timebox/src/main/kotlin/com/timebox/TimeboxApplication.kt
package com.timebox

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class TimeboxApplication

fun main(args: Array<String>) {
    runApplication<TimeboxApplication>(*args)
}
