package com.example.campusmart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.adapter.WalletFlowAdapter;
import com.example.campusmart.entity.WalletFlow;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WalletFlowsActivity extends AppCompatActivity {

    private RecyclerView rvFlows;
    private WalletFlowAdapter adapter;
    private List<WalletFlow> flowList = new ArrayList<>();
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet_flows);

        ImageView ivBack = findViewById(R.id.iv_back);
        rvFlows = findViewById(R.id.rv_flows);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        adapter = new WalletFlowAdapter(flowList);
        rvFlows.setLayoutManager(new LinearLayoutManager(this));
        rvFlows.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadFlows();
    }

    private void loadFlows() {
        String url = baseUrl + "/app/wallet/flows";
        Request.Builder builder = new Request.Builder().url(url).get();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(WalletFlowsActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(WalletFlowsActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<List<WalletFlow>> result = gson.fromJson(data, new TypeToken<Result<List<WalletFlow>>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            flowList.clear();
                            flowList.addAll(result.getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(WalletFlowsActivity.this, result != null ? result.getMessage() : "Load failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
