package com.example.raj.chattcp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class server extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
    }
    public void onClick(View view){
        MainActivity.server_status = true;
    }
}
