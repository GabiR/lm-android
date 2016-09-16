package com.cypien.leroy.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.joanzapata.pdfview.PDFView;

/**
 * Created by alexa on 10/1/2015.
 */
public class PdfViewerFragment extends Fragment {
    private View view;
    private PDFView pdfView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.pdf_viewer, container, false);
        pdfView = (PDFView) view.findViewById(R.id.pdfView);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Pdf Viewer");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Pdf Viewer"));
        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("PDF");

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        Bundle bundle = getArguments();
        pdfView.fromAsset(bundle.getString("filename"))
                .defaultPage(1)
                .showMinimap(false)
                .enableSwipe(true)
                .load();

        return view;
    }
}
