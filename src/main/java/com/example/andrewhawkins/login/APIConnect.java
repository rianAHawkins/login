package com.example.andrewhawkins.login;


/*
easy api connector
should be able to send url and data
return will be jsobject
status(0:not done,1:success,2:failed)
 */


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class APIConnect extends AsyncTask<Void, Void, Boolean> {

    private JSONObject data;
    private String URL;
    private JSONObject ZeReturn;
    private int status=0;

    APIConnect(String URL,JSONObject data) {
        this.URL = URL;
        this.data = data;
    }

    public int getstat()
    {
        return status;
    }

    public JSONObject getZeReturn()
    {
        return ZeReturn;
    }

    private void setPostRequestContent(HttpURLConnection conn,
                                       JSONObject jsonObject) throws IOException {

        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(jsonObject.toString());
        Log.i("log:", jsonObject.toString());
        writer.flush();
        writer.close();
        os.close();
    }

    public String getting(String URL,JSONObject json) {
        try {
            URL url = new URL(URL+"/authenticate");


            //setup connection variable con
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            //add json object to con
            setPostRequestContent(con, json);

            //conntect to url and pass json
            con.connect();

            //handle return of url
            if (con.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + con.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (con.getInputStream())));

            String output,SaveLines="";
            while ((output = br.readLine()) != null) {
                System.out.println(output);
                SaveLines += output;
            }
            if((SaveLines=="false")||(SaveLines==""))
            {
                return "ERROR NOTHING FROM API";
            }
            else
            {
                ZeReturn = new JSONObject(SaveLines);
                return "It's Right!!!!";
            }
        } catch (Exception e){
            return "ERROR FAILED TRY CATCH FROM API CALL";
        }
    }

    @Override
    protected Boolean doInBackground (Void...params){
        try {

            //new new here http://localhost:3001/login

            String apiR = getting(URL,data);
            if(!apiR.contains("ERROR"))
            {
                return true;
            }
            else
            {
                return false;
            }
        } catch (Exception error) {
            return false;
        }
    }

    @Override
    protected void onPostExecute ( final Boolean success){

        if (success) {
            status=1;
        } else {
            status=2;
        }
    }

    @Override
    protected void onCancelled () {

    }
}
