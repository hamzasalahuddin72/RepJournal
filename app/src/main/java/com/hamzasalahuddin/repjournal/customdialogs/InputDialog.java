package com.hamzasalahuddin.repjournal.customdialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hamzasalahuddin.repjournal.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class InputDialog extends Dialog {
    public EditText new_title_input;
    public Button proceed_exercise_add_button, cancel_exercise_add_button;
    CustomProgressDialog dialog;
    DocumentReference root;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser user;
    String date;
    SimpleDateFormat dayFormat;
    SimpleDateFormat monthFormat;
    Date d1;
    Date d2;
    String day;
    String month;

    public InputDialog(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.windowAnimations = android.R.style.Animation_InputMethod;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setAttributes(params);
        setTitle(null);
        setCancelable(true);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.input_dialog, null);
        setContentView(view);

        date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        dayFormat = new SimpleDateFormat("dd");
        monthFormat = new SimpleDateFormat("MMMM");
        d1 = new Date();
        d2 = new Date();
        day = dayFormat.format(d1);
        month = monthFormat.format(d2);

        new_title_input = findViewById(R.id.new_title_input);
        proceed_exercise_add_button = findViewById(R.id.proceed_exercise_add_button);
        cancel_exercise_add_button = findViewById(R.id.cancel_exercise_add_button);
        dialog = new CustomProgressDialog(getContext());

        firebaseFirestore = FirebaseFirestore.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        root = firebaseFirestore.collection("users")
                .document(user.getUid());

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        showKeypad();

        cancel_exercise_add_button.setOnClickListener(v -> {
            cancel();
        });
    }

    public void showKeypad() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(new_title_input.getRootView(), InputMethodManager.SHOW_IMPLICIT);
        new_title_input.requestFocus();
    }

    public void addExercises(String phaseId, String workoutDay, String muscleGroupTitle) {
        String title_input = new_title_input.getText().toString().trim();
        if (title_input.isEmpty()) {
            new_title_input.setError("Please enter the exercise title");
        } else {
            dialog.show();
            Map<String, Object> exercises = new HashMap<>();
            exercises.put("exerciseTitle", title_input);
            exercises.put("dateModified", date);

            DocumentReference phaseRef = root.collection("userdata").document(phaseId);
            DocumentReference calendarRef = phaseRef.collection("calendar").document(workoutDay);
            DocumentReference mgRef = calendarRef.collection("musclegroups").document(muscleGroupTitle);
            DocumentReference exercisesRef = mgRef.collection("exercises").document(title_input);

            exercisesRef.set(exercises).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(getContext(), "Exercise added", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    cancel();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    showKeypad();
                }
            });
        }
    }

    public void createPhase() {
        dialog.show();
        String title_input = new_title_input.getText().toString().trim();

        if (title_input.isEmpty()) {
            new_title_input.setError("Please enter a phase title");
            dialog.dismiss();
        } else {
            cancel();
            Map<String,Object> phases = new HashMap<>();
            phases.put("phaseTitle", title_input);
            phases.put("dayPhaseCreated", day);
            phases.put("monthPhaseCreated", month);
            phases.put("datePhaseCreated", date);

            DocumentReference phaseRef = firebaseFirestore.collection("users")
                    .document(user.getUid()).collection("userdata").document(title_input.toLowerCase(Locale.ROOT));
            phaseRef.set(phases).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    calendar(title_input);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    show();
                }
            });
        }
    }

    public void calendar(String phaseTitle) {
        String[] weekDays = {"monday", "tuesday", "wednesday", "thursday", "friday", "saturday", "sunday"};

        for (int i = 0; i < weekDays.length; i++) {
            Map<String, Object> days = new HashMap<>();
            days.put("dayTitle", weekDays[i]);

            DocumentReference phaseRef = firebaseFirestore.collection("users")
                    .document(user.getUid()).collection("userdata").document(phaseTitle);
            DocumentReference weekdaysRef = phaseRef.collection("calendar").document(weekDays[i]);
            weekdaysRef.set(days).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    dialog.dismiss();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    show();
                }
            });
        }
    }
}
