package com.example.personaldiaryapptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;

public class TemplateEditActivity extends AppCompatActivity {

    EditText templateTitleEditText;
    EditText templateContentEditText;
    SharedPreferences sharedPreferences;

    static ArrayList<String> templateTitles = new ArrayList<>();
    static ArrayList<String> templateContents = new ArrayList<>();

    String templateTitle;
    String templateContent;

    public void saveTemplate(View view) {

        templateTitle = templateTitleEditText.getText().toString();
        templateContent = templateContentEditText.getText().toString();

        if (templateTitle.matches("") || templateContent.matches("")) {
            new AlertDialog.Builder(TemplateEditActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .setTitle("Please enter content.")
                    .setMessage("Are you sure you don't want to enter any content?" + "\n\n" + "Note: This template will not save if you don't enter any content")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("No", null)
                    .show();
            return;
        }

        templateTitles.add(templateTitle);
        templateContents.add(templateContent);

        try {
            sharedPreferences.edit().putString("templateTitles", ObjectSerializer.serialize(templateTitles)).apply();
            sharedPreferences.edit().putString("templateContents", ObjectSerializer.serialize(templateContents)).apply();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Log.i("clicked?", "clicked");
        Log.i("title", templateTitle);
        Log.i("content", templateContent);
        Log.i("templateTitles", TemplateEditActivity.templateTitles.toString());
        Log.i("templateContents", TemplateEditActivity.templateContents.toString());

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_template_edit);

        templateTitleEditText = (EditText) findViewById(R.id.templateTitleEditText);
        templateContentEditText = (EditText) findViewById(R.id.templateContentEditText);
        sharedPreferences = getApplicationContext().getSharedPreferences("com.example.personaldiaryapptest", Context.MODE_PRIVATE);
    }
}