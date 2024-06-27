package com.project.fooddiaryv3;

public class DiaryEntry {
    private String title;
    private String content;
    private String date;
    private String imageUri;
    private String weather;
    private double latitude;
    private double longitude;

    public DiaryEntry(String title, String content, String date, String imageUri, String weather, double latitude, double longitude) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.imageUri = imageUri;
        this.weather = weather;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // Getters and setters for all fields
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getImageUri() {
        return imageUri;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
