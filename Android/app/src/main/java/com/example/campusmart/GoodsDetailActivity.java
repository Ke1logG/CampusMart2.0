package com.example.campusmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.campusmart.adapter.GoodsDetailAdapter;
import com.example.campusmart.entity.Goods;
import com.example.campusmart.entity.Order;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GoodsDetailActivity extends AppCompatActivity {
    private ImageView ivBack, ivLike, ivCollect;
    private Button btnBuy;
    private RecyclerView rvGoodsDetail;
    private GoodsDetailAdapter adapter;
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;
    private Long goodId;
    private Goods goods;
    private boolean isLiked = false;
    private boolean isCollected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);

        Intent intent = getIntent();
        goodId = intent.getLongExtra("product_good_id", -1);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);

        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        initViews();
        initRecyclerView(new ArrayList<>());
        setClickEvents();

        if (goodId != -1) {
            loadGoodsDetail(goodId);
            if (token != null) {
                checkLikeStatus();
                checkCollectStatus();
            }
        } else {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivLike = findViewById(R.id.iv_like);
        ivCollect = findViewById(R.id.iv_collect);
        btnBuy = findViewById(R.id.btn_buy);
        rvGoodsDetail = findViewById(R.id.rv_goods_detail);
    }

    private void initRecyclerView(List<GoodsDetailAdapter.GoodsDetailItem> dataList) {
        adapter = new GoodsDetailAdapter(dataList);
        rvGoodsDetail.setLayoutManager(new LinearLayoutManager(this));
        rvGoodsDetail.setAdapter(adapter);
    }

    private void setClickEvents() {
        ivBack.setOnClickListener(v -> finish());

        ivLike.setOnClickListener(v -> {
            if (!checkLogin()) return;
            if (isLiked) {
                unlike();
            } else {
                like();
            }
        });

        ivCollect.setOnClickListener(v -> {
            if (!checkLogin()) return;
            if (isCollected) {
                uncollect();
            } else {
                collect();
            }
        });

        btnBuy.setOnClickListener(v -> {
            if (!checkLogin()) return;
            if (goods == null) {
                Toast.makeText(this, "商品信息加载中，请稍后", Toast.LENGTH_SHORT).show();
                return;
            }
            createOrder();
        });
    }

    private boolean checkLogin() {
        if (token == null || token.isEmpty()) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void loadGoodsDetail(Long goodId) {
        String url = baseUrl + "/app/goods/selectById?id=" + goodId;
        get(url, new TypeToken<Result<Goods>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                updateUI(result.getData());
            } else {
                Toast.makeText(GoodsDetailActivity.this,
                        "Load failed: " + (result != null ? result.getMessage() : "Unknown error"),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkLikeStatus() {
        String url = baseUrl + "/app/like/" + goodId;
        get(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                isLiked = result.getData();
                updateLikeIcon();
            }
        });
    }

    private void checkCollectStatus() {
        String url = baseUrl + "/app/collection/" + goodId;
        get(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                isCollected = result.getData();
                updateCollectIcon();
            }
        });
    }

    private void like() {
        String url = baseUrl + "/app/like/" + goodId;
        postEmpty(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200) {
                isLiked = true;
                updateLikeIcon();
                Toast.makeText(this, "Liked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void unlike() {
        String url = baseUrl + "/app/like/" + goodId;
        delete(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200) {
                isLiked = false;
                updateLikeIcon();
                Toast.makeText(this, "Unliked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void collect() {
        String url = baseUrl + "/app/collection/" + goodId;
        postEmpty(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200) {
                isCollected = true;
                updateCollectIcon();
                Toast.makeText(this, "Collected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uncollect() {
        String url = baseUrl + "/app/collection/" + goodId;
        delete(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200) {
                isCollected = false;
                updateCollectIcon();
                Toast.makeText(this, "Uncollected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateLikeIcon() {
        ivLike.setColorFilter(isLiked ? Color.parseColor("#FF4081") : Color.parseColor("#999999"));
    }

    private void updateCollectIcon() {
        ivCollect.setColorFilter(isCollected ? Color.parseColor("#FF4081") : Color.parseColor("#999999"));
    }

    private void createOrder() {
        String url = baseUrl + "/app/orders/create?goodId=" + goodId;
        postEmpty(url, new TypeToken<Result<Order>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                Order order = result.getData();
                Toast.makeText(this, "Order created", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(GoodsDetailActivity.this, OrderDetailActivity.class);
                intent.putExtra("order_id", order.getOrderID());
                startActivity(intent);
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private <T> void get(String url, TypeToken<Result<T>> typeToken, ResponseCallback<T> callback) {
        Request.Builder builder = new Request.Builder().url(url).get();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        enqueue(builder.build(), typeToken, callback);
    }

    private <T> void postEmpty(String url, TypeToken<Result<T>> typeToken, ResponseCallback<T> callback) {
        Request.Builder builder = new Request.Builder().url(url).post(RequestBody.create("", MediaType.parse("application/json; charset=utf-8")));
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        enqueue(builder.build(), typeToken, callback);
    }

    private <T> void delete(String url, TypeToken<Result<T>> typeToken, ResponseCallback<T> callback) {
        Request.Builder builder = new Request.Builder().url(url).delete();
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        enqueue(builder.build(), typeToken, callback);
    }

    private <T> void enqueue(Request request, TypeToken<Result<T>> typeToken, ResponseCallback<T> callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(GoodsDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(GoodsDetailActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<T> result = gson.fromJson(data, typeToken.getType());
                    runOnUiThread(() -> callback.onResponse(result));
                } else {
                    runOnUiThread(() -> Toast.makeText(GoodsDetailActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private interface ResponseCallback<T> {
        void onResponse(Result<T> result);
    }

    private void updateUI(Goods goods) {
        this.goods = goods;
        List<GoodsDetailAdapter.GoodsDetailItem> dataList = new ArrayList<>();

        Intent intent = getIntent();
        String publisher = intent.getStringExtra("product_publisher");
        String avatarUrl = intent.getStringExtra("product_avatar_url");
        String productImageUrl = intent.getStringExtra("product_image_url");

        dataList.add(new GoodsDetailAdapter.GoodsDetailItem(
                GoodsDetailAdapter.TYPE_USER_INFO,
                R.drawable.avatar_placeholder,
                publisher != null ? publisher : "Unknown",
                goods.getAppearance() != null ? goods.getAppearance() : "Unknown"
        ));

        dataList.add(new GoodsDetailAdapter.GoodsDetailItem(
                GoodsDetailAdapter.TYPE_PRICE_TITLE,
                "¥ " + goods.getPrice(),
                goods.getTitle() != null ? goods.getTitle() : "No title"
        ));

        dataList.add(new GoodsDetailAdapter.GoodsDetailItem(
                GoodsDetailAdapter.TYPE_DESC,
                goods.getItemDescription() != null ? goods.getItemDescription() : "No description"
        ));

        dataList.add(new GoodsDetailAdapter.GoodsDetailItem(
                GoodsDetailAdapter.TYPE_GOODS_IMG,
                R.drawable.placeholder
        ));

        adapter = new GoodsDetailAdapter(dataList) {
            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                super.onBindViewHolder(holder, position);
                if (getItemViewType(position) == TYPE_GOODS_IMG && holder instanceof GoodsImgViewHolder) {
                    GoodsImgViewHolder imgHolder = (GoodsImgViewHolder) holder;
                    if (productImageUrl != null && !productImageUrl.isEmpty()) {
                        Glide.with(GoodsDetailActivity.this)
                                .load(productImageUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.placeholder)
                                        .error(R.drawable.placeholder)
                                        .centerCrop())
                                .into(imgHolder.ivGoods);
                    }
                }
                else if (getItemViewType(position) == TYPE_USER_INFO && holder instanceof UserInfoViewHolder) {
                    UserInfoViewHolder userHolder = (UserInfoViewHolder) holder;
                    if (avatarUrl != null && !avatarUrl.isEmpty()) {
                        Glide.with(GoodsDetailActivity.this)
                                .load(avatarUrl)
                                .apply(new RequestOptions()
                                        .placeholder(R.drawable.avatar_placeholder)
                                        .error(R.drawable.avatar_placeholder)
                                        .circleCrop())
                                .into(userHolder.ivAvatar);
                    }
                }
            }
        };
        rvGoodsDetail.setAdapter(adapter);
    }
}
