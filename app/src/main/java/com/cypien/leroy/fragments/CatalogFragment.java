package com.cypien.leroy.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.adapters.CatalogAdapter;
import com.cypien.leroy.models.Catalog;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.RecyclerItemClickListener;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.gson.reflect.TypeToken;
import com.rey.material.widget.ProgressView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class CatalogFragment extends Fragment {

    String sb = "";
    List<Catalog> cataloage;
    AtomicInteger pendingRequests;
    View noInternet;
    ProgressView progressView;
    private View view;
    private TextView txtCatalogActual, seeAllOffers;
    private ImageView coperta_catalog;
    private RecyclerView recyclerView;
    private RequestQueue requestQueue;
    private com.rey.material.widget.LinearLayout retry;
    private FragmentActivity mActivity;
    private ProgressView progressLayout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.catalog_fragment, container, false);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Catalog"));
        ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Cataloage");
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);


        LeroyApplication application = (LeroyApplication) mActivity.getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Catalog");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        txtCatalogActual = (TextView) view.findViewById(R.id.txtCatalogActual);
        coperta_catalog = (ImageView) view.findViewById(R.id.coperta_catalog);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        seeAllOffers = (TextView) view.findViewById(R.id.seeAllOffers);
        noInternet = view.findViewById(R.id.no_internet);
        requestQueue = Volley.newRequestQueue(mActivity);
     /*   String url = "https://api.publitas.com/v1/groups/leroymerlin/publications.json";*/

        progressView = (ProgressView) view.findViewById(R.id.progress);
        progressLayout = (ProgressView) view.findViewById(R.id.progress_layout);
        cataloage = new ArrayList<>();


        retry = (com.rey.material.widget.LinearLayout) view.findViewById(R.id.retry);

        loadPage();
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                loadPage();
            }
        });

        coperta_catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeFragment(0);
            }
        });

        seeAllOffers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CatalogListFragment f = new CatalogListFragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("cataloage", (Serializable) cataloage);

                f.setArguments(bundle);
                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.content_frame, f).addToBackStack(null);
                ft.commit();
            }
        });
        return view;

    }

    private void loadPage() {


        seeAllOffers.setEnabled(false);
        if (Connections.isNetworkConnected(mActivity)) {
            noInternet.setVisibility(View.GONE);
            String url = "https://api.publitas.com/v1/groups/leroymerlin/publications.json";
            JsonArrayRequest obreq = new JsonArrayRequest(Request.Method.GET, url,
                    new Response.Listener<JSONArray>() {
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
            //   new PageLoaderCommunity(mActivity, mWebView).execute(urlLink);
        } else {
            Type myObjectType = new TypeToken<Integer>() {
            }.getType();
            Integer nrCatalog = (Integer) LeroyApplication.getCacheManager().get("catalog_nr", Integer.class, myObjectType);
            myObjectType = new TypeToken<Catalog>() {
            }.getType();
            if (nrCatalog != null) {
                for (int i = 0; i < nrCatalog; i++) {
                    Catalog catalog = (Catalog) LeroyApplication.getCacheManager().get("catalog_" + i, Catalog.class, myObjectType);
                    if (catalog != null) {
                        catalog.buildImage();
                        cataloage.add(catalog);
                    }

                }
                setUIelements();
            } else {
                noInternet.setVisibility(View.VISIBLE);
            }


        }
    }

    private void changeFragment(int position) {
        ViewCatalogFragment f = new ViewCatalogFragment();
        Bundle bundle = new Bundle();
        bundle.putString("title", cataloage.get(position).getTitle());
        bundle.putString("url", cataloage.get(position).getSlug());
        f.setArguments(bundle);
        FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_frame, f).addToBackStack(null);
        ft.commit();
    }

    boolean makeCatalogDetailRequest(final Catalog catalog) {
        requestQueue = Volley.newRequestQueue(mActivity);
        String url = "https://api.publitas.com/v1/groups/leroymerlin/publications/" + catalog.getSlug() + ".json";

        JsonObjectRequest obreq = new JsonObjectRequest(Request.Method.GET, url,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject config = response.getJSONObject("config");
                            //   String websiteURL = config.getString("websiteUrl");
                            String downloadPdfUrl = "https://view.publitas.com" + config.getString("downloadPdfUrl");
                            String coverImageSlug = response.getJSONArray("spreads").getJSONObject(0).getJSONArray("pages").get(0).toString();
                            String coverImageURL = "https://view.publitas.com" + coverImageSlug + "-at600.jpg";
                            String slug = "http://view.publitas.com/leroymerlin/" + config.getString("slug");
                            //  catalog.setWebsiteURL(websiteURL);
                            catalog.setPdfURL(downloadPdfUrl);
                            catalog.setCoverImageURL(coverImageURL);
                            catalog.setSlug(slug);


                            pendingRequests.decrementAndGet();
                            Log.e("request", pendingRequests.get() + " " + slug);
                            if (pendingRequests.get() <= 0) {
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
        seeAllOffers.setEnabled(true);
        //Picasso.with(mActivity).load(cataloage.get(0).getCoverImageURL()).fit().into(coperta_catalog);
        if (Connections.isNetworkConnected(mActivity)) {
            new DownloadImageTask(coperta_catalog)
                    .execute(cataloage.get(0).getCoverImageURL());
        } else {
            coperta_catalog.setImageBitmap(cataloage.get(0).getCover());
            progressView.setVisibility(View.GONE);

        }

        CatalogAdapter adapter = new CatalogAdapter(cataloage.subList(1, cataloage.size()), mActivity);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mActivity);
        mLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        progressLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(mActivity, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {

                        changeFragment(position + 1);
                    }
                })
        );


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity)
            mActivity = (FragmentActivity) context;
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
            cataloage.get(0).buildImageBase(result);
            LeroyApplication.getCacheManager().put("catalog_0", cataloage.get(0));
            //        LeroyApplication.getCacheManager().put("catalog_nr", 1);
            bmImage.setImageBitmap(result);
            progressView.setVisibility(View.GONE);
        }
    }
}
