package com.example.campusmart;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.view.View;
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
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class GoodsDetailActivity extends AppCompatActivity {
    private ImageView ivBack, ivLike;
    private Button btnBuy;
    private RecyclerView rvGoodsDetail;
    private GoodsDetailAdapter adapter;
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;
    private Long goodId;
    private Goods goods;

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
        } else {
            Toast.makeText(this, "Invalid product ID", Toast.LENGTH_SHORT).show();
        }
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivLike = findViewById(R.id.iv_like);
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
        ivLike.setOnClickListener(v -> Toast.makeText(this, "like", Toast.LENGTH_SHORT).show());
        btnBuy.setOnClickListener(v -> {
            if (goods == null) {
                Toast.makeText(this, "商品信息加载中，请稍后", Toast.LENGTH_SHORT).show();
                return;
            }

            SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
            Long currentUserId = sp.getLong("user_id", 0);
            String selfAvatarUrl = sp.getString("avatar_url", "");

            if (currentUserId <= 0 || token == null || token.isEmpty()) {
                Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
                return;
            }

            Long sellerId = goods.getPublishUserID();
            String sellerNickname = getIntent().getStringExtra("product_publisher");
            String otherAvatarUrl = getIntent().getStringExtra("product_avatar_url");


            Intent intent = new Intent(GoodsDetailActivity.this, ChatActivity.class);
            intent.putExtra("currentUserId", currentUserId);
            intent.putExtra("otherUserId", sellerId);
            intent.putExtra("token", token);
            intent.putExtra("otherNickname", sellerNickname != null ? sellerNickname : "卖家");
            intent.putExtra("selfAvatarUrl", selfAvatarUrl);
            intent.putExtra("otherAvatarUrl", otherAvatarUrl != null ? otherAvatarUrl : "");
            startActivity(intent);
        });
    }

    private void loadGoodsDetail(Long goodId) {
        String url = baseUrl + "/app/goods/selectById?id=" + goodId;

        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();

        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("access-token", token);
        }

        Request request = requestBuilder.build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(GoodsDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(GoodsDetailActivity.this, "Login expired, please login again", Toast.LENGTH_SHORT).show();
                    });
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Type type = new TypeToken<Result<Goods>>(){}.getType();
                    Result<Goods> result = gson.fromJson(responseData, type);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            updateUI(result.getData());
                        } else {
                            Toast.makeText(GoodsDetailActivity.this,
                                    "Load failed: " + (result != null ? result.getMessage() : "Unknown error"),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() ->
                            Toast.makeText(GoodsDetailActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
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
                "¥ " + goods.getPrice(), // 价格格式化
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