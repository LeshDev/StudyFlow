package com.example.study;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentMarksFragment extends BottomSheetDialogFragment {

    private static final String ARG_STUDENT_ID = "student_id";
    private long studentId;

    private TextView tvAverageRating;
    private RecyclerView rvMarksGrid;
    private MarksAdapter adapter;
    private final List<GradeResponse> marksList = new ArrayList<>();

    private StudyFlowApi api;
    private PreferenceManager prefManager;

    private RadioGroup rgMarks;
    private MaterialButton btnSendGrade;

    public static StudentMarksFragment newInstance(long studentId) {
        StudentMarksFragment fragment = new StudentMarksFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_STUDENT_ID, studentId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            studentId = getArguments().getLong(ARG_STUDENT_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.about_student, container, false);

        tvAverageRating = view.findViewById(R.id.tvStudentDetailRating);
        rvMarksGrid = view.findViewById(R.id.rvStudentDetailMarksGrid);

        if (rvMarksGrid != null) {
            rvMarksGrid.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
            adapter = new MarksAdapter(marksList);
            rvMarksGrid.setAdapter(adapter);
        }

        api = NetworkService.getInstance().getJSONApi();
        prefManager = new PreferenceManager(getContext());

        rgMarks = view.findViewById(R.id.rgMarks);
        btnSendGrade = view.findViewById(R.id.btnSendGrade);

        View actionCard = view.findViewById(R.id.teacherActionCard);
        View btnRemove = view.findViewById(R.id.btnRemoveStudentDetail);
        View tvLabelSetGrade = view.findViewById(R.id.tvLabelSetGrade);

        if ("student".equals(prefManager.getRole())) {
            if (actionCard != null) actionCard.setVisibility(View.GONE);
            if (btnRemove != null) btnRemove.setVisibility(View.GONE);
            if (tvLabelSetGrade != null) tvLabelSetGrade.setVisibility(View.GONE);
        }

        View btnBack = view.findViewById(R.id.btnBack);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> dismiss());
        }

        if (btnSendGrade != null && rgMarks != null) {
            btnSendGrade.setOnClickListener(v -> {
                int checkedId = rgMarks.getCheckedRadioButtonId();
                if (checkedId == -1) {
                    Toast.makeText(getContext(), "Выберите оценку", Toast.LENGTH_SHORT).show();
                    return;
                }

                RadioButton rb = view.findViewById(checkedId);
                String markStr = rb.getText().toString();
                int markValue = markStr.equals("Н") ? 0 : Integer.parseInt(markStr);

                long teacherId = prefManager.getUserId();
                if (teacherId == -1) {
                    Toast.makeText(getContext(), "Ошибка: ID учителя не найден", Toast.LENGTH_SHORT).show();
                    return;
                }

                btnSendGrade.setEnabled(false);

                GradeRequest request = new GradeRequest(studentId, teacherId, markValue);
                api.addGrade(request).enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                        if (!isAdded()) return;
                        btnSendGrade.setEnabled(true);

                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Оценка выставлена", Toast.LENGTH_SHORT).show();
                            rgMarks.clearCheck();
                            loadMarks();
                        } else {
                            Toast.makeText(getContext(), "Ошибка сервера: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable t) {
                        if (!isAdded()) return;
                        btnSendGrade.setEnabled(true);
                        Toast.makeText(getContext(), "Ошибка сети: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            });
        }

        loadMarks();
        return view;
    }

    private void loadMarks() {
        if (api == null) return;
        api.getStudentMarks(studentId).enqueue(new Callback<List<GradeResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<GradeResponse>> call, @NonNull Response<List<GradeResponse>> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    marksList.clear();
                    marksList.addAll(response.body());
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                    calculateAverage();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<GradeResponse>> call, @NonNull Throwable t) {
            }
        });
    }

    private void calculateAverage() {
        if (tvAverageRating == null) return;
        if (marksList.isEmpty()) {
            tvAverageRating.setText("0.0");
            return;
        }
        double sum = 0;
        int count = 0;
        for (GradeResponse g : marksList) {
            if (g.getValue() > 0) {
                sum += g.getValue();
                count++;
            }
        }
        if (count == 0) {
            tvAverageRating.setText("0.0");
        } else {
            tvAverageRating.setText(String.format(Locale.US, "%.1f", sum / count));
        }
    }

    private static class MarksAdapter extends RecyclerView.Adapter<MarksAdapter.VH> {
        private final List<GradeResponse> list;

        MarksAdapter(List<GradeResponse> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.item_mark, p, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int pos) {
            GradeResponse grade = list.get(pos);

            if (grade.getValue() == 0) {
                holder.tvMarkValue.setText("Н");
                holder.tvMarkValue.setTextColor(Color.parseColor("#FF9500"));
            } else {
                holder.tvMarkValue.setText(String.valueOf(grade.getValue()));
                if (grade.getValue() <= 3) {
                    holder.tvMarkValue.setTextColor(Color.parseColor("#FF3B30"));
                } else {
                    holder.tvMarkValue.setTextColor(Color.parseColor("#34C759"));
                }
            }

            holder.tvMarkDate.setText(grade.getDate() != null ? grade.getDate() : "--:--");
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class VH extends RecyclerView.ViewHolder {
            TextView tvMarkValue;
            TextView tvMarkDate;

            VH(View v) {
                super(v);
                tvMarkValue = v.findViewById(R.id.tvMarkValue);
                tvMarkDate = v.findViewById(R.id.tvMarkDate);
            }
        }
    }
}