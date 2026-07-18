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
import com.example.campusmart.entity.Order;
import java.util.List;

public class HistoricalPurchaseAdapter extends RecyclerView.Adapter<HistoricalPurchaseAdapter.OrderViewHolder> {

    public interface OnOrderClickListener {
        void onItemClick(int position);
        void onActionClick(int position, String action);
    }

    private List<Order> orderList;
    private OnOrderClickListener listener;

    public HistoricalPurchaseAdapter(List<Order> orderList, OnOrderClickListener listener) {
        this.orderList = orderList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_collections_history, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        holder.tvTitle.setText(order.getGoodTitle() != null ? order.getGoodTitle() : "Unknown");
        holder.tvDesc.setText("Order #" + order.getOrderID());
        holder.tvNewDegree.setText(order.getStatus());
        holder.tvPrice.setText("¥ " + order.getAmount());

        if (order.getGoodPictureURL() != null && !order.getGoodPictureURL().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(order.getGoodPictureURL())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.placeholder)
                    .into(holder.ivGoodsImg);
        } else {
            holder.ivGoodsImg.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });

        holder.btnDelete.setText("Detail");
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(position);
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        ImageView ivGoodsImg;
        TextView tvTitle, tvDesc, tvNewDegree, tvPrice;
        Button btnDelete;

        public OrderViewHolder(@NonNull View itemView) {
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
