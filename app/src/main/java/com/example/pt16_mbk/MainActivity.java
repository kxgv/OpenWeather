package com.example.pt16_mbk;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;

import org.json.JSONException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    private String city;
    private Button buttonJ;
    private EditText editTextCity;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter, tAdapter;
    private RecyclerView.LayoutManager layoutManager;

    final static String API_KEY = "f71b94f4580396b55ad5773255b9e0e3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        editTextCity = (EditText) findViewById(R.id.editTextCity);
        buttonJ = (Button) findViewById(R.id.buttonJ);

        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        buttonJ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    OpenWeather(true,Float.parseFloat("0"),Float.parseFloat("0"));
                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        List <String> input = new ArrayList<>();
        for (int i = 0; i < 100; i++){
            input.add("Test "+i);
        }

        mAdapter = new MyAdapter(input);
        recyclerView.setAdapter(mAdapter);

//        if(savedInstanceState == null){
//            getSupportFragmentManager().beginTransaction().add(R.id.container, new WeatherFragment()).commit();
//        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == R.id.change_city){
//            showInputDialog();
//        }
//        return false;
//    }

//    private void showInputDialog(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setTitle("Change city");
//        final EditText input = new EditText(this);
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        builder.setView(input);
//        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                changeCity(input.getText().toString());
//            }
//        });
//        builder.show();
//    }

    //public void changeCity(String city){
    //    WeatherFragment wf = (WeatherFragment)getSupportFragmentManager().findFragmentById(R.id.container);
    //    wf.changeCity(city);
    //    new FavouriteCity(this).setCity(city);
    //}

    public void OpenWeather(Boolean JsonFormat, Float lat, Float lon) throws  XmlPullParserException, JSONException{
        String nomCiutat;
        String result;
        String myUrl;

        city = editTextCity.getText().toString();
        if (!city.isEmpty())
            nomCiutat = city;
        else nomCiutat = "Madrid";

        if (!JsonFormat)
            myUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" +
                    nomCiutat + "&units=metric&mode=xml&appid=" + API_KEY;
        else {
            if (lat != 0.0) {
                editTextCity.setText("Seleccionat a mapa: Latitud: " + lat + ", Longitud: " + lon);

                myUrl = "http://api.openweathermap.org/data/2.5/forecast?lat=" + lat +
                        "&lon=" + lon + "&units=metric&appid=" + API_KEY;
            } else
                myUrl = "http://api.openweathermap.org/data/2.5/forecast?q=" +
                        nomCiutat + "&units=metric&appid=" + API_KEY;

        }    Log.d("URL", "openWeather: " + lat + ", " + lon + ", " + myUrl);

        Downloader downloader = new Downloader(this);
        try {

            List<Temp> temps;
            result = downloader.execute(myUrl).get();

            if (!(result == null)){
                Parser parser = new Parser();
                if (!JsonFormat){
                    temps = parser.parsejaXml(result);
                    Temp temp = new Temp("DataProva", "22", "hot", "100");
                    temps.add(temp);
                } else {
                    temps = parser.parserJson(result);
                    Temp temp = new Temp("DataProva", "21", "hot", "100");
                    temps.add(temp);
                }

                if (temps != null) {
                    tAdapter = new CustomAdapter(temps);
                    recyclerView.setAdapter(tAdapter);
                }

                TemperaturesHelper temperaturesHelper = new TemperaturesHelper(this);
                temperaturesHelper.saveData(nomCiutat, temps);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public class Downloader extends AsyncTask<String, Void, String>{

        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;

        public Downloader (Context context){}

        @Override
        protected String doInBackground(String... params) {
            String URL = params[0];
            String result = "";
            String inputLine;

            BufferedReader reader = null;
            InputStreamReader streamReader = null;
            HttpURLConnection connection = null;

            try {

                URL myUrl = new URL(URL);

                connection = (HttpURLConnection) myUrl.openConnection();
                Log.d("URL", URL);

                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);
                connection.connect();

                streamReader = new InputStreamReader(connection.getInputStream());
                reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();

                while ((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                    Log.d("URL", URL);
                }

                result = stringBuilder.toString();
                Log.d("URL", " "+result);

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    reader.close();
                    streamReader.close();
                    connection.disconnect();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }
    }
}