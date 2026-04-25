package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Registration extends AppCompatActivity {

    private EditText etName, etPass;
    private Button btnRegister;
    private final String BASE_URL = "https://zrywvgzbeoclvxdrwlmb.supabase.co/";
    private final String API_KEY = "sb_secret_bFy7IuUUOLVLCQLutf-5Jg_lbi2cR8u";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        etName = findViewById(R.id.editTextName);
        etPass = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApi api = retrofit.create(SupabaseApi.class);

        btnRegister.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (name.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            User newUser = new User(name, pass);

            api.registerUser(API_KEY, "Bearer " + API_KEY, "application/json", "return=minimal", newUser)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> response) {
                            if (response.isSuccessful()) {
                                Toast.makeText(Registration.this, "Успешная регистрация!", Toast.LENGTH_LONG).show();
                                finish();
                                Intent intent = new Intent(Registration.this, MainActivity.class);
                                startActivity(intent);
                            } else {
                                switch (response.code()) {
                                    case 409:
                                        Toast.makeText(Registration.this, "Ошибка 409: такой пользователь уже есть в системе", Toast.LENGTH_SHORT).show();
                                        break;
                                    default:
                                        Toast.makeText(Registration.this, "Ошибка: " + response.code(), Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Toast.makeText(Registration.this, "Проверьте интернет", Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        TextView login = findViewById(R.id.textVoyti);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
            }
        });
    }
}