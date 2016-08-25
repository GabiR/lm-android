package com.cypien.leroy.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.StoresAdapter;
import com.cypien.leroy.models.Store;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.RecyclerItemClickListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class StoresFragment extends Fragment {

    List<Store> stores;
    View view;
    TextView listBtn, mapBtn;
    RecyclerView recyclerView;
    MapView mapView;
    GoogleMap map;
    LinearLayout info;
    TextView name;
    TextView address;
    FloatingActionButton fab;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.stores_screen, container, false);
        getStores();

        listBtn = (TextView) view.findViewById(R.id.listBtn);
        mapBtn = (TextView) view.findViewById(R.id.mapBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.stores_list);
        mapView = (MapView) view.findViewById(R.id.map);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);

        StoresAdapter adapter = new StoresAdapter(stores);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                StoreFragment f = new StoreFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("store", stores.get(position));
                f.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        }));

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Listă magazine");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "StoresFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        info = (LinearLayout)view.findViewById(R.id.info);
        name=(TextView)info.findViewById(R.id.name);
        address=(TextView)info.findViewById(R.id.address);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.store_btn_bg_pressed));
                mapBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.store_btn_bg));
                ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Listă magazine");
                recyclerView.setVisibility(View.VISIBLE);
                mapView.setVisibility(View.GONE);
                info.setVisibility(View.GONE);
                fab.setVisibility(View.GONE);
            }
        });

        listBtn.performClick();


        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.store_btn_bg));
                mapBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.store_btn_bg_pressed));
                ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Hartă magazine");
                recyclerView.setVisibility(View.GONE);
                mapView.setVisibility(View.VISIBLE);
                fab.setVisibility(View.VISIBLE);
            }
        });



        //map handling
        mapView = (MapView) view.findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        map = mapView.getMap();
        MapsInitializer.initialize(this.getActivity());


        if (map!=null){
            for (Store s:stores){
                Marker marker= map.addMarker(new MarkerOptions().position(s.getPosition())
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.marker)));
            }
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    info.setVisibility(View.GONE);
                }
            });

            map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(final Marker marker) {
                    for (Store s : stores) {
                        if (s.getPosition().equals(marker.getPosition())) {
                            name.setText(s.getName());
                            address.setText(s.getAddress());
                            info.setVisibility(View.VISIBLE);
                            info.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if(Connections.isGPSEnabled(getActivity())){
                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        startActivity(mapIntent);
                                    }else{
                                        new NotificationDialog(getActivity(), "Vă rugăm să vă activați GPS-ul pentru a putea continua!").show();
                                    }

                                }
                            });
                        }
                    }
                    return false;
                }
            });
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(45.673401, 25.590132), 6));
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(map.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.satellite_icon));
                } else {
                    map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                    fab.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.road_icon));
                }
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

    void getStores(){
        stores = new ArrayList<>();
        Store store;

        store = new Store(44.311482, 23.831041);
        store.setName("LEROY MERLIN CRAIOVA");
        store.setAddress("Calea București Nr. 80, Craiova, Electroputere Mall");
        store.setOpen("Luni - Sambata: 07:00 - 22:00 \n" + "Duminica: 09:00 - 22:00");
        store.setPhone("0374 409 900");
        store.setDirections("LEROY MERLIN Electroputere Mall te asteapta cu un Drive-In pentru materiale de constructii. Intri cu masina in zona special semnalizata, incarci marfa de care ai nevoie direct in masina ta, platesti la casa speciala si gata, ai rezolvat cumparaturile. Totul e mult mai simplu si mai usor cu Drive-In-ul LEROY MERLIN!");
        store.setFacebookAddress("LeroyMerlinCraiova");
        stores.add(store);

        store = new Store(45.673401, 25.590132);
        store.setName("LEROY MERLIN BRAȘOV");
        store.setAddress("Bulevardul Grivitei Nr. 1L, Brasov");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 19:00");
        store.setPhone("0374 409 970");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinBrasov");
        stores.add(store);

        store = new Store(44.970337, 25.961332);
        store.setName("LEROY MERLIN PLOIEȘTI");
        store.setAddress("DN1, km.6, Comuna Blejoi, Ploiesti");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 427 500");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinPloiesti");
        stores.add(store);

        store = new Store(44.384834, 25.999968);
        store.setName("LEROY MERLIN ALEXANDRIEI");
        store.setAddress("Str. Sperantei Nr. 94-96 Bragadiru, Ilfov");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 427 580");
        store.setDirections("Daca vii cu autoturismul accesul in magazinul LEROY MERLIN se face din Sos.Alexandria, aproape de podul spre Bragadiru, iar parcarea noastra dispune de cateva sute de locuri de parcare.");
        store.setFacebookAddress("LeroyMerlinAlexandriei");
        stores.add(store);

        store = new Store(44.395394, 26.123261);
        store.setName("LEROY MERLIN SUNPLAZA");
        store.setAddress("Calea Vacaresti Nr. 391, Bucuresti, Centrul Comercial Sun Plaza");
        store.setOpen("Luni - Sambata: 7:00 - 22:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 427 550");
        store.setDirections("Daca vii cu autoturismul accesul in magazinul LEROY MERLIN se face din Sos. Oltenitei sau din Calea Vacaresti, iar parcarea noastra dispune de cateva sute de locuri de parcare.\n" +
                "Poti avea acces direct la magazin si din statia de metrou Piata Sudului prin pasajul catre centrul comercial.");
        store.setFacebookAddress("LeroyMerlinSunPlaza");
        stores.add(store);

        store = new Store(44.489522, 26.015611);
        store.setName("LEROY MERLIN COLOSSEUM");
        store.setAddress("Sos. Chitilei Nr. 284 , Bucuresti, Parcul Comercial Colosseum");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 133 096");
        store.setDirections("Daca vii cu autoturismul accesul in magazinul LEROY MERLIN se face din Sos. Chitilei sau din Str. C-tin Godeanu, iar parcarea noastra dispune de cateva sute de locuri de parcare.\n" +
                "\n" +
                "Daca vii la noi folosind mijloacele de transport in comun, in zona magazinului nostru se afla mai multe linii de autobuz (422, 112, 304), tramvai (45) si o statie de metrou (Parc Bazilescu).");
        store.setFacebookAddress("LeroyMerlinBucurestiColosseumParc");
        stores.add(store);

        store = new Store(46.746276, 23.590713);
        store.setName("LEROY MERLIN CLUJ TURZII");
        store.setAddress("Calea Turzii nr.186, Cluj-Napoca");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 642 233");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinClujTurzii");
        stores.add(store);

        store = new Store(45.779945, 24.168741);
        store.setName("LEROY MERLIN SIBIU");
        store.setAddress("Str. Sibiului Nr.5, Selimbar, Jud. Sibiu, Shopping City Sibiu");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 642 255");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinSibiu");
        stores.add(store);

        store = new Store(44.192367, 28.600912);
        store.setName("LEROY MERLIN CONSTANTA");
        store.setAddress("Bd.Aurel Vlaicu nr. 207, Constanta");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 642 300");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinConstanta");
        stores.add(store);

        store = new Store(46.7816549, 23.6335967);
        store.setName("LEROY MERLIN CLUJ VLAICU");
        store.setAddress("Bd.Aurel Vlaicu Nr.182");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 642 330");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinClujVlaicu");
        stores.add(store);

        store = new Store(46.508953, 24.5086895);
        store.setName("LEROY MERLIN TARGU MURES");
        store.setAddress("Promenada Mall, Str. Gheorghe Doja nr.243");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 411 800​");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinTarguMures");
        stores.add(store);

        store = new Store(47.665358, 26.26668);
        store.setName("LEROY MERLIN SUCEAVA");
        store.setAddress("Str. Unirii nr 27B, Shopping City Suceava ");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 411 899");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinSuceava");
        stores.add(store);

        store = new Store(46.510856, 26.928101);
        store.setName("LEROY MERLIN BACAU");
        store.setAddress("Calea Republicii nr.181, Hello Shopping Park, Bacau");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 411 840");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinBacau");
        stores.add(store);

        store = new Store(47.148633, 27.599655);
        store.setName("LEROY MERLIN IASI");
        store.setAddress("Calea Chisinaului Nr.23, Iasi");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 20:00");
        store.setPhone("0374 411 820");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinIasi");
        stores.add(store);
    }
}
