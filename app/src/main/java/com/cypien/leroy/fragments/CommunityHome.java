package com.cypien.leroy.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.AddContestProjectActivity;
import com.cypien.leroy.activities.AddDiscussionActivity;
import com.cypien.leroy.activities.AddProjectActivity;
import com.cypien.leroy.activities.ViewAccountActivity;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularimageview.CircularImageView;

/**
 * Created by GabiRotaru on 27/08/16.
 */
public class CommunityHome extends Fragment {
    private View view;
    private SharedPreferences sp;
    private CircularImageView user_image;
    private TextView user_name, posts, views, projects;

    private RelativeLayout topul_meseriasilor, sfatul_meseriasului, evenimente;
    private RelativeLayout btnAddProject, btnAddDiscussion, btnAddContest;
    private android.widget.Button editAccount;

    // transforma un string base64 in imagine
    public static Bitmap decodeBase64(String input) {
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.community_home, container, false);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Facem-Facem"));
        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Facem-Facem");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Facem-Facem");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);

        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        user_image = (CircularImageView) view.findViewById(R.id.user_image);
        user_name = (TextView) view.findViewById(R.id.user_name);
        posts = (TextView) view.findViewById(R.id.posts);
        views = (TextView) view.findViewById(R.id.views);
        projects = (TextView) view.findViewById(R.id.projects);

        initProfileInformation();


        topul_meseriasilor = (RelativeLayout) view.findViewById(R.id.topul_meseriasilor);
        sfatul_meseriasului = (RelativeLayout) view.findViewById(R.id.sfatul_meseriasului);
        evenimente = (RelativeLayout) view.findViewById(R.id.evenimente);
        editAccount = (android.widget.Button) view.findViewById(R.id.btnEditAccount);
        editAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ViewAccountActivity.class);
                startActivity(intent);
            }
        });

        topul_meseriasilor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFragment(new TopUsersFragment());
            }
        });
        sfatul_meseriasului.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFragment(new AdvicesFragment());
            }
        });
        evenimente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToFragment(new EventsFragment());
            }
        });


        btnAddProject = (RelativeLayout) view.findViewById(R.id.btnAddProject);
        btnAddDiscussion = (RelativeLayout) view.findViewById(R.id.btnAddDiscussion);
        btnAddContest = (RelativeLayout) view.findViewById(R.id.btnAddContest);

        btnAddProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddProjectActivity.class);
                startActivity(intent);
                //   goToFragment(new AddProjectActivity());
            }
        });

        btnAddDiscussion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddDiscussionActivity.class);
                startActivity(intent);
            }
        });

        btnAddContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddContestProjectActivity.class);
                startActivity(intent);
            }
        });
        return view;
    }

    public void goToFragment(Fragment fragment) {
        if (fragment != null) {
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame_community, fragment).addToBackStack(null);
            ft.commit();
        }
    }

    // descarca informatiile despre utilizator, le adauga in shared preferences si le afiseaza
    private void initProfileInformation() {

        if (!sp.getString("avatar", "").equals(""))
            user_image.setImageBitmap(decodeBase64(sp.getString("avatar", "")));
        user_name.setText(sp.getString("username", ""));
        posts.setText(sp.getString("posts", ""));
        views.setText(sp.getString("profilevisits", ""));
        projects.setText(sp.getString("blognum", ""));

//        email.setText(sp.getString("email",""));
//        phone.setText(sp.getString("phone", ""));
//        String daily=sp.getString("dailyposts","");
//        if(daily.length()>5)
//            nrDaily.setText(daily.substring(0,5));
//        else
//            nrDaily.setText(daily);
//        nrFriends.setText(sp.getString("friendcount",""));
//        signUpDate.setText(sp.getString("joindate",""));
//        nrProjects.setText(sp.getString("blognum",""));
    }

    @Override
    public void onResume() {
        super.onResume();
        initProfileInformation();
    }
}
