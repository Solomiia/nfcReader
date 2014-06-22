package com.example.myapplication5.app.Common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.myapplication5.app.R;

/**
 * Created by Solomiia on 6/17/2014.
 */
public class AlertDialogManager {

    Context context;

    public AlertDialogManager(Context context)
    {
        this.context = context;
    }
    public Dialog onCreateDialog(String message,String title)
    {
        Dialog dialog = null;

            AlertDialog.Builder builder = new AlertDialog.Builder( context, R.style.CustomDialog );
            builder.setTitle(title);
            builder.setMessage(message)
                    .setCancelable( false )
                    .setPositiveButton("Ok",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            }
                    );
            dialog = builder.create();

        return dialog;
    }

    public Dialog MessageBox(String title, String text,final Activity activity, final Activity next, final Activity back, final String data)
    {
        //push some extra data if not a null
        AlertDialog.Builder builder = new AlertDialog.Builder(activity, R.style.CustomDialog );
        builder
                .setTitle(title)
                .setMessage(text)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    public void onClick(DialogInterface dialog, int which)
                    {
                        Intent myIntent = new Intent(activity, next.getClass());
                        myIntent.putExtra("result",data);
                        activity.startActivityForResult(myIntent, 1);
                    }
                });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface dialog, int which)
            {
                Intent myIntent = new Intent(activity, back.getClass());
                activity.startActivityForResult(myIntent, 1);
            }
        });
        return builder.create();


    }

}
