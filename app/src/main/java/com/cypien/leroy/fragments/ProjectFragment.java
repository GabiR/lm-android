package com.cypien.leroy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.CommentsAdapter;
import com.cypien.leroy.adapters.ImagesAdapter;
import com.cypien.leroy.models.Project;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.viewpagerindicator.CirclePageIndicator;
import com.viewpagerindicator.PageIndicator;


/**
 * Created by Alex on 22/10/15.
 */
public class ProjectFragment extends Fragment{
    private View view;
    private CircularImageView avatar;
    private TextView userName;
    private TextView projectName;
    private TextView views;
    private TextView commentsNumber;
    private TextView rating;
    private TextView publishDate;
    private TextView time;
    private TextView cost;
    private TextView details;
    private Button likeButton;
    private TextView addCommentButton;
    private Project project;
    private SharedPreferences sp;
    private ImagesAdapter adapter;
    private ViewPager pager;
    private PageIndicator indicator;
    private CommentsAdapter commAdapter;
    private ListView commentsList;
    private String type;
    private int position;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.project_screen,container,false);
        project=(Project)getArguments().getSerializable("project");
        position=getArguments().getInt("position");
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);
        if(project.isBlog())
            type="project";
        else
            type="CMS";

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "ProjectFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText(project.getTitle());

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);


        avatar = (CircularImageView) view.findViewById(R.id.avatar);
        userName = (TextView) view.findViewById(R.id.user_name);
        projectName = (TextView) view.findViewById(R.id.project_name);
        views = (TextView) view.findViewById(R.id.viz_n);
        commentsNumber = (TextView) view.findViewById(R.id.comm_n);
        rating = (TextView) view.findViewById(R.id.like_n);
        publishDate = (TextView) view.findViewById(R.id.publish_date);
        time = (TextView) view.findViewById(R.id.project_time);
        cost = (TextView) view.findViewById(R.id.project_cost);
        details = (TextView) view.findViewById(R.id.project_details);
        if(project.getAvatar()==null){
            avatar.setImageResource(R.drawable.unknown);
        }else
            avatar.setImageBitmap(project.getAvatar());
        if(type.equals("CMS")){
            time.setVisibility(View.GONE);
            cost.setVisibility(View.GONE);
        }
        userName.setText(project.getUsername());
        projectName.setText(project.getTitle());
        views.setText(project.getViews());
        commentsNumber.setText(project.getComments());
        rating.setText(project.getRating());
        publishDate.setText("Publicat pe: " + project.getDate());
        time.setText("Durata proiectului: " + project.getDuration());
        cost.setText("Costuri: " + project.getCosts());
        details.setText(Html.fromHtml(project.getDescription()));

        adapter = new ImagesAdapter(getActivity(),project.getBlogid(),type);
        pager=(ViewPager)view.findViewById(R.id.pager);
        pager.setAdapter(adapter);
        indicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        indicator.setViewPager(pager);
        ((CirclePageIndicator) indicator).setSnap(true);
        ((CirclePageIndicator) indicator).setRadius(15);
        ((CirclePageIndicator) indicator).setStrokeColor(0xFF499840);
        ((CirclePageIndicator) indicator).setFillColor(0xFF56bd4f);

        likeButton = (Button)view.findViewById(R.id.like_project);
        if(project.isLiked())
            likeButton.setVisibility(View.GONE);
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeroyApplication.getInstance().makeRequest(type+"_like", sp.getString("userid", ""), project.getBlogid());
                v.setVisibility(View.GONE);
                rating.setText("" + (Integer.parseInt(rating.getText().toString()) + 1));
                project.setLiked(true);
                project.setRating(""+(Integer.parseInt(project.getRating())+1));
                LeroyApplication.getCacheManager().put("project_" + position, project);
            }
        });

        commentsList = (ListView)view.findViewById(R.id.comments);
        commAdapter = new CommentsAdapter(getFragmentManager(),getActivity(),project.getBlogid(),type);
        commentsList.setAdapter(commAdapter);
        setListViewHeightBasedOnChildren();

        addCommentButton = (TextView)view.findViewById(R.id.add_comment);
        addCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commAdapter.showAddCommentDialog();
            }
        });

        if(!sp.getBoolean("isConnected",false)){
            view.findViewById(R.id.not_connected).setVisibility(View.VISIBLE);
            likeButton.setVisibility(View.GONE);
            addCommentButton.setVisibility(View.GONE);
        }
        return view;
    }


    public void setListViewHeightBasedOnChildren() {
        if (commAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(commentsList.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < commAdapter.getCount(); i++) {
            view = commAdapter.getView(i, view, commentsList);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, ViewGroup.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = commentsList.getLayoutParams();
        params.height = totalHeight + (commentsList.getDividerHeight() * (commAdapter.getCount()))+100;
        commentsList.setLayoutParams(params);
        commentsList.requestLayout();
    }
}
