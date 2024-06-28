package com.project.fooddiaryv3;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
        diaryAdapter = new DiaryAdapter(this,diaryEntries);
        recyclerView.setAdapter(diaryAdapter);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("diaries").child(currentUser.getUid());
            loadDiaryEntries();
        }

        Button addDiaryButton = findViewById(R.id.addDiaryButton);
        addDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mainPage.this, WriteNewDiaryActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_DIARY);
            }
        });

        Button logoutButton = findViewById(R.id.logoutButton);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(mainPage.this, LoginActivity.class));
                finish(); // Close mainPage
            }
        });
    }

    private void loadDiaryEntries() {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                diaryEntries.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    DiaryEntry diaryEntry = snapshot.getValue(DiaryEntry.class);
                    diaryEntries.add(0, diaryEntry);
                }
                diaryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle possible errors
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
            double latitude = data.getDoubleExtra("latitude", 0);
            double longitude = data.getDoubleExtra("longitude", 0);

            DiaryEntry newEntry = new DiaryEntry(title, content, dateTime, imageUri, weather, latitude, longitude);
            diaryEntries.add(0, newEntry);
            diaryAdapter.notifyItemInserted(0);
            recyclerView.scrollToPosition(0);

            saveDiaryEntry(newEntry);
        }
    }

    private void saveDiaryEntry(DiaryEntry diaryEntry) {
        if (mAuth.getCurrentUser() != null) {
            databaseReference.push().setValue(diaryEntry);
        }
    }
}
