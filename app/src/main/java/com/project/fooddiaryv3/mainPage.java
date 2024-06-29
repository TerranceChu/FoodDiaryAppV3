package com.project.fooddiaryv3;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class mainPage extends AppCompatActivity {

    private static final String TAG = "mainPage";

    private RecyclerView recyclerView;
    private DiaryAdapter diaryAdapter;
    private List<DiaryEntry> diaryEntries;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private static final int REQUEST_CODE_ADD_DIARY = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        diaryEntries = new ArrayList<>();
        diaryAdapter = new DiaryAdapter(this, diaryEntries);
        recyclerView.setAdapter(diaryAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("diaries").child(currentUser.getUid());
            loadDiaryEntries();
        } else {
            // 如果用户未登录，提示用户登录或导航到登录页面
            Toast.makeText(this, "Please log in to view your diaries.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intent = new Intent(mainPage.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button addDiaryButton = findViewById(R.id.addDiaryButton);
        addDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainPage.this, WriteNewDiaryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DIARY);
            }
        });
    }

    private void loadDiaryEntries() {
        if (databaseReference != null) {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    diaryEntries.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        DiaryEntry diaryEntry = snapshot.getValue(DiaryEntry.class);
                        Log.d(TAG, "Loaded diary entry: " + diaryEntry.getTitle());
                        diaryEntries.add(0, diaryEntry);
                    }
                    diaryAdapter.notifyDataSetChanged();
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // Handle possible errors
                    Log.e(TAG, "Failed to load data.", databaseError.toException());
                    Toast.makeText(mainPage.this, "Failed to load data.", Toast.LENGTH_SHORT).show();
                }
            });
        }
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
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            DiaryEntry newEntry = new DiaryEntry(title, content, dateTime, imageUri, weather, latitude, longitude);
            diaryEntries.add(0, newEntry);  // Add new entry to the top of the list
            diaryAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);  // Scroll to the latest entry

            // Save to Firebase
            if (databaseReference != null) {
                databaseReference.push().setValue(newEntry)
                        .addOnSuccessListener(aVoid -> Log.d(TAG, "Diary entry saved successfully"))
                        .addOnFailureListener(e -> Log.e(TAG, "Failed to save diary entry", e));
            }
        }
    }
}
