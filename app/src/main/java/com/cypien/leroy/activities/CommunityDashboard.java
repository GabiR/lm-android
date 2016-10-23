package com.cypien.leroy.activities;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.format.DateFormat;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.CommunityHome;
import com.cypien.leroy.fragments.ContestFragment;
import com.cypien.leroy.fragments.DiscussionsFragment;
import com.cypien.leroy.fragments.ProjectsListFragment;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.MapUtil;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.rey.material.widget.Spinner;

import org.json.JSONObject;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by GabiRotaru on 24/08/16.
 */
public class CommunityDashboard extends AppCompatActivity {


    private final String lastNameError = "<font color=\"#D50000\">Completați numele</font>";
    private final String firstNameError = "<font color=\"#D50000\">Completați prenumele</font>";
    private final String phoneError = "<font color=\"#D50000\">Număr invalid de cifre</font>";
    private final String cityError = "<font color=\"#D50000\">Completați localitatea</font>";
    private final String secondPhoneError = "<font color=\"#D50000\">Număr invalid de telefon</font>";

    private LinearLayout prevLayout;
    private Context context;

    private WebView currentWebview;
    private Stack<String> htmlStack;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private int inactive;
    private int active;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = CommunityDashboard.this;
        setContentView(R.layout.community_dashboard);

