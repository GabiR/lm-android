package com.cypien.leroy.fragments;/*
 * Created by Alex on 01.09.2016.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cypien.leroy.R;
import com.cypien.leroy.activities.CommunityDashboard;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.PageLoaderCommunity;

public class ViewCatalogFragment  extends Fragment {

    private View view;
    private WebView mWebView;
    private RelativeLayout mWebViewContainer;
    private RelativeLayout noInternet;
    private ProgressBar progressBar;
    private ImageView share, clipboard;
    private TextView urlLabel;
    private LinearLayout retry;
    private String urlLink = "";
    private FragmentActivity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.web_screen, container, false);

        final Bundle bundle = getArguments();
        urlLink = bundle.getString("url", "");
        ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText(bundle.getString("title", "Catalog"));
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.VISIBLE);
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);


        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Cataloage");
                if(!bundle.getBoolean("fromList")) {
                    ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.GONE);
                    ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.VISIBLE);
                }
                else{
                    ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);
                    ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.VISIBLE);
                }
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setMax(100);

        share = (ImageView) view.findViewById(R.id.share);
        clipboard = (ImageView) view.findViewById(R.id.clipboard);
        urlLabel = (TextView) view.findViewById(R.id.url);
        retry = (LinearLayout) view.findViewById(R.id.retry);

        noInternet = (RelativeLayout) view.findViewById(R.id.no_internet);
        retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noInternet.setVisibility(View.GONE);
                loadPage();
            }
        });

       // injectCookies();

        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebViewContainer = (RelativeLayout) view.findViewById(R.id.webViewContainer);


        clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) mActivity.getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url",urlLink );
                clipboard.setPrimaryClip(clip);
                Toast.makeText(mActivity, "Link copiat in clipboard", Toast.LENGTH_LONG).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,urlLink);
                startActivity(Intent.createChooser(intent, "Distribuiţi cu"));
            }
        });

        urlLabel.setText(urlLink);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient());

        loadPage();

        return view;
    }

    // controleaza comportamentul webview-ului la incarcarea paginilor
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if(Connections.isNetworkConnected(mActivity)){
                noInternet.setVisibility(View.GONE);
                if(url.contains("http://"))
                    urlLabel.setText(url.substring(url.indexOf("http://")+"http://".length()));
                else
                    urlLabel.setText(url);
                if (Uri.parse(url).getHost().equals("www.facem-facem.ro")) {
                    if (url.contains("pdf")){
                        view.loadUrl("http://docs.google.com/gview?embedded=true&url=" +url);
                        return false;
                    }
                    if (url.contains("fbredirect")||url.contains("newattachment.php")){
                        view.loadUrl(url);
                        return false;
                    }
                    new PageLoaderCommunity(((CommunityDashboard) mActivity),view).execute(url);
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
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }


    // verfica daca exista internet si incarca pagina
    private void loadPage(){
        if(Connections.isNetworkConnected(mActivity)){
            noInternet.setVisibility(View.GONE);
            mWebViewContainer.setVisibility(View.VISIBLE);
            mWebView.loadUrl(urlLink);
         //   new PageLoaderCommunity(mActivity, mWebView).execute(urlLink);
        }else {
            noInternet.setVisibility(View.VISIBLE);
            mWebViewContainer.setVisibility(View.GONE);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if(context instanceof FragmentActivity)
            mActivity = (FragmentActivity) context;
    }
}