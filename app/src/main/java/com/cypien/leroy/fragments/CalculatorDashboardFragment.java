package com.cypien.leroy.fragments;/*
 * Created by Alex on 20.09.2016.
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

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.app.Dialog;
import com.rey.material.app.DialogFragment;
import com.rey.material.app.SimpleDialog;
import com.rey.material.widget.Button;
import com.rey.material.widget.Spinner;

public class CalculatorDashboardFragment extends Fragment implements View.OnClickListener, TextWatcher {


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

        laminatedButton.setOnClickListener(this);
        parquetButton.setOnClickListener(this);
        pvcPanelButton.setOnClickListener(this);
        varnishButton.setOnClickListener(this);
        whitePaintButton.setOnClickListener(this);
        antiMoldButton.setOnClickListener(this);
        colorPaintButton.setOnClickListener(this);
        magneticPaintButton.setOnClickListener(this);
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
        Dialog.Builder builder = null;
        switch (v.getId()) {

            case R.id.laminated_button:
                unit = " pachete";
                i = 0;
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        result = (TextView) dialog.findViewById(R.id.result);
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity, R.array.parquet_modes, R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                        modes.setAdapter(adapter);

                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        modes.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(Spinner parent, View view, int position, long id) {
                                calculate();
                            }
                        });

                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator parchet laminat")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.parquet_button:
                unit = " pachete";
                i = 0;
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        result = (TextView) dialog.findViewById(R.id.result);
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity, R.array.parquet_modes, R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                        modes.setAdapter(adapter);

                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        modes.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(Spinner parent, View view, int position, long id) {
                                calculate();
                            }
                        });

                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator parchet din lemn masiv")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.pvc_button:
                i = 0;

                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        result = (TextView) dialog.findViewById(R.id.result);
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(mActivity, R.array.parquet_modes, R.layout.simple_spinner_item);
                        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                        modes.setAdapter(adapter);

                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        modes.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(Spinner parent, View view, int position, long id) {
                                calculate();
                            }
                        });

                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator covor PVC")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.varnish_button:
                i = 1;
                unit = " găleți/mână";
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        result = (TextView) dialog.findViewById(R.id.result);
                        result.setText("- găleți/mână");
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        modes.setVisibility(View.GONE);
                        surfacePack.setHint("Acoperire/găleată");
                        lengthInput.setHint("Înălțime");
                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);


                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator lăcuire")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.white_button:
                i = 2;
                unit = " găleți";
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        result = (TextView) dialog.findViewById(R.id.result);
                        result.setText("- găleți");
                        dialog.findViewById(R.id.hands).setVisibility(View.VISIBLE);
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        modes.setVisibility(View.GONE);
                        noHands = (EditText) dialog.findViewById(R.id.no_hands);

                        surfacePack.setHint("Acoperire/găleată");
                        lengthInput.setHint("Înălțime");
                        noHands.addTextChangedListener(CalculatorDashboardFragment.this);
                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);


                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator vopsea albă")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.anti_mold_button:
                i = 2;
                unit = " găleți";
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        result = (TextView) dialog.findViewById(R.id.result);
                        result.setText("- găleți");
                        dialog.findViewById(R.id.hands).setVisibility(View.VISIBLE);
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        modes.setVisibility(View.GONE);
                        noHands = (EditText) dialog.findViewById(R.id.no_hands);

                        surfacePack.setHint("Acoperire/găleată");
                        lengthInput.setHint("Înălțime");
                        noHands.addTextChangedListener(CalculatorDashboardFragment.this);
                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);


                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator vopsea antimucegai")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.color_button:
                i = 2;
                unit = " găleți";
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        result = (TextView) dialog.findViewById(R.id.result);
                        result.setText("- găleți");
                        dialog.findViewById(R.id.hands).setVisibility(View.VISIBLE);
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        modes.setVisibility(View.GONE);
                        noHands = (EditText) dialog.findViewById(R.id.no_hands);

                        surfacePack.setHint("Acoperire/găleată");
                        lengthInput.setHint("Înălțime");
                        noHands.addTextChangedListener(CalculatorDashboardFragment.this);
                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);


                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator vopsea colorată")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
            case R.id.magnetic_button:
                i = 1;
                unit = " găleți/mână";
                builder = new SimpleDialog.Builder(R.style.DialogStyle) {
                    @Override
                    protected Dialog onBuild(Context context, int styleId) {
                        return super.onBuild(context, styleId);

                    }

                    @Override
                    protected void onBuildDone(Dialog dialog) {
                        dialog.layoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

                        result = (TextView) dialog.findViewById(R.id.result);
                        result.setText("- găleți/mână");
                        lengthInput = (android.widget.EditText) dialog.findViewById(R.id.length);
                        widthInput = (EditText) dialog.findViewById(R.id.width);
                        surfacePack = (EditText) dialog.findViewById(R.id.per_pack);
                        modes = (Spinner) dialog.findViewById(R.id.mode);
                        modes.setVisibility(View.GONE);
                        surfacePack.setHint("Acoperire/găleată");
                        lengthInput.setHint("Înălțime");
                        surfacePack.addTextChangedListener(CalculatorDashboardFragment.this);
                        widthInput.addTextChangedListener(CalculatorDashboardFragment.this);
                        lengthInput.addTextChangedListener(CalculatorDashboardFragment.this);


                    }

                    @Override
                    public void onPositiveActionClicked(DialogFragment fragment) {
                        fragment.getDialog().dismiss();
                    }


                };


                builder.title("Calculator vopsea magnetică")
                        .positiveAction("ÎNCHIDE")
                        .contentView(R.layout.custom_calculator_dialog);
                break;
        }
        DialogFragment fragment = DialogFragment.newInstance(builder);
        fragment.show(getFragmentManager(), null);
    }

    private void calculate() {
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
            float r = surface / Float.parseFloat(q) + percent * surface / 100;

            result.setText(r + unit);
        } else {
            result.setText("-" + unit);
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
}
