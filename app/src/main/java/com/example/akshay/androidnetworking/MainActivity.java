package com.example.akshay.androidnetworking;

import android.app.ProgressDialog;
import android.media.Image;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


//TODO   We will fetch some text and images from an API. This API returns the data in the
//TODO  JSON format. We will convert this JSON form into String and will display it
//TODO  in appropriate TextViews and ImageViews.

//TODO Also add Internet Permission in Manifest file as we are fetching data from URL(i.e. Internet)

public class MainActivity extends AppCompatActivity {

    Button displayData;
    TextView titleTextView, categoryTextView;
    ImageView imageView;
    ProgressDialog progressDialog;
    String apiUrl = "http://mobileappdatabase.in/demo/smartnews/app_dashboard/jsonUrl/single-article.php?article-id=71";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //custom made method to initialize all the views
        getReferences();

        //Applying OnClickListener on the button
        displayData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Create your custom class object, which is a child class of ASyncTask and
                //call execute() method on it
                MyAsyncTask task = new MyAsyncTask();
                task.execute();

            }
        });



    }

    public void getReferences() {

        displayData = findViewById(R.id.displayData);
        titleTextView = findViewById(R.id.titleTextView);
        categoryTextView = findViewById(R.id.categoryTextView);
        imageView = findViewById(R.id.imageView);

    }



    /*
    * To perform a long required time task, like fetching some text or images from Internet,
    * we will be using AsyncTask here which performs the desired task in the background thread
     */

    /*
    * First we need to extend AsyncTask class with appropriate Parameters
     */
    private class MyAsyncTask extends AsyncTask<String,String,String>{

        String current="";
        URL url;
        HttpURLConnection urlConnection=null;


        //This method is called before doInBackground() method starts
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //We are displaying Progress Dialog till the fetching and loading of data
            // takes place in the background. It makes our app look more neat and clean.
            progressDialog = new ProgressDialog(MainActivity.this);
            progressDialog.setMessage("Please Wait");
            progressDialog.setCancelable(false);
            progressDialog.show();

        }

        @Override
        protected String doInBackground(String... strings) {

       try{

           //Make an URL object with the desired url from which you want to get the data
           url = new URL(apiUrl);

           //Create a connection with it to fetch data from that url
           urlConnection = (HttpURLConnection) url.openConnection();

           //Using InputStream to get data from the connected URL
           InputStream inputStream = urlConnection.getInputStream();

           InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

           //Fetching data from the URL and storing it in a String called current
           int data = inputStreamReader.read();
           while(data!=-1){
                current += (char)data;
                data = inputStreamReader.read();
           }

       }

        catch (MalformedURLException e) {
           e.printStackTrace();
       }
       catch (IOException e) {
           e.printStackTrace();
       }

       //Always close the opened URL connections
       finally {
           if(urlConnection!=null){
               urlConnection.disconnect();
           }
       }


            return current;
        }


        /*
        * After we get the data in JSON format, we need to just read and extract data from it.
        * Since there is no need of Internet, we don't need to perform it on background thread.
         */
        @Override
        protected void onPostExecute(String s) {

            //Dismissing the Progress Dialog once we have collected the data from the URL.
            progressDialog.dismiss();

            try{

                //Get the Array from JSON text
                JSONArray jsonArray = new JSONArray(s);

                //Choose an object from JSON Array
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                //Using this object, call getString() method and pass the appropriate keys
                //to get the desrired values
                String title = jsonObject.getString("title");
                String category = jsonObject.getString("category");
                String image = jsonObject.getString("image");

                titleTextView.setText(title);
                categoryTextView.setText(category);

                //We are using Glide Library to load the image from the URL
                Glide.with(getApplicationContext()).load(image)
                        .thumbnail(0.5f)
                        .crossFade()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imageView);


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }



}
