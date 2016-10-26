package com.cypien.leroy.activities;/*
 * Created by Alex on 29.08.2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.StaticMethods;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ViewAccountActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private CircularImageView userImage;
    private TextView name;
    private ImageView back, edit;
    private TextView birthDate, signUpDate, email, phone, username, friends, views, projects, posts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_account_activity);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: View Account"));
        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: View Account");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        sp = getSharedPreferences("com.cypien.leroy_preferences", Context.MODE_PRIVATE);
        userImage = (CircularImageView) findViewById(R.id.user_image);
        name = (TextView) findViewById(R.id.name);
        birthDate = (TextView) findViewById(R.id.birth_date);
        signUpDate = (TextView) findViewById(R.id.signup_date);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        username = (TextView) findViewById(R.id.username);
        posts = (TextView) findViewById(R.id.posts_number);
        views = (TextView) findViewById(R.id.views_number);
        friends = (TextView) findViewById(R.id.friends_number);
        projects = (TextView) findViewById(R.id.projects_number);
        back = (ImageView) findViewById(R.id.back_arrow);
        edit = (ImageView) findViewById(R.id.edit_account);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewAccountActivity.this, EditAccountActivity.class);
                startActivity(intent);
            }
        });
        initData();
    }

    private void initData() {

        if (!sp.getString("avatar", "").equals(""))
            userImage.setImageBitmap(StaticMethods.decodeBase64(sp.getString("avatar", "")));
        username.setText(sp.getString("username", ""));
        posts.setText(sp.getString("posts", ""));
        views.setText(sp.getString("profilevisits", ""));
        projects.setText(sp.getString("blognum", ""));
        email.setText(sp.getString("email", ""));
        phone.setText(sp.getString("phone", ""));
        if(sp.getString("phone", "0700000000").equals("0700000000"))
            name.setText(sp.getString("firstname", ""));
        else
            name.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        signUpDate.setText(sp.getString("joindate2", ""));
        birthDate.setText(sp.getString("birthdate", ""));

        friends.setText(sp.getString("friendcount", ""));

    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }
}
