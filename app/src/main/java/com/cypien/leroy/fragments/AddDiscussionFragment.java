package com.cypien.leroy.fragments;

import android.support.v4.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;

import org.json.JSONObject;

/**
 * Created by Alex on 19/10/15.
 */
public class AddDiscussionFragment extends Fragment{
    private View view;
    private Button addDiscussionButton;
    private EditText title, message;
    private TextView titleError, messageError;
    private Spinner categories;
    private SharedPreferences sp;

    @Nullable
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

}
