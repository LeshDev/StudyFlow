package com.example.study;

import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private BottomNavigationView bottomNav;
    private String userRole;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.viewPager);
        bottomNav = findViewById(R.id.bottomNavigation);

        PreferenceManager prefManager = new PreferenceManager(this);
        userRole = prefManager.getRole();

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (viewPager.getCurrentItem() != 0) {
                    viewPager.setCurrentItem(0, true);
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        bottomNav.getMenu().clear();
        if ("teacher".equals(userRole)) {
            bottomNav.inflateMenu(R.menu.bottom_menu_teacher);
        } else {
            bottomNav.inflateMenu(R.menu.bottom_nav_menu);
        }

        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(adapter);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                bottomNav.getMenu().getItem(position).setChecked(true);
            }
        });

        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            for (int i = 0; i < bottomNav.getMenu().size(); i++) {
                if (bottomNav.getMenu().getItem(i).getItemId() == id) {
                    viewPager.setCurrentItem(i, true);
                    return true;
                }
            }
            return false;
        });
    }

    private class ViewPagerAdapter extends FragmentStateAdapter {
        public ViewPagerAdapter(@NonNull AppCompatActivity fa) {
            super(fa);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            if ("teacher".equals(userRole)) {
                switch (position) {
                    case 0: return new RaspFragment();
                    case 1: return new DiaryFragment();
                    case 2: return new MyStudentsFragment();
                    case 3: return new DiaryFragment();
                    case 4: return new ProfileFragment();
                    default: return new RaspFragment();
                }
            } else {
                switch (position) {
                    case 0: return new RaspFragment();
                    case 1: return new DiaryFragment();
                    case 2: return new ProfileFragment();
                    default: return new RaspFragment();
                }
            }
        }

        @Override
        public int getItemCount() {
            return bottomNav.getMenu().size();
        }
    }
}