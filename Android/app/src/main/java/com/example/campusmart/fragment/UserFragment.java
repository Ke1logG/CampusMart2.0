package com.example.campusmart.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.campusmart.CollectionActivity;
import com.example.campusmart.HistoricalPurchaseActivity;
import com.example.campusmart.LoginActivity;
import com.example.campusmart.MyGoodsActivity;
import com.example.campusmart.PersonalInfoActivity;
import com.example.campusmart.R;
import com.example.campusmart.WalletActivity;

public class UserFragment extends Fragment {
    private TextView tvNickname;
    private TextView tvProfileSignature;
    private ImageView ivUserAvatar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);

        initViews(view);

        loadUserInfo();

        LinearLayout layoutPersonalInfo = view.findViewById(R.id.layout_personal_info);
        LinearLayout layoutMyGoods = view.findViewById(R.id.layout_my_goods);
        LinearLayout layoutHistoryPurchase = view.findViewById(R.id.layout_history_purchase);
        LinearLayout layoutCollection = view.findViewById(R.id.layout_collection);
        LinearLayout layoutWallet = view.findViewById(R.id.layout_wallet);
        Button btnLogout = view.findViewById(R.id.btn_logout);

        layoutPersonalInfo.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), PersonalInfoActivity.class);
            startActivity(intent);
        });

        layoutMyGoods.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MyGoodsActivity.class);
            startActivity(intent);
        });

        layoutHistoryPurchase.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), HistoricalPurchaseActivity.class);
            startActivity(intent);
        });

        layoutCollection.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), CollectionActivity.class);
            startActivity(intent);
        });

        layoutWallet.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), WalletActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            clearUserInfo();

            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            if (getActivity() != null) {
                getActivity().finish();
            }
        });

        return view;
    }

    private void initViews(View view) {
        tvNickname = view.findViewById(R.id.tv_nickname);
        tvProfileSignature = view.findViewById(R.id.tv_profileSignature);
        ivUserAvatar = view.findViewById(R.id.iv_user_avatar);
    }

    private void loadUserInfo() {
        SharedPreferences sp = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        String nickname = sp.getString("nickname", "User");
        String signature = sp.getString("profile_signature", "Nice to meet you");
        String avatarUrl = sp.getString("avatar_url", "");

        tvNickname.setText(nickname);
        tvProfileSignature.setText(signature);

        if (!avatarUrl.isEmpty()) {
            Glide.with(this)
                    .load(avatarUrl)
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .circleCrop()
                    .into(ivUserAvatar);
        }
    }

    private void clearUserInfo() {
        SharedPreferences sp = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadUserInfo();
    }
}
