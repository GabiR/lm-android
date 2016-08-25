package com.cypien.leroy.fragments.services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.ShopMainActivity;
import com.cypien.leroy.models.Service;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by alexa on 9/30/2015.
 */
public class PaymentFragment extends Fragment{
    private View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.payment_service_screen,container,false);

        Bundle bundle = getArguments();
        Service service = (Service)bundle.getSerializable("service");

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "PaymentFragment");
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

        view.findViewById(R.id.go_to_finance).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new FinancingFragment();
                Bundle bundle=new Bundle();
                bundle.putSerializable("service", ((ShopMainActivity) getActivity()).getServices().get(5));
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

}
