package com.example.finalproject

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.finalproject.data.ClarityDatabase
import com.example.finalproject.data.ClarityRepository
import com.example.finalproject.data.CompletionFilter
import com.example.finalproject.data.FocusSessionEntity
import com.example.finalproject.data.Priority
import com.example.finalproject.data.RepeatRule
import com.example.finalproject.data.SettingsStore
import com.example.finalproject.data.TaskCompletionEntity
import com.example.finalproject.data.TaskDraft
import com.example.finalproject.data.TaskEntity
import com.example.finalproject.data.TaskInstance
import com.example.finalproject.data.filterTaskInstances
import com.example.finalproject.data.taskInstancesForDate
import com.example.finalproject.data.validateTaskDraft
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

enum class AppTab(val label: String) {
    CALENDAR("日历"),
    TASKS("任务"),
    STATS("统计"),
    TIMER("专注"),
    SETTINGS("设置"),
}

class ClarityViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ClarityRepository(
        dao = ClarityDatabase.get(application).clarityDao(),
        settingsStore = SettingsStore(application),
    )

    val tasks: StateFlow<List<TaskEntity>> = repository.tasks.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )
    val completions: StateFlow<List<TaskCompletionEntity>> = repository.completions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )
    val focusSessions: StateFlow<List<FocusSessionEntity>> = repository.focusSessions.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        emptyList(),
    )
    val primaryColorHex: StateFlow<String> = repository.primaryColorHex.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        "#3E6658",
    )

    var currentTab = androidx.compose.runtime.mutableStateOf(AppTab.CALENDAR)
        private set
    var selectedDate = androidx.compose.runtime.mutableStateOf(LocalDate.now())
        private set
    var calendarExpanded = androidx.compose.runtime.mutableStateOf(false)
        private set
    var editingTaskId = androidx.compose.runtime.mutableStateOf<String?>(null)
        private set
    var editorOpen = androidx.compose.runtime.mutableStateOf(false)
        private set
    var selectedCategory = androidx.compose.runtime.mutableStateOf("全部任务")
        private set
    var searchQuery = androidx.compose.runtime.mutableStateOf("")
        private set
    var priorityFilter = androidx.compose.runtime.mutableStateOf<Priority?>(null)
        private set
    var completionFilter = androidx.compose.runtime.mutableStateOf(CompletionFilter.ALL)
        private set
    var selectedFocusTaskId = androidx.compose.runtime.mutableStateOf<String?>(null)
        private set

    private val messages = MutableSharedFlow<String>(extraBufferCapacity = 1)
    val messageEvents = messages.asSharedFlow()

    init {
        viewModelScope.launch {
            repository.seedIfEmpty()
        }
    }

    fun switchTab(tab: AppTab) {
        currentTab.value = tab
    }

    fun openEditor(taskId: String?) {
        editingTaskId.value = taskId
        editorOpen.value = true
    }

    fun closeEditor() {
        editingTaskId.value = null
        editorOpen.value = false
    }

    fun selectDate(date: LocalDate) {
        selectedDate.value = date
    }

    fun shiftCalendar(amount: Long) {
        selectedDate.value = if (calendarExpanded.value) {
            selectedDate.value.plusMonths(amount)
        } else {
            selectedDate.value.plusWeeks(amount)
        }
    }

    fun setCalendarExpanded(expanded: Boolean) {
        calendarExpanded.value = expanded
    }

    fun setCategory(category: String) {
        selectedCategory.value = category
        currentTab.value = AppTab.TASKS
    }

    fun setSearchQuery(query: String) {
        searchQuery.value = query
    }

    fun setPriorityFilter(priority: Priority?) {
        priorityFilter.value = priority
    }

    fun setCompletionFilter(filter: CompletionFilter) {
        completionFilter.value = filter
    }

    fun setFocusTask(taskId: String?) {
        selectedFocusTaskId.value = taskId
    }

    fun instancesFor(date: LocalDate): List<TaskInstance> {
        return taskInstancesForDate(tasks.value, completions.value, date)
    }

    fun filteredTaskInstances(): List<TaskInstance> {
        val today = LocalDate.now()
        val instances = tasks.value.flatMap { task ->
            val date = runCatching { LocalDate.parse(task.date) }.getOrElse { today }
            val displayDate = if (RepeatRule.fromStored(task.repeatRule) == RepeatRule.NONE && !task.isHabit) date else today
            taskInstancesForDate(listOf(task), completions.value, displayDate)
        }
        return filterTaskInstances(
            instances = instances,
            query = searchQuery.value,
            category = selectedCategory.value,
            priority = priorityFilter.value,
            completionFilter = completionFilter.value,
        )
    }

    fun saveTask(id: String?, draft: TaskDraft) {
        val validationError = validate(draft)
        if (validationError != null) {
            messages.tryEmit(validationError)
            return
        }
        val existing = tasks.value.firstOrNull { it.id == id }
        viewModelScope.launch {
            repository.saveTask(id, draft, completed = existing?.completed ?: false)
            editingTaskId.value = null
            editorOpen.value = false
            currentTab.value = AppTab.TASKS
            messages.emit(if (id == null) "任务已创建" else "任务已保存")
        }
    }

    fun toggleCompletion(instance: TaskInstance) {
        viewModelScope.launch {
            repository.updateTaskCompletion(instance.task, instance.date, !instance.completed)
            messages.emit(if (instance.completed) "已标记为未完成" else "已完成")
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.deleteTask(task.id)
            editingTaskId.value = null
            editorOpen.value = false
            currentTab.value = AppTab.TASKS
            messages.emit("任务已删除")
        }
    }

    fun addFocusSession(task: TaskEntity?, seconds: Long) {
        viewModelScope.launch {
            repository.addFocusSession(task, seconds)
            if (seconds > 0) messages.emit("专注记录已保存")
        }
    }

    fun setPrimaryColor(hex: String) {
        viewModelScope.launch {
            repository.setPrimaryColor(hex)
            messages.emit("主题色已更新")
        }
    }

    private fun validate(draft: TaskDraft): String? {
        return validateTaskDraft(draft)
    }
}
