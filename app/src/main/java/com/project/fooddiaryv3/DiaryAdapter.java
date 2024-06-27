package com.project.fooddiaryv3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.DiaryViewHolder> {
    private List<DiaryEntry> diaryEntries;
    private Context context;

    public DiaryAdapter(Context context, List<DiaryEntry> diaryEntries) {
        this.context = context;
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
        holder.dateTextView.setText(diaryEntry.getDate());
        holder.weatherTextView.setText(diaryEntry.getWeather());

        if (diaryEntry.getImageUri() != null) {
            holder.diaryImageView.setImageURI(Uri.parse(diaryEntry.getImageUri()));
        } else {
            holder.diaryImageView.setImageResource(R.drawable.placeholder); // 占位符图片
        }

        if (diaryEntry.getLatitude() != 0 && diaryEntry.getLongitude() != 0) {
            holder.openMapButton.setVisibility(View.VISIBLE);
            holder.noLocationTextView.setVisibility(View.GONE);
            holder.openMapButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri geoUri = Uri.parse("geo:" + diaryEntry.getLatitude() + "," + diaryEntry.getLongitude() + "?q=" + diaryEntry.getLatitude() + "," + diaryEntry.getLongitude());
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, geoUri);
                    context.startActivity(mapIntent);
                }
            });
        } else {
            holder.openMapButton.setVisibility(View.GONE);
            holder.noLocationTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return diaryEntries.size();
    }

    public static class DiaryViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, contentTextView, dateTextView, weatherTextView, noLocationTextView;
        ImageView diaryImageView;
        Button openMapButton;

        public DiaryViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.titleTextView);
            contentTextView = itemView.findViewById(R.id.contentTextView);
            dateTextView = itemView.findViewById(R.id.dateTextView);
            weatherTextView = itemView.findViewById(R.id.weatherTextView);
            diaryImageView = itemView.findViewById(R.id.diaryImageView);
            openMapButton = itemView.findViewById(R.id.openMapButton);
            noLocationTextView = itemView.findViewById(R.id.noLocationTextView);
        }
    }
}
