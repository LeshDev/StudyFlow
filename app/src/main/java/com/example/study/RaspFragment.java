package com.example.study;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
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

            allLessons.add(new Lesson(today, "09:00", "Химия", "402"));
            allLessons.add(new Lesson(today, "10:40", "Высшая мат.", "105"));
        // Завтра
        allLessons.add(new Lesson(today + 1, "09:00", "Физкультура", "Зал"));
        allLessons.add(new Lesson(today + 1, "12:20", "Информатика", "301"));
        // Послезавтра
        allLessons.add(new Lesson(today + 2, "14:00", "История", "205"));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.rasp_fragment, container, false);

        rvCalendar = view.findViewById(R.id.rvCalendar);
        rvSchedule = view.findViewById(R.id.rvSchedule);

        createMockData();

        List<Date> dates = new ArrayList<>();
        Calendar c = Calendar.getInstance();
        for (int i = 0; i < 30; i++) {
            dates.add(c.getTime());
            c.add(Calendar.DAY_OF_YEAR, 1);
        }

        CalendarAdapter calAdapter = new CalendarAdapter(dates);
        rvCalendar.setAdapter(calAdapter);

        scheduleAdapter = new ScheduleAdapter(new ArrayList<>());
        rvSchedule.setAdapter(scheduleAdapter);

        return view;
    }


    // адаптер расписания
    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.VH> {
        private List<Lesson> items;
        ScheduleAdapter(List<Lesson> items) { this.items = items; }

        void updateList(List<Lesson> newItems) {
            this.items = newItems;
            notifyDataSetChanged();
        }

        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            LinearLayout card = new LinearLayout(p.getContext());
            card.setOrientation(LinearLayout.VERTICAL);
            card.setBackgroundColor(Color.WHITE);
            card.setPadding(40, 40, 40, 40);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
            lp.setMargins(0, 0, 0, 24);
            card.setLayoutParams(lp);

            TextView t1 = new TextView(p.getContext());
            t1.setTextSize(17); t1.setTextColor(Color.BLACK); t1.setTypeface(null, Typeface.BOLD);
            TextView t2 = new TextView(p.getContext());
            t2.setTextColor(Color.GRAY);

            card.addView(t1); card.addView(t2);
            return new VH(card, t1, t2);
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
        }
    }
        // адаптер календаря
    private class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.VH> {
        private final List<Date> list;
        private int selectedPos = 0;

        public CalendarAdapter(List<Date> list) { this.list = list; }

        @NonNull
        @Override public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
            LinearLayout layout = new LinearLayout(p.getContext());
            layout.setLayoutParams(new ViewGroup.LayoutParams(160, -2));
            layout.setOrientation(LinearLayout.VERTICAL);
            layout.setPadding(0, 20, 0, 20);
            layout.setGravity(Gravity.CENTER);

            TextView name = new TextView(p.getContext());
            name.setTextSize(12); name.setTextColor(Color.parseColor("#8E8E93"));
            TextView num = new TextView(p.getContext());
            num.setTextSize(16); num.setTypeface(Typeface.SERIF, Typeface.BOLD);

            layout.addView(name); layout.addView(num);
            return new VH(layout, name, num);
        }

        private void filterLessons(int dayOfYear) {
            List<Lesson> filtered = new ArrayList<>();
            for (Lesson l : allLessons) {
                if (l.dayOfYear == dayOfYear) filtered.add(l);
            }
            scheduleAdapter.updateList(filtered);
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Date date = list.get(position);
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            holder.name.setText(new SimpleDateFormat("EE", Locale.getDefault()).format(date));
            holder.num.setText(new SimpleDateFormat("dd", Locale.getDefault()).format(date));

            boolean isSelected = (selectedPos == position);
            holder.num.setTextColor(isSelected ? Color.BLUE : Color.BLACK);

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
