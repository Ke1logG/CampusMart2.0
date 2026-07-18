package com.example.campusmart.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.campusmart.R;
import com.example.campusmart.entity.WalletFlow;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class WalletFlowAdapter extends RecyclerView.Adapter<WalletFlowAdapter.FlowViewHolder> {

    private List<WalletFlow> flowList;
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());

    public WalletFlowAdapter(List<WalletFlow> flowList) {
        this.flowList = flowList;
    }

    @NonNull
    @Override
    public FlowViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new FlowViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FlowViewHolder holder, int position) {
        WalletFlow flow = flowList.get(position);
        holder.tvTitle.setText(flow.getFlowType() + "  " + flow.getAmount());
        holder.tvSubtitle.setText((flow.getRemark() != null ? flow.getRemark() : "") + "\n" + DATE_FORMAT.format(flow.getCreateTime()));
    }

    @Override
    public int getItemCount() {
        return flowList.size();
    }

    static class FlowViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;

        public FlowViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(android.R.id.text1);
            tvSubtitle = itemView.findViewById(android.R.id.text2);
        }
    }
}
