package com.example.finalproject.data

import java.time.LocalDate
import java.time.LocalTime

fun validateTaskDraft(draft: TaskDraft): String? {
    if (draft.title.trim().isEmpty()) return "请输入任务标题"
    if (draft.category.trim().isEmpty()) return "请输入分类"
    val dateValid = runCatching { LocalDate.parse(draft.date) }.isSuccess
    if (!dateValid) return "日期格式应为 yyyy-MM-dd"

    val startText = draft.startTime.trim()
    val endText = draft.endTime.trim()
    if (startText.isEmpty() && endText.isEmpty()) return null
    if (startText.isEmpty() || endText.isEmpty()) return "请同时设置开始和结束时间"

    val start = runCatching { LocalTime.parse(startText) }.getOrNull()
    val end = runCatching { LocalTime.parse(endText) }.getOrNull()
    if (start == null || end == null) return "时间格式应为 HH:mm"
    if (!end.isAfter(start)) return "结束时间需要晚于开始时间"
    return null
}