        float density = context.getResources().getDisplayMetrics().density;
        inactive = (int) (density * 8);
        active = (int) (density * 6);
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName("Screen: Community Screen"));
        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen: Community Screen");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);
        if (sp.getBoolean("isConnected", false)) {
            getUserInformation();
            if(sp.getString("phone", "0700000000").equals("0700000000") && !LeroyApplication.shownDialog){
                LeroyApplication.shownDialog = true;
                long timestamp = Long.parseLong(sp.getString("joindate", "0"));
                long actualTimestamp = System.currentTimeMillis();
                long msDiff = actualTimestamp - timestamp;
                long days = TimeUnit.MILLISECONDS.toDays(msDiff);
                if(days>=10 ){
                    //insert dialog here
                    final boolean[] errors = new boolean[4];
                Arrays.fill(errors, false);
                    final Dialog dialog = new Dialog(context);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.edit_account_dialog);
                WindowManager.LayoutParams lp = new WindowManager.LayoutParams();

                Window window = dialog.getWindow();
                lp.copyFrom(window.getAttributes());
//This makes the dialog take up the full width
                lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                window.setAttributes(lp);



                    dialog.setCancelable(true);



                    android.widget.Button save = (android.widget.Button) dialog.findViewById(R.id.save);
                final EditText lastName = (EditText) dialog.findViewById(R.id.last_name);
                final EditText firstName = (EditText) dialog.findViewById(R.id.first_name);
                final EditText city = (EditText) dialog.findViewById(R.id.city);
                final EditText phone = (EditText) dialog.findViewById(R.id.phone);
                final Spinner county = (Spinner) dialog.findViewById(R.id.county);
                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.judete, R.layout.simple_spinner_item);
                adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);

                county.setAdapter(adapter);

                    //phone.setText(sp.getString("phone", ""));

                    firstName.setText(sp.getString("firstname", ""));
                    lastName.setText(sp.getString("lastname", ""));

                   // city.setText(sp.getString("address", ""));
                    int position = Arrays.asList((getResources().getStringArray(R.array.judete))).indexOf(sp.getString("city", ""));
                    county.setSelection(position);
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
                            phone.setText("");
                        }
                        if (hasFocus)
                            phone.setSelection(phone.length());
                    }
                });




                city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                    @Override
                    public void onFocusChange(View v, boolean hasFocus) {
                        if (errors[3] && hasFocus) {
                            errors[3] = false;
                            city.setText("");
                        }
                        if (hasFocus)
                            city.setSelection(city.length());
                    }
                });
                    save.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            boolean ok = true;
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
                            Matcher matcher = pattern.matcher(phone.getText().toString());
                            if (!matcher.matches()) {
                                phone.setText(Html.fromHtml(phoneError));
                                ok = false;
                                errors[2] = true;
                            } else if (phone.getText().toString().equals("0700000000")) {
                                phone.setText(Html.fromHtml(secondPhoneError));
                                ok = false;
                                errors[2] = true;
                            }


                            if (city.getText().toString().equals("")) {
                                city.setText(Html.fromHtml(cityError));
                                ok = false;
                                errors[3] = true;

                            }

                           /* lastName.clearFocus();
                            firstName.clearFocus();
                            city.clearFocus();
                            phone.clearFocus();*/

                            for (int i = 0; i < 4; i++)
                                if (errors[i]) {
                                    dialog.findViewById(R.id.focus_thief).requestFocus();
                                    return;
                                }
                            if (ok) {
                                if (Connections.isNetworkConnected(context)) {
                                    editProfileInformation(firstName.getText().toString(), lastName.getText().toString(), phone.getText().toString(), county.getSelectedItemPosition() + 1, city.getText().toString());
                                    getUserInformation();
                                    Toast.makeText(context, "Modificările au fost realizate cu succes!", Toast.LENGTH_LONG).show();
                                    dialog.dismiss();
                                } else {
                                    new NotificationDialog(context, "Vă rugăm să vă conectați la internet pentru a vă putea salva modificările!").show();
                                }
                            }


                        }
                    }  );



                    dialog.show();
               }
            }
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getChildAt(0).setVisibility(View.GONE);
        toolbar.getChildAt(1).setVisibility(View.VISIBLE);
        ((TextView) toolbar.getChildAt(2)).setText("Facem-Facem");

        LinearLayout home_button = (LinearLayout) findViewById(R.id.home_button);
        //    prevLayout = home_button;
        setOnClickAction(home_button);
        LinearLayout projects_button = (LinearLayout) findViewById(R.id.projects_button);
        setOnClickAction(projects_button);
        LinearLayout shop_button = (LinearLayout) findViewById(R.id.shop_button);
        setOnClickAction(shop_button);
        LinearLayout discussions_button = (LinearLayout) findViewById(R.id.discussions_button);
        setOnClickAction(discussions_button);
        LinearLayout contests_button = (LinearLayout) findViewById(R.id.contests_button);
        setOnClickAction(contests_button);
        prevLayout = projects_button;
        home_button.callOnClick();
        //    prevLayout.callOnClick();
    }

    private void editProfileInformation(String firstName, String lastName, String phone, int county, String city) {
        String link = "http://facem-facem.ro/api.php";
        String parameters = "";
        String response="";
        parameters = "api_m=" + "profile_updateprofile" +
                "&userfield[field5]=" + firstName +
                "&userfield[field6]=" + lastName +
                "&userfield[field10]=" + phone +
                "&userfield[field12]=" + county+
                "&userfield[field13]=" + city +
                "&api_c=" + sp.getString("apiclientid", "") +
                "&api_s=" + sp.getString("apiaccesstoken", "") +
                "&api_v=" + sp.getString("apiversion", "") +
                "&api_sig=" + sp.getString("signature", "");
        try {
            Log.e("link", link);
            Log.e("PARAMETERS", parameters);
            response = new WebServiceConnector().execute(link, parameters).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        Log.e("response", response);
    }
    void setOnClickAction(final LinearLayout linearLayout) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!linearLayout.getTag().toString().equals(prevLayout.getTag().toString())) {
                    if (!linearLayout.getTag().toString().equals("shop"))
                        resolvePrevBtn(linearLayout);
                    switch (linearLayout.getTag().toString()) {
                        case "home":
                            goToFragment(new CommunityHome(), "home");
                            ((ImageView) linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.home_green));
                            ((TextView) linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            ((TextView) linearLayout.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            linearLayout.setPadding(0, active, 0, inactive);
                            break;
                        case "projects":
                            goToFragment(new ProjectsListFragment(), "projects");
                            ((ImageView) linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.projects_green));
                            ((TextView) linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            ((TextView) linearLayout.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            linearLayout.setPadding(0, active, 0, inactive);
                            break;
                        case "discussions":
                            goToFragment(new DiscussionsFragment(), "discussions");
                            ((ImageView) linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.discussions_green));
                            ((TextView) linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            ((TextView) linearLayout.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            linearLayout.setPadding(0, active, 0, inactive);
                            break;
                        case "contests":
                            goToFragment(new ContestFragment(), "contests");
                            ((ImageView) linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.contests_green));
                            ((TextView) linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                            ((TextView) linearLayout.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                            linearLayout.setPadding(0, active, 0, inactive);
                            break;
                        case "shop":
                            Intent intent = new Intent(CommunityDashboard.this, ShopDashboard.class);
                            startActivity(intent);

                            break;
                    }
                }
            }
        });

    }

    void resolvePrevBtn(LinearLayout linearLayout) {
        if (prevLayout != null) {
            switch (prevLayout.getTag().toString()) {
                case "home":
                    ((ImageView) prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.home_gray));
                    break;
                case "projects":
                    ((ImageView) prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.projects_gray));
                    break;
                case "discussions":
                    ((ImageView) prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.discussions_gray));
                    break;
                case "contests":
                    ((ImageView) prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.contests_gray));
                    break;
            }

            ((TextView) prevLayout.getChildAt(1)).setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
            ((TextView) prevLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.unpressedblack));
            prevLayout.setPadding(0, inactive, 0, inactive);
        }

        prevLayout = linearLayout;
    }

    public void logoutClicked(View v) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setMessage("Doriţi să ieşiţi din cont?");
        builder.setNegativeButton("Anulare", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                spEditor = sp.edit();
                spEditor.clear();
                spEditor.commit();

                dialog.dismiss();
                Intent intent = new Intent(CommunityDashboard.this, LoginActivity.class);
                intent.putExtra("source", "shop_dashboard");
                startActivity(intent);
                finish();
            }
        });
        builder.show();
    }

    public void goToFragment(Fragment fragment, String tag) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            // ft.replace(R.id.content_frame_community, fragment).addToBackStack(tag);
            while (getSupportFragmentManager().getBackStackEntryCount() > 0)
                getSupportFragmentManager().popBackStackImmediate();
            ft.replace(R.id.content_frame_community, fragment);
            ft.commit();
        }
    }

    //ia de pe server informatiile despre utilizator
    private void getUserInformation() {
        try {

            JSONObject response = ((LeroyApplication) getApplication()).makeRequest("user_get", sp.getString("endpointCookie", ""), sp.getString("userid", ""));
            //Log.e("response", response.toString());
            response = response.getJSONObject("result");

            spEditor = sp.edit();
            spEditor.putString("joindate", response.getString("joindate"));
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
            spEditor.putString("joindate2", getDate(Long.parseLong(response.getString("joindate") + "000")));
            spEditor.putString("profilevisits", response.getString("profilevisits"));
            spEditor.putString("blognum", response.getString("blognum"));
            if(response.getString("phone").equals("0700000000") && sp.getLong("timestamp", -1)==-1){
                long timestamp = System.currentTimeMillis() ;
                Log.e("timestamp", String.valueOf(timestamp));
               spEditor.putLong("timestamp", timestamp);
            }
            spEditor.commit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // transforma timpul din milisecunde in data calendaristica
    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd.MM.yyyy", cal).toString();
        return date;
    }

    //metoda apelata din fragmentele ce contin webview pentru a sti care dintre webview-uri este afisat pe activitatea curenta
    public void setCurrentWebview(WebView webview) {
        currentWebview = webview;
        htmlStack = new Stack<>();
    }

    // intoarce stiva ce contine paginile html pe care le-a parcurs userul
    public Stack<String> getHtmlStack() {
        return htmlStack;
    }

    //intoarce cookieurile site-ului
    public Map<String, String> getCookies() {
        Map<String, String> cookies;
        cookies = MapUtil.stringToMap(sp.getString("cookies", ""));
        return cookies;
    }

    @Override
    public void onBackPressed() {
        if (!goBack()) {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();

            } else super.onBackPressed();

        }

    }

    //controleaza apasarea butoanelor de back
    private boolean goBack() {
        if (currentWebview != null) {
            if (currentWebview.canGoBack()) {
                currentWebview.goBack();
                return true;
            }
        }
        return false;
    }

}
