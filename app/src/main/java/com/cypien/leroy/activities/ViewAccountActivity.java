package com.cypien.leroy.activities;/*
 * Created by Alex on 29.08.2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.utils.StaticMethods;
import com.mikhaellopez.circularimageview.CircularImageView;

public class ViewAccountActivity extends AppCompatActivity {
    private SharedPreferences sp;
    private CircularImageView userImage;
    private TextView name;
    private TextView birthDate, signUpDate, email, phone, username, friends, views, projects, posts;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_account_activity);
        sp = getSharedPreferences("com.cypien.leroy_preferences", Context.MODE_PRIVATE);
        userImage = (CircularImageView) findViewById(R.id.user_image);
        name = (TextView) findViewById(R.id.name);
        birthDate = (TextView) findViewById(R.id.birth_date);
        signUpDate = (TextView) findViewById(R.id.signup_date);
        email = (TextView) findViewById(R.id.email);
        phone = (TextView) findViewById(R.id.phone);
        username = (TextView) findViewById(R.id.username);
        initData();
    }

    private void initData() {

        if(!sp.getString("avatar","").equals(""))
            userImage.setImageBitmap(StaticMethods.decodeBase64(sp.getString("avatar","")));
        username.setText(sp.getString("username",""));
        posts.setText(sp.getString("posts", ""));
        views.setText(sp.getString("profilevisits",""));
        projects.setText(sp.getString("blognum",""));
        email.setText(sp.getString("email",""));
       phone.setText(sp.getString("phone", ""));
//        String daily=sp.getString("dailyposts","");
//        if(daily.length()>5)
//            nrDaily.setText(daily.substring(0,5));
//        else
//            nrDaily.setText(daily);
       friends.setText(sp.getString("friendcount",""));
        signUpDate.setText(sp.getString("joindate",""));
//        nrProjects.setText(sp.getString("blognum",""));
    }
}
