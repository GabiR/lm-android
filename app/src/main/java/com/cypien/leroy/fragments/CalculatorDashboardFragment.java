package com.cypien.leroy.fragments;/*
 * Created by Alex on 20.09.2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

public class CalculatorDashboardFragment extends Fragment implements View.OnClickListener{


    private FragmentActivity mActivity;

    private TextView result;
    private android.widget.EditText lengthInput;
    private EditText widthInput;
    private EditText surfacePack;
    private EditText noHands;
    private Spinner modes;
    private int i = -1;
    private String unit = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.calculator_dashboard, container, false);

        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Calculator"));
        ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator");
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.VISIBLE);

        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        LeroyApplication application = (LeroyApplication) mActivity.getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Calculator");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Button laminatedButton = (Button) view.findViewById(R.id.laminated_button);
        Button parquetButton = (Button) view.findViewById(R.id.parquet_button);
        Button pvcPanelButton = (Button) view.findViewById(R.id.pvc_button);
        Button varnishButton = (Button) view.findViewById(R.id.varnish_button);
        Button whitePaintButton = (Button) view.findViewById(R.id.white_button);
        Button antiMoldButton = (Button) view.findViewById(R.id.anti_mold_button);
        Button colorPaintButton = (Button) view.findViewById(R.id.color_button);
        Button magneticPaintButton = (Button) view.findViewById(R.id.magnetic_button);
        Button coatingButton = (Button) view.findViewById(R.id.coating_button);
        Button universalButton = (Button) view.findViewById(R.id.universal_button);

        laminatedButton.setOnClickListener(this);
        parquetButton.setOnClickListener(this);
        pvcPanelButton.setOnClickListener(this);
        varnishButton.setOnClickListener(this);
        whitePaintButton.setOnClickListener(this);
        antiMoldButton.setOnClickListener(this);
        colorPaintButton.setOnClickListener(this);
        magneticPaintButton.setOnClickListener(this);
        coatingButton.setOnClickListener(this);
        universalButton.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity)
            mActivity = (FragmentActivity) context;
    }

    @Override
    public void onClick(View v) {
        CalculatorFragment fragment = new CalculatorFragment();
        Bundle bundle = new Bundle();
        String type = "laminated";

        switch (v.getId()) {

            case R.id.parquet_button:

                type = "parquet";
                break;
            case R.id.pvc_button:

                type = "pvc";
                break;
            case R.id.varnish_button:

                type = "varnish";
                break;
            case R.id.white_button:

                type = "white";
                break;
            case R.id.anti_mold_button:

                type = "antimold";
                break;
            case R.id.color_button:

                type = "color";
                break;
            case R.id.magnetic_button:

                type = "magnetic";
                break;
            case R.id.coating_button:
                type = "coating";
                break;
            case R.id.universal_button:
                type = "universal";
        }
        bundle.putString("type", type);
        fragment.setArguments(bundle);

        FragmentTransaction transaction = mActivity.getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).addToBackStack(null);
        transaction.commit();

    }




}
