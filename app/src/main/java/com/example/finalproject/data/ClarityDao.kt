package com.example.finalproject.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClarityDao {
    @Query("SELECT * FROM tasks ORDER BY date ASC, startTime ASC, createdAt ASC")
    fun observeTasks(): Flow<List<TaskEntity>>

    @Query("SELECT * FROM task_completions")
    fun observeCompletions(): Flow<List<TaskCompletionEntity>>

    @Query("SELECT * FROM focus_sessions ORDER BY completedAt DESC")
    fun observeFocusSessions(): Flow<List<FocusSessionEntity>>

    @Query("SELECT COUNT(*) FROM tasks")
    suspend fun taskCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTask(task: TaskEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTasks(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTask(id: String)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId")
    suspend fun deleteCompletionsForTask(taskId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun markComplete(completion: TaskCompletionEntity)

    @Query("DELETE FROM task_completions WHERE taskId = :taskId AND date = :date")
    suspend fun clearCompletion(taskId: String, date: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFocusSession(session: FocusSessionEntity)
}
