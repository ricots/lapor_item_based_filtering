package com.malang.lapor.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.malang.lapor.R;
import com.malang.lapor.koneksi.LoadProfileImage;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.oop.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class regis extends AppCompatActivity {
    ProgressDialog PD;
    EditText no, nama,alamat,input_pas;
    Button btnregis;
    String emailnya;
    private TextView txtemail1;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regis);

        //tampilan regis
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width * .8), (int) (height * .6));

        no = (EditText) findViewById(R.id.input_no);
        nama = (EditText) findViewById(R.id.input_nama);
        alamat = (EditText) findViewById(R.id.input_alamat);
        input_pas = (EditText) findViewById(R.id.input_passwordnya);

        //emailnya = getIntent().getStringExtra("Email");

        //loading
        PD = new ProgressDialog(this);
        PD.setMessage("silahkan tunggu.....");
        PD.setCancelable(false);

        btnregis = (Button) findViewById(R.id.regis);

        btnregis.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regis();
            }
        });
    }

    public  void fokus_no(){
        no.requestFocus();
    }

    public  void fokus_nama(){
        nama.requestFocus();
    }

    public  void fokus_alamat(){
        alamat.requestFocus();
    }

    public  void fokus_pass(){
        input_pas.requestFocus();
    }

    //menyimpan data
    public void regis() {
        PD.show();
        final String e_ktp = no.getText().toString();
        final String e_nama = nama.getText().toString();
        final String e_alamat = alamat.getText().toString();
        final String e_pas = input_pas.getText().toString();

        if (e_ktp.equals("")) {
            Toast.makeText(regis.this, "no ktp harap di isi", Toast.LENGTH_LONG).show();
            fokus_no();
            PD.dismiss();
        } else if (e_nama.equals("")) {
            Toast.makeText(regis.this, "nama harap di isi", Toast.LENGTH_LONG).show();
            fokus_nama();
            PD.dismiss();
        } else if (e_alamat.equals("")) {
            Toast.makeText(regis.this, "alamat harap di isi", Toast.LENGTH_LONG).show();
            fokus_alamat();
            PD.dismiss();
        } else if (e_pas.equals("")) {
            Toast.makeText(regis.this, "password harap di isi", Toast.LENGTH_LONG).show();
            fokus_pass();
            PD.dismiss();
        } else {
            StringRequest postRequest = new StringRequest(Request.Method.POST, config.REGIS,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            PD.dismiss();
                            Toast.makeText(getApplicationContext(),
                                    "berhasil registrasi",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(regis.this, MainActivity.class);
                            startActivity(intent);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    PD.dismiss();
                    Toast.makeText(getApplicationContext(),
                            "gagal registrasi", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put(config.KEY_NO_KTP, e_ktp);
                    params.put(config.KEY_NAMA, e_nama);
                    params.put(config.KEY_ALAMAT_USER, e_alamat);
                    params.put(config.KEY_pass, e_pas);
                    return params;
                }
            };

            // Adding request to request queue
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(postRequest);
        }
    }

}
