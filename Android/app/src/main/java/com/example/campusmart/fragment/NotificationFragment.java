package com.example.campusmart.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campusmart.R;
import com.example.campusmart.adapter.NotificationAdapter;
import com.example.campusmart.common.page.PageImpl;
import com.example.campusmart.entity.Notification;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationFragment extends Fragment {
    private RecyclerView rvNotifications;
    private NotificationAdapter adapter;
    private List<NotificationAdapter.Notification> notificationList = new ArrayList<>();
    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;
    private long currentPage = 1;
    private final long pageSize = 5;
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private long totalDataCount = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notification, container, false);
        initViews(view);
        initData();
        setupRecyclerView();
        loadNotifications(currentPage, pageSize);
        return view;
    }

    private void initViews(View view) {
        rvNotifications = view.findViewById(R.id.rv_notifications);
    }

    private void initData() {
        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);

        SharedPreferences sp = getActivity().getSharedPreferences("user_info", getActivity().MODE_PRIVATE);
        token = sp.getString("token", "");
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rvNotifications.setLayoutManager(layoutManager);
        adapter = new NotificationAdapter(notificationList);
        rvNotifications.setAdapter(adapter);

        rvNotifications.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                int totalItemCount = layoutManager.getItemCount();

                if (lastVisibleItemPosition == totalItemCount - 1 && hasMoreData && !isLoading) {
                    currentPage++;
                    loadNotifications(currentPage, pageSize);
                }
            }
        });
    }

    private void loadNotifications(long current, long size) {
        if (isLoading) return;
        isLoading = true;

        String url = baseUrl + "/app/notifications/page?current=" + current + "&size=" + size;
        Request request = new Request.Builder()
                .url(url)
                .addHeader("access-token", token)
                .get()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                    isLoading = false;
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Result<PageImpl<Notification>> result = gson.fromJson(
                            responseData,
                            new TypeToken<Result<PageImpl<Notification>>>(){}.getType()
                    );

                    getActivity().runOnUiThread(() -> {
                        if (result.getCode() == 200 && result.getData() != null) {
                            PageImpl<Notification> pageResult = result.getData();
                            List<Notification> dataList = pageResult.getRecords();
                            totalDataCount = pageResult.getTotal();

                            if (dataList != null && !dataList.isEmpty()) {
                                for (Notification notification : dataList) {
                                    notificationList.add(new NotificationAdapter.Notification(
                                            notification.getSenderName(),
                                            formatTime(notification.getSendTime()),
                                            notification.getNotificationContent()
                                    ));
                                }
                                adapter.notifyDataSetChanged();
                                hasMoreData = notificationList.size() < totalDataCount;
                            } else {
                                hasMoreData = false;
                                Toast.makeText(getContext(), "No more notifications", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), result.getMessage(), Toast.LENGTH_SHORT).show();
                            hasMoreData = false;
                        }
                        isLoading = false;
                    });
                } else {
                    getActivity().runOnUiThread(() -> {
                        Toast.makeText(getContext(), "Failed to load notifications", Toast.LENGTH_SHORT).show();
                        isLoading = false;
                        hasMoreData = false;
                    });
                }
            }
        });
    }

    private String formatTime(Date date) {
        if (date == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);
        return sdf.format(date);
    }
}