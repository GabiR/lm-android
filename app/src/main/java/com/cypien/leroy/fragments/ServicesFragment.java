package com.cypien.leroy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.ShopDashboard;
import com.cypien.leroy.adapters.ServiceListAdapter;
import com.cypien.leroy.fragments.services.CardFragment;
import com.cypien.leroy.fragments.services.DeliveryFragment;
import com.cypien.leroy.fragments.services.FinancingFragment;
import com.cypien.leroy.fragments.services.KidsFragment;
import com.cypien.leroy.fragments.services.OnlyTextFragment;
import com.cypien.leroy.fragments.services.PaymentFragment;
import com.cypien.leroy.fragments.services.PostSaleFragment;
import com.cypien.leroy.fragments.services.RambursFragment;
import com.cypien.leroy.fragments.services.RentalFragment;
import com.cypien.leroy.fragments.services.ReturnFragment;
import com.cypien.leroy.models.Service;
import com.cypien.leroy.utils.RecyclerItemClickListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by GabiRotaru on 03/08/16.
 */
public class ServicesFragment extends Fragment {

    RecyclerView recyclerView; //services list
    List<Service> services;
    Fragment fragment;
    FragmentTransaction fragmentTransaction;
    Bundle bundle;
    //LinearLayout pdfView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = getActivity().getLayoutInflater().inflate(R.layout.services_fragment, container, false);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Services"));
        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Services");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Servicii");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);

        recyclerView = (RecyclerView) view.findViewById(R.id.services_list);
//        pdfView = (LinearLayout) view.findViewById(R.id.pdf);
//        pdfView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                openPDF();
//            }
//        });

        services = ShopDashboard.getServices();
        if (services == null)
            services = new ArrayList<>();

        ServiceListAdapter adapter = new ServiceListAdapter(services, getActivity());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        switch (position) {
                            case 0:
                                fragment = new PaymentFragment();
                                break;
                            case 1:
                                fragment = new RambursFragment();
                                break;
                            case 2:
                                fragment = new OnlyTextFragment();
                                break;
                            case 3:
                                fragment = new DeliveryFragment();
                                break;
                            case 4:
                                fragment = new CardFragment();
                                break;
                            case 5:
                                fragment = new FinancingFragment();
                                break;
                            case 6:
                                fragment = new KidsFragment();
                                break;
                            case 7:
                                fragment = new RentalFragment();
                                break;
                            case 8:
                                fragment = new OnlyTextFragment();
                                break;
                            case 9:
                                fragment = new OnlyTextFragment();
                                break;
                            case 10:
                                fragment = new OnlyTextFragment();
                                break;
                            case 11:
                                fragment = new ReturnFragment();
                                break;
                            case 12:
                                fragment = new PostSaleFragment();
                                break;
                        }
                        changeFragment(fragment, position);
                    }
                })
        );

        return view;
    }


    private void changeFragment(Fragment fragment, int position) {
        bundle = new Bundle();
        bundle.putSerializable("service", services.get(position));
        fragment.setArguments(bundle);
        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        fragmentTransaction.commit();
    }

//    private void openPDF() {
//        Fragment fragment = new PdfViewerFragment();
//        bundle = new Bundle();
//        bundle.putString("filename", "adrese_service.pdf");
//        fragment.setArguments(bundle);
//        fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null);
//        fragmentTransaction.commit();
//    }


}
