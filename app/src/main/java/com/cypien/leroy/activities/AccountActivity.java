package com.cypien.leroy.activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.MyAccountFragment;
import com.cypien.leroy.fragments.NewAccountFragment;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;


/**
 * Created by Alex on 8/12/2015.
 */
public class AccountActivity extends FragmentActivity {
    private LinearLayout back;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_activity);
        fragmentManager=getFragmentManager();

        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "AccountActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        findViewById(R.id.menu_button).setVisibility(View.GONE);
        findViewById(R.id.devider).setVisibility(View.GONE);
        back= (LinearLayout)findViewById(R.id.back_button);

        Intent intent=getIntent();
        if(intent.getStringExtra("source").equals("new")){
            NewAccountFragment f = new NewAccountFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, f,"account");
            transaction.commit();
        }else{
            MyAccountFragment f = new MyAccountFragment();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.add(R.id.fragment_container, f);
            transaction.commit();
        }

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fragmentManager.getBackStackEntryCount()==0)
                    finish();
                else
                    fragmentManager.popBackStack();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if(fragmentManager.getBackStackEntryCount()==0)
            finish();
        else
            fragmentManager.popBackStack();
    }
}
