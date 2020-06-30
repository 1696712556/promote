package com.kx.promote.ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.kx.promote.R;
import com.kx.promote.bean.Group;

import java.util.ArrayList;
import java.util.List;

public class GroupRecyclerViewAdapter extends RecyclerView.Adapter<GroupRecyclerViewAdapter.ViewHolder> {
    private List<Group> groupList = new ArrayList<>();
    private Context mContext;

    public GroupRecyclerViewAdapter(List<Group> dataList){
        this.groupList = dataList;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_group,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Group group = groupList.get(position);
        if(group==null)
            return;
        holder.idView.setText(""+group.getId());
        //holder.orderNumberView.setText(group.getOrderlist().size());

    }

    @Override
    public int getItemCount() {
        return this.groupList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView idView;
        private TextView orderNumberView;
        private TextView prepriceView;
        private TextView actpriceView;
        private TextView timeView;
        private TextView customerView;
        private TextView stateView;
        public ViewHolder(View itemView) {
            super(itemView);
            idView = (TextView) itemView.findViewById(R.id.group_id);
            orderNumberView = (TextView) itemView.findViewById(R.id.group_order_number);
            prepriceView = (TextView) itemView.findViewById(R.id.group_preprice);
            actpriceView = (TextView) itemView.findViewById(R.id.group_actprice);
            timeView = (TextView) itemView.findViewById(R.id.group_time);
            customerView = (TextView) itemView.findViewById(R.id.group_customer);
            stateView = (TextView) itemView.findViewById(R.id.group_state);
        }
    }
}
