package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TeacherLogin extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teacher_login);

        TextView returnToStudent = findViewById(R.id.textReturnToStudent);

        returnToStudent.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherLogin.this, Login.class);
            startActivity(intent);
            finish();        });
    }
}
