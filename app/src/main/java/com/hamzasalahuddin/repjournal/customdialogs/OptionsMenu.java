package com.hamzasalahuddin.repjournal.customdialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hamzasalahuddin.repjournal.History;
import com.hamzasalahuddin.repjournal.R;
import com.hamzasalahuddin.repjournal.Workout;

import java.util.ArrayList;
import java.util.List;

public class OptionsMenu extends Dialog {
    public Button delete_element_button, edit_element_button;
    public CustomProgressDialog dialog;
    public List<String> exDList;
    public List<String> exList;
    public List<String> exRecList;

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    DocumentReference root;
    DocumentReference weekdayRef;
    FirebaseUser user;

    public OptionsMenu(@NonNull Context context) {
        super(context);

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.BOTTOM;
        params.windowAnimations = android.R.style.Animation_InputMethod;
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        getWindow().setAttributes(params);
        setTitle(null);
        setCancelable(true);
        setOnCancelListener(null);
        View view = LayoutInflater.from(context).inflate(R.layout.options_menu, null);
        setContentView(view);

        delete_element_button = findViewById(R.id.delete_element_button);
        edit_element_button = findViewById(R.id.edit_element_button);
        dialog = new CustomProgressDialog(getContext());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        root = firebaseFirestore.collection("users")
                .document(user.getUid());

    }


    public void deleteExercise(String phaseId, String workoutDay, String muscleGroupTitle, String exerciseTitle) {
        dialog.show();
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);
        DocumentReference exerciseRef = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                .collection("exercises").document(exerciseTitle);

