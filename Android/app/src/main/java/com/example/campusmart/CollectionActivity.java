package com.example.campusmart;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.R;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.campusmart.adapter.CollectionAdapter;
import com.example.campusmart.adapter.MyGoodsAdapter;

public class CollectionActivity extends AppCompatActivity implements CollectionAdapter.OnButtonClickListener {
    private RecyclerView rvCollection;
    private CollectionAdapter adapter;
    private List<CollectionAdapter.Goods> goodsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collection);

        ImageView ivBack = findViewById(R.id.iv_back);
        rvCollection = findViewById(R.id.rv_collection_history);

        initGoodsData();
        adapter = new CollectionAdapter(goodsList, this);
        rvCollection.setLayoutManager(new LinearLayoutManager(this));
        rvCollection.setAdapter(adapter);

        ivBack.setOnClickListener(v -> finish());
    }

    private void initGoodsData() {
        goodsList = new ArrayList<>();
        // 添加书籍商品
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.placeholder,
                "book",
                "I'm about to graduate and have found quite a few idle books.",
                "70% new",
                "¥ 30"
        ));
        // 添加键盘商品
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.keyboard,
                "99% new keyboard",
                "Wooting magnetic axis keyboard. I just bought it last week. It's too ugly",
                "99% new",
                "¥ 699"
        ));
        // 添加鼠标商品
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.mouse,
                "Has anyone bought my mouse?",
                "I'm about to graduate and have found quite a few idle books.",
                "80% new",
                "¥ 280"
        ));
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.placeholder,
                "book",
                "I'm about to graduate and have found quite a few idle books.",
                "70% new",
                "¥ 30"
        ));
        // 添加键盘商品
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.keyboard,
                "99% new keyboard",
                "Wooting magnetic axis keyboard. I just bought it last week. It's too ugly",
                "99% new",
                "¥ 699"
        ));
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.keyboard,
                "99% new keyboard",
                "Wooting magnetic axis keyboard. I just bought it last week. It's too ugly",
                "99% new",
                "¥ 699"
        ));
        // 添加鼠标商品
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.mouse,
                "Has anyone bought my mouse?",
                "I'm about to graduate and have found quite a few idle books.",
                "80% new",
                "¥ 280"
        ));
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.placeholder,
                "book",
                "I'm about to graduate and have found quite a few idle books.",
                "70% new",
                "¥ 30"
        ));
        // 添加键盘商品
        goodsList.add(new CollectionAdapter.Goods(
                R.drawable.keyboard,
                "99% new keyboard",
                "Wooting magnetic axis keyboard. I just bought it last week. It's too ugly",
                "99% new",
                "¥ 699"
        ));
    }

    @Override
    public void onDeleteClick(int position) {
        goodsList.remove(position);
        adapter.notifyItemRemoved(position);
        adapter.notifyItemRangeChanged(position, goodsList.size());
        Toast.makeText(this, "delete successful", Toast.LENGTH_SHORT).show();
    }

}