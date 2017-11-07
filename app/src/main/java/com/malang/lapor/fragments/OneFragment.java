package com.malang.lapor.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.malang.lapor.R;
import com.malang.lapor.adapter.adapter_laporan;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.oop.Item;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;


public class OneFragment extends Fragment {
    private List<Item> list_lapor;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter adapter_lapor;
    private RequestQueue requestQueue;
    private int requestCount = 1;
    RatingBar ratingBar;
    private TextView txtRatingValue;
    SwipeRefreshLayout swipe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_one,container,false);
        setHasOptionsMenu(true);
        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        swipe = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh_layout);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //list_lapor.clear();
                //adapter_lapor.notifyDataSetChanged();
                getData();
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

        list_lapor = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());
        getData();

        adapter_lapor = new adapter_laporan(list_lapor, getActivity());
        recyclerView.setAdapter(adapter_lapor);
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        list_lapor = new ArrayList<>();
        requestQueue = Volley.newRequestQueue(getActivity());

        getData();

        recyclerView.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView rv, int newState) {
                super.onScrollStateChanged(rv, newState);
                if (isLastItemDisplaying(recyclerView)) {
                    getData();
                }
            }
        });
        adapter_lapor = new adapter_laporan(list_lapor, getActivity());
        recyclerView.setAdapter(adapter_lapor);
    }



    private JsonArrayRequest getDataFromServer(int requestCount) {
        //final ProgressDialog loading = ProgressDialog.show(getActivity(),"Loading Data", "Please wait...",false,false);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(config.LIST_LAPORAN + String.valueOf(requestCount),
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //loading.dismiss();
                        parseData(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // progressBar.setVisibility(View.GONE);
                        Toast.makeText(OneFragment.this.getActivity(), "No More Items Available", Toast.LENGTH_SHORT).show();
                        //loading.dismiss();
                        //loading.dismiss();
                    }
                });
        jsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(
                3600, DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return jsonArrayRequest;
    }

    private void getData() {
        requestQueue.add(getDataFromServer(requestCount));
        requestCount++;
    }

    private void parseData(JSONArray array) {
        for (int i = 0; i < array.length(); i++) {
            Item lapor = new Item();
            JSONObject json = null;
            try {
                json = array.getJSONObject(i);
                lapor.setId_lapor(json.getString(config.KEY_ID_LAPOR));

                lapor.setJudul(json.getString(config.KEY_JUDUL));
                lapor.setStatus(json.getString(config.KEY_STATUS));
                lapor.setGambar(json.getString(config.KEY_GAMBAR));
                lapor.setRating(json.getInt(config.KEY_RATING));

                /*SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
                String email = pref.getString("EMAIL",null);*/
                //Toast.makeText(getActivity(),email,Toast.LENGTH_LONG).show();
                SharedPreferences pref = getActivity().getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                String ktp_user = pref.getString(config.EMAIL_SHARED_PREF, "Not Available");


                lapor.setEmail(ktp_user);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            list_lapor.add(lapor);
            swipe.setRefreshing(false);
        }

        adapter_lapor.notifyDataSetChanged();
    }

    private boolean isLastItemDisplaying(RecyclerView recyclerView) {
        if (recyclerView.getAdapter().getItemCount() != 0) {
            int lastVisibleItemPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
            if (lastVisibleItemPosition != RecyclerView.NO_POSITION && lastVisibleItemPosition == recyclerView.getAdapter().getItemCount() - 1)
                return true;
        }
        return false;
    }
}
