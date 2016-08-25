package com.cypien.leroy.activities;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.Encrypt;
import com.cypien.leroy.utils.MapUtil;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.sromku.simple.fb.Permission;
import com.sromku.simple.fb.SimpleFacebook;
import com.sromku.simple.fb.SimpleFacebookConfiguration;
import com.sromku.simple.fb.entities.Profile;
import com.sromku.simple.fb.listeners.OnLoginListener;
import com.sromku.simple.fb.listeners.OnProfileListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


/**
 * Created by Alex on 8/12/2015.
 */
public class LoginActivity extends Activity {
    private Button loginButton;
    private ImageView facebookLoginButton;
    private Button newAccountButton;
    private EditText userName, password;
    private TextView userNameError, passwordError;
    private TextView forgot,vistor;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private boolean ok;
    private final int SUCCESS_REGISTRATION=60;
    private String fbFirstName,fbLastName,fbEmail,fbName,fbId,fbAccessToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);

        setContentView(R.layout.login_screen);


        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "LoginActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        loginButton             = (Button) findViewById(R.id.login);
        newAccountButton        = (Button) findViewById(R.id.new_user);
        facebookLoginButton     = (ImageView) findViewById(R.id.facebook_login);
        userName                = (EditText) findViewById(R.id.user_name);
        password                = (EditText) findViewById(R.id.password);
        userNameError           = (TextView) findViewById(R.id.user_name_error);
        passwordError           = (TextView) findViewById(R.id.password_error);

        vistor = (TextView) findViewById(R.id.visit);
        vistor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor=sp.edit();
                spEditor.clear();
                spEditor.commit();
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(getApplicationContext())) {
                    showForgotPasswordDialog();
                } else {
                    new NotificationDialog(LoginActivity.this, "Pentru a vă putea reseta parola trebuie sa fiți conectat la internet!").show();
                }
            }
        });

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    passwordError.setVisibility(View.GONE);
                }
            }
        });

        userName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    userNameError.setVisibility(View.GONE);
                }
            }
        });

        facebookLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initFacebook();
                if (Connections.isNetworkConnected(getApplicationContext())) {
                    SimpleFacebook.getInstance(LoginActivity.this).login(new OnLoginListener() {
                        @Override
                        public void onLogin(final String accessToken, List<Permission> acceptedPermissions, List<Permission> declindedPermissions) {
                            fbAccessToken = accessToken;
                            Profile.Properties properties = new Profile.Properties.Builder()
                                    .add(Profile.Properties.ID)
                                    .add(Profile.Properties.NAME)
                                    .add(Profile.Properties.FIRST_NAME)
                                    .add(Profile.Properties.LAST_NAME)
                                    .add(Profile.Properties.EMAIL)
                                    .build();
                            SimpleFacebook.getInstance().getProfile(properties, new OnProfileListener() {
                                @Override
                                public void onComplete(Profile profile) {
                                    if (profile.getEmail() != null) {
                                        fbFirstName = profile.getFirstName();
                                        fbLastName = profile.getLastName();
                                        fbName = profile.getName();
                                        fbEmail = profile.getEmail();
                                        fbId = profile.getId();
                                        facebookConnect();
                                    } else {
                                        new NotificationDialog(LoginActivity.this,"Nu vă puteți conecta prin Facebook întrucât nu ați fost de accord cu preluarea informațiilor dumneavoastră").show();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onCancel() {
                            Log.v("","");
                        }

                        @Override
                        public void onException(Throwable throwable) {
                            Log.v("","");
                        }

                        @Override
                        public void onFail(String s) {
                            Log.v("","");
                        }
                    });

                } else {
                    new NotificationDialog(LoginActivity.this,"Vă rugăm să vă conectați la internet pentru a putea intra în cont!").show();
                }
            }

        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.focus_thief).requestFocus();
                ok = true;
                if (userName.getText().toString().equals("")) {
                    userName.setBackgroundResource(R.drawable.round_corners_red_border);
                    userNameError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (password.getText().toString().equals("")) {
                    password.setBackgroundResource(R.drawable.round_corners_red_border);
                    passwordError.setVisibility(View.VISIBLE);
                    ok = false;
                }
                if (ok) {
                    if (Connections.isNetworkConnected(getApplicationContext())) {
                        login(userName.getText().toString(), password.getText().toString());
                    } else {
                        new NotificationDialog(LoginActivity.this,"Vă rugăm să vă conectați la internet pentru a putea intra în cont!").show();
                    }
                }
            }
        });

        newAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, AccountActivity.class);
                intent.putExtra("source", "new");
                startActivityForResult(intent, SUCCESS_REGISTRATION);
            }
        });

        JSONObject response = makeContactRequest();


        if ( sp.getBoolean("isConnected", false) && Connections.isNetworkConnected(getApplicationContext()))
            login(sp.getString("username",""),sp.getString("password",""));
    }

    public JSONObject makeContactRequest(){
        try {
            String request = "{\"method\":\"catalog_get_all\",\"params\": [0, 100]}";
            return new JSONObject(new WebServiceConnector().execute("http://www.leroymerlin.ro/api/publicEndpoint", "q=" + URLEncoder.encode(request)).get());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    private void facebookConnect(){
        try {
            String answer=LeroyApplication.getInstance().makeRequest("user_get_id_by_fbid",fbId).getString("result");
            if(answer.equals("false")){
                if (init()) {
                    // adugam utilizator nou
                    String link = "http://facem-facem.ro/api.php";
                    String parameters = "api_m=" + "register_addmember" +
                            "&agree=" + 1 +
                            "&username=" + fbName+
                            "&email=" +fbEmail +
                            "&emailconfirm=" + fbEmail +
                            "&password_md5=" + Encrypt.getMD5UTFEncryptedPass(fbId) +
                            "&passwordconfirm_md5=" + Encrypt.getMD5UTFEncryptedPass(fbId) +
                            "&userfield[field5]=" + fbFirstName+
                            "&userfield[field6]=" + fbLastName +
                            "&userfield[field9]=" + "l" +
                            "&userfield[field10]=" + "0700000000" +
                            "&userfield[field11]=" + "" +
                            "&userfield[field12]=" + "1" +
                            "&userfield[field13]=" + "Bucuresti"+
                            "&dst=" + "1" +
                            "&timezoneoffset=" + "2" +
                            "&api_c=" + sp.getString("apiclientid", "") +
                            "&api_s=" + sp.getString("apiaccesstoken", "") +
                            "&api_v=" + sp.getString("apiversion", "") +
                            "&api_sig=" + sp.getString("signature", "");
                    try {
                        new WebServiceConnector().execute(link, parameters).get(5, TimeUnit.SECONDS);
                        login(fbName,fbId);
                        LeroyApplication.getInstance().makeRequest("user_update_fbdata", sp.getString("userid", ""), fbId, fbName, Encrypt.getMD5UTFEncryptedPass(fbId), fbAccessToken);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }else{
                LeroyApplication.getInstance().makeRequest("user_update_fbdata",answer,fbId,fbName,Encrypt.getMD5UTFEncryptedPass(fbId),fbAccessToken);
                JSONObject response = LeroyApplication.getInstance().makeRequest("user_get",answer);
                response = response.getJSONObject("result");
                login(response.getString("username"),fbId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    //preluarea informatiilor de autentificare de la pagina de register.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SimpleFacebook.getInstance(LoginActivity.this).onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SUCCESS_REGISTRATION) {
                userName.setText(data.getStringExtra("username"));
                password.setText(data.getStringExtra("password"));
            }
        }
    }

    //Conectarea utilizatorului la platforma si obtinerea cookie-urilor din site.
    private void login(String username, String password){
        if(init()) {
            String result = "";
            String link = "http://facem-facem.ro/api.php";
            String parameters = "api_m=" + "login_login" +
                    "&vb_login_md5password_utf=" + Encrypt.getMD5UTFEncryptedPass(password) +
                    "&vb_login_username=" + username +
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
                JSONObject response = new JSONObject(result);
                String acces = response.getJSONObject("response").getString("errormessage");
                if (acces.contains("redirect_login")) {
                    spEditor=sp.edit();
                    spEditor.putString("username",username);
                    spEditor.putString("password", password);
                    spEditor.putString("userid", response.getJSONObject("session").getString("userid"));
                    spEditor.putString("sessionhash",response.getJSONObject("session").getString("dbsessionhash"));
                    spEditor.putBoolean("isConnected", true);
                    try {
                        Map<String,String> cookeis = new GetCookies().execute(username, password).get();
                        if(cookeis!=null){
                            spEditor.putString("cookies", MapUtil.mapToString(cookeis));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    spEditor.commit();
                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();
                } else
                    new NotificationDialog(LoginActivity.this,"Numele de utilizator sau parola sunt incorecte").show();
            } catch (JSONException e){
                new NotificationDialog(LoginActivity.this,"Ne cerem scuze, dar platforma nu funcționează. Vă rugăm reveniți.").show();
                e.printStackTrace();
            }
        }else{
            new NotificationDialog(LoginActivity.this,"Ne cerem scuze, dar platforma nu funcționează. Veti putea utiliza aplicatia in modul vizitator.").show();
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    // Obtinerea informatiilor de acces la platforma vBulletin
    private boolean init() {
        String result = "";
        String link = "http://facem-facem.ro/api.php";
        String parameters = "api_m=api_init&clientname=vMobile&clientversion=1&platformname=Android&platformversion=4.4&uniqueid=abc";
        try {
            result = new WebServiceConnector().execute(link, parameters).get(5, TimeUnit.SECONDS);
        } catch (Exception e) {
            e.printStackTrace();
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
            new NotificationDialog(LoginActivity.this,"Ne cerem scuze, dar platforma nu funcționează. Vă rugăm reveniți.").show();
            e.printStackTrace();
        }
        return true;
    }

    //AsyncTask pentru logarea userului pe site si extragerea cookie-urilor aferente userului logat
    private class GetCookies extends AsyncTask<String, Void, Map<String, String>> {
        @Override
        protected Map<String, String> doInBackground(String... params) {
            try {
                return Jsoup.connect("http://www.facem-facem.ro/login.php?do=login")
                        .data("vb_login_username", params[0])
                        .data("cookieuser", "true")
                        .data("vb_login_md5password", Encrypt.getMD5UTFEncryptedPass(params[1]))
                        .data("vb_login_md5password_utf", Encrypt.getMD5UTFEncryptedPass(params[1]))
                        .data("securitytoken", "guest")
                        .data("s", "")
                        .data("do", "login")
                        .data("hiddenlogin", "do")
                        .method(Connection.Method.POST).execute().cookies();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    //initializare facebook
    private void initFacebook(){
        Permission[] permissions = new Permission[] {
                Permission.EMAIL
        };
        SimpleFacebookConfiguration configuration = new SimpleFacebookConfiguration.Builder()
                .setAppId("1002994899713823")
                .setNamespace("com.cypien.leroy")
                .setPermissions(permissions)
                .build();
        SimpleFacebook.setConfiguration(configuration);
    }

    private void showForgotPasswordDialog() {
        final Dialog dialog = new Dialog(this);
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(R.layout.forgot_password_dialog);
//        dialog.setCancelable(false);
//        Button cancelButton = (Button)dialog.findViewById(R.id.cancel);
//        Button resetButton = (Button)dialog.findViewById(R.id.reset);
//        final EditText email = (EditText)dialog.findViewById(R.id.email);
//        final TextView emailError = (TextView)dialog.findViewById(R.id.email_error);
//
//        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    v.setBackgroundResource(R.drawable.round_corners_black_border);
//                    emailError.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        resetButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ok = true;
//                if (email.getText().toString().equals("")||!email.getText().toString().contains("@")||!email.getText().toString().contains(".")) {
//                    email.setBackgroundResource(R.drawable.round_corners_red_border);
//                    emailError.setVisibility(View.VISIBLE);
//                    ok = false;
//                }
//                if(ok){
//                    if(init()){
//                        String link = "http://facem-facem.ro/api.php";
//                        String parameters = "api_m=" + "login_emailpassword"+
//                                "&email=" + email.getText().toString()+
//                                "&api_c=" + sp.getString("apiclientid", "") +
//                                "&api_s=" + sp.getString("apiaccesstoken", "") +
//                                "&api_v=" + sp.getString("apiversion", "") +
//                                "&api_sig=" + sp.getString("signature", "");
//                        new WebServiceConnector().execute(link,parameters);
//                        dialog.dismiss();
//                    }
//                }
//            }
//        });
//
//        cancelButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//            }
//        });
        dialog.show();
    }

}


