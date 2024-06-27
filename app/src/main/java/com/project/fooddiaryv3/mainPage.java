package com.project.fooddiaryv3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class mainPage extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<DiaryEntry> diaryEntries;
    private static final int REQUEST_CODE_ADD_DIARY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Initialize the list of diary entries
        diaryEntries = new ArrayList<>();
        diaryEntries.add(new DiaryEntry("First Entry", "This is the content of the first entry.", "2024-06-26 08:00", null, "Sunny", 0.0, 0.0));
        diaryEntries.add(new DiaryEntry("Second Entry", "This is the content of the second entry.", "2024-06-27 14:00", null, "Cloudy", 0.0, 0.0));

        diaryAdapter = new DiaryAdapter(diaryEntries);
        recyclerView.setAdapter(diaryAdapter);

        Button addDiaryButton = findViewById(R.id.addDiaryButton);
        addDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainPage.this, WriteNewDiaryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DIARY);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_DIARY && resultCode == RESULT_OK && data != null) {
            String title = data.getStringExtra("title");
            String content = data.getStringExtra("content");
            String dateTime = data.getStringExtra("dateTime");
            String weather = data.getStringExtra("weather");
            String imageUri = data.getStringExtra("imageUri");
            double latitude = data.getDoubleExtra("latitude", 0.0);
            double longitude = data.getDoubleExtra("longitude", 0.0);

            DiaryEntry newEntry = new DiaryEntry(title, content, dateTime, imageUri, weather, latitude, longitude);
            diaryEntries.add(0, newEntry);  // 将新条目添加到列表的开头
            diaryAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);  // 滚动到最新条目
        }
    }
}
