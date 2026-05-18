package com.example.study;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyStudentsFragment extends Fragment {
    private RecyclerView rvStudents;
    private StudentsAdapter adapter;
    private StudyFlowApi api;
    private PreferenceManager prefManager;
    private List<User> studentsList = new java.util.ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_students_fragment, container, false);

        rvStudents = view.findViewById(R.id.rvStudents);
        rvStudents.setLayoutManager(new LinearLayoutManager(getContext()));

        adapter = new StudentsAdapter(studentsList);
        rvStudents.setAdapter(adapter);

        Button btnAddStudent = view.findViewById(R.id.btnAddStudent);
        TextInputEditText etSearchStudent = view.findViewById(R.id.etSearchStudent);

        prefManager = new PreferenceManager(getContext());
        api = NetworkService.getInstance().getJSONApi();

        loadStudents();

        btnAddStudent.setOnClickListener(v -> {
            String nickname = etSearchStudent.getText().toString().trim();
            if (nickname.isEmpty()) {
                Toast.makeText(getContext(), "Введите никнейм", Toast.LENGTH_SHORT).show();
                return;
            }
            btnAddStudent.setEnabled(false);

            api.findStudentByNickname(nickname).enqueue(new Callback<List<User>>() {
                @Override
                public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                    btnAddStudent.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null && response.body().size() > 0) {
                        User foundStudent = response.body().get(0);
                        if (foundStudent != null) {
                            linkStudent(prefManager.getUserId(), foundStudent.getId(), etSearchStudent);
                        } else {
                            Toast.makeText(getContext(), "Данные ученика повреждены", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Ученик не найден", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<User>> call, Throwable t) {
                    btnAddStudent.setEnabled(true);
                    Toast.makeText(getContext(), "Ошибка поиска: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void loadStudents() {
        api.getMyStudents(prefManager.getUserId()).enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentsList.clear();
                    studentsList.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void linkStudent(long tId, long sId, TextInputEditText et) {
        ClassMember link = new ClassMember(tId, sId);
        api.addStudentToTeacher(link).enqueue(new Callback<ResponseBody>() {
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

    private void deleteStudentFromServer(long studentId) {
        long teacherId = prefManager.getUserId();

        api.deleteStudent(studentId, teacherId).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Ученик успешно исключен", Toast.LENGTH_SHORT).show();
                    loadStudents();
                } else {
                    Toast.makeText(getContext(), "Ошибка удаления: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showStudentDetailDialog(User student) {
        if (getContext() == null) return;

        BottomSheetDialog dialog = new BottomSheetDialog(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.about_student, null);
        dialog.setContentView(dialogView);

        TextView tvName = dialogView.findViewById(R.id.tvStudentDetailName);
        ImageButton btnBack = dialogView.findViewById(R.id.btnBack);
        Button btnRemove = dialogView.findViewById(R.id.btnRemoveStudentDetail);

        TextView tvRating = dialogView.findViewById(R.id.tvStudentDetailRating);
        RecyclerView rvMarksGrid = dialogView.findViewById(R.id.rvStudentDetailMarksGrid);

        tvName.setText(student.getUsername() != null ? student.getUsername() : "id: " + student.getId());

        rvMarksGrid.setLayoutManager(new androidx.recyclerview.widget.GridLayoutManager(getContext(), 7));
        java.util.ArrayList<Integer> marksList = new java.util.ArrayList<>();
        MarksAdapter marksAdapter = new MarksAdapter(marksList);
        rvMarksGrid.setAdapter(marksAdapter);

        api.getStudentGrades(student.getId()).enqueue(new Callback<List<GradeResponse>>() {
            @Override
            public void onResponse(Call<List<GradeResponse>> call, Response<List<GradeResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    marksList.clear();
                    double sum = 0;
                    for (GradeResponse g : response.body()) {
                        marksList.add(g.getValue());
                        sum += g.getValue();
                    }
                    if (!marksList.isEmpty()) {
                        tvRating.setText(String.format(java.util.Locale.US, "%.1f", sum / marksList.size()));
                    } else {
                        tvRating.setText("0.0");
                    }
                    marksAdapter.notifyDataSetChanged();
                }
            }
            @Override public void onFailure(Call<List<GradeResponse>> call, Throwable t) {}
        });

        btnBack.setOnClickListener(v -> dialog.dismiss());

        // ВЕРХНЯЯ КНОПКА УДАЛЕНИЯ (внутри шторки)
        btnRemove.setOnClickListener(v -> {
            deleteStudentFromServer(student.getId()); // Пингуем сервер
            dialog.dismiss(); // Закрываем шторку
        });

        dialog.show();
    }
    private static class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.VH> {
        private final List<Integer> list;
        MarksAdapter(List<Integer> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_mark, p, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            int mark = list.get(pos);
            holder.tvMark.setText(String.valueOf(mark));
            holder.tvMark.setTextColor(mark <= 3 ? android.graphics.Color.parseColor("#FF3B30") : android.graphics.Color.parseColor("#000000"));
        }

        @Override
        public int getItemCount() { return list.size(); }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvMark;
            VH(View v) { super(v); tvMark = v.findViewById(R.id.tvMark); }
        }
    }
    private class StudentsAdapter extends RecyclerView.Adapter<StudentsAdapter.VH> {
        private List<User> list;
        StudentsAdapter(List<User> list) { this.list = list; }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.student_item, p, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            User student = list.get(pos);
            if (student != null) {
                holder.name.setText(student.getUsername() != null ? student.getUsername() : "id: " + student.getId());
            }

            holder.studentContainer.setOnClickListener(v -> {
                if (student != null) showStudentDetailDialog(student);
            });

            holder.btnDelete.setOnClickListener(v -> {
                if (student != null) {
                    deleteStudentFromServer(student.getId());
                }
            });
        }

        @Override
        public int getItemCount() { return list.size(); }

        class VH extends RecyclerView.ViewHolder {
            TextView name;
            ImageButton btnDelete;
            LinearLayout studentContainer;
            VH(View v) {
                super(v);
                name = v.findViewById(R.id.tvStudentName);
                btnDelete = v.findViewById(R.id.btnDeleteStudent);
                studentContainer = v.findViewById(R.id.student);
            }
        }
    }
}