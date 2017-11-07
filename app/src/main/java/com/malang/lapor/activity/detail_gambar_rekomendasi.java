package com.malang.lapor.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.malang.lapor.R;
import com.malang.lapor.adapter.adapter_gambar_rekomendasi;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.oop.Item;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class detail_gambar_rekomendasi extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView detail_id, detail_desk;
    String txtkirimbtn;
    ImageView img_detail;
    RequestQueue getdetail;
    private List<Item> list_rekomendasi;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter_lapor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_gambar_rekomendasi);
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar_rekomendasi);
        collapsingToolbarLayout.setTitle("rekomendasi");
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Bundle bundle = getIntent().getExtras();
        txtkirimbtn = bundle.getString("kirim");
        img_detail = (ImageView) findViewById(R.id.detail_gambar_rekomendasi);

        recyclerView = (RecyclerView) findViewById(R.id.list_rekomendasi);
        recyclerView.setHasFixedSize(true);
        layoutManager = new GridLayoutManager(this, 3);
        recyclerView.setLayoutManager(layoutManager);
        list_rekomendasi = new ArrayList<>();

        getdetail = Volley.newRequestQueue(getApplicationContext());
        getdata();

        adapter_lapor = new adapter_gambar_rekomendasi(list_rekomendasi, this);
        recyclerView.setAdapter(adapter_lapor);
        gambar_rekom();

        /*SharedPreferences pref = getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        ktp_user = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");
        Toast.makeText(this,ktp_user,Toast.LENGTH_LONG).show();*/
    }

    public void getdata(){
        final ProgressDialog loading = ProgressDialog.show(this,"Loading Data", "Please wait...",false,false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(config.DETAIL_LAPORAN + txtkirimbtn,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        loading.dismiss();
                        parseData(response);}
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(detail_gambar_rekomendasi.this, "cek internet", Toast.LENGTH_SHORT).show();
                    }
                });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getdetail.add(jsonArrayRequest);
    }

    //This method will parse json data
    private void parseData(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                //detel_brg.setMata_kuliah(json.getString(config.GAMBAR));
                String desknya = json.getString(config.KEY_DESKRIPSI);
                String gmbrnya = json.getString(config.KEY_GAMBAR);
                String lat_peta = String.valueOf(json.getString(config.KEY_LAT));
                String lng_peta = String.valueOf(json.getString(config.KEY_LNG));
                //list_jadwal.add(it_jadwal);
                Picasso.with(getApplicationContext()).load(gmbrnya).into(img_detail);
               } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void gambar_rekom(){
        final ProgressDialog loading = ProgressDialog.show(this,"Loading Data", "Please wait...",false,false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(config.GAMBAR_REKOM,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response_gambar) {
                        loading.dismiss();
                        parseData_gambar(response_gambar);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        getdetail.add(jsonArrayRequest);
    }

    //This method will parse json data
    private void parseData_gambar(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            Item item_rating = new Item();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                item_rating.setId_lapor(json.getString(config.KEY_ID_LAPOR));
                item_rating.setGambar(json.getString(config.KEY_GAMBAR));
                item_rating.setRating(json.getInt(config.KEY_RATING_REKOM));
                item_rating.setTotal(json.getString(config.KEY_JUMLAH_LAPOR));
                list_rekomendasi.add(item_rating);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            adapter_lapor.notifyDataSetChanged();
        }
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
