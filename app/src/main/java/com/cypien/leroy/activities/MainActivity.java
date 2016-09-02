package com.cypien.leroy.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.text.format.DateFormat;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.AdvicesFragment;
import com.cypien.leroy.fragments.CatalogViewFragment;
import com.cypien.leroy.fragments.DashboardFragment;
import com.cypien.leroy.fragments.EventsFragment;
import com.cypien.leroy.fragments.FacebookFragment;
import com.cypien.leroy.fragments.InfoStoresFragment;
import com.cypien.leroy.fragments.ServicesFragment;
import com.cypien.leroy.fragments.StoresFragment;
import com.cypien.leroy.fragments.TopUsersFragment;
import com.cypien.leroy.fragments.VoiceFragment;
import com.cypien.leroy.models.Service;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.MapUtil;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;
import java.util.Stack;
import java.util.concurrent.ExecutionException;


/**
 * Created by Alex on 8/3/2015.
 */
public class MainActivity extends FragmentActivity {
    private LinearLayout menuButton;
    private LinearLayout backButton;
    private DrawerLayout drawerLayout;
    private View drawer;
    private Fragment f;
    private WebView currentWebview;
    private Stack<String> htmlStack;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private ArrayList<Service> services;
    private Button facemDashboard,projects,contest,forum,topUsers, events, advice, account,storeDashboard,catalog,map,storeServices,clientVoice,facebookPage;
    private Button login, logout;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);
        if(sp.getBoolean("isConnected",false)){
            getUserInformation();
        }

        ((TextView)findViewById(R.id.user)).setText(sp.getString("username", ""));

        ((TextView)findViewById(R.id.user)).setText(sp.getString("username",""));

        if(sp.getBoolean("isConnected",false)){
            findViewById(R.id.relative).setVisibility(View.VISIBLE);
            findViewById(R.id.login).setVisibility(View.GONE);
        }else {
            findViewById(R.id.login).setVisibility(View.VISIBLE);
            findViewById(R.id.relative).setVisibility(View.GONE);
        }
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_layout);
        drawer=findViewById(R.id.drawer);

        facemDashboard = (Button) findViewById(R.id.facem_dashboard);
        facemDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                f = new DashboardFragment();
                drawerLayout.closeDrawer(drawer);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
            }
        });

        projects = (Button) findViewById(R.id.projects);
        projects.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if ( sp.getBoolean("isConnected", false)) {
                   /* f = new AddProjectActivity();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();*/
                }else
                    new NotificationDialog(MainActivity.this,"Pentru a putea adăuga proiecte, vă rugăm să vă autentificați!").show();
            }
        });

        contest = (Button) findViewById(R.id.contest);
        contest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if ( sp.getBoolean("isConnected", false)) {
               /*     f = new AddContestProjectActivity();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();*/
                }else
                    new NotificationDialog(MainActivity.this,"Pentru a putea adăuga proiecte, vă rugăm să vă autentificați!").show();
            }
        });


        forum = (Button) findViewById(R.id.forum);
        forum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if ( sp.getBoolean("isConnected", false)) {
                 /*    f= new AddDiscussionFragment();
                     getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();*/
                }else
                    new NotificationDialog(MainActivity.this,"Pentru a putea adăuga discuții, vă rugăm să vă autentificați!").show();
            }
        });
        topUsers = (Button) findViewById(R.id.top_users);
        topUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if(Connections.isNetworkConnected(getApplicationContext())){
                    f=new TopUsersFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
                }else {
                    new NotificationDialog(MainActivity.this,"Pentru a vedea topul utilizatorilor, vă rugăm să vă conectați la internet!").show();
                }
            }
        });
        events = (Button) findViewById(R.id.events);
        events.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if(Connections.isNetworkConnected(getApplicationContext())){
                    f=new EventsFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
                }else {
                    new NotificationDialog(MainActivity.this,"Pentru a vedea evenimentele, vă rugăm să vă conectați la internet!").show();
                }
            }
        });
        advice = (Button) findViewById(R.id.advice);
        advice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if(Connections.isNetworkConnected(getApplicationContext())){
                    f=new AdvicesFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
                }else {
                    new NotificationDialog(MainActivity.this,"Pentru a vedea sfatul meseriasului, vă rugăm să vă conectați la internet!").show();
                }
            }
        });
        account = (Button) findViewById(R.id.account);
        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if (sp.getBoolean("isConnected",false)) {
                    Intent intent = new Intent(MainActivity.this,AccountActivity.class);
                    intent.putExtra("source", "existing");
                    startActivity(intent);
                }else
                    new NotificationDialog(MainActivity.this,"Pentru a putea vedea informații despre contul dumneavoastră, vă rugăm să vă autentificați!").show();
            }
        });

        storeDashboard = (Button) findViewById(R.id.store_dashboard);
        storeDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                f = new InfoStoresFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
            }
        });

        catalog = (Button) findViewById(R.id.catalog);
        catalog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if (Connections.isNetworkConnected(getApplicationContext())) {
                    f = new CatalogViewFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
                } else {
                    new NotificationDialog(MainActivity.this, "Pentru a vedea catalogul, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        map = (Button) findViewById(R.id.map);
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                f = new StoresFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
            }
        });

        storeServices = (Button) findViewById(R.id.services);
        storeServices.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                f = new ServicesFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
            }
        });

        clientVoice = (Button) findViewById(R.id.client_voice);
        clientVoice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if (sp.getBoolean("isConnected",false)) {
                    f =new VoiceFragment();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();

                }else
                    new NotificationDialog(MainActivity.this,"Pentru a putea trimite mesaje prin vocea clientului, vă rugăm să vă autentificați!").show();
            }
        });

        facebookPage = (Button) findViewById(R.id.facebook_page);
        facebookPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.closeDrawer(drawer);
                if(Connections.isNetworkConnected(getApplicationContext())){
                    f = new FacebookFragment("https://m.facebook.com/LeroyMerlinRomania");
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).addToBackStack(null).commit();
                }else{
                    new NotificationDialog(MainActivity.this,"Pentru a putea vizita pagina noastră de Facebook, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        backButton= (LinearLayout)findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!goBack())
                    getSupportFragmentManager().popBackStack();
            }
        });

        menuButton=(LinearLayout)findViewById(R.id.menu_button);
        menuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(drawer) == false) {
                    drawerLayout.openDrawer(drawer);
                } else {
                    drawerLayout.closeDrawer(drawer);
                }
            }
        });

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,OldLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        logout = (Button) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spEditor=sp.edit();
                spEditor.clear();
                spEditor.commit();
                Intent intent = new Intent(MainActivity.this,OldLoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        f = new DashboardFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, f).commit();

    }

    @Override
    public void onBackPressed() {
        if(!goBack())
            super.onBackPressed();
    }

    //metoda apelata din fragmentele ce contin webview pentru a sti care dintre webview-uri este afisat pe activitatea curenta
    public void setCurrentWebview(WebView webview){
        currentWebview=webview;
        htmlStack=new Stack<>();
    }

    // intoarce stiva ce contine paginile html pe care le-a parcurs userul
    public Stack<String> getHtmlStack(){
        return htmlStack;
    }

    //intoarce cookieurile site-ului
    public Map<String,String > getCookies(){
        Map<String,String> cookies;
        cookies= MapUtil.stringToMap(sp.getString("cookies", ""));
        return cookies;
    }

    //controleaza apasarea butoanelor de back
    private boolean goBack(){
        if(currentWebview!=null) {
            if (currentWebview.canGoBack()&&htmlStack.size()>0) {
                if(currentWebview.copyBackForwardList().getCurrentIndex()==1){
                    currentWebview.loadDataWithBaseURL(null, htmlStack.pop(), "text/html", "UTF-8", null);
                    currentWebview.clearHistory();
                }else {
                    currentWebview.goBack();
                }
                return true;
            }
            if (htmlStack.size() > 1) {
                htmlStack.pop();
                currentWebview.loadDataWithBaseURL(null, htmlStack.lastElement(), "text/html", "UTF-8", null);
                return true;
            }
        }
        if (drawerLayout.isDrawerOpen(drawer)){
            drawerLayout.closeDrawer(drawer);
            return true;
        }else
            return false;
    }

    //ia de pe server informatiile despre utilizator
    private void getUserInformation(){
        try {
            JSONObject response = ((LeroyApplication)getApplication()). makeRequest("user_get",sp.getString("userid", ""));
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
        }catch (Exception e){
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

    public ArrayList<Service> getServices(){
        if (services==null){
            services=new ArrayList<>();
//            services.add(new Service("Modalități de plată",R.drawable.plata));
//            services.add(new Service("Rambursăm de 2 x diferența",R.drawable.ramburs));
//            services.add(new Service("Pauză de cafea",R.drawable.cafea,"Daca vrei sa te odihnesti intre cumparaturi sau sa citesti o carte de bricolaj te asteptam in zona Cafenelei unde poti savura o cafea calda si un sandvis delicios."));
//            services.add(new Service("Livrare",R.drawable.livrare));
//            services.add(new Service("Card client de identificare",R.drawable.card));
//            services.add(new Service("Finanțare",R.drawable.finantare,"Atunci cand ai nevoie de mai multi bani pentru achizitionarea de <strong>produse de bricolaj, </strong>noi,<strong> LEROY MERLIN,&nbsp;</strong>iti oferim servicii de finantare extrem de eficiente.&nbsp;Prin serviciile noastre de finantare poti beneficia de:<br />- rate fixe pe toata durata de creditare, <br />- costuri avantajoase pentru orice achizitie,<br />- perioade flexibile de creditare (intre 6 si 60 de luni).<br /><br /><div><strong>Care sunt documentele necesare pentru a putea lua un credit ?</strong></div><div></div><div>Actele necesare accesarii unei finantari / documentele de credit solicitate sunt:&nbsp;</div>&nbsp;- adeverinta venit / talon pensie ( + decizie de pensionare ) &nbsp;<br />&nbsp;- Carte de Identitate ( in original )&nbsp;<br />&nbsp;- factura de utilitati de la adresa din Cartea de Identitate &ndash; in original&nbsp;<br />&nbsp;- devizul cu valoarea produselor achizitionate.<br /><br /><div>Va rugam sa aduceti aceste documente in magazin la biroul de credit.&nbsp;Raspunsul la cererea de creditare se acorda in aceeasi zi, de luni pana vineri in intervalul orar 9-17&nbsp;<br />Nota: Este posibil ca ofiterul de credit sa solicite si alte informatii/documente.<br /><br />Pentru un credit in valoare de 1000 lei pe o perioada de 12 luni (dobanda fixa aplicata la sold 18% si DAE 19.48%) rata lunara fixa va fi 92 de lei, iar valoarea totala platibila 1.104 lei. Oferta se aplica pentru cumparaturi de minim 300 lei.<br /><br />Numere de telefoane ale serviciilor de finantare din magazinele noastre:"));
//            services.add(new Service("Lumea micilor bricoleuri",R.drawable.bricoleuri,"Locul de joaca pentru copii este valabil doar pentru magazinele &nbsp;LEROY MERLIN Colosseum si Alexandriei. Sub supravegherea colegilor nostri, copiii tai se pot juca si bricola in voie intr-un spatiu vesel dedicat lor.<strong> Locul de joaca pentru copii este un serviciu gratuit.</strong><br /><br /><strong>Locul de joaca este disponibil in magazinele LEROY MERLIN Colosseum si LEROY MERLIN Alexandriei. Programul locului de joaca este disponibil in magazine.</strong>"));
//            services.add(new Service("Închiriere unelte",R.drawable.inchiriere,"Atunci cand ai nevoie de unelte pentru realizarea lucrarilor tale, noi te ajutam cu cea mai buna oferta pentru inchiriat unelte: nu solicitam garantie si iti oferim preturi mici la inchiriere.<br/><br/>Pentru mai multe detalii despre inchirierea uneltelor te rugam sa ne suni la:"));
//            services.add(new Service("Debitare lemn",R.drawable.debitare,"Daca ai de taiat la dimensiune anumite produse din magazinele noastre te ajutam cu serviciul debitare lemn."));
//            services.add(new Service("Confecționare chei",R.drawable.chei,"Daca ai de realizat chei te asteptam cu serviciul nostru dedicat de Confectionare chei. Serviciul este disponibil doar la magazinul LEROY MERLIN Colosseum la chioscul din fata magazinului."));
//            services.add(new Service("Mixare vopsea",R.drawable.vopsea,"Pentru ca tu sa te bucuri de culorile pe care ti le doresti, te ajutam cu mixarea vopselelor."));
//            services.add(new Service("Retur marfă", R.drawable.retur,"Rapiditatea si conditiile flexibile de executie cu care se realizeaza returul fac din acesta un serviciu unic.<br /><br /> Puteti cumpara linistiti mai multe produse sau va puteti razgandi, noi acceptam produsele la retur in 90 de zile de la cumparare, insotite de bonul de casa. Cand veniti sa returnati produsele va rugam ca acestea sa fie in stare perfecta (nefolosite) si in ambalajul original. Mai multe detalii si in magazinele noastre."));
//            services.add(new Service("Serviciul post-vânzare",R.drawable.post));
        }
        return services;
    }

    public JSONObject makeRequest(String... params){
        JSONArray array = new JSONArray();
        try {
            JSONObject request = new JSONObject();
            request.put("method", params[0]);
            request.put("params",array);
            return new JSONObject(new WebServiceConnector().execute("http://www.leroymerlin.ro/api/privateEndpoint/v1", "q=" + URLEncoder.encode(request.toString())).get());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }



        return null;
    }

}