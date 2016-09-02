package com.cypien.leroy.fragments;/*
 * Created by Alex on 02.09.2016.
 */

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

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.CatalogListAdapter;
import com.cypien.leroy.models.Catalog;
import com.cypien.leroy.utils.RecyclerItemClickListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.List;

public class CatalogListFragment extends Fragment {


    private RecyclerView recyclerView;

    List<Catalog> cataloage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.catalog_list_fragment, container, false);

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Cataloage");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.VISIBLE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });



        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "CatalogFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


        cataloage = (List<Catalog>) getArguments().getSerializable("cataloage");
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);


        CatalogListAdapter adapter = new CatalogListAdapter(cataloage, getContext());
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        changeFragment(position);
                    }
                })
        );
        return view;

    }
    private void changeFragment(int position){
        ViewCatalogFragment f = new ViewCatalogFragment();
        Bundle bundle=new Bundle();
        bundle.putString("title", cataloage.get(position).getTitle());
        bundle.putString("url", cataloage.get(position).getSlug());
        f.setArguments(bundle);
        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, f).addToBackStack(null);
        ft.commit();
    }

}
