package com.example.campusmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.campusmart.entity.UserWallet;
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

public class WalletActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvBalance;
    private Button btnWithdraw, btnFlows, btnConfirmWithdraw;
    private LinearLayout layoutWithdraw;
    private EditText etAmount, etAccount;

    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        initViews();
        loadWallet();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWallet();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvBalance = findViewById(R.id.tv_balance);
        btnWithdraw = findViewById(R.id.btn_withdraw);
        btnFlows = findViewById(R.id.btn_flows);
        layoutWithdraw = findViewById(R.id.layout_withdraw);
        etAmount = findViewById(R.id.et_amount);
        etAccount = findViewById(R.id.et_account);
        btnConfirmWithdraw = findViewById(R.id.btn_confirm_withdraw);

        ivBack.setOnClickListener(v -> finish());
        btnWithdraw.setOnClickListener(v -> layoutWithdraw.setVisibility(layoutWithdraw.getVisibility() == LinearLayout.VISIBLE ? LinearLayout.GONE : LinearLayout.VISIBLE));
        btnFlows.setOnClickListener(v -> startActivity(new Intent(this, WalletFlowsActivity.class)));
        btnConfirmWithdraw.setOnClickListener(v -> doWithdraw());
    }

    private void loadWallet() {
        String url = baseUrl + "/app/wallet";
        Request.Builder builder = new Request.Builder().url(url).get();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(WalletActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(WalletActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<UserWallet> result = gson.fromJson(data, new TypeToken<Result<UserWallet>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            tvBalance.setText("¥ " + result.getData().getBalance());
                        } else {
                            Toast.makeText(WalletActivity.this, result != null ? result.getMessage() : "Load failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void doWithdraw() {
        String amount = etAmount.getText().toString().trim();
        String account = etAccount.getText().toString().trim();
        if (amount.isEmpty() || account.isEmpty()) {
            Toast.makeText(this, "Please input amount and account", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = baseUrl + "/app/wallet/withdraw";
        FormBody formBody = new FormBody.Builder()
                .add("amount", amount)
                .add("alipayAccount", account)
                .build();
        Request.Builder builder = new Request.Builder().url(url).post(formBody);
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(WalletActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(WalletActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<Boolean> result = gson.fromJson(data, new TypeToken<Result<Boolean>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && Boolean.TRUE.equals(result.getData())) {
                            Toast.makeText(WalletActivity.this, "Withdraw success", Toast.LENGTH_SHORT).show();
                            layoutWithdraw.setVisibility(LinearLayout.GONE);
                            etAmount.setText("");
                            etAccount.setText("");
                            loadWallet();
                        } else {
                            Toast.makeText(WalletActivity.this, result != null ? result.getMessage() : "Withdraw failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
