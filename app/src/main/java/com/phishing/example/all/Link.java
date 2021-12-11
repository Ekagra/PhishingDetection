package com.phishing.example.all;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ClientCertRequest;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.webkit.WebViewCompat;
import 	androidx.webkit.WebViewFeature;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.safetynet.SafeBrowsingThreat;
import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.safetynet.SafetyNetApi;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

import static android.content.ContentValues.TAG;

public class Link extends AppCompatActivity {

    private Button btn;
    private EditText editText;
    private WebView webview;
    private Context mContext;
    private TextView textView;
    private ProgressBar progressBar;
    private ProgressDialog progressDialog;
    private boolean error;
    private String url;
    List<String> links;
    StringManipulation stringManipulation;

    Button bt;
    int value=0,gsb=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.check_link);
        btn = (Button) findViewById(R.id.button);
        editText = (EditText) findViewById(R.id.url);
        webview = (WebView) findViewById(R.id.web);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        textView = (TextView) findViewById(R.id.text) ;
        mContext = this;
        bt = (Button) findViewById(R.id.check);

        // Enable WebSettings
        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setAllowUniversalAccessFromFileURLs(true);
        settings.setDomStorageEnabled(true);
        webview.canGoBack();

        webview.getSettings().setSafeBrowsingEnabled(true);
        webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webview.getSettings().setSaveFormData(true);
        stringManipulation = new StringManipulation();



        //listener for button click
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webview.setVisibility(View.VISIBLE);
                url = editText.getText().toString();
                check(url);
                progressBar.setVisibility(View.VISIBLE);
            }
        });

        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Link.this, LinksStatus.class);
                startActivity(intent);
            }
        });



    }

    private void initStorage(String status){

        value=1;

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor sharedPreferencesEditor = sharedPref.edit();
        String all_safe = sharedPref.getString("safe_links", "");
        String all_phishing = sharedPref.getString("phishing_links", "");

        StringBuilder cList = new StringBuilder();

        if (status.equals("safe")) {
            links = new ArrayList<String>();
            links.add(url);
            cList.append(all_safe);

            if(stringManipulation.duplicate(cList.toString(), url)) {

                for (String s : links) {
                    cList.append(s);
                    cList.append(",");
                }

                sharedPreferencesEditor.putString("safe_links", cList.toString()).commit();

            }
        }

        if (status.equals("phishing")) {

            links = new ArrayList<String>();
            links.add(url);
            cList.append(all_phishing);

            if(stringManipulation.duplicate(cList.toString(), url)) {

                for (String s : links) {
                    cList.append(s);
                    cList.append(",");
                }

                sharedPreferencesEditor.putString("phishing_links", cList.toString()).commit();

            }
        }
    }

    private void showdialog(String ans){


        ViewDialog alert = new ViewDialog();
        url = editText.getText().toString();
        try {
            alert.showDialog(this, "HOST: " +(new URL(url)).getHost(),  ans, 0, webview);
        }catch (MalformedURLException e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void showalert(String ans){

        webview.setVisibility(View.GONE);

        ViewDialog alert = new ViewDialog();
        url = editText.getText().toString();
        try {
            alert.showDialog(this, "HOST: " +(new URL(url)).getHost(),  ans, 1, webview);
        }catch (MalformedURLException e){
            Log.d(TAG, e.getMessage());
        }
    }

    private void check(String url) {


        //check if the link's domain contains onlu numerics
        try {
            String domain = stringManipulation.getUrlDomainName((new URL(url)).getHost());
            if( stringManipulation.wrongurl(domain) && domain.length() >1){
                progressBar.setVisibility(View.GONE);
                showalert("RESULT: This link's domain only contains numerics!");
                initStorage("phishing");
            }
        }catch (MalformedURLException e){
            Log.d(TAG, e.getMessage());
        }



        //SafteyNet API
        SafetyNet.getClient(this).lookupUri(url,
                getString(R.string.safety_net_api_key),
                SafeBrowsingThreat.TYPE_POTENTIALLY_HARMFUL_APPLICATION,
                SafeBrowsingThreat.TYPE_SOCIAL_ENGINEERING)
                .addOnSuccessListener(this,
                        new OnSuccessListener<SafetyNetApi.SafeBrowsingResponse>() {
                            @Override
                            public void onSuccess(SafetyNetApi.SafeBrowsingResponse sbResponse) {
                                if (!sbResponse.getDetectedThreats().isEmpty() ) {
                                    gsb =1;
                                    progressBar.setVisibility(View.GONE);
                                    showalert( "RESULT: This link is flagged by Google Safe Browsing!");
                                    initStorage("phishing");
                                }
                            }
                        })
                .addOnFailureListener((Activity) mContext, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // An error occurred while communicating with the service.
                        if (e instanceof ApiException) {
                            // An error with the Google Play Services API contains some
                            // additional details.
                            ApiException apiException = (ApiException) e;
                            Log.d(TAG, "Error: " + CommonStatusCodes
                                    .getStatusCodeString(apiException.getStatusCode()));
                        } else {
                            Log.d(TAG, "Error: " + e.getMessage());
                        }
                    }
                });




        // load from here
        webview.loadUrl(url);

        webview.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            //invoked when ssl error produced in url
            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                super.onReceivedSslError(view, handler, error);
                progressBar.setVisibility(View.GONE);
                showalert("RESULT: This link produces SSL error!");
                initStorage("phishing");
            }

            //invoked when automatic login requested by host
            @Override
            public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
                progressBar.setVisibility(View.GONE);
                showalert("RESULT: This link wants to automatically log you in!");
                initStorage("phishing");
            }


            //invoked when https error occurs and alert is invoked when response code is 401 or 402
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                int statusCode = errorResponse.getStatusCode();
                if(statusCode == 401 || statusCode == 402){
                    progressBar.setVisibility(View.GONE);
                    showalert("RESULT: This website throws suspicious HTTP error");
                    initStorage("phishing");
                }
            }


            @RequiresApi(api = Build.VERSION_CODES.O_MR1)
            @Override
            public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
                super.onSafeBrowsingHit(view, request, threatType, callback);
                // The "true" argument indicates that your app reports incidents like
                // this one to Safe Browsing.
                if(gsb==0) {
                    callback.backToSafety(true);
                    progressBar.setVisibility(View.GONE);
                    showalert("RESULT: This link is flagged by Google Safe Browsing!");
                    initStorage("phishing");
                }
            }

            @Override
            public void onTooManyRedirects(WebView view, Message cancelMsg, Message continueMsg) {
                super.onTooManyRedirects(view, cancelMsg, continueMsg);
                progressBar.setVisibility(View.GONE);
                showalert("RESULT: This link is ridirecting several times!");
                initStorage("phishing");
            }

            @Override
            public void onReceivedClientCertRequest(WebView view, ClientCertRequest request) {
                super.onReceivedClientCertRequest(view, request);
                request.cancel();
                showalert("RESULT: This link created a certificate error.");
                initStorage("phishing");
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if(value ==0) {
                    value = 2;
                    progressBar.setVisibility(View.GONE);
                    showdialog("RESULT: The link can't be checked.");
                }

            }

            //invoked when page is successfully loaded
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(value==0) {
                    progressBar.setVisibility(View.GONE);
                    showdialog("RESULT: Non Phishing");
                    initStorage("safe");
                }
            }
        });


        value =0; //reset
        gsb=0;

    }

}
