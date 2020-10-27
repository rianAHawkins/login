package com.example.andrewhawkins.login;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Andrew.hawkins on 6/20/2016.
 */
public class Keno extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private String TheUrl="http://kenovision.radianttech.net:32350";//the url we will be pinging http://192.168.2.175:3001
    private JSONObject User = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.kenoface);
        Button btnClicked = (Button)findViewById(R.id.btnClick);
        btnClicked.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){

            }
        });
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(this,
                // Retrieve data rows for the device user's 'profile' contact.
                Uri.withAppendedPath(ContactsContract.Profile.CONTENT_URI,
                        ContactsContract.Contacts.Data.CONTENT_DIRECTORY), ProfileQuery.PROJECTION,

                // Select only email addresses.
                ContactsContract.Contacts.Data.MIMETYPE +
                        " = ?", new String[]{ContactsContract.CommonDataKinds.Email
                .CONTENT_ITEM_TYPE},

                // Show primary email addresses first. Note that there won't be
                // a primary email address if the user hasn't specified one.
                ContactsContract.Contacts.Data.IS_PRIMARY + " DESC");
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {

    }

    private interface ProfileQuery {
        String[] PROJECTION = {
                ContactsContract.CommonDataKinds.Email.ADDRESS,
                ContactsContract.CommonDataKinds.Email.IS_PRIMARY,
        };

        int ADDRESS = 0;
        int IS_PRIMARY = 1;
    }

    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail="";
        private final String mPassword="";

        public boolean getting(String URl, String Name, String Password) {
            try {
                System.out.println("URL:"+URl+" Name:"+Name+" Password:"+Password);
                //http://192.168.2.175:3001/login?user=dave&password=world
                URL url = new URL(URl+"/login?user="+Name+"&password="+Password);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + con.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (con.getInputStream())));

                String output,SaveLines="";
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    SaveLines+=output;
                }
                JSONObject js = new JSONObject(SaveLines);
                User = js;//give user to the whole class
            } catch (Exception e){
                return false;
            }
            return true;
        }

        public boolean post(String URl, String Name, String Password,int AGE) {
            try {
                System.out.println("URL:"+URl+" Name:"+Name+" Password:"+Password);
                //http://192.168.2.175:3001/login?user=dave&password=world
                URL url = new URL(URl+"/signUP?user="+Name+"&password="+Password+"&Age="+AGE);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");

                if (con.getResponseCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + con.getResponseCode());
                }

                BufferedReader br = new BufferedReader(new InputStreamReader(
                        (con.getInputStream())));

                String output,SaveLines="";
                System.out.println("Output from Server .... \n");
                while ((output = br.readLine()) != null) {
                    System.out.println(output);
                    SaveLines += output;
                }
            } catch (Exception e){
                return false;
            }
            return true;
        }

        @Override
        protected Boolean doInBackground (Void...params){
            try {

                //new new here http://localhost:3001/login

                boolean postr = getting(TheUrl,mEmail,mPassword);
                return postr;
            } catch (Exception error) {

            }

            // TODO: register the new account here.
            post(TheUrl,mEmail,mPassword,18);
            return true;
        }

    }


}

