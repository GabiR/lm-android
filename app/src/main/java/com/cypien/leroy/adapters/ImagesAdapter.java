package com.cypien.leroy.adapters;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import com.cypien.leroy.LeroyApplication;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Alex on 7/3/2015.
 */
public class ImagesAdapter extends PagerAdapter {

    private final SharedPreferences sp;
    private ArrayList<Bitmap> images;
    private Context context;
    private String  type;

    public ImagesAdapter(Context context, String projectId,String type) {
        this.type=type;
        this.context=context;
        images = new ArrayList<>();
        sp = context.getSharedPreferences("com.cypien.leroy_preferences", context.MODE_PRIVATE);
        Type myObjectType = new TypeToken<Integer>(){}.getType();
        Integer nrImages = (Integer)LeroyApplication.getCacheManager().get("images_nr"+projectId, Integer.class, myObjectType);
        myObjectType = new TypeToken<String>(){}.getType();
        if ( nrImages != null ) {
            for(int i=0; i<nrImages; i++){
                String image = (String)LeroyApplication.getCacheManager().get("image_"+projectId+i,String.class,myObjectType);
                images.add(decodeBase64(image));
            }
        } else {
            getImages(projectId);
        }
    }

    @Override
    public int getCount() {
        return images.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imageView = newImageViewInstance();
        imageView.setImageBitmap(images.get(position));
        container.addView(imageView);
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((ImageView) object);
    }

    private void getImages(String projectId){
        try {
            JSONObject response = LeroyApplication.getInstance().makeRequest(type+"_get_images",sp.getString("endpointCookie", ""), sp.getString("userid", ""), projectId);
            JSONArray resultArray = response.getJSONArray("result");
            for(int i=0;i<resultArray.length();i++){
                String imageBase=resultArray.getString(i);
                LeroyApplication.getCacheManager().put("image_"+projectId+i,imageBase);
                LeroyApplication.getCacheManager().put("images_nr"+projectId, i + 1);
                images.add(decodeBase64(resultArray.getString(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    // transforma un string base64 in imagine
    public static Bitmap decodeBase64(String input){
        byte[] decodedByte = Base64.decode(input, 0);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length, options);
        options.inSampleSize = calculateInSampleSize(options, 420, 330);
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length,options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    private ImageView newImageViewInstance() {
        ImageView iv = new ImageView(context);
        iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        lp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        iv.setLayoutParams(lp);
        return iv;
    }
}