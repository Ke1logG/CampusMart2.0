package com.example.campusmart.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.campusmart.DetailInfoActivity;
import com.example.campusmart.LoginActivity;
import com.example.campusmart.R;
import com.example.campusmart.ServiceActivity;

public class AddFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add, container, false);

        Button btnSell = view.findViewById(R.id.btn_sell);
        Button btnGift = view.findViewById(R.id.btn_gift);

        btnSell.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), DetailInfoActivity.class);
            startActivity(intent);
        });

        btnGift.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ServiceActivity.class);
            startActivity(intent);
        });

        return view;
    }
}