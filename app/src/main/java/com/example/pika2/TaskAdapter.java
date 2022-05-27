package com.example.pika2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.Viewholder> {

    private final Context context;
    private final ArrayList<TaskModel> taskModelArrayList;
    private final OnItemListener mOnItemListener;

    public TaskAdapter(Context context, ArrayList<TaskModel> taskModelArrayList, OnItemListener onItemListener) {
        this.context = context;
        this.taskModelArrayList = taskModelArrayList;
        this.mOnItemListener = onItemListener;
    }

    @NonNull
    @Override
    public TaskAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // to inflate the layout for each item of recycler view.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_task, parent, false);
        return new Viewholder(view, mOnItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.Viewholder holder, int position) {
        // to set data to textview and imageview of each card layout
        TaskModel model = taskModelArrayList.get(position);
        holder.taskNameTV.setText(model.getTask_name());
        holder.taskIV.setImageResource(model.getTask_img());
    }

    @Override
    public int getItemCount() {
        // this method is used for showing number
        // of card items in recycler view.
        return taskModelArrayList.size();
    }

    public static class Viewholder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView taskIV;
        TextView taskNameTV;
        OnItemListener onItemListener;

        public Viewholder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            taskIV = itemView.findViewById(R.id.taskImg);
            taskNameTV = itemView.findViewById(R.id.taskName);
            this.onItemListener = onItemListener;

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    public interface OnItemListener {
        void onItemClick(int position);
    }
}
