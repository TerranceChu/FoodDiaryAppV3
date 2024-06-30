package com.project.fooddiaryv3;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WriteNewDiaryActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_CODE_SPEECH_INPUT = 4;
    private static final String API_KEY = "1a5cd43a49cce9e045e9972afcb725a6";

    private EditText titleEditText, contentEditText, weatherEditText, dateTimeEditText;
    private ImageView selectedImageView;
    private Button selectImageButton, saveButton, recordLocationButton, takePhotoButton, voiceInputButton;

    private Uri selectedImageUri;
    private Calendar calendar;
    private FusedLocationProviderClient fusedLocationClient;
    private double latitude = 0.0, longitude = 0.0;  // intit to 0.0
    private StorageReference storageReference;
    private WeatherApi weatherApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_new_diary);

        titleEditText = findViewById(R.id.titleEditText);
        contentEditText = findViewById(R.id.contentEditText);
        weatherEditText = findViewById(R.id.weatherEditText);
        dateTimeEditText = findViewById(R.id.dateTimeEditText);
        selectedImageView = findViewById(R.id.selectedImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveButton = findViewById(R.id.saveButton);
        recordLocationButton = findViewById(R.id.recordLocationButton);
        takePhotoButton = findViewById(R.id.takePhotoButton);
        voiceInputButton = findViewById(R.id.voiceInputButton);

        calendar = Calendar.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        storageReference = FirebaseStorage.getInstance().getReference("diary_images");

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.openweathermap.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        weatherApi = retrofit.create(WeatherApi.class);

        dateTimeEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateTimePicker();
            }
        });

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImagePicker();
            }
        });

        takePhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(WriteNewDiaryActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WriteNewDiaryActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_IMAGE_CAPTURE);
                } else {
                    openCamera();
                }
            }
        });

        voiceInputButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceInput();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImageAndSaveDiaryEntry();
            }
        });

        recordLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(WriteNewDiaryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(WriteNewDiaryActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
                } else {
                    openMap();
                }
            }
        });
    }

    private void showDateTimePicker() {
        // 弹出日期选择对话框
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                // 弹出时间选择对话框
                TimePickerDialog timePickerDialog = new TimePickerDialog(WriteNewDiaryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);

                        // 更新日期和时间输入框
                        dateTimeEditText.setText(String.format("%d-%d-%d %02d:%02d", calendar.get(Calendar.YEAR),
                                calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                                calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)));
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
                timePickerDialog.show();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    }

    private void startVoiceInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "Speech recognition not supported on this device.", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImageAndSaveDiaryEntry() {
        if (selectedImageUri != null) {
            StorageReference fileReference = storageReference.child(System.currentTimeMillis() + "." + getFileExtension(selectedImageUri));
            fileReference.putFile(selectedImageUri)
                    .addOnSuccessListener(taskSnapshot -> fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imageUrl = uri.toString();
                        saveDiaryEntry(imageUrl);
                    }))
                    .addOnFailureListener(e -> Toast.makeText(WriteNewDiaryActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show());
        } else {
            saveDiaryEntry(null);
        }
    }

    private void saveDiaryEntry(String imageUrl) {
        String title = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();
        String weather = weatherEditText.getText().toString();
        String dateTime = dateTimeEditText.getText().toString();

        Intent resultIntent = new Intent();
        resultIntent.putExtra("title", title);
        resultIntent.putExtra("content", content);
        resultIntent.putExtra("weather", weather);
        resultIntent.putExtra("dateTime", dateTime);
        resultIntent.putExtra("imageUri", imageUrl);
        resultIntent.putExtra("latitude", latitude);
        resultIntent.putExtra("longitude", longitude);
        setResult(RESULT_OK, resultIntent);
        finish();
    }

    private String getFileExtension(Uri uri) {
        return getContentResolver().getType(uri).split("/")[1];
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_REQUEST && data != null && data.getData() != null) {
                selectedImageUri = data.getData();
                selectedImageView.setImageURI(selectedImageUri);
            } else if (requestCode == REQUEST_IMAGE_CAPTURE && data != null) {
                Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                selectedImageUri = getImageUri(imageBitmap);
                selectedImageView.setImageURI(selectedImageUri);
            } else if (requestCode == REQUEST_CODE_SPEECH_INPUT && data != null) {
                ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if (result != null && !result.isEmpty()) {
                    String spokenText = result.get(0);
                    showInsertTextDialog(spokenText);
                }
            }
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION_PERMISSION && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openMap();
        }
        if (requestCode == REQUEST_IMAGE_CAPTURE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        }
    }

    private void showInsertTextDialog(String spokenText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Insert Text");
        builder.setMessage("Where would you like to insert the text?");
        builder.setPositiveButton("Title", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentText = titleEditText.getText().toString();
                titleEditText.setText(currentText + " " + spokenText);
            }
        });
        builder.setNegativeButton("Content", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentText = contentEditText.getText().toString();
                contentEditText.setText(currentText + " " + spokenText);
            }
        });
        builder.setNeutralButton("Weather", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentText = weatherEditText.getText().toString();
                weatherEditText.setText(currentText + " " + spokenText);
            }
        });
        builder.show();
    }

    private void openMap() {
        try {
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map_container);
            if (mapFragment == null) {
                mapFragment = SupportMapFragment.newInstance();
                getSupportFragmentManager().beginTransaction().replace(R.id.map_container, mapFragment).addToBackStack(null).commit();
            }
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    if (ActivityCompat.checkSelfPermission(WriteNewDiaryActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(WriteNewDiaryActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        return;
                    }
                    googleMap.setMyLocationEnabled(true);
                    fusedLocationClient.getLastLocation()
                            .addOnSuccessListener(WriteNewDiaryActivity.this, new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null) {
                                        LatLng userLocation = new LatLng(location.getLatitude(), location.getLongitude());
                                        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));
                                        Marker marker = googleMap.addMarker(new MarkerOptions().position(userLocation).draggable(true));
                                        googleMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
                                            @Override
                                            public void onMarkerDragStart(Marker marker) {
                                                // Do nothing
                                            }

                                            @Override
                                            public void onMarkerDrag(Marker marker) {
                                                // Do nothing
                                            }

                                            @Override
                                            public void onMarkerDragEnd(Marker marker) {
                                                LatLng position = marker.getPosition();
                                                latitude = position.latitude;
                                                longitude = position.longitude;
                                                Toast.makeText(WriteNewDiaryActivity.this, "Location recorded: " + latitude + ", " + longitude, Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        fetchWeather(location.getLatitude(), location.getLongitude());
                                    }
                                }
                            });
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void fetchWeather(double latitude, double longitude) {
        Call<WeatherResponse> call = weatherApi.getCurrentWeather(latitude, longitude, API_KEY, "metric");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();
                    String weatherDescription = weatherResponse.getWeather()[0].getDescription();
                    float temperature = weatherResponse.getMain().getTemp();
                    weatherEditText.setText(weatherDescription + ", " + temperature + "°C");
                } else {
                    Toast.makeText(WriteNewDiaryActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(WriteNewDiaryActivity.this, "Failed to fetch weather data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
