package com.cypien.leroy.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.utils.Encrypt;
import com.cypien.leroy.utils.WebServiceConnector;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

/**
 * Created by Alex on 8/13/2015.
 */
public class NewAccountFragment extends Fragment {

    private EditText firstName, lastName, userName, phone, email, password, confirmPass,code,address;
    private TextView firstNameError, lastNameError, userNameError, phoneError, emailError, passwordError, agreeError,codeError,addressError;
    private Spinner city;
    private CheckBox agree;
    private TextView rules;
    private Button createButton;
    private ScrollView view;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private boolean ok;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
       /* view = (ScrollView) getActivity().getLayoutInflater().inflate(R.layout.new_account_screen, container, false);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Creează cont");

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "NewAccountFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        firstName = (EditText) view.findViewById(R.id.first_name);
        lastName = (EditText) view.findViewById(R.id.last_name);
        userName = (EditText) view.findViewById(R.id.user_name);
        phone = (EditText) view.findViewById(R.id.phone);
        email = (EditText) view.findViewById(R.id.email);
        password = (EditText) view.findViewById(R.id.password);
        confirmPass = (EditText) view.findViewById(R.id.confirm_password);
        code = (EditText) view.findViewById(R.id.cod);
        address = (EditText) view.findViewById(R.id.localitate);
        agree = (CheckBox) view.findViewById(R.id.checkbox);
        agree.setTypeface(Typeface.createFromAsset(getActivity().getAssets(),"Amatic-Bold.ttf"));
        rules = (TextView) view.findViewById(R.id.rules);
        createButton = (Button) view.findViewById(R.id.create_account);
        firstNameError = (TextView) view.findViewById(R.id.first_name_error);
        lastNameError = (TextView) view.findViewById(R.id.last_name_error);
        userNameError = (TextView) view.findViewById(R.id.user_name_error);
        phoneError = (TextView) view.findViewById(R.id.phone_error);
        emailError = (TextView) view.findViewById(R.id.email_error);
        passwordError = (TextView) view.findViewById(R.id.password_error);
        agreeError = (TextView) view.findViewById(R.id.checkbox_error);
        codeError = (TextView) view.findViewById(R.id.cod_error);
        addressError = (TextView) view.findViewById(R.id.address_error);
        city = (Spinner) view.findViewById(R.id.judet);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.judete, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);

        editClick(address,addressError);
        editClick(firstName, firstNameError);
        editClick(lastName, lastNameError);
        editClick(userName, userNameError);
        editClick(email, emailError);
        editClick(phone, phoneError);
        editClick(code,codeError);

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    confirmPass.setBackgroundResource(R.drawable.round_corners_black_border);
                    passwordError.setVisibility(View.GONE);
                }
            }
        });

        confirmPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    password.setBackgroundResource(R.drawable.round_corners_black_border);
                    passwordError.setVisibility(View.GONE);
                }
            }
        });

        agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                agreeError.setVisibility(View.GONE);
            }
        });

        rules.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRulesPopup();
            }
        });

        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.focus_thief).requestFocus();
                ok = true;
                if (firstName.getText().toString().equals("")) {
                    firstName.setBackgroundResource(R.drawable.round_corners_red_border);
                    firstNameError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (lastName.getText().toString().equals("")) {
                    lastName.setBackgroundResource(R.drawable.round_corners_red_border);
                    lastNameError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (userName.getText().toString().equals("")) {
                    userNameError.setText("VĂ RUGĂM SĂ COMPLETAȚI NUME UTILIZATOR");
                    userName.setBackgroundResource(R.drawable.round_corners_red_border);
                    userNameError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (phone.getText().toString().length()!=10) {
                    phone.setBackgroundResource(R.drawable.round_corners_red_border);
                    phoneError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (email.getText().toString().equals("")||!email.getText().toString().contains("@")||!email.getText().toString().contains(".")) {
                    email.setBackgroundResource(R.drawable.round_corners_red_border);
                    emailError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (password.getText().toString().equals("") || confirmPass.getText().toString().equals("")) {
                    password.setBackgroundResource(R.drawable.round_corners_red_border);
                    confirmPass.setBackgroundResource(R.drawable.round_corners_red_border);
                    passwordError.setText("VĂ RUGĂM SĂ COMPLETAȚI AMBELE CÂMPURI PENTRU PAROLĂ");
                    passwordError.setVisibility(View.VISIBLE);
                    ok = false;
                } else if (!password.getText().toString().equals(confirmPass.getText().toString())) {
                    password.setBackgroundResource(R.drawable.round_corners_red_border);
                    confirmPass.setBackgroundResource(R.drawable.round_corners_red_border);
                    passwordError.setText("PAROLELE FURNIZATE NU SUNT LA FEL!");
                    passwordError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if(code.getText().toString().length()!=17 && !code.getText().toString().equals("")){
                    code.setBackgroundResource(R.drawable.round_corners_red_border);
                    codeError.setVisibility(View.VISIBLE);
                    ok=false;
                }
                if(address.getText().toString().equals("")){
                    address.setBackgroundResource(R.drawable.round_corners_red_border);
                    addressError.setVisibility(View.VISIBLE);
                    ok=false;
                }
                if (!agree.isChecked()) {
                    agreeError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if(ok) {
                    if (Connections.isNetworkConnected(getActivity())) {
                        if(init()){
                            // adugam utilizator nou
                            String result = "";
                            String link = "http://facem-facem.ro/api.php";
                            String parameters = "api_m=" + "register_addmember" +
                                    "&agree=" + 1 +
                                    "&username=" + userName.getText().toString() +
                                    "&email=" + email.getText().toString() +
                                    "&emailconfirm=" + email.getText().toString() +
                                    "&password_md5=" + Encrypt.getMD5UTFEncryptedPass(password.getText().toString()) +
                                    "&passwordconfirm_md5=" + Encrypt.getMD5UTFEncryptedPass(confirmPass.getText().toString()) +
                                    "&userfield[field5]=" + firstName.getText().toString() +
                                    "&userfield[field6]=" + lastName.getText().toString() +
                                    "&userfield[field9]=" + "l" +
                                    "&userfield[field10]=" + phone.getText().toString() +
                                    "&userfield[field11]=" + code.getText().toString() +
                                    "&userfield[field12]=" + (city.getSelectedItemPosition()+1)+
                                    "&userfield[field13]=" + address.getText().toString() +
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
                                    emailError.setText("ACEST E-MAIL ESTE DEJA UTILIZAT");
                                    email.setBackgroundResource(R.drawable.round_corners_red_border);
                                    emailError.setVisibility(View.VISIBLE);
                                    ok = false;
                                }
                                if (response.contains("usernametaken")) {
                                    userNameError.setText("ACEST NUME DE UTILIZATOR ESTE DEJA UTILIZAT");
                                    userName.setBackgroundResource(R.drawable.round_corners_red_border);
                                    userNameError.setVisibility(View.VISIBLE);
                                    ok = false;
                                }
                                if (response.contains("alreadyregistered")) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("username", userName.getText().toString());
                                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                                    getActivity().finish();
                                }
                                if (response.contains("registeremail")) {
                                    Intent resultIntent = new Intent();
                                    resultIntent.putExtra("username", userName.getText().toString());
                                    resultIntent.putExtra("password", password.getText().toString());
                                    getActivity().setResult(Activity.RESULT_OK, resultIntent);
                                    getActivity().finish();
                                }
                            } catch (JSONException e) {
                                new NotificationDialog(getActivity(),"Ne cerem scuze, dar platforma nu funcționează. Vă rugăm reveniți.").show();
                                e.printStackTrace();
                            }
                        }
                    } else {
                        new NotificationDialog(getActivity(),"Vă rugăm să vă conectați la internet pentru a vă putea inregistra!").show();
                    }
                }
            }
        });*/
        return view;
    }

    private void showRulesPopup() {
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.rules_dialog);
        dialog.findViewById(R.id.close_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void editClick(final EditText edit, final TextView error) {
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    error.setVisibility(View.GONE);
                }
            }
        });
    }

    // Obtinerea informatiilor de acces la platforma vBulletin
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


