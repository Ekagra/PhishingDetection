package com.phishing.example.all;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LinksAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private LayoutInflater mInflater;
    private int layoutResource;
    List<String> v=null;


    public LinksAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<String> str) {
        super(context, resource,str);
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mContext = context;
        layoutResource = resource;
        this.v = str;

    }

    private static class ViewHolder{
      TextView text;

    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder holder;

        if(convertView == null){
            convertView = mInflater.inflate(layoutResource, parent, false);

            holder = new ViewHolder();
            holder.text= (TextView) convertView.findViewById(R.id.link);
            convertView.setTag(holder);
        }
        else{
            holder = (LinksAdapter.ViewHolder) convertView.getTag();
        }

        holder.text.setText(v.get(position));

        return convertView;
    }

    }