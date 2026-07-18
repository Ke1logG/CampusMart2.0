package com.example.campusmart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.campusmart.entity.AlipayAccountBind;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class AlipayBindActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvCurrentBind;
    private EditText etAccount, etRealName;
    private Button btnBind;

    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alipay_bind);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        initViews();
        loadBindInfo();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvCurrentBind = findViewById(R.id.tv_current_bind);
        etAccount = findViewById(R.id.et_account);
        etRealName = findViewById(R.id.et_real_name);
        btnBind = findViewById(R.id.btn_bind);

        ivBack.setOnClickListener(v -> finish());
        btnBind.setOnClickListener(v -> doBind());
    }

    private void loadBindInfo() {
        String url = baseUrl + "/app/alipay/bind";
        Request.Builder builder = new Request.Builder().url(url).get();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(AlipayBindActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(AlipayBindActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<AlipayAccountBind> result = gson.fromJson(data, new TypeToken<Result<AlipayAccountBind>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            AlipayAccountBind bind = result.getData();
                            tvCurrentBind.setText("Current bind: " + bind.getAccount() + " (" + bind.getRealName() + ")");
                            etAccount.setText(bind.getAccount());
                            etRealName.setText(bind.getRealName());
                        } else {
                            tvCurrentBind.setText("No Alipay account bound");
                        }
                    });
                }
            }
        });
    }

    private void doBind() {
        String account = etAccount.getText().toString().trim();
        String realName = etRealName.getText().toString().trim();
        if (account.isEmpty() || realName.isEmpty()) {
            Toast.makeText(this, "Please input account and real name", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = baseUrl + "/app/alipay/bind";
        FormBody formBody = new FormBody.Builder()
                .add("account", account)
                .add("realName", realName)
                .build();
        Request.Builder builder = new Request.Builder().url(url).post(formBody);
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(AlipayBindActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(AlipayBindActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<Boolean> result = gson.fromJson(data, new TypeToken<Result<Boolean>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && Boolean.TRUE.equals(result.getData())) {
                            Toast.makeText(AlipayBindActivity.this, "Bind success", Toast.LENGTH_SHORT).show();
                            loadBindInfo();
                        } else {
                            Toast.makeText(AlipayBindActivity.this, result != null ? result.getMessage() : "Bind failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
