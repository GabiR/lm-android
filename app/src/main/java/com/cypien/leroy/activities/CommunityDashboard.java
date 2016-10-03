package com.cypien.leroy.activities;

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
import android.text.format.DateFormat;
import android.util.TypedValue;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.CommunityHome;
import com.cypien.leroy.fragments.ContestFragment;
import com.cypien.leroy.fragments.DiscussionsFragment;
import com.cypien.leroy.fragments.ProjectsListFragment;
import com.cypien.leroy.utils.MapUtil;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;

/**
 * Created by GabiRotaru on 24/08/16.
 */
public class CommunityDashboard extends AppCompatActivity {
    LinearLayout home_button, projects_button, discussions_button, contests_button, shop_button;
    LinearLayout prevLayout;
    Context context;

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
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getChildAt(0).setVisibility(View.GONE);
        toolbar.getChildAt(1).setVisibility(View.VISIBLE);
        ((TextView) toolbar.getChildAt(2)).setText("Facem-Facem");

        home_button = (LinearLayout) findViewById(R.id.home_button);
        //    prevLayout = home_button;
        setOnClickAction(home_button);
        projects_button = (LinearLayout) findViewById(R.id.projects_button);
        setOnClickAction(projects_button);
        shop_button = (LinearLayout) findViewById(R.id.shop_button);
        setOnClickAction(shop_button);
        discussions_button = (LinearLayout) findViewById(R.id.discussions_button);
        setOnClickAction(discussions_button);
        contests_button = (LinearLayout) findViewById(R.id.contests_button);
        setOnClickAction(contests_button);
        prevLayout = projects_button;
        home_button.callOnClick();
        //    prevLayout.callOnClick();
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
