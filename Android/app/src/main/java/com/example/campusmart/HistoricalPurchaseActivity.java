package com.example.campusmart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.R;
import com.example.campusmart.adapter.HistoricalPurchaseAdapter;

import java.util.ArrayList;
import java.util.List;

public class HistoricalPurchaseActivity extends AppCompatActivity implements HistoricalPurchaseAdapter.OnDeleteClickListener {
    private RecyclerView rvHistoricalPurchase;
    private HistoricalPurchaseAdapter adapter;
    private List<HistoricalPurchaseAdapter.Purchase> purchaseList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historical_purchase);

        // 初始化控件
        ImageView ivBack = findViewById(R.id.iv_back);
        rvHistoricalPurchase = findViewById(R.id.rv_collection_history);

        // 初始化订单数据
        initPurchaseData();
        // 初始化RecyclerView
        adapter = new HistoricalPurchaseAdapter(purchaseList, this);
        rvHistoricalPurchase.setLayoutManager(new LinearLayoutManager(this));
        rvHistoricalPurchase.setAdapter(adapter);

        // 返回按钮点击事件
        ivBack.setOnClickListener(v -> finish());
    }

    // 初始化订单模拟数据
    private void initPurchaseData() {
        purchaseList = new ArrayList<>();
        // 添加书籍订单
        purchaseList.add(new HistoricalPurchaseAdapter.Purchase(
                R.drawable.placeholder,
                "book",
                "I'm about to graduate and have found quite a few idle books.",
                "70% new",
                "¥ 30"
        ));
        // 添加键盘订单
        purchaseList.add(new HistoricalPurchaseAdapter.Purchase(
                R.drawable.keyboard,
                "99% new keyboard",
                "Wooting magnetic axis keyboard. I just bought it last week. It's too ugly",
                "99% new",
                "¥ 699"
        ));
        // 添加鼠标订单
        purchaseList.add(new HistoricalPurchaseAdapter.Purchase(
                R.drawable.mouse,
                "Has anyone bought my mouse?",
                "I'm about to graduate and have found quite a few idle books.",
                "80% new",
                "¥ 280"
        ));
    }

    // 删除按钮点击回调
    @Override
    public void onDeleteClick(int position) {
        // 从列表中移除订单并刷新适配器
        purchaseList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, purchaseList.size());
        Toast.makeText(this, "delete successful", Toast.LENGTH_SHORT).show();
    }
}