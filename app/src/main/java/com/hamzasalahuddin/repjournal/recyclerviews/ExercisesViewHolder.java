package com.hamzasalahuddin.repjournal.recyclerviews;

import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hamzasalahuddin.repjournal.R;

public class ExercisesViewHolder extends RecyclerView.ViewHolder {
    public TextView exercise_workout_title;
    public ImageButton exercises_options, workout_history;
    public Button add_set_button;
    public RecyclerView recycler_view_exercises_data;

    public ExercisesViewHolder(@NonNull View itemView) {
        super(itemView);


        exercise_workout_title = itemView.findViewById(R.id.exercise_workout_title);
        exercises_options = itemView.findViewById(R.id.exercises_options);
        workout_history = itemView.findViewById(R.id.workout_history);
        add_set_button = itemView.findViewById(R.id.add_set_button);
        recycler_view_exercises_data = itemView.findViewById(R.id.recycler_view_exercises_data);

        recycler_view_exercises_data.setLayoutManager(new LinearLayoutManager(itemView.getContext(),
                LinearLayoutManager.VERTICAL, false));
        recycler_view_exercises_data.setHasFixedSize(true);
    }
}
