package com.example.campusmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.campusmart.R;
import com.example.campusmart.entity.Goods;
import java.util.List;

public class CollectionAdapter extends RecyclerView.Adapter<CollectionAdapter.GoodsViewHolder> {

    public interface OnButtonClickListener {
        void onDeleteClick(int position);
        void onItemClick(int position);
    }

    private List<Goods> goodsList;
    private OnButtonClickListener listener;

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
        holder.tvTitle.setText(goods.getTitle() != null ? goods.getTitle() : "Unknown");
        holder.tvDesc.setText(goods.getItemDescription() != null ? goods.getItemDescription() : "");
        holder.tvNewDegree.setText(goods.getAppearance() != null ? goods.getAppearance() : "");
        holder.tvPrice.setText("¥ " + goods.getPrice());

        String imageUrl = null;
        // Goods entity does not have pictureURL, fallback to placeholder
        holder.ivGoodsImg.setImageResource(R.drawable.placeholder);

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDeleteClick(position);
        });
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
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
