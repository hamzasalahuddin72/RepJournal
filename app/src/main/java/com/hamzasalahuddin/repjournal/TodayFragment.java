package com.hamzasalahuddin.repjournal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;
import com.hamzasalahuddin.repjournal.recyclerviews.MuscleGroupsAdapter;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TodayFragment extends Fragment {
    ConstraintLayout today_workout_title;
    TextView current_day;
    ImageButton top_profile_pic;
    Button start_workout_button;
    CustomProgressDialog dialog;
    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;
    String dayOfTheWeek;
    String latestPhaseIdFiltered;
    DocumentReference root;
    StorageReference storageReference;
    FirebaseAuth auth;
    int gray;
    int lightblue;
    OnBackPressedCallback onBackPressedCallback;
    MotionLayout motionLayout;
    PieChart pieChart;

    public TodayFragment() {
    }

    public static TodayFragment newInstance(String param1, String param2) {
        TodayFragment fragment = new TodayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onBackPressedCallback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (motionLayout.getProgress() != 0.0) {
                    motionLayout.transitionToStart();
                } else {
                    setEnabled(false);
                    requireActivity().onBackPressed();
                }
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, onBackPressedCallback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_today, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        dialog = new CustomProgressDialog(getActivity());
        dialog.show();

        today_workout_title = view.findViewById(R.id.today_workout_title);
        current_day = view.findViewById(R.id.day_today);
        start_workout_button = view.findViewById(R.id.start_workout_button);
        recyclerView = view.findViewById(R.id.mg_recyclerview);
        top_profile_pic = view.findViewById(R.id.top_profile_pic);
        motionLayout = view.findViewById(R.id.today_motion_layout);
        pieChart = view.findViewById(R.id.pie_chart);

        auth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        downloadImage();

        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date d = new Date();
        dayOfTheWeek = sdf.format(d).toLowerCase(Locale.ROOT);
        current_day.setText(dayOfTheWeek.toUpperCase(Locale.ROOT));

        gray = Color.parseColor("#808080");
        lightblue = Color.parseColor("#2196F3");

        root = FirebaseFirestore.getInstance().collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());

        root.collection("userdata")
                .orderBy("datePhaseCreated", Query.Direction.DESCENDING).limit(1).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<String> list = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        list.add(document.getId());
                    }
                    String latestPhaseId = list.toString();
                    latestPhaseIdFiltered = latestPhaseId.substring(1, latestPhaseId.length() - 1);

                    start_workout_button.setOnClickListener(v -> getCurrentDayWorkout(latestPhaseIdFiltered));

                    if (!latestPhaseIdFiltered.isEmpty()) {
                        todayWorkoutInitiate(latestPhaseIdFiltered);
                        setupChart();
                        loadChartData();
                    } else {
                        todayWorkoutInitiate(" ");
                    }
                }
            }
        });

        top_profile_pic.setOnClickListener(v -> {
            Fragment fragment = new ProfileFragment();
            getFragmentManager()
                    .beginTransaction().addToBackStack(null)
                    .replace(R.id.main_container, fragment)
                    .commit();
        });

    }

    private void downloadImage() {
        storageReference.child("users/" + auth.getCurrentUser().getUid() + "/profile_pic")
                .getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(top_profile_pic);
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
            }
        });
    }

    public void getCurrentDayWorkout(String latestPhase) {
        Intent intent = new Intent(getActivity(), Workout.class);
        intent.putExtra("workoutDay", dayOfTheWeek);
        intent.putExtra("phaseId", latestPhase);
        startActivity(intent);
    }

    public void todayWorkoutInitiate(String phaseId) {
        root.collection("userdata").document(phaseId).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) {
                            today_workout_title.setVisibility(View.VISIBLE);
                            root.collection("userdata").document(phaseId)
                                    .collection("calendar").document(dayOfTheWeek)
                                    .collection("musclegroups").orderBy("muscleGroupTitle").get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                List<String> list = new ArrayList<>();
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    list.add(document.getId());
                                                }

                                                if (list.isEmpty()) {
                                                    start_workout_button.setText("START ADDING EXERCISES");
                                                    start_workout_button.setOnClickListener(v -> {
                                                        getCurrentDayWorkout(phaseId);
                                                    });
                                                }
                                                recyclerView.setHasFixedSize(true);
                                                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(3,
                                                        LinearLayoutManager.VERTICAL);
                                                recyclerView.setLayoutManager(staggeredGridLayoutManager);
                                                adapter = new MuscleGroupsAdapter((ArrayList<String>) list);
                                                recyclerView.setAdapter(adapter);

                                            String docs = list.toString();
                                            String docsFiltered = docs.substring(1, docs.length() - 1);
                                                if (docsFiltered.equals("rest")) {
                                                    start_workout_button.setEnabled(false);
                                                    start_workout_button.setBackgroundColor(gray);
                                                    start_workout_button.setText("REST TODAY");
                                                } else {
                                                    start_workout_button.setEnabled(true);
                                                    start_workout_button.setBackgroundColor(lightblue);
                                                }
                                            } else {
                                                Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        } else {
                            int navy = Color.parseColor("#0B096A");
                            start_workout_button.setText("NO WORKOUT PHASE ADDED");
                            start_workout_button.setBackgroundColor(navy);
                            today_workout_title.setVisibility(View.GONE);
                            start_workout_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Fragment fragment = new PhasesFragment();
                                    getFragmentManager()
                                            .beginTransaction().addToBackStack(null)
                                            .replace(R.id.main_container, fragment)
                                            .commit();
                                }
                            });
                        }
                    }
                });
    }

    private void setupChart() {
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(8);
        pieChart.setEntryLabelColor(Color.BLACK);
        pieChart.setCenterText(dayOfTheWeek.toUpperCase());
        pieChart.setCenterTextSize(20);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void loadChartData() {
        ArrayList<PieEntry> entries = new ArrayList<>();
        root.collection("userdata").document(latestPhaseIdFiltered)
                .collection("calendar").document(dayOfTheWeek)
                .collection("musclegroups").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                root.collection("userdata").document(latestPhaseIdFiltered)
                                        .collection("calendar").document(dayOfTheWeek)
                                        .collection("musclegroups").document(document.getId())
                                        .collection("exercises").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        entries.add(new PieEntry((float) task.getResult().size()/100, document.getId().toUpperCase() + ""));
                                                    }
                                                    ArrayList<Integer> colors = new ArrayList<>();
                                                    for (int color: ColorTemplate.MATERIAL_COLORS) {
                                                        colors.add(color);
                                                    }

                                                    for (int color: ColorTemplate.VORDIPLOM_COLORS) {
                                                        colors.add(color);
                                                    }

                                                    PieDataSet dataSet = new PieDataSet(entries, "EXERCISES");
                                                    dataSet.setColors(colors);

                                                    PieData data = new PieData(dataSet);
                                                    data.setDrawValues(true);
                                                    data.setValueFormatter(new PercentFormatter(pieChart));
                                                    data.setValueTextSize(12f);
                                                    data.setValueTextColor(Color.BLACK);

                                                    pieChart.setData(data);
                                                    pieChart.invalidate();
                                                }
                                            }
                                        });
                            }
                        } else {
                            Log.d("Count", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }
}