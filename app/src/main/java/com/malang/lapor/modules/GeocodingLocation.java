package com.malang.lapor.modules;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodingLocation {

    private static final String TAG = "GeocodingLocation";

    public static void getAddressFromLocation(final String locationAddress,
                                              final Context context, final Handler handler) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String result = null;
                String lat = null;
                String lng =null;
                try {
                    List<Address> addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0) {
                        Address address = addressList.get(0);
                        StringBuilder sb = new StringBuilder();
                        sb.append(address.getLatitude()).append("");
                        //sb.append(address.getLongitude()).append("\n");
                        result = sb.toString();
                        lat = sb.toString();

                        StringBuilder sb1 = new StringBuilder();
                        //sb.append(address.getLatitude()).append("\n");
                        sb1.append(address.getLongitude()).append("");
                        lng = sb1.toString();
                    }
                } catch (IOException e) {
                    //Toast.makeText(context,"tidak bisa konek",Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Unable to connect to Geocoder", e);
                } finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if ((result != null) && (lat != null) && (lng != null)) {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress;
                        //lat =        "Latitude and Longitude :" + lat;
                        bundle.putString("address", result);
                        bundle.putString("txt", lat);
                        bundle.putString("txt1", lng);
                        message.setData(bundle);
                    } else {
                        message.what = 1;
                        Bundle bundle = new Bundle();
                        result = "Address: " + locationAddress +
                                "\n Unable to get Latitude and Longitude for this address location.";
                        bundle.putString("address", result);
                        message.setData(bundle);
                    }
                    message.sendToTarget();
                }
            }
        };
        thread.start();
    }
}


