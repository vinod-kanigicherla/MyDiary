package com.example.personaldiaryapptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    ListView diaryListView;
    SharedPreferences sharedPreferences;
    String diaryDate;
    Spinner sortSpinner;
    Spinner categoriesSpinner;

    static ArrayList<String> previousTitles;
    static ArrayList<String> previousDates;
    static List<Map<String,String>> diaryList;
    static Map<String, String> diaryEntry = new HashMap<>();
    static SimpleAdapter simpleAdapter;
    static ArrayAdapter<String> categoriesArrayAdapter;
    static ArrayList<String> contents = new ArrayList<>();

    static ArrayList<String> categoryNames;

    static List<Map<String,String>> dailyEventsHMList;
    static ArrayList<String> dailyEventsContentsList;

    static List<Map<String,String>> majorEventsHMList;
    static ArrayList<String> majorEventsContentsList;

    static List<Map<String,String>> goalsHMList;
    static ArrayList<String> goalsContentsList;

    static List<Map<String,String>> hobbiesHMList;
    static ArrayList<String> hobbiesContentsList;




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        if (item.getItemId() == R.id.addtemplate) {
            Intent intent = new Intent(getApplicationContext(), TemplateEditActivity.class);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.clearalltemplates) {
            new AlertDialog.Builder(MainActivity.this)
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setTitle("Clear All Templates")
                    .setMessage("Are you sure you want to clear all templates?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            TemplateEditActivity.templateTitles.clear();
                            TemplateEditActivity.templateContents.clear();

                            try {
                                sharedPreferences.edit().putString("templateContents", ObjectSerializer.serialize(TemplateEditActivity.templateContents)).apply();
                                sharedPreferences.edit().putString("templateTitles", ObjectSerializer.serialize(TemplateEditActivity.templateTitles)).apply();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).toString().equals("all")) {
            diaryListView.setAdapter(simpleAdapter);
            simpleAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        previousTitles = new ArrayList<>();
        previousDates = new ArrayList<>();

        sortSpinner = (Spinner) findViewById(R.id.sortSpinner);
        ArrayAdapter<CharSequence> sortArrayAdapter = ArrayAdapter.createFromResource(this, R.array.sortOptions, android.R.layout.simple_spinner_item);

        sortSpinner.setAdapter(sortArrayAdapter);
        sortSpinner.setOnItemSelectedListener(this);

        sharedPreferences = this.getSharedPreferences("com.example.personaldiaryapptest", Context.MODE_PRIVATE);

        Calendar calendar = Calendar.getInstance();
        diaryDate = DateFormat.getDateInstance().format(calendar.getTime());

        diaryListView = findViewById(R.id.diaryListView);
        diaryList = new ArrayList<>();

        simpleAdapter = new SimpleAdapter(this, diaryList, android.R.layout.simple_list_item_2, new String[] {"title","date"}, new int[] {android.R.id.text1, android.R.id.text2});
        diaryListView.setAdapter(simpleAdapter);

        categoryNames = new ArrayList<>();

        categoryNames.add("Daily Events");
        categoryNames.add("Major Events");
        categoryNames.add("Goals");
        categoryNames.add("Hobbies");

        dailyEventsHMList = new ArrayList<>();
        dailyEventsContentsList = new ArrayList<>();

        majorEventsHMList = new ArrayList<>();
        majorEventsContentsList = new ArrayList<>();

        goalsHMList = new ArrayList<>();
        goalsContentsList = new ArrayList<>();

        hobbiesHMList = new ArrayList<>();
        hobbiesContentsList = new ArrayList<>();

        ArrayList <String> dailyEventsTitles = new ArrayList<>();
        ArrayList<String> dailyEventsDates = new ArrayList<>();

        try {
            dailyEventsTitles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("dailyeventstitles", ObjectSerializer.serialize(new ArrayList<String>())));
            dailyEventsDates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("dailyeventsdates", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0; i < dailyEventsTitles.size(); i++) {
            Map<String, String> diaryMap = new HashMap<>();
            diaryMap.put("title", dailyEventsTitles.get(i));
            diaryMap.put("date", dailyEventsDates.get(i));
            dailyEventsHMList.add(diaryMap);
        }


        ArrayList <String> majorEventsTitles = new ArrayList<>();
        ArrayList<String> majorEventsDates = new ArrayList<>();

        try {
            majorEventsTitles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("majoreventstitles", ObjectSerializer.serialize(new ArrayList<String>())));
            majorEventsDates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("majoreventsdates", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0; i < majorEventsTitles.size(); i++) {
            Map<String, String> diaryMap = new HashMap<>();
            diaryMap.put("title", majorEventsTitles.get(i));
            diaryMap.put("date", majorEventsDates.get(i));
            majorEventsHMList.add(diaryMap);
        }

        ArrayList<String> goalsTitles = new ArrayList<>();
        ArrayList<String> goalsDates = new ArrayList<>();

        try {
            goalsTitles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("goalstitles", ObjectSerializer.serialize(new ArrayList<String>())));
            goalsDates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("goalsdates", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0; i < goalsTitles.size(); i++) {
            Map<String, String> diaryMap = new HashMap<>();
            diaryMap.put("title", goalsTitles.get(i));
            diaryMap.put("date", goalsDates.get(i));
            goalsHMList.add(diaryMap);
        }

        ArrayList<String> hobbiesTitles = new ArrayList<>();
        ArrayList<String> hobbiesDates = new ArrayList<>();

        try {
            hobbiesTitles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("hobbiestitles", ObjectSerializer.serialize(new ArrayList<String>())));
            hobbiesDates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("hobbiesdates", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0; i < hobbiesTitles.size(); i++) {
            Map<String, String> diaryMap = new HashMap<>();
            diaryMap.put("title", hobbiesTitles.get(i));
            diaryMap.put("date", hobbiesDates.get(i));
            hobbiesHMList.add(diaryMap);
        }

        ArrayList <String> titles = new ArrayList<>();
        ArrayList<String> dates = new ArrayList<>();

        try {
            titles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("titles", ObjectSerializer.serialize(new ArrayList<String>())));
            dates = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("dates", ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (int i=0; i < titles.size(); i++) {
            Map<String, String> diaryMap = new HashMap<>();
            diaryMap.put("title", titles.get(i));
            diaryMap.put("date", dates.get(i));
            diaryList.add(diaryMap);
        }

        Log.i("diaryList", diaryList.toString());

        ArrayList<String> previousContents = new ArrayList<>();

        try {
            previousContents = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("contents", ObjectSerializer.serialize(new ArrayList<String>())));
            if (!previousContents.isEmpty()) {
                contents = previousContents;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<String> previousTemplateTitles = new ArrayList<>();
        ArrayList<String> previousTemplateContents = new ArrayList<>();

        try {
            previousTemplateTitles = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("templateTitles", ObjectSerializer.serialize(new ArrayList<>())));
            previousTemplateContents = (ArrayList<String>) ObjectSerializer.deserialize(sharedPreferences.getString("templateContents", ObjectSerializer.serialize(new ArrayList<>())));

            if (!previousTemplateTitles.isEmpty() && !previousTemplateContents.isEmpty()) {
                TemplateEditActivity.templateTitles = previousTemplateTitles;
                TemplateEditActivity.templateContents = previousTemplateContents;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("templateTitles", TemplateEditActivity.templateTitles.toString());
        Log.i("templateContents", TemplateEditActivity.templateContents.toString());

        int lastDatePosition = diaryList.size()-1;

        if (diaryList.isEmpty()) {
            diaryEntry.put("title", "New Title");
            diaryEntry.put("date", diaryDate);
            contents.add(0, "");
            diaryList.add(diaryEntry);
        } else if (!diaryDate.equals(diaryList.get(0).get("date"))) {
            diaryEntry.put("title", "New Title");
            diaryEntry.put("date", diaryDate);
            diaryList.add(0, diaryEntry);
            contents.add(0, "");
        }


        for (Map<String, String> previousDiaryMap : MainActivity.diaryList) {
            previousTitles.add(previousDiaryMap.get("title"));
            previousDates.add(previousDiaryMap.get("date"));
        }

        try {
            sharedPreferences.edit().putString("titles", ObjectSerializer.serialize(previousTitles)).apply();
            sharedPreferences.edit().putString("dates", ObjectSerializer.serialize(previousDates)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sharedPreferences.edit().putString("contents", ObjectSerializer.serialize(MainActivity.contents)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            sharedPreferences.edit().putString("contents", ObjectSerializer.serialize(MainActivity.contents)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("diarydate", diaryDate);

        diaryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("position", String.valueOf(position));
                Intent intent = new Intent(getApplicationContext(), PageEditActivity.class);
                intent.putExtra("notePosition", position);
                startActivity(intent);
            }
        });
    }
}