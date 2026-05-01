package com.example.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MyStudentsFragment extends Fragment {
    private final String BASE_URL = "https://zrywvgzbeoclvxdrwlmb.supabase.co/";
    private final String API_KEY = "";

    private RecyclerView rvStudents;
    private StudentsAdapter adapter;
    private SupabaseApi api;
    private PreferenceManager prefManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_students_fragment, container, false);

        rvStudents = view.findViewById(R.id.rvStudents); // Добавь его в XML макета!
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        Button btnAddStudent = view.findViewById(R.id.btnAddStudent);
        TextInputEditText etSearchStudent = view.findViewById(R.id.etSearchStudent);

        prefManager = new PreferenceManager(getContext());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(SupabaseApi.class);

        loadStudents();

        btnAddStudent.setOnClickListener(v -> {
            String nickname = etSearchStudent.getText().toString().trim();
            if (nickname.isEmpty()) return;

            api.findStudentByNickname(API_KEY, "eq." + nickname, "eq.student").enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                        long studentId = response.body().get(0).getId();
                        long teacherId = prefManager.getUserId();
                        linkStudent(teacherId, studentId, etSearchStudent);
                    } else {
                        Toast.makeText(getContext(), "Ученик не найден", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override public void onFailure(Call<List<User>> call, Throwable t) {}
            });
        });

        return view;
    }

    private void loadStudents() {
        api.getMyStudents(API_KEY, "eq." + prefManager.getUserId()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter = new StudentsAdapter(response.body());
                    rvStudents.setAdapter(adapter);
                }
            }
            @Override public void onFailure(Call<List<User>> call, Throwable t) {}
        });
    }

    private void linkStudent(long tId, long sId, TextInputEditText et) {
        ClassMember link = new ClassMember(tId, sId);
        api.addStudentToTeacher(API_KEY, "Bearer " + API_KEY, link).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ученик добавлен!", Toast.LENGTH_SHORT).show();
                    et.setText("");
                    loadStudents();
                } else {
                    Toast.makeText(getContext(), "Ошибка добавления", Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(Call<ResponseBody> call, Throwable t) {}
        });
    }

    // адаптер учеников
    private class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.VH> {
        private List<User> list;
        StudentsAdapter(List<User> list) { this.list = list; }

        @NonNull
        @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.student_item, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int pos) {
            User studentRelation = list.get(pos);
            if (studentRelation.getStudentData() != null) {
                holder.name.setText(studentRelation.getStudentData().getUsername());
            }
        }

        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView name;
            VH(View v) { super(v); name = v.findViewById(R.id.tvStudentName); }
        }
    }
}
