package com.example.diaryapp.model

import java.util.Date

data class Diary (
    val title: String,
    val content: String,
    val tags: List<String> = emptyList(),
    val date: Date = Date()
)