package com.hamzasalahuddin.repjournal;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeekdaysFragment extends Fragment {

    public WeekdaysFragment() {
    }

    public static WeekdaysFragment newInstance(String param1, String param2) {
        WeekdaysFragment fragment = new WeekdaysFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_weekdays, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button mondayBtn = view.findViewById(R.id.monday_btn);
        Button tuesdayBtn = view.findViewById(R.id.tuesday_btn);
        Button wednesdayBtn = view.findViewById(R.id.wednesday_btn);
        Button thursdayBtn = view.findViewById(R.id.thursday_btn);
        Button fridayBtn = view.findViewById(R.id.friday_btn);
        Button saturdayBtn = view.findViewById(R.id.saturday_btn);
        Button sundayBtn = view.findViewById(R.id.sunday_btn);
        ImageButton back_button = view.findViewById(R.id.back_button);

        Bundle bundle = this.getArguments();

        back_button.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        if (bundle != null) {
            String phaseId = bundle.getString("phaseId");

            mondayBtn.setOnClickListener(v -> {
                Intent mondayActivity = new Intent(getActivity(), Workout.class);
                mondayActivity.putExtra("workoutDay", "monday");
                mondayActivity.putExtra("phaseId", phaseId);
                startActivity(mondayActivity);
            });
            tuesdayBtn.setOnClickListener(v -> {
                Intent tuesdayActivity = new Intent(getActivity(), Workout.class);
                tuesdayActivity.putExtra("workoutDay", "tuesday");
                tuesdayActivity.putExtra("phaseId", phaseId);
                startActivity(tuesdayActivity);
            });
            wednesdayBtn.setOnClickListener(v -> {
                Intent wednesdayActivity = new Intent(getActivity(), Workout.class);
                wednesdayActivity.putExtra("workoutDay", "wednesday");
                wednesdayActivity.putExtra("phaseId", phaseId);
                startActivity(wednesdayActivity);
            });
            thursdayBtn.setOnClickListener(v -> {
                Intent thursdayActivity = new Intent(getActivity(), Workout.class);
                thursdayActivity.putExtra("workoutDay", "thursday");
                thursdayActivity.putExtra("phaseId", phaseId);
                startActivity(thursdayActivity);
            });
            fridayBtn.setOnClickListener(v -> {
                Intent fridayActivity = new Intent(getActivity(), Workout.class);
                fridayActivity.putExtra("workoutDay", "friday");
                fridayActivity.putExtra("phaseId", phaseId);
                startActivity(fridayActivity);
            });
            saturdayBtn.setOnClickListener(v -> {
                Intent saturdayActivity = new Intent(getActivity(), Workout.class);
                saturdayActivity.putExtra("workoutDay", "saturday");
                saturdayActivity.putExtra("phaseId", phaseId);
                startActivity(saturdayActivity);
            });
            sundayBtn.setOnClickListener(v -> {
                Intent sundayActivity = new Intent(getActivity(), Workout.class);
                sundayActivity.putExtra("workoutDay", "sunday");
                sundayActivity.putExtra("phaseId", phaseId);
                startActivity(sundayActivity);
            });
        }
    }
}