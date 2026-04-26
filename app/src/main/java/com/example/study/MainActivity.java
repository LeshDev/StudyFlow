package com.example.study;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, new RaspFragment())
                .commit();

        BottomNavigationView bottomNav = findViewById(R.id.bottomNavigation);
        PreferenceManager prefManager = new PreferenceManager(MainActivity.this);
        bottomNav.getMenu().clear();

        if ("teacher".equals(prefManager.getRole())) {
            bottomNav.inflateMenu(R.menu.bottom_menu_teacher);
            bottomNav.setOnItemSelectedListener(item -> {
                int id = item.getItemId();

                if (id == R.id.nav_raspisanie) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new RaspFragment())
                            .commit();
                    return true;
                }
                if (id == R.id.nav_chats) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new ChatsFragment())
                            .commit();
                    return true;
                }
                if (id == R.id.nav_students) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new MyStudentsFragment())
                            .commit();
                    return true;
                }

                if (id == R.id.nav_homework) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new ChatsFragment())
                            .commit();
                    return true;
                }

                if (id == R.id.nav_profile) {
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.container, new ProfileFragment())
                            .commit();
                    return true;
                }
                return false;
            });
        }
        else {
            bottomNav.inflateMenu(R.menu.bottom_nav_menu);
            if (bottomNav != null) {
                bottomNav.setOnItemSelectedListener(item -> {
                    int id = item.getItemId();

                    if (id == R.id.nav_raspisanie) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new RaspFragment())
                                .commit();
                        return true;
                    }
                    if (id == R.id.nav_chats) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new ChatsFragment())
                                .commit();
                        return true;
                    }

                    if (id == R.id.nav_profile) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.container, new ProfileFragment())
                                .commit();
                        return true;
                    }
                    return false;
                });
            }
        }
    }
}