package com.cypien.leroy.activities;/*
 * Created by Alex on 29.08.2016.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.Encrypt;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.StaticMethods;
import com.cypien.leroy.utils.WebServiceConnector;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

public class EditAccountActivity extends AppCompatActivity {

    private final String lastNameError = "<font color=\"#D50000\">Completați numele</font>";
    private final String firstNameError = "<font color=\"#D50000\">Completați prenumele</font>";
    private final String phoneError = "<font color=\"#D50000\">Număr invalid de cifre</font>";
    private final String emailError = "<font color=\"#D50000\">Adresă de email invalidă</font>";
    private final String passwordError = "<font color=\"#D50000\">Parolele nu sunt identice</font>";
    private final String inexistentPassword = "<font color=\"#D50000\">Completați parola</font>";
    private final String cityError = "<font color=\"#D50000\">Completați localitatea</font>";
    private final String emailTaken = "<font color=\"#D50000\">Această adresă este deja utilizată</font>";
    private final int IMAGE = 284;
    boolean[] errors = new boolean[7];
    private CircularImageView userImage;
    private ImageView editImage;
    private EditText lastName, firstName, city, email, confirmPassword, password, phone;
    private com.rey.material.widget.Spinner county;
    private TextView name;
    private Button save;
    private ImageView back;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private boolean ok;
    private String lastPhone = "";
    private String lastEmail = "";
    private String lastPassword = "";
    private String lastConfirmPassword = "";
    private String imagePath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_account_activity);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Edit Account"));
        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Edit Account");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());
        sp = getSharedPreferences("com.cypien.leroy_preferences", Context.MODE_PRIVATE);
        lastName = (EditText) findViewById(R.id.last_name);
        firstName = (EditText) findViewById(R.id.first_name);
        city = (EditText) findViewById(R.id.city);
        email = (EditText) findViewById(R.id.email);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        password = (EditText) findViewById(R.id.password);
        phone = (EditText) findViewById(R.id.phone);

        back = (ImageView) findViewById(R.id.back_arrow);

        county = (com.rey.material.widget.Spinner) findViewById(R.id.county);
        name = (TextView) findViewById(R.id.name);
        save = (Button) findViewById(R.id.save_profile);
        userImage = (CircularImageView) findViewById(R.id.user_image);
        editImage = (ImageView) findViewById(R.id.edit_image);


        Arrays.fill(errors, false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.judete, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        county.setAdapter(adapter);


        setFocus(findViewById(R.id.first_name_focus), firstName);
        setFocus(findViewById(R.id.last_name_focus), lastName);
        setFocus(findViewById(R.id.phone_focus), phone);
        setFocus(findViewById(R.id.email_focus), email);
        setFocus(findViewById(R.id.password_focus), password);
        setFocus(findViewById(R.id.confirm_password_focus), confirmPassword);
        setFocus(findViewById(R.id.city_focus), city);
        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[0] && hasFocus) {
                    errors[0] = false;
                    firstName.setText("");
                }
                if (hasFocus)
                    firstName.setSelection(firstName.length());
            }
        });
        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditAccountActivity.this, AddPhotoActivity.class);
                startActivityForResult(intent, IMAGE);
            }
        });
        if (imagePath != null) {
            Picasso.with(EditAccountActivity.this)
                    .load(new File(imagePath))
                    .fit().centerInside()
                    .into(userImage);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        lastName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[1] && hasFocus) {
                    errors[1] = false;
                    lastName.setText("");

                }
                if (hasFocus)
                    lastName.setSelection(lastName.length());
            }
        });

        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[2] && hasFocus) {
                    errors[2] = false;
                    if (!lastPhone.equals(Jsoup.parse(phoneError).text()))
                        phone.setText(lastPhone);
                    else
                        phone.setText("");
                }
                if (hasFocus)
                    phone.setSelection(phone.length());
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[3] && hasFocus) {
                    errors[3] = false;
                    if (!lastEmail.equals(Jsoup.parse(emailError).text()) && !lastEmail.equals(Jsoup.parse(emailTaken).text()))
                        email.setText(lastEmail);
                    else
                        email.setText("");

                }
                if (hasFocus)
                    email.setSelection(email.length());
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[4] && hasFocus) {
                    errors[4] = false;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    if (!lastPassword.equals(Jsoup.parse(passwordError).text()) && !lastPassword.equals(Jsoup.parse(inexistentPassword).text()))
                        password.setText(lastPassword);
                    else
                        password.setText("");

                }
                if (hasFocus)
                    password.setSelection(password.length());
            }
        });
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[5] && hasFocus) {
                    errors[5] = false;
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    if (!lastConfirmPassword.equals(Jsoup.parse(inexistentPassword).text()) && !lastConfirmPassword.equals(Jsoup.parse(passwordError).text()))
                        confirmPassword.setText(lastConfirmPassword);
                    else
                        confirmPassword.setText("");

                }
                if (hasFocus)
                    confirmPassword.setSelection(confirmPassword.length());
            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[6] && hasFocus) {
                    errors[6] = false;
                    city.setText("");
                }
                if (hasFocus)
                    city.setSelection(city.length());
            }
        });

        save.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                lastPhone = phone.getText().toString();
                lastEmail = email.getText().toString();
                lastPassword = password.getText().toString();
                lastConfirmPassword = confirmPassword.getText().toString();

                ok = true;
                if (firstName.getText().toString().equals("")) {
                    firstName.setText(Html.fromHtml(firstNameError));
                    ok = false;
                    errors[0] = true;

                }
                if (lastName.getText().toString().equals("")) {
                    lastName.setText(Html.fromHtml(lastNameError));
                    ok = false;
                    errors[1] = true;

                }

                Pattern pattern = Pattern.compile("\\d{10}");
                Matcher matcher = pattern.matcher(lastPhone);
                if (!matcher.matches()) {
                    phone.setText(Html.fromHtml(phoneError));
                    ok = false;
                    errors[2] = true;
                }

                if (lastEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(lastEmail).matches()) {
                    email.setText(Html.fromHtml(emailError));
                    ok = false;
                    errors[3] = true;
                }


                if (lastPassword.equals("")) {
                    ok = false;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setText(Html.fromHtml(inexistentPassword));
                    errors[4] = true;
                }
                if (lastConfirmPassword.equals("")) {
                    ok = false;
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPassword.setText(Html.fromHtml(inexistentPassword));
                    errors[5] = true;
                }
                if (!lastPassword.equals(lastConfirmPassword)) {
                    errors[4] = true;
                    errors[5] = true;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setText(Html.fromHtml(passwordError));
                    confirmPassword.setText(Html.fromHtml(passwordError));
                    ok = false;

                }

                if (city.getText().toString().equals("")) {
                    city.setText(Html.fromHtml(cityError));
                    ok = false;
                    errors[6] = true;

                }

                for (int i = 0; i < 7; i++)
                    if (errors[i]) {
                        findViewById(R.id.parentLayout).requestFocus();
                        return;
                    }
                if (ok) {
                    if (Connections.isNetworkConnected(EditAccountActivity.this)) {
                        if (imagePath != null) {
                            uploadPhoto();
                            editProfileInformation();
                        } else {
                            editProfileInformation();
                            getUserInformation();
                            new NotificationDialog(EditAccountActivity.this, "Modificările au fost realizate cu succes!").show();
                            finish();
                        }

                    } else {
                        new NotificationDialog(EditAccountActivity.this, "Vă rugăm să vă conectați la internet pentru a vă putea salva modificările!").show();
                    }
                }
                findViewById(R.id.parentLayout).requestFocus();
            }
        });


        initData();
    }


    private void setFocus(View viewById, final View view) {
        viewById.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.requestFocus();
            }
        });
    }


    private void initData() {

        if (!sp.getString("avatar", "").equals(""))
            userImage.setImageBitmap(StaticMethods.decodeBase64(sp.getString("avatar", "")));

        email.setText(sp.getString("email", ""));
        phone.setText(sp.getString("phone", ""));
        name.setText(sp.getString("firstname", "") + " " + sp.getString("lastname", ""));
        firstName.setText(sp.getString("firstname", ""));
        lastName.setText(sp.getString("lastname", ""));
        password.setText(sp.getString("password", ""));
        confirmPassword.setText(sp.getString("password", ""));
        city.setText(sp.getString("address", ""));
        int position = Arrays.asList((getResources().getStringArray(R.array.judete))).indexOf(sp.getString("city", ""));
        county.setSelection(position);
    }

    //preia adresa imaginii
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            if (requestCode == IMAGE) {
                imagePath = data.getStringExtra("path");
                pathIsNull(imagePath);
                Picasso.with(EditAccountActivity.this)
                        .load(new File(imagePath))
                        .fit().centerInside()
                        .into(userImage);
            }
        }
    }

    private void pathIsNull(String path) {
        if (path == null)
            new NotificationDialog(EditAccountActivity.this, "Ne pare rau, dar imaginea pe care ati selectat-o nu se gaseste stocata pe dispozivul dumneavoastra!").show();
    }

    // incarca pe server noul avatar
    private void uploadPhoto() {
        RequestParams params = new RequestParams();
        params.put("api_m", "profile_updateavatar");
        params.put("api_c", sp.getString("apiclientid", ""));
        params.put("api_s", sp.getString("apiaccesstoken", ""));
        params.put("api_v", sp.getString("apiversion", ""));
        params.put("api_sig", sp.getString("signature", ""));
        try {
            params.put("upload", new File(imagePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://facem-facem.ro/api.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                super.onPostProcessResponse(instance, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getUserInformation();
                new NotificationDialog(EditAccountActivity.this, "Modificările au fost realizate cu succes!").show();
                finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    //ia de pe server informatiile despre utilizator
    private void getUserInformation() {
        try {
            JSONObject response = LeroyApplication.getInstance().makeRequest("user_get", sp.getString("endpointCookie", ""), sp.getString("userid", ""));
            response = response.getJSONObject("result");
            spEditor = sp.edit();
            spEditor.putString("email", response.getString("email"));
            spEditor.putString("birthday", response.getString("birthday"));
            spEditor.putString("firstname", response.getString("firstname"));
            spEditor.putString("lastname", response.getString("lastname"));
            spEditor.putString("phone", response.getString("phone"));
            spEditor.putString("avatar", response.getString("avatar"));
            spEditor.putString("address", response.getString("address"));
            spEditor.putString("city", response.getString("city"));
            spEditor.putString("posts", response.getString("posts"));
            spEditor.putString("dailyposts", "" + (response.getDouble("dailyposts") % 2f));
            spEditor.putString("friendcount", response.getString("friendcount"));
            spEditor.putString("joindate", getDate(Long.parseLong(response.getString("joindate") + "000")));
            spEditor.putString("profilevisits", response.getString("profilevisits"));
            spEditor.putString("blognum", response.getString("blognum"));
            spEditor.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // transforma timpul din milisecunde in data calendaristica
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        return DateFormat.format("dd.MM.yyyy", cal).toString();
    }

    //modifica pe server datele utilizatorului
    private void editProfileInformation() {
        String link = "http://facem-facem.ro/api.php";
        String parameters = "";
        if (!(password.getText().toString()).equals(sp.getString("password", ""))) {
            parameters = "api_m=" + "profile_updatepassword" +
                    "&currentpassword_md5=" + Encrypt.getMD5UTFEncryptedPass(sp.getString("password", "")) +
                    "&email=" + email.getText().toString() +
                    "&emailconfirm=" + email.getText().toString() +
                    "&newpassword_md5=" + Encrypt.getMD5UTFEncryptedPass(password.getText().toString()) +
                    "&newpasswordconfirm_md5=" + Encrypt.getMD5UTFEncryptedPass(password.getText().toString()) +
                    "&api_c=" + sp.getString("apiclientid", "") +
                    "&api_s=" + sp.getString("apiaccesstoken", "") +
                    "&api_v=" + sp.getString("apiversion", "") +
                    "&api_sig=" + sp.getString("signature", "");
            try {
                new WebServiceConnector().execute(link, parameters).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        parameters = "api_m=" + "profile_updateprofile" +
                "&userfield[field5]=" + firstName.getText().toString() +
                "&userfield[field6]=" + lastName.getText().toString() +
                "&userfield[field10]=" + phone.getText().toString() +
                "&userfield[field12]=" + (county.getSelectedItemPosition() + 1) +
                "&userfield[field13]=" + city.getText().toString() +
                "&api_c=" + sp.getString("apiclientid", "") +
                "&api_s=" + sp.getString("apiaccesstoken", "") +
                "&api_v=" + sp.getString("apiversion", "") +
                "&api_sig=" + sp.getString("signature", "");
        try {
            Log.e("parameters", parameters);
            Log.e("link", link);
            String response = new WebServiceConnector().execute(link, parameters).get();
            Log.e("response", response);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
