package com.malang.lapor.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.malang.lapor.R;
import com.malang.lapor.activity.detail_gambar_rekomendasi;
import com.malang.lapor.adapter.adapter_laporan;
import com.malang.lapor.adapter.adapter_rekomendasi;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.network.CameraActivity;
import com.malang.lapor.oop.Item;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;


public class ThreeFragment extends Fragment{
    private List<Item> list_rekomendasi;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter_lapor;
    RequestQueue requestQueue;
    private int requestCount = 1;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    SwipeRefreshLayout swipe;
    TextView total_laporan;

    public ThreeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_three,container,false);
        /*SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        String email = pref.getString("EMAIL", null);*/
        total_laporan = (TextView) v.findViewById(R.id.total_laporan);
        sp = getActivity().getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String npm = sp.getString(config.EMAIL_SHARED_PREF, "Not Available");
        //Toast.makeText(getActivity(), email, Toast.LENGTH_LONG).show();
        recyclerView = (RecyclerView)v.findViewById(R.id.recyclerView_rekomendasi);
        recyclerView.setHasFixedSize(true);

        // The number of Columns
        layoutManager = new GridLayoutManager(getActivity(), 3);
        recyclerView.setLayoutManager(layoutManager);

        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout_rekom);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list_rekomendasi.clear();
                adapter_lapor.notifyDataSetChanged();
                getdata();
                swipe.setRefreshing(false);
            }
        });

        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           //swipe.setRefreshing(true);
                       }
                   }
        );

        list_rekomendasi = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        getdata();

        adapter_lapor = new adapter_rekomendasi(list_rekomendasi, getActivity());
        recyclerView.setAdapter(adapter_lapor);
        return v;
    }


    private void getdata(){
        final ProgressDialog loading = ProgressDialog.show(getActivity(),"Loading Data", "Please wait...",false,false);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(config.LIST_REKOMENDASI,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response_gambar) {
                        loading.dismiss();

                        JSONObject tes= null;
                        try {
                            tes = response_gambar.getJSONObject(0);
                            String harga = String.valueOf("total laporan " + tes.getInt(config.KEY_JUMLAH_LAPOR));
                            total_laporan.setText(harga);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        parseData(response_gambar);
                        Log.d("isine",response_gambar.toString());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Toast.makeText(ThreeFragment.this.getActivity(), "cek internet", Toast.LENGTH_SHORT).show();
                    }
                });
        //RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(60000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(jsonArrayRequest);
    }



    private void parseData(JSONArray array){
        for(int i = 0; i<array.length(); i++) {
            Item lapor_rating = new Item();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                lapor_rating.setJudul(json.getString(config.KEY_JUDUL));
                lapor_rating.setId_lapor(json.getString(config.KEY_ID_LAPOR));
                lapor_rating.setGambar(json.getString(config.KEY_GAMBAR));
                lapor_rating.setRating(json.getInt(config.KEY_JUMLAH_REKOMENDASI));
                lapor_rating.setRating_lapor(json.getInt(config.KEY_RATING));
                //lapor_rating.setTotal(json.getString(config.KEY_JUMLAH_LAPOR));
                lapor_rating.setRekomendasi(json.getString(config.KEY_REKOMENDASI));
                Log.d("nilai ",json.getString(config.KEY_JUMLAH_LAPOR));

            } catch (JSONException e) {
                e.printStackTrace();
            }

            list_rekomendasi.add(lapor_rating);

            adapter_lapor.notifyDataSetChanged();
            swipe.setRefreshing(false);
        }

    }


   /* private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            Item lapor_rating = new Item();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                lapor_rating.setId_lapor(json.getString(config.KEY_ID_LAPOR));
                lapor_rating.setGambar(json.getString(config.KEY_GAMBAR));
                lapor_rating.setRating(json.getInt(config.KEY_RATING_REKOM));
                lapor_rating.setRating_lapor(json.getInt(config.KEY_RATING));
                lapor_rating.setTotal(json.getString(config.KEY_JUMLAH_LAPOR));
                lapor_rating.setRekomendasi(json.getString(config.KEY_REKOMENDASI));
                *//*SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                String email = pref.getString("EMAIL",null);*//*
                //Toast.makeText(getActivity(),email,Toast.LENGTH_LONG).show();

                *//*SharedPreferences pref = getActivity().getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String ktp_user = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");*//*


                //lapor.setEmail(ktp_user);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            list_rekomendasi.add(lapor_rating);
            adapter_lapor.notifyDataSetChanged();
            swipe.setRefreshing(false);
        }

       // adapter_lapor.notifyDataSetChanged();
    }*/

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }
}
