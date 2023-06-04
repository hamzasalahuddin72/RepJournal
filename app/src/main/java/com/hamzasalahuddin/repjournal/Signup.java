package com.hamzasalahuddin.repjournal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Signup extends AppCompatActivity {
    EditText first_name, last_name, email_signup, password_signup, confirm_password;
    Button register_button;
    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;

    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_signup);

        first_name = findViewById(R.id.first_name);
        last_name = findViewById(R.id.last_name);
        email_signup = findViewById(R.id.email_signup);
        password_signup = findViewById(R.id.password_signup);
        confirm_password = findViewById(R.id.confirm_password);

        auth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        dialog = new CustomProgressDialog(this);

        register_button = findViewById(R.id.verify_button);
        register_button.setOnClickListener(v -> registerAccount());
    }

    public void registerAccount() {
        String firstname = first_name.getText().toString().trim();
        String lastname = last_name.getText().toString().trim();
        String email = email_signup.getText().toString().trim();
        String password = password_signup.getText().toString().trim();
        String confirmpassword = confirm_password.getText().toString().trim();
        String dateAccountCreated = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        if (firstname.isEmpty()) { // empty first name
            first_name.setError("Please enter your first name");
        } else if (lastname.isEmpty()) { // empty last name
            last_name.setError("Please enter your last name");
        } else if (email.isEmpty()) { // empty email
            email_signup.setError("Please provide an email");
        } else if (!email.matches("^([\\w\\-\\.]+)@((\\[([0-9]{1,3}\\.){3}[0-9]{1,3}\\])|(([\\w\\-]+\\.)+)([a-zA-Z]{2,4}))$")) {
            email_signup.setError("Please provide a valid email address");
        } else if (password.isEmpty()) { // empty password
            password_signup.setError("Please create a password");
        } else if (!password.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[@$!%*?&.]).{8,}$")) {
            password_signup.setError("Password must contain atleast one number, one uppercase letter and one special character (@ $ ! % * ? & .)");
        } else if (confirmpassword.isEmpty()) { // confirmpassword is empty
            confirm_password.setError("Please confirm your password");
        } else if (!password.equals(confirmpassword)) { // passwords do not match
            confirm_password.setError("Passwords don't match");
        } else {
            dialog.show();
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                        String getFirstname = first_name.getText().toString();
                        String getLastname = last_name.getText().toString();
                        String getEmail = email_signup.getText().toString();
                        if (task.isSuccessful()) {
                            Map<String,Object> userdata = new HashMap<>();
                            userdata.put("email", getEmail);
                            userdata.put("firstname", getFirstname);
                            userdata.put("lastname", getLastname);
                            userdata.put("dateAccountCreated", dateAccountCreated);

                            DocumentReference userRef = firebaseFirestore.collection("users")
                                    .document(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .collection("credentials").document(FirebaseAuth.getInstance().getCurrentUser().getUid());

                            userRef.set(userdata).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(Signup.this, "User has been registered", Toast.LENGTH_SHORT).show();
                                    proceedActivity();
                                    dialog.dismiss();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Signup.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                }
                            });
                        }
                });
        }
    }

    private void proceedActivity() {
        Intent intent = new Intent(Signup.this, Verification.class);
        intent.putExtra("newEmail", email_signup.getText().toString().trim());
        intent.putExtra("newPassword", password_signup.getText().toString().trim());
        startActivity(intent);
    }
}