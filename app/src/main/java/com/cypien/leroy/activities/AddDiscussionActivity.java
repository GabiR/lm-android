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

import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;

import org.json.JSONObject;

/**
 * Created by Alex on 19/10/15.
 */
public class AddDiscussionActivity extends AppCompatActivity{
    private final String subjectError = "<font color=\"#D50000\">Completați titlul</font>";
    private final String messageError = "<font color=\"#D50000\">Completați mesajul</font>";
    private View view;
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
                    for(int i=0;i<2;i++)
                        if(errors[i])
                            return;
                    if (ok) {
                        int forumId;
                        if(category.getSelectedItemPosition()==0){
                            forumId=2;
                        }else{
                            forumId=category.getSelectedItemPosition()+3;
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
                            if (new JSONObject(result).getJSONObject("response").getString("errormessage").equals("redirect_postthanks")){
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

    /* @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.add_discussion_screen, container, false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Adaugă discutie");
        ((ImageView) actionBarView.findViewById(R.id.logo)).setImageResource(R.drawable.logo);
        actionBarView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);


        categories = (Spinner) view.findViewById(R.id.categories);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.categories_array, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        categories.setAdapter(adapter);
        categories.setSelection(12);

        title = (EditText) view.findViewById(R.id.subject);
        message = (EditText) view.findViewById(R.id.message);
        titleError = (TextView) view.findViewById(R.id.subject_error);
        messageError = (TextView) view.findViewById(R.id.message_error);

        // controleaza disparitia erorilor
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    titleError.setVisibility(View.GONE);
                }
            }
        });

        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    messageError.setVisibility(View.GONE);
                }
            }
        });

        addDiscussionButton= (Button) view.findViewById(R.id.post_discussion);
        addDiscussionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(getActivity())) {
                    view.findViewById(R.id.focus_thief).requestFocus();
                    boolean ok = true;
                    message.clearFocus();
                    if (title.getText().toString().equals("")) {
                        title.setBackgroundResource(R.drawable.round_corners_red_border);
                        titleError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (message.getText().toString().length() < 10) {
                        message.setBackgroundResource(R.drawable.round_corners_red_border);
                        messageError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (ok) {
                        int forumId;
                        if(categories.getSelectedItemPosition()==0){
                            forumId=2;
                        }else{
                            forumId=categories.getSelectedItemPosition()+3;
                        }
                        String result = "";
                        String link = "http://facem-facem.ro/api.php";
                        String parameters = "api_m=" + "newthread_postthread" +
                                "&forumid=" + "" + forumId +
                                "&subject=" + title.getText().toString() +
                                "&message=" + message.getText().toString() +
                                "&api_c=" + sp.getString("apiclientid", "") +
                                "&api_s=" + sp.getString("apiaccesstoken", "") +
                                "&api_v=" + sp.getString("apiversion", "") +
                                "&api_sig=" + sp.getString("signature", "");
                        try {
                            result = new WebServiceConnector().execute(link, parameters).get();
                            if (new JSONObject(result).getJSONObject("response").getString("errormessage").equals("redirect_postthanks")){
                                new NotificationDialog(getActivity(), "Discutia dumneavoastră a fost adăugată cu succes!").show();
                                getFragmentManager().popBackStack();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    new NotificationDialog(getActivity(), "Pentru a putea adăuga discutia dumneavoastră, vă rugăm să va conectați la internet!").show();
                }
            }
        });
        return view;
    }
*/
}
