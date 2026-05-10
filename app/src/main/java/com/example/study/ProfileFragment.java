package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ProfileFragment extends Fragment {

    private final String BASE_URL = "";
    private final String API_KEY = "";
    private TextView teacherName;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        TextView tvName = view.findViewById(R.id.tvProfileName);
        LinearLayout itemLogout = view.findViewById(R.id.itemLogout);
        TextView roleText = view.findViewById(R.id.roleText);
        teacherName = view.findViewById(R.id.teacherName);

        PreferenceManager prefManager = new PreferenceManager(getContext());
        tvName.setText(prefManager.getUsername());

        if ("student".equals(prefManager.getRole())) {
            roleText.setText("Ученик");
            long myId = prefManager.getUserId();
            if (myId != -1) {
                loadTeacher(myId);
            }
        } else {
            roleText.setText("Учитель");
            teacherName.setText("Вы учитель");
        }

        itemLogout.setOnClickListener(v -> {
            prefManager.clear();
            startActivity(new Intent(getActivity(), Registration.class));
            getActivity().finish();
        });

        return view;
    }

    private void loadTeacher(long studentId) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SupabaseApi api = retrofit.create(SupabaseApi.class);

        api.getMyTeacher(API_KEY, "eq." + studentId)
                .enqueue(new Callback<List<Map<String, Map<String, String>>>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Map<String, Map<String, String>>>> call,
                                           @NonNull Response<List<Map<String, Map<String, String>>>> response) {
                        if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                            Map<String, String> data = response.body().get(0).get("teacher");
                            if (data != null) {
                                teacherName.setText(data.get("username"));
                            }
                        } else {
                            teacherName.setText("Не назначен");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Map<String, Map<String, String>>>> call, @NonNull Throwable t) {
                        teacherName.setText("Ошибка сети");
                    }
                });
    }
}