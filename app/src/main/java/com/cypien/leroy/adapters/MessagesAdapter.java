package com.cypien.leroy.adapters;/*
 * Created by Alex on 26.09.2016.
 */

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Message;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesListViewHolder> {
    private List<Message> messages;
    SimpleDateFormat sdfFirst;
    SimpleDateFormat sdf;
    public MessagesAdapter(List<Message> messages) {
        this.messages = messages;

        sdfFirst = new SimpleDateFormat("dd.MM.yyyy HH:mm");
        sdf = new SimpleDateFormat("MMM dd");
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

        Date date = null;
        try {
            date = sdfFirst.parse(message.getDate());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.date.setText(sdf.format(date));
        holder.title.setText(message.getTitle());
       holder.content.setText(message.getContent());
        //TODO icon
        if(!message.isRead()){
            holder.icon.setImageResource(R.drawable.unread_icon);
            holder.title.setTypeface(null,Typeface.BOLD);
            holder.content.setTypeface(null, Typeface.NORMAL);
            holder.date.setTypeface(null, Typeface.NORMAL);
            holder.date.setTextColor(Color.parseColor("#5FA437"));
        }
        else{
            holder.title.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            holder.content.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            holder.date.setTypeface(Typeface.create("sans-serif-light", Typeface.NORMAL));
            holder.date.setTextColor(Color.parseColor("#979797"));
            holder.icon.setImageResource(R.drawable.read_icon);
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
