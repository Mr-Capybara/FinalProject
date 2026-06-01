package com.example.finalproject.data

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import kotlin.math.roundToInt

fun TaskEntity.occursOn(date: LocalDate): Boolean {
    val startDate = runCatching { LocalDate.parse(this.date) }.getOrNull() ?: return false
    if (date.isBefore(startDate)) return false
    return when (RepeatRule.fromStored(repeatRule)) {
        RepeatRule.NONE -> date == startDate
        RepeatRule.DAILY -> true
        RepeatRule.WEEKLY -> date.dayOfWeek == startDate.dayOfWeek
        RepeatRule.MONTHLY -> date.dayOfMonth == startDate.dayOfMonth
    }
}

fun taskInstancesForDate(
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
    date: LocalDate,
): List<TaskInstance> {
    val completionKeys = completions.map { it.taskId to it.date }.toSet()
    return tasks
        .filter { it.occursOn(date) }
        .map { task ->
            val completed = if (RepeatRule.fromStored(task.repeatRule) == RepeatRule.NONE && !task.isHabit) {
                task.completed
            } else {
                (task.id to date.toString()) in completionKeys
            }
            TaskInstance(task, date, completed)
        }
        .sortedWith(compareBy<TaskInstance> { it.task.startTime.ifBlank { "99:99" } }.thenBy { it.task.title })
}

fun weekDates(anchor: LocalDate): List<LocalDate> {
    val sundayOffset = anchor.dayOfWeek.value % 7L
    val start = anchor.minusDays(sundayOffset)
    return (0..6).map { start.plusDays(it.toLong()) }
}

fun monthGridDates(anchor: LocalDate): List<LocalDate?> {
    val first = anchor.withDayOfMonth(1)
    val blanks = first.dayOfWeek.value % 7
    val days = first.lengthOfMonth()
    return List(blanks) { null } + (1..days).map { first.withDayOfMonth(it) }
}

fun filterTaskInstances(
    instances: List<TaskInstance>,
    query: String,
    category: String,
    priority: Priority?,
    completionFilter: CompletionFilter,
): List<TaskInstance> {
    val normalized = query.trim()
    return instances.filter { item ->
        val task = item.task
        val matchesQuery = normalized.isEmpty() ||
            task.title.contains(normalized, ignoreCase = true) ||
            task.notes.contains(normalized, ignoreCase = true) ||
            task.category.contains(normalized, ignoreCase = true)
        val matchesCategory = category == "全部任务" || task.category == category
        val matchesPriority = priority == null || Priority.fromStored(task.priority) == priority
        val matchesCompletion = when (completionFilter) {
            CompletionFilter.ALL -> true
            CompletionFilter.ACTIVE -> !item.completed
            CompletionFilter.DONE -> item.completed
        }
        matchesQuery && matchesCategory && matchesPriority && matchesCompletion
    }.sortedWith(compareBy<TaskInstance> { it.completed }.thenBy { it.task.date }.thenBy { it.task.startTime.ifBlank { "99:99" } })
}

