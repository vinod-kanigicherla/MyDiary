package com.example.diaryapp

// Imports for Android classes and components
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Imports for Jetpack Compose components and tools
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// Import for Gson (for JSON serialization/deserialization)
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Import for your Diary model class
import com.example.diaryapp.model.Diary

// Import for your app's theme
import com.example.diaryapp.ui.theme.DiaryAppTheme
import java.util.Date


class EditDiaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val diaryJson = intent.getStringExtra("diary")
        val diary = Gson().fromJson(diaryJson, Diary::class.java) // Deserialize the diary object

        setContent {
            DiaryAppTheme {
                EditDiaryScreen(
                    diary = diary,
                    onSaveDiary = { updatedDiary ->
                        // Update the existing diary in the data store
                        updateDiary(this, updatedDiary)

                        // Return the updated diary to MainActivity
                        val resultIntent = Intent()
                        resultIntent.putExtra("diary", Gson().toJson(updatedDiary)) // Serialize updated diary
                        setResult(RESULT_OK, resultIntent)
                        finish() // Close the activity
                    }
                )
            }
        }
    }

    private fun updateDiary(context: Context, updatedDiary: Diary) {
        val sharedPreferences: SharedPreferences = context.getSharedPreferences("DiaryApp", Context.MODE_PRIVATE)

        // Retrieve the existing list of diaries
        val gson = Gson()
        val diaryListType = object : TypeToken<MutableList<Diary>>() {}.type
        val existingDiaries: MutableList<Diary> = gson.fromJson(
            sharedPreferences.getString("diaryList", null),
            diaryListType
        ) ?: mutableListOf()

        // Find and update the diary
        val diaryIndex = existingDiaries.indexOfFirst { it.date == updatedDiary.date } // Find by unique date
        if (diaryIndex != -1) {
            existingDiaries[diaryIndex] = updatedDiary
        }

        // Save the updated list back to SharedPreferences
        val editor = sharedPreferences.edit()
        val jsonString = gson.toJson(existingDiaries)
        editor.putString("diaryList", jsonString)
        editor.apply()
    }
}

@Composable
fun EditDiaryScreen(
    diary: Diary? = null,  // For editing, we can pass an existing diary
    onSaveDiary: (Diary) -> Unit  // Callback when saving the diary
) {
    var title by remember { mutableStateOf(diary?.title ?: "") }  // Pre-populate title if editing
    var content by remember { mutableStateOf(diary?.content ?: "") }  // Pre-populate content if editing

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = content,
            onValueChange = { content = it },
            label = { Text("Content") },
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),  // Large text field for content
            maxLines = Int.MAX_VALUE
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                val newDiary = Diary(
                    title = title,
                    content = content,
                    tags = diary?.tags ?: emptyList(),  // You can add tag editing later
                    date = diary?.date ?: Date()  // Keep original date or use current date
                )
                onSaveDiary(newDiary)  // Trigger the callback
            },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text(if (diary != null) "Update Diary" else "Create Diary")
        }
    }
}

