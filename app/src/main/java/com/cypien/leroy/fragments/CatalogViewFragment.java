package com.cypien.leroy.fragments;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.cypien.leroy.R;
import com.cypien.leroy.activities.MainActivity;
import com.cypien.leroy.utils.PageLoader;

/**
 * Created by Alex on 8/5/2015.
 */

public class CatalogViewFragment extends Fragment{
    private View view;
    private WebView mWebView;
    private ImageView share, clipboard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.web_screen,container,false);

        mWebView = (WebView) view.findViewById(R.id.web_view);

        share = (ImageView) view.findViewById(R.id.share);
        clipboard = (ImageView) view.findViewById(R.id.clipboard);

        ((MainActivity)getActivity()).setCurrentWebview(mWebView);
        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());
        mWebView.loadUrl("http://catalog.leroymerlin.ro/");
        return view;
    }

    // controleaza comportamentul webview-ului la incarcarea paginilor
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Uri.parse(url).getHost().equals("www.facem-facem.ro")) {
                if (url.contains("pdf")) {
                    view.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
                    return false;
                }
                new PageLoader(((MainActivity)getActivity()),view).execute(url);
                return true;
            }else{
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
