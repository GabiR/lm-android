package com.cypien.leroy.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cypien.leroy.R;
import com.cypien.leroy.fragments.CatalogFragment;
import com.cypien.leroy.fragments.ServicesFragment;
import com.cypien.leroy.fragments.ShopHomeFragment;
import com.cypien.leroy.fragments.StoresFragment;
import com.cypien.leroy.models.Service;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Created by GabiRotaru on 31/07/16.
 */
public class ShopDashboard extends AppCompatActivity  {

    LinearLayout home_button, catalog_button, shop_button, services_button, community_button;
    LinearLayout prevLayout;
    Context context;
    private WebView currentWebview;
    private Stack<String> htmlStack;

    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = ShopDashboard.this;
        setContentView(R.layout.shop_dashboard);

        sp = getSharedPreferences("com.cypien.leroy_preferences", MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar.getChildAt(0).setVisibility(View.GONE);
        toolbar.getChildAt(1).setVisibility(View.VISIBLE);
        ((TextView) toolbar.getChildAt(2)).setText("Leroy Merlin România");

        home_button = (LinearLayout) findViewById(R.id.home_button);
        prevLayout = home_button;
        setOnClickAction(home_button);
        catalog_button = (LinearLayout) findViewById(R.id.catalog_button);
        setOnClickAction(catalog_button);
        shop_button = (LinearLayout) findViewById(R.id.shop_button);
        setOnClickAction(shop_button);
        services_button = (LinearLayout) findViewById(R.id.services_button);
        setOnClickAction(services_button);
        community_button = (LinearLayout) findViewById(R.id.community_button);
        setOnClickAction(community_button);

        prevLayout.callOnClick();


    }

