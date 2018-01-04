package com.example.singh.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {
    ArrayList<String> celeburls = new ArrayList<String>();
    ArrayList<String> celebNames = new ArrayList<String>();
    int chosenCeleb;
    ImageView imgView;
    int locationOfAnswer = 0;
    String[] answers = new String[4];
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public class imageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(in);
                return myBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;
            try {
                url = new URL(strings[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();
                while(data!=-1){
                    char current = (char)data;
                    data = reader.read();
                    result+=current;
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgView = (ImageView)findViewById(R.id.imageView);
        button0 = (Button)findViewById(R.id.button1);
        button1 = (Button)findViewById(R.id.button2);
        button2 = (Button)findViewById(R.id.button3);
        button3 = (Button)findViewById(R.id.button4);
        DownloadTask task = new DownloadTask();
        String result = null;
        try {
            result = task.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            Pattern p = Pattern.compile("src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()) {
//                System.out.print(m.group(1));
                celeburls.add(m.group(1));
            }

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()) {
//                System.out.print(m.group(1));
                celebNames.add(m.group(1));
            }
            createQues();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void createQues()  {
        Random random = new Random();
        chosenCeleb = random.nextInt(celebNames.size());
        imageDownloader imgTask = new imageDownloader();
        Bitmap celebImage;
        try {
            celebImage = imgTask.execute(celeburls.get(chosenCeleb)).get();
            imgView.setImageBitmap(celebImage);

            locationOfAnswer = random.nextInt(4);
            int incorrectans;
            for(int i=0;i<4;i++){
                if(i==locationOfAnswer){
                    answers[i] = celebNames.get(chosenCeleb);
                } else {
                    incorrectans = random.nextInt(celeburls.size());
                    while(incorrectans == chosenCeleb){
                        incorrectans = random.nextInt(4);
                    }
                    answers[i] = celebNames.get(incorrectans);
                }
            }
            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);
//            Log.i("Contents of url", result);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void choseCeleb(View view){
        if(view.getTag().toString().equals(Integer.toString(locationOfAnswer))){
            Toast.makeText(getApplicationContext(),"Correct!!",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(),"Wrong!! It was"+celebNames.get(chosenCeleb),Toast.LENGTH_LONG).show();
        }

        createQues();
    }
}
