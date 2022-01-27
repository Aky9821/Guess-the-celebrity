package com.aky.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    String source="";
    String[] imgsrc;
    String[] imgText;
    Map <Integer,Integer>mp=new HashMap<Integer,Integer>();
    int correctAnswer=-1;
    int r;
    int score,done;
    ImageView imageView;
    Button button1;
    Button button2;
    Button button3;
    Button button4;
    Button button5;
    Button button6;
    TextView textView;
    TextView scoreView;
    class DownloadSource extends AsyncTask<String, String,String>{

        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
            button1.setVisibility(View.INVISIBLE);
            button2.setVisibility(View.INVISIBLE);
            button3.setVisibility(View.INVISIBLE);
            button4.setVisibility(View.INVISIBLE);
            button5.setVisibility(View.INVISIBLE);
            button6.setVisibility(View.INVISIBLE);

        }
        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();
                InputStream inputStream=urlConnection.getInputStream();
                InputStreamReader inputStreamReader=new InputStreamReader(inputStream);
                BufferedReader bufferedReader= new BufferedReader(inputStreamReader);
                String sourcecode= bufferedReader.readLine();
                while (bufferedReader.readLine()!=null){
                    sourcecode+="\n"+bufferedReader.readLine();
                }
                return sourcecode;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }
        protected void onPostExecute(String sourcecode) {
            textView.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            button3.setVisibility(View.VISIBLE);
            button4.setVisibility(View.VISIBLE);
            findViewById(R.id.progressBar).setVisibility(View.GONE);



            source=sourcecode;
            codeSearch(source);
            try {
                start();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        void codeSearch(String source){
            Pattern p = Pattern.compile( "src=\"https://m.media-amazon.com/images/M/(.*?).jpg\"");
            Matcher m = p.matcher(source);
            imgsrc= new String[150];
            imgText=new String[150];
            int x=1;
            while (m.find()) {
                imgsrc[x]="https://m.media-amazon.com/images/M/"+m.group(1)+".jpg";
                Log.i("imgsrc : ",""+x+"  "+imgsrc[x]);
                x++;
            }
            p = Pattern.compile( "alt=\"(.*?)\"");
            m= p.matcher(source);

            x=0;
            while (m.find()) {

                imgText[x]=m.group(1);
                Log.i("imgText : ",""+x+"  "+imgText[x]);
                x++;
                if (x>100)
                    break;
            }
        }
    }

    public class ImageDownloader extends AsyncTask<String,Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = urlConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;
            }
            catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView=findViewById(R.id.imageView);
        button1=findViewById(R.id.button1);
        button2=findViewById(R.id.button2);
        button3=findViewById(R.id.button3);
        button4=findViewById(R.id.button4);
        button5=findViewById(R.id.button5);
        button6=findViewById(R.id.button6);
        textView=findViewById(R.id.textView);
        scoreView=findViewById(R.id.scoreView);
        score=0;
        done=0;


        DownloadSource downloadSource= new DownloadSource();
        downloadSource.execute("https://www.imdb.com/list/ls052283250/");
    }

    void start() throws IOException {
        Bitmap image;
        r= randomGenerator(100);
        if(!mp.containsKey(r)){
            mp.put(r,1);
            try {
                ImageDownloader imageDownloader=new ImageDownloader();
                image=imageDownloader.execute(imgsrc[r]).get();
                imageView.setImageBitmap(image);
            }
            catch (ExecutionException e) {
                e.printStackTrace();
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }
            int rb=randomGenerator(4);
            correctAnswer=rb;
            int  ro1=randomGenerator(100);;
            while(ro1==r)
                ro1=randomGenerator(100);
            int ro2 =randomGenerator(100);
            while (ro2==r||ro2==ro1)
                ro2=randomGenerator(100);
            int ro3=randomGenerator(100);
            while (ro3==r||ro3==ro1||ro3==ro2)
                ro3=randomGenerator(100);
            Log.i("df",""+rb);
            switch(rb){
                case 1:
                    button1.setText(imgText[r]);
                    button2.setText(imgText[ro1]);
                    button3.setText(imgText[ro2]);
                    button4.setText(imgText[ro3]);
                    break;
                case 2:
                    button2.setText(imgText[r]);
                    button1.setText(imgText[ro1]);
                    button3.setText(imgText[ro2]);
                    button4.setText(imgText[ro3]);
                    break;
                case 3:
                    button3.setText(imgText[r]);
                    button2.setText(imgText[ro1]);
                    button1.setText(imgText[ro2]);
                    button4.setText(imgText[ro3]);
                    break;
                case 4:
                    button4.setText(imgText[r]);
                    button2.setText(imgText[ro1]);
                    button3.setText(imgText[ro2]);
                    button1.setText(imgText[ro3]);
                    break;
            }

        }
        else
            start();
    }

    int randomGenerator(int x){
        double randNumber = Math.random();
        int r = (int) (randNumber * x )+1;
        return r;
    }
    public void onClickFunction(View v) throws IOException {
        switch(v.getId()) {
            case R.id.button1:
                checkCondition(1,v);
                break;
            case R.id.button2:
                checkCondition(2,v);
                break;
            case R.id.button3:
                checkCondition(3,v);
                break;
            case R.id.button4:
                checkCondition(4,v);
                break;
        }
    }
    void checkCondition(int x,View v){
        button1.getBackground().setAlpha(50);
        button2.getBackground().setAlpha(50);
        button3.getBackground().setAlpha(50);
        button4.getBackground().setAlpha(50);
        imageView.setAlpha(50);
        Button clickedButton= findViewById(v.getId());

        button1.setClickable(false);
        button2.setClickable(false);
        button3.setClickable(false);
        button4.setClickable(false);
        button5.setVisibility(View.VISIBLE);
        button6.setVisibility(View.VISIBLE);
        textView.setVisibility(View.VISIBLE);
        done++;
        if(x==correctAnswer){
            clickedButton.getBackground().setAlpha(255);
            clickedButton.setBackgroundColor(Color.parseColor("#3cb371"));
            textView.setText("Correct\nThe celebrity is "+imgText[r]);
            textView.setBackgroundColor(Color.parseColor("#3cb371"));
            textView.setTextColor(Color.parseColor("#f5fafc"));
            score++;
        }
        else{
            clickedButton.getBackground().setAlpha(255);
            clickedButton.setBackgroundColor(Color.parseColor("#FF6347"));
            textView.setText("Incorrect\nThe celebrity is "+imgText[r]);
            textView.setBackgroundColor(Color.parseColor("#FF6347"));
            textView.setTextColor(Color.parseColor("#f5fafc"));

        }
        scoreView.setVisibility(View.VISIBLE);
        scoreView.setText(""+score+" / "+done);
        if(done==100){
            button6.setClickable(false);
            button6.setBackgroundColor(Color.parseColor("#262626"));
            TextView exitView=findViewById(R.id.exitView);
            exitView.setVisibility(View.VISIBLE);
            exitView.setText("You have reached The End\nYou know "+score+" out of "+done+" celebrities.");
        }
    }
    public void nextFunction(View v) throws IOException {

            button1.getBackground().setAlpha(255);
            button2.getBackground().setAlpha(255);
            button3.getBackground().setAlpha(255);
            button4.getBackground().setAlpha(255);
            imageView.setAlpha(255);
            button5.setVisibility(View.INVISIBLE);
            button6.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            button1.setClickable(true);
            button2.setClickable(true);
            button3.setClickable(true);
            button4.setClickable(true);
            button1.setBackgroundColor(Color.parseColor("#EDE5E5"));
            button2.setBackgroundColor(Color.parseColor("#EDE5E5"));
            button3.setBackgroundColor(Color.parseColor("#EDE5E5"));
            button4.setBackgroundColor(Color.parseColor("#EDE5E5"));
            start();
    }
    public void exitFunction(View v){
        this.finishAffinity();
    }
}