package com.example.study;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RaspFragment extends Fragment {
    private RecyclerView rvCalendar, rvSchedule;
    private List<Lesson> allLessons = new ArrayList<>();
    BottomNavigationView bottomNav;
    private ScheduleAdapter scheduleAdapter;

    public static class Lesson {
        String time, name, room;
        int dayOfYear;

        public Lesson(int dayOfYear, String time, String name, String room) {
            this.dayOfYear = dayOfYear;
            this.time = time;
            this.name = name;
            this.room = room;
        }
    }

    private void createMockData() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        // Сегодня
        allLessons.add(new Lesson(Calendar.MONDAY, "09:00", "Химия", "402"));
        allLessons.add(new Lesson(Calendar.MONDAY, "10:40", "Высшая мат.", "105"));
        // Завтра
        allLessons.add(new Lesson(Calendar.TUESDAY, "09:00", "Физкультура", "Зал"));
        allLessons.add(new Lesson(Calendar.TUESDAY, "12:20", "Информатика", "301"));
        // Послезавтра
        allLessons.add(new Lesson(Calendar.WEDNESDAY, "14:00", "История", "205"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rasp_fragment, container, false);

        rvCalendar = view.findViewById(R.id.rvCalendar);
        rvSchedule = view.findViewById(R.id.rvSchedule);

        createMockData();

        List<Date> dates = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < 365; i++) {
            dates.add(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        CalendarAdapter calAdapter = new CalendarAdapter(dates);
        rvCalendar.setAdapter(calAdapter);
        new LinearSnapHelper().attachToRecyclerView(rvCalendar);

        scheduleAdapter = new ScheduleAdapter(new ArrayList<>());
        rvSchedule.setAdapter(scheduleAdapter);

        int today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
        filterLessons(today);

        return view;
    }

    private void filterLessons(int dayOfYear) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_YEAR, dayOfYear);
        int clickedDay = cal.get(Calendar.DAY_OF_WEEK);
        List<Lesson> filtered = new ArrayList<>();
        for (Lesson l : allLessons) {
            if (l.dayOfYear == clickedDay) filtered.add(l);
        }
        if (scheduleAdapter != null) scheduleAdapter.updateList(filtered);
    }

    // адаптер расписания
    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.VH> {
        private List<Lesson> items;
        ScheduleAdapter(List<Lesson> items) { this.items = items; }

        void updateList(List<Lesson> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull @Override public RaspFragment.ScheduleAdapter.VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            View v = LayoutInflater.from(p.getContext()).inflate(R.layout.lesson_item, p, false);
            return new VH(v);
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Lesson l = items.get(position);
            holder.title.setText(l.time + " — " + l.name);
            holder.sub.setText("Аудитория: " + l.room);
        }

        @Override public int getItemCount() { return items.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView title, sub;
            VH(View v, TextView t1, TextView t2) { super(v); title = t1; sub = t2; }

            public VH(View v) {
                super(v);
                title = v.findViewById(R.id.tvLessonTitle);
                sub = v.findViewById(R.id.tvLessonRoom);
            }
        }
    }

    // адаптер календаря
    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.VH> {
        private final List<Date> list;
        private int selectedPos = 0;

        public CalendarAdapter(List<Date> list) { this.list = list; }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            LinearLayout layout = new LinearLayout(p.getContext());
            int screenWidth = p.getResources().getDisplayMetrics().widthPixels;
            layout.setLayoutParams(new ViewGroup.LayoutParams(screenWidth / 8, -2));
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(0, 20, 0, 20);
            layout.setGravity(Gravity.CENTER);

            TextView name = new TextView(p.getContext());
            name.setTextSize(11);
            name.setTextColor(Color.parseColor("#8E8E93"));
            name.setGravity(Gravity.CENTER);

            TextView num = new TextView(p.getContext());
            num.setTextSize(16);
            num.setTypeface(Typeface.SERIF, Typeface.BOLD);
            num.setGravity(Gravity.CENTER);

            LinearLayout.LayoutParams numLp = new LinearLayout.LayoutParams(100, 100);
            numLp.topMargin = 10;
            num.setLayoutParams(numLp);

            layout.addView(name);
            layout.addView(num);
            return new VH(layout, name, num);
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Date date = list.get(position);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            holder.name.setText(new SimpleDateFormat("EE", Locale.getDefault()).format(date).toUpperCase());
            holder.num.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(date));

            boolean isSelected = (selectedPos == position);

            if (isSelected) {
                holder.num.setBackgroundResource(R.drawable.selected_day_bg);
                holder.num.setTextColor(Color.WHITE);
            } else {
                holder.num.setBackground(null);
                holder.num.setTextColor(Color.BLACK);
            }

            holder.itemView.setOnClickListener(v -> {
                int old = selectedPos;
                selectedPos = holder.getAdapterPosition();
                notifyItemChanged(old);
                notifyItemChanged(selectedPos);
                filterLessons(cal.get(Calendar.DAY_OF_YEAR));
            });
        }

        @Override public int getItemCount() { return list.size(); }
        class VH extends RecyclerView.ViewHolder {
            TextView name, num;
            VH(View v, TextView na, TextView nu) { super(v); name = na; num = nu; }
        }
    }
}