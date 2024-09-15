package com.example.diaryapp.tests

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import com.example.diaryapp.model.Diary
import com.google.gson.reflect.TypeToken
import java.util.Date

class DiaryManagerTest {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var context: Context

    // Redefine the saveDiaries function for testing purposes
    private fun saveDiaries(sharedPreferences: SharedPreferences, diaries: List<Diary>) {
        val editor = sharedPreferences.edit()
        val jsonString = Gson().toJson(diaries)
        editor.putString("diaryList", jsonString)
        editor.apply()
    }

    // Redefine the getDiaries function for testing purposes
    private fun getDiaries(sharedPreferences: SharedPreferences): List<Diary> {
        val gson = Gson()
        val diaryListType = object : TypeToken<MutableList<Diary>>() {}.type
        return gson.fromJson(sharedPreferences.getString("diaryList", null), diaryListType) ?: mutableListOf()
    }


    @Before
    fun setup() {
        // Use ApplicationProvider to get the context in unit tests
        context = ApplicationProvider.getApplicationContext<Context>()

        // Mock the SharedPreferences object (you can also use real SharedPreferences)
        sharedPreferences = Mockito.mock(SharedPreferences::class.java)


    }

    @Test
    fun testSaveAndRetrieveDiaries() {
        // Create a sample diary
        val diary = Diary(title = "Sample Diary", content = "Sample Content", date = Date())
        val diaryList = mutableListOf(diary)

        // Mock the SharedPreferences editor
        val editor = Mockito.mock(SharedPreferences.Editor::class.java)
        Mockito.`when`(sharedPreferences.edit()).thenReturn(editor)

        // Mock the putString and apply methods of the editor
        Mockito.`when`(editor.putString(Mockito.anyString(), Mockito.anyString())).thenReturn(editor)
        Mockito.doNothing().`when`(editor).apply()

        // Save the diary list
        saveDiaries(sharedPreferences, diaryList)

        // Capture the JSON string that was saved
        val diaryListJson = Gson().toJson(diaryList)

        // Verify that the putString method was called with the correct key and value
        Mockito.verify(editor).putString("diaryList", diaryListJson)
        Mockito.verify(editor).apply()

        // Mock the getString method of SharedPreferences to return the diary list JSON
        Mockito.`when`(sharedPreferences.getString("diaryList", null)).thenReturn(diaryListJson)

        // Retrieve the diaries and verify they match the saved data
        val retrievedDiaries = getDiaries(sharedPreferences)
        assertEquals(diaryList, retrievedDiaries)
    }
}
