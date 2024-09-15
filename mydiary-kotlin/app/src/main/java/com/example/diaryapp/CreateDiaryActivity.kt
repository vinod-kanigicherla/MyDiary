package com.example.diaryapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.diaryapp.model.Diary
import com.example.diaryapp.ui.theme.DiaryAppTheme
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class CreateDiaryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            DiaryAppTheme {
                CreateDiaryScreen(onSaveDiary = { diary ->
                    saveDiary(this, diary)
                    // Return the newly created diary to MainActivity
                    val resultIntent = Intent()
                    resultIntent.putExtra("newDiary", Gson().toJson(diary))  // Pass the diary as JSON
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()  // Close the activity
                })
            }
        }
    }

    private fun saveDiary(context: Context, diary: Diary) {
        // Optionally save the diary here or rely on MainActivity to handle persistence
        Log.d("CreateDiaryActivity", "Diary saved: ${diary.title}, ${diary.content}")
    }
}


@Composable
fun CreateDiaryScreen(
    diary: Diary? = null,  // For editing, we can pass an existing diary
    onSaveDiary: (Diary) -> Unit  // Callback when saving the diary
) {
    var title by remember { mutableStateOf(diary?.title ?: "") }  // Default title
    var content by remember { mutableStateOf(diary?.content ?: "") }  // Default content

    // Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),  // Padding around the screen
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title input
        TextField(
            value = title,  // Current value of title
            onValueChange = { title = it },  // Update title state on user input
            label = { Text("Title") },  // Label for title
            modifier = Modifier.fillMaxWidth(),  // Full width TextField
            singleLine = true  // Restrict to one line
        )

        // Spacer between title and content
        Spacer(modifier = Modifier.height(8.dp))

        // Content input (Multi-line)
        TextField(
            value = content,  // Current value of content
            onValueChange = { content = it },  // Update content state on user input
            label = { Text("Content") },  // Label for content
            modifier = Modifier
                .fillMaxWidth()  // Full width TextField
                .weight(1f),  // Use remaining space for content input
            maxLines = Int.MAX_VALUE,  // Allow as many lines as needed
        )

        // Spacer between content and button
        Spacer(modifier = Modifier.height(16.dp))

        // Submit button
        Button(
            onClick = {
                // Create a new diary object and call onSaveDiary callback
                val newDiary = Diary(
                    title = title,
                    content = content,
                    tags = diary?.tags ?: emptyList(),  // Handle tags later
                    date = diary?.date ?: Date()  // Default to the current date if it's new
                )
                onSaveDiary(newDiary)  // Trigger save callback with the new diary
            },
            modifier = Modifier.align(Alignment.End)  // Align the button to the end (right)
        ) {
            // Display "Update" or "Create" depending on whether it's editing or creating a new diary
            Text(if (diary != null) "Update Diary" else "Create Diary")
        }
    }
}
