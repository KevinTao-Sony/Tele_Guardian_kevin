package com.surrey.tele_guardian;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.location.Location;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

import static android.view.Gravity.CENTER_HORIZONTAL;
import static android.view.View.VISIBLE;

public class ContactsPage extends AppCompatActivity {
    public ArrayList<JSONObject> contacts = new ArrayList<>();
    Button get_contacts;
    RelativeLayout ContactsAfterAnimationView;
    ImageView icon;
    TextView label;
    LinearLayout buttonll;
    JSONObject obj;

    private FusedLocationProviderClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_page);
        if (getArrayList("contacts")== null){
            saveArrayList(contacts, "contacts");
        }

        initView();

        new CountDownTimer(5000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                startAnimation();
            }
            @Override
            public void onFinish() {
            }
        }.start();
        Log.d("array", contacts.toString());
    }

    private void initView(){

        contacts = getArrayList("contacts");
        Collections.sort(contacts, new NameComparator());

        buttonll = findViewById(R.id.linearbutton);
        icon = findViewById(R.id.icon);
        label = findViewById(R.id.ContactsText);
        ContactsAfterAnimationView = findViewById(R.id.ContactsAfterAnimationView);
        get_contacts = findViewById(R.id.open_contacts);
        get_contacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Intent.ACTION_PICK,  ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent, 1);
            }
        });
        try {
            loadPage();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        JSONObject obj = new JSONObject();
        switch (reqCode) {
            case (1) :
                if (resultCode == Activity.RESULT_OK) {
                    Uri contactData = data.getData();
                    Cursor c =  managedQuery(contactData, null, null, null, null);
                    if (c.moveToFirst()) {
                        String name = c.getString(c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                        String id = c.getString(c.getColumnIndex(ContactsContract.Contacts._ID));

                        if (Integer.parseInt(c.getString(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0)
                        {
                            Cursor email = getContentResolver().query(ContactsContract.CommonDataKinds.Email.CONTENT_URI,null,ContactsContract.CommonDataKinds.Email.CONTACT_ID +" = "+ id,null, null);
                            while (email.moveToNext()) {
                                 final String emails = email.getString(email.getColumnIndex(ContactsContract.CommonDataKinds.Email.DATA));
                                try {
                                    obj.put("EMAIL" , emails);
                                    Log.d("email", emails);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            email.close();

                            Cursor phones = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = "+ id,null, null);
                            while (phones.moveToNext()) {

                                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                                try {
                                    obj.put("NAME" , name);
                                    obj.put("NUMBER", phoneNumber);
                                    obj.put("nominated","false");
                                    Log.d("json", obj.toString());
                                    contacts.add(obj);
                                    //new ContactsRequest().execute("post",name,phoneNumber,obj.get("EMAIL").toString());

                                    saveArrayList(contacts, "contacts");
                                    loadPage();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                break;

                            }
                            phones.close();
                        }
                    }
                }
                break;
        }
    }
    private void loadPage() throws JSONException {
        final LinearLayout main_layer = findViewById(R.id.linearLayout);
        main_layer.removeAllViews();
        Log.i("obj",contacts.toString());
        if (contacts.size() != 0){
            for (int i = 0; i < contacts.size(); i++){
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
                final Object object = contacts.get(i);
                String json = object.toString();

                final JSONObject obj = new JSONObject(json);
                final String name = obj.get("NAME").toString();
                final String email = obj.get("EMAIL").toString();
                final String number = obj.get("NUMBER").toString();
                final LinearLayout layout = new LinearLayout(getApplicationContext());

                LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(1000,200);
                layout.setOrientation(LinearLayout.VERTICAL);

                layout.setLayoutParams(params);
                final Button button = new Button(new ContextThemeWrapper(this, R.style.contactButtons), null, 0);
                button.setLayoutParams(params1);
                String buttonText = name +"\n"+ "Number: " + number +"\n" +"Email: " +email;
                button.setText(buttonText);
                button.setGravity(CENTER_HORIZONTAL);
                Button delete = new Button(this);
                delete.setText("Remove");
                if (obj.get("nominated").equals("true")){
                    button.setBackgroundResource(R.drawable.round_button_state);
                }
                final int finalI = i;

                button.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //todo remove below to replace into notifacitons
                        client = LocationServices.getFusedLocationProviderClient(ContactsPage.this);

                        client.getLastLocation().addOnSuccessListener(ContactsPage.this, new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object o) {
                                String location = o.toString().split("fused ")[1];
                                String location_Split = location.split(" hAcc")[0];
                                SmsManager smsManager = SmsManager.getDefault();
                                smsManager.sendTextMessage(number, null, "Please check on me, I have not responded to panic button, I was recently at " + location, null, null);
                                final SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ContactsPage.this);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("location", location_Split);
                                editor.apply();

                        }

                        });
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ContactsPage.this);
                        String longandlat = sharedPreferences.getString("location","");
                        String locationTXT = "I have not responded to the panic button, i was recently at " + longandlat;
                        Log.i("location",locationTXT);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(number, null,  locationTXT, null, null);
                        try {
                            obj.put("nominated","true");

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        saveArrayList(contacts, "contacts");
                        contacts.set(finalI, obj);
                        Log.i("obj",contacts.get(finalI).toString());
                        new ContactsRequest().execute("post",name,number,email);
                        button.setBackgroundResource(R.drawable.round_button_state);


                    }
                });
                delete.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {

                        contacts.remove(object);
                        saveArrayList(contacts, "contacts");
                        try {
                            loadPage();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(ContactsPage.this, "Contact Removed",
                                Toast.LENGTH_LONG).show();

                    }});
                layout.addView(button);
                layout.addView(delete);
                main_layer.addView(layout);
            }
        }
    }
    public void saveArrayList(ArrayList<JSONObject> list, String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public ArrayList<JSONObject> getArrayList(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<JSONObject>>() {}.getType();
        return gson.fromJson(json, type);
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
                ContactsAfterAnimationView.setVisibility(VISIBLE);
                label.setVisibility(VISIBLE);
                buttonll.setVisibility(VISIBLE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
    class ContactsRequest extends AsyncTask<String, Integer, String > {

        @Override
        protected String doInBackground(String... params) {
            String response = "";
            String contactsID = "";
            try {
                BufferedReader reader = null;
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(ContactsPage.this);
                String token = sharedPreferences.getString("token", "");
                //todo replace with shivs address
                URL url = new URL("http://10.77.90.236:3000/users/" + token+"/contacts"); //in the real code, there is an ip and a port
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);
                conn.setDoInput(true);
                conn.connect();
                JSONObject jsonParam = new JSONObject();
                jsonParam.put("name", params[1]);
                jsonParam.put("email", params[3]);
                jsonParam.put("mobile", params[2]);


                DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                os.writeBytes(jsonParam.toString());

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

                }
                Log.i("STATUS", contactsID);
                os.flush();
                os.close();
                conn.disconnect();

                return (contactsID);


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return contactsID;
        }

            protected void onPostExecute(String contactsID){

        }
    }
}
