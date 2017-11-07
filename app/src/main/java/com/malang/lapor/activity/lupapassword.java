package com.malang.lapor.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.malang.lapor.R;
import com.malang.lapor.koneksi.config;

import java.util.HashMap;
import java.util.Map;

public class lupapassword extends AppCompatActivity {
    EditText ktp,pass;
    ProgressDialog PD;
    Button forget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lupapassword);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("lupa password");

        pass = (EditText) findViewById(R.id.pass_forget);
        ktp = (EditText) findViewById(R.id.ktp_forget);
        PD = new ProgressDialog(this);

        forget = (Button) findViewById(R.id.forget_pass);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* if (pass.getText().equals("")) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                    pass.requestFocus();*/
                    //ktp.requestFocus();
                foreget_password();
            }
        });
    }

    public  void fokus_ktp(){
        pass.requestFocus();
    }

    public  void fokus_pass(){
        ktp.requestFocus();
    }

    //proses update password
    public void foreget_password() {
        PD.setMessage("silahkan tunggu...");
        PD.show();
        final String input_ktp = ktp.getText().toString();
        final String input_pass = pass.getText().toString();
        if (input_ktp.equals("")){
            Toast.makeText(lupapassword.this,"no ktp harap di isi",Toast.LENGTH_LONG).show();
            fokus_pass();
            hideDialog();
        }else if (input_pass.equals("")){
            Toast.makeText(lupapassword.this,"password harap di isi",Toast.LENGTH_LONG).show();
            fokus_ktp();
            hideDialog();
        }
        StringRequest postRequest = new StringRequest(Request.Method.POST, config.FORGET,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),"sukses update password",Toast.LENGTH_LONG).show();
                        PD.dismiss();
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
                params.put(config.KEY_pass,input_pass);
                params.put(config.KEY_NO_KTP,input_ktp);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(lupapassword.this);
        requestQueue.add(postRequest);
    }

    private void showDialog() {
        if (!PD.isShowing())
            PD.show();

    }

    private void hideDialog() {
        if (PD.isShowing())
            PD.dismiss();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
