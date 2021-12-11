package com.phishing.example.all;

import android.util.Log;

import java.util.ArrayList;

public class StringManipulation {

    public String getUrlDomainName(String url) {
        String domain = null;
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        ArrayList<Integer> www = new ArrayList<Integer>();

        for(int i=0; i< url.length(); i++){

            if(url.charAt(i) == '.'){
                arrayList.add(i);
            }

            if(url.charAt(i) == 'w'){
                www.add(i);
            }
        }

        if(www.size() ==3){
            url = url.substring(4, url.length()-1);
            domain = url.substring(0, arrayList.get(arrayList.size() - 1)-4);
        }else {
            domain = url.substring(0, arrayList.get(arrayList.size() - 1));
        }
        return domain;
    }

    boolean wrongurl(String url) {

        for (int i = 0; i < url.length(); i++) {
            if (url.charAt(i) != '.' || url.charAt(i) != '0' || url.charAt(i) != '1' || url.charAt(i) != '2' || url.charAt(i) != '3' || url.charAt(i) != '4'
                    || url.charAt(i) != '5' || url.charAt(i) != '6' || url.charAt(i) != '7' || url.charAt(i) != '8' || url.charAt(i) != '9') {

                return false;
            }
        }

            return true;
        }

    boolean duplicate(String all, String url) {

        String[] items = all.split(",");
        for (String item : items) {
            if (item.equals(url)) {
                return false;
            }
        }

        return true;
    }

}
