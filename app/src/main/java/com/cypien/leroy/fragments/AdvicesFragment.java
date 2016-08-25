package com.cypien.leroy.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.MainActivity;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.PageLoader;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.util.Map;

/**
 * Created by GabiRotaru on 09/03/16.
 */
public class AdvicesFragment extends Fragment {

    private View view;
    private WebView mWebView;
    private View loading;
    private View noInternet;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.web_screen, container, false);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Sfatul meseriasului");
        ((ImageView) actionBarView.findViewById(R.id.logo)).setImageResource(R.drawable.logo);
        actionBarView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "AdvicesFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        loading=view.findViewById(R.id.loading);
        noInternet=view.findViewById(R.id.no_internet);
        noInternet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPage();
            }
        });

        injectCookies();
        mWebView = (WebView) view.findViewById(R.id.web_view);
        ((MainActivity)getActivity()).setCurrentWebview(mWebView);
        mWebView.setWebViewClient(new MyWebViewClient());
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        loadPage();

        return view;
    }

    // controleaza comportamentul webview-ului la incarcarea paginilor
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Connections.isNetworkConnected(getActivity())){
                if (Uri.parse(url).getHost().equals("www.facem-facem.ro")) {
                    if (url.contains("pdf")){
                        view.loadUrl("http://docs.google.com/gview?embedded=true&url=" +url);
                        return false;
                    }
                    if (url.contains("fbredirect")||url.contains("newattachment.php")){
                        view.loadUrl(url);
                        return false;
                    }
                    new PageLoader(((MainActivity)getActivity()),view).execute(url);
                    return true;
                }else {
                    view.loadUrl(url);
                    return false;
                }
            }else {
                noInternet.setVisibility(View.VISIBLE);
                return true;
            }

        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            loading.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loading.setVisibility(View.GONE);
                    mWebView.setVisibility(View.VISIBLE);
                }
            }, 6000);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            loading.setVisibility(View.GONE);
            mWebView.setVisibility(View.VISIBLE);
        }
    }

    // verfica daca exista internet si incarca pagina
    private void loadPage(){
        if(Connections.isNetworkConnected(getActivity())){
            noInternet.setVisibility(View.GONE);
            new PageLoader((MainActivity) getActivity(), mWebView).execute("http://www.facem-facem.ro/sfaturi.php");
        }else {
            noInternet.setVisibility(View.VISIBLE);
        }
    }

    // adauga cookieurile site-ului pentru a fi folosite de catre webview
    private void injectCookies(){
        Map<String,String> cookies = ((MainActivity)getActivity()).getCookies();
        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        for (Map.Entry<String, String> cookie : cookies.entrySet()){
            String cookieString = cookie.getKey() + "=" + cookie.getValue() + "; domain=" + "www.facem-facem.ro";
            cookieManager.setCookie("www.facem-facem.ro", cookieString);
            CookieSyncManager.getInstance().sync();
        }
    }
}
