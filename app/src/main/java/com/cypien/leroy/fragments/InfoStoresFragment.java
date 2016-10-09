package com.cypien.leroy.fragments;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by alexa on 10/12/2015.
 */
public class InfoStoresFragment extends Fragment{

    private View view;
    private ImageView seeCatalog,seeStores,servicesButton,voiceButton;
    private SharedPreferences sp;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* view = getActivity().getLayoutInflater().inflate(R.layout.info_stores_dashboard_screen, container, false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Informații Magazin");
        ((ImageView) actionBarView.findViewById(R.id.logo)).setImageResource(R.drawable.logo);
        actionBarView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);


        seeCatalog=(ImageView)view.findViewById(R.id.see_catalog);
        seeCatalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(getActivity())) {
                    CatalogViewFragment f = new CatalogViewFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
                } else {
                    new NotificationDialog(getActivity(), "Pentru a vedea catalogul, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        seeStores=(ImageView)view.findViewById(R.id.see_stores);
        seeStores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StoresFragment f = new StoresFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();

            }
        });

        servicesButton=(ImageView)view.findViewById(R.id.see_services);
        servicesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicesFragment f = new ServicesFragment();
                getFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();

            }
        });

        voiceButton=(ImageView)view.findViewById(R.id.contact);
        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (sp.getBoolean("isConnected",false)) {
                    VoiceFragment f =new VoiceFragment();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();

                }else
                    new NotificationDialog(getActivity(),"Pentru a putea trimite mesaje prin vocea clientului, vă rugăm să vă autentificați!").show();
            }
        });
*/
        return view;
    }
}
