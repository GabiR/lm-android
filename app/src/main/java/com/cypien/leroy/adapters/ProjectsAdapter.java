package com.cypien.leroy.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.models.Project;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by Alex on 21/10/15.
 */
public class ProjectsAdapter extends ArrayAdapter<Project>{

    private LayoutInflater inflater;

    public ProjectsAdapter(Activity context) {
        super(context, R.layout.project_item);
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class ViewHolder {
        ImageView image;
        CircularImageView avatar;
        TextView projectName;
        TextView userName;
        TextView rating;
        TextView views;
        TextView comments;
        TextView share;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        ViewHolder holder;
        final Project project = getItem(position);

        if (convertView == null) {
            view = inflater.inflate(R.layout.project_item, parent,false);
            holder = new ViewHolder();
            holder.image= (ImageView) view.findViewById(R.id.main_image);
            holder.avatar= (CircularImageView) view.findViewById(R.id.avatar);
            holder.projectName = (TextView) view.findViewById(R.id.project_name);
            holder.userName = (TextView) view.findViewById(R.id.user_name);
            holder.rating = (TextView) view.findViewById(R.id.like_n);
            holder.views = (TextView) view.findViewById(R.id.viz_n);
            holder.comments = (TextView) view.findViewById(R.id.comm_n);
            holder.share = (TextView) view.findViewById(R.id.share_button1);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final String type ;
        if (project.isBlog())
            type="entries";
        else
            type="content";
        holder.share.setFocusable(false);
        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, project.getTitle());
                shareIntent.putExtra(android.content.Intent.EXTRA_TEXT, "http://www.facem-facem.ro/"+type+"/" + project.getBlogid());
                getContext().startActivity(Intent.createChooser(shareIntent, project.getTitle()));
            }
        });

        if(project.getImage()==null){
            holder.avatar.setImageResource(R.drawable.temp_pic);
        }else
            holder.image.setImageBitmap(project.getImage());

        if(project.getAvatar()==null){
            holder.avatar.setImageResource(R.drawable.unknown);
        }else
            holder.avatar.setImageBitmap(project.getAvatar());

        holder.projectName.setText(project.getTitle());
        holder.userName.setText("realizat de " + project.getUsername());
        holder.rating.setText(project.getRating());
        holder.views.setText(project.getViews());
        holder.comments.setText(project.getComments());
        return view;
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Project getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public void add(Project object) {
        super.add(object);
        object.buildImage();
        object.buildAvatar();
    }
}
