package com.cypien.leroy.fragments;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.activities.MainActivity;
import com.cypien.leroy.activities.ShopDashboard;
import com.cypien.leroy.utils.PageLoader;

/**
 * Created by Alex on 9/7/2015.
 */
public class FacebookFragment extends Fragment {
    private View view;
    private WebView mWebView;
    private String url;
    private ImageView share, clipboard;


    public FacebookFragment(){

    }

    @SuppressLint("ValidFragment")
    public FacebookFragment(String url) {
        this.url=url;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.web_screen, container, false);


        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Pagina Facebook");

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        share = (ImageView) view.findViewById(R.id.share);
        clipboard = (ImageView) view.findViewById(R.id.clipboard);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        mWebView = (WebView) view.findViewById(R.id.web_view);
        ((ShopDashboard)getActivity()).setCurrentWebview(mWebView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());

        mWebView.loadUrl(url);
        return view;
    }

    // controleaza comportamentul webview-ului la incarcarea paginilor
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.facem-facem.ro")) {
                if (url.contains("pdf")){
                    view.loadUrl("http://docs.google.com/gview?embedded=true&url=" +url);
                    return false;
                }
                new PageLoader(((MainActivity)getActivity()),view).execute(url);
                return true;
            }
            else {
                view.loadUrl(url);
                return false;
            }
        }
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            mWebView.setVisibility(View.GONE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            mWebView.setVisibility(View.VISIBLE);
        }
    }
}
