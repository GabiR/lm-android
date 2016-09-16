package com.cypien.leroy.fragments;/*
 * Created by Alex on 12.09.2016.
 */

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.ShopDashboard;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.MyWebViewClient;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.io.InputStream;

public class ViewPageFragment extends Fragment {

    private View view;
    private WebView mWebView;
    private RelativeLayout mWebViewContainer;
    private RelativeLayout noInternet;
    private ProgressBar progressBar;
    private ImageView share, clipboard;
    private TextView urlLabel;
    private LinearLayout retry;
    private InputStream input;
    private String encoded;
    private String url;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.web_screen, container, false);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: View WebPage"));
        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: View WebPage");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Bundle bundle = this.getArguments();
        String title = bundle.getString("title", "");
        url = bundle.getString("url");
        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText(title);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.VISIBLE);

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
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
        if (!title.equals("Cariere")) {

            byte[] buffer = new byte[0];
            try {
                if (!title.equals("Preț INEGALABIL"))
                    if (title.equals("Sfaturi UTILE"))
                        input = getActivity().getAssets().open("hideAdvicesSections.js");
                    else
                        input = getActivity().getAssets().open("hideCalculatorSections.js");
                else
                    input = getActivity().getAssets().open("hideProductsSections.js");
                buffer = new byte[input.available()];
                input.read(buffer);
                input.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            // String-ify the script byte-array using BASE64 encoding !!!
            encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
        }


        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebViewContainer = (RelativeLayout) view.findViewById(R.id.webViewContainer);

        ((ShopDashboard) getActivity()).setCurrentWebview(mWebView);

        clipboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("url", urlLabel.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Link copiat in clipboard", Toast.LENGTH_LONG).show();
            }
        });

        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, urlLabel.getText().toString());
                startActivity(Intent.createChooser(intent, "Distribuiţi cu"));
            }
        });

        urlLabel.setText(url);

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setBuiltInZoomControls(true);
        mWebView.setWebViewClient(new MyWebViewClient(encoded, getActivity(), progressBar, noInternet, urlLabel));

        loadPage();

        return view;
    }


    // verfica daca exista internet si incarca pagina
    private void loadPage() {
        if (Connections.isNetworkConnected(getActivity())) {
            noInternet.setVisibility(View.GONE);
            mWebViewContainer.setVisibility(View.VISIBLE);
            mWebView.loadUrl(url);

        } else {
            noInternet.setVisibility(View.VISIBLE);
            mWebViewContainer.setVisibility(View.GONE);
        }
    }


}