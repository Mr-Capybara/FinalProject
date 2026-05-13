package com.example.finalproject.data

import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.util.UUID

class ClarityRepository(
    private val dao: ClarityDao,
    private val settingsStore: SettingsStore,
) {
    val tasks: Flow<List<TaskEntity>> = dao.observeTasks()
    val completions: Flow<List<TaskCompletionEntity>> = dao.observeCompletions()
    val focusSessions: Flow<List<FocusSessionEntity>> = dao.observeFocusSessions()
    val primaryColorHex: Flow<String> = settingsStore.primaryColorHex

    suspend fun seedIfEmpty() {
        if (dao.taskCount() > 0) return
        val today = LocalDate.now().toString()
        val now = System.currentTimeMillis()
        dao.upsertTasks(
            listOf(
                seedTask("正念冥想", "健康", Priority.MEDIUM, today, "08:00", "08:30", true, now),
                seedTask("起草 Q4 战略文档", "深度工作", Priority.HIGH, today, "11:00", "12:30", false, now),
                seedTask("设计系统评审同步", "会议", Priority.MEDIUM, today, "14:00", "15:00", false, now),
                seedTask("处理团队 UI 反馈", "设计", Priority.LOW, today, "16:00", "17:00", false, now),
                seedTask("阅读《设计心理学》", "学习", Priority.LOW, today, "20:00", "21:00", false, now),
            )
        )
    }

    suspend fun saveTask(id: String?, draft: TaskDraft, completed: Boolean = false) {
        val now = System.currentTimeMillis()
        dao.upsertTask(
            TaskEntity(
                id = id ?: UUID.randomUUID().toString(),
                title = draft.title.trim(),
                notes = draft.notes.trim(),
                date = draft.date,
                startTime = draft.startTime,
                endTime = draft.endTime,
                category = draft.category.trim().ifBlank { "未分类" },
                priority = draft.priority.name,
                completed = completed,
                isHabit = draft.isHabit,
                repeatRule = if (draft.isHabit && draft.repeatRule == RepeatRule.NONE) RepeatRule.DAILY.name else draft.repeatRule.name,
                createdAt = now,
                updatedAt = now,
            )
        )
    }

    suspend fun updateTaskCompletion(task: TaskEntity, date: LocalDate, completed: Boolean) {
        if (RepeatRule.fromStored(task.repeatRule) == RepeatRule.NONE && !task.isHabit) {
            dao.upsertTask(task.copy(completed = completed, updatedAt = System.currentTimeMillis()))
        } else {
            if (completed) {
                dao.markComplete(TaskCompletionEntity(task.id, date.toString(), System.currentTimeMillis()))
            } else {
                dao.clearCompletion(task.id, date.toString())
            }
        }
    }

    suspend fun deleteTask(id: String) {
        dao.deleteCompletionsForTask(id)
        dao.deleteTask(id)
    }

    suspend fun addFocusSession(task: TaskEntity?, durationSeconds: Long) {
        if (durationSeconds <= 0) return
        dao.addFocusSession(
            FocusSessionEntity(
                id = UUID.randomUUID().toString(),
                taskId = task?.id,
                taskTitle = task?.title ?: "自由专注",
                durationSeconds = durationSeconds,
                completedAt = System.currentTimeMillis(),
            )
        )
    }

    suspend fun setPrimaryColor(hex: String) {
        settingsStore.setPrimaryColor(hex)
    }

    private fun seedTask(
        title: String,
        category: String,
        priority: Priority,
        date: String,
        startTime: String,
        endTime: String,
        completed: Boolean,
        now: Long,
    ) = TaskEntity(
        id = UUID.randomUUID().toString(),
        title = title,
        notes = "",
        date = date,
        startTime = startTime,
        endTime = endTime,
        category = category,
        priority = priority.name,
        completed = completed,
        isHabit = false,
        repeatRule = RepeatRule.NONE.name,
        createdAt = now,
        updatedAt = now,
    )
}
