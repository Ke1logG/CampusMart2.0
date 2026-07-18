package com.example.campusmart;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.campusmart.entity.Order;
import com.example.campusmart.entity.Payment;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OrderDetailActivity extends AppCompatActivity {

    private ImageView ivBack;
    private TextView tvOrderId, tvGoodTitle, tvAmount, tvStatus, tvCreateTime;
    private Button btnPay, btnCancel, btnConfirm;
    private LinearLayout layoutActions;
    private WebView webViewPay;

    private OkHttpClient client;
    private Gson gson;
    private String baseUrl;
    private String token;
    private Long orderId;
    private Order order;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        orderId = getIntent().getLongExtra("order_id", -1);
        if (orderId == -1) {
            Toast.makeText(this, "Invalid order", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        client = new OkHttpClient();
        gson = new Gson();
        baseUrl = getResources().getString(R.string.base_url);
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", null);

        initViews();
        loadOrderDetail();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        tvOrderId = findViewById(R.id.tv_order_id);
        tvGoodTitle = findViewById(R.id.tv_good_title);
        tvAmount = findViewById(R.id.tv_amount);
        tvStatus = findViewById(R.id.tv_status);
        tvCreateTime = findViewById(R.id.tv_create_time);
        btnPay = findViewById(R.id.btn_pay);
        btnCancel = findViewById(R.id.btn_cancel);
        btnConfirm = findViewById(R.id.btn_confirm);
        layoutActions = findViewById(R.id.layout_actions);
        webViewPay = findViewById(R.id.webview_pay);

        ivBack.setOnClickListener(v -> finish());

        WebSettings settings = webViewPay.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);

        btnPay.setOnClickListener(v -> startPay());
        btnCancel.setOnClickListener(v -> cancelOrder());
        btnConfirm.setOnClickListener(v -> confirmReceipt());
    }

    private void loadOrderDetail() {
        String url = baseUrl + "/app/orders/" + orderId;
        get(url, new TypeToken<Result<Order>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                order = result.getData();
                updateUI();
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI() {
        tvOrderId.setText("Order #" + order.getOrderID());
        tvGoodTitle.setText(order.getGoodTitle());
        tvAmount.setText("¥ " + order.getAmount());
        tvStatus.setText("Status: " + order.getStatus());
        tvCreateTime.setText(DATE_FORMAT.format(order.getCreateTime()));

        String status = order.getStatus();
        if ("CREATED".equals(status)) {
            btnPay.setVisibility(Button.VISIBLE);
            btnCancel.setVisibility(Button.VISIBLE);
            btnConfirm.setVisibility(Button.GONE);
        } else if ("PAID".equals(status)) {
            btnPay.setVisibility(Button.GONE);
            btnCancel.setVisibility(Button.GONE);
            btnConfirm.setVisibility(Button.VISIBLE);
        } else {
            layoutActions.setVisibility(LinearLayout.GONE);
        }
    }

    private void startPay() {
        String url = baseUrl + "/app/payments/order/" + orderId;
        get(url, new TypeToken<Result<Payment>>() {}, paymentResult -> {
            if (paymentResult.getCode() == 200 && paymentResult.getData() != null) {
                Payment payment = paymentResult.getData();
                loadAlipayForm(payment.getPaymentID());
            } else {
                createPayment();
            }
        });
    }

    private void createPayment() {
        String url = baseUrl + "/app/payments/create?orderId=" + orderId;
        postEmpty(url, new TypeToken<Result<Payment>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                loadAlipayForm(result.getData().getPaymentID());
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadAlipayForm(Long paymentId) {
        String url = baseUrl + "/app/payments/" + paymentId + "/alipay/orderString";
        get(url, new TypeToken<Result<String>>() {}, result -> {
            if (result.getCode() == 200 && result.getData() != null) {
                webViewPay.setVisibility(WebView.VISIBLE);
                webViewPay.loadDataWithBaseURL(null, result.getData(), "text/html", "UTF-8", null);
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cancelOrder() {
        String url = baseUrl + "/app/orders/" + orderId + "/cancel";
        postEmpty(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_SHORT).show();
                loadOrderDetail();
            } else {
                Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void confirmReceipt() {
        String url = baseUrl + "/app/orders/" + orderId + "/confirm";
        postEmpty(url, new TypeToken<Result<Boolean>>() {}, result -> {
            if (result.getCode() == 200) {
                Toast.makeText(this, "Confirmed", Toast.LENGTH_SHORT).show();
                loadOrderDetail();
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
        RequestBody body = RequestBody.create("", MediaType.parse("application/json; charset=utf-8"));
        Request.Builder builder = new Request.Builder().url(url).post(body);
        if (token != null && !token.isEmpty()) {
            builder.addHeader("access-token", token);
        }
        enqueue(builder.build(), typeToken, callback);
    }

    private <T> void enqueue(Request request, TypeToken<Result<T>> typeToken, ResponseCallback<T> callback) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUiThread(() -> Toast.makeText(OrderDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if (response.code() == 601 || response.code() == 602) {
                    runOnUiThread(() -> Toast.makeText(OrderDetailActivity.this, "Login expired", Toast.LENGTH_SHORT).show());
                    return;
                }
                if (response.isSuccessful() && response.body() != null) {
                    String data = response.body().string();
                    Result<T> result = gson.fromJson(data, typeToken.getType());
                    runOnUiThread(() -> callback.onResponse(result));
                } else {
                    runOnUiThread(() -> Toast.makeText(OrderDetailActivity.this, "Server error: " + response.code(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private interface ResponseCallback<T> {
        void onResponse(Result<T> result);
    }
}
