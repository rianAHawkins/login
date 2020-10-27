package com.example.andrewhawkins.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Theip extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theip);
        final EditText etIP = (EditText)findViewById(R.id.txtIP);
        Button btnClicked = (Button)findViewById(R.id.btnClick);
        btnClicked.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                String ips = etIP.getText().toString();
                SaveIp(ips);
            }
        });
        try{
            String filename =getString(R.string.ipFile);
            File fl = new File(filename);
            fl.delete();
        }catch(Exception e){

        }
    }
    public void SaveIp(final String URL) {
        String dir = getApplicationInfo().dataDir;//get directory
        try {
            String filename =getString(R.string.ipFile);
            FileWriter out = new FileWriter(new File(dir, filename));
            out.write(URL);
            out.close();
            changeActivity();
        } catch (IOException e) {

        }
    }

    public void changeActivity(){
        try {
            Intent myIntent = new Intent(this, LoginActivity.class);
            startActivity(myIntent);
        } catch (Exception IntentERROR) {
            System.out.print("An error occured with the Intent");
        }
    }
}
