package com.hamzasalahuddin.repjournal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.Window;

import com.hamzasalahuddin.repjournal.customdialogs.CustomProgressDialog;
import com.hamzasalahuddin.repjournal.databinding.ActivityNavBarBinding;

public class NavBar extends AppCompatActivity {
    ActivityNavBarBinding bind;
    CustomProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        bind = ActivityNavBarBinding.inflate(getLayoutInflater());
        setContentView(bind.getRoot());

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.main_container, new TodayFragment());
        ft.commit();

        dialog = new CustomProgressDialog(this);

        bind.navBar.setOnItemSelectedListener(item -> {
            FragmentTransaction fragmentTransaction = fm.beginTransaction();
            switch (item.getItemId()) {
                case R.id.home_nav:
                    fragmentTransaction.replace(R.id.main_container, new TodayFragment());
                    fragmentTransaction.commit();
                    break;
                case R.id.phases_nav:
                    switchActivity(new PhasesFragment());
                    break;
                case R.id.profile_nav:
                    switchActivity(new ProfileFragment());
                    break;
            }
            return true;
        });
    }
    private void switchActivity(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(R.id.main_container, fragment);
        ft.addToBackStack(null).commit();
    }
}