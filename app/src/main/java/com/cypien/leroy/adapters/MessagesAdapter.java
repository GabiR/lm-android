package com.cypien.leroy.adapters;/*
 * Created by Alex on 26.09.2016.
 */

import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Message;

import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesListViewHolder> {
    private List<Message> messages;

    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;
    }

    @Override
    public MessagesListViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_row, parent, false);

        return new MessagesListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MessagesListViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.title.setText(message.getTitle());
       holder.content.setText(message.getContent());
        holder.date.setText(message.getDate());
        //TODO icon
        if(message.isRead()){
            holder.title.setTypeface(Typeface.DEFAULT_BOLD);
        }
        else{
            holder.title.setTypeface(Typeface.DEFAULT);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class MessagesListViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public ImageView icon;
        public TextView date;

        public MessagesListViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            content = (TextView) view.findViewById(R.id.content);
            icon = (ImageView) view.findViewById(R.id.icon);
            date = (TextView) view.findViewById(R.id.date);
        }
    }


}
