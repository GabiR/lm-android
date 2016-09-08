package com.cypien.leroy.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.Encrypt;
import com.cypien.leroy.utils.MapUtil;
import com.cypien.leroy.utils.MyGestureListener;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.TimeUtils;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by GabiRotaru on 23/08/16.
 */
public class LoginActivity extends AppCompatActivity {
    private ImageView skyImage, shopInfoBtn, footer;
    private TextView shopInfoText, btnContact, txtBack, btnLogin, btnLoginFb, forgot, createAccount;
    private EditText edtUser, edtPass;
    private RelativeLayout main_layout, bottom_dialog;
    private Context context;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    private int screenWidth, screenHeight, skyImageSize;

    private String fbFirstName,fbLastName,fbEmail,fbName,fbId,fbAccessToken;
    private final int SUCCESS_REGISTRATION=60;

    boolean ok;
    private boolean visibleBottomView = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        context = LoginActivity.this;


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

            if (new TimeUtils().isMorning())
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.sky_day));
            else
                window.setStatusBarColor(ContextCompat.getColor(context, R.color.sky_night));
        }

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);

        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "OldLoginActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        screenWidth = metrics.widthPixels;
        screenHeight = metrics.heightPixels;

        main_layout = (RelativeLayout) findViewById(R.id.mainLayout);
        skyImage = (ImageView) findViewById(R.id.skyImage);
        shopInfoBtn = (ImageView) findViewById(R.id.shopInfoBtn);
        shopInfoText = (TextView) findViewById(R.id.shopInfoText);
        footer = (ImageView) findViewById(R.id.footer);
        btnContact = (TextView) findViewById(R.id.btnContact);
        bottom_dialog = (RelativeLayout) findViewById(R.id.bottom_dialog);

        txtBack = (TextView) findViewById(R.id.txtBack);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        edtUser = (EditText) findViewById(R.id.edtUser);
        edtPass = (EditText) findViewById(R.id.edtPass);
        btnLogin = (TextView) findViewById(R.id.btnLogin);
        btnLoginFb = (TextView) findViewById(R.id.btnFb);
        forgot = (TextView) findViewById(R.id.forgot);
        createAccount = (TextView) findViewById(R.id.create_account);
        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, NewCreateAccountActivity.class);
                startActivity(intent);
            }
        });
        Intent i = getIntent();
        if(i==null || i.getStringExtra("source") == null) {
            bottom_dialog.animate().translationY(screenHeight - skyImageSize);
        } else {
            Toast.makeText(context, "Aveti nevoie de un cont pentru comunitate", Toast.LENGTH_LONG).show();
        }


        if (new TimeUtils().isMorning())
            skyImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sky_day));
        else
            skyImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.sky_night));

        shopInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ShopDashboard.class);
                startActivity(intent);
                finish();
            }
        });

        btnContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, VoiceActivity.class);
                startActivity(intent);
            }
        });

        final MyGestureListener footerGestureListener = new MyGestureListener();
        final MyGestureListener bottomViewGestureListener = new MyGestureListener();
        footer.setOnTouchListener(footerGestureListener);
        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyGestureListener.Action action = footerGestureListener.getAction();
                if(action == MyGestureListener.Action.BT || action == MyGestureListener.Action.None){
                    Log.e("footer", "open"+action.toString());
                    visibleBottomView = true;
                    bottom_dialog.animate().translationY(0).withLayer().start();
                }

            }
        });

//        footer.setOnTouchListener(new OnSwipeTouchListener(context) {
//            public void onSwipeTop() {
//                bottom_dialog.animate().translationY(0).withLayer().start();
//
//            }
//        });

        bottom_dialog.setOnTouchListener(bottomViewGestureListener);
        bottom_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(bottomViewGestureListener.getAction() == MyGestureListener.Action.TB){
                    visibleBottomView = false;
                    bottom_dialog.animate().translationY(screenHeight - skyImageSize).withLayer().start();
                }
            }
        });

