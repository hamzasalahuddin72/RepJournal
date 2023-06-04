package com.hamzasalahuddin.repjournal;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GestureDetectorCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;
import com.hamzasalahuddin.repjournal.customdialogs.InputDialog;
import com.hamzasalahuddin.repjournal.customdialogs.OptionsMenu;
import com.hamzasalahuddin.repjournal.recyclerviews.ExerciseRepsModel;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesDataModel;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesDataViewHolder;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesModel;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesRepsViewHolder;
import com.hamzasalahuddin.repjournal.recyclerviews.ExercisesViewHolder;
import com.hamzasalahuddin.repjournal.recyclerviews.RepCountAdapter;
import com.hamzasalahuddin.repjournal.recyclerviews.RepCountButtonsModel;
import com.hamzasalahuddin.repjournal.recyclerviews.WorkoutModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class Workout extends AppCompatActivity implements AdapterView.OnItemSelectedListener, RepCountAdapter.IRepCount {
    FirestoreRecyclerAdapter<WorkoutModel,WorkoutViewHolder> adapter;
    FirestoreRecyclerAdapter<ExercisesModel, ExercisesViewHolder> adapter2;
    FirestoreRecyclerAdapter<ExercisesDataModel, ExercisesDataViewHolder> adapter3;
    FirestoreRecyclerAdapter<ExerciseRepsModel, ExercisesRepsViewHolder> adapter4;
    TextView current_workout_day;
    Button add_mg_btn;
    ImageButton back_button;
    RecyclerView recycler_view_muscle_groups;
    RecyclerView rep_count_recycler_view;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference root;
    DocumentReference weekdayRef;
    FirebaseUser user;
    String workoutDay;
    String phaseId;
    CustomProgressDialog dialog;
    OptionsMenu optionsMenu;
    InputDialog inputDialog;
    AlertDialog exerciseRepPopup;
    AlertDialog.Builder builder;
    WindowManager.LayoutParams params;
    View view;
    Spinner weights_spinner;
    List<String> setsList;
    ArrayList<RepCountButtonsModel> repCountButtonArray;
    String setCount;
    String repCount;
    String weight;
    String mgTitle;
    String exTitle;
    String exDateCreated;
    String date;
    String month;
    String year;
    String dayMonthYear;
    Boolean weightSelected = false;
    TextView day_rep_recorded, month_rep_recorded,
            popup_exercise_title, select_reps_title, set_count_view;
    LinearLayout recycler_view_toggle_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_workout);

        current_workout_day = findViewById(R.id.current_workout_day);
        add_mg_btn = findViewById(R.id.add_mg_btn);
        back_button = findViewById(R.id.back_button);
        recycler_view_muscle_groups = findViewById(R.id.recycler_view_muscle_groups);

        SimpleDateFormat dayFormat = new SimpleDateFormat("dd");
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM");
        SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
        Date d1 = new Date();
        Date d2 = new Date();
        Date d3 = new Date();
        date = dayFormat.format(d1).toLowerCase(Locale.ROOT);
        month = monthFormat.format(d2).toLowerCase(Locale.ROOT);
        year = yearFormat.format(d3).toLowerCase(Locale.ROOT);
        dayMonthYear = date + " " + month + " " + year;

        dialog = new CustomProgressDialog(this);
        inputDialog = new InputDialog(this);
        optionsMenu = new OptionsMenu(this);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        workoutDay = getIntent().getExtras().getString("workoutDay");
        phaseId = getIntent().getExtras().getString("phaseId");

        root = firebaseFirestore.collection("users")
                .document(user.getUid());
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);

        current_workout_day.setText(workoutDay.toUpperCase(Locale.ROOT));

        add_mg_btn.setOnClickListener(v -> {
            Intent exercises = new Intent(getApplicationContext(), MuscleGroups.class);
            exercises.putExtra("workoutDay", workoutDay);
            exercises.putExtra("phaseId", phaseId);
            startActivity(exercises);
        });

        back_button.setOnClickListener(v -> {
            onBackPressed();
        });

        repCountButtonArray = new ArrayList<>();
        createRepCountButtons();

        builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);

        view = getLayoutInflater().inflate(R.layout.new_exercise_data_popup, null);

        rep_count_recycler_view = view.findViewById(R.id.rep_count_recycler_view);

        weights_spinner = view.findViewById(R.id.weights_spinner);
        ArrayAdapter<CharSequence> weightsAdapter = ArrayAdapter.createFromResource(this,
                R.array.weights_kg, R.layout.weight_spinner_item);
        weightsAdapter.setDropDownViewResource(R.layout.weight_spinner_dropdown);
        weights_spinner.setAdapter(weightsAdapter);
        weights_spinner.setOnItemSelectedListener(this);

        day_rep_recorded = view.findViewById(R.id.day_rep_recorded);
        month_rep_recorded = view.findViewById(R.id.month_rep_recorded);
        popup_exercise_title = view.findViewById(R.id.popup_exercise_title);
        select_reps_title = view.findViewById(R.id.select_reps_title);
        set_count_view = view.findViewById(R.id.set_count_view);
        recycler_view_toggle_layout = view.findViewById(R.id.recycler_view_toggle_layout);

        params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.windowAnimations = android.R.style.Animation_InputMethod;

        builder.setView(view);
        exerciseRepPopup = builder.create();
        exerciseRepPopup.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        exerciseRepPopup.getWindow().setAttributes(params);
        setRepCountAdapter();

        Query query = weekdayRef.collection("musclegroups").orderBy("dateModified", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<WorkoutModel> recyclerOptions = new FirestoreRecyclerOptions.Builder<WorkoutModel>()
                .setQuery(query, WorkoutModel.class).build();
        adapter = new FirestoreRecyclerAdapter<WorkoutModel, WorkoutViewHolder>(recyclerOptions) {
            @NonNull
            @Override
            public WorkoutViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workout_item, parent, false);
                return new WorkoutViewHolder(view);
            }
            @Override
            protected void onBindViewHolder(@NonNull WorkoutViewHolder workoutViewHolder, int position, @NonNull WorkoutModel workoutModel) {
                String muscleGroupTitle = workoutModel.getMuscleGroupTitle();

                workoutViewHolder.muscle_group_workout_title.setText(muscleGroupTitle.toUpperCase(Locale.ROOT));
                workoutViewHolder.add_exercise_button.setOnClickListener(v ->  {
                    Bundle bundle = new Bundle();
                    bundle.putString("workoutDay", workoutDay);
                    inputDialog.show();
                    inputDialog.new_title_input.setHint("Exercise title");
                    inputDialog.proceed_exercise_add_button.setOnClickListener(v1 -> {
                        inputDialog.addExercises(phaseId, workoutDay, muscleGroupTitle);
                    });
                });
                inputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        inputDialog.new_title_input.setText("");
                    }
                });
                workoutViewHolder.muscle_group_options.setOnClickListener(v -> {
                    optionsMenu.show();
                    optionsMenu.delete_element_button.setText("DELETE " + muscleGroupTitle);
                    optionsMenu.edit_element_button.setText("EDIT " + muscleGroupTitle);
                    optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                        optionsMenu.deleteMuscleGroupDocs(phaseId, workoutDay, muscleGroupTitle);
                    });
                });

                Query query2 = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                        .collection("exercises").orderBy("dateModified", Query.Direction.DESCENDING);

                FirestoreRecyclerOptions<ExercisesModel> recyclerOptions2 = new FirestoreRecyclerOptions.Builder<ExercisesModel>()
                        .setQuery(query2, ExercisesModel.class).build();

                adapter2 = new FirestoreRecyclerAdapter<ExercisesModel, ExercisesViewHolder>(recyclerOptions2) {
                    @NonNull
                    @Override
                    public ExercisesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_item, parent, false);
                        return new ExercisesViewHolder(view);
                    }

                    @Override
                    protected void onBindViewHolder(@NonNull ExercisesViewHolder exercisesViewHolder, int position2, @NonNull ExercisesModel exercisesModel) {
                        String exerciseTitle = exercisesModel.getExerciseTitle();

                        exercisesViewHolder.workout_history.setOnClickListener(v -> {
                            Intent intent = new Intent(Workout.this, History.class);
                            intent.putExtra("workoutDay", workoutDay);
                            intent.putExtra("phaseId", phaseId);
                            intent.putExtra("muscleGroupTitle", muscleGroupTitle);
                            intent.putExtra("exerciseTitle", exerciseTitle);
                            intent.putExtra("currentDate", dayMonthYear);
                            startActivity(intent);
                        });

                        exercisesViewHolder.exercise_workout_title.setText(exerciseTitle.toUpperCase(Locale.ROOT));
                        exercisesViewHolder.exercises_options.setOnClickListener(v -> {
                            optionsMenu.show();
                            optionsMenu.delete_element_button.setText("DELETE " + exerciseTitle);
                            optionsMenu.edit_element_button.setText("EDIT " + exerciseTitle);
                            optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                                optionsMenu.deleteExercise(phaseId, workoutDay, muscleGroupTitle, exerciseTitle);
                            });
                        });

                        exercisesViewHolder.add_set_button.setOnClickListener(v -> {
                            exDateCreated = dayMonthYear;
                            exTitle = exerciseTitle;
                            mgTitle = muscleGroupTitle;
                            popup_exercise_title.setText(exerciseTitle.toUpperCase(Locale.ROOT));
                            addExerciseData(muscleGroupTitle, exerciseTitle);
                            getSetsCount(muscleGroupTitle, exerciseTitle, dayMonthYear);
                            recycler_view_toggle_layout.setVisibility(View.GONE);

                        });
                        resetWeightSpinner();

                        Query query3 = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                                .collection("exercises").document(exerciseTitle)
                                .collection("exerciseData").orderBy("dateModified", Query.Direction.DESCENDING).limit(2);

                        FirestoreRecyclerOptions<ExercisesDataModel> recyclerOptions3 = new FirestoreRecyclerOptions.Builder<ExercisesDataModel>()
                                .setQuery(query3, ExercisesDataModel.class).build();

                        adapter3 = new FirestoreRecyclerAdapter<ExercisesDataModel, ExercisesDataViewHolder>(recyclerOptions3) {
                            @NonNull
                            @Override
                            public ExercisesDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_data_item, parent, false);
                                return new ExercisesDataViewHolder(view);
                            }

                            @Override
                            protected void onBindViewHolder(@NonNull ExercisesDataViewHolder exercisesDataViewHolder, int position3, @NonNull ExercisesDataModel exercisesDataModel) {
                                String exerciseDateCreated = exercisesDataModel.getExerciseDataCreated();

                                exercisesDataViewHolder.day_data_created.setText(exercisesDataModel.getExerciseDayCreated().toUpperCase(Locale.ROOT));
                                exercisesDataViewHolder.month_data_created.setText(exercisesDataModel.getExerciseMonthCreated().toUpperCase(Locale.ROOT));

                                exercisesDataViewHolder.itemView.setOnClickListener(v -> {
                                    exDateCreated = exerciseDateCreated;
                                    exTitle = exerciseTitle;
                                    mgTitle = muscleGroupTitle;
                                    popup_exercise_title.setText(exerciseTitle.toUpperCase(Locale.ROOT));
                                    getSetsCount(muscleGroupTitle, exerciseTitle, exerciseDateCreated);
                                    recycler_view_toggle_layout.setVisibility(View.GONE);
                                    getRecordDate(exercisesDataModel.getExerciseDayCreated(),
                                            exercisesDataModel.getExerciseMonthCreated());
                                    exerciseRepPopup.show();
                                });

                                exercisesDataViewHolder.itemView.setOnLongClickListener(v -> {
                                    optionsMenu.show();
                                    if (exerciseDateCreated.equals(dayMonthYear)) {
                                        optionsMenu.delete_element_button.setText("DELETE RECORD FOR TODAY");
                                        optionsMenu.edit_element_button.setText("EDIT RECORD FOR TODAY");
                                    } else {
                                        optionsMenu.delete_element_button.setText("DELETE RECORD FOR " + "\n" + exerciseDateCreated);
                                        optionsMenu.edit_element_button.setText("EDIT RECORD FOR " + "\n" + exerciseDateCreated);
                                    }
                                    optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                                        optionsMenu.deleteExerciseData(phaseId, workoutDay, muscleGroupTitle, exerciseTitle, exerciseDateCreated);
                                    });
                                    return true;
                                });
                                resetWeightSpinner();

                                Query query4 = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                                        .collection("exercises").document(exerciseTitle)
                                        .collection("exerciseData").document(exercisesDataModel.getExerciseDataCreated())
                                        .collection("exerciseRecords").orderBy("setCount", Query.Direction.ASCENDING);

                                FirestoreRecyclerOptions<ExerciseRepsModel> recyclerOptions4 = new FirestoreRecyclerOptions.Builder<ExerciseRepsModel>()
                                        .setQuery(query4, ExerciseRepsModel.class).build();
                                adapter4 = new FirestoreRecyclerAdapter<ExerciseRepsModel, ExercisesRepsViewHolder>(recyclerOptions4) {
                                    int i = 0;
                                    @NonNull
                                    @Override
                                    public ExercisesRepsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.exercise_rep_item, parent, false);
                                        return new ExercisesRepsViewHolder(view);
                                    }

                                    @Override
                                    protected void onBindViewHolder(@NonNull ExercisesRepsViewHolder exercisesRepsViewHolder, int position4, @NonNull ExerciseRepsModel exerciseRepsModel) {
                                        String weight = "<small> <font color='#03dac5'>"+ exerciseRepsModel.getWeight() +"</font></small><br>"
                                                + "<big> <font-color='#000000'>"+ exerciseRepsModel.getRepCount() +"</font></big><br>";

                                        exercisesRepsViewHolder.exercise_rep_count.setText(Html.fromHtml(weight));
                                        exercisesRepsViewHolder.set_title_rep_count.setText("SET " + exerciseRepsModel.getSetCount());

                                        exercisesRepsViewHolder.exercise_rep_count.setOnClickListener(v -> {
                                            i++;
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                @Override
                                                public void run() {
                                                    int repCount = Integer.parseInt(exerciseRepsModel.getRepCount());
                                                    if (i == 1) {
                                                        for (int i = 0; i < weights_spinner.getCount(); i++) {
                                                            if (weights_spinner.getItemAtPosition(i).equals(exerciseRepsModel.getWeight())) {
                                                                weights_spinner.setSelection(i);
                                                            }
                                                        }
                                                        exDateCreated = exerciseDateCreated;
                                                        exTitle = exerciseTitle;
                                                        mgTitle = muscleGroupTitle;
                                                        popup_exercise_title.setText(exerciseTitle.toUpperCase(Locale.ROOT));
                                                        set_count_view.setText(exerciseRepsModel.getSetCount());
                                                        recycler_view_toggle_layout.setVisibility(View.GONE);
                                                        getRecordDate(exercisesDataModel.getExerciseDayCreated(),
                                                                exercisesDataModel.getExerciseMonthCreated());
                                                        exerciseRepPopup.show();
                                                    } else if (i == 2) {
                                                        int repCountIncrement = Integer.sum(repCount, 1);
                                                        String repCountIncrementString = String.valueOf(repCountIncrement);
                                                        addExerciseRecords(muscleGroupTitle, exerciseTitle, exerciseDateCreated,
                                                                exerciseRepsModel.getSetCount(), repCountIncrementString, exerciseRepsModel.getWeight());
                                                    } else if (i == 3) {
                                                        int repCountDecrement = Integer.sum(repCount, -1);
                                                        String repCountDecrementString = String.valueOf(repCountDecrement);
                                                        addExerciseRecords(muscleGroupTitle, exerciseTitle, exerciseDateCreated,
                                                                exerciseRepsModel.getSetCount(), repCountDecrementString, exerciseRepsModel.getWeight());
                                                    }
                                                    i = 0;
                                                }
                                            }, 400);
                                        });
                                        exercisesRepsViewHolder.exercise_rep_count.setOnLongClickListener(view -> {
                                            optionsMenu.show();
                                            optionsMenu.delete_element_button.setText("DELETE SET " + exerciseRepsModel.getSetCount());
                                            optionsMenu.edit_element_button.setText("EDIT SET " + exerciseRepsModel.getSetCount());
                                            optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                                                optionsMenu.deleteSet(phaseId, workoutDay, muscleGroupTitle, exerciseTitle,
                                                        exerciseDateCreated, exerciseRepsModel.getSetCount());
                                            });
                                            return true;
                                        });
                                        exercisesRepsViewHolder.itemView.setOnLongClickListener(view -> {
                                            optionsMenu.show();
                                            optionsMenu.delete_element_button.setText("DELETE SET " + exerciseRepsModel.getSetCount());
                                            optionsMenu.edit_element_button.setText("EDIT SET " + exerciseRepsModel.getSetCount());
                                            optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                                                optionsMenu.deleteSet(phaseId, workoutDay, muscleGroupTitle, exerciseTitle,
                                                        exerciseDateCreated, exerciseRepsModel.getSetCount());
                                            });
                                            return true;
                                        });
                                        exercisesDataViewHolder.recycler_view_exercises_rep.setOnTouchListener((v, event) -> {
                                            if (event.getAction() == MotionEvent.ACTION_UP) {
                                                exDateCreated = exerciseDateCreated;
                                                exTitle = exerciseTitle;
                                                mgTitle = muscleGroupTitle;
                                                popup_exercise_title.setText(exerciseTitle.toUpperCase(Locale.ROOT));
                                                getSetsCount(muscleGroupTitle, exerciseTitle, exerciseDateCreated);
                                                recycler_view_toggle_layout.setVisibility(View.GONE);
                                                getRecordDate(exercisesDataModel.getExerciseDayCreated(),
                                                        exercisesDataModel.getExerciseMonthCreated());
                                                exerciseRepPopup.show();
                                                return true;
                                            }
                                            return false;
                                        });
                                        resetWeightSpinner();
                                    }
                                };
                                adapter4.startListening();
                                adapter4.notifyDataSetChanged();
                                exercisesDataViewHolder.recycler_view_exercises_rep.setAdapter(adapter4);
                                exercisesDataViewHolder.recycler_view_exercises_rep.setLayoutManager(
                                        new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL));
                            }
                        };
                        adapter3.startListening();
                        adapter3.notifyDataSetChanged();
                        exercisesViewHolder.recycler_view_exercises_data.setAdapter(adapter3);
                    }
                };
                adapter2.startListening();
                adapter2.notifyDataSetChanged();
                workoutViewHolder.recycler_view_exercises.setAdapter(adapter2);
            }
        };
        recycler_view_muscle_groups.setHasFixedSize(true);
        recycler_view_muscle_groups.setLayoutManager(new LinearLayoutManager(Workout.this, LinearLayoutManager.HORIZONTAL, false));
        recycler_view_muscle_groups.setAdapter(adapter);

        SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(recycler_view_muscle_groups);


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (!parent.getItemAtPosition(position).equals("kg")) {
            weightSelected = true;
            weight = parent.getItemAtPosition(position).toString();
            if ( weightSelected == true) {
                recycler_view_toggle_layout.setVisibility(View.VISIBLE);
            }
        } else {
            weightSelected = false;
            recycler_view_toggle_layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    @Override
    public void getRepCount(Intent intent) {
        repCount = intent.getStringExtra("repCount");
        setCount = set_count_view.getText().toString();
        addExerciseRecords(mgTitle, exTitle, exDateCreated, setCount, repCount, weight);
    }

    public class WorkoutViewHolder extends RecyclerView.ViewHolder {
        public TextView muscle_group_workout_title;
        public Button add_exercise_button;
        public MotionLayout motionLayoutExercises;
        public ConstraintLayout exercise_card;
        public Button proceed_exercise_add_button, cancel_exercise_add_button;
        public ImageButton muscle_group_options;
        public RecyclerView recycler_view_exercises;
        public RecyclerView.LayoutManager manager;

        public WorkoutViewHolder(@NonNull View itemView) {
            super(itemView);

            muscle_group_workout_title = itemView.findViewById(R.id.muscle_group_workout_title);
            add_exercise_button = itemView.findViewById(R.id.add_exercise_button);
            motionLayoutExercises = itemView.findViewById(R.id.motionLayoutExercises);
            proceed_exercise_add_button = itemView.findViewById(R.id.proceed_exercise_add_button);
            cancel_exercise_add_button = itemView.findViewById(R.id.cancel_exercise_add_button);
            exercise_card = itemView.findViewById(R.id.exercise_card);
            muscle_group_options = itemView.findViewById(R.id.muscle_group_options);
            recycler_view_exercises = itemView.findViewById(R.id.recycler_view_exercises);
            manager = new LinearLayoutManager(itemView.getContext(), LinearLayoutManager.VERTICAL, false);
            recycler_view_exercises.setLayoutManager(manager);

            SnapHelper snapHelper = new LinearSnapHelper();
            snapHelper.attachToRecyclerView(recycler_view_exercises);
        }
    }

    public void addExerciseData(String muscleGroupTitle, String exerciseTitle) {
        String dateModified = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        dialog.show();
        Map<String,Object> exerciseData = new HashMap<>();
        exerciseData.put("exerciseDataCreated", dayMonthYear);
        exerciseData.put("exerciseDayCreated", date);
        exerciseData.put("exerciseMonthCreated", month);
        exerciseData.put("dateModified", dateModified);

        DocumentReference phaseRef = root.collection("userdata").document(phaseId);
        DocumentReference calendarRef = phaseRef.collection("calendar").document(workoutDay);
        DocumentReference mgRef = calendarRef.collection("musclegroups").document(muscleGroupTitle);
        DocumentReference exercisesRef = mgRef.collection("exercises").document(exerciseTitle);
        DocumentReference exercisesDataRef = exercisesRef.collection("exerciseData").document(dayMonthYear);

        exercisesDataRef.set(exerciseData).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                getRecordDate(date.toUpperCase(Locale.ROOT), month.toUpperCase(Locale.ROOT));
                exerciseRepPopup.show();
                dialog.dismiss();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Workout.this, "Failed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void addExerciseRecords(String muscleGroupTitle, String exerciseTitle, String exerciseDateCreated, String setCount, String repCount, String weight) {
        Map<String,Object> exerciseRecord = new HashMap<>();
        exerciseRecord.put("setCount", setCount);
        exerciseRecord.put("repCount", repCount);
        exerciseRecord.put("weight", weight);

        DocumentReference exerciseRecordRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay)
                .collection("musclegroups").document(muscleGroupTitle)
                .collection("exercises").document(exerciseTitle)
                .collection("exerciseData").document(exerciseDateCreated)
                .collection("exerciseRecords").document("set" + setCount);

        exerciseRecordRef.set(exerciseRecord).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                exerciseRepPopup.dismiss();
                exerciseRepPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        weights_spinner.setSelection(0);
                        recycler_view_toggle_layout.setVisibility(View.GONE);
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(Workout.this, "Failed", Toast.LENGTH_SHORT).show();
                exerciseRepPopup.dismiss();
                exerciseRepPopup.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        weights_spinner.setSelection(0);
                        recycler_view_toggle_layout.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    public void resetWeightSpinner() {
        exerciseRepPopup.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                weights_spinner.setSelection(0);
            }
        });
    }

    public void createRepCountButtons() {
        String[] repCountButtonsList = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "10",
                "11", "12", "13", "14", "15", "16", "17", "18", "19", "20",
                "21", "22", "23", "24", "25", "26", "27", "28", "29", "30"};

        for (int i = 0; i < repCountButtonsList.length; i++) {
            repCountButtonArray.add(new RepCountButtonsModel(repCountButtonsList[i]));
        }
    }

    public void setRepCountAdapter() {
        RepCountAdapter adapter = new RepCountAdapter(repCountButtonArray, this);
        rep_count_recycler_view.setAdapter(adapter);
        rep_count_recycler_view.setLayoutManager(new StaggeredGridLayoutManager(4, StaggeredGridLayoutManager.VERTICAL));
        rep_count_recycler_view.setHasFixedSize(true);
    }

    public void getSetsCount(String muscleGroupTitle, String exerciseTitle, String exerciseDateCreated) {
        set_count_view.setText("1");
        DocumentReference exerciseDataRef = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                .collection("exercises").document(exerciseTitle)
                .collection("exerciseData").document(exerciseDateCreated);
        exerciseDataRef.collection("exerciseRecords").orderBy("setCount").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            setsList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                setsList.add(document.getId());
                            }
                            if (setsList.size() >= 1) {
                                int setsCountInt = setsList.size();
                                int nextSetCountInt = Integer.sum(setsCountInt, 1);
                                String nextSetCount = Integer.toString(nextSetCountInt);
                                set_count_view.setText(nextSetCount);
                            }
                        }
                    }
                });
    }

    public void getRecordDate(String day, String month) {
        day_rep_recorded.setText(day.toUpperCase(Locale.ROOT));
        month_rep_recorded.setText(month.toUpperCase(Locale.ROOT));
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