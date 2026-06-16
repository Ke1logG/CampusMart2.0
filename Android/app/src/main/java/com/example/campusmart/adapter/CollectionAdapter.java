package com.example.campusmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.R;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.GoodsViewHolder> {
    public static class Goods {
        public int imgRes;
        public String title;
        public String desc;
        public String newDegree;
        public String price;

        public Goods(int imgRes, String title, String desc, String newDegree, String price) {
            this.imgRes = imgRes;
            this.title = title;
            this.desc = desc;
            this.newDegree = newDegree;
            this.price = price;
        }
    }

    private List<Goods> goodsList;
    private OnButtonClickListener listener;

    public interface OnButtonClickListener {
        void onDeleteClick(int position);
    }

    public CollectionAdapter(List<Goods> goodsList, OnButtonClickListener listener) {
        this.goodsList = goodsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public GoodsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collections_history, parent, false);
        return new GoodsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoodsViewHolder holder, int position) {
        Goods goods = goodsList.get(position);
        holder.ivGoodsImg.setImageResource(goods.imgRes);
        holder.tvTitle.setText(goods.title);
        holder.tvDesc.setText(goods.desc);
        holder.tvNewDegree.setText(goods.newDegree);
        holder.tvPrice.setText(goods.price);
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return goodsList.size();
    }

    static class GoodsViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsImg;
        TextView tvTitle, tvDesc, tvNewDegree, tvPrice;
        Button btnDelete;

        public GoodsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGoodsImg = itemView.findViewById(R.id.iv_goods_img);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvDesc = itemView.findViewById(R.id.tv_desc);
            tvNewDegree = itemView.findViewById(R.id.tv_new_degree);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}