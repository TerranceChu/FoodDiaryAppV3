package com.project.fooddiaryv3;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class mainPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<DiaryEntry> diaryEntries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        diaryEntries = new ArrayList<>();
        diaryEntries.add(new DiaryEntry("First Entry", "This is the content of the first entry.", "2024-06-26"));
        diaryEntries.add(new DiaryEntry("Second Entry", "This is the content of the second entry.", "2024-06-27"));

        diaryAdapter = new DiaryAdapter(diaryEntries);
        recyclerView.setAdapter(diaryAdapter);
    }
}
