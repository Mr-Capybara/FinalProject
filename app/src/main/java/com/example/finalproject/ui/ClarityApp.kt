package com.example.finalproject.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.BarChart
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.FormatListBulleted
import androidx.compose.material.icons.rounded.HelpOutline
import androidx.compose.material.icons.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.Label
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material.icons.rounded.MusicNote
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Pause
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.Stop
import androidx.compose.material.icons.rounded.Timer
import androidx.compose.material.icons.rounded.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.finalproject.AppTab
import com.example.finalproject.ClarityViewModel
import com.example.finalproject.audio.BrownNoisePlayer
import com.example.finalproject.data.CategoryShare
import com.example.finalproject.data.CompletionFilter
import com.example.finalproject.data.CompletionBucket
import com.example.finalproject.data.FocusSessionEntity
import com.example.finalproject.data.HabitProgress
import com.example.finalproject.data.Priority
import com.example.finalproject.data.RepeatRule
import com.example.finalproject.data.TaskCompletionEntity
import com.example.finalproject.data.TaskDraft
import com.example.finalproject.data.TaskEntity
import com.example.finalproject.data.TaskInstance
import com.example.finalproject.data.calculateDashboardStats
import com.example.finalproject.data.monthGridDates
import com.example.finalproject.data.taskInstancesForDate
import com.example.finalproject.data.weekDates
import com.example.finalproject.ui.theme.ClarityPrimary
import com.example.finalproject.ui.theme.ClarityPrimaryContainer
import com.example.finalproject.ui.theme.ClaritySecondaryContainer
import com.example.finalproject.ui.theme.ClarityTertiaryContainer
import kotlinx.coroutines.delay
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val ContainerPadding = 24.dp
private val StackGap = 16.dp
private val ItemPadding = 12.dp
private val SoftShape = RoundedCornerShape(16.dp)
private val ItemShape = RoundedCornerShape(12.dp)
private val DateFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日")
private val MonthFormatter = DateTimeFormatter.ofPattern("yyyy年M月")

@Composable
fun ClarityApp(viewModel: ClarityViewModel) {
    val tasks by viewModel.tasks.collectAsState()
    val completions by viewModel.completions.collectAsState()
    val focusSessions by viewModel.focusSessions.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    var drawerOpen by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.messageEvents.collect { snackbarHostState.showSnackbar(it) }
    }

    Surface(color = MaterialTheme.colorScheme.surfaceContainerHighest, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .shadow(18.dp)
                    .background(MaterialTheme.colorScheme.background)
            ) {
                Scaffold(
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ) { innerPadding ->
                    if (viewModel.editorOpen.value) {
                        EditTaskScreen(
                            viewModel = viewModel,
                            task = tasks.firstOrNull { it.id == viewModel.editingTaskId.value },
                            categories = categories(tasks),
                            modifier = Modifier.padding(innerPadding),
                        )
                    } else {
                        MainShell(
                            viewModel = viewModel,
                            tasks = tasks,
                            completions = completions,
                            focusSessions = focusSessions,
                            onMenu = { drawerOpen = true },
                            modifier = Modifier.padding(innerPadding),
                        )
                    }
                }

                if (!viewModel.editorOpen.value && viewModel.currentTab.value == AppTab.TASKS) {
                    FloatingActionButton(
                        onClick = { viewModel.openEditor(null) },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        shape = ItemShape,
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .padding(end = 24.dp, bottom = 92.dp)
                    ) {
                        Icon(Icons.Rounded.Add, contentDescription = "新增任务")
                    }
                }

                CategoryDrawer(
                    open = drawerOpen,
                    categories = categories(tasks),
                    activeCategory = viewModel.selectedCategory.value,
                    onClose = { drawerOpen = false },
                    onSelect = {
                        viewModel.setCategory(it)
                        drawerOpen = false
                    },
                )
            }
        }
    }
}

