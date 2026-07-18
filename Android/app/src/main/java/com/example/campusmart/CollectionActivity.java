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
import com.example.campusmart.adapter.CollectionAdapter;
import com.example.campusmart.entity.Goods;
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

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnButtonClickListener {
    private RecyclerView rvCollection;
    private CollectionAdapter adapter;
    private List<Goods> goodsList = new ArrayList<>();
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;
    private int pendingRequests = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        ImageView ivBack = findViewById(R.id.iv_back);
        rvCollection = findViewById(R.id.rv_collection_history);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        adapter = new CollectionAdapter(goodsList, this);
        rvCollection.setLayoutManager(new LinearLayoutManager(this));
        rvCollection.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());

        loadCollections();
    }

    private void loadCollections() {
        String url = baseUrl + "/app/collection/list";
        Request.Builder builder = new Request.Builder().url(url).get();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(CollectionActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(CollectionActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<List<Long>> result = gson.fromJson(data, new TypeToken<Result<List<Long>>>(){}.getType());
                    runOnUiThread(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            loadGoodsDetails(result.getData());
                        } else {
                            Toast.makeText(CollectionActivity.this, result != null ? result.getMessage() : "Load failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(CollectionActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void loadGoodsDetails(List<Long> goodIds) {
        goodsList.clear();
        if (goodIds.isEmpty()) {
            adapter.notifyDataSetChanged();
            return;
        }
        pendingRequests = goodIds.size();
        for (Long goodId : goodIds) {
            String url = baseUrl + "/app/goods/selectById?id=" + goodId;
            Request.Builder builder = new Request.Builder().url(url).get();
            if (token != null && !token.isEmpty()) {
                builder.addHeader("access-token", token);
            }
            client.newCall(builder.build()).enqueue(new Callback() {
                @Override
                public void onFailure(@NonNull Call call, @NonNull IOException e) {
                    decrementPending();
                }

                @Override
                public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                    if (response.isSuccessful() && response.body() != null) {
                        String data = response.body().string();
                        Result<Goods> result = gson.fromJson(data, new TypeToken<Result<Goods>>(){}.getType());
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            runOnUiThread(() -> {
                                goodsList.add(result.getData());
                                decrementPending();
                            });
                            return;
                        }
                    }
                    decrementPending();
                }
            });
        }
    }

    private synchronized void decrementPending() {
        pendingRequests--;
        if (pendingRequests <= 0) {
            runOnUiThread(() -> adapter.notifyDataSetChanged());
        }
    }

    @Override
    public void onDeleteClick(int position) {
        Goods goods = goodsList.get(position);
        String url = baseUrl + "/app/collection/" + goods.getGoodID();
        Request.Builder builder = new Request.Builder().url(url).delete();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(CollectionActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                runOnUiThread(() -> {
                    goodsList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, goodsList.size());
                    Toast.makeText(CollectionActivity.this, "Removed", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Goods goods = goodsList.get(position);
        Intent intent = new Intent(this, GoodsDetailActivity.class);
        intent.putExtra("product_good_id", goods.getGoodID());
        startActivity(intent);
    }
}
