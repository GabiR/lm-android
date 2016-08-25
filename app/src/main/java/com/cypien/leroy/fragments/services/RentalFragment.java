package com.cypien.leroy.fragments.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Service;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by alexa on 10/1/2015.
 */
public class RentalFragment extends Fragment{
    private View view;
    private int phoneNumbers[]={R.id.phone1,R.id.phone2,R.id.phone3,R.id.phone4,R.id.phone5,R.id.phone6,
                                R.id.phone7,R.id.phone8,R.id.phone9,R.id.phone10,R.id.phone11, R.id.phone12,
                                R.id.phone13, R.id.phone14};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.rental_service_screen,container,false);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "ToolsRentalFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Bundle bundle = getArguments();
        Service service = (Service)bundle.getSerializable("service");

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText(service.getName());

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        for (int i=0; i<phoneNumbers.length;i++){
            view.findViewById(phoneNumbers[i]).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Connections.isTelephonyEnabled(getActivity()))
                        makePhoneCall(((TextView)v).getText().toString());
                    else
                        new NotificationDialog(getActivity(),"Ne pare rau dar nu puteți efectua apeluri de pe acest dispozitiv!").show();
                }
            });
        }

        return view;
    }

    private void makePhoneCall(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
