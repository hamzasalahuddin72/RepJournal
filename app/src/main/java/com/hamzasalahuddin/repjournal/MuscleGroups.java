package com.hamzasalahuddin.repjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class MuscleGroups extends AppCompatActivity {
    CardView mg_clickable1, mg_clickable2, mg_clickable3, mg_clickable4, mg_clickable5, mg_clickable6, mg_clickable7, mg_clickable8;
    CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8;
    ConstraintLayout mg_container1, mg_container2, mg_container3, mg_container4, mg_container5, mg_container6, mg_container7, mg_container8;
    ImageView shoulders_image, chest_image, biceps_image, triceps_image, back_image, abdomen_image, legs_image, rest_image;
    TextView mg_textview1, mg_textview2, mg_textview3, mg_textview4, mg_textview5, mg_textview6, mg_textview7, mg_textview8;
    Button select_mgs_btn;
    ImageButton back_button;
    CustomProgressDialog dialog;
    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    FirebaseFirestore firebaseFirestore;
    String date;
    int black;
    int white;
    int navy;

    @SuppressLint("ResourceAsColor")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_musclegroups);

        select_mgs_btn = findViewById(R.id.select_mgs_btn);
        back_button = findViewById(R.id.back_button);
        mg_clickable1 = findViewById(R.id.mg_clickable1);
        mg_clickable2 = findViewById(R.id.mg_clickable2);
        mg_clickable3 = findViewById(R.id.mg_clickable3);
        mg_clickable4 = findViewById(R.id.mg_clickable4);
        mg_clickable5 = findViewById(R.id.mg_clickable5);
        mg_clickable6 = findViewById(R.id.mg_clickable6);
        mg_clickable7 = findViewById(R.id.mg_clickable7);
        mg_clickable8 = findViewById(R.id.mg_clickable8);
        checkBox1 = findViewById(R.id.checkBox1);
        checkBox2 = findViewById(R.id.checkBox2);
        checkBox3 = findViewById(R.id.checkBox3);
        checkBox4 = findViewById(R.id.checkBox4);
        checkBox5 = findViewById(R.id.checkBox5);
        checkBox6 = findViewById(R.id.checkBox6);
        checkBox7 = findViewById(R.id.checkBox7);
        checkBox8 = findViewById(R.id.checkBox8);
        mg_container1 = findViewById(R.id.mg_container1);
        mg_container2 = findViewById(R.id.mg_container2);
        mg_container3 = findViewById(R.id.mg_container3);
        mg_container4 = findViewById(R.id.mg_container4);
        mg_container5 = findViewById(R.id.mg_container5);
        mg_container6 = findViewById(R.id.mg_container6);
        mg_container7 = findViewById(R.id.mg_container7);
        mg_container8 = findViewById(R.id.mg_container8);
        mg_textview1 = findViewById(R.id.mg_textview1);
        mg_textview2 = findViewById(R.id.mg_textview2);
        mg_textview3 = findViewById(R.id.mg_textview3);
        mg_textview4 = findViewById(R.id.mg_textview4);
        mg_textview5 = findViewById(R.id.mg_textview5);
        mg_textview6 = findViewById(R.id.mg_textview6);
        mg_textview7 = findViewById(R.id.mg_textview7);
        mg_textview8 = findViewById(R.id.mg_textview8);
        shoulders_image = findViewById(R.id.shoulders_image);
        chest_image = findViewById(R.id.chest_image);
        biceps_image = findViewById(R.id.biceps_image);
        triceps_image = findViewById(R.id.triceps_image);
        back_image = findViewById(R.id.back_image);
        abdomen_image = findViewById(R.id.abdomen_image);
        legs_image = findViewById(R.id.legs_image);
        rest_image = findViewById(R.id.rest_image);

        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        String phaseId = getIntent().getExtras().getString("phaseId");
        String workoutDay = getIntent().getExtras().getString("workoutDay");
        date = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        black = Color.parseColor("#FF000000");
        white = Color.parseColor("#FFFFFFFF");
        navy = Color.parseColor("#0B096A");

        back_button.setOnClickListener(v -> onBackPressed());

        mg_clickable1.setOnClickListener(v -> {
            toggleAppearance(checkBox1, shoulders_image, mg_textview1, mg_container1);
        });
        mg_clickable2.setOnClickListener(v -> {
            toggleAppearance(checkBox2, chest_image, mg_textview2, mg_container2);
        });
        mg_clickable3.setOnClickListener(v -> {
            toggleAppearance(checkBox3, biceps_image, mg_textview3, mg_container3);
        });
        mg_clickable4.setOnClickListener(v -> {
            toggleAppearance(checkBox4, triceps_image, mg_textview4, mg_container4);
        });
        mg_clickable5.setOnClickListener(v -> {
            toggleAppearance(checkBox5, back_image, mg_textview5, mg_container5);
        });
        mg_clickable6.setOnClickListener(v -> {
            toggleAppearance(checkBox6, abdomen_image, mg_textview6, mg_container6);
        });
        mg_clickable7.setOnClickListener(v -> {
            toggleAppearance(checkBox7, legs_image, mg_textview7, mg_container7);
        });
        mg_clickable8.setOnClickListener(v -> {
            toggleAppearance(checkBox8, rest_image, mg_textview8, mg_container8);

            int[] cardviewArray = {R.id.mg_clickable1, R.id.mg_clickable2, R.id.mg_clickable3,
                    R.id.mg_clickable4, R.id.mg_clickable5, R.id.mg_clickable6, R.id.mg_clickable7};
            int[] checkboxArray = {R.id.checkBox1, R.id.checkBox2, R.id.checkBox3,
                    R.id.checkBox4, R.id.checkBox5, R.id.checkBox6, R.id.checkBox7};
            int[]  mg_textviewsArray = {R.id.mg_textview1, R.id.mg_textview2, R.id.mg_textview3,
                    R.id.mg_textview4, R.id.mg_textview5, R.id.mg_textview6,
                    R.id.mg_textview7, R.id.mg_textview8};
            int[]  mg_imagesArray = {R.id.shoulders_image, R.id.chest_image, R.id.biceps_image,
                    R.id.triceps_image, R.id.back_image, R.id.abdomen_image, R.id.legs_image, R.id.rest_image};
            int[]  mg_containersArray = {R.id.mg_container1, R.id.mg_container2, R.id.mg_container3,
                    R.id.mg_container4, R.id.mg_container5, R.id.mg_container6,
                    R.id.mg_container7, R.id.mg_container8};

            if (checkBox8.isChecked()) {
                for (int i = 0; i < cardviewArray.length; i++) {
                    CardView cardView = findViewById(cardviewArray[i]);
                    CheckBox checkBox = findViewById(checkboxArray[i]);
                    ImageView imageView = findViewById(mg_imagesArray[i]);
                    TextView textView = findViewById(mg_textviewsArray[i]);
                    ConstraintLayout constraintLayout = findViewById(mg_containersArray[i]);

                    cardView.setEnabled(false);
                    checkBox.setChecked(false);

                    imageView.setColorFilter(black);
                    textView.setTextColor(navy);
                    constraintLayout.setBackgroundResource(R.drawable.mg_container_style_notchecked);
                    checkBox.setChecked(false);
                }
            } else {
                for (int i = 0; i < cardviewArray.length; i++) {
                    CardView cardView = findViewById(cardviewArray[i]);
                    cardView.setEnabled(true);
                }
            }
        });

        select_mgs_btn.setOnClickListener(v -> {
            if (checkBox1.isChecked()) {
                addMuscleGroups("shoulders");
                finish();
            } if (checkBox2.isChecked()) {
                addMuscleGroups("chest");
                finish();
            } if (checkBox3.isChecked()) {
                addMuscleGroups("biceps");
                finish();
            } if (checkBox4.isChecked()) {
                addMuscleGroups("triceps");
                finish();
            } if (checkBox5.isChecked()) {
                addMuscleGroups("back");
                finish();
            } if (checkBox6.isChecked()) {
                addMuscleGroups("abdomen");
                finish();
            } if (checkBox7.isChecked()) {
                addMuscleGroups("legs");
                finish();
            } if (checkBox8.isChecked()) {
                addMuscleGroups("rest");
                finish();
            }
        });
    }

    private void toggleAppearance(CheckBox mg_checkbox_parameter, ImageView mg_image_parameter,
                                  TextView mg_textview_parameter, ConstraintLayout mg_container_parameter) {

        if (mg_checkbox_parameter.isChecked()) {
            mg_image_parameter.setColorFilter(black);
            mg_textview_parameter.setTextColor(navy);
            mg_container_parameter.setBackgroundResource(R.drawable.mg_container_style_notchecked);
            mg_checkbox_parameter.setChecked(false);
        } else {
            mg_image_parameter.setColorFilter(white);
            mg_textview_parameter.setTextColor(white);
            mg_container_parameter.setBackgroundResource(R.drawable.mg_container_style_checked);
            mg_checkbox_parameter.setChecked(true);
        }
    }

    private void addMuscleGroups(String muscleGroup) {
        String phaseId = getIntent().getExtras().getString("phaseId");
        String workoutDay = getIntent().getExtras().getString("workoutDay");

        Map<String,Object> musclegroups = new HashMap<>();
        musclegroups.put("muscleGroupTitle", muscleGroup);
        musclegroups.put("phaseId", phaseId);
        musclegroups.put("workoutDay", workoutDay);
        musclegroups.put("dateModified", date);

        DocumentReference phaseRef = firebaseFirestore.collection("users")
                .document(firebaseUser.getUid())
                .collection("userdata").document(phaseId);
        DocumentReference calendarRef = phaseRef.collection("calendar").document(workoutDay);
        DocumentReference mgRef = calendarRef.collection("musclegroups").document(muscleGroup);

        mgRef.set(musclegroups).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(MuscleGroups.this, "Muscle groups added for " + workoutDay, Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MuscleGroups.this, "Failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}