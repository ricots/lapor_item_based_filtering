package com.malang.lapor.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
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
import com.malang.lapor.activity.detail_laporan;
import com.malang.lapor.activity.home;
import com.malang.lapor.activity.regis;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.network.CustomVolleyRequest;
import com.malang.lapor.oop.Item;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by ACER on 2/28/2017.
 */

public class adapter_laporan extends RecyclerView.Adapter<adapter_laporan.ViewHolder> {
    private ImageLoader imageLoader;
    private Context context;
    List<Item> laporan;
    ArrayList<String> lokasinya;
   /* private final OnLecturerClickListener listener;
    private final ActivateProgressBar progressBarListener;
    List<Item> filteredLectures*/


    public adapter_laporan(List<Item> laporan, Context context){
        super();
        //Getting all the superheroes
        this.laporan = laporan;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_lapor, parent, false);
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
        holder.txtjudul.setText(lapor.getJudul());
        holder.txtproses.setText(lapor.getStatus());
        holder.emailnya.setText(lapor.getNo_ktp());
        holder.rating.setText("jumlah rating gambar " +String.valueOf(lapor.getRating()));


        holder.item = lapor;
    }

    @Override
    public int getItemCount() {
        return laporan.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public NetworkImageView imageView;
        public TextView txtjudul,id_lapor,emailnya;
        public TextView txtproses,rating;
        public RatingBar ratingBar;
        Button btnrating;
        float rtng;
        String no_ktpnya;
        Item item;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (NetworkImageView) itemView.findViewById(R.id.image_view);
            id_lapor = (TextView) itemView.findViewById(R.id.id_lapor);
            emailnya = (TextView) itemView.findViewById(R.id.emailnya);
            txtjudul = (TextView) itemView.findViewById(R.id.judul);
            txtproses= (TextView) itemView.findViewById(R.id.proses);
            rating= (TextView) itemView.findViewById(R.id.rating);
            ratingBar=(RatingBar) itemView.findViewById(R.id.ratingBar1);
            btnrating = (Button) itemView.findViewById(R.id.btnrating);


            ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                @Override
                public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                    rtng = rating * 2;
                    Toast.makeText(context, "Nilai : " + String.valueOf(rtng), Toast.LENGTH_SHORT).show();
                    SharedPreferences pref = context.getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    no_ktpnya = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");
                    /*SharedPreferences pref = context.getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                    String npm = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");
                    emailnya.setText(npm);*/
                    /*SharedPreferences pref = context.getSharedPreferences("MyPref", MODE_PRIVATE);
                    String email = pref.getString("EMAIL", null);*/
                    //Toast.makeText(context, npm, Toast.LENGTH_LONG).show();
                }
            });

            btnrating.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String e_id_lapor = id_lapor.getText().toString();
                    SharedPreferences pref = context.getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                   /* String npm = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");
                    emailnya.setText(npm);*/
                    final String e_email = no_ktpnya;
                    final Float e_rating = rtng;
                    StringRequest postRequest = new StringRequest(Request.Method.POST, config.BERI_RATING,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    //PD.dismiss();
                                    Toast.makeText(context,
                                            "berhasil merating",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //PD.dismiss();
                            Toast.makeText(context,
                                    "gagal merating", Toast.LENGTH_SHORT).show();
                        }
                    }) {
                        @Override
                        protected Map<String, String> getParams() {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put(config.KEY_ID_LAPOR, e_id_lapor);
                            params.put(config.KEY_NO_KTP_RATING, e_email);
                            params.put(config.KEY_RATING, String.valueOf(e_rating));
                            return params;
                        }
                    };

                    // Adding request to request queue
                    RequestQueue requestQueue = Volley.newRequestQueue(context);
                    requestQueue.add(postRequest);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent goDetail = new Intent(context, detail_laporan.class);
                    String id_kirim = id_lapor.getText().toString();
                    Bundle bundle = new Bundle();
                    bundle.putString("kirim",id_kirim);
                    goDetail.putExtras(bundle);
                    v.getContext().startActivity(goDetail);
                }
            });
        }
    }
}
