package com.surrey.tele_guardian;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static android.view.View.VISIBLE;

public class RegisterPage extends AppCompatActivity {

    private JSONObject obj;
    private ImageView icon;
    private EditText password, username,email,name;
    private RelativeLayout RegisterAfterAnimationView;
    private Button register;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_register_page);
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

    private void initViews() {
        final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);


        password = findViewById(R.id.RegisterPasswordEditText);
        username = findViewById(R.id.RegisterUsername);
        email = findViewById(R.id.RegisterEmail);
        name = findViewById(R.id.RegisterName);

        register = findViewById(R.id.RegisterButton);

        RegisterAfterAnimationView = findViewById(R.id.RegisterAfterAnimationView);
        icon = findViewById(R.id.icon);
        icon.setImageResource(R.drawable.icon);

        register.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //remove previous

                        SharedPreferences.Editor remove = sharedPreferences.edit();
                        remove.clear();
                        remove.commit();

                        String USER_TXT = username.getText().toString();
                        String PASSWORD_TXT = password.getText().toString();
                        String EMAIL_TXT = email.getText().toString();
                        String NAME_TXT = name.getText().toString();
                        SharedPreferences.Editor editor = sharedPreferences.edit();

                        editor.putString("username", USER_TXT);
                        editor.putString("password", PASSWORD_TXT);
                        editor.apply();
                        new Register_request().execute(USER_TXT,PASSWORD_TXT,EMAIL_TXT,NAME_TXT);

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
                RegisterAfterAnimationView.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }

    class Register_request extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            String response = "";
            try {
                BufferedReader reader = null;
                //todo replace with shivs address
                URL url = new URL("http://10.77.90.236:3000/users/register"); //in the real code, there is an ip and a port
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
                jsonParam.put("email", params[2]);
                jsonParam.put("name", params[3]);

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
                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(RegisterPage.this);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("token", token);
                editor.apply();

                os.flush();
                os.close();

                Log.i("STATUS", String.valueOf(conn.getResponseCode()));
                Log.i("MSG", conn.getResponseMessage());
                response = String.valueOf(conn.getResponseCode());
                conn.disconnect();

            } catch (Exception e) {

            }

            return response;
        }

        protected void onPostExecute(String Result) {
            //todo replace the equal to whatever shiv sends
            if (Result.equals("201")) {
                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(intent);
            } else {
                Toast.makeText(RegisterPage.this, "Invalid Details",
                        Toast.LENGTH_LONG).show();
            }

        }
    }
}
