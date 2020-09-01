package com.example.appcronometro;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class ListClass {

    public void addTempos(ArrayList al, ArrayAdapter aa, String s){
        al.add(s);
        aa.notifyDataSetChanged();
    }

}