    void setOnClickAction(final LinearLayout linearLayout) {
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(! linearLayout.getTag().toString().equals("community")) resolvePrevBtn(linearLayout);
                switch(linearLayout.getTag().toString()) {
                    case "home":
                        //TODO
                        ((ImageView)linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.home_green));
                        ((TextView)linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        goToFragment(new ShopHomeFragment(), "home");
                        break;
                    case "catalog":
                        goToFragment(new CatalogFragment(), "catalog");
                        ((ImageView)linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.catalog_green));
                        ((TextView)linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        break;
                    case "shops":
                        goToFragment(new StoresFragment(), "shops");
                        ((ImageView)linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shops_green));
                        ((TextView)linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        break;
                    case "services":
                        goToFragment(new ServicesFragment(), "services");
                        ((ImageView)linearLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.services_green));
                        ((TextView)linearLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                        break;
                    case "community":
                        Intent intent;
                        if(!sp.getBoolean("isConnected", false)) {
                            intent = new Intent(ShopDashboard.this, LoginActivity.class);
                            intent.putExtra("source", "shop_dashboard");
                        } else {
                            intent = new Intent(ShopDashboard.this, CommunityDashboard.class);
                        }
                        startActivity(intent);
                        break;
                }
            }
        });

    }

    void resolvePrevBtn(LinearLayout linearLayout) {
        if(prevLayout!= null) {
            switch (prevLayout.getTag().toString()) {
                case "home":
                    ((ImageView)prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.home_gray));
                    break;
                case "catalog":
                    ((ImageView)prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.catalog_gray));
                    break;
                case "shops":
                    ((ImageView)prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.shops_gray));
                    break;
                case "services":
                    ((ImageView)prevLayout.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, R.drawable.services_gray));
                    break;
            }

            ((TextView)prevLayout.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.black));
        }

        prevLayout = linearLayout;
    }

    public void goToFragment (Fragment fragment, String tag) {
        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

            ft.replace(R.id.content_frame, fragment).addToBackStack(tag);
            ft.commit();
        }
    }

    public static ArrayList<Service> getServices(){
        ArrayList<Service> services = new ArrayList<>();

        services.add(new Service("Modalități de plată",R.drawable.plata, R.drawable.modalitati_plata_icon));
        services.add(new Service("Rambursăm de 2 x diferența",R.drawable.ramburs, R.drawable.rambursam_diferenta_icon));
        services.add(new Service("Pauză de cafea",R.drawable.cafea, R.drawable.pauza_cafea_icon, "Daca vrei sa te odihnesti intre cumparaturi sau sa citesti o carte de bricolaj te asteptam in zona Cafenelei unde poti savura o cafea calda si un sandvis delicios."));
        services.add(new Service("Livrare",R.drawable.livrare, R.drawable.livrare_icon));
        services.add(new Service("Card client de identificare",R.drawable.card, R.drawable.card_client_icon));
        services.add(new Service("Finanțare",R.drawable.finantare, R.drawable.finantare_icon, "Atunci cand ai nevoie de mai multi bani pentru achizitionarea de <strong>produse de bricolaj, </strong>noi,<strong> LEROY MERLIN,&nbsp;</strong>iti oferim servicii de finantare extrem de eficiente.&nbsp;Prin serviciile noastre de finantare poti beneficia de:<br />- rate fixe pe toata durata de creditare, <br />- costuri avantajoase pentru orice achizitie,<br />- perioade flexibile de creditare (intre 6 si 60 de luni).<br /><br /><div><strong>Care sunt documentele necesare pentru a putea lua un credit ?</strong></div><div></div><div>Actele necesare accesarii unei finantari / documentele de credit solicitate sunt:&nbsp;</div>&nbsp;- adeverinta venit / talon pensie ( + decizie de pensionare ) &nbsp;<br />&nbsp;- Carte de Identitate ( in original )&nbsp;<br />&nbsp;- factura de utilitati de la adresa din Cartea de Identitate &ndash; in original&nbsp;<br />&nbsp;- devizul cu valoarea produselor achizitionate.<br /><br /><div>Va rugam sa aduceti aceste documente in magazin la biroul de credit.&nbsp;Raspunsul la cererea de creditare se acorda in aceeasi zi, de luni pana vineri in intervalul orar 9-17&nbsp;<br />Nota: Este posibil ca ofiterul de credit sa solicite si alte informatii/documente.<br /><br />Pentru un credit in valoare de 1000 lei pe o perioada de 12 luni (dobanda fixa aplicata la sold 18% si DAE 19.48%) rata lunara fixa va fi 92 de lei, iar valoarea totala platibila 1.104 lei. Oferta se aplica pentru cumparaturi de minim 300 lei.<br /><br />Numere de telefoane ale serviciilor de finantare din magazinele noastre:"));
        services.add(new Service("Lumea micilor bricoleuri",R.drawable.bricoleuri, R.drawable.bricoleuri_icon,"Locul de joaca pentru copii este valabil doar pentru magazinele &nbsp;LEROY MERLIN Colosseum si Alexandriei. Sub supravegherea colegilor nostri, copiii tai se pot juca si bricola in voie intr-un spatiu vesel dedicat lor.<strong> Locul de joaca pentru copii este un serviciu gratuit.</strong><br /><br /><strong>Locul de joaca este disponibil in magazinele LEROY MERLIN Colosseum si LEROY MERLIN Alexandriei. Programul locului de joaca este disponibil in magazine.</strong>"));
        services.add(new Service("Închiriere unelte",R.drawable.inchiriere, R.drawable.inchirieri_unelte_icon, "Atunci cand ai nevoie de unelte pentru realizarea lucrarilor tale, noi te ajutam cu cea mai buna oferta pentru inchiriat unelte: nu solicitam garantie si iti oferim preturi mici la inchiriere.<br/><br/>Pentru mai multe detalii despre inchirierea uneltelor te rugam sa ne suni la:"));
        services.add(new Service("Debitare lemn",R.drawable.debitare, R.drawable.debitare_lemn_icon, "Daca ai de taiat la dimensiune anumite produse din magazinele noastre te ajutam cu serviciul debitare lemn."));
        services.add(new Service("Confecționare chei",R.drawable.chei, R.drawable.chei_icon, "Daca ai de realizat chei te asteptam cu serviciul nostru dedicat de Confectionare chei. Serviciul este disponibil doar la magazinul LEROY MERLIN Colosseum, Alexandriei si Ploiesti la chioscul de inchirieri unelte."));
        services.add(new Service("Mixare vopsea",R.drawable.vopsea, R.drawable.vopsea_icon, "Pentru ca tu sa te bucuri de culorile pe care ti le doresti, te ajutam cu mixarea vopselelor."));
        services.add(new Service("Retur marfă", R.drawable.retur, R.drawable.retur_icon, "Rapiditatea si conditiile flexibile de executie cu care se realizeaza returul fac din acesta un serviciu unic.<br /><br /> Puteti cumpara linistiti mai multe produse sau va puteti razgandi, noi acceptam produsele la retur in 90 de zile de la cumparare, insotite de bonul de casa. Cand veniti sa returnati produsele va rugam ca acestea sa fie in stare perfecta (nefolosite) si in ambalajul original. Mai multe detalii si in magazinele noastre."));
        services.add(new Service("Serviciul post-vânzare", R.drawable.post, R.drawable.post_vanzare_icon));

        return services;
    }

    //metoda apelata din fragmentele ce contin webview pentru a sti care dintre webview-uri este afisat pe activitatea curenta
    public void setCurrentWebview(WebView webview){
        currentWebview = webview;
        htmlStack = new Stack<>();
    }


    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount()>1) {
            getSupportFragmentManager().popBackStack();
            if(getSupportFragmentManager().getBackStackEntryCount()-2>=0) {
                String name = getSupportFragmentManager().getBackStackEntryAt(getSupportFragmentManager().getBackStackEntryCount() - 2).getName();
                LinearLayout button;
                if(name!=null) {
                    switch (name) {

                        case "catalog":
                            button = catalog_button;
                            break;
                        case "shops":
                            button = shop_button;
                            break;
                        case "services":
                            button = services_button;
                            break;
                        default:
                            button = home_button;

                    }
                    resolvePrevBtn(button);
                    ((ImageView) button.getChildAt(0)).setImageDrawable(ContextCompat.getDrawable(context, getResources().getIdentifier(name + "_green", "drawable", getPackageName())));
                    ((TextView) button.getChildAt(1)).setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));

                    Log.e("bla", name);
                }
            }
        }
        else {
            if(getSupportFragmentManager().getBackStackEntryCount() == 1)
                getSupportFragmentManager().popBackStack();
            super.onBackPressed();
        }

    }

    public void outClicked(View view) {

        Intent intent = new Intent(this, LoginActivity.class);
        intent.putExtra("fromShop", true);
        startActivity(intent);
        finish();
    }
}
