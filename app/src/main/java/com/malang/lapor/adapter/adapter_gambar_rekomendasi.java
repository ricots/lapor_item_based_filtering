package com.malang.lapor.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.malang.lapor.R;
import com.malang.lapor.activity.detail_gambar_rekomendasi;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.network.CustomVolleyRequest;
import com.malang.lapor.oop.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ACER on 2/28/2017.
 */

public class adapter_gambar_rekomendasi extends RecyclerView.Adapter<adapter_gambar_rekomendasi.ViewHolder> {
    private ImageLoader imageLoader;
    private Context context;
    List<Item> laporan;
    ArrayList<String> lokasinya;
   /* private final OnLecturerClickListener listener;
    private final ActivateProgressBar progressBarListener;
    List<Item> filteredLectures*/


    public adapter_gambar_rekomendasi(List<Item> laporan, Context context){
        super();
        //Getting all the superheroes
        this.laporan = laporan;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_gambar_rekomendasi, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Item lapor =  laporan.get(position);

        imageLoader = CustomVolleyRequest.getInstance(context).getImageLoader();
        imageLoader.get(lapor.getGambar(), ImageLoader.getImageListener(holder.imageView, R.mipmap.ic_launcher, android.R.drawable.ic_dialog_alert));

        holder.imageView.setImageUrl(lapor.getGambar(), imageLoader);
        holder.id_lapor.setText(lapor.getId_lapor());
        holder.ratingnya.setText("jumlah rekomendasi " + String.valueOf(lapor.getRating()));
        //holder.rating_lapor.setText("jumlah rating " + String.valueOf(lapor.getRating_lapor()));
        holder.total.setText("jumlah laporan " + lapor.getTotal());
        holder.item = lapor;
    }

    @Override
    public int getItemCount() {
        return laporan.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public NetworkImageView imageView;
        public TextView total,id_lapor,ratingnya,rating_lapor;
        public TextView txtproses,rating;
        public RatingBar ratingBar;
        Button btnrating;
        float rtng;
        String no_ktpnya;
        Item item;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.img_rekomendasi1);
            id_lapor = (TextView) itemView.findViewById(R.id.id_lapor_rekomendasi1);
            ratingnya = (TextView) itemView.findViewById(R.id.ratingnya1);
            rating_lapor = (TextView) itemView.findViewById(R.id.rating_lapor1);
            total = (TextView) itemView.findViewById(R.id.total1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                  /*  Intent goDetail = new Intent(context, detail_gambar_rekomendasi.class);
                    String id_kirim = id_lapor.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("kirim",id_kirim);
                    goDetail.putExtras(bundle);
                    v.getContext().startActivity(goDetail);*/
                    String idnya = id_lapor.getText().toString();
                    String rtngnya = rating_lapor.getText().toString();
                    SharedPreferences pref = context.getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    String ktp_user = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");
                    Toast.makeText(context,ktp_user,Toast.LENGTH_LONG).show();
                    update(idnya,rtngnya,getPosition());
                    simpan(ktp_user,idnya,getPosition());

                }
            });
        }
    }

    public void update(String idnya, String rtngnya, final int position){
        final String id = idnya;
        final String ratingnya_rekom = rtngnya;

        final ProgressDialog loading = ProgressDialog.show(context,"UPDATE DATA...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,config.UPDATE_RATING,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        loading.dismiss();
                        Toast.makeText(context, "di rekomendasi" , Toast.LENGTH_LONG).show();
                        System.out.println("hapus " + s);
                        //notifyItemRemoved(position);
                        Log.e("id","idnya " + position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        Toast.makeText(context, "gagal rekomendasi, coba lagi", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new HashMap<String, String>();
                params.put(config.KEY_ID_LAPOR, id);
                params.put(config.KEY_RATING_REKOM, ratingnya_rekom);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        requestQueue.add(stringRequest);

    }

    public void simpan(String ktp_user,String idnya, final int position){
        final String id = idnya;
        final String ktp_usernya = ktp_user;

        //final ProgressDialog loading = ProgressDialog.show(context,"UPDATE DATA...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST,config.simpan_detail_rekom,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //loading.dismiss();
                        //Toast.makeText(context, "di rekomendasi" , Toast.LENGTH_LONG).show();
                        System.out.println("simpan " + s);
                        //notifyItemRemoved(position);
                        Log.e("id","idnya " + position);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        //loading.dismiss();

                        //Showing toast
                        Toast.makeText(context, "gagal rekomendasi, coba lagi", Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() {

                Map<String,String> params = new HashMap<String, String>();
                params.put(config.KEY_NO_KTP_Rrekom, ktp_usernya);
                params.put(config.KEY_ID_LAPOR, id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(context);

        requestQueue.add(stringRequest);

    }
}
