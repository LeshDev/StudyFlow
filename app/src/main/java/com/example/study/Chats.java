package com.example.study;

import android.os.Bundle;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;

public class Chats extends BaseMenuActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chats);

        TextInputEditText etSearchField = findViewById(R.id.etSearchField);
        Button btnSearch = findViewById(R.id.btnSearch);
        RecyclerView rvChats = findViewById(R.id.rvChats);


        btnSearch.setOnClickListener(v -> {
            String name = etSearchField.getText().toString().trim();

            if (name == name) {
            }
        });
        setupMenu();
    }
}
