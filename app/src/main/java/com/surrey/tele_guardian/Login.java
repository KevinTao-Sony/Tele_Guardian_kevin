package com.surrey.tele_guardian;

import android.Manifest;
import android.animation.Animator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class Login extends AppCompatActivity {

    private JSONObject obj;
    private ImageView icon;
    private TextView skip;
    private ProgressBar loadingProgressBar;
    private RelativeLayout rootView, afterAnimationView;
    private Button login ;
    private EditText password_box;
    private EditText username_box;
    private Button Register;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.READ_CONTACTS, Manifest.permission.SEND_SMS,Manifest.permission.READ_PHONE_STATE,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.INTERNET}, 1);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_login);
        initViews();

        preload();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startAnimation();
            }
            @Override
            public void onFinish() {
            }
        }.start();
    }

    private void initViews() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        login = findViewById(R.id.loginButton);
        icon = findViewById(R.id.icon);
        skip = findViewById(R.id.skipTextView);
        loadingProgressBar = findViewById(R.id.loadingProgressBar);
        rootView = findViewById(R.id.rootView);
        afterAnimationView = findViewById(R.id.afterAnimationView);
        Register = findViewById(R.id.register);
        loadingProgressBar.setVisibility(GONE);
        rootView.setBackgroundColor(ContextCompat.getColor(Login.this, R.color.colorSplashText));
        icon.setImageResource(R.drawable.icon);

        password_box = findViewById(R.id.passwordEditText);
        username_box = findViewById(R.id.Username);

        Register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Login.this, RegisterPage.class);
                        startActivity(intent);
                    }
                });

        skip.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                    }
                });
        login.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String USER_TXT = username_box.getText().toString();
                        String PASSWORD_TXT = password_box.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("username", USER_TXT);
                        editor.putString("password", PASSWORD_TXT);
                        editor.apply();
                        new login_request().execute(USER_TXT,PASSWORD_TXT);

                    }
                });

    }

    private void startAnimation() {
        ViewPropertyAnimator viewPropertyAnimator = icon.animate();
        viewPropertyAnimator.x(50f);
        viewPropertyAnimator.y(100f);
        viewPropertyAnimator.setDuration(1000);
        viewPropertyAnimator.setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                afterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    private void preload(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String username = sharedPreferences.getString("username", "");
        String password = sharedPreferences.getString("password", "");
        username_box.setText(username);
        password_box.setText(password);

    }
    class login_request extends AsyncTask<String, String, String>{
        @Override
        protected  String doInBackground(String... params) {
            String response = "";
            try {


                //todo replace with shivs address
                URL url = new URL("http://10.77.104.212:3000/users/login"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept","application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                BufferedReader reader = null;
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
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Login.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.apply();

                os.flush();
                os.close();

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
                Intent intent = new Intent(Login.this, MainActivity.class);
                startActivity(intent);
            }
            else{
                Toast.makeText(Login.this, "Wrong username or password",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}