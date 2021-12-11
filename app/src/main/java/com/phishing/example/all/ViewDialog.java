package com.phishing.example.all;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

public class ViewDialog {



    public void showDialog(Activity activity, String proxy, String ans, int status, final WebView webView){
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.custom_popup);

        TextView text1 = (TextView) dialog.findViewById(R.id.proxy);
        TextView text3 = (TextView) dialog.findViewById(R.id.ans);
        RelativeLayout alert = (RelativeLayout) dialog.findViewById(R.id.alert);
        final WebView web = webView;
        text1.setText(proxy);
        text3.setText(ans);

        Button dialogButton = (Button) dialog.findViewById(R.id.btn_dialog);
        Button proceed = (Button) dialog.findViewById(R.id.procced);


        if(status==1){
            alert.setVisibility(View.VISIBLE);
            proceed.setVisibility(View.VISIBLE);

            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    web.clearCache(true);
                    dialog.dismiss();
                }
            });


            proceed.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    web.goForward();
                    web.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                }
            });

        }else {

            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
        }
        dialog.show();

    }
}