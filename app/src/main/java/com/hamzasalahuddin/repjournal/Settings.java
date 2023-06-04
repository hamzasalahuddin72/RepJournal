package com.hamzasalahuddin.repjournal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Window;
import android.widget.ImageButton;

public class Settings extends AppCompatActivity {
    ImageButton back_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_settings);

        back_button = findViewById(R.id.back_button);

        back_button.setOnClickListener(v -> onBackPressed());
    }
}