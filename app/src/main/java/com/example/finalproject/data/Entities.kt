package com.example.finalproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey val id: String,
    val title: String,
    val notes: String,
    val date: String,
    val startTime: String,
    val endTime: String,
    val category: String,
    val priority: String,
    val completed: Boolean,
    val isHabit: Boolean,
    val repeatRule: String,
    val createdAt: Long,
    val updatedAt: Long,
)

@Entity(tableName = "task_completions", primaryKeys = ["taskId", "date"])
data class TaskCompletionEntity(
    val taskId: String,
    val date: String,
    val completedAt: Long,
)

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey val id: String,
    val taskId: String?,
    val taskTitle: String,
    val durationSeconds: Long,
    val completedAt: Long,
)