fun calculateDashboardStats(
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
    focusSessions: List<FocusSessionEntity>,
    today: LocalDate,
    zoneId: ZoneId = ZoneId.systemDefault(),
): DashboardStats {
    val lookbackStart = today.minusDays(59)
    val allInstances = generateSequence(lookbackStart) { it.plusDays(1) }
        .takeWhile { !it.isAfter(today) }
        .flatMap { date -> taskInstancesForDate(tasks, completions, date).asSequence() }
        .toList()
    val completedInstances = allInstances.filter { it.completed }
    val completionRate = if (allInstances.isEmpty()) 0 else ((completedInstances.size.toDouble() / allInstances.size) * 100).roundToInt()

    val currentWeekStart = today.with(DayOfWeek.MONDAY)
    val previousWeekStart = currentWeekStart.minusWeeks(1)
    fun sessionDate(session: FocusSessionEntity) = Instant.ofEpochMilli(session.completedAt).atZone(zoneId).toLocalDate()
    val currentWeekSessions = focusSessions
        .filter { !sessionDate(it).isBefore(currentWeekStart) && !sessionDate(it).isAfter(today) }
    val currentWeekSeconds = currentWeekSessions
        .sumOf { it.durationSeconds }
    val previousWeekSeconds = focusSessions
        .filter {
            val d = sessionDate(it)
            !d.isBefore(previousWeekStart) && d.isBefore(currentWeekStart)
        }
        .sumOf { it.durationSeconds }
    val trend = when {
        previousWeekSeconds <= 0L && currentWeekSeconds > 0L -> 100
        previousWeekSeconds <= 0L -> 0
        else -> (((currentWeekSeconds - previousWeekSeconds).toDouble() / previousWeekSeconds) * 100).roundToInt()
    }

    val categoryDistribution = completedInstances
        .groupBy { it.task.category }
        .mapValues { it.value.size }
        .entries
        .sortedByDescending { it.value }
        .map { entry ->
            CategoryShare(
                name = entry.key,
                percentage = if (completedInstances.isEmpty()) 0 else ((entry.value.toDouble() / completedInstances.size) * 100).roundToInt(),
            )
        }

    val todayInstances = taskInstancesForDate(tasks, completions, today)
    val weekCompletion = weekDates(today).map { date ->
        val instances = taskInstancesForDate(tasks, completions, date)
        CompletionBucket(
            date = date,
            completed = instances.count { it.completed },
            total = instances.size,
        )
    }
    val habitProgress = tasks
        .filter { it.isHabit }
        .map { habitProgressForTask(it, completions, today) }
    val todayHabitInstances = todayInstances.filter { it.task.isHabit }
    val recentHabitInstances = generateSequence(today.minusDays(29)) { it.plusDays(1) }
        .takeWhile { !it.isAfter(today) }
        .flatMap { date -> taskInstancesForDate(tasks.filter { it.isHabit }, completions, date).asSequence() }
        .toList()
    val recentHabitsDone = recentHabitInstances.count { it.completed }
    val averageFocusMinutes = if (currentWeekSessions.isEmpty()) {
        0
    } else {
        (currentWeekSessions.sumOf { it.durationSeconds }.toDouble() / currentWeekSessions.size / 60).roundToInt()
    }

    return DashboardStats(
        focusSeconds = focusSessions.sumOf { it.durationSeconds },
        focusTrendPercent = trend,
        completionRate = completionRate,
        tasksDone = completedInstances.size,
        currentStreakDays = currentStreakDays(completedInstances.map { it.date }.toSet(), today),
        todayCompleted = todayInstances.count { it.completed },
        todayTotal = todayInstances.size,
        weeklyCompletion = weekCompletion,
        todayHabitsDone = todayHabitInstances.count { it.completed },
        todayHabitsTotal = todayHabitInstances.size,
        habitCompletionRate30Days = if (recentHabitInstances.isEmpty()) 0 else ((recentHabitsDone.toDouble() / recentHabitInstances.size) * 100).roundToInt(),
        currentHabitStreak = habitProgress.maxOfOrNull { it.currentStreak } ?: 0,
        bestHabitStreak = habitProgress.maxOfOrNull { it.bestStreak } ?: 0,
        weeklyFocusSessions = currentWeekSessions.size,
        averageFocusMinutes = averageFocusMinutes,
        habitLeaders = habitProgress
            .sortedWith(compareByDescending<HabitProgress> { it.currentStreak }.thenByDescending { it.completionRate30Days }.thenBy { it.title })
            .take(3),
        categoryDistribution = categoryDistribution,
    )
}

fun habitProgressForTask(
    task: TaskEntity,
    completions: List<TaskCompletionEntity>,
    today: LocalDate,
): HabitProgress {
    val completionDates = completions
        .asSequence()
        .filter { it.taskId == task.id }
        .mapNotNull { runCatching { LocalDate.parse(it.date) }.getOrNull() }
        .toSet()
    val scheduledDates = occurrenceDates(task, today)
    val currentStreak = scheduledDates.asReversed().takeWhile { it in completionDates }.count()
    var bestStreak = 0
    var run = 0
    scheduledDates.forEach { date ->
        if (date in completionDates) {
            run += 1
            bestStreak = maxOf(bestStreak, run)
        } else {
            run = 0
        }
    }
    val recentDates = scheduledDates.filter { !it.isBefore(today.minusDays(29)) }
    val recentCompleted = recentDates.count { it in completionDates }
    val repeatRule = RepeatRule.fromStored(task.repeatRule)

    return HabitProgress(
        taskId = task.id,
        title = task.title,
        currentStreak = currentStreak,
        bestStreak = bestStreak,
        completionRate30Days = if (recentDates.isEmpty()) 0 else ((recentCompleted.toDouble() / recentDates.size) * 100).roundToInt(),
        completedToday = today in completionDates,
        scheduledToday = task.occursOn(today),
        streakUnit = if (repeatRule == RepeatRule.DAILY || task.isHabit && repeatRule == RepeatRule.NONE) "天" else "次",
    )
}

private fun currentStreakDays(completedDates: Set<LocalDate>, today: LocalDate): Int {
    if (completedDates.isEmpty()) return 0
    var cursor = today
    var streak = 0
    while (cursor in completedDates) {
        streak += 1
        cursor = cursor.minusDays(1)
    }
    return streak
}

fun occurrenceCountUntil(task: TaskEntity, today: LocalDate): Int {
    val start = runCatching { LocalDate.parse(task.date) }.getOrNull() ?: return 0
    if (today.isBefore(start)) return 0
    return when (RepeatRule.fromStored(task.repeatRule)) {
        RepeatRule.NONE -> 1
        RepeatRule.DAILY -> ChronoUnit.DAYS.between(start, today).toInt() + 1
        RepeatRule.WEEKLY -> ChronoUnit.WEEKS.between(start, today).toInt() + 1
        RepeatRule.MONTHLY -> ChronoUnit.MONTHS.between(start.withDayOfMonth(1), today.withDayOfMonth(1)).toInt() + 1
    }
}

private fun occurrenceDates(task: TaskEntity, today: LocalDate): List<LocalDate> {
    val start = runCatching { LocalDate.parse(task.date) }.getOrNull() ?: return emptyList()
    if (today.isBefore(start)) return emptyList()
    return generateSequence(start) { it.plusDays(1) }
        .takeWhile { !it.isAfter(today) }
        .filter { task.occursOn(it) }
        .toList()
}
