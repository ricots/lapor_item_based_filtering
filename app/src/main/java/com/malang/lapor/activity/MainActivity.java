package com.malang.lapor.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.iid.FirebaseInstanceId;
import com.malang.lapor.R;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.oop.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    // Logcat tag
    private static final String TAG = "MainActivity";

    // Profile pic image size in pixels
    private static final int PROFILE_PIC_SIZE = 400;

    /**
     * A flag indicating that a PendingIntent is in progress and prevents us
     * from starting further intents.
     */
    private boolean mIntentInProgress;

    private boolean mSignInClicked;

    private ConnectionResult mConnectionResult;

    private Button btnSignOut, btnRevokeAccess;
    TextView text,akun;
    private FrameLayout flSrt;
    String personName,personPhotoUrl,personGooglePlusProfile,aboutMe,email,alamat;
    ProgressDialog progress_dialog;
    RequestQueue requestQueue;

    ProgressDialog PD;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    private boolean loggedIn = false;
    EditText ktp,pass;
    Button login,forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        flSrt = (FrameLayout)findViewById(R.id.flStrt);
        text = (TextView) findViewById(R.id.text_id);
        akun = (TextView) findViewById(R.id.akun);
        pass = (EditText) findViewById(R.id.pass);
        ktp = (EditText) findViewById(R.id.ktp);

        akun.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in_akun = new Intent(MainActivity.this, regis.class);
                startActivity(in_akun);
            }
        });

        sp = this.getSharedPreferences("isi data", 0);
        spe = sp.edit();
        PD = new ProgressDialog(this);

        login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String token = FirebaseInstanceId.getInstance().getToken();
                regis_user(token);
                login();
            }
        });

        forget = (Button) findViewById(R.id.forget);
        forget.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(MainActivity.this,lupapassword.class);
                startActivity(in);
            }
        });
        }

    @Override
    protected void onResume() {
        super.onResume();
        //In onresume fetching value from sharedpreference
        SharedPreferences sharedPreferences = getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

        //Fetching the boolean value form sharedpreferences
        loggedIn = sharedPreferences.getBoolean(config.LOGGEDIN_SHARED_PREF, false);

        //If we will get true
        if(loggedIn){
            //We will start the Profile Activity
            Intent intent = new Intent(MainActivity.this, home.class);
            startActivity(intent);
        }
    }

    //proses login
    private void login() {
        //Getting values from edit texts
        final String input_ktpnya = ktp.getText().toString();
        final String password = pass.getText().toString();

        showDialog();
        if ((input_ktpnya.equals(""))){
            Toast.makeText(MainActivity.this,"username harap di isi",Toast.LENGTH_LONG).show();
            hideDialog();
            //return;
        }else if ((password.equals(""))){
            Toast.makeText(MainActivity.this,"password harap di isi",Toast.LENGTH_LONG).show();
            hideDialog();

        }else {
            //PD.setMessage("Login Process...");
            //showDialog();
            //Creating a string request
            StringRequest stringRequest = new StringRequest(Request.Method.POST, config.LOGIN,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {

                            //If we are getting success from server

                            if (response.contains(config.LOGIN_SUCCESS)) {
                                hideDialog();
                                sp = MainActivity.this.getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);

                                //Creating editor to store values to shared preferences
                                spe = sp.edit();

                                //Adding values to editor
                                spe.putBoolean(config.LOGGEDIN_SHARED_PREF, true);
                                spe.putString(config.EMAIL_SHARED_PREF, input_ktpnya);

                                spe.commit();
                                gotohome();

                            } else {
                                hideDialog();
                                Toast.makeText(MainActivity.this, "Invalid username or password", Toast.LENGTH_LONG).show();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //You can handle error here if you want
                            hideDialog();
                            Toast.makeText(MainActivity.this, "The server unreachable", Toast.LENGTH_LONG).show();

                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    //Adding parameters to request
                    params.put(config.KEY_NO_KTP, input_ktpnya);
                    params.put(config.KEY_pass, password);

                    return params;
                }
            };

            Volley.newRequestQueue(this).add(stringRequest);
        }
    }

    private void gotohome() {
        Intent intent = new Intent(MainActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showDialog() {
        if (!PD.isShowing())
            PD.show();
        PD.setMessage("Login Process...");
    }

    private void hideDialog() {
        if (PD.isShowing())
            PD.dismiss();
    }

    //token hp untuk notifikasi
    public void regis_user(final String token) {
        PD.show();
        final String input_ktp = ktp.getText().toString();
        StringRequest postRequest = new StringRequest(Request.Method.POST, config.UPDATE_TOKEN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                PD.dismiss();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put(config.KEY_TOKEN,token);
                params.put(config.KEY_NO_KTP,input_ktp);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        requestQueue.add(postRequest);
    }



}