package com.example.campusmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.R;
import java.util.List;

public class GoodsDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int TYPE_USER_INFO = 0;
    public static final int TYPE_PRICE_TITLE = 1;
    public static final int TYPE_DESC = 2;
    public static final int TYPE_GOODS_IMG = 3;
    private List<GoodsDetailItem> dataList;

    public GoodsDetailAdapter(List<GoodsDetailItem> dataList) {
        this.dataList = dataList;
    }

    // 根据位置返回条目类型
    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).type;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case TYPE_USER_INFO:
                return new UserInfoViewHolder(inflater.inflate(R.layout.item_user_info, parent, false));
            case TYPE_PRICE_TITLE:
                return new PriceTitleViewHolder(inflater.inflate(R.layout.item_price_title, parent, false));
            case TYPE_DESC:
                return new DescViewHolder(inflater.inflate(R.layout.item_desc, parent, false));
            case TYPE_GOODS_IMG:
                return new GoodsImgViewHolder(inflater.inflate(R.layout.item_goods_img, parent, false));
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        GoodsDetailItem item = dataList.get(position);
        switch (getItemViewType(position)) {
            case TYPE_USER_INFO:
                ((UserInfoViewHolder) holder).bind(item);
                break;
            case TYPE_PRICE_TITLE:
                ((PriceTitleViewHolder) holder).bind(item);
                break;
            case TYPE_DESC:
                ((DescViewHolder) holder).bind(item);
                break;
            case TYPE_GOODS_IMG:
                ((GoodsImgViewHolder) holder).bind(item);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    protected static class UserInfoViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivAvatar;
        TextView tvUsername, tvNewDegree;

        public UserInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);
            tvUsername = itemView.findViewById(R.id.tv_username);
            tvNewDegree = itemView.findViewById(R.id.tv_new_degree);
        }

        public void bind(GoodsDetailItem item) {
            ivAvatar.setImageResource(item.avatarRes);
            tvUsername.setText(item.username);
            tvNewDegree.setText(item.newDegree);
        }
    }

    static class PriceTitleViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrice, tvTitle;

        public PriceTitleViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrice = itemView.findViewById(R.id.tv_price);
            tvTitle = itemView.findViewById(R.id.tv_title);
        }

        public void bind(GoodsDetailItem item) {
            tvPrice.setText(item.price);
            tvTitle.setText(item.title);
        }
    }

    static class DescViewHolder extends RecyclerView.ViewHolder {
        TextView tvDesc;

        public DescViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDesc = itemView.findViewById(R.id.tv_desc);
        }

        public void bind(GoodsDetailItem item) {
            tvDesc.setText(item.desc);
        }
    }

    protected static class GoodsImgViewHolder extends RecyclerView.ViewHolder {
        public ImageView ivGoods;

        public GoodsImgViewHolder(@NonNull View itemView) {
            super(itemView);
            ivGoods = itemView.findViewById(R.id.iv_goods);
        }

        public void bind(GoodsDetailItem item) {
            ivGoods.setImageResource(item.goodsImgRes);
        }
    }

    public static class GoodsDetailItem {
        int type;
        // 用户信息字段
        int avatarRes;
        String username, newDegree;
        // 价格标题字段
        String price, title;
        // 描述字段
        String desc;
        // 商品图片字段
        int goodsImgRes;

        // 构造方法（根据类型传参）
        public GoodsDetailItem(int type, int avatarRes, String username, String newDegree) {
            this.type = type;
            this.avatarRes = avatarRes;
            this.username = username;
            this.newDegree = newDegree;
        }

        public GoodsDetailItem(int type, String price, String title) {
            this.type = type;
            this.price = price;
            this.title = title;
        }

        public GoodsDetailItem(int type, String desc) {
            this.type = type;
            this.desc = desc;
        }

        public GoodsDetailItem(int type, int goodsImgRes) {
            this.type = type;
            this.goodsImgRes = goodsImgRes;
        }
    }
}