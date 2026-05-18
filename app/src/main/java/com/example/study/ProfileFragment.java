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

        if ("student".equals(prefManager.getRole())) {
            roleText.setText("Ученик");
            long myId = prefManager.getUserId();
            if (myId != -1) {
                loadTeacher(myId);

                if (savedInstanceState == null) {
                    getChildFragmentManager().beginTransaction()
                            .replace(R.id.studentMarksContainer, StudentMarksFragment.newInstance(myId))
                            .commit();
                }
            }
        }
        else {
            roleText.setText("Учитель");
            teacherName.setText("Вы учитель");
            View marksContainer = view.findViewById(R.id.studentMarksContainer);
            if (marksContainer != null) {
                marksContainer.setVisibility(View.GONE);
            }
        }

        return view;
    }

    private void loadTeacher(long studentId) {
        StudyFlowApi api = NetworkService.getInstance().getJSONApi();

        api.getMyTeacher(studentId).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    teacherName.setText(response.body().getUsername());
                }
                else {
                    teacherName.setText("Не назначен");
                }
            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                teacherName.setText("Ошибка сервера");
            }
        });
    }
}