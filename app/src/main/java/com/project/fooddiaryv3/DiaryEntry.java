package com.project.fooddiaryv3;

public class DiaryEntry {
    private String title;
    private String content;
    private String date;
    private String imageUri;
    private String weather;  // 添加天气信息字段

    public DiaryEntry(String title, String content, String date, String imageUri, String weather) {
        this.title = title;
        this.content = content;
        this.date = date;
        this.imageUri = imageUri;
        this.weather = weather;  // 初始化天气信息字段
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
}
