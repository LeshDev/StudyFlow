package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Profile extends BaseMenuActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);

        LinearLayout itemLogout = findViewById(R.id.itemLogout);
        itemLogout.setOnClickListener(v -> {
            Intent intent = new Intent(Profile.this, Registration.class);
            startActivity(intent);
        });

        setupMenu();
    }
}
