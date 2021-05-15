package com.example.mooyaho;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.mooyaho.Fragment.PeopleFragment;

public class friendsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.friends_list);

        getFragmentManager().beginTransaction().replace(R.id.fragmentlayout_people, new PeopleFragment()).commit();

    }



}