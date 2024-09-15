package com.example.diaryapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.diaryapp.model.Diary
import com.example.diaryapp.ui.theme.DiaryAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class MainActivity : ComponentActivity() {
    private var diaryList = mutableStateListOf<Diary>() // State to hold diaries

    // Request codes to identify the result
    private val CREATE_DIARY_REQUEST_CODE = 1
    private val EDIT_DIARY_REQUEST_CODE = 2
    private val DELETE_DIARY_REQUEST_CODE = 3

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Load diaries from SharedPreferences when the app starts
        diaryList.addAll(getDiaries(this))

        setContent {
            DiaryAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    floatingActionButton = {
                        FloatingActionButton(onClick = {
                            val intent = Intent(this@MainActivity, CreateDiaryActivity::class.java)
                            startActivityForResult(intent, CREATE_DIARY_REQUEST_CODE) // Start CreateDiaryActivity for result
                        }) {
                            Icon(Icons.Default.Add, contentDescription = "Add Diary Entry")
                        }
                    }
                ) { innerPadding ->
                    DiaryListScreen(
                        modifier = Modifier.padding(innerPadding),
                        diaryList = diaryList,
                        onDiaryClick = { diary ->
                            // Existing click handler for editing the diary
                            val intent = Intent(this@MainActivity, EditDiaryActivity::class.java)
                            intent.putExtra("diary", Gson().toJson(diary)) // Serialize diary object
                            startActivity(intent)
                        },
                        onDiaryLongClick = { diary ->
                            // Handle long-click (delete diary)
                            val diaryJson = Gson().toJson(diary)
                            val intent = Intent().apply {
                                putExtra("diary", diaryJson)
                            }
                            startActivityForResult(intent, DELETE_DIARY_REQUEST_CODE)
                        }
                    )

                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && data != null) {
            val diaryJson = data.getStringExtra("diary")
            val updatedDiary = Gson().fromJson(diaryJson, Diary::class.java)

            when (requestCode) {
                CREATE_DIARY_REQUEST_CODE -> {
                    // Add the new diary to the list
                    diaryList.add(updatedDiary)
                }
                EDIT_DIARY_REQUEST_CODE -> {
                    // Update the existing diary in the list
                    val diaryIndex = diaryList.indexOfFirst { it.date == updatedDiary.date }
                    if (diaryIndex != -1) {
                        diaryList[diaryIndex] = updatedDiary
                    }
                }
                DELETE_DIARY_REQUEST_CODE -> {
                    // Remove the diary from the list
                    val diaryIndex = diaryList.indexOfFirst { it.date == updatedDiary.date }
                    if (diaryIndex != -1) {
                        diaryList.removeAt(diaryIndex)
                    }
                }

            }

            // Save the updated list back to SharedPreferences
            saveDiaries(this, diaryList)
        }
    }

    private fun getDiaries(context: Context): MutableList<Diary> {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("DiaryApp", Context.MODE_PRIVATE)
        val gson = Gson()
        val diaryListType = object : TypeToken<MutableList<Diary>>() {}.type
        return gson.fromJson(sharedPreferences.getString("diaryList", null), diaryListType) ?: mutableListOf()
    }

    private fun saveDiaries(context: Context, diaries: MutableList<Diary>) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("DiaryApp", Context.MODE_PRIVATE)
        val gson = Gson()
        val editor = sharedPreferences.edit()
        val jsonString = gson.toJson(diaries)
        editor.putString("diaryList", jsonString)
        editor.apply()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DiaryItem(diary: Diary, onClick: () -> Unit, onLongClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { onLongClick() }  // Detect long-click event
            )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = diary.title, style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = diary.content, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = diary.date.toString(), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun DiaryListScreen(
    modifier: Modifier = Modifier,
    diaryList: List<Diary>,
    onDiaryClick: (Diary) -> Unit,
    onDiaryLongClick: (Diary) -> Unit // Add the long click handler
) {
    Column(modifier = modifier.fillMaxSize().padding(16.dp)) {
        if (diaryList.isEmpty()) {
            Text(text = "No diaries yet!", style = MaterialTheme.typography.titleMedium)
        } else {
            diaryList.forEach { diary ->
                DiaryItem(
                    diary = diary,
                    onClick = { onDiaryClick(diary) },
                    onLongClick = { onDiaryLongClick(diary) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}