package com.hamzasalahuddin.repjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Application;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;
import com.hamzasalahuddin.repjournal.customdialogs.OptionsMenu;
import com.hamzasalahuddin.repjournal.recyclerviews.ExerciseRepsModel;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesDataModel;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesDataViewHolder;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesRepsViewHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class History extends AppCompatActivity {
    FirestoreRecyclerAdapter<ExercisesDataModel, ExercisesDataViewHolder> adapter;
    FirestoreRecyclerAdapter<ExerciseRepsModel, ExercisesRepsViewHolder> adapter2;
    TextView exercise_title, muscle_group_title;
    ImageButton exercise_history_options, back_button;
    RecyclerView recycler_view_history;
    FirebaseFirestore firebaseFirestore;
    DocumentReference root;
    DocumentReference weekdayRef;
    OptionsMenu optionsMenu;
    String workoutDay;
    String phaseId;
    String muscleGroupTitle;
    String exerciseTitle;
    String currentDate;
    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_history);

        workoutDay = getIntent().getStringExtra("workoutDay");
        phaseId = getIntent().getStringExtra("phaseId");
        muscleGroupTitle = getIntent().getStringExtra("muscleGroupTitle");
        exerciseTitle = getIntent().getStringExtra("exerciseTitle");
        currentDate = getIntent().getStringExtra("currentDate");

        exercise_title = findViewById(R.id.exercise_title);
        muscle_group_title = findViewById(R.id.muscle_group_title);
        exercise_history_options = findViewById(R.id.exercise_history_options);
        recycler_view_history = findViewById(R.id.recycler_view_history);
        back_button = findViewById(R.id.back_button);
        optionsMenu = new OptionsMenu(this);
        dialog = new CustomProgressDialog(this);

        exercise_title.setSelected(true);

        back_button.setOnClickListener(v -> onBackPressed());

        muscle_group_title.setText(muscleGroupTitle.toUpperCase(Locale.ROOT) + "  |  ");
        exercise_title.setText(exerciseTitle.toUpperCase(Locale.ROOT));

        exercise_history_options.setOnClickListener(v -> {
            optionsMenu.delete_element_button.setText("DELETE " + exerciseTitle);
            optionsMenu.edit_element_button.setText("EDIT " + exerciseTitle);
            optionsMenu.show();
            optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                dialog.show();
                optionsMenu.deleteExercise(phaseId, workoutDay, muscleGroupTitle, exerciseTitle);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 1000);
            });
        });

        firebaseFirestore = FirebaseFirestore.getInstance();
        root = firebaseFirestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid());
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);

        Query query = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                .collection("exercises").document(exerciseTitle)
                .collection("exerciseData").orderBy("dateModified", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ExercisesDataModel> recyclerOptions = new FirestoreRecyclerOptions.Builder<ExercisesDataModel>()
                .setQuery(query, ExercisesDataModel.class).build();

        adapter = new FirestoreRecyclerAdapter<ExercisesDataModel, ExercisesDataViewHolder>(recyclerOptions) {
            @NonNull
            @Override
            public ExercisesDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_data_item, parent, false);
                return new ExercisesDataViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ExercisesDataViewHolder holder, int position, @NonNull ExercisesDataModel model) {
                String exerciseDataCreated = model.getExerciseDataCreated();
                holder.day_data_created.setText(model.getExerciseDayCreated());
                holder.month_data_created.setText(model.getExerciseMonthCreated().toUpperCase(Locale.ROOT));

                holder.itemView.setOnClickListener(v -> {
                    optionsMenu.show();
                    if (model.getExerciseDataCreated().equals(currentDate)) {
                        optionsMenu.delete_element_button.setText("DELETE RECORD FOR TODAY");
                        optionsMenu.edit_element_button.setText("EDIT RECORD FOR TODAY");
                    } else {
                        optionsMenu.delete_element_button.setText("DELETE RECORD FOR " + "\n" + model.getExerciseDataCreated());
                        optionsMenu.edit_element_button.setText("EDIT RECORD FOR " + "\n" + model.getExerciseDataCreated());
                    }
                    optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                        optionsMenu.deleteExerciseData(phaseId, workoutDay, muscleGroupTitle, exerciseTitle, model.getExerciseDataCreated());
                    });
                });
                holder.recycler_view_exercises_rep.setOnTouchListener((v, event) -> {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        optionsMenu.show();
                        if (model.getExerciseDataCreated().equals(currentDate)) {
                            optionsMenu.delete_element_button.setText("DELETE RECORD FOR TODAY");
                            optionsMenu.edit_element_button.setText("EDIT RECORD FOR TODAY");
                        } else {
                            optionsMenu.delete_element_button.setText("DELETE RECORD FOR " + "\n" + model.getExerciseDataCreated());
                            optionsMenu.edit_element_button.setText("EDIT RECORD FOR " + "\n" + model.getExerciseDataCreated());
                        }
                        optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                            optionsMenu.deleteExerciseData(phaseId, workoutDay, muscleGroupTitle, exerciseTitle, model.getExerciseDataCreated());
                        });
                        return true;
                    }
                    return false;
                });

                Query query = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                        .collection("exercises").document(exerciseTitle)
                        .collection("exerciseData").document(model.getExerciseDataCreated())
                        .collection("exerciseRecords").orderBy("setCount", Query.Direction.ASCENDING);

                FirestoreRecyclerOptions<ExerciseRepsModel> recyclerOptions = new FirestoreRecyclerOptions.Builder<ExerciseRepsModel>()
                        .setQuery(query, ExerciseRepsModel.class).build();

                adapter2 = new FirestoreRecyclerAdapter<ExerciseRepsModel, ExercisesRepsViewHolder>(recyclerOptions) {
                    @NonNull
                    @Override
                    public ExercisesRepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_rep_item, parent, false);
                        return new ExercisesRepsViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ExercisesRepsViewHolder holder, int position, @NonNull ExerciseRepsModel model) {
                        String weight = "<small> <font color='#03dac5'>"+ model.getWeight() +"</font></small><br>"
                                + "<big> <font-color='#000000'>"+ model.getRepCount() +"</font></big><br>";

                        holder.exercise_rep_count.setText(Html.fromHtml(weight));
                        holder.set_title_rep_count.setText("SET " + model.getSetCount());

                        holder.exercise_rep_count.setOnClickListener(v -> {
                            optionsMenu.show();
                            optionsMenu.delete_element_button.setText("DELETE SET " + model.getSetCount());
                            optionsMenu.edit_element_button.setText("EDIT SET " + model.getSetCount());
                            optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                                optionsMenu.deleteSet(phaseId, workoutDay, muscleGroupTitle, exerciseTitle,
                                        exerciseDataCreated, model.getSetCount());
                            });
                        });
                        holder.set_title_rep_count.setOnClickListener(v -> {
                            optionsMenu.show();
                            optionsMenu.delete_element_button.setText("DELETE SET " + model.getSetCount());
                            optionsMenu.edit_element_button.setText("EDIT SET " + model.getSetCount());
                            optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                                optionsMenu.deleteSet(phaseId, workoutDay, muscleGroupTitle, exerciseTitle,
                                        exerciseDataCreated, model.getSetCount());
                            });
                        });

                    }
                };
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                holder.recycler_view_exercises_rep.setLayoutManager(
                        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                holder.recycler_view_exercises_rep.setAdapter(adapter2);
            }
        };
        adapter.notifyDataSetChanged();
        recycler_view_history.setHasFixedSize(true);
        recycler_view_history.setLayoutManager(new LinearLayoutManager(History.this,
                LinearLayoutManager.VERTICAL, false));
        recycler_view_history.setAdapter(adapter);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }
}