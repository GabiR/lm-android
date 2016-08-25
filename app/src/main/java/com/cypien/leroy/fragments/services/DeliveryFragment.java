package com.cypien.leroy.fragments.services;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.PdfViewerFragment;
import com.cypien.leroy.models.Service;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by GabiRotaru on 09/03/16.
 */
public class DeliveryFragment extends Fragment {
    private View view;
    TextView first_half, second_half;
    TextView bucharest_cost, cluj_cost, craiova_cost, ploiesti_cost, targu_mures_cost, iasi_cost;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.delivery_service_screen,container,false);

        Bundle bundle = getArguments();
        Service service = (Service)bundle.getSerializable("service");

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "DeliveryServiceFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText(service.getName());

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        first_half = (TextView) view.findViewById(R.id.first_half);
        second_half = (TextView) view.findViewById(R.id.second_half);

        first_half.setText(Html.fromHtml("<font color=\"#000000\">Iti oferim avantajul unui tarif perceput in functie de zonele de livrare, precum si serviciul util de livrare expres, prin care iti livram marfa imediat.<br/><br/> Daca ai nevoie de transport rapid iti oferim serviciile de </font> <font color=\"#339966\"><b>Livrare expres* - livrare in aceeasi zi.</b></font> <font color=\"#000000\">Livrarile expres sunt realizate in intervale orare, contactandu-va telefonic in prealabil. In cazul in care nu sunt masini disponibile, livrarea va avea loc in ziua urmatoare, la acelasi pret.<br/><b>Programul livrarilor:</b><br/>Luni-Sambata: 08.00-20.00<br/>Duminica: 10.00-17.00 <br/><br/></font>"));
        second_half.setText(Html.fromHtml("<br/><br/><font color=\"#339966\"><b>Livrare cu macara* </b></font> <font color=\"#000000\"><br/><b>Livram in 48 de ore**<br/> Livrarea se face cu un camion de 8 tone dotat cu macara. Daca se depaseste tonajul se mai plateste o livrare.<br />Programul livrarilor:<br /><br />Luni-Sambata: 08.00-20.00<br />Duminca: 10.00-17.00<br /><br /> <br/>* Livrarea inseamna transportul marfurilor de la magazin si descarcarea acestora din masina. Serviciul nu este disponibil la magazinul din Brasov.<br />** In limita masinilor disponibile<br />&nbsp;</strong></strong></p><strong><strong>Daca nu te grabesti poti alege serviciul <b>&quot;Livrare cu program prestabilit&quot;</b> ceea ce inseamna ca produsele de bricolaj cumparate vor ajunge la tine in functie de programul stabilit de comun acord cu magazinul.<br /><br /><br />Preturile difera in functie de zona unde doresti livrarea si de tipul serviciului de livrare ales. Pentru detalii complete despre livrare intreaba consultantii de vanzare din magazin sau citeste materialele informative din magazin dedicate serviciului <b>LEROY MERLIN </b>de livrare la domiciliu.</font>"));

        bucharest_cost = (TextView) view.findViewById(R.id.bucharest_cost);
        cluj_cost = (TextView) view.findViewById(R.id.cluj_cost);
        craiova_cost = (TextView) view.findViewById(R.id.craiova_cost);
        ploiesti_cost = (TextView) view.findViewById(R.id.ploiesti_cost);
        targu_mures_cost = (TextView) view.findViewById(R.id.targu_mures_cost);
        iasi_cost = (TextView) view.findViewById(R.id.iasi_cost);

        putListener(bucharest_cost, "bucharest_delivery.pdf");
        putListener(cluj_cost, "cluj_delivery.pdf");
        putListener(craiova_cost,"craiova_delivery.pdf");
        putListener(ploiesti_cost,"ploiesti_delivery.pdf");
        putListener(targu_mures_cost,"targu_mures_delivery.pdf");
        putListener(iasi_cost,"iasi_delivery.pdf");


        return view;
    }

    void putListener(TextView txt, final String assetName) {
        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = new PdfViewerFragment();
                Bundle bundle = new Bundle();
                bundle.putString("filename",assetName);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.content_frame, fragment).addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }
}
