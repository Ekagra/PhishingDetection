package com.phishing.example.all;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.SafeBrowsingResponse;
import android.webkit.SslErrorHandler;
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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;

public class Link extends Fragment {

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
    Button bt;
    int value=0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.check_link, container, false);
        btn = (Button) view.findViewById(R.id.button);
        editText = (EditText) view.findViewById(R.id.url);
        webview = (WebView) view.findViewById(R.id.web);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        textView = (TextView) view.findViewById(R.id.text) ;
        mContext = getActivity();
        bt = (Button) view.findViewById(R.id.check);

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

        //listener for button click
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = editText.getText().toString();
                check(url);
                progressBar.setVisibility(View.VISIBLE);
            }
        });




        return view;
    }

    private void initStorage(String status){

        value=1;

            bt.setVisibility(View.VISIBLE);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor sharedPreferencesEditor = sharedPref.edit();
            String all_safe = sharedPref.getString("safe_links", "");
            String all_phishing = sharedPref.getString("phishing_links", "");

            StringBuilder cList = new StringBuilder();

            if (status.equals("safe")) {
                links = new ArrayList<String>();
                links.add(url);
                cList.append(all_safe);

                for (String s : links) {
                    cList.append(s);
                    cList.append(",");
                }

                sharedPreferencesEditor.putString("safe_links", cList.toString()).commit();
            }

            if (status.equals("phishing")) {

                links = new ArrayList<String>();
                links.add(url);
                cList.append(all_phishing);

                
                for (String s : links) {
                    cList.append(s);
                    cList.append(",");
                }

                sharedPreferencesEditor.putString("phishing_links", cList.toString()).commit();
            }


            //Goto Classified Links Activity
            bt.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(), LinksStatus.class);
                    startActivity(intent);
                }
            });




    }

    private void check(String url){

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
                textView.setVisibility(View.VISIBLE);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                textView.setText("PHISHING POSSIBILITY: This websites produces SSL error!");
                initStorage("phishing");
            }

            //invoked when automatic login requested by host
            @Override
            public void onReceivedLoginRequest(WebView view, String realm, @Nullable String account, String args) {
                super.onReceivedLoginRequest(view, realm, account, args);
                progressBar.setVisibility(View.GONE);
                textView.setVisibility(View.VISIBLE);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                textView.setText("PHISHING POSSIBILITY: This website wants to automatically log you in!");
                initStorage("phishing");
            }

            //invoked when host is flagged by google safe browsing
            @Override
            public void onSafeBrowsingHit(WebView view, WebResourceRequest request, int threatType, SafeBrowsingResponse callback) {
                super.onSafeBrowsingHit(view, request, threatType, callback);
                progressBar.setVisibility(View.GONE);
                textView.setTextColor(getResources().getColor(R.color.colorAccent));
                textView.setVisibility(View.VISIBLE);
                textView.setText("PHISHING POSSIBILITY: This website is flagged by Google Safe Browsing!");
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
                    textView.setTextColor(getResources().getColor(R.color.colorAccent));
                    textView.setVisibility(View.VISIBLE);
                    textView.setText("PHISHING POSSIBILITY: This website is flagged by Google Safe Browsing!");
                    initStorage("phishing");
                }
            }

            //invoked when page is successfully loaded
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(value==0) {
                    progressBar.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                    textView.setTextColor(getResources().getColor(R.color.green));
                    textView.setText("SAFE: This link doesn't seems to be malicious");
                    initStorage("safe");
                }
            }
        });

        value =0; //reset



    }


}
