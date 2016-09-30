package com.cypien.leroy.fragments;/*
 * Created by Alex on 28.09.2016.
 */

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.rey.material.widget.Spinner;

public class CalculatorFragment extends Fragment  implements TextWatcher {


    private FragmentActivity mActivity;
    private TextView result;
    private EditText lengthInput;
    private EditText widthInput;
    private EditText surfacePack;
    private Spinner modes;
    private String unit = " pachete";
    private int i = 0;
    private ArrayAdapter<CharSequence> adapter;
    private EditText noHands;
    private TextView neverForget;
    private boolean shown = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.custom_calculator_fragment, container, false);

        Bundle bundle = getArguments();

        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);
        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setVisibility(View.VISIBLE);

        ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.onBackPressed();
            }
        });
        result = (TextView) view.findViewById(R.id.result);
        lengthInput = (android.widget.EditText) view.findViewById(R.id.length);
        widthInput = (EditText) view.findViewById(R.id.width);
        surfacePack = (EditText) view.findViewById(R.id.per_pack);
        modes = (Spinner) view.findViewById(R.id.mode);
        noHands = (EditText) view.findViewById(R.id.no_hands);
        neverForget = (TextView) view.findViewById(R.id.never_forget);
        noHands.addTextChangedListener(this);
        surfacePack.addTextChangedListener(this);
        widthInput.addTextChangedListener(this);
        lengthInput.addTextChangedListener(this);
        String type = bundle.getString("type");
        assert type != null;
        switch (type){
            case "parquet":
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator parchet din lemn masiv");
                adapter = ArrayAdapter.createFromResource(mActivity, R.array.parquet_modes, R.layout.simple_spinner_item);
                break;
            case "pvc":
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator covor PVC");
                adapter = ArrayAdapter.createFromResource(mActivity, R.array.parquet_modes, R.layout.simple_spinner_item);
                break;
            case "varnish":
                i = 1;
                unit = " găleți/mână";
                result.setText("? găleți/mână");
                modes.setVisibility(View.GONE);
                surfacePack.setHint("Acoperire/găleată");
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de tratament: grund și ulei");
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator lăcuire");

                break;
            case "white":
                i = 2;
                unit = " găleți";
                result.setText("? găleți");
               view.findViewById(R.id.hands).setVisibility(View.VISIBLE);
                modes.setVisibility(View.GONE);
                surfacePack.setHint("Acoperire/găleată");
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de amorsă");
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator vopsea albă");
                break;
            case "antimold":
                i = 2;
                unit = " găleți";
                result.setText("? găleți");
                view.findViewById(R.id.hands).setVisibility(View.VISIBLE);
                modes.setVisibility(View.GONE);
                surfacePack.setHint("Acoperire/găleată");
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de amorsă");
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator vopsea antimucegai");
                break;
            case "color":
                i = 2;
                unit = " găleți";
                result.setText("? găleți");
                view.findViewById(R.id.hands).setVisibility(View.VISIBLE);
                modes.setVisibility(View.GONE);
                surfacePack.setHint("Acoperire/găleată");
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de amorsă");
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator vopsea colorată");
                break;
            case "magnetic":
                i = 1;
                unit = " găleți/mână";
                result.setText("? găleți/mână");
                modes.setVisibility(View.GONE);
                surfacePack.setHint("Acoperire/găleată");
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de grund");

                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator vopsea magnetică");
                break;
            case "coating":
                i = 3;
                unit = " găleți (25 kg)";
                result.setText("? găleți (25 kg)");
                view.findViewById(R.id.info).setVisibility(View.VISIBLE);
                ((View) surfacePack.getParent()).setVisibility(View.GONE);
            //   surfacePack.getParent().setVisibility(View.GONE);
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de amorsă");
                adapter = ArrayAdapter.createFromResource(mActivity, R.array.coating_modes, R.layout.simple_spinner_item);
                ((TextView) modes.getChildAt(0)).setText("Granulație");
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator tencuială decorativă");
                break;
            case "universal":
                i = 1;
                unit = " găleți/mână";
                result.setText("? găleți/mână");
                modes.setVisibility(View.GONE);
                surfacePack.setHint("Acoperire/găleată");
                lengthInput.setHint("Înălțime");
                neverForget.setText("Nu uita de grund");

                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator vopsea universală");

                break;
            default:
                ((TextView) ((Toolbar) mActivity.findViewById(R.id.toolbar)).getChildAt(2)).setText("Calculator parchet laminat");
                adapter = ArrayAdapter.createFromResource(mActivity, R.array.parquet_modes, R.layout.simple_spinner_item);

        }



        if(i==0 || i==3) {
            adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            modes.setAdapter(adapter);
            modes.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                @Override
                public void onItemSelected(Spinner parent, View view, int position, long id) {
                    calculate();
                }
            });
        }
        return view;
    }
    private void calculate() {
        if(i==3) {
            float p = modes.getSelectedItemPosition();
            float consum = p/2 + 2.5f;
            float length, width;
            try {
                length = Float.parseFloat(lengthInput.getText().toString());

            } catch (NumberFormatException ex) {
                length = 0.0f;
            }
            try {
                width = Float.parseFloat(widthInput.getText().toString());

            } catch (NumberFormatException ex) {
                width = 0.0f;
            }
            float r = length*width*consum/25;
            result.setText(r + unit);
            if (!shown) {
                shown = true;
                neverForget.setVisibility(View.VISIBLE);
            }
        }
        else {
            String q = surfacePack.getText().toString();


            if (!q.equals("") && Float.valueOf(q) != 0f) {
                float length, width;
                try {
                    length = Float.parseFloat(lengthInput.getText().toString());

                } catch (NumberFormatException ex) {
                    length = 0.0f;
                }
                try {
                    width = Float.parseFloat(widthInput.getText().toString());

                } catch (NumberFormatException ex) {
                    width = 0.0f;
                }

                int percent = (modes.getSelectedItemPosition() + 1) * 5;
                if (i != 0) {
                    percent = 5;
                }

                float surface = length * width;
                //  float res = percent*surface/100;
                if (i == 2) {
                    try {
                        surface *= Float.parseFloat(noHands.getText().toString());

                    } catch (NumberFormatException ignored) {

                    }
                }
                int r = Math.round(surface / Float.parseFloat(q) + percent * surface / 100);

                result.setText(r + unit);
                if (!shown) {
                    shown = true;
                    neverForget.setVisibility(View.VISIBLE);
                }
            } else {
                shown = false;
                neverForget.setVisibility(View.GONE);
                result.setText("?" + unit);
            }
        }

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        calculate();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentActivity)
            mActivity = (FragmentActivity) context;
    }
}
