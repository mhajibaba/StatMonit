package com.pec.mob.statmonit.layout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Notification;

import java.util.ArrayList;
import java.util.List;

public class NotificationRecyclerViewAdapter extends RecyclerView
        .Adapter<NotificationRecyclerViewAdapter
        .DataObjectHolder> {
    private List<Notification> mDataset;
    private static NotificationClickListener notificationClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView dateTime;
        TextView desc;

        public DataObjectHolder(View itemView) {
            super(itemView);
            label = (TextView) itemView.findViewById(R.id.txtNotifyText);
            dateTime = (TextView) itemView.findViewById(R.id.txtNotifyTitle);
            desc = (TextView) itemView.findViewById(R.id.txtNotifyDesc);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            notificationClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(NotificationClickListener myClickListener) {
        this.notificationClickListener = myClickListener;
    }

    public NotificationRecyclerViewAdapter(List<Notification> myDataset) {
        if(myDataset!=null) {
            mDataset = myDataset;
        }
        else {
            mDataset = new ArrayList<>();
        }
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notify_card_view, parent, false);

        return new DataObjectHolder(view);
    }

    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {
        if(mDataset!=null) {
            holder.label.setText(mDataset.get(position).getTitle());
            holder.dateTime.setText(mDataset.get(position).getDate());
            String description = mDataset.get(position).getText();
            String desc;
            String[] lines = description.split("\n");
            if(lines.length>2) {
                desc = lines[3];
            }else {
                desc = description;
            }
            holder.desc.setText(desc);
        }
    }

    public void addItem(Notification dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface NotificationClickListener {
        void onItemClick(int position, View v);
    }
}