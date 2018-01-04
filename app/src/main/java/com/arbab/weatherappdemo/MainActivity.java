package com.arbab.weatherappdemo;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    EditText cityName;
    TextView resultText;
    TextView resultDetails;

    public void findWeather(View view){
        Log.i("City name:",cityName.getText().toString());
        DownloadTask task = new DownloadTask();
        task.execute("http://api.openweathermap.org/data/2.5/weather?q="+cityName.getText().toString()+"&appid=ab5befcc77c3cfdf0e2623ebeaa42442");
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(cityName.getWindowToken(),0);
    }


    public class DownloadTask extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... params) {
            System.out.println(params[0]);

            String result = "";
            URL url;
            HttpURLConnection connection = null;

            try{
                url = new URL(params[0]);
                connection = (HttpURLConnection)url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = in.read();

                while (data != -1){

                    char current = (char)data;
                    result += current;
                    data = reader.read();
                }

                return result;
            }
            catch (Exception e){
                e.printStackTrace();

            }

            return "done";
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                String messageFinal = "";
                String messageDetails = "";
                JSONObject jsonObj = new JSONObject(result);
                String weatherInfo = jsonObj.getString("weather");
                String cityName = jsonObj.getString("name");
                String temp = jsonObj.getString("main");
                Log.i("Temperature",temp);
                Log.i("City Name",cityName);
                JSONObject js = new JSONObject(temp);

                String temperature = js.getString("temp");
                String min_temp = js.getString("temp_min");
                String max_temp = js.getString("temp_max");

                float amount = Float.parseFloat(temperature);
                float amount_min = Float.parseFloat(min_temp);
                float amount_max = Float.parseFloat(max_temp);

                float ftemp = amount - (float)273.15;
                float ftemp_min = amount_min - (float)273.15;
                float ftemp_max = amount_max - (float)273.15;

                System.out.println("Current Temperature:"+ftemp+"'C");
                System.out.println("Current Temperature:"+ftemp_min+"'C");
                System.out.println("Current Temperature:"+ftemp_max+"'C");


                JSONArray arr = new JSONArray(weatherInfo);
                for (int i =0 ; i<arr.length(); i++){
                    JSONObject jsonPart = arr.getJSONObject(i);

                    String main;
                    main = jsonPart.getString("main");
                    String description;
                    description = jsonPart.getString("description");
                    System.out.println(main + description);
                    if(main != "" && description != "" ){
                        messageFinal = main + ": "+ description+ "\r\n";
                        System.out.println(messageFinal);
                    }

                }
                messageDetails = "Current Temperature: "+String.valueOf(ftemp)+ "'C" + "\r\n" +"Min. Temperature: "+String.valueOf(ftemp_min)+ "'C" + "\r\n"+"Max. Temperature: "+String.valueOf(ftemp_max)+ "'C" + "\r\n" ;

                if(messageFinal!=""){
                    resultText.setText(messageFinal);
                }
                if(messageDetails != ""){
                    resultDetails.setText(messageDetails);
                }

            } catch (JSONException e) {
                e.printStackTrace();

            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cityName = (EditText)findViewById(R.id.city);
        resultText = (TextView)findViewById(R.id.resultText);
        resultDetails = (TextView) findViewById(R.id.resultDetails);
    }
}