@Composable
private fun MainShell(
    viewModel: ClarityViewModel,
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
    focusSessions: List<FocusSessionEntity>,
    onMenu: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(Modifier.fillMaxSize()) {
            TopBar(currentTab = viewModel.currentTab.value, onMenu = onMenu)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ContainerPadding)
                    .padding(top = 8.dp, bottom = 116.dp)
            ) {
                when (viewModel.currentTab.value) {
                    AppTab.CALENDAR -> CalendarScreen(viewModel, tasks, completions)
                    AppTab.TASKS -> TasksScreen(viewModel)
                    AppTab.STATS -> StatsScreen(tasks, completions, focusSessions)
                    AppTab.TIMER -> TimerScreen(viewModel, tasks, completions)
                    AppTab.SETTINGS -> SettingsScreen(viewModel)
                }
            }
        }
        BottomNav(
            active = viewModel.currentTab.value,
            onSelect = viewModel::switchTab,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun TopBar(currentTab: AppTab, onMenu: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .height(72.dp)
            .padding(horizontal = ContainerPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (currentTab == AppTab.TASKS) {
            IconButton(onClick = onMenu) {
                Icon(Icons.Rounded.Menu, contentDescription = "打开分类", tint = MaterialTheme.colorScheme.primary)
            }
        } else {
            Spacer(Modifier.size(48.dp))
        }
        Text(
            text = "Clarity",
            style = MaterialTheme.typography.headlineLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.weight(1f)
        )
        Spacer(Modifier.size(48.dp))
    }
}

@Composable
private fun BottomNav(active: AppTab, onSelect: (AppTab) -> Unit, modifier: Modifier = Modifier) {
    Box(modifier, contentAlignment = Alignment.BottomCenter) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .shadow(10.dp)
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            navItems().forEach { item ->
                val selected = item.tab == active
                Column(
                    modifier = Modifier
                        .clip(ItemShape)
                        .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
                        .clickable { onSelect(item.tab) }
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Icon(
                        item.icon,
                        contentDescription = item.tab.label,
                        tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        item.tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryDrawer(
    open: Boolean,
    categories: List<String>,
    activeCategory: String,
    onClose: () -> Unit,
    onSelect: (String) -> Unit,
) {
    AnimatedVisibility(visible = open) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.2f))
                    .clickable { onClose() }
            )
            Column(
                modifier = Modifier
                    .width(288.dp)
                    .fillMaxHeight()
                    .shadow(18.dp, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerLow, RoundedCornerShape(topEnd = 16.dp, bottomEnd = 16.dp))
                    .padding(ContainerPadding),
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    IconButton(onClick = onClose) {
                        Icon(Icons.Rounded.Close, contentDescription = "关闭")
                    }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text("AC", color = MaterialTheme.colorScheme.onPrimaryContainer, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("Alex Chen", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary)
                        Text("正在专注：深度工作", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                Spacer(Modifier.height(28.dp))
                Text("分类", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                Spacer(Modifier.height(8.dp))
                categories.forEach { category ->
                    val active = category == activeCategory
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(ItemShape)
                            .background(if (active) MaterialTheme.colorScheme.secondaryContainer else Color.Transparent)
                            .clickable { onSelect(category) }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            Icons.Rounded.Label,
                            contentDescription = null,
                            tint = if (active) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            category,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            color = if (active) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (active) FontWeight.Bold else FontWeight.Normal,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarScreen(
    viewModel: ClarityViewModel,
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
) {
    val selectedDate = viewModel.selectedDate.value
    val expanded = viewModel.calendarExpanded.value
    val visibleDates = if (expanded) monthGridDates(selectedDate) else weekDates(selectedDate)
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        PageCard(
            modifier = Modifier.pointerInput(expanded) {
                detectVerticalDragGestures(
                    onVerticalDrag = { _, dragAmount -> dragOffset += dragAmount },
                    onDragEnd = {
                        when {
                            dragOffset > 48f -> viewModel.setCalendarExpanded(true)
                            dragOffset < -48f -> viewModel.setCalendarExpanded(false)
                        }
                        dragOffset = 0f
                    }
                )
            }
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(selectedDate.format(MonthFormatter), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Medium)
                Row {
                    IconButton(onClick = { viewModel.shiftCalendar(-1) }) {
                        Icon(Icons.Rounded.ChevronLeft, contentDescription = "上一页")
                    }
                    IconButton(onClick = { viewModel.shiftCalendar(1) }) {
                        Icon(Icons.Rounded.ChevronRight, contentDescription = "下一页")
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            CalendarWeekHeader()
            CalendarDateGrid(
                dates = visibleDates,
                selectedDate = selectedDate,
                tasks = tasks,
                completions = completions,
                onSelect = {
                    viewModel.selectDate(it)
                    if (expanded) viewModel.setCalendarExpanded(false)
                },
            )
            Spacer(Modifier.height(14.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.setCalendarExpanded(!expanded) }
                    .padding(vertical = 8.dp),
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    Modifier
                        .width(48.dp)
                        .height(6.dp)
                        .background(MaterialTheme.colorScheme.surfaceContainerHighest, CircleShape)
                )
            }
        }

        Column {
            Text("日程", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Medium)
            Spacer(Modifier.height(StackGap))
            val dayTasks = taskInstancesForDate(tasks, completions, selectedDate)
            if (dayTasks.isEmpty()) {
                EmptyState("这一天还没有安排任务。")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(StackGap)) {
                    dayTasks.forEach { instance ->
                        TaskRow(
                            instance = instance,
                            showCategory = true,
                            showDate = false,
                            onToggle = { viewModel.toggleCompletion(instance) },
                            onClick = { viewModel.openEditor(instance.task.id) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CalendarWeekHeader() {
    Row(Modifier.fillMaxWidth()) {
        listOf("日", "一", "二", "三", "四", "五", "六").forEach {
            Text(
                it,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.outline,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun CalendarDateGrid(
    dates: List<LocalDate?>,
    selectedDate: LocalDate,
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
    onSelect: (LocalDate) -> Unit,
) {
    val rows = dates.chunked(7)
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        rows.forEach { rowDates ->
            Row(Modifier.fillMaxWidth()) {
                rowDates.forEach { date ->
                    if (date == null) {
                        Spacer(Modifier.weight(1f).height(42.dp))
                    } else {
                        val selected = date == selectedDate
                        val today = date == LocalDate.now()
                        val dayInstances = taskInstancesForDate(tasks, completions, date)
                        val hasRegularTasks = dayInstances.any { !it.task.isHabit }
                        val habitInstances = dayInstances.filter { it.task.isHabit }
                        val hasHabits = habitInstances.isNotEmpty()
                        val hasCompletedHabit = habitInstances.any { it.completed }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(42.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(
                                        when {
                                            selected -> MaterialTheme.colorScheme.primary
                                            today -> MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                                            else -> Color.Transparent
                                        }
                                    )
                                    .clickable { onSelect(date) },
                                contentAlignment = Alignment.Center,
                            ) {
                                Text(
                                    date.dayOfMonth.toString(),
                                    color = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
                                    style = MaterialTheme.typography.bodyMedium,
                                )
                                if ((hasRegularTasks || hasHabits) && !selected) {
                                    Row(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .padding(bottom = 4.dp),
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                    ) {
                                        if (hasRegularTasks) {
                                            Box(
                                                Modifier
                                                    .size(4.dp)
                                                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                                            )
                                        }
                                        if (hasHabits) {
                                            HabitCalendarDot(completed = hasCompletedHabit)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                repeat(7 - rowDates.size) {
                    Spacer(Modifier.weight(1f).height(42.dp))
                }
            }
        }
    }
}

@Composable
private fun HabitCalendarDot(completed: Boolean) {
    val color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.75f)
    val modifier = Modifier
        .size(5.dp)
        .clip(CircleShape)
    Box(
        if (completed) {
            modifier.background(color, CircleShape)
        } else {
            modifier.border(1.dp, color, CircleShape)
        }
    )
}

@Composable
private fun TasksScreen(viewModel: ClarityViewModel) {
    val filtered = viewModel.filteredTaskInstances()
    Column(verticalArrangement = Arrangement.spacedBy(StackGap)) {
        Text(viewModel.selectedCategory.value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
        OutlinedTextField(
            value = viewModel.searchQuery.value,
            onValueChange = viewModel::setSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
            placeholder = { Text("搜索标题、备注或分类") },
            shape = ItemShape,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
            )
        )
        FilterPanel(viewModel)
        if (filtered.isEmpty()) {
            EmptyState("没有符合条件的任务。")
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(StackGap)) {
                filtered.forEach { instance ->
                    TaskRow(
                        instance = instance,
                        showCategory = viewModel.selectedCategory.value == "全部任务",
                        showDate = true,
                        onToggle = { viewModel.toggleCompletion(instance) },
                        onClick = { viewModel.openEditor(instance.task.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun FilterPanel(viewModel: ClarityViewModel) {
    PageCard(contentPadding = 12.dp) {
        Text("筛选", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(8.dp))
        ChipRow {
            CompletionFilter.entries.forEach { filter ->
                FilterChip(
                    selected = viewModel.completionFilter.value == filter,
                    onClick = { viewModel.setCompletionFilter(filter) },
                    label = { Text(filter.label) },
                )
            }
        }
        Spacer(Modifier.height(8.dp))
        ChipRow {
            FilterChip(
                selected = viewModel.priorityFilter.value == null,
                onClick = { viewModel.setPriorityFilter(null) },
                label = { Text("全部优先级") },
            )
            Priority.entries.forEach { priority ->
                FilterChip(
                    selected = viewModel.priorityFilter.value == priority,
                    onClick = { viewModel.setPriorityFilter(priority) },
                    label = { Text(priority.label) },
                    leadingIcon = { Icon(Icons.Rounded.Flag, contentDescription = null, modifier = Modifier.size(16.dp)) },
                )
            }
        }
    }
}

@Composable
private fun EditTaskScreen(
    viewModel: ClarityViewModel,
    task: TaskEntity?,
    categories: List<String>,
    modifier: Modifier = Modifier,
) {
    var title by remember(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var notes by remember(task?.id) { mutableStateOf(task?.notes.orEmpty()) }
    var category by remember(task?.id) { mutableStateOf(task?.category ?: "工作") }
    var priority by remember(task?.id) { mutableStateOf(task?.let { Priority.fromStored(it.priority) } ?: Priority.MEDIUM) }
    var date by remember(task?.id) { mutableStateOf(task?.date ?: LocalDate.now().toString()) }
    var startTime by remember(task?.id) { mutableStateOf(task?.startTime ?: "12:00") }
    var endTime by remember(task?.id) { mutableStateOf(task?.endTime ?: "13:00") }
    var isHabit by remember(task?.id) { mutableStateOf(task?.isHabit ?: false) }
    var repeatRule by remember(task?.id) { mutableStateOf(task?.let { RepeatRule.fromStored(it.repeatRule) } ?: RepeatRule.NONE) }
    var newCategoryOpen by remember { mutableStateOf(false) }
    var deleteConfirmOpen by remember { mutableStateOf(false) }
    var datePickerOpen by remember { mutableStateOf(false) }
    var startTimePickerOpen by remember { mutableStateOf(false) }
    var endTimePickerOpen by remember { mutableStateOf(false) }
    val visibleCategories = (listOf(category) + categories.filter { it != "全部任务" })
        .map { it.trim() }
        .filter { it.isNotBlank() }
        .distinct()
        .take(8)

    Box(
        modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .imePadding()
    ) {
        Column(Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .height(72.dp)
                    .padding(horizontal = ContainerPadding),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = viewModel::closeEditor) {
                    Icon(Icons.Rounded.ArrowBack, contentDescription = "返回")
                }
                Text(
                    if (task == null) "新建任务" else "编辑任务",
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                if (task != null) {
                    IconButton(onClick = { deleteConfirmOpen = true }) {
                        Icon(Icons.Rounded.Delete, contentDescription = "删除任务", tint = MaterialTheme.colorScheme.error)
                    }
                } else {
                    Spacer(Modifier.size(48.dp))
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = ContainerPadding)
                    .padding(bottom = 116.dp),
                verticalArrangement = Arrangement.spacedBy(StackGap),
            ) {
                PageCard {
                    FieldLabel("任务标题")
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("要完成什么？") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ItemShape,
                    )
                }
                PageCard {
                    FieldLabel("分类")
                    ChipRow {
                        visibleCategories.forEach { item ->
                            FilterChip(selected = category == item, onClick = { category = item }, label = { Text(item) })
                        }
                        OutlinedButton(onClick = { newCategoryOpen = true }, shape = CircleShape) {
                            Icon(Icons.Rounded.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("新建")
                        }
                    }
                }
                PageCard {
                    FieldLabel("优先级")
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                        Priority.entries.forEach { item ->
                            SelectBox(
                                selected = priority == item,
                                title = item.label,
                                icon = Icons.Rounded.Flag,
                                modifier = Modifier.weight(1f),
                                onClick = { priority = item },
                            )
                        }
                    }
                }
                PageCard {
                    FieldLabel("日期与时间")
                    ReadOnlyPickerField(
                        value = date,
                        label = "日期",
                        icon = Icons.Rounded.CalendarMonth,
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { datePickerOpen = true },
                    )
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ReadOnlyPickerField(
                            value = startTime,
                            label = "开始时间",
                            icon = Icons.Rounded.Timer,
                            modifier = Modifier.weight(1f),
                            onClick = { startTimePickerOpen = true },
                        )
                        ReadOnlyPickerField(
                            value = endTime,
                            label = "结束时间",
                            icon = Icons.Rounded.Timer,
                            modifier = Modifier.weight(1f),
                            onClick = { endTimePickerOpen = true },
                        )
                    }
                }
                PageCard {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                        Column(Modifier.weight(1f)) {
                            FieldLabel("习惯打卡")
                            Text("开启后按天记录完成状态", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = isHabit, onCheckedChange = {
                            isHabit = it
                            if (it && repeatRule == RepeatRule.NONE) repeatRule = RepeatRule.DAILY
                        })
                    }
                    Spacer(Modifier.height(12.dp))
                    FieldLabel("重复")
                    ChipRow {
                        RepeatRule.entries.forEach { rule ->
                            FilterChip(
                                selected = repeatRule == rule,
                                onClick = {
                                    repeatRule = rule
                                    if (rule != RepeatRule.NONE) isHabit = isHabit
                                },
                                label = { Text(rule.label) },
                                leadingIcon = {
                                    if (rule != RepeatRule.NONE) Icon(Icons.Rounded.Repeat, contentDescription = null, modifier = Modifier.size(16.dp))
                                },
                            )
                        }
                    }
                }
                PageCard {
                    FieldLabel("备注")
                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        placeholder = { Text("补充细节或子任务...") },
                        minLines = 3,
                        modifier = Modifier.fillMaxWidth(),
                        shape = ItemShape,
                    )
                }
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background.copy(alpha = 0.94f))
                .navigationBarsPadding()
                .padding(ContainerPadding)
        ) {
            Button(
                onClick = {
                    viewModel.saveTask(
                        id = task?.id,
                        draft = TaskDraft(
                            title = title,
                            notes = notes,
                            date = date,
                            startTime = startTime,
                            endTime = endTime,
                            category = category,
                            priority = priority,
                            isHabit = isHabit,
                            repeatRule = repeatRule,
                        )
                    )
                },
                enabled = title.isNotBlank(),
                shape = SoftShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
            ) {
                Icon(Icons.Rounded.Check, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("保存任务", style = MaterialTheme.typography.headlineMedium)
            }
        }
    }

    if (newCategoryOpen) {
        CategoryDialog(
            onDismiss = { newCategoryOpen = false },
            onSave = {
                category = it
                newCategoryOpen = false
            },
        )
    }
    if (deleteConfirmOpen && task != null) {
        AlertDialog(
            onDismissRequest = { deleteConfirmOpen = false },
            title = { Text("删除任务") },
            text = { Text("确定删除“${task.title}”吗？此操作不可撤销。") },
            confirmButton = {
                TextButton(onClick = { viewModel.deleteTask(task) }) {
                    Text("删除", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { deleteConfirmOpen = false }) { Text("取消") }
            },
        )
    }
    if (datePickerOpen) {
        TaskDatePickerDialog(
            value = date,
            onDismiss = { datePickerOpen = false },
            onConfirm = {
                date = it
                datePickerOpen = false
            },
        )
    }
    if (startTimePickerOpen) {
        TaskTimePickerDialog(
            title = "开始时间",
            value = startTime,
            onDismiss = { startTimePickerOpen = false },
            onConfirm = {
                startTime = it
                startTimePickerOpen = false
            },
        )
    }
    if (endTimePickerOpen) {
        TaskTimePickerDialog(
            title = "结束时间",
            value = endTime,
            onDismiss = { endTimePickerOpen = false },
            onConfirm = {
                endTime = it
                endTimePickerOpen = false
            },
        )
    }
}

@Composable
private fun ReadOnlyPickerField(
    value: String,
    label: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Box(modifier) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            singleLine = true,
            trailingIcon = { Icon(icon, contentDescription = null, modifier = Modifier.size(18.dp)) },
            modifier = Modifier.fillMaxWidth(),
            shape = ItemShape,
        )
        Box(
            Modifier
                .matchParentSize()
                .clip(ItemShape)
                .clickable(onClick = onClick)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskDatePickerDialog(
    value: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val pickerState = rememberDatePickerState(initialSelectedDateMillis = parseDraftDate(value).toPickerMillis())
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val selected = pickerState.selectedDateMillis
                    if (selected != null) onConfirm(selected.toLocalDate().toString()) else onDismiss()
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    ) {
        DatePicker(state = pickerState, showModeToggle = false)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TaskTimePickerDialog(
    title: String,
    value: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    val initialTime = parseDraftTime(value)
    val pickerState = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true,
    )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                TimePicker(state = pickerState)
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(formatTime(pickerState.hour, pickerState.minute)) }) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

@Composable
private fun CategoryDialog(onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var value by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建分类") },
        text = {
            OutlinedTextField(
                value = value,
                onValueChange = { value = it },
                label = { Text("分类名称") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
        },
        confirmButton = {
            TextButton(enabled = value.isNotBlank(), onClick = { onSave(value.trim()) }) { Text("保存") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun TimerScreen(
    viewModel: ClarityViewModel,
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
) {
    var totalMinutes by remember { mutableIntStateOf(25) }
    val totalSeconds = totalMinutes * 60
    var timeLeft by remember { mutableIntStateOf(totalSeconds) }
    var playing by remember { mutableStateOf(false) }
    var noiseOn by remember { mutableStateOf(false) }
    var taskMenuOpen by remember { mutableStateOf(false) }
    var customDurationOpen by remember { mutableStateOf(false) }
    var customMinutesText by remember { mutableStateOf(totalMinutes.toString()) }
    val noisePlayer = remember { BrownNoisePlayer() }
    val today = LocalDate.now()
    val candidates = taskInstancesForDate(tasks, completions, today).filter { !it.completed }
    val selectedTask = tasks.firstOrNull { it.id == viewModel.selectedFocusTaskId.value } ?: candidates.firstOrNull()?.task
    val setDuration: (Int) -> Unit = { minutes ->
        val safeMinutes = minutes.coerceIn(1, 240)
        totalMinutes = safeMinutes
        timeLeft = safeMinutes * 60
        playing = false
    }

    DisposableEffect(Unit) {
        onDispose { noisePlayer.stop() }
    }

    LaunchedEffect(playing, timeLeft) {
        if (playing && timeLeft > 0) {
            delay(1_000)
            timeLeft -= 1
        }
        if (playing && timeLeft == 0) {
            playing = false
            viewModel.addFocusSession(selectedTask, totalSeconds.toLong())
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                        .clickable { taskMenuOpen = true }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Rounded.FormatListBulleted, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(6.dp))
                    Text("正在专注", style = MaterialTheme.typography.labelSmall)
                }
                Spacer(Modifier.height(12.dp))
                Text(selectedTask?.title ?: "自由专注", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
                Text("$totalMinutes 分钟番茄钟", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            DropdownMenu(expanded = taskMenuOpen, onDismissRequest = { taskMenuOpen = false }) {
                DropdownMenuItem(text = { Text("自由专注") }, onClick = {
                    viewModel.setFocusTask(null)
                    taskMenuOpen = false
                })
                candidates.forEach { instance ->
                    DropdownMenuItem(text = { Text(instance.task.title) }, onClick = {
                        viewModel.setFocusTask(instance.task.id)
                        taskMenuOpen = false
                    })
                }
            }
        }
        Spacer(Modifier.height(24.dp))
        DurationSelector(
            totalMinutes = totalMinutes,
            onSelect = setDuration,
            onCustom = {
                customMinutesText = totalMinutes.toString()
                customDurationOpen = true
            },
        )
        Spacer(Modifier.height(32.dp))
        TimerDial(timeLeft = timeLeft, totalSeconds = totalSeconds)
        Spacer(Modifier.height(42.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(StackGap), verticalAlignment = Alignment.CenterVertically) {
            TimerButton(icon = Icons.Rounded.Stop, size = 56.dp, selected = false, contentDescription = "结束") {
                if (timeLeft < totalSeconds) viewModel.addFocusSession(selectedTask, (totalSeconds - timeLeft).toLong())
                playing = false
                timeLeft = totalSeconds
            }
            TimerButton(icon = if (playing) Icons.Rounded.Pause else Icons.Rounded.PlayArrow, size = 80.dp, selected = true, contentDescription = "开始或暂停") {
                playing = !playing
            }
            TimerButton(icon = Icons.Rounded.MusicNote, size = 56.dp, selected = noiseOn, contentDescription = "棕噪音") {
                noiseOn = !noiseOn
                if (noiseOn) noisePlayer.start() else noisePlayer.stop()
            }
        }
    }

    if (customDurationOpen) {
        CustomDurationDialog(
            value = customMinutesText,
            onValueChange = { customMinutesText = it },
            onDismiss = { customDurationOpen = false },
            onSave = {
                setDuration(it)
                customDurationOpen = false
            },
        )
    }
}

@Composable
private fun DurationSelector(
    totalMinutes: Int,
    onSelect: (Int) -> Unit,
    onCustom: () -> Unit,
) {
    val presets = listOf(15, 25, 45, 60)
    ChipRow {
        presets.forEach { minutes ->
            FilterChip(
                selected = totalMinutes == minutes,
                onClick = { onSelect(minutes) },
                label = { Text("${minutes} 分钟") },
            )
        }
        FilterChip(
            selected = totalMinutes !in presets,
            onClick = onCustom,
            label = { Text("自定义") },
        )
    }
}

@Composable
private fun CustomDurationDialog(
    value: String,
    onValueChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit,
) {
    val minutes = value.toIntOrNull()
    val valid = minutes != null && minutes in 1..240
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("自定义专注时长") },
        text = {
            Column {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    label = { Text("分钟") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                Text("请输入 1 到 240 分钟。", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        },
        confirmButton = {
            TextButton(enabled = valid, onClick = { onSave(minutes ?: 25) }) {
                Text("保存")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("取消") }
        },
    )
}

@Composable
private fun TimerDial(timeLeft: Int, totalSeconds: Int) {
    val progress = (totalSeconds - timeLeft).toFloat() / totalSeconds
    val minutes = (timeLeft / 60).toString().padStart(2, '0')
    val seconds = (timeLeft % 60).toString().padStart(2, '0')
    Box(Modifier.size(270.dp), contentAlignment = Alignment.Center) {
        Box(
            Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.12f), CircleShape)
        )
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 10.dp.toPx()
            drawCircle(
                color = Color(0xFFE8E8E6),
                radius = size.minDimension / 2 - stroke,
                center = Offset(size.width / 2, size.height / 2),
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
            drawArc(
                color = ClarityPrimary,
                startAngle = -90f,
                sweepAngle = 360f * progress,
                useCenter = false,
                topLeft = Offset(stroke, stroke),
                size = androidx.compose.ui.geometry.Size(size.width - stroke * 2, size.height - stroke * 2),
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Box(
            Modifier
                .size(232.dp)
                .background(MaterialTheme.colorScheme.surface, CircleShape)
                .shadow(8.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$minutes:$seconds", fontSize = 58.sp, fontWeight = FontWeight.Light, color = MaterialTheme.colorScheme.primary)
        }
    }
}

@Composable
private fun TimerButton(
    icon: ImageVector,
    size: androidx.compose.ui.unit.Dp,
    selected: Boolean,
    contentDescription: String,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .size(size)
            .clip(ItemShape)
            .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
            .shadow(4.dp, ItemShape)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            icon,
            contentDescription = contentDescription,
            tint = if (selected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(if (size > 60.dp) 40.dp else 24.dp),
        )
    }
}

@Composable
private fun StatsScreen(
    tasks: List<TaskEntity>,
    completions: List<TaskCompletionEntity>,
    focusSessions: List<FocusSessionEntity>,
) {
    val stats = calculateDashboardStats(tasks, completions, focusSessions, LocalDate.now())
    Column(verticalArrangement = Arrangement.spacedBy(StackGap)) {
        PageCard {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Column {
                    FieldLabel("专注时长")
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            ((stats.focusSeconds / 3600.0) * 10).roundToInt().div(10.0).toString(),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 46.sp,
                        )
                        Spacer(Modifier.width(6.dp))
                        Text("小时", color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 8.dp))
                    }
                    Row(
                        Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.35f))
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Rounded.TrendingUp, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("本周 ${if (stats.focusTrendPercent >= 0) "+" else ""}${stats.focusTrendPercent}%", style = MaterialTheme.typography.labelSmall)
                    }
                }
                Icon(Icons.Rounded.Timer, contentDescription = null, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f), modifier = Modifier.size(96.dp))
            }
            Spacer(Modifier.height(14.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                QuietMetric("本周专注", stats.weeklyFocusSessions.toString(), "次", Modifier.weight(1f))
                QuietMetric("平均时长", stats.averageFocusMinutes.toString(), "分钟", Modifier.weight(1f))
            }
        }
        PageCard {
            FieldLabel("任务概览")
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                CompletionRing(stats.completionRate)
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuietMetric("今日完成", "${stats.todayCompleted}/${stats.todayTotal}", "项")
                    QuietMetric("累计完成", stats.tasksDone.toString(), "项")
                    QuietMetric("连续天数", stats.currentStreakDays.toString(), "天")
                }
            }
            Spacer(Modifier.height(16.dp))
            WeekCompletionTrend(stats.weeklyCompletion)
        }
        PageCard {
            FieldLabel("习惯养成")
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                QuietMetric("今日打卡", "${stats.todayHabitsDone}/${stats.todayHabitsTotal}", "项", Modifier.weight(1f))
                QuietMetric("30 天完成率", stats.habitCompletionRate30Days.toString(), "%", Modifier.weight(1f))
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(18.dp), modifier = Modifier.fillMaxWidth()) {
                QuietMetric("当前连续", stats.currentHabitStreak.toString(), "次", Modifier.weight(1f))
                QuietMetric("最佳连续", stats.bestHabitStreak.toString(), "次", Modifier.weight(1f))
            }
            Spacer(Modifier.height(16.dp))
            if (stats.habitLeaders.isEmpty()) {
                Text("开启习惯打卡后会显示养成趋势。", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    stats.habitLeaders.forEach { progress ->
                        HabitProgressRow(progress)
                    }
                }
            }
        }
        PageCard {
            FieldLabel("分类分布")
            Spacer(Modifier.height(12.dp))
            if (stats.categoryDistribution.isEmpty()) {
                Text("完成任务后会显示分类占比。", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    stats.categoryDistribution.forEachIndexed { index, share ->
                        CategoryBar(share = share, color = categoryColors(index))
                    }
                }
            }
        }
    }
}

@Composable
private fun QuietMetric(title: String, value: String, unit: String, modifier: Modifier = Modifier) {
    Column(modifier) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
            if (unit.isNotBlank()) {
                Spacer(Modifier.width(4.dp))
                Text(unit, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(bottom = 3.dp))
            }
        }
    }
}

@Composable
private fun WeekCompletionTrend(buckets: List<CompletionBucket>) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("本周趋势", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            buckets.forEach { bucket ->
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                ) {
                    Box(
                        modifier = Modifier
                            .height(44.dp)
                            .fillMaxWidth(),
                        contentAlignment = Alignment.BottomCenter,
                    ) {
                        Box(
                            Modifier
                                .fillMaxWidth(0.38f)
                                .height((4 + bucket.percentage * 0.4f).dp)
                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.72f), CircleShape)
                        )
                    }
                    Text(weekdayLabel(bucket.date), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                }
            }
        }
    }
}

@Composable
private fun HabitProgressRow(progress: HabitProgress) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(progress.title, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
            Spacer(Modifier.width(12.dp))
            Text(
                "连续 ${progress.currentStreak}${progress.streakUnit} · ${progress.completionRate30Days}%",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(6.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow, CircleShape)
        ) {
            Box(
                Modifier
                    .fillMaxWidth((progress.completionRate30Days / 100f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(MaterialTheme.colorScheme.tertiary.copy(alpha = 0.65f), CircleShape)
            )
        }
    }
}

@Composable
private fun CompletionRing(percent: Int) {
    Box(Modifier.size(112.dp), contentAlignment = Alignment.Center) {
        Canvas(Modifier.fillMaxSize()) {
            val stroke = 12.dp.toPx()
            drawCircle(Color(0xFFF3F4F1), style = Stroke(stroke), radius = size.minDimension / 2 - stroke)
            drawArc(
                color = ClarityPrimaryContainer,
                startAngle = -90f,
                sweepAngle = 360f * percent / 100f,
                useCenter = false,
                topLeft = Offset(stroke, stroke),
                size = androidx.compose.ui.geometry.Size(size.width - stroke * 2, size.height - stroke * 2),
                style = Stroke(stroke, cap = StrokeCap.Round)
            )
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("$percent%", style = MaterialTheme.typography.headlineMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
            Text("完成", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
        }
    }
}

@Composable
private fun SmallMetricCard(title: String, value: String, unit: String) {
    PageCard(contentPadding = 16.dp) {
        FieldLabel(title)
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
            Spacer(Modifier.width(4.dp))
            Text(unit, color = MaterialTheme.colorScheme.outline, modifier = Modifier.padding(bottom = 5.dp))
        }
    }
}

@Composable
private fun CategoryBar(share: CategoryShare, color: Color) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(8.dp).background(color, CircleShape))
                Spacer(Modifier.width(8.dp))
                Text(share.name)
            }
            Text("${share.percentage}%", color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Medium)
        }
        Box(
            Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(MaterialTheme.colorScheme.surfaceContainerLow, CircleShape)
        ) {
            Box(
                Modifier
                    .fillMaxWidth((share.percentage / 100f).coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .background(color, CircleShape)
            )
        }
    }
}

@Composable
private fun SettingsScreen(viewModel: ClarityViewModel) {
    var instructionsOpen by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(28.dp)) {
        Column {
            Text("偏好", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
            Text("设置", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.SemiBold)
        }
        PageCard {
            FieldLabel("外观")
            SettingsRow(
                icon = Icons.Rounded.Palette,
                title = "主题色",
                subtitle = "自定义主要强调色",
                trailing = {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("#3E6658" to Color(0xFF3E6658), "#42636F" to Color(0xFF42636F), "#80524E" to Color(0xFF80524E)).forEach { (hex, color) ->
                            Box(
                                Modifier
                                    .size(26.dp)
                                    .background(color, CircleShape)
                                    .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
                                    .clickable { viewModel.setPrimaryColor(hex) }
                            )
                        }
                    }
                }
            )
        }
        PageCard {
            FieldLabel("帮助与关于")
            SettingsRow(
                icon = Icons.Rounded.HelpOutline,
                title = "使用说明",
                subtitle = "了解如何高效使用 Clarity",
                onClick = { instructionsOpen = true },
            )
        }
    }
    if (instructionsOpen) {
        AlertDialog(
            onDismissRequest = { instructionsOpen = false },
            title = { Text("使用说明") },
            text = {
                Text("在日历页查看每天的任务安排；在任务页通过分类、搜索、优先级和状态筛选任务；新建任务时可设置习惯或重复规则；专注页可围绕任务开启 25 分钟番茄钟，完成后统计页会自动更新。")
            },
            confirmButton = { TextButton(onClick = { instructionsOpen = false }) { Text("知道了") } },
        )
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    trailing: @Composable (() -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(ItemShape)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .size(40.dp)
                .background(MaterialTheme.colorScheme.secondaryContainer, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSecondaryContainer)
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Medium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (trailing != null) trailing() else Icon(Icons.Rounded.KeyboardArrowRight, contentDescription = null, tint = MaterialTheme.colorScheme.outline)
    }
}

@Composable
private fun TaskRow(
    instance: TaskInstance,
    showCategory: Boolean,
    showDate: Boolean,
    onToggle: () -> Unit,
    onClick: () -> Unit,
) {
    val task = instance.task
    val habitAccent = if (task.isHabit) MaterialTheme.colorScheme.tertiary.copy(alpha = 0.55f) else Color.Transparent
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, ItemShape, clip = false)
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, ItemShape)
            .clip(ItemShape)
            .clickable(onClick = onClick)
            .padding(StackGap),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            Modifier
                .width(3.dp)
                .height(42.dp)
                .background(habitAccent, CircleShape)
        )
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(5.dp))
                .border(1.5.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(5.dp))
                .background(if (instance.completed) MaterialTheme.colorScheme.primary else Color.Transparent)
                .clickable(onClick = onToggle),
            contentAlignment = Alignment.Center,
        ) {
            if (instance.completed) Icon(Icons.Rounded.Done, contentDescription = "已完成", tint = MaterialTheme.colorScheme.onPrimary, modifier = Modifier.size(16.dp))
        }
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f)) {
            Text(
                task.title,
                style = MaterialTheme.typography.bodyLarge,
                color = if (instance.completed) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface,
                textDecoration = if (instance.completed) TextDecoration.LineThrough else TextDecoration.None,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (showCategory) {
                    Text(task.category, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (showDate) {
                    if (showCategory) Text(" · ", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(instance.date.format(DateFormatter), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                if (task.startTime.isNotBlank()) {
                    Text(" · ${task.startTime}-${task.endTime}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
        Spacer(Modifier.width(8.dp))
        PriorityBadge(Priority.fromStored(task.priority))
    }
}

@Composable
private fun PriorityBadge(priority: Priority) {
    val (border, text, bg) = when (priority) {
        Priority.HIGH -> Triple(MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.error, MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.35f))
        Priority.MEDIUM -> Triple(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
        Priority.LOW -> Triple(MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.outline, MaterialTheme.colorScheme.surfaceContainer)
    }
    Text(
        priority.label,
        modifier = Modifier
            .border(1.dp, border, RoundedCornerShape(6.dp))
            .background(bg, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        color = text,
        style = MaterialTheme.typography.labelSmall,
    )
}

@Composable
private fun SelectBox(
    selected: Boolean,
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .clip(ItemShape)
            .border(1.dp, if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.outlineVariant, ItemShape)
            .background(if (selected) MaterialTheme.colorScheme.primaryContainer else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.outline)
        Text(title, color = if (selected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface)
    }
}

@Composable
private fun PageCard(
    modifier: Modifier = Modifier,
    contentPadding: androidx.compose.ui.unit.Dp = ContainerPadding,
    content: @Composable ColumnScope.() -> Unit,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = SoftShape,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLowest),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
    ) {
        Column(Modifier.padding(contentPadding), content = content)
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelSmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontWeight = FontWeight.Medium,
    )
    Spacer(Modifier.height(8.dp))
}

@Composable
private fun ChipRow(content: @Composable RowScope.() -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun EmptyState(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surfaceContainerLowest, SoftShape)
            .border(1.dp, MaterialTheme.colorScheme.surfaceContainer, SoftShape)
            .padding(28.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
    }
}

private data class NavItem(val tab: AppTab, val icon: ImageVector)

private fun navItems() = listOf(
    NavItem(AppTab.CALENDAR, Icons.Rounded.CalendarMonth),
    NavItem(AppTab.TASKS, Icons.Rounded.FormatListBulleted),
    NavItem(AppTab.STATS, Icons.Rounded.BarChart),
    NavItem(AppTab.TIMER, Icons.Rounded.Timer),
    NavItem(AppTab.SETTINGS, Icons.Rounded.Settings),
)

private fun categories(tasks: List<TaskEntity>): List<String> {
    return listOf("全部任务") + tasks.map { it.category }.distinct().sorted()
}

private fun categoryColors(index: Int): Color {
    return listOf(ClarityPrimary, ClarityPrimaryContainer, ClaritySecondaryContainer, ClarityTertiaryContainer)[index % 4]
}

private fun parseDraftDate(value: String): LocalDate {
    return runCatching { LocalDate.parse(value) }.getOrDefault(LocalDate.now())
}

private fun parseDraftTime(value: String): LocalTime {
    return runCatching { LocalTime.parse(value) }.getOrDefault(LocalTime.NOON)
}

private fun LocalDate.toPickerMillis(): Long {
    return atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
}

private fun Long.toLocalDate(): LocalDate {
    return Instant.ofEpochMilli(this).atZone(ZoneId.systemDefault()).toLocalDate()
}

private fun formatTime(hour: Int, minute: Int): String {
    return "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}"
}

private fun weekdayLabel(date: LocalDate): String {
    return when (date.dayOfWeek.value) {
        1 -> "一"
        2 -> "二"
        3 -> "三"
        4 -> "四"
        5 -> "五"
        6 -> "六"
        else -> "日"
    }
}
