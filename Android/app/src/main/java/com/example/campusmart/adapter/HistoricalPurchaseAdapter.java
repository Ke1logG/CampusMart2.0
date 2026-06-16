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

public class HistoricalPurchaseAdapter extends RecyclerView.Adapter<HistoricalPurchaseAdapter.PurchaseViewHolder> {
    public static class Purchase {
        public int imgRes;
        public String title;
        public String desc;
        public String newDegree;
        public String price;

        public Purchase(int imgRes, String title, String desc, String newDegree, String price) {
            this.imgRes = imgRes;
            this.title = title;
            this.desc = desc;
            this.newDegree = newDegree;
            this.price = price;
        }
    }

    private List<Purchase> purchaseList;
    private OnDeleteClickListener listener;

    public interface OnDeleteClickListener {
        void onDeleteClick(int position);
    }

    public HistoricalPurchaseAdapter(List<Purchase> purchaseList, OnDeleteClickListener listener) {
        this.purchaseList = purchaseList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collections_history, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Purchase purchase = purchaseList.get(position);
        holder.ivGoodsImg.setImageResource(purchase.imgRes);
        holder.tvTitle.setText(purchase.title);
        holder.tvDesc.setText(purchase.desc);
        holder.tvNewDegree.setText(purchase.newDegree);
        holder.tvPrice.setText(purchase.price);
        holder.btnDelete.setOnClickListener(v -> listener.onDeleteClick(position));
    }

    @Override
    public int getItemCount() {
        return purchaseList.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsImg;
        TextView tvTitle, tvDesc, tvNewDegree, tvPrice;
        Button btnDelete;

        public PurchaseViewHolder(@NonNull View itemView) {
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