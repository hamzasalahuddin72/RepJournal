package com.hamzasalahuddin.repjournal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class ProfileFragment extends Fragment {
    private Context context;
    private static final int GALLERY_REQUEST = 1889;
    CardView logout_button;
    TextView my_account_name, my_account_email;
    ImageButton settings_button, user_profile_pic, back_button;
    ImageView profile_pic_bg;
    CustomProgressDialog dialog;
    FirebaseFirestore firebaseFirestore;
    FirebaseAuth auth;
    StorageReference storageReference;
    OnBackPressedCallback onBackPressedCallback;
    MotionLayout motionLayout;
    Handler handler = new Handler();

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance() {
        ProfileFragment fragment = new ProfileFragment();
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
        context = container.getContext();
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        logout_button = view.findViewById(R.id.logout_button);
        my_account_name = view.findViewById(R.id.my_account_name);
        my_account_email = view.findViewById(R.id.my_account_email);
        settings_button = view.findViewById(R.id.settings_button);
        user_profile_pic = view.findViewById(R.id.user_profile_pic);
        profile_pic_bg = view.findViewById(R.id.profile_pic_bg);
        motionLayout = view.findViewById(R.id.profile_motion_layout);
        back_button = view.findViewById(R.id.back_button);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        dialog = new CustomProgressDialog(getActivity());
        dialog.show();

        logout_button.setOnClickListener(v -> {
            dialog.show();
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            FirebaseAuth.getInstance().signOut();
                            backToMainActivity();
                            dialog.dismiss();
                        }
                    },
                    1500);
        });

        back_button.setOnClickListener(v -> {
            requireActivity().onBackPressed();
        });

        settings_button.setOnClickListener(v -> openSettings());

        Task<DocumentSnapshot> reference = firebaseFirestore.collection("users")
                .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .collection("credentials").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            String email = documentSnapshot.getString("email");
                            String firstname = documentSnapshot.getString("firstname");
                            String lastname = documentSnapshot.getString("lastname");
                            String accountName = firstname + " " + lastname;
                            my_account_email.setText(email);
                            my_account_name.setText(accountName);
                        }
                    }
                });

        user_profile_pic.setOnClickListener(v -> {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(gallery, GALLERY_REQUEST);
            dialog.show();
        });

        downloadImage();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (user_profile_pic.getDrawable() != null) {
                    blurBackground();
                }
            }
        }, 1000);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                uploadImage(imageUri);
            } else {
                dialog.dismiss();
            }
        }
    }

    private void uploadImage(Uri imageUri) {
        storageReference.child("users/" + auth.getCurrentUser().getUid() + "/profile_pic")
        .putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                user_profile_pic.setImageDrawable(null);
                profile_pic_bg.setImageDrawable(null);
                downloadImage();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (user_profile_pic.getDrawable() != null) {
                            blurBackground();
                            Toast.makeText(getActivity(), "Saved", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, 3000);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getActivity(), "Failed to upload profile pic", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void downloadImage() {
        storageReference.child("users/" + auth.getCurrentUser().getUid() + "/profile_pic").getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(user_profile_pic);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(getActivity(), "Failed to load image", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
    }

    private void blurBackground() {
        BitmapDrawable drawable = (BitmapDrawable) user_profile_pic.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] imageInByte = stream.toByteArray();
        ByteArrayInputStream bis = new ByteArrayInputStream(imageInByte);
        Bitmap blur = BlurEffect.blur(context, BitmapFactory.decodeStream(bis));
        profile_pic_bg.setImageBitmap(blur);
        dialog.dismiss();
    }

    private void openSettings() {
        startActivity(new Intent(getActivity(), Settings.class));
    }

    private void backToMainActivity() {
        Intent mainActivity = new Intent(getActivity(), MainActivity.class);
        mainActivity.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(mainActivity);
    }
}