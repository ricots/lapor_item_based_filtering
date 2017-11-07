package com.malang.lapor.activity;

import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.location.Location;
import android.location.LocationListener;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.malang.lapor.R;
import com.malang.lapor.koneksi.config;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class detail_laporan extends AppCompatActivity implements OnMapReadyCallback{
    CollapsingToolbarLayout collapsingToolbarLayout;
    TextView detail_id, detail_desk;
    ImageView img_detail;
    RequestQueue getdetail;
    GoogleMap mGoogleMap;
    GoogleApiClient mGoogleApiClient;
    LatLng latLng;
    Marker currLocationMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_laporan);
        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("kerusakan jalan");
        setSupportActionBar(toolbar1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        final Bundle bundle = getIntent().getExtras();
        String txtkirimbtn = bundle.getString("kirim");

        MapFragment mFragmentMap = MapFragment.newInstance();
        FragmentTransaction mTranscation = getFragmentManager().beginTransaction();
        mTranscation.add(R.id.map_detail,mFragmentMap);
        mTranscation.commit();
        mFragmentMap.getMapAsync(this);

        detail_id = (TextView) findViewById(R.id.detail_id);
        detail_desk = (TextView) findViewById(R.id.detail_deskripsi);
        img_detail = (ImageView) findViewById(R.id.detail_gambar_lapor);
        detail_id.setText(txtkirimbtn);
        getdetail = Volley.newRequestQueue(getApplicationContext());
        getdata();
    }

    public void getdata(){
        final ProgressDialog loading = ProgressDialog.show(this,"Loading Data", "Please wait...",false,false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(config.DETAIL_LAPORAN + detail_id.getText().toString(),
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
                        Toast.makeText(detail_laporan.this, "cek internet", Toast.LENGTH_SHORT).show();
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
                detail_desk.setText(desknya);
                mGoogleMap.addMarker(new MarkerOptions().position(
                        new LatLng(Double.parseDouble(lat_peta),Double.parseDouble(lng_peta))));
                  //mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(json.getDouble(config.KEY_LAT), json.getDouble(config.KEY_LNG)));
            } catch (JSONException e) {
                e.printStackTrace();
            }

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*mGoogleMap = googleMap;
        mGoogleMap.setMyLocationEnabled(true);
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        //buildGoogleApiClient();
        LatLng Pusat = new LatLng(-7.9784696,112.5617418);
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Pusat,14));

        mGoogleApiClient.connect();*/
        mGoogleMap = googleMap;
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        // Add a marker in Sydney and move the camera
        LatLng TutorialsPoint = new LatLng(21, 57);
        /*mGoogleMap.addMarker(new
                MarkerOptions().position(TutorialsPoint).title("Tutorialspoint.com"));
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(TutorialsPoint));*/
    }

}
