package com.hamzasalahuddin.repjournal;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
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
import com.hamzasalahuddin.repjournal.recyclerviews.PhasesModel;

import java.util.ArrayList;
import java.util.List;

public class PhasesFragment extends Fragment {
    Button add_phase_button;
//    ImageButton back_button;
    CustomProgressDialog dialog;
    RelativeLayout relative_layout_phases;
    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseFirestore firebaseFirestore;
    DocumentReference root;
    RecyclerView recyclerView;
    FirestoreRecyclerAdapter<PhasesModel, PhasesViewHolder> adapter;
    List<String> listWd;
    List<String> listMg;
    List<String> listEx;
    List<String> exDList;
    List<String> exRecList;
    InputDialog inputDialog;
    OptionsMenu optionsMenu;

    public PhasesFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_phases, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        add_phase_button = view.findViewById(R.id.add_phase_button);
//        back_button = view.findViewById(R.id.back_button);
        dialog = new CustomProgressDialog(getActivity());
        relative_layout_phases = view.findViewById(R.id.relative_layout_phases);
        recyclerView = view.findViewById(R.id.recycler_view);

        inputDialog = new InputDialog(getActivity());
        optionsMenu = new OptionsMenu(getActivity());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        firebaseFirestore = FirebaseFirestore.getInstance();

        root = firebaseFirestore.collection("users").document(user.getUid());

        add_phase_button.setOnClickListener(v -> {
            inputDialog.show();
            inputDialog.new_title_input.setHint("Phase title");
            inputDialog.proceed_exercise_add_button.setOnClickListener(v1 -> {
                inputDialog.createPhase();
            });
        });

//        back_button.setOnClickListener(v -> {
//            requireActivity().onBackPressed();
//        });

        inputDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                inputDialog.new_title_input.setText("");
            }
        });

        Query query = root.collection("userdata")
                .orderBy("datePhaseCreated", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PhasesModel> options = new FirestoreRecyclerOptions.Builder<PhasesModel>()
                .setQuery(query, PhasesModel.class).build();
        adapter = new FirestoreRecyclerAdapter<PhasesModel, PhasesViewHolder>(options) {
            @NonNull
            @Override
            public PhasesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.phase_item, parent, false);
                return new PhasesViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PhasesViewHolder holder, int i, @NonNull PhasesModel model) {
                String ref = adapter.getSnapshots().getSnapshot(i).getId();

                holder.phase_title.setText(model.getPhaseTitle());
                holder.day_phase_created.setText(model.getDayPhaseCreated());
                holder.month_phase_created.setText(model.getMonthPhaseCreated());
                holder.itemView.setOnLongClickListener(v -> {
                    optionsMenu.show();
                    optionsMenu.delete_element_button.setText("DELETE " + ref);
                    optionsMenu.edit_element_button.setText("EDIT " + ref);
                    optionsMenu.delete_element_button.setOnClickListener(v1 -> {
                        deletePhaseDocs(ref);
                        optionsMenu.cancel();
                    });
                    optionsMenu.edit_element_button.setOnClickListener(v1 -> {
                        Toast.makeText(getContext(), "Edit phase", Toast.LENGTH_SHORT).show();
                        optionsMenu.cancel();
                    });
                    return true;
                });
                holder.itemView.setOnClickListener(v -> {
                    Bundle bundle = new Bundle();
                    bundle.putString("phaseId", ref);
                    Fragment fragment = new WeekdaysFragment();
                    fragment.setArguments(bundle);
                    getFragmentManager()
                            .beginTransaction().addToBackStack(null)
                            .replace(R.id.main_container, fragment)
                            .commit();
                });
            }
        };

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
}

    public class PhasesViewHolder extends RecyclerView.ViewHolder {
        private TextView phase_title, day_phase_created, month_phase_created;

        public PhasesViewHolder(@NonNull View itemView) {
            super(itemView);

            phase_title = itemView.findViewById(R.id.phase_title);
            day_phase_created = itemView.findViewById(R.id.day_phase_created);
            month_phase_created = itemView.findViewById(R.id.month_phase_created);
        }
    }

    public void deletePhaseDocs(String phaseId) {
        dialog.show();
        DocumentReference phaseRef = root.collection("userdata").document(phaseId);
        phaseRef.collection("calendar").orderBy("dayTitle").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            listWd = new ArrayList<>();
                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                listWd.add(queryDocumentSnapshot.getId());
                            }

                            if (listWd.isEmpty()) {
                                deletePhase(phaseId);
                            } else {
                                String[] weeklist = listWd.toArray(new String[0]);
                                for (int i = 0; i < weeklist.length; i++) {
                                    int finalI = i;
                                    phaseRef.collection("calendar").document(weeklist[i])
                                            .collection("musclegroups").orderBy("muscleGroupTitle").get()
                                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        listMg = new ArrayList<>();
                                                        for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                                            listMg.add(queryDocumentSnapshot.getId());
                                                        }

                                                        if (listMg.isEmpty()) {
                                                            phaseRef.collection("calendar").document(weeklist[finalI])
                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                    if (task.isSuccessful()) {
                                                                        deletePhase(phaseId);
                                                                    } else {
                                                                        Toast.makeText(getActivity(), "Failed to delete calendar", Toast.LENGTH_SHORT).show();
                                                                    }
                                                                }
                                                            });
                                                        } else {
                                                            String[] mglist = listMg.toArray(new String[0]);
                                                            for (int j = 0; j < mglist.length; j++) {
                                                                int finalJ = j;
                                                                phaseRef.collection("calendar").document(weeklist[finalI])
                                                                        .collection("musclegroups").document(mglist[j])
                                                                        .collection("exercises").orderBy("exerciseTitle").get()
                                                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                            @Override
                                                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                                                if (task.isSuccessful()) {
                                                                                    listEx = new ArrayList<>();
                                                                                    for (QueryDocumentSnapshot queryDocumentSnapshot1 : task.getResult()) {
                                                                                        listEx.add(queryDocumentSnapshot1.getId());
                                                                                    }

                                                                                    if (listEx.isEmpty()) {
                                                                                        phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                .collection("musclegroups").document(mglist[finalJ])
                                                                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                        @Override
                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                            if (task.isSuccessful()) {
                                                                                                                deletePhase(phaseId);
                                                                                                            } else {
                                                                                                                Toast.makeText(getActivity(), "Failed to delete calendar", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                                } else {
                                                                                                    Toast.makeText(getActivity(), "Failed to delete muscle groups", Toast.LENGTH_SHORT).show();
                                                                                                }
                                                                                            }
                                                                                        });
                                                                                    } else {
                                                                                        String[] exerciseList = listEx.toArray(new String[0]);
                                                                                        for (int k = 0; k < exerciseList.length; k++) {
                                                                                            int finalK = k;
                                                                                            phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                    .collection("musclegroups").document(mglist[finalJ])
                                                                                                    .collection("exercises").document(exerciseList[k])
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
                                                                                                                    phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                            .collection("musclegroups").document(mglist[finalJ])
                                                                                                                            .collection("exercises").document(exerciseList[finalK])
                                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                        @Override
                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                        .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                    @Override
                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                            phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                @Override
                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                        deletePhase(phaseId);
                                                                                                                                                    } else {
                                                                                                                                                        Toast.makeText(getActivity(), "Failed to delete calendar", Toast.LENGTH_SHORT).show();
                                                                                                                                                    }
                                                                                                                                                }
                                                                                                                                            });
                                                                                                                                        } else {
                                                                                                                                            Toast.makeText(getActivity(), "Failed to delete muscle groups", Toast.LENGTH_SHORT).show();
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });
                                                                                                                            } else {
                                                                                                                                Toast.makeText(getActivity(), "Failed to delete exercises", Toast.LENGTH_SHORT).show();
                                                                                                                            }
                                                                                                                        }
                                                                                                                    });
                                                                                                                } else {
                                                                                                                    String[] exerciseDataList = exDList.toArray(new String[0]);
                                                                                                                    for (int l = 0; l < exerciseDataList.length; l++) {
                                                                                                                        int finalL = l;
                                                                                                                        phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                .collection("exercises").document(exerciseList[finalK])
                                                                                                                                .collection("exerciseData").document(exerciseDataList[l])
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
                                                                                                                                                phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                        .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                        .collection("exercises").document(exerciseList[finalK])
                                                                                                                                                        .collection("exerciseData").document(exerciseDataList[finalL])
                                                                                                                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                                            phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                    .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                                    .collection("exercises").document(exerciseList[finalK])
                                                                                                                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                @Override
                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                                        phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                                .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                            @Override
                                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                                                                    phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                        @Override
                                                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                                                                deletePhase(phaseId);
                                                                                                                                                                                            } else {
                                                                                                                                                                                                Toast.makeText(getActivity(), "Failed to delete calendar", Toast.LENGTH_SHORT).show();
                                                                                                                                                                                            }
                                                                                                                                                                                        }
                                                                                                                                                                                    });
                                                                                                                                                                                } else {
                                                                                                                                                                                    Toast.makeText(getActivity(), "Failed to delete muscle groups", Toast.LENGTH_SHORT).show();                                                                                                                                                                                }
                                                                                                                                                                            }
                                                                                                                                                                        });
                                                                                                                                                                    } else {
                                                                                                                                                                        Toast.makeText(getActivity(), "Failed to delete exercises", Toast.LENGTH_SHORT).show();
                                                                                                                                                                    }
                                                                                                                                                                }
                                                                                                                                                            });
                                                                                                                                                        } else {
                                                                                                                                                            Toast.makeText(getActivity(), "Failed to delete exercise data", Toast.LENGTH_SHORT).show();
                                                                                                                                                        }
                                                                                                                                                    }
                                                                                                                                                });
                                                                                                                                            } else {
                                                                                                                                                String[] exerciseRecordsList = exRecList.toArray(new String[0]);
                                                                                                                                                for (int m = 0; m < exerciseRecordsList.length; m++) {
                                                                                                                                                    phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                            .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                            .collection("exercises").document(exerciseList[finalK])
                                                                                                                                                            .collection("exerciseData").document(exerciseDataList[finalL])
                                                                                                                                                            .collection("exerciseRecords").document(exerciseRecordsList[m])
                                                                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                        @Override
                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                                phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                        .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                                        .collection("exercises").document(exerciseList[finalK])
                                                                                                                                                                        .collection("exerciseData").document(exerciseDataList[finalL])
                                                                                                                                                                        .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                        if (task.isSuccessful()) {
                                                                                                                                                                            phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                                    .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                                                    .collection("exercises").document(exerciseList[finalK])
                                                                                                                                                                                    .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                @Override
                                                                                                                                                                                public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                    if (task.isSuccessful()) {
                                                                                                                                                                                        phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                                                .collection("musclegroups").document(mglist[finalJ])
                                                                                                                                                                                                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                            @Override
                                                                                                                                                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                if (task.isSuccessful()) {
                                                                                                                                                                                                    phaseRef.collection("calendar").document(weeklist[finalI])
                                                                                                                                                                                                            .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                                                                                                                                                        @Override
                                                                                                                                                                                                        public void onComplete(@NonNull Task<Void> task) {
                                                                                                                                                                                                            if (task.isSuccessful()) {
                                                                                                                                                                                                                deletePhase(phaseId);
                                                                                                                                                                                                            }
                                                                                                                                                                                                        }
                                                                                                                                                                                                    });
                                                                                                                                                                                                } else {
                                                                                                                                                                                                    Toast.makeText(getActivity(), "Failed to delete calendar", Toast.LENGTH_SHORT).show();
                                                                                                                                                                                                }
                                                                                                                                                                                            }
                                                                                                                                                                                        });
                                                                                                                                                                                    } else {
                                                                                                                                                                                        Toast.makeText(getActivity(), "Failed to delete muscle group", Toast.LENGTH_SHORT).show();
                                                                                                                                                                                    }
                                                                                                                                                                                }
                                                                                                                                                                            });
                                                                                                                                                                        } else {
                                                                                                                                                                            Toast.makeText(getActivity(), "Failed to delete exercises", Toast.LENGTH_SHORT).show();
                                                                                                                                                                        }
                                                                                                                                                                    }
                                                                                                                                                                });
                                                                                                                                                            } else {
                                                                                                                                                                Toast.makeText(getActivity(), "Failed to delete exercises data", Toast.LENGTH_SHORT).show();
                                                                                                                                                            }
                                                                                                                                                        }
                                                                                                                                                    });
                                                                                                                                                }
                                                                                                                                            }
                                                                                                                                        } else {
                                                                                                                                            Toast.makeText(getActivity(), "Task 3 failed", Toast.LENGTH_SHORT).show();
                                                                                                                                        }
                                                                                                                                    }
                                                                                                                                });
                                                                                                                    }
                                                                                                                }
                                                                                                            } else {
                                                                                                                Toast.makeText(getActivity(), "Task 2 failed", Toast.LENGTH_SHORT).show();
                                                                                                            }
                                                                                                        }
                                                                                                    });
                                                                                        }
                                                                                    }
                                                                                } else {
                                                                                    Toast.makeText(getActivity(), "Task 3 failed", Toast.LENGTH_SHORT).show();
                                                                                }
                                                                            }
                                                                        });
                                                            }
                                                        }
                                                    } else {
                                                        Toast.makeText(getActivity(), "Task 2 failed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                }
                            }
                        } else {
                            Toast.makeText(getActivity(), "Task 1 failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void deletePhase(String phaseId) {
        DocumentReference phaseRef = root.collection("userdata").document(phaseId);
        phaseRef.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getActivity(), "Phase deleted", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Failed to delete phase", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
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