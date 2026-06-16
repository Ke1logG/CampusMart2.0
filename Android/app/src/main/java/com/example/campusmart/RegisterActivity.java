package com.example.campusmart;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import com.example.campusmart.entity.RegisterVo;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText etSchoolName;
    private EditText etStudentId;
    private EditText etUsername;
    private EditText etPassword;
    private EditText etPhoneNumber;
    private OkHttpClient client;
    private Gson gson;
    private String BASE_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etSchoolName = findViewById(R.id.et_school_name);
        etStudentId = findViewById(R.id.et_student_id);
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        etPhoneNumber = findViewById(R.id.et_phone);

        TextView tvLogin = findViewById(R.id.tv_login);
        AppCompatButton btnRegister = findViewById(R.id.btn_register);

        BASE_URL = getResources().getString(R.string.base_url);
        client = new OkHttpClient();
        gson = new Gson();

        tvLogin.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            finish();
        });

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String schoolName = etSchoolName.getText().toString().trim();
        String studentId = etStudentId.getText().toString().trim();
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String phoneNumber = etPhoneNumber.getText().toString().trim();

        if (schoolName.isEmpty() || studentId.isEmpty() ||
                username.isEmpty() || password.isEmpty() || phoneNumber.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }

        RegisterVo registerVo = new RegisterVo();
        registerVo.setSchoolName(schoolName);
        registerVo.setStudentID(Long.valueOf(studentId));
        registerVo.setUsername(username);
        registerVo.setPassword(password);
        registerVo.setPhone(Long.valueOf(phoneNumber));

        String registerUrl = BASE_URL + "/app/register";
        RequestBody requestBody = RequestBody.create(
                gson.toJson(registerVo),
                MediaType.parse("application/json; charset=utf-8")
        );

        Request request = new Request.Builder()
                .url(registerUrl)
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RegisterActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String responseData = response.body().string();
                    Result<?> registerResult = gson.fromJson(
                            responseData,
                            new TypeToken<Result<?>>(){}.getType()
                    );

                    runOnUiThread(() -> {
                        if (registerResult.getCode() == 200) {
                            Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    registerResult.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }
}