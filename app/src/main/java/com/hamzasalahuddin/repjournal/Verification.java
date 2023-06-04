package com.hamzasalahuddin.repjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Verification extends AppCompatActivity {
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Button verify_button;
        FirebaseAuth auth;

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_verification);

        verify_button = findViewById(R.id.verify_button);

        auth = FirebaseAuth.getInstance();

        String email = getIntent().getExtras().getString("newEmail");
        String password = getIntent().getExtras().getString("newPassword");

        auth.getCurrentUser().sendEmailVerification();

        Handler resendHandler = new Handler();
        int delay = 5000;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            if (auth.getCurrentUser().isEmailVerified()) {
                                Toast.makeText(getApplicationContext(), "Welcome! Your email is verified now", Toast.LENGTH_SHORT).show();
                                proceedActivity();
                                handler.removeCallbacksAndMessages(null);
                            }
                        }
                    }
                });
                handler.postDelayed(this, delay);
            }
        }, delay);

        verify_button.setOnClickListener(v -> {
            resendHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    auth.getCurrentUser().sendEmailVerification();
                    Toast.makeText(Verification.this, "Verification mail is sent", Toast.LENGTH_SHORT).show();
                }
            }, 1);
        });
    }

    @Override
    public void onBackPressed() {
        handler.removeCallbacksAndMessages(null);
        super.onBackPressed();
    }

    private void proceedActivity() {
        Intent intent = new Intent(this, NavBar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}