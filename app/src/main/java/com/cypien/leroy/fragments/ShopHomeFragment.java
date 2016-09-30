package com.cypien.leroy.fragments;/*
 * Created by Alex on 12.09.2016.
 */

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.VoiceActivity;
import com.cypien.leroy.models.Message;
import com.cypien.leroy.models.Store;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.DatabaseConnector;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShopHomeFragment extends Fragment {


    private View view;
    private RelativeLayout career, clientVoice, advices, calculator, nearestStore;
    private RelativeLayout prices;
    private ArrayList<Store> stores;
    private RelativeLayout message;
    private TextView noMessages;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.shop_home, container, false);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Shop Home"));
        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Leroy Merlin");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);
        Bundle bundle = this.getArguments();
        if(bundle!=null) {
            Message mess = (Message) bundle.getSerializable("message");
            if (mess != null && !mess.isRead()) {

                MessagesFragment f = new MessagesFragment();
                f.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        }
        career = (RelativeLayout) view.findViewById(R.id.career);
        clientVoice = (RelativeLayout) view.findViewById(R.id.client_voice);
        advices = (RelativeLayout) view.findViewById(R.id.advices);
        calculator = (RelativeLayout) view.findViewById(R.id.calculator);
        nearestStore = (RelativeLayout) view.findViewById(R.id.nearest_store);
        prices = (RelativeLayout) view.findViewById(R.id.prices);
        message = (RelativeLayout) view.findViewById(R.id.message);
        noMessages = (TextView) view.findViewById(R.id.no_messages);

        SimpleDateFormat sdfDate = new SimpleDateFormat("dd.MM.yyyy HH:mm");//dd/MM/yyyy
        Date now = new Date();
        String strDate = sdfDate.format(now);
        int n = DatabaseConnector.getHelper(getActivity()).getUnreadMessagesNumber(strDate);
        if(n!=0) {
            noMessages.setText(String.valueOf(n));
            noMessages.setVisibility(View.VISIBLE);
        }
        message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MessagesFragment f = new MessagesFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        });
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
                CalculatorDashboardFragment f = new CalculatorDashboardFragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        });
        nearestStore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (!checkPermission())
                        return;
                }
                stores = DatabaseConnector.getHelper(getActivity()).loadStores();
                // }
                // getStores();
                if (stores.size() == 0 && !Connections.isNetworkConnected(getActivity())) {
                    new NotificationDialog(getActivity(), "Vă rugăm să vă conectați la Internet pentru a afla cel mai apropiat magazin!").show();
                    return;
                }
                if (stores.size() == 0 && Connections.isNetworkConnected(getActivity())) {
                    new GetStore().execute(null, null, null);
                    return;
                }

                Location location = getLastKnownLocation();

                if (location != null) {
                    Store store = getStore(location);
                    StoreFragment f = new StoreFragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("store", store);
                    f.setArguments(bundle);
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.content_frame, f).addToBackStack(null);
                    transaction.commit();
                }

            }
        });

        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (PermissionUtils.isPermissionGranted(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                || PermissionUtils.isPermissionGranted(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            nearestStore.callOnClick();
        }
    }

    private boolean checkPermission() {
        String[] permissions = new String[2];
        int i = 0;
        if (PermissionUtils.shouldRequestPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION))
            permissions[i++] = Manifest.permission.ACCESS_COARSE_LOCATION;
        if (PermissionUtils.shouldRequestPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
            permissions[i++] = Manifest.permission.ACCESS_FINE_LOCATION;

        if (i > 0) {
            PermissionUtils.requestPermission(getActivity(), permissions, 0);
            return false;
        }
        return true;
    }

    private boolean isLocationEnabled(LocationManager locationManager) {
        return
                locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                        locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    private Location getLastKnownLocation() {


        LocationManager mLocationManager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        if (!isLocationEnabled(mLocationManager)) {
            getActivity().startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            return null;
        }
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
        //      Log.e("location", bestLocation.getLatitude()+" "+bestLocation.getLongitude());
        return bestLocation;
    }

    public Store getStore(Location location) {

        /*if(Connections.isNetworkConnected(getActivity())){
            uploadStores();
        }
        else{*/


        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        float[] distance = new float[1];
        Store store = stores.get(0);
        Location.distanceBetween(latitude, longitude, store.getLatitude(), store.getLongitude(), distance);
        float minDistance = distance[0];

        for (Store s : stores) {
            Location.distanceBetween(latitude, longitude, s.getLatitude(), s.getLongitude(), distance);
            if (minDistance > distance[0]) {
                minDistance = distance[0];
                store = s;
            }
        }
        return store;
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
            Location location = getLastKnownLocation();
            pg.dismiss();
            if (location != null) {
                Store store = getStore(location);

                StoreFragment f = new StoreFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("store", store);
                f.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        }
    }


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
