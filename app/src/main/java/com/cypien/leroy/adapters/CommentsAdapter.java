package com.cypien.leroy.adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.ProjectFragment;
import com.cypien.leroy.models.Comment;
import com.cypien.leroy.utils.Connections;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

/**
 * Created by Alex on 29/10/15.
 */
public class CommentsAdapter extends ArrayAdapter<Comment> {

    private LayoutInflater inflater;
    private SharedPreferences sp;
    private boolean ok;
    private String blogId;
    private Activity activity;
    private String type;
    private FragmentManager fragmentManager;

    public CommentsAdapter(FragmentManager fm, Activity context, String blogId, String type) {
        super(context, R.layout.comment_item);
        this.blogId = blogId;
        this.type = type;
        this.activity = context;
        this.fragmentManager = fm;
        sp = context.getSharedPreferences("com.cypien.leroy_preferences", context.MODE_PRIVATE);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (!Connections.isNetworkConnected(context)) {
            Type myObjectType = new TypeToken<Integer>() {
            }.getType();
            Integer nrComments = (Integer) LeroyApplication.getCacheManager().get("comments_nr" + blogId, Integer.class, myObjectType);
            myObjectType = new TypeToken<Comment>() {
            }.getType();
            if (nrComments != null) {
                for (int i = 0; i < nrComments; i++) {
                    Comment comment = (Comment) LeroyApplication.getCacheManager().get("comment" + blogId + i, Comment.class, myObjectType);
                    add(comment);
                }
            }
        } else {
            getComments();
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        final ViewHolder holder;
        final Comment comment = getItem(position);

        if (convertView == null) {
            view = inflater.inflate(R.layout.comment_item, parent, false);
            holder = new ViewHolder();
            holder.avatar = (ImageView) view.findViewById(R.id.avatar);
            holder.comment = (TextView) view.findViewById(R.id.comment);
            holder.userName = (TextView) view.findViewById(R.id.user_name);
            holder.rating = (TextView) view.findViewById(R.id.comm_like);
            holder.date = (TextView) view.findViewById(R.id.publish_date);
            holder.answer = (Button) view.findViewById(R.id.answer);
            holder.like = (Button) view.findViewById(R.id.like_comment);
            holder.delete = (Button) view.findViewById(R.id.delete);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        holder.answer.setFocusable(false);
        holder.like.setFocusable(false);
        holder.delete.setFocusable(false);
        if (!sp.getBoolean("isConnected", false)) {
            holder.like.setVisibility(View.GONE);
            holder.delete.setVisibility(View.GONE);
            holder.answer.setVisibility(View.GONE);
            view.findViewById(R.id.hands).setVisibility(View.GONE);
        }
        if (!comment.getUserid().equals(sp.getString("userid", "")))
            holder.delete.setVisibility(View.GONE);
        if (comment.isLiked())
            holder.like.setVisibility(View.GONE);
        holder.answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddCommentDialog();
            }
        });
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeroyApplication.getInstance().makeRequest(type + "_like_comment", sp.getString("endpointCookie", ""), sp.getString("userid", ""), comment.getBlogtextid());
                v.setVisibility(View.GONE);
                comment.setLiked(true);
                comment.setRating("" + (Integer.parseInt(comment.getRating()) + 1));
                LeroyApplication.getCacheManager().put("comment" + blogId + position, comment);
                holder.rating.setText("" + (Integer.parseInt(holder.rating.getText().toString()) + 1));
            }
        });
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeroyApplication.getInstance().makeRequest(type + "_delete_comment", sp.getString("endpointCookie", ""), sp.getString("userid", ""), comment.getBlogtextid());
                LeroyApplication.getCacheManager().unset("comment" + blogId + position);
                remove(getItem(position));
                LeroyApplication.getCacheManager().put("comments_nr" + blogId, getCount());
                notifyDataSetChanged();
                ((ProjectFragment) fragmentManager.findFragmentByTag("project")).setListViewHeightBasedOnChildren();

            }
        });

        holder.userName.setText(comment.getUser().getUserName());
        if (comment.getRating() == null || comment.getRating().equals(""))
            holder.rating.setText("0");
        else
            holder.rating.setText(comment.getRating());
        if (type.equals("CMS")) {
            holder.rating.setVisibility(View.GONE);
            holder.like.setVisibility(View.GONE);
        }
        comment.getUser().buildImage();
        if (comment.getUser().getAvatar() == null) {
            holder.avatar.setImageResource(R.drawable.unknown);
        } else
            holder.avatar.setImageBitmap(comment.getUser().getAvatar());
        holder.comment.setText(comment.getPagetext());
        holder.date.setText(comment.getDate());
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Comment getItem(int position) {
        return super.getItem(position);
    }

    public void showAddCommentDialog() {
        final Dialog dialog = new Dialog(getContext());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.add_comment_dialog);
        dialog.setCancelable(true);

        TextView postButton = (TextView) dialog.findViewById(R.id.add_comment);

        final EditText comment = (EditText) dialog.findViewById(R.id.comment);


        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Connections.isNetworkConnected(activity)) {
                    Toast.makeText(getContext(), "Vă rugăm să vă conectați la Internet pentru a putea comenta!", Toast.LENGTH_LONG).show();
                    return;
                }

                if (comment.getText().toString().equals("")) {

                    Toast.makeText(getContext(), "Vă rugăm să completați cu comentariul dumneavoastră!", Toast.LENGTH_LONG).show();
                    return;
                }

                JSONObject jsn = LeroyApplication.getInstance().makeRequest(type + "_add_comment", sp.getString("endpointCookie", ""), sp.getString("userid", ""), blogId, comment.getText().toString());


                getComments();
                notifyDataSetChanged();
                ((ProjectFragment) fragmentManager.findFragmentByTag("project")).setListViewHeightBasedOnChildren();
                dialog.dismiss();

            }
        });


        dialog.show();
    }

    private void getComments() {
        try {
            clear();
            JSONObject response = LeroyApplication.getInstance().makeRequest(type + "_get_comments", sp.getString("endpointCookie", ""), sp.getString("userid", ""), blogId);
            JSONArray resultArray = response.getJSONArray("result");
            for (int i = 0; i < resultArray.length(); i++) {
                Comment comment = new Comment();
                comment.setType(type);
                comment = comment.fromJson(resultArray.getJSONObject(i), sp.getString("endpointCookie", ""));
                LeroyApplication.getCacheManager().put("comment" + blogId + i, comment);
                add(comment);
                LeroyApplication.getCacheManager().put("comments_nr" + blogId, getCount());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder {
        ImageView avatar;
        TextView userName;
        TextView date;
        TextView rating;
        TextView comment;
        Button answer;
        Button like;
        Button delete;
    }

}
