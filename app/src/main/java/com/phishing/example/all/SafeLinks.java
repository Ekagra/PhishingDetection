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
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SafeLinks extends Fragment {


    List<String> links;
    LinksAdapter adapter;
    Context mContext;

    //ViewModels
    ListView listView;
    TextView text;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.linkslist, container, false);
        text = (TextView) view.findViewById(R.id.error) ;
        listView = (ListView) view.findViewById(R.id.list) ;
        mContext = this.getActivity();
        setupList();


        return view;
    }

    private void setupList(){

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String all_safe = sharedPref.getString("safe_links", "");


        if(all_safe.isEmpty()){
            text.setVisibility(View.VISIBLE);
        }else{
            //serialize
            links = new ArrayList<String>();
            String cList = sharedPref.getString("safe_links", "");
            String[] items = cList.split(",");
            for(int i=0; i < items.length; i++){
                links.add(items[i]);
            }


            adapter = new LinksAdapter(mContext, R.layout.safe_link, links);
            listView.setAdapter(adapter);
        }
    }

}
