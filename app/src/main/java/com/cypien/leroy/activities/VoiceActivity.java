package com.cypien.leroy.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.rey.material.widget.Spinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Created by Alex on 02.09.2016.
 */
public class VoiceActivity extends AppCompatActivity {
    private static final int REQUEST_CHOOSER = 1234;
    private final String subjectError = "<font color=\"#D50000\">Completați subiectul</font>";
    private final String messageError = "<font color=\"#D50000\">Completați mesajul</font>";
    private final String emailError = "<font color=\"#D50000\">Adresa de email invalidă</font>";
    private final String firstNameError = "<font color=\"#D50000\">Completați prenumele</font>";
    private final String lastNameError = "<font color=\"#D50000\">Completați numele</font>";
    EditText email, firstName, lastName, phone, subject, message;
    Spinner store;
    boolean[] errors = new boolean[5];
    Button pickFile, sendMessage;
    private String filePath = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vocea_clientului);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Vocea Clientului"));
        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Vocea Clientului");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        email = (EditText) findViewById(R.id.email);
        firstName = (EditText) findViewById(R.id.first_name);
        lastName = (EditText) findViewById(R.id.last_name);
        phone = (EditText) findViewById(R.id.phone);
        subject = (EditText) findViewById(R.id.subject);
        message = (EditText) findViewById(R.id.message);
        store = (Spinner) findViewById(R.id.store);
        pickFile = (Button) findViewById(R.id.pick_file);
        sendMessage = (Button) findViewById(R.id.send_message);


        findViewById(R.id.back_arrow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.stores, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        store.setAdapter(adapter);

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[0] && hasFocus) {
                    errors[0] = false;
                    email.setText("");
                }
            }
        });
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[1] && hasFocus) {
                    errors[1] = false;
                    firstName.setText("");
                }
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[2] && hasFocus) {
                    errors[2] = false;
                    lastName.setText("");
                }
            }
        });
        subject.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[3] && hasFocus) {
                    errors[3] = false;
                    subject.setText("");
                }
            }
        });
        message.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[4] && hasFocus) {
                    errors[4] = false;
                    message.setText("");
                }
            }
        });
        pickFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getContentIntent = FileUtils.createGetContentIntent();
                Intent intent = Intent.createChooser(getContentIntent, "Alegeți un fișier");
                startActivityForResult(intent, REQUEST_CHOOSER);
            }
        });
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean ok = true;
                String lastEmail = email.getText().toString();
                if (lastEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(lastEmail).matches()) {
                    email.setText(Html.fromHtml(emailError));
                    ok = false;
                    errors[0] = true;
                }
                if (firstName.getText().toString().equals("")) {
                    firstName.setText(Html.fromHtml(firstNameError));
                    ok = false;
                    errors[1] = true;
                }
                if (lastName.getText().toString().equals("")) {
                    lastName.setText(Html.fromHtml(lastNameError));
                    ok = false;
                    errors[2] = true;
                }
                if (subject.getText().toString().equals("")) {
                    subject.setText(Html.fromHtml(subjectError));
                    ok = false;
                    errors[3] = true;
                }

                if (message.getText().toString().equals("")) {
                    message.setText(Html.fromHtml(messageError));
                    ok = false;
                    errors[4] = true;
                }
                findViewById(R.id.focus_thief).requestFocus();
                for (int i = 0; i < 5; i++)
                    if (errors[i])
                        return;
                if (ok) {
                    if (Connections.isNetworkConnected(VoiceActivity.this)) {
                        String content = "";

                        content = content + "Nume: " + lastName.getText().toString() + " " + firstName.getText().toString() + "\n";
                        Pattern pattern = Pattern.compile("\\d{10}");
                        Matcher matcher = pattern.matcher(phone.getText().toString());
                        if (!phone.getText().toString().equals("") && matcher.matches())
                            content = content + "Telefon: " + phone + "\n";

                        content = content + "Adresa e-mail: " + lastEmail + "\n";
                        content = content + "Magazin: " + store.getSelectedItem().toString() + "\n\n";
                        content = content + "\n" + message.getText().toString();
                        JSONObject jsn = new JSONObject();
                        try {
                            jsn.put("email_body", content);
                            jsn.put("email_subject", subject.getText().toString());
                            jsn.put("email_from", email);
                            jsn.put("email_from_name", firstName.getText().toString() + " " + lastName.getText().toString());
                            jsn.put("email_attachment", fileToBase64(filePath));
                            jsn.put("email_attachment_name", new File(filePath).getName());
                            jsn = LeroyApplication.getInstance().makePublicRequest("email_send", jsn.toString());
                            if (jsn != null && jsn.getString("result").equals("null")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(VoiceActivity.this, R.style.AppCompatAlertDialogStyle);
                                builder.setMessage(Html.fromHtml("Mesajul dumneavoastră a fost expediat cu succes!"));

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        VoiceActivity.this.finish();
                                    }
                                });
                                builder.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        new NotificationDialog(VoiceActivity.this, "Pentru a putea trimite mesaje prin vocea clientului, vă rugăm să vă autentificați!").show();

                    }
                }
            }
        });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHOOSER:
                if (resultCode == RESULT_OK) {
                    final Uri uri = data.getData();
                    String path = FileUtils.getPath(this, uri);
                    if (path != null && FileUtils.isLocal(path)) {
                        filePath = path;
                        Toast.makeText(this, "Fișierul a fost adăugat cu succes!", Toast.LENGTH_LONG).show();
                    } else {
                        filePath = "";
                        Toast.makeText(this, "Fișierul nu a fost adăugat.", Toast.LENGTH_LONG).show();

                    }
                }
                break;
        }
    }

    private String fileToBase64(String filePath) {
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

        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }
}
