package com.cypien.leroy.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.CatalogAdapter;
import com.cypien.leroy.models.Catalog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class CatalogFragment extends Fragment {

    private View view;
    private TextView txtCatalogActual, seeAllOffers;
    private ImageView coperta_catalog;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;

    String sb = "";
    List<Catalog> cataloage;

    AtomicInteger pendingRequests;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.catalog_fragment, container, false);

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Cataloage");
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "CatalogFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        txtCatalogActual = (TextView) view.findViewById(R.id.txtCatalogActual);
        coperta_catalog = (ImageView) view.findViewById(R.id.coperta_catalog);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        seeAllOffers = (TextView) view.findViewById(R.id.seeAllOffers);

        requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.publitas.com/v1/groups/leroymerlin/publications.json";


        cataloage = new ArrayList<>();

        JsonArrayRequest obreq = new JsonArrayRequest(Request.Method.GET, url,
                new Response.Listener<JSONArray>(){
                    @Override
                    public void onResponse(JSONArray response) {
                        pendingRequests = new AtomicInteger();

                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonobject = null;
                            try {
                                Catalog catalog = new Catalog();

                                jsonobject = response.getJSONObject(i);
                                String title = jsonobject.getString("title");
                                String slug = jsonobject.getString("slug");
                                String url = jsonobject.getString("url");
                                catalog.setTitle(title);
                                catalog.setSlug(slug);
                                catalog.setContextualURL(url);
                                cataloage.add(catalog);

                                pendingRequests.incrementAndGet();


                                //websiteUrl
                                //downloadPdfUrl

                                //"spreads":
                                //   [{"pages": ["/1613/130970/pages/f44eab2912eb90a5e127683240a2b6c8e9f6213b"],
                                //    "hotspots": []}, ...]
                                //https://view.publitas.com +  + -at600.jpg


                                //1) avem cataloage si are net = > request si inlocuim toate cataloagele (POATE poze incarcare); le salvam local
                                //2) nu avem cataloage si are net => la fel
                                //3) avem cataloage si nu are net => le afisam pe alea si POATE bara sus cu activati internetul pt a cauta bla
                                //4) nu avem cataloage si nu are net => ?POATE imagine all screen cu activeaza netul

                                makeCatalogDetailRequest(catalog);

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }

                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                    }
                }
        );
        requestQueue.add(obreq);


        return view;

    }

    boolean makeCatalogDetailRequest(final Catalog catalog) {
        requestQueue = Volley.newRequestQueue(getActivity());
        String url = "https://api.publitas.com/v1/groups/leroymerlin/publications/" + catalog.getSlug() + ".json";

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject config = response.getJSONObject("config");
                            String websiteURL = config.getString("websiteUrl");
                            String downloadPdfUrl = "https://view.publitas.com/" + config.getString("downloadPdfUrl");
                            String coverImageSlug = response.getJSONArray("spreads").getJSONObject(0).getJSONArray("pages").get(0).toString();
                            String coverImageURL = "https://view.publitas.com" + coverImageSlug + "-at600.jpg";

                            catalog.setWebsiteURL(websiteURL);
                            catalog.setPdfURL(downloadPdfUrl);
                            catalog.setCoverImageURL(coverImageURL);

                            pendingRequests.decrementAndGet();

                            if(pendingRequests.get() <= 0) {
                                setUIelements();
                            }

                            //websiteUrl
                            //downloadPdfUrl

                            //"spreads":
                            //   [{"pages": ["/1613/130970/pages/f44eab2912eb90a5e127683240a2b6c8e9f6213b"],
                            //    "hotspots": []}, ...]
                            //https://view.publitas.com +  + -at600.jpg


                            //1) avem cataloage si are net = > request si inlocuim toate cataloagele (POATE poze incarcare); le salvam local
                            //2) nu avem cataloage si are net => la fel
                            //3) avem cataloage si nu are net => le afisam pe alea si POATE bara sus cu activati internetul pt a cauta bla
                            //4) nu avem cataloage si nu are net => ?POATE imagine all screen cu activeaza netul

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }



                    }

                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("Volley", "Error");
                        pendingRequests.decrementAndGet();

                    }
                }
        );
        requestQueue.add(obreq);

        return false;
    }

    void setUIelements() {
        //Picasso.with(getActivity()).load(cataloage.get(0).getCoverImageURL()).fit().into(coperta_catalog);
        new DownloadImageTask(coperta_catalog)
                .execute(cataloage.get(0).getCoverImageURL());

        CatalogAdapter adapter = new CatalogAdapter(cataloage, getActivity());
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }



}
