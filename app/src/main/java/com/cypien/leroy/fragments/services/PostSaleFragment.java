package com.cypien.leroy.fragments.services;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.PdfViewerFragment;
import com.cypien.leroy.models.Service;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by alexa on 10/1/2015.
 */
public class PostSaleFragment extends Fragment{
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.post_sale_service_screen,container,false);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "PostSaleFragment");
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

        view.findViewById(R.id.view_services).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PdfViewerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("filename","post.pdf");
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
