package com.example.campusmart;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class ServiceActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_service);

        ImageView ivBack = findViewById(R.id.iv_back);
        ivBack.setOnClickListener(v -> finish());

        Button btnSCUN = findViewById(R.id.btn_SCNU);
        Button btnWater = findViewById(R.id.btn_water);
        Button btnNetwork = findViewById(R.id.btn_network);

        btnSCUN.setOnClickListener(v ->{
            openWebPage("https://moodle.scnu.edu.cn/");
        });

        btnWater.setOnClickListener(v -> {
            openWeChatAndCopyUrl("https://ecardwx.scnu.edu.cn/wechat/water/smallbag.html");
        });

        btnNetwork.setOnClickListener(v -> {
            openWeChatAndCopyUrl("https://inet.scnu.edu.cn/ccb/pay.html");
        });
    }
    private void openWebPage(String url) {
        if (url == null || !url.startsWith("http")) {
            return;
        }

        Uri webpageUri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpageUri);

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(this, "No available browser application", Toast.LENGTH_SHORT).show();
        }
    }

    private void openWeChatAndCopyUrl(String url) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("wechat_url", url);
        clipboard.setPrimaryClip(clipData);

        Intent weChatIntent = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
        if (weChatIntent != null) {
            weChatIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(weChatIntent);
            Toast.makeText(this, "The link has been copied to the clipboard.\n" +
                    "Please paste it into the search bar of WeChat to access.", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "WeChat was not detected. \nYou will be redirected to the app store to download it.", Toast.LENGTH_SHORT).show();
            openWeChatInAppStore();
        }
    }

    private void openWeChatInAppStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.tencent.mm")));
        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://weixin.qq.com/")));
        }
    }
}