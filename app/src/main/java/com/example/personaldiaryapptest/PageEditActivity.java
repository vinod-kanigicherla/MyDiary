package com.example.personaldiaryapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.personaldiaryapptest.MainActivity.dailyEventsHMList;
import static com.example.personaldiaryapptest.MainActivity.diaryList;
import static com.example.personaldiaryapptest.MainActivity.goalsHMList;
import static com.example.personaldiaryapptest.MainActivity.hobbiesHMList;
import static com.example.personaldiaryapptest.MainActivity.majorEventsHMList;
import static com.example.personaldiaryapptest.MainActivity.previousDates;
import static com.example.personaldiaryapptest.MainActivity.previousTitles;

public class PageEditActivity extends AppCompatActivity {

    static int notePosition;

    static ArrayList<String> previousDailyEventsTitles;
    static ArrayList<String> previousDailyEventsDates;

    static ArrayList<String> previousMajorEventsTitles;
    static ArrayList<String> previousMajorEventsDates;

    static ArrayList<String> previousGoalsTitles;
    static ArrayList<String> previousGoalsDates;

    static ArrayList<String> previousHobbiesTitles;
    static ArrayList<String> previousHobbiesDates;

    EditText titleEditText;
    EditText contentEditText;
    TextView dateTextView;
    TextView categoriesTextView;
    String categories = "Categories: \n";

    SharedPreferences sharedPreferences;


    static String[] categoriesStringArray;
    static boolean[] checkedCategories;
    static List<String> categoriesList;
    static ArrayList<String> selectedCategories;
    static ArrayList<boolean[]> checkedCategoriesList;
    static boolean isDuplicate;

    public void useTemplate(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_menu_save);
        builder.setTitle("Use Template");
        builder.setMessage("\nChoose one of the saved templates below:\n");

