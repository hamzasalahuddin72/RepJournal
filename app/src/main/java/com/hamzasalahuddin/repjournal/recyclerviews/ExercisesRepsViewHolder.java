package com.hamzasalahuddin.repjournal.recyclerviews;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hamzasalahuddin.repjournal.R;

public class ExercisesRepsViewHolder extends RecyclerView.ViewHolder {
    public Button exercise_rep_count;
    public TextView set_title_rep_count;

    public ExercisesRepsViewHolder(@NonNull View itemView) {
        super(itemView);

        exercise_rep_count = itemView.findViewById(R.id.exercise_rep_count);
        set_title_rep_count = itemView.findViewById(R.id.set_title_rep_count);
    }
}
