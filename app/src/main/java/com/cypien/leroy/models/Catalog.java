package com.cypien.leroy.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;

/**
 * Created by GabiRotaru on 04/08/16.
 */
public class Catalog implements Serializable {
    String title;
    //Nope, cu asta se deschide catalogul
    String slug; //identificator
    String pdfURL; //link descarcare pdf
    String contextualURL; //apelare pt detalii catalog
    // NOOOOOOOOOOOOOOOOOOOOOOOOOOOOOO
   // String websiteURL; //link catalog animat -> de deschis in webview in app
    String coverImageURL; //link imagine coperta;
                          // forma: /1613/130970/pages/f44eab2912eb90a5e127683240a2b6c8e9f6213b
                          // https://view.publitas.com + forma  + -at600.jpg

    transient Bitmap cover;
    String imageBase;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getPdfURL() {
        return pdfURL;
    }

    public void setPdfURL(String pdfURL) {
        this.pdfURL = pdfURL;
    }

    public String getContextualURL() {
        return contextualURL;
    }

    public void setContextualURL(String contextualURL) {
        this.contextualURL = contextualURL;
    }

   /* public String getWebsiteURL() {
        return websiteURL;
    }

    public void setWebsiteURL(String websiteURL) {
        this.websiteURL = websiteURL;
    }
*/
    public String getCoverImageURL() {
        return coverImageURL;
    }

    public void setCoverImageURL(String coverImageURL) {
        this.coverImageURL = coverImageURL;
    }

    public Bitmap getCover() {
        return cover;
    }

    public void setCover(Bitmap cover) {
        this.cover = cover;
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

    public String getImageBase() {
        return imageBase;
    }

    public void setImageBase(String imageBase) {
        this.imageBase = imageBase;
    }

    public Bitmap buildImage(){
        cover=decodeBase64(imageBase);
        //       imageBase=null;
        return cover;
    }



    public static String encodeToBase64(Bitmap image)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }


    public void buildImageBase(Bitmap bitmap) {
        imageBase = encodeToBase64(bitmap);
    }
}
