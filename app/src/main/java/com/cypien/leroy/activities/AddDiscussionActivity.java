package com.cypien.leroy.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

/**
 * Created by Alex on 19/10/15.
 */
public class AddDiscussionActivity extends AppCompatActivity {
    private final String subjectError = "<font color=\"#D50000\">Completați titlul</font>";
    private final String messageError = "<font color=\"#D50000\">Completați mesajul</font>";
    ;
    private Button addDiscussionButton;
    private EditText subject, message;

    private SharedPreferences sp;

    private boolean[] errors = new boolean[2];
    private com.rey.material.widget.Spinner category;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_discussion_screen);
        sp = getSharedPreferences("com.cypien.leroy_preferences", Context.MODE_PRIVATE);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Add Discussion"));
        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Add Discussion");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        category = (com.rey.material.widget.Spinner) findViewById(R.id.category);
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.message);
        addDiscussionButton = (Button) findViewById(R.id.post_discussion);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.categories_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[0] && hasFocus) {
                    errors[0] = false;
                    subject.setText("");
                }
            }
        });
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[1] && hasFocus) {
                    errors[1] = false;

                    message.setText("");
                }
            }
        });
        setFocus(findViewById(R.id.title_focus), subject);

        findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        addDiscussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(AddDiscussionActivity.this)) {
                    findViewById(R.id.focus_thief).requestFocus();
                    boolean ok = true;
                    message.clearFocus();
                    if (subject.getText().toString().equals("")) {
                        subject.setText(Html.fromHtml(subjectError));
                        errors[0] = true;
                        ok = false;
                    }
                    if (message.getText().toString().length() < 10) {
                        message.setText(Html.fromHtml(messageError));
                        errors[1] = true;
                        ok = false;
                    }
                    for (int i = 0; i < 2; i++)
                        if (errors[i])
                            return;
                    if (ok) {
                        int forumId;
                        if (category.getSelectedItemPosition() == 0) {
                            forumId = 2;
                        } else {
                            forumId = category.getSelectedItemPosition() + 3;
                        }
                        String result = "";
                        String link = "http://facem-facem.ro/api.php";
                        String parameters = "api_m=" + "newthread_postthread" +
                                "&forumid=" + "" + forumId +
                                "&subject=" + subject.getText().toString() +
                                "&message=" + message.getText().toString() +
                                "&api_c=" + sp.getString("apiclientid", "") +
                                "&api_s=" + sp.getString("apiaccesstoken", "") +
                                "&api_v=" + sp.getString("apiversion", "") +
                                "&api_sig=" + sp.getString("signature", "");
                        try {
                            result = new WebServiceConnector().execute(link, parameters).get();
                            if (new JSONObject(result).getJSONObject("response").getString("errormessage").equals("redirect_postthanks")) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddDiscussionActivity.this, R.style.AppCompatAlertDialogStyle);
                                builder.setMessage(Html.fromHtml("Discuția dumneavoastră a fost adăugată cu succes!"));

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        AddDiscussionActivity.this.finish();
                                    }
                                });
                                builder.show();


                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    new NotificationDialog(AddDiscussionActivity.this, "Pentru a putea adăuga discuția dumneavoastră, vă rugăm să va conectați la internet!").show();
                }
            }
        });


    }

    private void setFocus(View viewById, final View view) {
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.requestFocus();
            }
        });
    }

}
