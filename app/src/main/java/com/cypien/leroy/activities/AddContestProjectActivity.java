package com.cypien.leroy.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.models.Contest;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.rey.material.widget.Spinner;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex on 03/11/15.
 */
public class AddContestProjectActivity extends AppCompatActivity{
    private final String titleError = "<font color=\"#D50000\">Completați titlul</font>";
    private final String detailsError = "<font color=\"#D50000\">Prea puține detalii</font>";

    private EditText title, details;

    private ImageView image1, image2, image3;
    private Button addProjectButton;
    private String path1, path2, path3;
    private SharedPreferences sp;
    private Spinner contests;

    //  private static String selectedCategoriesIndexes,selectedCategories;
    private final int IMAGE1=175,IMAGE2=176,IMAGE3=177;

    boolean[] errors = new boolean[2];
    private ArrayAdapter<Contest> adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_contest_project);
        sp = getSharedPreferences("com.cypien.leroy_preferences", Context.MODE_PRIVATE);


        ImageView back_arrow = (ImageView) findViewById(R.id.back_arrow);

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        addProjectButton = (Button) findViewById(R.id.add_project);

        image1 = (ImageView) findViewById(R.id.picture1);
        image1.setTag(IMAGE1);
        getImage(image1);

        image2 = (ImageView) findViewById(R.id.picture2);
        image2.setTag(IMAGE2);
        getImage(image2);

        image3 = (ImageView) findViewById(R.id.picture3);
        image3.setTag(IMAGE3);
        getImage(image3);

        title = (EditText)findViewById(R.id.project_name);

        details = (EditText) findViewById(R.id.project_details);
        contests = (Spinner) findViewById(R.id.contests);

       adapter = new ArrayAdapter<>(AddContestProjectActivity.this, R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        getContests();
        contests.setAdapter(adapter);
   
        // controleaza disparitia erorilor

        placeImages();

        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[0] && hasFocus) {
                    errors[0] = false;
                    title.setText("");
                }
            }
        });
        details.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (errors[1] && hasFocus) {
                    errors[1] = false;

                    details.setText("");
                }
            }
        });
        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(AddContestProjectActivity.this)) {
                    findViewById(R.id.focus_thief).requestFocus();
                    boolean ok = true;
                    details.clearFocus();
                    if (title.getText().toString().equals("")) {
                        title.setText(Html.fromHtml(titleError));
                        errors[0] = true;
                        ok = false;
                    }
                    if (details.getText().toString().length() < 1) {
                        details.setText(Html.fromHtml(detailsError));
                        errors[1] = true;
                        ok = false;
                    }
                    if(errors[0] || errors[1])
                        return;

                    if (ok) {
                        JSONObject jsn = new JSONObject();
                        try {
                            jsn.put("pagetext", details.getText().toString());
                            jsn.put("title", title.getText().toString());
                            jsn.put("userid", sp.getString("userid", ""));
                            jsn.put("parentnode", ((Contest)contests.getSelectedItem()).getContestId());
                            jsn.put("mainimagename", title.getText().toString() + ".jpg");
                            if (path1 != null) {
                                jsn.put("mainimage", encodeImageTobase64(path1));
                                path1 = null;
                            } else if (path2 != null) {
                                jsn.put("mainimage", encodeImageTobase64(path2));
                                path2 = null;
                            } else {
                                jsn.put("mainimage", encodeImageTobase64(path3));
                                path3 = null;
                            }
                            JSONObject images = new JSONObject();
                            if (path1 != null)
                                images.put("image1.jpg", encodeImageTobase64(path1));
                            if (path2 != null)
                                images.put("image2.jpg", encodeImageTobase64(path2));
                            if (path3 != null)
                                images.put("image3.jpg", encodeImageTobase64(path3));
                            jsn.put("images", images);
                            jsn = makeRequest("CMS_create", sp.getString("userid", ""), jsn.toString());
                            if (jsn != null && jsn.getJSONObject("result").getString("userid").equals(sp.getString("userid", ""))) {
                                //   new NotificationDialog(getActivity(), "Proiectul dumneavoastră a fost adăugat cu succes !").show();
                                AlertDialog.Builder builder = new AlertDialog.Builder(AddContestProjectActivity.this, R.style.AppCompatAlertDialogStyle);
                                builder.setMessage(Html.fromHtml("Proiectul dumneavoastră a fost adăugat cu succes!"));

                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        AddContestProjectActivity.this.finish();
                                    }
                                });
                                builder.show();
                                LeroyApplication.getCacheManager().unset("projects_nr");
                                getFragmentManager().popBackStack();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                } else {
                    new NotificationDialog(AddContestProjectActivity.this,"Pentru a putea adăuga proiectul dumneavoastră, vă rugăm să va conectați la internet!").show();
                }
            }
        });

    }

    // porneste activitate de preluare a imaginii
    private void getImage(ImageView image) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // picturesError.setVisibility(View.GONE);
                Intent intent = new Intent(AddContestProjectActivity.this, AddPhotoActivity.class);
                startActivityForResult(intent, (int)v.getTag());
            }
        });
    }

    // afiseaza imaginile existente in imageview-uri
    private void placeImages() {
        if (path1 != null) {
            Picasso.with(AddContestProjectActivity.this)
                    .load(new File(path1))
                    .placeholder(R.drawable.add_picture)
                    .fit().centerInside()
                    .into(image1);
        }
        if (path2 != null) {
            Picasso.with(AddContestProjectActivity.this)
                    .load(new File(path2))
                    .placeholder(R.drawable.add_picture)
                    .fit().centerInside()
                    .into(image2);
        }
        if (path3 != null) {
            Picasso.with(AddContestProjectActivity.this)
                    .load(new File(path3))
                    .placeholder(R.drawable.add_picture)
                    .fit().centerInside()
                    .into(image3);
        }
    }

    //preia adresa imaginii
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== RESULT_OK){
           /* view.post(new Runnable() {
                @Override
                public void run() {
                    ((ScrollView) view).fullScroll(ScrollView.FOCUS_DOWN);
                }
            });*/
            if (requestCode==IMAGE1) {
                path1 = data.getStringExtra("path");
                pathIsNull(path1);
            }
            if (requestCode==IMAGE2) {
                path2 = data.getStringExtra("path");
                pathIsNull(path2);
            }
            if (requestCode==IMAGE3) {
                path3 = data.getStringExtra("path");
                pathIsNull(path3);
            }
            placeImages();
        }
    }

    // transforma o imaginea intr-un string base64 pentru a putea fi transmisa prin http request
    private String encodeImageTobase64(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, 1920, 1920), Matrix.ScaleToFit.CENTER);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        bitmap.recycle();
        return imageEncoded;
    }

    public JSONObject makeRequest(String... params){
        JSONArray array = new JSONArray();
        try {
            array.put(params[1]);
            array.put(new JSONObject(params[2]));
            JSONObject request = new JSONObject();
            request.put("method", params[0]);
            request.put("params",array);
            return new JSONObject(new WebServiceConnector().execute("http://www.facem-facem.ro/customAPI/privateEndpoint/"+LeroyApplication.getInstance().APIVersion, "q=" + URLEncoder.encode(request.toString())).get());

        } catch (JSONException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void getContests(){
        JSONObject response = LeroyApplication.getInstance().makeRequest("CMS_get_contests");
        try {
            JSONArray resultArray = response.getJSONArray("result");
            for(int i=0;i<resultArray.length();i++){
                Contest contest = new Contest();
                contest=contest.fromJson(resultArray.getJSONObject(i));
                if(!contest.getTitle().contains("Castigatorii"))
                    adapter.add(contest);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (adapter.getCount()==0){
            new NotificationDialog(AddContestProjectActivity.this, "Ne pare rău, dar momentan nu există niciun concurs în desfășurare!");
        }

    }

    private void pathIsNull(String path){
        if(path==null)
            new NotificationDialog(AddContestProjectActivity.this,"Ne pare rău, dar imaginea pe care ați selectat-o nu se găsește stocată pe dispozitivul dumneavoastră!").show();
    }
}

  /*  private View view;
    private ImageView image1, image2, image3;
    private Button addProjectButton;
    private String path1, path2, path3;
    private EditText title,details;
    private TextView titleError, detailsError,picturesError;
    private Spinner contests;
    private SharedPreferences sp;
    private ArrayAdapter<Contest> adapter;
    private final int IMAGE1=175,IMAGE2=176,IMAGE3=177;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.add_contest_project, container, false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        View actionBarView = getActivity().findViewById(R.id.actionbar);
        ((TextView) actionBarView.findViewById(R.id.title)).setText("Adaugă în concurs");
        ((ImageView) actionBarView.findViewById(R.id.logo)).setImageResource(R.drawable.logo);
        actionBarView.findViewById(R.id.back_button).setVisibility(View.VISIBLE);

        addProjectButton = (Button) view.findViewById(R.id.add_project);
        contests = (Spinner) view.findViewById(R.id.contests);
        adapter = new ArrayAdapter<>(getActivity(),R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        getContests();
        contests.setAdapter(adapter);

        picturesError = (TextView)view.findViewById(R.id.pictures_error);

        image1 = (ImageView) view.findViewById(R.id.picture1);
        image1.setTag(IMAGE1);
        getImage(image1);

        image2 = (ImageView) view.findViewById(R.id.picture2);
        image2.setTag(IMAGE2);
        getImage(image2);

        image3 = (ImageView) view.findViewById(R.id.picture3);
        image3.setTag(IMAGE3);
        getImage(image3);

        title = (EditText) view.findViewById(R.id.project_name);
        details = (EditText) view.findViewById(R.id.project_details);
        titleError = (TextView) view.findViewById(R.id.title_error);
        detailsError = (TextView) view.findViewById(R.id.details_error);

        // controleaza disparitia erorilor
        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    titleError.setVisibility(View.GONE);
                }
            }
        });

        details.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    v.setBackgroundResource(R.drawable.round_corners_black_border);
                    detailsError.setVisibility(View.GONE);
                }
            }
        });

        placeImages();

        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Connections.isNetworkConnected(getActivity())) {
                    view.findViewById(R.id.focus_thief).requestFocus();
                    boolean ok = true;
                    details.clearFocus();
                    if (title.getText().toString().equals("")) {
                        title.setBackgroundResource(R.drawable.round_corners_red_border);
                        titleError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if (details.getText().toString().length() < 10) {
                        details.setBackgroundResource(R.drawable.round_corners_red_border);
                        detailsError.setVisibility(View.VISIBLE);
                        ok = false;
                    }
                    if(path1==null && path2==null && path3==null){
                        picturesError.setVisibility(View.VISIBLE);
                        ok=false;
                    }
                    if (ok) {
                        JSONObject jsn = new JSONObject();
                        try {
                            jsn.put("pagetext",details.getText().toString());
                            jsn.put("title",title.getText().toString());
                            jsn.put("userid", sp.getString("userid", ""));
                            jsn.put("parentnode", ((Contest)contests.getSelectedItem()).getContestId());
                            jsn.put("mainimagename",title.getText().toString()+".jpg");
                            if(path1!=null){
                                jsn.put("mainimage",encodeImageTobase64(path1));
                                path1=null;
                            }else if(path2!=null) {
                                jsn.put("mainimage", encodeImageTobase64(path2));
                                path2=null;
                            }else {
                                jsn.put("mainimage", encodeImageTobase64(path3));
                                path3=null;
                            }
                            JSONObject images = new JSONObject();
                            if (path1 != null)
                                images.put("image1.jpg",encodeImageTobase64(path1));
                            if (path2 != null)
                                images.put("image2.jpg",encodeImageTobase64(path2));
                            if (path3 != null)
                                images.put("image3.jpg",encodeImageTobase64(path3));
                            jsn.put("images",images);
                            jsn = makeRequest("CMS_create",sp.getString("userid",""), jsn.toString());
                            if (jsn!=null && jsn.getJSONObject("result").getString("userid").equals(sp.getString("userid",""))){
                                new NotificationDialog(getActivity(), "Proiectul dumneavoastră a fost adăugat cu succes !").show();
                                LeroyApplication.getCacheManager().unset("projects_nr");
                                getFragmentManager().popBackStack();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    new NotificationDialog(getActivity(),"Pentru a putea adăuga proiectul dumneavoastră, vă rugăm să va conectați la internet!").show();
                }
            }
        });
        return view;
    }

    // porneste activitate de preluare a imaginii
    private void getImage(ImageView image) {
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picturesError.setVisibility(View.GONE);
                Intent intent = new Intent(getActivity(), AddPhotoActivity.class);
                startActivityForResult(intent, (int)v.getTag());
            }
        });
    }

    // afiseaza imaginile existente in imageview-uri
    private void placeImages() {
        if (path1 != null) {
            Picasso.with(getActivity())
                    .load(new File(path1))
                    .placeholder(R.drawable.add_picture)
                    .fit().centerInside()
                    .into(image1);
        }
        if (path2 != null) {
            Picasso.with(getActivity())
                    .load(new File(path2))
                    .placeholder(R.drawable.add_picture)
                    .fit().centerInside()
                    .into(image2);
        }
        if (path3 != null) {
            Picasso.with(getActivity())
                    .load(new File(path3))
                    .placeholder(R.drawable.add_picture)
                    .fit().centerInside()
                    .into(image3);
        }
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
            if (requestCode==IMAGE1) {
                path1 = data.getStringExtra("path");
                pathIsNull(path1);
            }
            if (requestCode==IMAGE2) {
                path2 = data.getStringExtra("path");
                pathIsNull(path2);
            }
            if (requestCode==IMAGE3) {
                path3 = data.getStringExtra("path");
                pathIsNull(path3);
            }
            placeImages();
        }
    }

    // transforma o imaginea intr-un string base64 pentru a putea fi transmisa prin http request
    private String encodeImageTobase64(String path) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(path, bmOptions);
        Matrix m = new Matrix();
        m.setRectToRect(new RectF(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                new RectF(0, 0, 1920, 1920), Matrix.ScaleToFit.CENTER);
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        bitmap.recycle();
        return imageEncoded;
    }

    public JSONObject makeRequest(String... params){
        JSONArray array = new JSONArray();
        try {
            array.put(params[1]);
            array.put(new JSONObject(params[2]));
            JSONObject request = new JSONObject();
            request.put("method", params[0]);
            request.put("params",array);
            return new JSONObject(new WebServiceConnector().execute("http://www.facem-facem.ro/customAPI/privateEndpoint/"+LeroyApplication.getInstance().APIVersion, "q=" + URLEncoder.encode(request.toString())).get());

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        } catch (ExecutionException e1) {
            e1.printStackTrace();
        }

        return null;
    }

    private void getContests(){
        JSONObject response = LeroyApplication.getInstance().makeRequest("CMS_get_contests");
        try {
            JSONArray resultArray = response.getJSONArray("result");
                for(int i=0;i<resultArray.length();i++){
                    Contest contest = new Contest();
                    contest=contest.fromJson(resultArray.getJSONObject(i));
                    if(!contest.getTitle().contains("Castigatorii"))
                         adapter.add(contest);
                }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (adapter.getCount()==0){
            new NotificationDialog(getActivity(),"Ne pare rău, dar momentan nu exista niciun concurs în desfașurare!");
            getActivity().getSupportFragmentManager().popBackStack();
        }

    }

    private void pathIsNull(String path){
        if(path==null)
            new NotificationDialog(getActivity(),"Ne pare rau, dar imaginea pe care ati selectat-o nu se gaseste stocata pe dispozivul dumneavoastra!").show();
    }*/
//}
