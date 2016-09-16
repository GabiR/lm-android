package com.cypien.leroy.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
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
import com.cypien.leroy.activities.CommunityDashboard;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.MyWebViewClient;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * Created by alexa on 10/15/2015.
 */
public class TopUsersFragment extends Fragment {
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.web_screen, container, false);

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Top utilizatori");
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Top Users"));
        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Top Users");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());


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


        byte[] buffer = new byte[0];
        try {
            input = getActivity().getAssets().open("hideSections.js");
            buffer = new byte[input.available()];
            input.read(buffer);
            input.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        // String-ify the script byte-array using BASE64 encoding !!!
        encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
        injectCookies();

        mWebView = (WebView) view.findViewById(R.id.web_view);
        mWebViewContainer = (RelativeLayout) view.findViewById(R.id.webViewContainer);

        ((CommunityDashboard) getActivity()).setCurrentWebview(mWebView);

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

        urlLabel.setText("http://www.facem-facem.ro/leaderboards.php?top_u=luna");

        mWebView.setWebChromeClient(new WebChromeClient() {
            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
            }
        });

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        mWebView.setWebViewClient(new MyWebViewClient(encoded, getActivity(), progressBar, noInternet, urlLabel));
        loadPage();


        return view;
    }

    // verfica daca exista internet si incarca pagina
    private void loadPage() {
        if (Connections.isNetworkConnected(getActivity())) {
            noInternet.setVisibility(View.GONE);
            mWebViewContainer.setVisibility(View.VISIBLE);
            mWebView.loadUrl("http://www.facem-facem.ro/leaderboards.php?top_u=luna");
            //new PageLoaderCommunity(((CommunityDashboard) getActivity()), mWebView).execute("http://www.facem-facem.ro/leaderboards.php?top_u=luna");
        } else {
            noInternet.setVisibility(View.VISIBLE);
            mWebViewContainer.setVisibility(View.GONE);
        }
    }

    // adauga cookieurile site-ului pentru a fi folosite de catre webview
    private void injectCookies() {
        Map<String, String> cookies = ((CommunityDashboard) getActivity()).getCookies();
        CookieSyncManager.createInstance(getActivity());
        CookieManager cookieManager = CookieManager.getInstance();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            String cookieString = cookie.getKey() + "=" + cookie.getValue() + "; domain=" + "www.facem-facem.ro";
            cookieManager.setCookie("www.facem-facem.ro", cookieString);
            CookieSyncManager.getInstance().sync();
        }
    }

 /*   // controleaza comportamentul webview-ului la incarcarea paginilor
    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (Connections.isNetworkConnected(getActivity())) {
                if (Uri.parse(url).getHost().equals("www.facem-facem.ro")) {
                    if (url.contains("pdf")) {
                        view.loadUrl("http://docs.google.com/gview?embedded=true&url=" + url);
                        return false;
                    }
                    if (url.contains("fbredirect") || url.contains("newattachment.php")) {
                        view.loadUrl(url);
                        return false;
                    }
                    new PageLoaderCommunity(((CommunityDashboard) getActivity()), view).execute(url);
                    return true;
                } else {
                    view.loadUrl(url);
                    return false;
                }
            } else {
                noInternet.setVisibility(View.VISIBLE);
                return true;
            }
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            progressBar.setProgress(0);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            progressBar.setVisibility(View.GONE);
        }
    }*/
}
