package com.surrey.tele_guardian;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class SplashPage extends AppCompatActivity {
    private JSONObject obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_page);
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
//        Intent intent = new Intent(SplashPage.this, Login.class);
//        startActivity(intent);

        new login_request().execute(username,password);

    }
    class login_request extends AsyncTask<String, String, String> {
        @Override
        protected  String doInBackground(String... params) {
            String response = "";
            try {
                BufferedReader reader = null;
                //todo replace with shivs address
                URL url = new URL("http://10.77.104.212:3000/users/login"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();

                JSONObject jsonParam = new JSONObject();
                jsonParam.put("userName", params[0]);
                jsonParam.put("password", params[1]);

                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

                InputStream stream = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");

                }
                Log.d("Response: ", "> " + buffer.toString());
                try {
                    obj = new JSONObject(buffer.toString());

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + line + "\"");
                }
                String token = obj.get("_id").toString();
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(SplashPage.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.apply();

                os.flush();
                os.close();
                Log.i("token",token);
                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG" , conn.getResponseMessage());
                response =  String.valueOf(conn.getResponseCode());

                conn.disconnect();
            } catch (Exception e) {

            }


            return response;
        }
        protected void onPostExecute(String Result){
            //todo replace the equal to whatever shiv sends
            if (Result.equals("201")){
                Intent intent = new Intent(SplashPage.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Intent intent = new Intent(SplashPage.this, Login.class);
                startActivity(intent);
            }

        }

    }
}
