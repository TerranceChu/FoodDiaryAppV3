package com.project.fooddiaryv3;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private List<DiaryEntry> diaryEntries;

    public DiaryAdapter(List<DiaryEntry> diaryEntries) {
        this.diaryEntries = diaryEntries;
    }

    @NonNull
    @Override
    public DiaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.diary_entry_item, parent, false);
        return new DiaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DiaryViewHolder holder, int position) {
        DiaryEntry diaryEntry = diaryEntries.get(position);
        holder.titleTextView.setText(diaryEntry.getTitle());
        holder.contentTextView.setText(diaryEntry.getContent());
        holder.dateTimeTextView.setText(diaryEntry.getDateTime());
        holder.weatherTextView.setText(diaryEntry.getWeather());
        holder.gpsTextView.setText(String.format("Lat: %.2f, Lon: %.2f", diaryEntry.getLatitude(), diaryEntry.getLongitude()));

        if (diaryEntry.getImageUri() != null) {
            holder.diaryImageView.setImageURI(Uri.parse(diaryEntry.getImageUri()));
        } else {
            holder.diaryImageView.setImageResource(R.drawable.placeholder); // 引用占位符图片
        }
    }

    @Override
    public int getItemCount() {
        return diaryEntries.size();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, dateTimeTextView, weatherTextView, gpsTextView;
        ImageView diaryImageView;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            dateTimeTextView = itemView.findViewById(R.id.dateTimeTextView);
            weatherTextView = itemView.findViewById(R.id.weatherTextView);
            gpsTextView = itemView.findViewById(R.id.gpsTextView);
            diaryImageView = itemView.findViewById(R.id.diaryImageView);
        }
    }
}
