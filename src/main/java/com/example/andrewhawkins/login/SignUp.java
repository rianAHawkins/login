package com.example.andrewhawkins.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;

public class SignUp extends AppCompatActivity {
    String TheUrl = "";
    APIConnect con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TheUrl = getString(R.string.Url);
        Button btnBack = (Button)findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });


        Button btnClicked = (Button)findViewById(R.id.btnSign);
        btnClicked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView tvName = (TextView) findViewById(R.id.txtUserName);
                TextView tvPassword = (TextView) findViewById(R.id.txtPassword);

                JSONObject Fresh = new JSONObject();
                try
                {
                    Fresh.put("name", tvName.getText().toString());
                    Fresh.put("name", tvPassword.getText().toString());
                }
                catch (Exception x)
                {
                    System.out.println("Errors out here boys");
                }

                con = new APIConnect(TheUrl+"/signup",Fresh);
                con.execute((Void) null);
                checkonit(con,5,3);
            }
        });
    }

    Handler h = new Handler();
    int delay;
    Runnable runnable;
    protected void checkonit(APIConnect z,int times, int every)
    {
        delay = every*1000;
        h.postDelayed( runnable = new Runnable() {
            public void run() {
                //do something
                if(con.getFinished())
                {
                    JSONObject User = con.getZeReturn();
                    back();
                }
                h.postDelayed(runnable, delay);
            }
        }, delay);
    }

    protected void back(){
        try {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        } catch (Exception IntentERROR) {
            System.out.print("An error occured with the Intent");
        }
    }

}
