package com.example.finalproject.data

import java.time.LocalDate

enum class Priority(val label: String) {
    LOW("低"),
    MEDIUM("中"),
    HIGH("高");

    companion object {
        fun fromStored(value: String): Priority = entries.firstOrNull { it.name == value } ?: MEDIUM
    }
}

enum class RepeatRule(val label: String) {
    NONE("不重复"),
    DAILY("每天"),
    WEEKLY("每周"),
    MONTHLY("每月");

    companion object {
        fun fromStored(value: String): RepeatRule = entries.firstOrNull { it.name == value } ?: NONE
    }
}

enum class CompletionFilter(val label: String) {
    ALL("全部"),
    ACTIVE("未完成"),
    DONE("已完成")
}

data class TaskDraft(
    val title: String,
    val notes: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val category: String,
    val priority: Priority,
    val isHabit: Boolean,
    val repeatRule: RepeatRule,
)

data class TaskInstance(
    val task: TaskEntity,
    val date: LocalDate,
    val completed: Boolean,
) {
    val stableId: String = "${task.id}-${date}"
}

data class DashboardStats(
    val focusSeconds: Long,
    val focusTrendPercent: Int,
    val completionRate: Int,
    val tasksDone: Int,
    val currentStreakDays: Int,
    val categoryDistribution: List<CategoryShare>,
)

data class CategoryShare(
    val name: String,
    val percentage: Int,
)
