package com.cypien.leroy.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.format.DateFormat;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.utils.Encrypt;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpResponse;

/**
 * Created by Alex on 8/14/2015.
 */
public class EditAccountFragment extends Fragment{
    private EditText firstName,lastName,phone,email,password,confirmPass,address;
    private TextView firstNameError,lastNameError,phoneError,emailError,passwordError;
    private Spinner city;
    private Button changePhoto,saveButton;
    private ImageView photo;
    private String imagePath;
    private ScrollView view;
    private SharedPreferences sp;
    private SharedPreferences.Editor spEditor;
    private boolean ok;
    private final int IMAGE=284;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
       /* view=(ScrollView)getActivity().getLayoutInflater().inflate(R.layout.edit_account_screen,container,false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Editare cont");

        //initializeza campurile si controleaza disparitia erorilor
        firstNameError = (TextView)view.findViewById(R.id.first_name_error);
        firstName=(EditText)view.findViewById(R.id.first_name);
        firstName.setText(sp.getString("firstname", ""));
        editClick(firstName, firstNameError);

        lastNameError = (TextView)view.findViewById(R.id.last_name_error);
        lastName=(EditText)view.findViewById(R.id.last_name);
        lastName.setText(sp.getString("lastname", ""));
        editClick(lastName, lastNameError);

        phoneError = (TextView)view.findViewById(R.id.phone_error);
        phone=(EditText)view.findViewById(R.id.phone);
        phone.setText(sp.getString("phone", ""));
        editClick(phone, phoneError);

        emailError = (TextView)view.findViewById(R.id.email_error);
        email=(EditText)view.findViewById(R.id.email);
        email.setText(sp.getString("email", ""));
        editClick(email, emailError);

        passwordError = (TextView)view.findViewById(R.id.password_error);
        password=(EditText)view.findViewById(R.id.password);
        password.setText(sp.getString("password", ""));
        confirmPass=(EditText)view.findViewById(R.id.confirm_password);
        confirmPass.setText(sp.getString("password", ""));

        address = (EditText) view.findViewById(R.id.localitate);
        address.setText(sp.getString("address",""));
        city = (Spinner) view.findViewById(R.id.judet);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.judete, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        city.setAdapter(adapter);
        int position = Arrays.asList((getResources().getStringArray(R.array.judete))).indexOf(sp.getString("city",""));
        city.setSelection(position);

        password.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    confirmPass.setBackgroundResource(R.drawable.round_corners_black_border);
                    passwordError.setVisibility(View.GONE);
                }
            }
        });

        confirmPass.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    password.setBackgroundResource(R.drawable.round_corners_black_border);
                    passwordError.setVisibility(View.GONE);
                }
            }
        });

        //porneste activitatae de preluare a imaginii
        changePhoto=(Button)view.findViewById(R.id.change_picture);
        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                startActivityForResult(intent, IMAGE);
            }
        });

        photo=(ImageView)view.findViewById(R.id.picture);
        if(!sp.getString("avatar","").equals(""))
            photo.setImageBitmap(decodeBase64(sp.getString("avatar","")));
        if (imagePath!=null){
            Picasso.with(getActivity())
                    .load(new File(imagePath))
                    .fit().centerInside()
                    .into(photo);
        }

        saveButton=(Button)view.findViewById(R.id.save_profile);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Connections.isNetworkConnected(getActivity())){
                    view.findViewById(R.id.focus_thief).requestFocus();
                    ok=true;
                    if (firstName.getText().toString().equals("")) {
                        firstName.setBackgroundResource(R.drawable.round_corners_red_border);
                        firstNameError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (lastName.getText().toString().equals("")) {
                        lastName.setBackgroundResource(R.drawable.round_corners_red_border);
                        lastNameError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (phone.getText().toString().length()!=10) {
                        phone.setBackgroundResource(R.drawable.round_corners_red_border);
                        phoneError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (email.getText().toString().equals("")||!email.getText().toString().contains("@")||!email.getText().toString().contains(".")) {
                        email.setBackgroundResource(R.drawable.round_corners_red_border);
                        emailError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (password.getText().toString().equals("") || confirmPass.getText().toString().equals("")) {
                        password.setBackgroundResource(R.drawable.round_corners_red_border);
                        confirmPass.setBackgroundResource(R.drawable.round_corners_red_border);
                        passwordError.setText("VĂ RUGĂM SĂ COMPLETAȚI AMBELE CÂMPURI PENTRU PAROLĂ");
                        passwordError.setVisibility(View.VISIBLE);
                        ok = false;
                    } else if (!password.getText().toString().equals(confirmPass.getText().toString())) {
                        password.setBackgroundResource(R.drawable.round_corners_red_border);
                        confirmPass.setBackgroundResource(R.drawable.round_corners_red_border);
                        passwordError.setText("PAROLELE FURNIZATE NU SUNT LA FEL!");
                        passwordError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if(ok){
                        if(imagePath!=null){
                            uploadPhoto();
                            editProfileInformation();
                        }else {
                            editProfileInformation();
                            getUserInformation();
                            new NotificationDialog(getActivity(),"Modificările au fost realizate cu succes!").show();
                            getFragmentManager().popBackStack();
                        }
                    }
                }else{
                    new NotificationDialog(getActivity(),"Pentru a putea salva modificările, vă rugăm să vă conectați la internet!").show();
                }
            }
        });

        return view;*/
        return view;
    }

    //preia adresa imaginii
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode==getActivity().RESULT_OK){
            view.post(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView) view).fullScroll(ScrollView.FOCUS_DOWN);
                }
            });
            if (requestCode==IMAGE){
                imagePath = data.getStringExtra("path");
                pathIsNull(imagePath);
                Picasso.with(getActivity())
                        .load(new File(imagePath))
                        .fit().centerInside()
                        .into(photo);
            }
        }
    }

    //la focus pe edittext dispare eroarea
    private void editClick(final EditText edit, final TextView error){
        edit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    error.setVisibility(View.GONE);
                }
            }
        });
    }

    // incarca pe server noul avatar
    private void uploadPhoto(){
        RequestParams params = new RequestParams();
        params.put("api_m","profile_updateavatar");
        params.put("api_c",sp.getString("apiclientid", ""));
        params.put("api_s",sp.getString("apiaccesstoken", ""));
        params.put("api_v",sp.getString("apiversion", ""));
        params.put("api_sig", sp.getString("signature", ""));
        try {
            params.put("upload", new File(imagePath));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.post("http://facem-facem.ro/api.php", params, new AsyncHttpResponseHandler() {
            @Override
            public void onPostProcessResponse(ResponseHandlerInterface instance, HttpResponse response) {
                super.onPostProcessResponse(instance, response);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                getUserInformation();
                new NotificationDialog(getActivity(), "Modificările au fost realizate cu succes!").show();
                getFragmentManager().popBackStack();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    //modifica pe server datele utilizatorului
    private void editProfileInformation(){
        String link="http://facem-facem.ro/api.php";
        String parameters="";
        if(!(password.getText().toString()).equals(sp.getString("password",""))){
            parameters = "api_m=" + "profile_updatepassword" +
                    "&currentpassword_md5="+ Encrypt.getMD5UTFEncryptedPass(sp.getString("password", ""))+
                    "&email="+email.getText().toString()+
                    "&emailconfirm="+email.getText().toString()+
                    "&newpassword_md5="+Encrypt.getMD5UTFEncryptedPass(password.getText().toString())+
                    "&newpasswordconfirm_md5="+Encrypt.getMD5UTFEncryptedPass(confirmPass.getText().toString())+
                    "&api_c=" + sp.getString("apiclientid","") +
                    "&api_s=" + sp.getString("apiaccesstoken","") +
                    "&api_v=" + sp.getString("apiversion","") +
                    "&api_sig=" + sp.getString("signature","");
            try {
                new WebServiceConnector().execute(link, parameters).get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        parameters = "api_m=" + "profile_updateprofile" +
                "&userfield[field5]=" + firstName.getText().toString() +
                "&userfield[field6]=" + lastName.getText().toString() +
                "&userfield[field10]=" + phone.getText().toString() +
                "&userfield[field12]=" + (city.getSelectedItemPosition()+1)+
                "&userfield[field13]=" + address.getText().toString() +
                "&api_c=" + sp.getString("apiclientid","") +
                "&api_s=" + sp.getString("apiaccesstoken","") +
                "&api_v=" + sp.getString("apiversion","") +
                "&api_sig=" + sp.getString("signature","");
        try {
            new WebServiceConnector().execute(link, parameters).get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // transforma un string base64 in imagine
    private Bitmap decodeBase64(String input){
        byte[] decodedByte = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    //ia de pe server informatiile despre utilizator
    private void getUserInformation(){
        try {
            JSONObject response = LeroyApplication.getInstance().makeRequest("user_get", sp.getString("endpointCookie", ""), sp.getString("userid", ""));
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

    private void pathIsNull(String path){
        if(path==null)
            new NotificationDialog(getActivity(),"Ne pare rau, dar imaginea pe care ati selectat-o nu se gaseste stocata pe dispozivul dumneavoastra!").show();
    }


}
