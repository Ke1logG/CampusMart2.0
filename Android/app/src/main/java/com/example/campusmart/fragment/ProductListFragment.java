package com.example.campusmart.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusmart.R;
import com.example.campusmart.adapter.ProductAdapter;
import com.example.campusmart.result.Result;
import com.example.campusmart.vo.GoodsVo;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;
import com.example.campusmart.common.page.IPage;
import com.example.campusmart.common.page.PageImpl;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ProductListFragment extends Fragment {
    private RecyclerView rvProducts;
    private ProductAdapter productAdapter;
    private List<ProductAdapter.Product> productList = new ArrayList<>();
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private long currentPage = 1;
    private final long pageSize = 10;
    private boolean isLoading = false;
    private boolean hasMore = true;
    private String token;
    private EditText etSearch;
    private ImageView btnSearch;
    private String currentKeyword = "";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);

        SharedPreferences sp = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        token = sp.getString("token", null);

        etSearch = view.findViewById(R.id.et_search);
        btnSearch = view.findViewById(R.id.btn_search);

        btnSearch.setOnClickListener(v -> performSearch());

        rvProducts = view.findViewById(R.id.rv_products);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        rvProducts.setLayoutManager(layoutManager);

        productAdapter = new ProductAdapter(productList);
        rvProducts.setAdapter(productAdapter);

        loadGoodsData(currentPage, pageSize, false);

        rvProducts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && hasMore &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0) {
                    currentPage++;
                    loadGoodsData(currentPage, pageSize, !currentKeyword.isEmpty());
                }
            }
        });

        return view;
    }

    private void performSearch() {
        String keyword = etSearch.getText().toString().trim();
        currentKeyword = keyword;
        productList.clear();
        productAdapter.notifyDataSetChanged();

        currentPage = 1;
        hasMore = true;

        loadGoodsData(currentPage, pageSize, true);
    }

    private void loadGoodsData(long current, long size, boolean isSearch) {
        if (isLoading) return;
        isLoading = true;

        String url;
        if (isSearch && !currentKeyword.isEmpty()) {
            // 搜索接口
            url = baseUrl + "/app/goods/search?current=" + current + "&size=" + size + "&titleKeyword=" + currentKeyword;
        } else {
            // 普通列表接口
            url = baseUrl + "/app/goods/page?current=" + current + "&size=" + size;
        }

        // 创建请求
        Request.Builder requestBuilder = new Request.Builder()
                .url(url)
                .get();

        // 如果Token存在则添加到请求头
        if (token != null && !token.isEmpty()) {
            requestBuilder.addHeader("access-token", token);
        }

        Request request = requestBuilder.build();

        // 发送请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                // 处理Token失效情况
                if (response.code() == 601 || response.code() == 602) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(getContext(), "Login expired, please login again", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    });
                    return;
                }

                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();

                    // 解析JSON响应
                    Type type = new TypeToken<Result<PageImpl<GoodsVo>>>(){}.getType();
                    Result<PageImpl<GoodsVo>> result = gson.fromJson(responseData, type);

                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (result != null && result.getCode() == 200 && result.getData() != null) {
                            List<GoodsVo> goodsList = result.getData().getRecords();

                            // 判断是否有更多数据
                            hasMore = current < result.getData().getPages();

                            // 转换数据并添加到列表
                            for (GoodsVo goods : goodsList) {
                                productList.add(new ProductAdapter.Product(
                                        goods.getTitle(),
                                        R.drawable.placeholder,
                                        goods.getNickname(),
                                        R.drawable.avatar_placeholder,
                                        goods.getPictureURL(),
                                        goods.getAvatarURL(),
                                        goods.getGoodID()
                                ));
                            }

                            // 通知适配器更新
                            productAdapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "Load failed: " + (result != null ? result.getMessage() : "Unknown error"), Toast.LENGTH_SHORT).show();
                        }
                        isLoading = false;
                    });
                } else {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Toast.makeText(getContext(), "Server error: " + response.code(), Toast.LENGTH_SHORT).show();
                        isLoading = false;
                    });
                }
            }
        });
    }

}