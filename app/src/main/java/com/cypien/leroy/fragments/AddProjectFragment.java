package com.cypien.leroy.fragments;

import android.annotation.TargetApi;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.cypien.leroy.activities.AddPhotoActivity;
import com.cypien.leroy.adapters.CategoriesAdapter;
import com.cypien.leroy.models.Category;
import com.cypien.leroy.utils.Connections;
import com.cypien.leroy.utils.NotificationDialog;
import com.cypien.leroy.utils.WebServiceConnector;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutionException;

/**
 * Created by Alex on 8/11/2015.
 */
public class AddProjectFragment extends Fragment {
    private View view;

    private EditText title, costs, duration, categories, details;

    private ImageView image1, image2, image3;
    private Button addProjectButton;
    private String path1, path2, path3;
    private SharedPreferences sp;
    private ListView categoriesList;
    private PopupWindow categoriesWindow;
    private CategoriesAdapter adapter;
    private static String selectedCategoriesIndexes,selectedCategories;
    private final int IMAGE1=175,IMAGE2=176,IMAGE3=177;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.add_project_screen, container, false);
        sp = getActivity().getSharedPreferences("com.cypien.leroy_preferences", getActivity().MODE_PRIVATE);

        ((TextView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(2)).setText("Adaugă proiect");

        ImageView back_arrow = (ImageView) ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(0);
        back_arrow.setVisibility(View.VISIBLE);
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            }
        });

        ((Toolbar) getActivity().findViewById(R.id.toolbar)).getChildAt(1).setVisibility(View.GONE);

        addProjectButton = (Button) view.findViewById(R.id.create);

        image1 = (ImageView) view.findViewById(R.id.picture1);
        image1.setTag(IMAGE1);
        getImage(image1);

        image2 = (ImageView) view.findViewById(R.id.picture2);
        image2.setTag(IMAGE2);
        getImage(image2);

        image3 = (ImageView) view.findViewById(R.id.picture3);
        image3.setTag(IMAGE3);
        getImage(image3);

        title = (EditText) view.findViewById(R.id.title);
        costs = (EditText) view.findViewById(R.id.costs);
        duration = (EditText) view.findViewById(R.id.duration);
        details = (EditText) view.findViewById(R.id.details);
        categories = (EditText) view.findViewById(R.id.categories);
        initCategoriesWindow();
        categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesWindow.showAsDropDown(v, 0, 0);
            }
        });
        categories.setText(selectedCategories);

        // controleaza disparitia erorilor
//        title.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    v.setBackgroundResource(R.drawable.round_corners_black_border);
//                    titleError.setVisibility(View.GONE);
//                }
//            }
//        });
//
//        details.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    v.setBackgroundResource(R.drawable.round_corners_black_border);
//                    detailsError.setVisibility(View.GONE);
//                }
//            }
//        });

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
                        ok = false;
                    }
                    if (details.getText().toString().length() < 10) {
                        details.setBackgroundResource(R.drawable.round_corners_red_border);
                        ok = false;
                    }
                    if (ok) {
                        JSONObject jsn = new JSONObject();
                        try {
                            jsn.put("comments_visible","0");
                            jsn.put("ratingnum","0");
                            jsn.put("views","0");
                            jsn.put("description",details.getText().toString());
                            jsn.put("title",title.getText().toString());
                            jsn.put("costs",costs.getText().toString());
                            jsn.put("userid",sp.getString("userid", ""));
                            jsn.put("duration", duration.getText().toString());
                            jsn.put("categories",selectedCategoriesIndexes);
                            JSONObject images = new JSONObject();
                            if (path1 != null)
                                images.put("image1.jpg",""+encodeImageTobase64(path1));
                            if (path2 != null)
                                images.put("image2.jpg",encodeImageTobase64(path2));
                            if (path3 != null)
                                images.put("image3.jpg",encodeImageTobase64(path3));
                            jsn.put("images",images);
                            jsn = makeRequest("project_create",sp.getString("userid",""), jsn.toString());
                                if (jsn!=null && jsn.getJSONObject("result").getString("username").equals(sp.getString("username",""))){
                                    new NotificationDialog(getActivity(), "Proiectul dumneavoastră a fost adăugat cu succes!").show();
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

    private ArrayList<Category> getCategories() {
        ArrayList<Category> categories = new ArrayList<>();
        for (String cat : Arrays.asList((getResources().getStringArray(R.array.categories_array)))) {
            categories.add(new Category(cat));
        }
        return categories;
    }

    //setari lista categorii
    private void initCategoriesWindow() {
        categoriesWindow = new PopupWindow(getActivity());
        categoriesList = new ListView(getActivity());
        adapter = new CategoriesAdapter(getActivity(), getCategories());
        categoriesList.setAdapter(adapter);
        categoriesList.setItemsCanFocus(true);
        ViewTreeObserver vto = categories.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                categoriesWindow.setWidth(categories.getWidth());
                categoriesWindow.setHeight((int)(categories.getWidth()*0.7));
                ViewTreeObserver obs = categories.getViewTreeObserver();
                obs.removeOnGlobalLayoutListener(this);
            }

        });
        categoriesWindow.setFocusable(true);
        categoriesWindow.setContentView(categoriesList);
        categoriesWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                selectedCategories = adapter.getSelectedCategories();
                selectedCategoriesIndexes = adapter.getSelectedCategoriesIndexes();
                categories.setText(selectedCategories);
            }
        });
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

    private void pathIsNull(String path){
        if(path==null)
            new NotificationDialog(getActivity(),"Ne pare rău, dar imaginea pe care ați selectat-o nu se găseste stocată pe dispozivul dumneavoastră!").show();
    }

}
