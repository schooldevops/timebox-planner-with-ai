// File: backend-timebox/src/main/kotlin/com/timebox/dashboard/dto/DashboardDto.kt
package com.timebox.dashboard.dto

import com.timebox.timebox.dto.TimeboxResponse
import java.time.LocalDate

data class DailyDashboardResponse(
    val date: LocalDate,
    val totalBlocks: Int,
    val completedBlocks: Int,
    val completionRate: Double,
    val plannedMinutes: Int,
    val focusedMinutes: Int,
    val timeboxes: List<TimeboxResponse>
)

data class WeeklyDashboardResponse(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val totalFocusedMinutes: Int,
    val dailyStats: List<DailyStat>,
    val topTags: List<TagStat>
)

data class DailyStat(
    val date: LocalDate,
    val focusedMinutes: Int,
    val completedBlocks: Int
)

data class TagStat(
    val tagId: Long,
    val name: String,
    val color: String?,
    val count: Int
)