        Spinner templateSpinner = new Spinner(this);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, TemplateEditActivity.templateTitles);
        templateSpinner.setAdapter(spinnerArrayAdapter);

        builder.setView(templateSpinner);

        builder.setPositiveButton("Use", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedItem = templateSpinner.getSelectedItem().toString();

                for (int i = 0; i < TemplateEditActivity.templateTitles.size(); i++) {
                    if (TemplateEditActivity.templateTitles.get(i) == selectedItem) {
                        titleEditText.setText(TemplateEditActivity.templateTitles.get(i));
                        contentEditText.setText(TemplateEditActivity.templateContents.get(i));
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", null);
        builder.show();
    }

    public void setCategories(View view) {
//
//        if (!checkedCategoriesList.isEmpty()) {
//            checkedCategories = checkedCategoriesList.get(notePosition);
//        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setIcon(android.R.drawable.ic_input_get);
        builder.setTitle("Set Categories");

        builder.setMultiChoiceItems(categoriesStringArray,  checkedCategories, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                checkedCategories[which] = isChecked;
                String selectedItem = categoriesList.get(which);
                Toast.makeText(PageEditActivity.this, selectedItem, Toast.LENGTH_SHORT).show();
            }
        });

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                for (int i = 0; i < checkedCategories.length; i++) {
                    boolean checked = checkedCategories[i];
                    if (checked) {
                        selectedCategories.add(categoriesList.get(i));
                    }
                }

                for (String category: selectedCategories) {
                    if (category.equals("Daily Events")) {
                        //add for loop to check if this diary page is in the daily events list
                        //if it is not in then
                        //also if it is in change the value of boolean checked based on page number

                        if (!MainActivity.dailyEventsHMList.isEmpty()) {
                            for (Map<String,String> diaryPage: MainActivity.dailyEventsHMList) {
                                if (diaryPage.equals(MainActivity.diaryList.get(notePosition))) {
                                    isDuplicate = true;
                                    return;
                                }
                            }
                        }

                        MainActivity.dailyEventsHMList.add(MainActivity.diaryList.get(notePosition));
//                        MainActivity.dailyEventsContentsList.add(MainActivity.contents.get(notePosition));

                        for (Map<String, String> previousDiaryMap : dailyEventsHMList) {
                            previousDailyEventsTitles.add(previousDiaryMap.get("title"));
                            previousDailyEventsDates.add(previousDiaryMap.get("date"));
                        }

                        try {
                            sharedPreferences.edit().putString("dailyeventstitles", ObjectSerializer.serialize(previousDailyEventsTitles)).apply();
                            sharedPreferences.edit().putString("dailyeventsdates", ObjectSerializer.serialize(previousDailyEventsDates)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!isDuplicate) {
                            checkedCategoriesList.add(checkedCategories);
                        }

                    } else if (category.equals("Major Events")) {
                        if (!MainActivity.majorEventsHMList.isEmpty()) {
                            for (Map<String,String> diaryPage: MainActivity.majorEventsHMList) {
                                if (diaryPage.equals(MainActivity.diaryList.get(notePosition))) {
                                    isDuplicate = true;
                                    return;
                                }
                            }
                        }

                        MainActivity.majorEventsHMList.add(MainActivity.diaryList.get(notePosition));
//                        MainActivity.majorEventsContentsList.add(MainActivity.contents.get(notePosition));

                        for (Map<String, String> previousDiaryMap : majorEventsHMList) {
                            previousMajorEventsTitles.add(previousDiaryMap.get("title"));
                            previousMajorEventsDates.add(previousDiaryMap.get("date"));
                        }

                        try {
                            sharedPreferences.edit().putString("majoreventstitles", ObjectSerializer.serialize(previousMajorEventsTitles)).apply();
                            sharedPreferences.edit().putString("majoreventsdates", ObjectSerializer.serialize(previousMajorEventsDates)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!isDuplicate) {
                            checkedCategoriesList.add(checkedCategories);
                        }

                    } else if (category.equals("Goals")) {
                        if (!MainActivity.goalsHMList.isEmpty()) {
                            for (Map<String,String> diaryPage: MainActivity.goalsHMList) {
                                if (diaryPage.equals(MainActivity.diaryList.get(notePosition))) {
                                    isDuplicate = true;
                                    return;
                                }
                            }
                        }

                        MainActivity.goalsHMList.add(MainActivity.diaryList.get(notePosition));
//                        MainActivity.goalsContentsList.add(MainActivity.contents.get(notePosition));

                        for (Map<String, String> previousDiaryMap : goalsHMList) {
                            previousGoalsTitles.add(previousDiaryMap.get("title"));
                            previousGoalsDates.add(previousDiaryMap.get("date"));
                        }

                        try {
                            sharedPreferences.edit().putString("goalstitles", ObjectSerializer.serialize(previousGoalsTitles)).apply();
                            sharedPreferences.edit().putString("goalsdates", ObjectSerializer.serialize(previousGoalsDates)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!isDuplicate) {
                            checkedCategoriesList.add(checkedCategories);
                        }

                    } else if (category.equals("Hobbies")) {

                        if (!MainActivity.hobbiesHMList.isEmpty()) {
                            for (Map<String,String> diaryPage: MainActivity.hobbiesHMList) {
                                if (diaryPage.equals(MainActivity.diaryList.get(notePosition))) {
                                    isDuplicate = true;
                                    return;
                                }
                            }
                        }

                        MainActivity.hobbiesHMList.add(MainActivity.diaryList.get(notePosition));
//                        MainActivity.hobbiesContentsList.add(MainActivity.contents.get(notePosition));

                        for (Map<String, String> previousDiaryMap : hobbiesHMList) {
                            previousHobbiesTitles.add(previousDiaryMap.get("title"));
                            previousDates.add(previousDiaryMap.get("date"));
                        }

                        try {
                            sharedPreferences.edit().putString("hobbiestitles", ObjectSerializer.serialize(previousHobbiesTitles)).apply();
                            sharedPreferences.edit().putString("hobbiesdates", ObjectSerializer.serialize(previousGoalsDates)).apply();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        if (!isDuplicate) {
                            checkedCategoriesList.add(checkedCategories);
                        }
                    }
                }

                for (Map<String, String> diaryPage : dailyEventsHMList) {
                    if (MainActivity.diaryList.get(notePosition).equals(diaryPage)) {
                        if (!categories.contains("Daily Events")) {
                            categories += "\nDaily Events";
                        }
                    }
                }
                for (Map<String, String> diaryPage : majorEventsHMList) {
                    if (diaryList.get(notePosition).equals(diaryPage)) {
                        if (!categories.contains("Major Events")) {
                            categories += "\nMajor Events";
                        }
                    }
                }
                for (Map<String, String> diaryPage : goalsHMList) {
                    if (diaryList.get(notePosition).equals(diaryPage)) {
                        if (!categories.contains("Goals")) {
                            categories += "\nGoals";
                        }
                    }
                }
                for (Map<String, String> diaryPage : hobbiesHMList) {
                    if (MainActivity.diaryList.get(notePosition).equals(diaryPage)) {
                        if (!categories.contains("Hobbies")) {
                            categories += "\nHobbies";
                        }
                    }
                }

                categoriesTextView.setText(categories);

                Log.i("selectedCategoriesList", selectedCategories.toString());
                Log.i("majoreventslist", MainActivity.majorEventsHMList.toString());
                Log.i("dailyeventslist", MainActivity.dailyEventsHMList.toString());
                Log.i("goaleventslist", MainActivity.goalsHMList.toString());
                Log.i("hobbieseventslist", MainActivity.hobbiesHMList.toString());
                Log.i("checkedcategorieslist", checkedCategoriesList.toString());
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                categories = "Categories: \n";
            }
        });

        builder.create();
        builder.show();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_edit);

        categoriesStringArray = MainActivity.categoryNames.toArray(new String[0]);
        checkedCategories = new boolean[categoriesStringArray.length];
        categoriesList = Arrays.asList(categoriesStringArray);
        selectedCategories = new ArrayList<>();
        checkedCategoriesList = new ArrayList<boolean[]>();
        categoriesTextView = findViewById(R.id.categoriesTextView);

        previousDailyEventsTitles = new ArrayList<>();
        previousDailyEventsDates = new ArrayList<>();

        previousGoalsTitles = new ArrayList<>();
        previousGoalsDates = new ArrayList<>();

        previousMajorEventsTitles = new ArrayList<>();
        previousMajorEventsDates = new ArrayList<>();

        previousHobbiesTitles = new ArrayList<>();
        previousHobbiesDates= new ArrayList<>();

        Log.i("categoriesstringarray", categoriesStringArray.toString());

        Intent intent = getIntent();
        notePosition = intent.getIntExtra("notePosition", -1);

        Map<String,String> currentDiaryEntry = MainActivity.diaryList.get(notePosition);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        dateTextView = findViewById(R.id.dateTextView);

        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.personaldiaryapptest", Context.MODE_PRIVATE);

        dateTextView.setText(currentDiaryEntry.get("date"));

        Log.i("dailyevents", dailyEventsHMList.toString());

        for (Map<String, String> diaryPage : dailyEventsHMList) {
            if (MainActivity.diaryList.get(notePosition).equals(diaryPage)) {
                categories += "\nDaily Events";
            }
        }
        for (Map<String, String> diaryPage : majorEventsHMList) {
            if (diaryList.get(notePosition).equals(diaryPage)) {
                categories += "\nMajor Events";
            }
        }
        for (Map<String, String> diaryPage : goalsHMList) {
            if (diaryList.get(notePosition).equals(diaryPage)) {
                categories += "\nGoals";
            }
        }
        for (Map<String, String> diaryPage : hobbiesHMList) {
            if (MainActivity.diaryList.get(notePosition).equals(diaryPage)) {
                categories += "\nHobbies";
            }
        }

        categoriesTextView.setText(categories);

        if (notePosition != -1) {
            titleEditText.setText(MainActivity.diaryList.get(notePosition).get("title"));
            if (!MainActivity.contents.isEmpty()) {
                contentEditText.setText(MainActivity.contents.get(notePosition));
            }
        }

        titleEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    currentDiaryEntry.replace("title", String.valueOf(s));
                }

                MainActivity.diaryList.set(notePosition, currentDiaryEntry);
                MainActivity.simpleAdapter.notifyDataSetChanged();

                previousTitles.clear();
                previousDates.clear();

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
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        contentEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Log.i("position", String.valueOf(notePosition));
                Log.i("emptyornot", String.valueOf(MainActivity.contents.isEmpty()));
                MainActivity.contents.set(notePosition, String.valueOf(s));

                try {
                    sharedPreferences.edit().putString("contents", ObjectSerializer.serialize(MainActivity.contents)).apply();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }
}