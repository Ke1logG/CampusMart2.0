package com.example.campusmart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.campusmart.R;
import com.example.campusmart.fragment.AddFragment;
import com.example.campusmart.fragment.MessageFragment;
import com.example.campusmart.fragment.ProductListFragment;
import com.example.campusmart.fragment.NotificationFragment;
import com.example.campusmart.fragment.UserFragment;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout layoutHome, layoutNotification, layoutMessage, layoutUser, layoutAdd;
    private ImageView ivHome, ivNotification, ivMessage, ivUser, ivAdd;
    private LinearLayout currentSelectedLayout;
    private ImageView currentSelectedIv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initNavigationViews();
        setSelected(layoutHome, ivHome);
        setNavigationClickListeners();
        replaceFragment(new ProductListFragment());
    }

    private void initNavigationViews() {
        // 布局项
        layoutHome = findViewById(R.id.layout_home);
        layoutNotification = findViewById(R.id.layout_notification);
        layoutAdd = findViewById(R.id.layout_add);
        layoutMessage = findViewById(R.id.layout_message);
        layoutUser = findViewById(R.id.layout_user);
        // 图标
        ivHome = findViewById(R.id.iv_home);
        ivNotification = findViewById(R.id.iv_notification);
        ivMessage = findViewById(R.id.iv_message);
        ivUser = findViewById(R.id.iv_user);
        ivAdd = findViewById(R.id.iv_add);
    }

    private void setNavigationClickListeners() {
        // 首页点击事件
        layoutHome.setOnClickListener(v -> {
            setSelected(layoutHome, ivHome);
            replaceFragment(new ProductListFragment());
        });

        // 通知点击事件
        layoutNotification.setOnClickListener(v -> {
            setSelected(layoutNotification, ivNotification);
            replaceFragment(new NotificationFragment());
        });

        // 消息点击事件
        layoutMessage.setOnClickListener(v -> {
            setSelected(layoutMessage, ivMessage);
             replaceFragment(new MessageFragment());
        });

        // 用户点击事件
        layoutUser.setOnClickListener(v -> {
            setSelected(layoutUser, ivUser);
            replaceFragment(new UserFragment());
        });

        layoutAdd.setOnClickListener(v -> {
            setSelected(layoutAdd, ivAdd);
            replaceFragment(new AddFragment());
        });
    }

    //切换Fragment
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // 设置导航栏选中状态
    private void setSelected(LinearLayout layout, ImageView iv) {
        if (currentSelectedLayout != null) {
            currentSelectedLayout.setSelected(false);
        }
        if (currentSelectedIv != null) {
            currentSelectedIv.setSelected(false);
        }

        layout.setSelected(true);
        iv.setSelected(true);

        currentSelectedLayout = layout;
        currentSelectedIv = iv;
    }
}