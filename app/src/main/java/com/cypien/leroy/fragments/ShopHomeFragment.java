package com.cypien.leroy.fragments;/*
 * Created by Alex on 12.09.2016.
 */

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.activities.VoiceActivity;
import com.cypien.leroy.models.Store;

import java.util.ArrayList;
import java.util.List;

public class ShopHomeFragment extends Fragment {


    private View view;
    private RelativeLayout career, clientVoice, advices, calculator, nearestStore;
    private ImageView prices;
    private ArrayList<Store> stores;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.shop_home, container, false);

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Leroy Merlin");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);
        career = (RelativeLayout) view.findViewById(R.id.career);
        clientVoice = (RelativeLayout) view.findViewById(R.id.client_voice);
        advices = (RelativeLayout) view.findViewById(R.id.advices);
        calculator = (RelativeLayout) view.findViewById(R.id.calculator);
        nearestStore = (RelativeLayout) view.findViewById(R.id.nearest_store);
        prices = (ImageView) view.findViewById(R.id.prices);



        prices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPageFragment f = new ViewPageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", "Preț INEGALABIL");
                bundle.putString("url", "http://www.leroymerlin.ro/mobile/produse.html");
                f.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        });
        career.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ViewPageFragment f = new ViewPageFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("title", "Cariere");
                    bundle.putString("url", "http://job.leroymerlin.ro/");
                    f.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.content_frame, f).addToBackStack(null);
                    transaction.commit();
            }
        });
        clientVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VoiceActivity.class);
                startActivity(intent);
            }
        });
        advices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPageFragment f = new ViewPageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", "Sfaturi UTILE");
                bundle.putString("url", "http://www.leroymerlin.ro/sfaturi-utile");
                f.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        });
        calculator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        nearestStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Location location = getLastKnownLocation();
                Store store = getStore(location);
                Log.e("store", store.getName());
                StoreFragment f = new StoreFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("store", store);
                f.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }

    private Location getLastKnownLocation() {
        LocationManager mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return null;
            }
            Location l = mLocationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        Log.e("location", bestLocation.getLatitude()+" "+bestLocation.getLongitude());
        return bestLocation;
    }

    public Store getStore(Location location){
        getStores();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float[] distance = new float[1];
        Store store = stores.get(0);
        Location.distanceBetween(latitude, longitude, store.getLatitude(), store.getLongitude(),distance);
        float minDistance = distance[0];

        for(Store s:stores){
            Location.distanceBetween(latitude,longitude,s.getLatitude(),s.getLongitude(),distance);
            if(minDistance>distance[0]) {
                minDistance = distance[0];
                store = s;
            }
        }
        return store;
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


        store = new Store(45.703744, 21.183396);
        store.setName("LEROY MERLIN TIMISOARA");
        store.setAddress("Calea Sagului, DN 59, Timisoara");
        store.setOpen("Luni - Sambata: 7:00 - 21:00\n" + "Duminica: 9:00 - 19:00");
        store.setPhone("0374 411 860");
        store.setDirections("");
        store.setFacebookAddress("LeroyMerlinTimisoara");
        stores.add(store);
    }
}
