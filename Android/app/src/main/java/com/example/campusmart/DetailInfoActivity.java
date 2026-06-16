package com.example.campusmart;

import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.example.campusmart.R;
import com.example.campusmart.result.Result;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DetailInfoActivity extends AppCompatActivity {
    private static final String TAG = "DetailInfoActivity";
    private static final int PICK_IMAGE_REQUEST = 1;
    private String BASE_URL;
    private ImageView ivBack, ivAddPic;
    private EditText etTitle, etAppearance, etPrice, etDesc;
    private Button btnSubmit;
    private Uri selectedImageUri;
    private String token;
    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_info);

        // 从SharedPreferences获取token和userId
        SharedPreferences sp = getSharedPreferences("user_info", MODE_PRIVATE);
        token = sp.getString("token", "");
        userId = sp.getLong("user_id", 0);

        // 验证用户登录状态
        if (token.isEmpty() || userId == 0) {
            Toast.makeText(this, "请先登录", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        BASE_URL = getResources().getString(R.string.base_url) + "/app/goods";

        // 初始化控件
        initViews();

        // 设置控件点击事件
        setClickEvents();
    }

    private void initViews() {
        ivBack = findViewById(R.id.iv_back);
        ivAddPic = findViewById(R.id.iv_add_pic);
        etTitle = findViewById(R.id.et_title);
        etAppearance = findViewById(R.id.et_appearance);
        etPrice = findViewById(R.id.et_price);
        etDesc = findViewById(R.id.et_desc);
        btnSubmit = findViewById(R.id.btn_submit);
    }

    private void setClickEvents() {
        ivBack.setOnClickListener(v -> finish());
        ivAddPic.setOnClickListener(v -> openImageChooser());
        btnSubmit.setOnClickListener(v -> submitGoodsInfo());
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                Glide.with(this)
                        .load(selectedImageUri)
                        .into(ivAddPic);
            }
        }
    }

    private void submitGoodsInfo() {
        String title = etTitle.getText().toString().trim();
        String appearance = etAppearance.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();
        if (title.isEmpty() || appearance.isEmpty() || priceStr.isEmpty() || desc.isEmpty()) {
            Toast.makeText(this, "请填写完整信息", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "请选择商品图片", Toast.LENGTH_SHORT).show();
            return;
        }
        long price;
        try {
            price = Long.parseLong(priceStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "价格格式错误", Toast.LENGTH_SHORT).show();
            return;
        }
        new Thread(() -> {
            try {
                long goodsId = addGoods(title, appearance, desc, price, userId);
                if (goodsId <= 0) {
                    runOnUiThread(() -> Toast.makeText(DetailInfoActivity.this, "商品创建失败", Toast.LENGTH_SHORT).show());
                    return;
                }

                String imagePath = getRealPathFromUri(selectedImageUri);
                boolean uploadSuccess = uploadGoodsImage(imagePath, goodsId);

                runOnUiThread(() -> {
                    if (uploadSuccess) {
                        Toast.makeText(DetailInfoActivity.this, "发布成功", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(DetailInfoActivity.this, "图片上传失败", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (Exception e) {
                Log.e(TAG, "提交失败: " + e.getMessage());
                runOnUiThread(() -> Toast.makeText(DetailInfoActivity.this, "网络请求失败", Toast.LENGTH_SHORT).show());
            }
        }).start();
    }

    private long addGoods(String title, String appearance, String desc, long price, long publishUserID) throws IOException {
        OkHttpClient client = new OkHttpClient();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        String formattedTime = sdf.format(new Date());

        String json = "{" +
                "\"title\":\"" + title + "\"," +
                "\"appearance\":\"" + appearance + "\"," +
                "\"itemDescription\":\"" + desc + "\"," +
                "\"price\":" + price + "," +
                "\"publishTime\":\"" + formattedTime + "\"," +
                "\"publishUserID\":" + publishUserID +
                "}";

        RequestBody requestBody = RequestBody.create(json, MediaType.parse("application/json"));

        Request request = new Request.Builder()
                .url(BASE_URL + "/add")
                .addHeader("Content-Type", "application/json")
                .addHeader("access-token", token)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("添加商品失败: " + response.code());
            }

            String responseData = response.body().string();
            Log.d(TAG, "添加商品返回: " + responseData);

            Gson gson = new Gson();
            Result<Long> result = gson.fromJson(responseData, new com.google.gson.reflect.TypeToken<Result<Long>>(){}.getType());

            if (result.getCode() != 200) {
                throw new IOException("添加商品失败: " + result.getMessage());
            }

            Long goodsId = result.getData();
            if (goodsId == null) {
                throw new IOException("返回商品ID为空");
            }
            return goodsId;
        }
    }

    private boolean uploadGoodsImage(String imagePath, long goodsId) throws IOException {
        OkHttpClient client = new OkHttpClient();

        File originalFile = new File(imagePath);
        File compressedFile = compressImage(Uri.fromFile(originalFile));

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", compressedFile.getName(),
                        RequestBody.create(compressedFile, MediaType.parse("image/*")))
                .addFormDataPart("GoodID", String.valueOf(goodsId))
                .build();

        Request request = new Request.Builder()
                .url(BASE_URL + "/file/uploadGoodsPicture")
                .addHeader("access-token", token)
                .post(requestBody)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("图片上传失败: " + response.code());
            }

            String responseData = response.body().string();
            Log.d(TAG, "图片上传返回: " + responseData);
            return responseData.contains("\"code\":200");
        }
    }

    private String getRealPathFromUri(Uri uri) {
        String path = null;
        if (ContentResolver.SCHEME_CONTENT.equals(uri.getScheme())) {
            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA},
                    null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                path = cursor.getString(columnIndex);
                cursor.close();
            }
        } else if (ContentResolver.SCHEME_FILE.equals(uri.getScheme())) {
            path = uri.getPath();
        }
        return path;
    }

    private File compressImage(Uri uri) throws IOException {
        // 获取原始图片
        InputStream is = getContentResolver().openInputStream(uri);
        Bitmap originalBitmap = BitmapFactory.decodeStream(is);
        is.close();

        final long TARGET_SIZE = 500 * 1024;

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int quality = 100;
        originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);

        while (out.toByteArray().length > TARGET_SIZE && quality > 10) {
            out.reset();
            quality -= 10;
            originalBitmap.compress(Bitmap.CompressFormat.JPEG, quality, out);
        }

        if (out.toByteArray().length > TARGET_SIZE) {
            out.reset();
            float scale = (float) Math.sqrt((double) TARGET_SIZE / out.toByteArray().length);
            int newWidth = (int) (originalBitmap.getWidth() * scale);
            int newHeight = (int) (originalBitmap.getHeight() * scale);

            Bitmap scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, true);
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, out);
            scaledBitmap.recycle();
        }

        originalBitmap.recycle();

        File compressedFile = File.createTempFile("compressed", ".jpg", getCacheDir());
        FileOutputStream fos = new FileOutputStream(compressedFile);
        fos.write(out.toByteArray());
        fos.flush();
        fos.close();

        Log.d(TAG, "压缩后图片大小: " + compressedFile.length() + " bytes");
        return compressedFile;
    }

}