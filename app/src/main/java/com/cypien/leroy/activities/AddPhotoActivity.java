package com.cypien.leroy.activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.LinearLayout;

import com.cypien.leroy.LeroyApplication;
import com.cypien.leroy.R;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;



/**
 * Created by Alex on 9/24/2015.
 */
public class AddPhotoActivity extends Activity{

    private LinearLayout newPhotoButton;
    private LinearLayout galleryPhotoButton;
    final int GALLERY_PHOTO=1;
    final int NEW_PHOTO=2;
    private String path;
    private Uri fileUri;

   // private ImageView camera_icon, gallery_icon, mascota_icon;
   // private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_photo_screen);

        LeroyApplication application = (LeroyApplication) getApplication();
        Tracker mTracker = application.getDefaultTracker();
        mTracker.setScreenName("Screen:" + "AddPhotoActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
       // screenHeight = metrics.heightPixels;


        // cazul in care se doreste capturarea unei imagini cu camera telefonului
        newPhotoButton=(LinearLayout)findViewById(R.id.new_photo);
        newPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                fileUri = Uri.fromFile(getOutputMediaFile());
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, NEW_PHOTO);
            }
        });

        // cazul in care se doreste preluarea unei imagini din galerie
        galleryPhotoButton=(LinearLayout)findViewById(R.id.photo);
        galleryPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, GALLERY_PHOTO);
            }
        });

       /* mascota_icon.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });*/
    }

    // preluarea imaginii ,transmiterea adresei imaginii catre fragmentul care a solicitat-o si revenirea la acel fragment
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if (requestCode == GALLERY_PHOTO ) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = { MediaStore.Images.Media.DATA };
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                int columnIndex;
                if (cursor != null) {
                    cursor.moveToFirst();
                    columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    path=cursor.getString(columnIndex);
                    cursor.close();
                }


            }
            if(requestCode == NEW_PHOTO ){
                path=fileUri.getPath();
                File file = new File(path);
                MediaScannerConnection.scanFile(getApplicationContext(), new String[]{file.getAbsolutePath()},
                        null, new MediaScannerConnection.OnScanCompletedListener() {
                            public void onScanCompleted(String path, Uri uri) {
                            }
                        });
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("path", path);
            setResult(Activity.RESULT_OK, resultIntent);
            finish();
        }
    }

    // genereare fisier pentru imaginea capturata de camera
    private static File getOutputMediaFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFileName;
        mediaFileName = new File( Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + File.separator
                + "IMG_" + timeStamp + ".jpg");
        return mediaFileName;
    }

    /*public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        if(hasFocus) {
            camera_icon.getLayoutParams().height = (int) (screenHeight / 9.14f);
            gallery_icon.getLayoutParams().height = (int) (screenHeight / 9.14f);
            mascota_icon.getLayoutParams().height = (int) (screenHeight / 4.67f);
        }
    }*/
}
