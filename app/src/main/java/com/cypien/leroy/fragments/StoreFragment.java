package com.cypien.leroy.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Store;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Calendar;

/**
 * Created by Alex on 8/5/2015.
 */
public class StoreFragment extends Fragment {
    private static View view;
    private GoogleMap map;
    private TextView name, address, timetable_today, timetable_general, phone;
    private Store store;
    private LinearLayout seeStore, callPhone, seeFacebook, extindeProgramul;
    private MapView mapView;
    private TextView facebook;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.store_screen, container, false);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "StoreFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Bundle bundle = this.getArguments();
        store = (Store) bundle.getSerializable("store");

        if(store!=null)
            ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText(store.getName());
        else
            ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Magazin");

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        mapView = (MapView) view.findViewById(R.id.store_map);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        MapsInitializer.initialize(this.getActivity());
        if (map != null) {
            map.addMarker(new MarkerOptions().position(store.getPosition())
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(store.getPosition(), 10));
        }

        name = (TextView) view.findViewById(R.id.name);
        name.setText(store.getName());

        address = (TextView) view.findViewById(R.id.address);
        address.setText(store.getAddress());

        //deschide intent catre gMaps pentru navigatie catre locatie
        seeStore = (LinearLayout) view.findViewById(R.id.see_store);
        seeStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isGPSEnabled(getActivity())){
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + store.getPosition().latitude + "," + store.getPosition().longitude);
                    Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    mapIntent.setPackage("com.google.android.apps.maps");
                    startActivity(mapIntent);
                }else{
                    new NotificationDialog(getActivity(), "Vă rugăm să vă activați GPS-ul pentru a putea continua!").show();
                }
            }
        });

        phone = (TextView) view.findViewById(R.id.phone);
        phone.setText(store.getPhone());

        callPhone = (LinearLayout) view.findViewById(R.id.call_phone);
        callPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isTelephonyEnabled(getActivity()))
                    makePhoneCall(store.getPhone());
                else
                    new NotificationDialog(getActivity(),"Ne pare rau dar nu puteți efectua apeluri de pe acest dispozitiv!").show();
            }
        });

        facebook = (TextView) view.findViewById(R.id.facebook);
        facebook.setText("@" + store.getFacebookAddress());

        seeFacebook = (LinearLayout) view.findViewById(R.id.see_facebook);
        seeFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isNetworkConnected(getActivity())){
                    FacebookFragment f= new FacebookFragment("https://www.facebook.com/" + store.getFacebookAddress());
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.content_frame, f).addToBackStack(null);
                    fragmentTransaction.commit();
                }else{
                    new NotificationDialog(getActivity(),"Pentru a putea vizita pagina noastră de Facebook, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        timetable_general = (TextView) view.findViewById(R.id.timetable_general);
        timetable_today = (TextView) view.findViewById(R.id.timetable_today);
        Calendar today = Calendar.getInstance();
        int dayOfWeek = today.get(Calendar.DAY_OF_WEEK); //incepe de la 1; ex: Luni = 1
        String program = store.getOpen(), programParsat = "";
        if(dayOfWeek < 7)
            programParsat = program.substring(program.indexOf("Luni - Sambata: ") + 16, program.indexOf("\n"));
        else
            programParsat = program.substring(program.indexOf("Duminica: ") + 10);
        timetable_today.setText("Astazi " + programParsat);

        extindeProgramul = (LinearLayout) view.findViewById(R.id.extinde_programul);
        extindeProgramul.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timetable_general.setText(store.getOpen());
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();

    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    private void makePhoneCall(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
