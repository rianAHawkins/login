package com.example.andrewhawkins.login;

/**
 * Created by riana on 12/22/2016.
 */
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import static android.R.drawable.ic_media_pause;
import static android.R.drawable.ic_media_play;
import static android.content.ContentValues.TAG;

/**
 * A placeholder fragment containing a simple view.
 */
public class Middle extends android.support.v4.app.Fragment {
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static String ARG_URL = "";

    private SongTask mST;

    private SongListTask mSLT;
    int intTotalSongs=0,mCurrentPosition;
    double secondaryPosition =0;

    ImageButton btnPlays;
    ImageView imCoverArt;

    TextView tvTimeS,tvTimeE,tvSongName;

    ListView lvSongs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.musixplayer, container, false);
        //*********************************SetUpViews****************************************

        tvSongName =(TextView) rootView.findViewById(R.id.tvSongName);
        //rootView.setBackground(R.drawable.ic_There);
        tvTimeE = (TextView) rootView.findViewById(R.id.tvDuration);
        tvTimeS=(TextView) rootView.findViewById(R.id.tvCurrentTime);

        tvSongName.setText("");
        tvTimeE.setText("");
        tvTimeS.setText("");

        imCoverArt = (ImageView) rootView.findViewById(R.id.ivCover);

        btnPlays = (ImageButton) rootView.findViewById(R.id.ibtnPlay);
        btnPlays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                play(songID);
            }
        });

        ImageButton btnNext = (ImageButton) rootView.findViewById(R.id.ibtnForward);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               next();
            }
        });

        ImageButton btnBack = (ImageButton) rootView.findViewById(R.id.ibtnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        lvSongs = (ListView) rootView.findViewById(R.id.lvSongs);

        lvSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(pageReady) {
                    play(position);
                }
            }
        });

        lvSongs.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if((CurrentSongCount-1<totalItemCount || CurrentSongCount==0)&& uploadlimit<10) {
                    if ((firstVisibleItem + 7) > totalItemCount) {
                        if (scrollReady) {
                            GetList(totalItemCount);
                            CurrentSongCount = totalItemCount;
                            uploadlimit=0;
                            scrollReady = false;
                        } else {
                            scrollReady = true;
                        }
                    }
                }else{
                    uploadlimit++;
                }

            }
        });


        Switch swRepeat = (Switch) rootView.findViewById(R.id.swRepeat);
        swRepeat.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mediaPlayer.setLooping(isChecked);
            }});

        Switch svPicList = (Switch) rootView.findViewById(R.id.svPicList);
        svPicList.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    lvSongs.setVisibility(View.VISIBLE);
                    uploadlimit=0;
                    imCoverArt.setVisibility(View.INVISIBLE);
                }else{
                    lvSongs.setVisibility(View.INVISIBLE);
                    imCoverArt.setVisibility(View.VISIBLE);
                }
            }});

        sbMusix = (SeekBar)rootView.findViewById(R.id.sbMediaMoment);
        secondaryPosition=0;
        sbMusix.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    double max = ((secondaryPosition/100)*sbMusix.getMax());
                    if(progress<max) {
                        mediaPlayer.pause();
                        mediaPlayer.seekTo(progress * 1000);
                        mediaPlayer.start();
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        adapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1,listItems);
        lvSongs.setAdapter(adapter);
        pageReady=true;

        //*********************************SetUpViews****************************************
        return rootView;
    }

    boolean pageReady=false,scrollReady=false;
    int CurrentSongCount=0,uploadlimit=0;
    SeekBar sbMusix;
    static int songID=0;
    static MediaPlayer mediaPlayer=null;

    public void play(int intSong){
        try {
            if ((intSong == songID) && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                btnPlays.setImageResource(ic_media_pause);
            } else if ((intSong == songID) && (!mediaPlayer.isPlaying())) {
                mediaPlayer.start();
                btnPlays.setImageResource(ic_media_play);
            } else {
                songID = intSong;
                String url = ARG_URL + "/song?id=" + songID; // your URL here
                instanceMed(url,0);
            }

        }catch (Exception e){

        }
        if(mediaPlayer==null) {
            String url = ARG_URL + "/song?id=" + songID; // your URL here
            instanceMed(url,0);
        }
    }

    int duration=0;
    public void instanceMed(String url,int pos){
        try{
            GetChangeImage();
        }catch (Exception e){
            System.out.println("error");
        }
        //get name and author
        try {
            GetSongName(songID);
        }catch (Exception e){
            System.out.println("Error Getting Name");
        }

        //set media player
        try {
            if(mediaPlayer==null) {
                SettingmediaPlayer();
            }else{
                mediaPlayer.reset();
            }
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // might take long! (for buffering, etc)
            mediaPlayer.seekTo(pos);
            mediaPlayer.start();
            duration = mediaPlayer.getDuration()/1000;

            int min = (int)Math.floor(duration/60);
            String currentTime = min+":"+(duration-(60*min));
            tvTimeE.setText(currentTime);

            sbMusix.setMax(duration);
            final Handler mHandler = new Handler();

            try {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (mediaPlayer != null) {
                            mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                            int min = (int) Math.floor(mCurrentPosition / 60);
                            String currentTime = min + ":" + (mCurrentPosition - (60 * min));
                            tvTimeS.setText(currentTime);
                            sbMusix.setProgress(mCurrentPosition);
                            if (mCurrentPosition >= (duration - 1)) {
                                if (!mediaPlayer.isLooping()) {
                                    next();
                                }
                            }
                        }
                        mHandler.postDelayed(this, 500);
                    }
                });
            }catch (Exception x){
                Log.d(TAG,"UI Error:"+x.toString());
            }
        }catch (Exception e){

        }
    }

    public void SettingmediaPlayer(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnBufferingUpdateListener(new MediaPlayer.OnBufferingUpdateListener() {
            public void onBufferingUpdate(MediaPlayer mp, int percent) {
                secondaryPosition= percent;
            }
        });
    }

    public void next(){
        if (songID < intTotalSongs+1)
        {
            play(songID + 1);
        }else
        {
            play(0);
        }
    }

    public void back(){
        if(songID>0){
            play(songID-1);
        }
        else
        {
            play(intTotalSongs+1);
        }
    }

    public void GetSongName(int x) {
        mST = new SongTask(x,this,ARG_URL);
        mST.execute((Void) null);
    }

    boolean finishedupdate = true;
    public void GetList(int x){
        if(finishedupdate) {
            finishedupdate=false;
            mSLT = new SongListTask(x, this, ARG_URL);
            mSLT.execute((Void) null);
        }
    }

    public void ChangeSongName(JSONObject SongName) {
        try {
            tvSongName.setText(SongName.get("Author") + " " + SongName.getString("name"));
        }catch (Exception e){

        }
    }

    public void GetChangeImage(){
        String url = ARG_URL + "/coverArt?id=" + songID;
        new DownloadImageTask(imCoverArt)
                .execute(url);
    }

    List<JSONObject> jsA =new ArrayList<>();
    public void AddSongs(JSONObject js)
    {
        boolean error = false;
        int x=0;
        jsA=new ArrayList<>();
        while (!error) {
            try
            {
                jsA.add(js.getJSONObject(x+""));

            }catch (Exception a)
            {
                error=true;
            }
            x++;
        }
    }

    int index=0;
    public void UpdateList(){
        if(index>jsA.size()){
            index=0;
        }
        for(int count=0;count<jsA.size();count++){
            try {
                String Author = jsA.get(index).getString("Author");
                String Name = jsA.get(index).getString("name");
                int id = jsA.get(index).getInt("Songid");
                adapter.add("ID:"+id+" Author:"+Author+" Name:"+Name);
                intTotalSongs++;
                index++;
            }catch (Exception z){
                index++;
            }
        }
        intTotalSongs+=jsA.size();
        jsA=new ArrayList<>();
        finishedupdate=true;
    }
    ArrayAdapter<String> adapter;
    ArrayList<String> listItems = new ArrayList<String>();

    public static Middle newInstance(int sectionNumber,String URL) {
        Middle fragment = new Middle();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        ARG_URL = URL;
        fragment.setArguments(args);
        return fragment;
    }
    //*************************END OF MIDDLE*************************

    //************************Song task***************************
    public class SongTask extends AsyncTask<Void, Void, Boolean> {

        private final int intID;
        private final Middle mid;
        private final String Url;

        SongTask(int intID, Middle mid,String Url) {
            this.intID=intID;
            this.mid = mid;
            this.Url = Url;
        }

        public boolean getting() {
            try {
                System.out.println("UrL:"+Url+" id:"+intID);
                //http://192.168.2.175:3001/login?user=dave&password=world (example)
                URL url = new URL(Url+"/songName?id="+intID);

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
                    SaveLines += output;
                }
                if(SaveLines!=""){
                    if(SaveLines=="false"){
                        return false;
                    }
                    JSONObject js = new JSONObject(SaveLines);
                    mid.ChangeSongName(js);
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

                boolean postr = getting();
                return postr;
            } catch (Exception error) {

            }

            return true;
        }

        @Override
        protected void onPostExecute ( final Boolean success){

        }

        @Override
        protected void onCancelled () {

        }
    }
    //************************End Song task**************************

    //************************Cover task*****************************
    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }
    //************************End cover task*************************

    //************************Song List task*************************
    public class SongListTask extends AsyncTask<Void, Void, Boolean> {

        private final int intID;
        private final Middle mid;
        private final String Url;

        SongListTask(int intID, Middle mid,String Url) {
            this.intID=intID;
            this.mid = mid;
            this.Url = Url;
        }

        public boolean getting() {
            try {
                System.out.println("UrL:"+Url+" id:"+intID);
                //http://192.168.2.175:3001/login?user=dave&password=world (example)
                URL url = new URL(Url+"/listSongs?id="+intID);

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
                    SaveLines += output;
                }
                if(SaveLines!=""){
                    if(SaveLines=="false"){
                        return false;
                    }
                    JSONObject js = new JSONObject(SaveLines);
                    mid.AddSongs(js);
                    mid.UpdateList();
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

                boolean postr = getting();
                return postr;
            } catch (Exception error) {

            }

            return true;
        }

        @Override
        protected void onPostExecute ( final Boolean success){

        }

        @Override
        protected void onCancelled () {

        }
    }
    //************************End************************************

}
