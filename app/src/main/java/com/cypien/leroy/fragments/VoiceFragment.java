package com.cypien.leroy.fragments;

import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ipaulpro.afilechooser.utils.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex on 9/10/2015.
 */
public class VoiceFragment extends Fragment {

    private ScrollView view;
    private EditText subject,message;
    private Spinner stores;
    private TextView subjectError,messageError;
    private Button sendMessageButton,fileButton;
    private SharedPreferences sp;
    private static final int REQUEST_CHOOSER = 1234;
    private String filePath;
    TextView successMessage;

    //date utilizator
    String firstname, lastname, phone, email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=(ScrollView) getActivity().getLayoutInflater().inflate(R.layout.voice_screen,container,false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Vocea clientului");
        ((ImageView) actionBarView.findViewById(R.id.logo)).setImageResource(R.drawable.logo);
        actionBarView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "ClientVoiceFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        filePath = "";
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        firstname = sp.getString("firstname", "");
        lastname = sp.getString("lastname", "");
        phone = sp.getString("phone","");
        email = sp.getString("email","");

        subject =(EditText)view.findViewById(R.id.subject);
        message =(EditText)view.findViewById(R.id.message);
        subjectError = (TextView)view.findViewById(R.id.subject_error);
        messageError = (TextView)view.findViewById(R.id.message_error);
        successMessage = (TextView)view.findViewById(R.id.success_message);
        sendMessageButton = (Button)view.findViewById(R.id.send_message);
        fileButton = (Button)view.findViewById(R.id.add_files);
        stores = (Spinner) view.findViewById(R.id.stores);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.stores, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        stores.setAdapter(adapter);

        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    subjectError.setVisibility(View.GONE);
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

        fileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, "Alegeți un fișier");
                startActivityForResult(intent, REQUEST_CHOOSER);
            }
        });

        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.focus_thief).requestFocus();
                boolean ok = true;
                message.clearFocus();
                if (subject.getText().toString().equals("")) {
                    subject.setBackgroundResource(R.drawable.round_corners_red_border);
                    subjectError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (message.getText().toString().equals("")) {
                    message.setBackgroundResource(R.drawable.round_corners_red_border);
                    messageError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (ok) {
                    successMessage.setVisibility(View.GONE);
                    String content = "";
                    if (!lastname.equals("")) {
                        content = content + "Nume: " + lastname;
                        if (!firstname.equals(""))
                            content = content + " " + firstname + "\n";
                        else
                            content = content + "\n";
                    }
                    if (!phone.equals(""))
                        content = content + "Telefon: " + phone + "\n";
                    if (!email.equals(""))
                        content = content + "Adresa e-mail: " + email + "\n";
                    content = content + "Magazin: " + stores.getSelectedItem().toString() + "\n\n";
                    content = content + "\n" + message.getText().toString();
                    JSONObject jsn = new JSONObject();
                    try {
                        jsn.put("email_body", content);
                        jsn.put("email_subject", subject.getText().toString());
                        jsn.put("email_from", email);
                        jsn.put("email_from_name", firstname + " " + lastname);
                        jsn.put("email_attachment", fileToBase64(filePath));
                        jsn.put("email_attachment_name", new File(filePath).getName());
                        jsn = makeRequest("email_send", jsn.toString());
                        if (jsn!=null && jsn.getString("result").equals("null")){
                            new NotificationDialog(getActivity(), "Mesajul dumneavoastră a fost expediat cu succes !").show();
                            getFragmentManager().popBackStack();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == getActivity().RESULT_OK) {
                    final Uri uri = data.getData();
                    String path = FileUtils.getPath(getActivity(), uri);
                    if (path != null && FileUtils.isLocal(path)) {
                        filePath = path;
                        successMessage.setVisibility(View.VISIBLE);
                    } else {
                        filePath = "";
                        successMessage.setVisibility(View.GONE);
                    }
                }
                break;
        }
    }

    public JSONObject makeRequest(String... params){
        ArrayList<JSONObject> parameters = new ArrayList<>();
        try {
            for(int i = 1;i<params.length;i++){
                parameters.add(new JSONObject(params[i]) );
            }
            JSONObject request = new JSONObject();
            request.put("method", params[0]);
            request.put("params",new JSONArray(parameters));
            return new JSONObject(new WebServiceConnector().execute("http://www.facem-facem.ro/customAPI/privateEndpoint/"+ LeroyApplication.getInstance().APIVersion, "q=" + URLEncoder.encode(request.toString())).get());
        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String fileToBase64(String filePath){
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(filePath);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            Base64OutputStream output64 = new Base64OutputStream(output, Base64.DEFAULT);
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                output64.write(buffer, 0, bytesRead);
            }
            output64.close();
            return output.toString();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
