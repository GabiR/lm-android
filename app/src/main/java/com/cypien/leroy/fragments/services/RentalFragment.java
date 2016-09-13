package com.cypien.leroy.fragments.services;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Service;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

/**
 * Created by alexa on 10/1/2015.
 */
public class RentalFragment extends Fragment{
    private View view;
    private int phoneNumbers[]={R.id.phone1,R.id.phone2,R.id.phone3,R.id.phone4,R.id.phone5,R.id.phone6,
                                R.id.phone7,R.id.phone8,R.id.phone9,R.id.phone10,R.id.phone11, R.id.phone12,
                                R.id.phone13, R.id.phone14};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.rental_service_screen,container,false);

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "ToolsRentalFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        Bundle bundle = getArguments();
        Service service = (Service)bundle.getSerializable("service");

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText(service.getName());

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });
        TextView details = (TextView) view.findViewById(R.id.details);
        details.setText(Html.fromHtml(" <p>Atunci cand ai nevoie de unelte pentru realizarea lucrarilor tale, noi te ajutam cu cea mai buna oferta pentru inchiriat unelte: nu solicitam garantie si iti oferim preturi mici la inchiriere.<br />\n" +
                "<br />\n" +
                "<br />\n" +
                "<br />\n" +
                "Pentru mai multe detalii despre inchirierea uneltelor te rugam sa ne suni la:</p>\n" +
                "\n" +

                "&#9679; <strong>0799.728.010 pentru LEROY MERLIN Sun Plaza</strong> &nbsp;(program inchirieri Luni-Sambata 08:00 &ndash; 21:00, Duminica 09:00 &ndash; 20:00).<br/>" +
                "&#9679; <strong>0731.917.320 pentru LEROY MERLIN Colosseum</strong> (program inchirieri Luni-Sambata 08:00 &ndash; 21:00, Duminica 09:00 &ndash; 20:00).<br/>" +
                "&#9679; <strong>0799.728.011 pentru LEROY MERLIN Ploiesti&nbsp;</strong>(program inchirieri Luni-Sambata 08:00 &ndash; 21:00, Duminica 09:00 &ndash; 20:00).<br/>" +
                "&#9679; <strong>0371 350 710 pentru LEROY MERLIN Alexandriei&nbsp;</strong>(program inchirieri Luni-Sambata 08:00 &ndash; 21:00, Duminica 09:00 &ndash; 20:00).<br/>" +
                "&#9679; <strong>0762.941.611 pentru LEROY MERLIN Craiova&nbsp;</strong>(program inchirieri Luni-Vineri: 08.00-20.00/ Sambata:08:00 - 17:00/ Duminica 09.00-15.00).<br/>" +
                "&#9679; <strong>0268.440.189,&nbsp;</strong> <strong>&nbsp;pentru&nbsp;LEROY MERLIN&nbsp;Brasov&nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storebv@smartrent.ro\">smartrent_storebv@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0364.805.845</strong>&nbsp; <strong> pentru LEROY MERLIN&nbsp;Cluj Turzii&nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storecj@smartrent.ro\">smartrent_storecj@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>\u200B0369.429.10</strong> <strong> pentru LEROY MERLIN&nbsp;Sibiu&nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storesb@smartrent.ro\">smartrent_storesb@smartrent.ro</a><br/>" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0241.838.740</strong> <strong>&nbsp;pentru LEROY MERLIN Constanta&nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storect@smartrent.ro\">smartrent_storect@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0364.410.006</strong> <strong> pentru LEROY MERLIN Cluj Vlaicu&nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storecj2@smartrent.ro\">smartrent_storecj2@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0365.455.047</strong> <strong> pentru LEROY MERLIN Targu Mures&nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storetgm@smartrent.ro\">smartrent_storetgm@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0330.803.510&nbsp;pentru LEROY MERLIN Suceava &nbsp;&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storesv@smartrent.ro\">smartrent_storesv@smartrent.ro</a><br/>" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0234.701.707 pentru LEROY MERLIN Bacau &nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storebc@smartrent.ro\">smartrent_storebc@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>" +
                "&#9679; <strong>0332.434.091 pentru LEROY MERLIN Iasi</strong> <strong>&nbsp;</strong>(program inchirieri Luni-Sambata: 08.00-20.00/ Duminica 10.00-18.00).<br />" +
                "Adresa e-mail: <a href=\"mailto:smartrent_storeis@smartrent.ro\">smartrent_storeis@smartrent.ro</a><br />" +
                "Responsabil centru de inchiriere unelte:&nbsp;<strong>0755.072.853</strong><br/>"));
        details.setMovementMethod(LinkMovementMethod.getInstance());
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

       /* for (int phoneNumber : phoneNumbers) {
            view.findViewById(phoneNumber).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Connections.isTelephonyEnabled(getActivity()))
                        makePhoneCall(((TextView) v).getText().toString());
                    else
                        new NotificationDialog(getActivity(), "Ne pare rau dar nu puteți efectua apeluri de pe acest dispozitiv!").show();
                }
            });
        }*/

        return view;
    }

    private void makePhoneCall(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }
}
