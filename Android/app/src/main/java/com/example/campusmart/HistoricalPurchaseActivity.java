package com.example.campusmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.adapter.HistoricalPurchaseAdapter;
import com.example.campusmart.entity.Order;
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

public class HistoricalPurchaseActivity extends AppCompatActivity implements HistoricalPurchaseAdapter.OnOrderClickListener {
    private RecyclerView rvHistoricalPurchase;
    private HistoricalPurchaseAdapter adapter;
    private List<Order> orderList = new ArrayList<>();
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_purchase);

        ImageView ivBack = findViewById(R.id.iv_back);
        rvHistoricalPurchase = findViewById(R.id.rv_collection_history);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        adapter = new HistoricalPurchaseAdapter(orderList, this);
        rvHistoricalPurchase.setLayoutManager(new LinearLayoutManager(this));
        rvHistoricalPurchase.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadOrders();
    }

    private void loadOrders() {
        String url = baseUrl + "/app/orders/buyer/list";
        Request.Builder builder = new Request.Builder().url(url).get();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        Request request = builder.build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(HistoricalPurchaseActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(HistoricalPurchaseActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<List<Order>> result = gson.fromJson(data, new TypeToken<Result<List<Order>>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            orderList.clear();
                            orderList.addAll(result.getData());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(HistoricalPurchaseActivity.this, result != null ? result.getMessage() : "Load failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(HistoricalPurchaseActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Order order = orderList.get(position);
        Intent intent = new Intent(this, OrderDetailActivity.class);
        intent.putExtra("order_id", order.getOrderID());
        startActivity(intent);
    }

    @Override
    public void onActionClick(int position, String action) {
        // not used
    }
}
