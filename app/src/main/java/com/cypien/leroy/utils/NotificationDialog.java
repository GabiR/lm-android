package com.cypien.leroy.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.Html;

import com.cypien.leroy.R;



/**
 * Created by Alex on 9/21/2015.
 */
public class NotificationDialog {
    Context context;
    String message;

    public NotificationDialog(Context context, String message) {
        this.context = context;
        this.message = message;
    }

    public void show() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setMessage(Html.fromHtml(message));
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }
}
