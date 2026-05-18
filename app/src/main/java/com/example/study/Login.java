package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Login extends AppCompatActivity {

    public static final String BASE_URL = "http://100.91.165.12:8080/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        EditText etName = findViewById(R.id.etUsername);
        EditText etPass = findViewById(R.id.etPassword);
        Button btnLogin = findViewById(R.id.btnLogin);
        TextView tvRegister = findViewById(R.id.textReturn);
        PreferenceManager prefManager = new PreferenceManager(Login.this);

        StudyFlowApi api = NetworkService.getInstance().getJSONApi();

        btnLogin.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            User loginData = new User(name, pass);

            api.loginUser(loginData).enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body();

                        prefManager.saveUserId(user.getId());
                        prefManager.saveUsername(user.getUsername());
                        prefManager.saveRole(user.getRole());

                        Toast.makeText(Login.this, "Вход выполнен!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(Login.this, "Неверное имя или пароль", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<User> call, Throwable t) {
                    Toast.makeText(Login.this, "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvRegister.setOnClickListener(v -> {
            Intent intent = new Intent(Login.this, Registration.class);
            startActivity(intent);
            finish();
        });
    }
}