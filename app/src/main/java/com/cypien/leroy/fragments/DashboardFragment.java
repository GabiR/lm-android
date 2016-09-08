package com.cypien.leroy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Project;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Alex on 8/6/2015.
 */
public class DashboardFragment extends Fragment{
    private View view;
    private ImageView forumButton;
    private ImageView topButton;
    private ImageView storesInfoButton;
    private ImageView contestButton;
    private RelativeLayout lastProjectsButton;
    private ImageView logoProject;
    private FragmentManager fragmentManager;


    private SharedPreferences sp;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view= getActivity().getLayoutInflater().inflate(R.layout.dashboard_screen, container, false);
        fragmentManager = getFragmentManager();
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);


        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Bun venit!");
        ((ImageView)actionBarView.findViewById(R.id.logo)).setImageResource(R.drawable.dashboard_logo);
        actionBarView.findViewById(R.id.back_button).setVisibility(View.GONE);
        if(sp.getBoolean("isConnected",false)){
            ((TextView) actionBarView.findViewById(R.id.title)).setText("Bun venit, "+sp.getString("lastname", "")+"!");
        }

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "DashboardFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        // controleaza afisarea ultimului proiect
        if(Connections.isNetworkConnected(getActivity())){
            Project project = getLastProject();
            project.buildImage();
            project.buildAvatar();
            ((TextView)view.findViewById(R.id.project_name)).setText(project.getTitle());
            ((TextView)view.findViewById(R.id.user_name)).setText(project.getUsername());
            ((TextView)view.findViewById(R.id.viz_n)).setText(project.getViews());
            ((TextView)view.findViewById(R.id.comm_n)).setText(project.getComments());
            ((TextView)view.findViewById(R.id.like_n)).setText(project.getRating());
            ((ImageView)view.findViewById(R.id.main_image)).setImageBitmap(project.getImage());
            ((ImageView)view.findViewById(R.id.avatar)).setImageBitmap(project.getAvatar());
        }else {
            logoProject = (ImageView)view.findViewById(R.id.last_project_no_internet);
            logoProject.setVisibility(View.VISIBLE);
        }

        contestButton=(ImageView)view.findViewById(R.id.see_contest);
        contestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isNetworkConnected(getActivity())){
                    ContestFragment f = new ContestFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, f).addToBackStack(null);
                    transaction.commit();
                }else {
                    new NotificationDialog(getActivity(),"Pentru a vedea secțiunea de concurs, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        lastProjectsButton=(RelativeLayout)view.findViewById(R.id.last_projects);
        lastProjectsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isNetworkConnected(getActivity())){
                    ProjectsListFragment f = new ProjectsListFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, f).addToBackStack(null);
                    transaction.commit();
                }else {
                    new NotificationDialog(getActivity(),"Pentru a vedea ultimele proiecte, vă rugăm să vă conectați la internet!").show();
                }
            }
        });
        logoProject = (ImageView)view.findViewById(R.id.last_project_no_internet);

        topButton=(ImageView)view.findViewById(R.id.topul_mesterilor);
        topButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isNetworkConnected(getActivity())){
                    TopUsersFragment f = new TopUsersFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, f).addToBackStack(null);
                    transaction.commit();
                }else {
                    new NotificationDialog(getActivity(),"Pentru a vedea topul utilizatorilor, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        forumButton =(ImageView)view.findViewById(R.id.ultimele_discutii);
        forumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isNetworkConnected(getActivity())){
                    DiscussionsFragment f = new DiscussionsFragment();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragment_container, f).addToBackStack(null);
                    transaction.commit();
                }else {
                    new NotificationDialog(getActivity(),"Pentru a vedea forumul nostru, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        storesInfoButton=(ImageView)view.findViewById(R.id.info_magazine);
        storesInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoStoresFragment f = new InfoStoresFragment();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.fragment_container, f).addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private Project getLastProject(){
        Project project = new Project();
        JSONObject response = LeroyApplication.getInstance().makeRequest("project_get_combined",sp.getString("endpointCookie", ""),sp.getString("userid",""),"0","1");
        try {
            JSONArray resultArray = response.getJSONArray("result");
            project.fromJson(resultArray.getJSONObject(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  project;
    }

}
