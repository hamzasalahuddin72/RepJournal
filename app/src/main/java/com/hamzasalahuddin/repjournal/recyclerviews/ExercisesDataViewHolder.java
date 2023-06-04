package com.hamzasalahuddin.repjournal.recyclerviews;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamzasalahuddin.repjournal.R;

public class ExercisesDataViewHolder extends RecyclerView.ViewHolder {
    public TextView day_data_created;
    public TextView month_data_created;
    public RecyclerView recycler_view_exercises_rep;

    public ExercisesDataViewHolder(@NonNull View itemView) {
        super(itemView);

        day_data_created = itemView.findViewById(R.id.day_data_created);
        month_data_created = itemView.findViewById(R.id.month_data_created);
        recycler_view_exercises_rep = itemView.findViewById(R.id.recycler_view_exercises_rep);
    }
}
