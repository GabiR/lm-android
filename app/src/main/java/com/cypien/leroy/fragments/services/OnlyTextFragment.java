package com.cypien.leroy.fragments.services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Service;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by alexa on 9/30/2015.
 */
public class OnlyTextFragment extends Fragment{

    private TextView details;
    private ImageView image;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.only_text_service, container, false);

        details = (TextView) view.findViewById(R.id.service_content);
        image = (ImageView) view.findViewById(R.id.service_image);
        Bundle bundle = getArguments();
        Service service = (Service)bundle.getSerializable("service");

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "OnlyTextServiceFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

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

        details.setText(Html.fromHtml(service.getDetails()));
        image.setImageDrawable(ContextCompat.getDrawable(getActivity(), service.getImage()));

        return view;
    }
}

