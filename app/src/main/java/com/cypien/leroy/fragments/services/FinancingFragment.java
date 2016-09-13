package com.cypien.leroy.fragments.services;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.fragments.ViewPageFragment;
import com.cypien.leroy.models.Service;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by alexa on 10/1/2015.
 */
public class FinancingFragment extends Fragment{
    private View view;
    private TextView financingDetails;
    private int phoneNumbers[]={R.id.phone1,R.id.phone2,R.id.phone3,R.id.phone4,R.id.phone5,R.id.phone6,
                            R.id.phone7,R.id.phone8,R.id.phone9,R.id.phone10,R.id.phone11, R.id.phone12,
                            R.id.phone13, R.id.phone14, R.id.phone15};
    private TextView financingDetails2, simulator;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.financing_service_screen,container,false);

        Bundle bundle = getArguments();
        Service service = (Service)bundle.getSerializable("service");

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "FinancingServiceFragment");
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

        simulator = (TextView) view.findViewById(R.id.simulator);

        simulator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPageFragment f = new ViewPageFragment();
                Bundle bundle = new Bundle();
                bundle.putString("title", "Finanțare");
                bundle.putString("url", "http://www.leroymerlin.ro/finantare");
                f.setArguments(bundle);
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, f).addToBackStack(null);
                transaction.commit();
            }
        });

        financingDetails = (TextView) view.findViewById(R.id.financing_details);
        financingDetails.setText(Html.fromHtml("Atunci cand ai nevoie de mai multi bani pentru achizitionarea de <strong>produse de bricolaj, </strong>noi,<strong> LEROY MERLIN,&nbsp;</strong>iti oferim servicii de finantare extrem de eficiente.&nbsp;Prin serviciile noastre de finantare poti beneficia de:<br />\n" +
                "- rate fixe pe toata durata de creditare,<br />\n" +
                "- costuri avantajoase pentru orice achizitie,<br />\n" +
                "- perioade flexibile de creditare (intre 6 si 60 de luni)."));
        financingDetails2 = (TextView) view.findViewById(R.id.financing_details2);
        financingDetails2.setText(Html.fromHtml("<strong>Care sunt documentele necesare pentru a putea lua un credit ?</strong><br/><br/>Actele necesare accesarii unei finantari / documentele de credit solicitate sunt:<p><strong>Pentru salariati</strong> :</p>\n" +
                "&#9679; Adeverinta de venit ( sa nu fie &nbsp;mai veche de 30 de zile )<br/>" +
                "&#9679; Carte de identitate&nbsp;<br/>" +
                "&#9679; Factura de utilitati ( sa nu fie mai veche de 90 de zile )<br/>" +
                "<br/>" +
                "<p><strong>Pentru pensionari</strong> :</p>\n" +

                "&#9679; Talon de pensie&nbsp;<br/>" +
                "&#9679; Decizie de pensionare&nbsp;<br/>" +
                "&#9679; Carte de identitate&nbsp;<br/>" +
                "&#9679; Factura de utilitati ( sa nu fie mai veche de 90 de zile )<br/>" +
                "<p>*<em>toate documentele trebuie sa fie in original</em></p>\n" +
                "\n" +
                "<div>Va rugam sa aduceti aceste documente in magazin la biroul de credit.&nbsp;Raspunsul la cererea de creditare se acorda in aceeasi zi, de luni pana vineri in intervalul orar 9-17&nbsp;<br />\n" +
                "Nota: Este posibil ca ofiterul de credit sa solicite si alte informatii/documente.<br />\n" +
                "<br />\n" +
                "Pentru un credit in valoare de 1000 lei pe o perioada de 12 luni (dobanda fixa aplicata la sold 18% si DAE 19.48%) rata lunara fixa va fi 92 de lei, iar valoarea totala platibila 1.104 lei. Oferta se aplica pentru cumparaturi de minim 300 lei.<br />\n" +
                "<br />" +
                "Numere de telefoane ale serviciilor de finantare din magazinele noastre:"));
        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        view.findViewById(R.id.form).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveFile();
            }
        });

        for (int phoneNumber : phoneNumbers) {
            view.findViewById(phoneNumber).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (Connections.isTelephonyEnabled(getActivity()))
                        makePhoneCall(((TextView) v).getText().toString());
                    else
                        new NotificationDialog(getActivity(), "Ne pare rau dar nu puteți efectua apeluri de pe acest dispozitiv!").show();
                }
            });
        }

        return view;
    }

    private void makePhoneCall(String phone){
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + phone));
        startActivity(intent);
    }

    private void saveFile() {
        AssetManager assetManager = getActivity().getAssets();
        InputStream in = null;
        long length = 0;
        String path="";
        OutputStream out = null;
        try {
            in = assetManager.open("finance.pdf");
            File outFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), "Formular de finantare.pdf");
            out = new FileOutputStream(outFile);
            path = outFile.getAbsolutePath();
            byte[] buffer = new byte[1024];
            int read;
            while((read = in.read(buffer)) != -1){
                Log.e("bla", String.valueOf(read));
                out.write(buffer, 0, read);
                length += read;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
            }

            Log.e("bla", String.valueOf(length));
            DownloadManager manager = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);

            manager.addCompletedDownload("Formular de finantare.pdf", "LeroyMerlin", true, "application/pdf", path,length,true);
            new NotificationDialog(getActivity(),"Formularul de finantare a fost descarcat. Va rugam sa verificati directorul de descarcari .").show();
        }
    }
}
