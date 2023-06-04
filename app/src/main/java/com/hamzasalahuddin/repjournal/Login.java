package com.hamzasalahuddin.repjournal;

import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;

public class Login extends AppCompatActivity {
    EditText email_login, password_login;
    Button login_proceed_button, signup_button;

    FirebaseAuth auth;
    FirebaseUser user;

    CustomProgressDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);

        email_login = findViewById(R.id.email_login);
        password_login = findViewById(R.id.password_login);

        login_proceed_button = findViewById(R.id.login_proceed_button);
        signup_button = findViewById(R.id.signup_button);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        dialog = new CustomProgressDialog(Login.this);

        login_proceed_button.setOnClickListener(v -> loginUser());
        signup_button.setOnClickListener(v -> {
            dialog.show();
            new android.os.Handler(Looper.getMainLooper()).postDelayed(
                    new Runnable() {
                        public void run() {
                            Intent signup = new Intent(getApplicationContext(), Signup.class);
                            startActivity(signup);
                            dialog.dismiss();
                        }
                    },
                    500);
        });
    }

    private void loginUser() {
        String email = email_login.getText().toString().trim();
        String password = password_login.getText().toString().trim();

        if (email.isEmpty()) {
            email_login.setError("Please enter an email");
        } else if (password.isEmpty()) {
            password_login.setError("Please enter the password");
        } else {
            dialog.show();
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user.isEmailVerified()) {
                            proceedLogin();
                        } else {
                            dialog.dismiss();
                            proceedVerification();
                        }
                    } else {
                        dialog.dismiss();
                        Toast.makeText(Login.this, "Login failed.. Please check your credentials", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void proceedVerification() {
        Intent intent = new Intent(Login.this, Verification.class);
        intent.putExtra("newEmail", email_login.getText().toString().trim());
        intent.putExtra("newPassword", password_login.getText().toString().trim());
        startActivity(intent);
    }

    private void proceedLogin() {
        Intent intent = new Intent(this, NavBar.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
