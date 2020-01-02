package com.surrey.tele_guardian;

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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.view.View.VISIBLE;

public class Account extends AppCompatActivity {
    private RelativeLayout AccountAfterAnimationView;
    private ImageView icon;
    TextView username, name, email;
    JSONObject obj;
    String Name ,Username,Email;
    Button LOGOUT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_account);
        new Account_request().execute();
        initViews();



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
                AccountAfterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
    private void initViews() {

        LOGOUT = findViewById(R.id.Logout);
        AccountAfterAnimationView = findViewById(R.id.AccountAfterAnimationView);
        icon = findViewById(R.id.icon);
        icon.setImageResource(R.drawable.icon);

        username = findViewById(R.id.AccountUsername);
        name = findViewById(R.id.AccountName);
        email = findViewById(R.id.AccountEmail);

        username.setText(Username);
        name.setText(Name);
        email.setText(Email);

        LOGOUT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Account.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("username");
                editor.remove("password");
                editor.commit();
                Intent intent = new Intent(Account.this, Login.class);
                startActivity(intent);
            }});

    }
    class Account_request extends AsyncTask<String, String, String> {
        @Override

        protected String doInBackground(String... params) {
            String response = "";
            try {
                BufferedReader reader = null;
                //todo replace with shivs address
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(Account.this);
                String token = sharedPreferences.getString("token", "");
                URL url = new URL("http://10.77.104.212:3000/users/"+token); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Content-Type", "application/json");
                //conn.setRequestProperty("Accept","application/json");

                conn.connect();


                InputStream stream = conn.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line+"\n");

                }
                try {
                    obj = new JSONObject(buffer.toString());

                } catch (Throwable t) {
                    Log.e("My App", "Could not parse malformed JSON: \"" + line + "\"");
                }
                Name = obj.get("name").toString();
                Username = obj.get("userName").toString();
                Email = obj.get("email").toString();


                response = String.valueOf(conn.getResponseCode());
                conn.disconnect();

            } catch (Exception e) { }

            return response;
        }

        protected void onPostExecute(String Result) {
            Log.d("results",Result);
            //todo replace the equal to whatever shiv sends
            if (Result.equals("200")) {
                initViews();
            } else {
                Toast.makeText(Account.this, "ERROR",
                        Toast.LENGTH_LONG).show();
            }

        }
    }

}
