package com.example.weatherapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity
{
    TextView city;
    Button searchbtn;
    TextView output;



    class Weather extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... address)
        {
            try {
                URL url = new URL(address[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream ifs = connection.getInputStream();
                InputStreamReader ifsReader = new InputStreamReader(ifs);

                //retrieve data and return it as String
                int data = ifsReader.read();
                String content = "";
                char ch;

                while(data != -1)
                {
                    ch = (char) data;
                    content = content + ch;
                    data = ifsReader.read();
                }

                return content;
            } catch (MalformedURLException e)
            {
                e.printStackTrace();
            } catch (IOException e){
                e.printStackTrace();
            }
            return null;
        }

    }

    //utility function for formatting
    public String capitalize(String str)
    {
        String words[]=str.split("\\s");
        String capitalizeWord="";
        for(String w:words){
            String first=w.substring(0,1);
            String afterfirst=w.substring(1);
            capitalizeWord+=first.toUpperCase()+afterfirst+" ";
        }
        return capitalizeWord.trim();
    }

    public int convertF(String str)
    {
        double far = Double.parseDouble(str);

        far = ((1.8 * far)  + 32);

        int endFar = (int) Math.ceil(far);
        return endFar;
    }

    public void search(View view)
    {
        city = findViewById(R.id.cityPrompt);
        searchbtn = findViewById(R.id.getCity);
        output = findViewById(R.id.displayText);
        String content;

        String cityname = city.getText().toString();

        Weather weather = new Weather();
        try {
            content = weather.execute("https://openweathermap.org/data/2.5/weather?q=" + cityname + "&appid=b6907d289e10d714a6e88b30761fae22").get();
             Log.i("content",content);


            JSONObject jsonobj = new JSONObject(content);
            String weatherData = jsonobj.getString("weather");
            String mainTemp = jsonobj.getString("main");
            Log.i("weather in JSON", weatherData);
            JSONArray weatherArray = new JSONArray(weatherData);

            String main = "";
            String description = "";
            String temp = "";
            String high = "";
            String low = "";
            String feels = "";

            for(int i = 0; i < weatherArray.length();i++)
            {
                JSONObject weatherPart = weatherArray.getJSONObject(i);
                main = weatherPart.getString("main");
                description = weatherPart.getString("description");

            }


            JSONObject apimain = new JSONObject(mainTemp);
            temp = apimain.getString("temp");
            high = apimain.getString("temp_max");
            low = apimain.getString("temp_min");
            feels = apimain.getString("feels_like");
            int convertedTemp;
            convertedTemp = convertF(temp);

            int convertedHigh = convertF(high);
            int convertedLow = convertF(low);
            int convertedFeels = convertF(feels);


             //Log.i("Main: ", main);
             //Log.i("Description", description);

            description = capitalize(description);

            String completeOutput = "Weather in " + cityname + "\n";
            completeOutput = completeOutput + main + "\n" + convertedTemp + "\nHigh: " + convertedHigh + "\nLow: " + convertedLow
                    + "\nFeels Like: " + convertedFeels;

            output.setText(completeOutput);

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


    }
}
