package com.cypien.leroy.utils;/*
 * Created by Alex on 09.09.2016.
 */

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

// controleaza comportamentul webview-ului la incarcarea paginilor
public class MyWebViewClient extends WebViewClient {


    private String encoded;
    private Activity activity;
    private View noInternet;
    private ProgressBar progressBar;
    private TextView urlLabel;


    public MyWebViewClient(String encoded, Activity activity, ProgressBar progressBar, View noInternet, TextView urlLabel) {
        super();
        this.encoded = encoded;
        this.activity = activity;
        this.progressBar = progressBar;
        this.noInternet = noInternet;
        this.urlLabel = urlLabel;
    }

    private void injectScriptFile(WebView view) {

        if(encoded!=null && !encoded.equals("")) {
            view.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var script = document.createElement('script');" +
                    "script.type = 'text/javascript';" +
                    "script.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(script)" +
                    "})()");
        }

    }


    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {

        if(Connections.isNetworkConnected(activity)){
            if (Uri.parse(url).getHost().equals("www.facem-facem.ro")) {
                if (url.contains("pdf")){
                    view.loadUrl("http://docs.google.com/gview?embedded=true&url=" +url);
                    return false;
                }


            }
            if (url.contains("pdf")){
                view.loadUrl("http://docs.google.com/gview?embedded=true&url=" +url);
                return false;
            }
            view.loadUrl(url);

            return false;

        }else {
            noInternet.setVisibility(View.VISIBLE);
            return true;
        }
    }

    @Override
    public void onPageStarted(final WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        progressBar.setProgress(0);
        progressBar.setVisibility(View.VISIBLE);
        urlLabel.setText(url);

    }


    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);

        injectScriptFile(view);
        progressBar.setVisibility(View.GONE);

    }
}
