package com.cypien.leroy.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.StoresAdapter;
import com.cypien.leroy.models.Store;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.DatabaseConnector;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
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
    TextView textNoInternet;
    RelativeLayout noInternet;
    FloatingActionButton fab;
    private com.rey.material.widget.LinearLayout retry;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.stores_screen, container, false);

        /*if(Connections.isNetworkConnected(getActivity()))
            try {
                uploadStores();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("eroare", e.toString());
            }
        else*/

        // getStores();
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Stores"));
        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Stores");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        listBtn = (TextView) view.findViewById(R.id.listBtn);
        mapBtn = (TextView) view.findViewById(R.id.mapBtn);
        recyclerView = (RecyclerView) view.findViewById(R.id.stores_list);
        mapView = (MapView) view.findViewById(R.id.map);
        fab = (FloatingActionButton) view.findViewById(R.id.fab);
        noInternet = (RelativeLayout) view.findViewById(R.id.no_internet);
        retry = (com.rey.material.widget.LinearLayout) view.findViewById(R.id.retry);
        textNoInternet = (TextView) view.findViewById(R.id.txtMesaj);
        textNoInternet.setText("Vă rugăm să activați internetul pentru a putea vedea magazinele Leroy Merlin!");
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                loadPage();
            }
        });
        loadPage();
      /*  StoresAdapter adapter = new StoresAdapter(stores);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);*/

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


        info = (LinearLayout) view.findViewById(R.id.info);
        name = (TextView) info.findViewById(R.id.name);
        address = (TextView) info.findViewById(R.id.address);

        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.store_btn_bg_pressed));
                mapBtn.setBackgroundColor(getResources().getColor(R.color.transparent));
                // mapBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.color.transparent));
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
                listBtn.setBackgroundColor(getResources().getColor(R.color.transparent));
                // listBtn.setBackgroundDrawable(ContextCompat.getDrawable(getActivity(), R.color.transparent));
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


        if (map != null) {
            for (Store s : stores) {
                Marker marker = map.addMarker(new MarkerOptions().position(s.getPosition())
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
                                    if (Connections.isGPSEnabled(getActivity())) {
                                        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + marker.getPosition().latitude + "," + marker.getPosition().longitude);
                                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                                        mapIntent.setPackage("com.google.android.apps.maps");
                                        startActivity(mapIntent);
                                    } else {
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
                if (map.getMapType() == GoogleMap.MAP_TYPE_HYBRID) {
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

    private void loadPage() {

        stores = DatabaseConnector.getHelper(getActivity()).loadStores();
        if (stores.size() == 0 && !Connections.isNetworkConnected(getActivity())) {
            noInternet.setVisibility(View.VISIBLE);
            return;
        } else if (stores.size() == 0 && Connections.isNetworkConnected(getActivity())) {
            new GetStore().execute(null, null, null);
            return;
        }

        StoresAdapter adapter = new StoresAdapter(stores);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
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

    public class GetStore extends AsyncTask<Void, Void, Void> {
        private ProgressDialog pg;

        private void uploadStores() throws JSONException {
            DatabaseConnector.getHelper(getActivity()).deleteAllStores();
            JSONObject jsonObject = makePublicRequest();
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            int size = jsonArray != null ? jsonArray.length() : 0;
            for (int i = 0; i < size; i++) {
                JSONObject jsn = jsonArray.getJSONObject(i);
                Store store = new Store();
                store.setId(jsn.getString("contact_id"));
                store.setAddress(jsn.getString("contact_adresa"));
                store.setFacebookAddress(jsn.getString("contact_facebook"));
                store.setLatitude(jsn.getDouble("contact_lat"));
                store.setLongitude(jsn.getDouble("contact_lng"));
                store.setPhone(jsn.getString("contact_tel"));
                store.setOpen("Luni - Sambata: " + jsn.getString("contact_orar_rest_deschidere") + ":00 - " +
                        jsn.getString("contact_orar_rest_inchidere") + ":00\nDuminica: " + jsn.getString("contact_orar_duminica_deschidere") + ":00 - " +
                        jsn.getString("contact_orar_duminica_inchidere") + ":00");
                store.setName(jsn.getString("contact_magazin_title"));

                DatabaseConnector.getHelper(getActivity()).insertStore(store);
                stores.add(store);
            }
        }

        public JSONObject makePublicRequest() {
            ArrayList<Integer> parameters = new ArrayList<>();
            parameters.add(0);
            parameters.add(100);
            JSONObject request = new JSONObject();
            try {
                request.put("method", "contact_get_all");
                request.put("params", new JSONArray(parameters));

                return new JSONObject(getRequest("http://www.leroymerlin.ro/api/publicEndpoint", "q=" + request.toString()));
            } catch (JSONException e) {
                Log.e("eroare", e.toString());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                uploadStores();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected String getRequest(String... info) {
            HttpURLConnection connection;
            OutputStreamWriter request = null;
            URL url = null;
            StringBuilder sb = new StringBuilder();
            InputStreamReader isr = null;
            BufferedReader reader = null;

            try {
                url = new URL(info[0]);
                connection = (HttpURLConnection) url.openConnection();

                connection.setInstanceFollowRedirects(false);
                connection.setDoOutput(true);
                connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
                connection.setRequestMethod("POST");
                request = new OutputStreamWriter(connection.getOutputStream());

                request.write(info[1]);
                request.flush();
                request.close();

                String line = "";
                isr = new InputStreamReader(connection.getInputStream());
                reader = new BufferedReader(isr);
                line = reader.readLine();
                while (line != null) {
                    sb.append(line);
                    try {
                        line = reader.readLine();
                    } catch (Exception e) {
                        line = null;
                    }
                }
            } catch (Exception e) {
                Log.e("eroare", e.toString());
                e.printStackTrace();
            }
            if (sb != null && !sb.equals(""))
                return sb.toString();
            else return "";
        }

        @Override
        protected void onPreExecute() {
            pg = new ProgressDialog(getActivity());
            pg.setCancelable(false);
            pg.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            noInternet.setVisibility(View.GONE);
            StoresAdapter adapter = new StoresAdapter(stores);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            recyclerView.setLayoutManager(mLayoutManager);
            recyclerView.setHasFixedSize(true);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
            pg.dismiss();
        }
    }
   /* private void uploadStores() throws JSONException {

        JSONObject jsonObject = makePublicRequest();
        JSONArray jsonArray = jsonObject.getJSONArray("result");
        int size = jsonArray != null ? jsonArray.length() : 0;
        DatabaseConnector.getHelper(getActivity()).deleteAllStores();
        for (int i = 0; i < size; i++) {
            JSONObject jsn = jsonArray.getJSONObject(i);
            Store store = new Store();
            store.setId(jsn.getString("contact_id"));
            store.setAddress(jsn.getString("contact_adresa"));
            store.setFacebookAddress(jsn.getString("contact_facebook"));
            store.setLatitude(jsn.getDouble("contact_lat"));
            store.setLongitude(jsn.getDouble("contact_lng"));
            store.setPhone(jsn.getString("contact_tel"));
            store.setOpen("Luni - Sambata: "+jsn.getString("contact_orar_rest_deschidere")+":00 - "+
                    jsn.getString("contact_orar_rest_inchidere")+"\nDuminica: "+jsn.getString("contact_orar_duminica_deschidere"+":00 - "+
            jsn.getString("contact_orar_duminica_inchidere")+":00"));
            store.setName(jsn.getString("contact_magazin_title"));
            stores.add(store);
            DatabaseConnector.getHelper(getActivity()).insertStore(store);
        }
    }*/

    /*public JSONObject makePublicRequest(){
        ArrayList<Integer> parameters = new ArrayList<>();
        parameters.add(0);
        parameters.add(100);
        JSONObject request = new JSONObject();
        try {
            request.put("method", "contact_get_all");
            request.put("params",new JSONArray(parameters));
            Log.e("request", request.toString());
            return new JSONObject(new WebServiceConnector().execute("http://www.leroymerlin.ro/api/publicEndpoint", "q=" + request.toString()).get());
        } catch (JSONException | InterruptedException | ExecutionException e) {
            Log.e("eroare", e.toString());
            e.printStackTrace();
        }
        return null;
    }*/

  /*  void getStores(){
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
    }*/
}
