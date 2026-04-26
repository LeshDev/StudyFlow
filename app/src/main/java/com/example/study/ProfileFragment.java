package com.example.study;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment, container, false);

        TextView tvName = view.findViewById(R.id.tvProfileName);
        LinearLayout itemLogout = view.findViewById(R.id.itemLogout);
        TextView roleText = view.findViewById(R.id.roleText);

        PreferenceManager prefManager = new PreferenceManager(getContext());
        String savedName = prefManager.getUsername();
        String savedRole = prefManager.getRole();

        if (savedRole.equals("student")) {
            roleText.setText("Ученик / раб");
        }
        if (savedRole.equals("teacher")) {
            roleText.setText("Учитель / раб");
        }

        tvName.setText(savedName);
        itemLogout.setOnClickListener(v -> {
            prefManager.clear();

            Intent intent = new Intent(getActivity(), Registration.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        return view;
    }
}