//        bottom_dialog.setOnTouchListener(new OnSwipeTouchListener(context) {
//            public void onSwipeBottom() {
//                bottom_dialog.animate().translationY(screenHeight - skyImageSize).withLayer().start();
//
//            }
//        });

        txtBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottom_dialog.animate().translationY(screenHeight - skyImageSize).withLayer().start();

            }
        });

        edtUser.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    edtUser.setHintTextColor(ContextCompat.getColor(context, R.color.light_grey));
                    edtUser.setHint("Nume utilizator");
                }
            }
        });

        edtPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus) {
                    edtPass.setHintTextColor(ContextCompat.getColor(context, R.color.light_grey));
                    edtPass.setHint("Parola");
                }
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok = true;
                if (edtUser.getText().toString().equals("")) {
                    edtUser.setText("");
                    edtUser.setHintTextColor(ContextCompat.getColor(context, R.color.light_red));
                    edtUser.setHint("Introduceti e-mail");

                    ok = false;
                }
                if (edtPass.getText().toString().equals("")) {
                    edtPass.setText("");
                    edtPass.setHintTextColor(ContextCompat.getColor(context, R.color.light_red));
                    edtPass.setHint("Introduceti parola");

                    ok = false;
                }
                if (ok) {
                    if (Connections.isNetworkConnected(getApplicationContext())) {
                        login(edtUser.getText().toString(), edtPass.getText().toString());
                    } else {
                        new NotificationDialog(LoginActivity.this,"Vă rugăm să vă conectați la internet pentru a putea intra în cont!").show();
                    }
                }
            }
        });

        btnLoginFb.setOnClickListener(new View.OnClickListener() {
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

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(getApplicationContext())) {
                    showForgotPasswordDialog();
                } else {
                    new NotificationDialog(context, "Pentru a vă putea reseta parola trebuie sa fiți conectat la internet!").show();
                }
            }
        });

        if ( sp.getBoolean("isConnected", false) && Connections.isNetworkConnected(getApplicationContext())) {
            login(sp.getString("username", ""), sp.getString("password", ""));
        }


    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            float shopBtnWidth = screenWidth / 1.29f;
            shopInfoBtn.getLayoutParams().width = (int) shopBtnWidth;
            shopInfoBtn.requestLayout();

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 0, (int) (shopInfoBtn.getHeight() / 4.55f));
            params.addRule(RelativeLayout.ALIGN_BOTTOM, shopInfoBtn.getId());
            shopInfoText.setLayoutParams(params);

            skyImageSize = skyImage.getHeight();
        }
    }


    private void facebookConnect(){
        try {
            String answer= LeroyApplication.getInstance().makePublicRequest("user_get_id_by_fbid",fbId).getString("result");
            if(answer.equals("false")){
                if (init()) {
                    // adaugam utilizator nou
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
                        LeroyApplication.getInstance().makePublicRequest("user_update_fbdata", sp.getString("userid", ""), fbId, fbName, Encrypt.getMD5UTFEncryptedPass(fbId), fbAccessToken);
                    } catch (Exception e) {
                        Log.e("eroare", e.toString());
                        e.printStackTrace();
                    }
                }
            }else{

                JSONObject jsonObject = new JSONObject(answer);
                Log.e("json", jsonObject.getString("userid"));

                LeroyApplication.getInstance().makePublicRequest("user_update_fbdata", jsonObject.getString("userid"), fbId,fbName,Encrypt.getMD5UTFEncryptedPass(fbId),fbAccessToken);
                injectCookies();
                //JSONObject response = LeroyApplication.getInstance().makeRequest("user_get",answer);

             //   response = response.getJSONObject("result");
              //  Log.e("response", response.toString());
                login(fbName,fbId);
            }
        } catch (Exception e) {
            e.printStackTrace();
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

//    preluarea informatiilor de autentificare de la pagina de register.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        SimpleFacebook.getInstance(LoginActivity.this).onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == SUCCESS_REGISTRATION) {
//                userName.setText(data.getStringExtra("username"));
//                password.setText(data.getStringExtra("password"));
            }
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



                   /* Map<String, String> endpointCookie = new HashMap<>();
                    endpointCookie.put("name", "auth");
                    endpointCookie.put("domain", "www.facem-facem.ro");
                    endpointCookie.put("path", "/");
                    endpointCookie.put("value",response.getJSONObject("session").getString("dbsessionhash"));*/
                    spEditor.putString("endpointCookie",response.getJSONObject("session").getString("dbsessionhash"));





                    try {
                        Map<String,String> cookeis = new GetCookies().execute(username, password).get();
                        if(cookeis!=null){
                            spEditor.putString("cookies", MapUtil.mapToString(cookeis));
                        }
                    } catch (InterruptedException | ExecutionException e) {
                        e.printStackTrace();
                    }
                    spEditor.commit();
                    Intent intent = new Intent(LoginActivity.this, CommunityDashboard.class);
                    startActivity(intent);
                    finish();
                } else
                    new NotificationDialog(LoginActivity.this,"Numele de utilizator sau parola sunt incorecte").show();
            } catch (JSONException e){
                new NotificationDialog(LoginActivity.this,"Ne cerem scuze, dar platforma nu funcționează. Vă rugăm reveniți.").show();
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this,"Ne cerem scuze, dar platforma nu funcționează. Vă redirecţionăm spre secţiunea informaţii magazine.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(LoginActivity.this, ShopDashboard.class);
            startActivity(intent);
            finish();
        }
    }

    private void showForgotPasswordDialog() {
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.forgot_password_dialog);
        dialog.setCancelable(true);

        final TextView titluPopup = (TextView) dialog.findViewById(R.id.titluPopup);
        final TextView contentPopup = (TextView) dialog.findViewById(R.id.contentPopup);


        final TextView resetButton = (TextView) dialog.findViewById(R.id.reset);
        final TextView inchideButton = (TextView) dialog.findViewById(R.id.inchide_popup);

        final EditText email = (EditText)dialog.findViewById(R.id.email);
        RelativeLayout root_view_forgot = (RelativeLayout)dialog.findViewById(R.id.root_view_forgot);

        //views that need to catch Touch event

        LinearLayout bg_white = (LinearLayout) dialog.findViewById(R.id.bg_white);

        bg_white.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        //end

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok = true;
                if (email.getText().toString().equals("")||!email.getText().toString().contains("@")||!email.getText().toString().contains(".")) {
                    email.setText("");
                    email.setHintTextColor(ContextCompat.getColor(context, R.color.light_red));
                    email.setHint("Adresa de e-mail incorectă");
                    email.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_edt_rounded_red));

                    ok = false;
                }
                if(ok){
                    if(init()){
                        String link = "http://facem-facem.ro/api.php";
                        String parameters = "api_m=" + "login_emailpassword"+
                                "&email=" + email.getText().toString()+
                                "&api_c=" + sp.getString("apiclientid", "") +
                                "&api_s=" + sp.getString("apiaccesstoken", "") +
                                "&api_v=" + sp.getString("apiversion", "") +
                                "&api_sig=" + sp.getString("signature", "");
                        new WebServiceConnector().execute(link,parameters);

                        titluPopup.setText("Vă mulțumim!");
                        titluPopup.setTypeface(null, Typeface.BOLD);
                        contentPopup.setText("Instrucțiuni pentru resetarea parolei au fost trimise pe adresa dumneavoastră de e-mail.");

                        email.setVisibility(View.GONE);
                        resetButton.setVisibility(View.GONE);
                        inchideButton.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        root_view_forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        inchideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public void onBackPressed() {

        if(visibleBottomView){
            visibleBottomView = false;
            bottom_dialog.animate().translationY(screenHeight - skyImageSize).withLayer().start();
        }
        else
            super.onBackPressed();
    }

    private void injectCookies() {
        Map<String, String> cookies = MapUtil.stringToMap(sp.getString("endpointCookie", ""));
        Log.e("endpointCookie", sp.getString("endpointCookie", "NUUUUUUUUUU"));
        CookieSyncManager.createInstance(this);

        CookieManager cookieManager = CookieManager.getInstance();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            String cookieString = cookie.getKey() + "=" + cookie.getValue() + "; domain=" + "www.facem-facem.ro";
            Log.e("cookieString", cookieString);
            cookieManager.setCookie("www.facem-facem.ro", cookieString);
            CookieSyncManager.getInstance().sync();
        }
    }


}
