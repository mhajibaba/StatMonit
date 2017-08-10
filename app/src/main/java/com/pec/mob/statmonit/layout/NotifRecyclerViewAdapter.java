package com.pec.mob.statmonit.layout;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Message;
import com.pec.mob.statmonit.object.Notif;

import java.util.ArrayList;
import java.util.List;

public class NotifRecyclerViewAdapter extends RecyclerView
        .Adapter<NotifRecyclerViewAdapter
        .DataObjectHolder> {
    private List<Notif> mDataset;
    private static NotificationClickListener notificationClickListener;

    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {
        TextView label;
        TextView title;
        TextView desc;

        public DataObjectHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.txtNotifyTitle);
            label = (TextView) itemView.findViewById(R.id.txtNotifyText);
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

    public NotifRecyclerViewAdapter(List<Notif> myDataset) {
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
            holder.title.setText(mDataset.get(position).getTitle());
            holder.label.setText(mDataset.get(position).getDate());
            String description = mDataset.get(position).getMessage();
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

    public void addItem(Notif dataObj, int index) {
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