package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;

public class BaseMenuActivity extends AppCompatActivity {
    protected void setupMenu() {
        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        if (bottomNav != null) {
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_raspisanie) {
                    startActivity(new Intent(this, MainActivity.class));
                    return true;
                }
                if (id == R.id.nav_chats) {
                    startActivity(new Intent(this, Chats.class));
                    return true;
                }

                if (id == R.id.nav_profile) {
                    if (!(this instanceof Profile)) {
                        startActivity(new Intent(this, Profile.class));
                    }
                    return true;
                }

                return false;
            });
        }
    }
}