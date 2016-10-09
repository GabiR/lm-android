package com.cypien.leroy.fragments;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Alex on 8/14/2015.
 */
public class MyAccountFragment extends Fragment{
    private View view;
    private ImageView profilePicture;
    private TextView firstName,lastName,userName,email,phone,birthDate,nrProjects,nrView,nrFriends,nrTotal,nrDaily,signUpDate;
    private Button modifyButton;
    private SharedPreferences sp;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* view = getActivity().getLayoutInflater().inflate(R.layout.my_account_screen, container, false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Contul meu");

        LeroyApplication application = (LeroyApplication) getActivity().getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "MyAccountFragment");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        firstName = (TextView)view.findViewById(R.id.first_name);
        lastName = (TextView)view.findViewById(R.id.last_name);
        userName = (TextView)view.findViewById(R.id.user_name);
        email = (TextView)view.findViewById(R.id.email);
        phone = (TextView)view.findViewById(R.id.phone);
        birthDate = (TextView)view.findViewById(R.id.birth_date);
        nrProjects = (TextView)view.findViewById(R.id.projects_number);
        nrView = (TextView)view.findViewById(R.id.profile_view);
        nrFriends = (TextView)view.findViewById(R.id.friends);
        nrTotal = (TextView)view.findViewById(R.id.total_posts);
        nrDaily = (TextView)view.findViewById(R.id.daily_posts);
        signUpDate = (TextView)view.findViewById(R.id.signup_date);
        profilePicture = (ImageView)view.findViewById(R.id.profile_picture);

        initProfileInformation();

        modifyButton = (Button)view.findViewById(R.id.modify_profile);
        modifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditAccountFragment f = new EditAccountFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, f,"profile").addToBackStack(null);
                transaction.commit();
            }
        });
*/
        return view;
    }

    // descarca informatiile despre utilizator, le adauga in shared preferences si le afiseaza
    private void initProfileInformation(){

            birthDate.setText(sp.getString("birthday", ""));
            firstName.setText(sp.getString("firstname", ""));
            lastName.setText(sp.getString("lastname", ""));
            userName.setText(sp.getString("username",""));
            email.setText(sp.getString("email",""));
            phone.setText(sp.getString("phone", ""));
            nrTotal.setText(sp.getString("posts", ""));
            String daily=sp.getString("dailyposts","");
            if(daily.length()>5)
                nrDaily.setText(daily.substring(0,5));
            else
                nrDaily.setText(daily);
            nrFriends.setText(sp.getString("friendcount",""));
            signUpDate.setText(sp.getString("joindate",""));
            nrView.setText(sp.getString("profilevisits",""));
            nrProjects.setText(sp.getString("blognum",""));
            if(!sp.getString("avatar","").equals(""))
                profilePicture.setImageBitmap(decodeBase64(sp.getString("avatar","")));
    }

    // transforma un string base64 in imagine
    public static Bitmap decodeBase64(String input){
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }
}
