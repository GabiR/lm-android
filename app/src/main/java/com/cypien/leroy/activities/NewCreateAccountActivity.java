package com.cypien.leroy.activities;/*
 * Created by Alex on 25.08.2016.
 */

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.SignUpEvent;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.Encrypt;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.rey.material.widget.CheckBox;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NewCreateAccountActivity extends AppCompatActivity {

    private final String lastNameError = "<font color=\"#D50000\">Completați nume</font>";
    private final String firstNameError = "<font color=\"#D50000\">Completați prenume</font>";
    private final String usernameError = "<font color=\"#D50000\">Completați nume de utilizator</font>";
    private final String phoneError = "<font color=\"#D50000\">Număr invalid de cifre</font>";
    private final String emailError = "<font color=\"#D50000\">Adresă de email invalidă</font>";
    private final String passwordError = "<font color=\"#D50000\">Parolele nu sunt identice</font>";
    private final String inexistentPassword = "<font color=\"#D50000\">Completați parola</font>";
    private final String cityError = "<font color=\"#D50000\">Completați localitatea</font>";
    private final String usernameTaken = "<font color=\"#D50000\">Acest nume de utilizator este deja utilizat</font>";
    private final String emailTaken = "<font color=\"#D50000\">Această adresă este deja utilizată</font>";


    EditText lastName, firstName, city, email, confirmPassword, password, username, phone;
    com.rey.material.widget.Spinner county;
    com.rey.material.widget.CheckBox agreement;
    TextView rules;
    Button create;
    ImageView back;
    boolean[] errors = new boolean[8];
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private boolean ok;
    private String lastUsername = "", lastPhone = "";
    private String lastEmail = "";
    private String lastPassword = "";
    private String lastConfirmPassword = "";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_account_activity);
        sp = getSharedPreferences("com.cypien.leroy_preferences", Context.MODE_PRIVATE);
        lastName = (EditText) findViewById(R.id.last_name);
        firstName = (EditText) findViewById(R.id.first_name);
        city = (EditText) findViewById(R.id.city);
        email = (EditText) findViewById(R.id.email);
        confirmPassword = (EditText) findViewById(R.id.confirm_password);
        password = (EditText) findViewById(R.id.password);

        username = (EditText) findViewById(R.id.username);
        phone = (EditText) findViewById(R.id.phone);

        back = (ImageView) findViewById(R.id.back_arrow);

        county = (com.rey.material.widget.Spinner) findViewById(R.id.county);
        rules = (TextView) findViewById(R.id.rules);
        create = (Button) findViewById(R.id.create);

        agreement = (CheckBox) findViewById(R.id.checkbox);

        Arrays.fill(errors, false);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.judete, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

        county.setAdapter(adapter);

        setFocus(findViewById(R.id.first_name_focus), firstName);
        setFocus(findViewById(R.id.last_name_focus), lastName);
        setFocus(findViewById(R.id.email_focus), email);
        setFocus(findViewById(R.id.phone_focus), phone);
        setFocus(findViewById(R.id.username_focus), username);
        setFocus(findViewById(R.id.password_focus), password);
        setFocus(findViewById(R.id.confirm_password_focus), confirmPassword);
        setFocus(findViewById(R.id.city), city);

        firstName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[0] && hasFocus) {
                    errors[0] = false;
                    firstName.setText("");
                }
            }
        });

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
            }
        });
        username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[2] && hasFocus) {
                    errors[2] = false;
                    if (!lastUsername.equals(Jsoup.parse(usernameError).text()) && !lastUsername.equals(Jsoup.parse(usernameTaken).text()))
                        username.setText(lastUsername);
                    else
                        username.setText("");

                }
            }
        });
        phone.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[3] && hasFocus) {
                    errors[3] = false;
                    if (!lastPhone.equals(Jsoup.parse(phoneError).text()))
                        phone.setText(lastPhone);
                    else
                        phone.setText("");
                }
            }
        });
        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[4] && hasFocus) {
                    errors[4] = false;
                    if (!lastEmail.equals(Jsoup.parse(emailError).text()) && !lastEmail.equals(Jsoup.parse(emailTaken).text()))
                        email.setText(lastEmail);
                    else
                        email.setText("");

                }
            }
        });
        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[5] && hasFocus) {
                    errors[5] = false;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    if (!lastPassword.equals(Jsoup.parse(passwordError).text()) && !lastPassword.equals(Jsoup.parse(inexistentPassword).text()))
                        password.setText(lastPassword);
                    else
                        password.setText("");

                }
            }
        });
        confirmPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[6] && hasFocus) {
                    errors[6] = false;
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    if (!lastConfirmPassword.equals(Jsoup.parse(inexistentPassword).text()) && !lastConfirmPassword.equals(Jsoup.parse(passwordError).text()))
                        confirmPassword.setText(lastConfirmPassword);
                    else
                        confirmPassword.setText("");

                }
            }
        });

        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[7] && hasFocus) {
                    errors[7] = false;
                    city.setText("");
                }
            }
        });
        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(NewCreateAccountActivity.this, R.style.AppCompatAlertDialogStyle);
                builder.setTitle("Reguli forum");
                builder.setMessage(Html.fromHtml("<b>Doar numele de utilizator va apărea public pe site</b><br>Înregistrarea pe acest forum este gratuită! " +
                        "Insistăm pe respectarea regulilor și politicilor de mai jos. Dacă sunteți de acord cu termenii, vă rugăm să verificați caseta de " +
                        "validare și apăsați butonul CREEAZĂ CONT.<br>Deși administratorii și moderatorii acestui forum vor incerca să țină la distanță toate " +
                        "mesajele neplăcute din acest forum, este imposibil pentru noi să revizuim toate mesajele. Toate mesajele exprimă părerea autorului și " +
                        "membrii administrației forumului nu vor fi trași la răspundere pentru conținutul mesajelor.<br>Fiind de acord cu aceste reguli, vei garanta " +
                        "că nu vei posta mesaje cu conținut obscen, vulgar, cu orientare sexuală, cu ură, amenințări, sau interzise de lege.<br>Administratorii " +
                        "forumului iși rezervă dreptul de a șterge, edita, muta sau închide orice forum pentru orice motiv.<br><font color=\"#4F872E\">Pentru a trece la " +
                        "următorul pas, trebuie să fiți de acord cu termenii și condițiile site-ului.</font>"));
                builder.setPositiveButton("ÎNCHIDE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                //   new NotificationDialog(NewCreateAccountActivity.this, "Vă rugăm să vă conectați la internet pentru a vă putea inregistra!").show();
                //    showRulesPopup();
            }
        });
        create.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {

                lastUsername = username.getText().toString();
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
                if (lastUsername.equals("")) {
                    username.setText(Html.fromHtml(usernameError));
                    ok = false;
                    errors[2] = true;
                }
                Pattern pattern = Pattern.compile("\\d{10}");
                Matcher matcher = pattern.matcher(lastPhone);
                if (!matcher.matches()) {
                    phone.setText(Html.fromHtml(phoneError));
                    ok = false;
                    errors[3] = true;
                }

                if (lastEmail.equals("") || !Patterns.EMAIL_ADDRESS.matcher(lastEmail).matches()) {
                    email.setText(Html.fromHtml(emailError));
                    ok = false;
                    errors[4] = true;
                }


                if (lastPassword.equals("")) {
                    ok = false;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setText(Html.fromHtml(inexistentPassword));
                    errors[5] = true;
                }
                if (lastConfirmPassword.equals("")) {
                    ok = false;
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPassword.setText(Html.fromHtml(inexistentPassword));
                    errors[6] = true;
                }
                if (!lastPassword.equals(lastConfirmPassword)) {
                    errors[5] = true;
                    errors[6] = true;
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    confirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    password.setText(Html.fromHtml(passwordError));
                    confirmPassword.setText(Html.fromHtml(passwordError));
                    ok = false;

                }

                if (city.getText().toString().equals("")) {
                    city.setText(Html.fromHtml(cityError));
                    ok = false;
                    errors[8] = true;

                }
                if (!agreement.isChecked()) {
                    Toast.makeText(NewCreateAccountActivity.this, "Nu ați fost de acord cu regulile comunității", Toast.LENGTH_LONG).show();
                    ok = false;
                }
                for (int i = 0; i < 9; i++)
                    if (errors[i]) {
                        findViewById(R.id.parentLayout).requestFocus();
                        return;
                    }
                if (ok) {
                    if (Connections.isNetworkConnected(NewCreateAccountActivity.this)) {
                        if (init()) {
                            // adugam utilizator nou
                            String result = "";
                            String link = "http://facem-facem.ro/api.php";
                            String parameters = "api_m=" + "register_addmember" +
                                    "&agree=" + 1 +
                                    "&username=" + username.getText().toString() +
                                    "&email=" + email.getText().toString() +
                                    "&emailconfirm=" + email.getText().toString() +
                                    "&password_md5=" + Encrypt.getMD5UTFEncryptedPass(password.getText().toString()) +
                                    "&passwordconfirm_md5=" + Encrypt.getMD5UTFEncryptedPass(confirmPassword.getText().toString()) +
                                    "&userfield[field5]=" + firstName.getText().toString() +
                                    "&userfield[field6]=" + lastName.getText().toString() +
                                    "&userfield[field9]=" + "l" +
                                    "&userfield[field10]=" + phone.getText().toString() +
                                    "&userfield[field11]=" + "" +
                                    "&userfield[field12]=" + (county.getSelectedItemPosition() + 1) +
                                    "&userfield[field13]=" + city.getText().toString() +
                                    "&dst=" + "1" +
                                    "&timezoneoffset=" + "2" +
                                    "&api_c=" + sp.getString("apiclientid", "") +
                                    "&api_s=" + sp.getString("apiaccesstoken", "") +
                                    "&api_v=" + sp.getString("apiversion", "") +
                                    "&api_sig=" + sp.getString("signature", "");
                            try {
                                result = new WebServiceConnector().execute(link, parameters).get(5, TimeUnit.SECONDS);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            try {
                                String response = new JSONObject(result).getString("response");
                                if (response.contains("emailtaken")) {
                                    //seterror
                                    errors[4] = true;
                                    email.setText(Html.fromHtml(emailTaken));
                                    ok = false;

                                }
                                if (response.contains("usernametaken")) {
                                    errors[2] = true;
                                    username.setText(Html.fromHtml(usernameTaken));
                                    ok = false;
                                }
                                if (response.contains("alreadyregistered")) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("username", username.getText().toString());
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                                if (response.contains("registeremail")) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("username", username.getText().toString());
                                    resultIntent.putExtra("password", password.getText().toString());
                                    setResult(Activity.RESULT_OK, resultIntent);
                                    finish();
                                }
                                Answers.getInstance().logSignUp(new SignUpEvent()
                                        .putMethod("Create account")
                                        .putSuccess(true));
                            } catch (JSONException e) {
                                Answers.getInstance().logSignUp(new SignUpEvent()
                                        .putMethod("Create account")
                                        .putSuccess(false)
                                        .putCustomAttribute("Cause", "Platform not working"));
                                new NotificationDialog(NewCreateAccountActivity.this, "Ne cerem scuze, dar platforma nu funcționează. Vă rugăm reveniți.").show();
                                e.printStackTrace();
                            }
                        }
                    } else {
                        new NotificationDialog(NewCreateAccountActivity.this, "Vă rugăm să vă conectați la internet pentru a vă putea inregistra!").show();
                    }
                }
                findViewById(R.id.parentLayout).requestFocus();
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


    private boolean init() {
        String result = "";
        String link = "http://facem-facem.ro/api.php";
        String parameters = "api_m=api_init&clientname=vMobile&clientversion=1&platformname=Android&platformversion=4.4&uniqueid=abc";
        try {
            result = new WebServiceConnector().execute(link, parameters).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            return false;
        }
        try {
            JSONObject response = new JSONObject(result);
            String signature = Encrypt.getMD5UTFEncryptedPass(response.getString("apiaccesstoken") +
                    response.getString("apiclientid") +
                    response.getString("secret") +
                    "aWZN4XTj");
            spEditor = sp.edit();
            spEditor.putString("apiaccesstoken", response.getString("apiaccesstoken"));
            spEditor.putString("apiversion", response.getString("apiversion"));
            spEditor.putString("apiclientid", response.getString("apiclientid"));
            spEditor.putString("signature", signature);
            spEditor.commit();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return true;
    }
}