        exerciseRef.collection("exerciseData").orderBy("exerciseDataCreated").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            exDList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                exDList.add(document.getId());
                            }

                            if (exDList.isEmpty()) {
                                exerciseRef.delete().addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(), "Failed to delete exercise", Toast.LENGTH_SHORT).show();
                                        dialog.dismiss();
                                    }
                                });
                                dialog.dismiss();
                                cancel();
                            } else {
                                String[] exerciseDataList = exDList.toArray(new String[0]);
                                for (int i = 0; i < exerciseDataList.length; i++) {
                                    int finalI = i;
                                    exerciseRef.collection("exerciseData").document(exerciseDataList[i])
                                            .collection("exerciseRecords").orderBy("setCount").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        exRecList = new ArrayList<>();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            exRecList.add(document.getId());
                                                        }

                                                        if (exRecList.isEmpty()) {
                                                            exerciseRef.collection("exerciseData").document(exerciseDataList[finalI])
                                                                    .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                @Override
                                                                public void onSuccess(Void unused) {
                                                                    exerciseRef.delete().addOnFailureListener(new OnFailureListener() {
                                                                        @Override
                                                                        public void onFailure(@NonNull Exception e) {
                                                                            Toast.makeText(getContext(), "Failed to delete exercise", Toast.LENGTH_SHORT).show();
                                                                            dialog.dismiss();
                                                                        }
                                                                    });
                                                                    dialog.dismiss();
                                                                    cancel();
                                                                }
                                                            });
                                                        } else {
                                                            String[] exerciseRecordsList = exRecList.toArray(new String[0]);
                                                            for (int j = 0; j < exerciseRecordsList.length; j++) {
                                                                exerciseRef.collection("exerciseData").document(exerciseDataList[finalI])
                                                                        .collection("exerciseRecords").document(exerciseRecordsList[j])
                                                                        .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                    @Override
                                                                    public void onSuccess(Void unused) {
                                                                        exerciseRef.collection("exerciseData").document(exerciseDataList[finalI])
                                                                                .delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                                            @Override
                                                                            public void onSuccess(Void unused) {
                                                                                exerciseRef.delete().addOnFailureListener(new OnFailureListener() {
                                                                                    @Override
                                                                                    public void onFailure(@NonNull Exception e) {
                                                                                        Toast.makeText(getContext(), "Failed to delete exercise", Toast.LENGTH_SHORT).show();
                                                                                        dialog.dismiss();
                                                                                    }
                                                                                });
                                                                                dialog.dismiss();
                                                                                cancel();
                                                                            }
                                                                        });
                                                                        dialog.dismiss();
                                                                        cancel();
                                                                    }
                                                                });
                                                            }
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Task 2 failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                    dialog.dismiss();
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Task 1 failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deleteMuscleGroupDocs(String phaseId, String workoutDay, String muscleGroupTitle) {
        dialog.show();
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);
        DocumentReference muscleGroupRef = weekdayRef.collection("musclegroups").document(muscleGroupTitle);
        muscleGroupRef.collection("exercises").orderBy("exerciseTitle").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            exList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                exList.add(document.getId());
                            }

                            if (exList.isEmpty()) {
                                deleteMuscleGroup(phaseId, workoutDay, muscleGroupTitle);
                            } else {
                                String[] exerciseList = exList.toArray(new String[0]);
                                for (int i = 0; i < exerciseList.length; i++) {
                                    int finalI = i;
                                    muscleGroupRef.collection("exercises").document(exerciseList[i])
                                            .collection("exerciseData").orderBy("exerciseDataCreated").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        exDList = new ArrayList<>();
                                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                                            exDList.add(document.getId());
                                                        }

                                                        if (exDList.isEmpty()) {
                                                            muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        deleteMuscleGroup(phaseId, workoutDay, muscleGroupTitle);
                                                                    } else {
                                                                        Toast.makeText(getContext(), "Failed to delete exercises", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            String[] exerciseDataList = exDList.toArray(new String[0]);
                                                            for (int j = 0; j < exerciseDataList.length; j++) {
                                                                int finalJ = j;
                                                                muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                        .collection("exerciseData").document(exerciseDataList[j])
                                                                        .collection("exerciseRecords").orderBy("setCount").get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    exRecList = new ArrayList<>();
                                                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                                                        exRecList.add(document.getId());
                                                                                    }

                                                                                    if (exRecList.isEmpty()) {
                                                                                        muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                                                .collection("exerciseData").document(exerciseDataList[finalJ])
                                                                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                deleteMuscleGroup(phaseId, workoutDay, muscleGroupTitle);
                                                                                                            } else {
                                                                                                                Toast.makeText(getContext(), "Failed to delete exercises", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                } else {
                                                                                                    Toast.makeText(getContext(), "Failed to delete exercise data", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        String[] exerciseRecordsList = exRecList.toArray(new String[0]);
                                                                                        for (int k = 0; k < exerciseRecordsList.length; k++) {
                                                                                            muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                                                    .collection("exerciseData").document(exerciseDataList[finalJ])
                                                                                                    .collection("exerciseRecords").document(exerciseRecordsList[k])
                                                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                @Override
                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                    if (task.isSuccessful()) {
                                                                                                        muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                                                                .collection("exerciseData").document(exerciseDataList[finalJ])
                                                                                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                            @Override
                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                if (task.isSuccessful()) {
                                                                                                                    muscleGroupRef.collection("exercises").document(exerciseList[finalI])
                                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                deleteMuscleGroup(phaseId, workoutDay, muscleGroupTitle);
                                                                                                                            } else {
                                                                                                                                Toast.makeText(getContext(), "Failed to delete exercises", Toast.LENGTH_SHORT).show();
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                } else {
                                                                                                                    Toast.makeText(getContext(), "Failed to delete exercises data", Toast.LENGTH_SHORT).show();
                                                                                                                }
                                                                                                            }
                                                                                                        });
                                                                                                    } else {
                                                                                                        Toast.makeText(getContext(), "Failed to delete exercises records", Toast.LENGTH_SHORT).show();
                                                                                                    }
                                                                                                }
                                                                                            });
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    Toast.makeText(getContext(), "Task 3 failed", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    } else {
                                                        Toast.makeText(getContext(), "Task 2 failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(getContext(), "Task 1 failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deleteMuscleGroup(String phaseId, String workoutDay, String muscleGroupTitle) {
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);
        DocumentReference muscleGroupRef = weekdayRef.collection("musclegroups").document(muscleGroupTitle);
        muscleGroupRef.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(getContext(), muscleGroupTitle + " deleted", Toast.LENGTH_SHORT).show();
                    cancel();
                } else {
                    Toast.makeText(getContext(), "Failed to delete muscle group", Toast.LENGTH_SHORT).show();
                }
                dialog.dismiss();
            }
        });
    }

    public void deleteExerciseData(String phaseId, String workoutDay, String muscleGroupTitle, String exerciseTitle, String exerciseDateCreated) {
        dialog.show();
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);
        DocumentReference exerciseDataRef = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                .collection("exercises").document(exerciseTitle)
                .collection("exerciseData").document(exerciseDateCreated);
        exerciseDataRef.collection("exerciseRecords").orderBy("setCount")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    exRecList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        exRecList.add(document.getId());
                    }

                    if (exRecList.isEmpty()) {
                        exerciseDataRef.delete().addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(getContext(), "Failed to delete exercise data", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                        dialog.dismiss();
                        cancel();
                    } else {
                        String[] exerciseRecordsList = exRecList.toArray(new String[0]);
                        for (int i = 0; i < exerciseRecordsList.length; i++) {
                            exerciseDataRef.collection("exerciseRecords").document(exerciseRecordsList[i])
                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        exerciseDataRef.delete().addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(getContext(), "Failed to delete exercise data", Toast.LENGTH_SHORT).show();
                                                dialog.dismiss();
                                            }
                                        });
                                        dialog.dismiss();
                                        cancel();
                                    } else {
                                        Toast.makeText(getContext(), "Failed to delete exercise records", Toast.LENGTH_SHORT).show();
                                    }
                                    dialog.dismiss();
                                }
                            });
                        }
                    }
                    dialog.dismiss();
                    cancel();
                } else {
                    Toast.makeText(getContext(), "Task 1 failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void deleteSet(String phaseId, String workoutDay, String muscleGroupTitle, String exerciseTitle, String exerciseDateCreated, String setCountId) {
        dialog.show();
        weekdayRef = root.collection("userdata").document(phaseId)
                .collection("calendar").document(workoutDay);
        DocumentReference exerciseDateRef = weekdayRef.collection("musclegroups").document(muscleGroupTitle)
                .collection("exercises").document(exerciseTitle)
                .collection("exerciseData").document(exerciseDateCreated);
        DocumentReference exerciseRecordRef = exerciseDateRef.collection("exerciseRecords").document("set" + setCountId);

        exerciseRecordRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(getContext(), "Set deleted", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                cancel();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getContext(), "Failed", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                cancel();
            }
        });
    }
}
