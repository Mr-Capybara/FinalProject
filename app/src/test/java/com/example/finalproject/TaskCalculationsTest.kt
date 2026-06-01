package com.example.finalproject

import com.example.finalproject.data.CompletionFilter
import com.example.finalproject.data.FocusSessionEntity
import com.example.finalproject.data.Priority
import com.example.finalproject.data.RepeatRule
import com.example.finalproject.data.TaskCompletionEntity
import com.example.finalproject.data.TaskEntity
import com.example.finalproject.data.calculateDashboardStats
import com.example.finalproject.data.filterTaskInstances
import com.example.finalproject.data.habitProgressForTask
import com.example.finalproject.data.taskInstancesForDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class TaskCalculationsTest {
    @Test
    fun filtersByQueryPriorityAndCompletion() {
        val date = LocalDate.parse("2026-05-11")
        val tasks = listOf(
            task(id = "1", title = "写项目报告", category = "学习", priority = Priority.HIGH, completed = false),
            task(id = "2", title = "整理房间", category = "生活", priority = Priority.LOW, completed = true),
        )
        val instances = taskInstancesForDate(tasks, emptyList(), date)

        val result = filterTaskInstances(
            instances = instances,
            query = "报告",
            category = "全部任务",
            priority = Priority.HIGH,
            completionFilter = CompletionFilter.ACTIVE,
        )

        assertEquals(1, result.size)
        assertEquals("写项目报告", result.single().task.title)
    }

    @Test
    fun expandsDailyWeeklyAndMonthlyTasksForCalendarDates() {
        val tasks = listOf(
            task(id = "daily", title = "背单词", repeat = RepeatRule.DAILY, isHabit = true),
            task(id = "weekly", title = "周复盘", repeat = RepeatRule.WEEKLY, date = "2026-05-11"),
            task(id = "monthly", title = "月度整理", repeat = RepeatRule.MONTHLY, date = "2026-05-11"),
        )

        val tuesday = taskInstancesForDate(tasks, emptyList(), LocalDate.parse("2026-05-12")).map { it.task.id }
        val nextMonday = taskInstancesForDate(tasks, emptyList(), LocalDate.parse("2026-05-18")).map { it.task.id }
        val nextMonthSameDay = taskInstancesForDate(tasks, emptyList(), LocalDate.parse("2026-06-11")).map { it.task.id }

        assertEquals(listOf("daily"), tuesday)
        assertTrue(nextMonday.contains("daily"))
        assertTrue(nextMonday.contains("weekly"))
        assertTrue(nextMonthSameDay.contains("daily"))
        assertTrue(nextMonthSameDay.contains("monthly"))
    }

    @Test
    fun calculatesDashboardStatsFromRealData() {
        val today = LocalDate.parse("2026-05-11")
        val zone = ZoneId.of("Asia/Shanghai")
        val tasks = listOf(
            task(id = "1", title = "完成论文", category = "学习", priority = Priority.HIGH, completed = true),
            task(id = "2", title = "跑步", category = "健康", priority = Priority.MEDIUM, repeat = RepeatRule.DAILY, isHabit = true, date = "2026-05-10"),
        )
        val completions = listOf(
            TaskCompletionEntity("2", "2026-05-11", 1L),
            TaskCompletionEntity("2", "2026-05-10", 1L),
        )
        val sessions = listOf(
            FocusSessionEntity("s1", "1", "完成论文", 1_800, millis("2026-05-11T10:00:00", zone)),
            FocusSessionEntity("s2", "1", "完成论文", 900, millis("2026-05-04T10:00:00", zone)),
        )

        val stats = calculateDashboardStats(tasks, completions, sessions, today, zone)

        assertEquals(2_700, stats.focusSeconds)
        assertEquals(3, stats.tasksDone)
        assertTrue(stats.completionRate > 0)
        assertEquals(2, stats.currentStreakDays)
        assertEquals(2, stats.todayCompleted)
        assertEquals(2, stats.todayTotal)
        assertEquals(1, stats.todayHabitsDone)
        assertEquals(1, stats.todayHabitsTotal)
        assertEquals(100, stats.habitCompletionRate30Days)
        assertEquals(2, stats.currentHabitStreak)
        assertEquals(1, stats.weeklyFocusSessions)
        assertEquals(30, stats.averageFocusMinutes)
        assertEquals("健康", stats.categoryDistribution.first().name)
    }

    @Test
    fun calculatesDailyHabitProgressWithCurrentAndBestStreaks() {
        val today = LocalDate.parse("2026-05-11")
        val habit = task(id = "daily", title = "背单词", repeat = RepeatRule.DAILY, isHabit = true, date = "2026-05-08")
        val completions = listOf(
            TaskCompletionEntity("daily", "2026-05-09", 1L),
            TaskCompletionEntity("daily", "2026-05-10", 1L),
            TaskCompletionEntity("daily", "2026-05-11", 1L),
        )

        val progress = habitProgressForTask(habit, completions, today)

        assertEquals(3, progress.currentStreak)
        assertEquals(3, progress.bestStreak)
        assertEquals(75, progress.completionRate30Days)
        assertTrue(progress.completedToday)
        assertEquals("天", progress.streakUnit)
    }

    @Test
    fun dailyHabitStreakStopsAtMissingOccurrence() {
        val today = LocalDate.parse("2026-05-11")
        val habit = task(id = "daily", title = "背单词", repeat = RepeatRule.DAILY, isHabit = true, date = "2026-05-09")
        val completions = listOf(
            TaskCompletionEntity("daily", "2026-05-09", 1L),
            TaskCompletionEntity("daily", "2026-05-11", 1L),
        )

        val progress = habitProgressForTask(habit, completions, today)

        assertEquals(1, progress.currentStreak)
        assertEquals(1, progress.bestStreak)
        assertEquals(67, progress.completionRate30Days)
    }

    @Test
    fun calculatesWeeklyAndMonthlyHabitProgressByOccurrence() {
        val weekly = task(id = "weekly", title = "周复盘", repeat = RepeatRule.WEEKLY, isHabit = true, date = "2026-05-04")
        val monthly = task(id = "monthly", title = "月度整理", repeat = RepeatRule.MONTHLY, isHabit = true, date = "2026-01-15")
        val completions = listOf(
            TaskCompletionEntity("weekly", "2026-05-11", 1L),
            TaskCompletionEntity("weekly", "2026-05-18", 1L),
            TaskCompletionEntity("weekly", "2026-05-25", 1L),
            TaskCompletionEntity("monthly", "2026-01-15", 1L),
            TaskCompletionEntity("monthly", "2026-03-15", 1L),
            TaskCompletionEntity("monthly", "2026-04-15", 1L),
        )

        val weeklyProgress = habitProgressForTask(weekly, completions, LocalDate.parse("2026-05-25"))
        val monthlyProgress = habitProgressForTask(monthly, completions, LocalDate.parse("2026-04-15"))

        assertEquals(3, weeklyProgress.currentStreak)
        assertEquals(3, weeklyProgress.bestStreak)
        assertEquals("次", weeklyProgress.streakUnit)
        assertEquals(2, monthlyProgress.currentStreak)
        assertEquals(2, monthlyProgress.bestStreak)
        assertEquals("次", monthlyProgress.streakUnit)
    }

    private fun task(
        id: String,
        title: String,
        category: String = "学习",
        priority: Priority = Priority.MEDIUM,
        completed: Boolean = false,
        repeat: RepeatRule = RepeatRule.NONE,
        isHabit: Boolean = false,
        date: String = "2026-05-11",
    ) = TaskEntity(
        id = id,
        title = title,
        notes = "",
        date = date,
        startTime = "09:00",
        endTime = "10:00",
        category = category,
        priority = priority.name,
        completed = completed,
        isHabit = isHabit,
        repeatRule = repeat.name,
        createdAt = 1L,
        updatedAt = 1L,
    )

    private fun millis(value: String, zone: ZoneId): Long {
        return LocalDateTime.parse(value).atZone(zone).toInstant().toEpochMilli()
    }
}
