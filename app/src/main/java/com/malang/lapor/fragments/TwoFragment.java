package com.malang.lapor.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.malang.lapor.R;
import com.malang.lapor.koneksi.config;
import com.malang.lapor.modules.GPSTracker;
import com.malang.lapor.modules.GeocodingLocation;
import com.malang.lapor.modules.RealPathUtil;

import android.Manifest;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.support.v7.app.AlertDialog;
import android.content.CursorLoader;
import android.database.Cursor;

import org.json.JSONException;
import org.json.JSONObject;
import android.os.Handler;
import android.os.Message;


public class TwoFragment extends Fragment{
ImageView upload_gambar;
    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    File resultingFile;
    static final int REQUEST_TAKE_PHOTO = 11111;
    double slat, slng;
    String path;
    TextView tv_coordinat,email_lapor,kecamatan;
    EditText input_alamat,input_judul,input_detail;
    Button upload;
    Uri filePath;
    SharedPreferences sp;
    SharedPreferences.Editor spe;
    com.android.volley.RequestQueue RequestQueue;
    GPSTracker gpsx;
    private Bitmap oldDrawable;
    private int STORAGE_PERMISSION_CODE = 23;
    public static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    public static final String ALLOW_KEY = "ALLOWED";
    public static final String CAMERA_PREF = "camera_pref";
    ProgressDialog PD;

    public TwoFragment() {
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
        View v = inflater.inflate(R.layout.fragment_two,container,false);
        /*SharedPreferences pref = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        String email = pref.getString("EMAIL", null);
        Toast.makeText(getActivity(), email, Toast.LENGTH_LONG).show();*/
        sp = getActivity().getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String npm = sp.getString(config.EMAIL_SHARED_PREF, "Not Available");
        RequestQueue = Volley.newRequestQueue(getActivity());

        upload_gambar = (ImageView) v.findViewById(R.id.upload_gambar);
        tv_coordinat = (TextView) v.findViewById(R.id.tvCoordinat);
        upload_gambar.setImageResource(R.drawable.noimage);
        input_judul = (EditText) v.findViewById(R.id.input_judul);
        input_detail = (EditText) v.findViewById(R.id.input_detail);
        input_alamat = (EditText) v.findViewById(R.id.input_alamat);
        email_lapor = (TextView) v.findViewById(R.id.email_lapor);
        kecamatan = (TextView) v.findViewById(R.id.kecamatan);
        email_lapor.setText(npm);

        PD = new ProgressDialog(getActivity());
        PD.setMessage("proses upload.....");
        PD.setCancelable(false);

        upload = (Button) v.findViewById(R.id.upload);
        upload.setEnabled(false);
        upload.setBackgroundColor(Color.RED);
        upload_gambar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                simpan_laporan();
            }
        });

        if(isReadStorageAllowed()){
            //If permission is already having then showing the toast
            Toast.makeText(TwoFragment.this.getActivity(),"You already have the permission",Toast.LENGTH_LONG).show();
            //Existing the method with return
        }

        //If the app has not the permission then asking for the permission
        requestStoragePermission();

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            if (getFromPref(getActivity(), ALLOW_KEY)) {

                showSettingsAlert();

            } else if (ContextCompat.checkSelfPermission(getActivity(),
                    Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.CAMERA)) {
                    showAlert();
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{Manifest.permission.CAMERA},
                            MY_PERMISSIONS_REQUEST_CAMERA);
                }
            }
        } else {
            //openCamera();
        }
        return v;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 15, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            //Bitmap imageBitmap = (Bitmap) extras.get("data");
            //img.setImageBitmap(imageBitmap);
            bitmap = (Bitmap) extras.get("data");
            upload_gambar.setImageBitmap(bitmap);
            filePath = data.getData();
            LocationToAddress();
            upload.setEnabled(true);
            upload.setBackgroundColor(Color.BLUE);
            }
        else if (data != null) {
            String selectedImagePath;
            Uri selectedImageUri = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), selectedImageUri);
                selectedImagePath = RealPathUtil.getPath(getActivity().getApplicationContext(), selectedImageUri);
                Log.i("Image File Path", ""+selectedImagePath);
                tv_coordinat.setText(selectedImagePath);
                //upload_gambar.setImageBitmap(bitmap);
                show_GEO_IMAGE(selectedImagePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                upload_gambar.setImageBitmap(bitmap);
                upload.setEnabled(true);
                upload.setBackgroundColor(Color.BLUE);
            }

        } else {
            Toast.makeText(getActivity(), "Try Again!!", Toast.LENGTH_SHORT).show();
            upload.setEnabled(false);
            upload.setBackgroundColor(Color.RED);
        }

    }

    public void showFileChooser() {
        final CharSequence[] items = {"Ambil Foto","Pilih dari Galeri",
                "Batal"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Tambah Foto");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Ambil Foto")) {
                    Intent cameraIntent = new Intent(
                            android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(
                            cameraIntent,
                            REQUEST_TAKE_PHOTO);

                } else if (items[item].equals("Pilih dari Galeri")) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
                } else if (items[item].equals("Batal")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private void setFullImageFromFilePath(String imagePath, ImageView imageView) {
        // Get the dimensions of the View
        int targetW = imageView.getWidth();
        int targetH = imageView.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, bmOptions);
        imageView.setImageBitmap(bitmap);
    }

    public void show_GEO_IMAGE(String fileLocation){
        String lat="", latR="",lng="", lngR="";
        try{
            ExifInterface exif = new ExifInterface(fileLocation);
            lat = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
            latR = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
            lng = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
            lngR = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);
        }catch (Exception e){
            e.printStackTrace();
        }

        if(lat != null && latR != null && lng != null && lngR != null){
            slat = dms2Dec(lat);
            slng = dms2Dec(lng);

            slat = latR.contains("S") ? -slat : slat;
            slng = lngR.contains("W") ? -slng : slng;

            Toast.makeText(getActivity(), "lat : " + slat + "\n" + "log = " + slng, Toast.LENGTH_SHORT).show();
            //script konversi lat dan lang -> alamat
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            try {
                addresses = geocoder.getFromLocation(slat, slng, 1);
                String address = addresses.get(0).getAddressLine(0);
                String localityString = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                input_alamat.setText(address + ", " + " " + localityString + ", "  + state);

                //texview kecamatan
                kecamatan.setText(localityString);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(getActivity(), "Gambar ini tidak memiliki coordinat GPS", Toast.LENGTH_SHORT).show();
            input_alamat.setText("");
        }
    }


    public  void add_TAG_GEO (String fileLocation, double lat, double lng){
        try{
            ExifInterface exif = new ExifInterface(fileLocation);
            exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE,dec2DMS(lat));
            exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE,dec2DMS(lng));

            if (lat > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,"N");

            }else {
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,"S");
            }
            if (lng > 0) {
                exif.setAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF,"E");

            }else{
                exif.setAttribute(ExifInterface.TAG_GPS_LATITUDE_REF,"W");
            }

            exif.saveAttributes();

            Toast.makeText(getActivity(), "Berhasil Menambahkan Coordinat", Toast.LENGTH_SHORT).show();

        }catch (Exception e){

        }
    }

    private String dec2DMS(double coordinate) {

        // Get absolute value of the coordinate (if negative, make it positive).
        coordinate = Math.abs(coordinate);  // -105.9876543 -> 105.9876543

        // Place degrees into String.
        String stringCoord = Integer.toString((int)coordinate) + "/1,";  // 105/1,

        // Place minutes into String.
        coordinate = (coordinate % 1) * 60;  // .987654321 * 60 = 59.259258
        stringCoord = stringCoord + Integer.toString((int)coordinate) + "/1,";  // 105/1,59/1,

        // Place seconds into String.
        coordinate = (coordinate % 1) * 60000;  // .259258 * 60000 = 15555
        stringCoord = stringCoord + Integer.toString((int)coordinate) + "/1000";  // 105/1,59/1,15555/1000

        return stringCoord;
    }


    private double dms2Dec(String sDMS) {
        double dRV = 999.0;
        try {
            String[] DMSs = sDMS.split(",", 3);
            String s[] = DMSs[0].split("/", 2);
            dRV = (new Double(s[0]) / new Double(s[1]));
            s = DMSs[1].split("/", 2);
            dRV += ((new Double(s[0]) / new Double(s[1])) / 60);
            s = DMSs[2].split("/", 2);
            dRV += ((new Double(s[0]) / new Double(s[1])) / 3600);

        } catch (Exception e) {

        }
        return dRV;

    }

    public void simpan_laporan(){
        PD.show();
        String backgroundImageName = String.valueOf(upload_gambar.getTag());
        final String image = getStringImage(bitmap);
        final String input_jdl = input_judul.getText().toString();
        final String input_dtl = input_detail.getText().toString();
        final String input_almt = input_alamat.getText().toString();
        final String input_email_lapor = email_lapor.getText().toString();
        final String deteksi = kecamatan.getText().toString();

        if (deteksi.equalsIgnoreCase("sukun")) {
            Toast.makeText(getActivity(), "maaf gambar anda kirim bukan termasuk kabupaten", Toast.LENGTH_LONG).show();
            PD.dismiss();
        }else if (deteksi.equalsIgnoreCase("kedungkandang")){
            Toast.makeText(TwoFragment.this.getActivity(),"judul harap di isi",Toast.LENGTH_LONG).show();
            PD.dismiss();
        }else if (input_jdl.equals("")){
            Toast.makeText(TwoFragment.this.getActivity(),"judul harap di isi",Toast.LENGTH_LONG).show();
            PD.dismiss();
        }else if (input_dtl.equals("")){
            Toast.makeText(TwoFragment.this.getActivity(),"detail harap di isi",Toast.LENGTH_LONG).show();
            PD.dismiss();
        }else if(image.equals("") || bitmap == null){
            Toast.makeText(TwoFragment.this.getActivity(), "upload image terlebih dahulu", Toast.LENGTH_LONG).show();
            PD.dismiss();
            // new RegisterAsyntaskNew().execute();
            }else{
      //final String image = getStringImage(bitmap);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, config.SIMPAN_LAPORAN,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        PD.dismiss();
                        Toast.makeText(getActivity(), "upload sukses" , Toast.LENGTH_LONG).show();
                        System.out.println("hasilnya "+ s);
                        reset();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        PD.dismiss();
                        Toast.makeText(getActivity(), volleyError.toString(), Toast.LENGTH_LONG).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new Hashtable<String, String>();
                params.put(config.KEY_NO_KTP, input_email_lapor);
                params.put(config.KEY_JUDUL, input_jdl);
                params.put(config.KEY_DESKRIPSI, input_dtl);
                params.put(config.KEY_GAMBAR, image);
                params.put(config.KEY_ALAMAT, input_almt);
                params.put(config.KEY_LAT, String.valueOf(slat));
                params.put(config.KEY_LNG, String.valueOf(slng));
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);

    }}

    public void reset(){
        input_judul.setText("");
        input_alamat.setText("");
        input_detail.setText("");
        upload_gambar.setImageResource(R.drawable.noimage);
        tv_coordinat.setText("cari gambar");
        upload.setBackgroundColor(Color.RED);
    }

    public void LocationToAddress(){

//
        gpsx = new GPSTracker(getActivity());


        slat = gpsx.getLatitude();
        slng = gpsx.getLongitude();

        final String lattadd = String.valueOf(slat);
        final String logtadd = String.valueOf(slng);


        //    JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng=23.781522,"90.3704991&key=AIzaSyBma_A78YGbZwGav3SR3vSGoAXka8FGFzQ", new Response.Listener<JSONObject>() {

        JsonObjectRequest request = new JsonObjectRequest("https://maps.googleapis.com/maps/api/geocode/json?latlng="+lattadd+","+logtadd+"&key=AIzaSyDnX4KCoDJv9bis5NoVrLQzuQwXE2U4KVg",
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {

                try {
                    String getaddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                    input_alamat.setText(getaddress);
                    //String address = input_alamat.getText().toString();
                    //if (input_alamat.getText().equals(getaddress)){

                        GeocodingLocation locationAddress = new GeocodingLocation();
                        locationAddress.getAddressFromLocation(getaddress,
                                getActivity(), new GeocoderHandler());
                    Toast.makeText(getActivity(), "lat " + lattadd + "\n" + "long " + logtadd, Toast.LENGTH_SHORT).show();
                    //}

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        RequestQueue.add(request);
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress,locationlat = null,locationlng = null;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    locationlat = bundle.getString("txt");
                    locationlng = bundle.getString("txt1");
                    break;
                default:
                    //locationAddress = null;
            }
            //hideDialog();
            //Toast.makeText(getActivity(), "lat " + locationlat + "\n" + "long " + locationlng, Toast.LENGTH_SHORT).show();
            //lat.setText(locationlat);
            //lng.setText(locationlng);
        }
    }

    private boolean isReadStorageAllowed() {
        //Getting the permission status
        int result = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE);

        //If permission is granted returning true
        if (result == PackageManager.PERMISSION_GRANTED)
            return true;

        //If permission is not granted returning false
        return false;
    }

    //Requesting permission
    private void requestStoragePermission(){

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            //If the user has denied the permission previously your code will come to this block
            //Here you can explain why you need this permission
            //Explain here why you need this permission
        }

        //And finally ask for the permission
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},STORAGE_PERMISSION_CODE);
    }

    //This method will be called when the user will tap on allow or deny
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_CAMERA: {
                for (int i = 0, len = permissions.length; i < len; i++) {
                    String permission = permissions[i];
                    if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                        boolean showRationale =
                                ActivityCompat.shouldShowRequestPermissionRationale
                                        (getActivity(), permission);
                        if (showRationale) {
                            showAlert();
                        } else if (!showRationale) {
                            // user denied flagging NEVER ASK AGAIN
                            // you can either enable some fall back,
                            // disable features of your app
                            // or open another dialog explaining
                            // again the permission and directing to
                            // the app setting
                            saveToPreferences(getActivity(), ALLOW_KEY, true);
                        }}}}}


        //Checking the request code of our request
        if(requestCode == STORAGE_PERMISSION_CODE){

            //If permission is granted
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                //Displaying a toast
                Toast.makeText(getActivity(),"Permission granted now you can read the storage",Toast.LENGTH_LONG).show();
            }else{
                //Displaying another toast if permission is not granted
                Toast.makeText(getActivity(),"Oops you just denied the permission",Toast.LENGTH_LONG).show();
            }
        }
    }

    //camera
    public void showAlert(){
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        getActivity().finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSIONS_REQUEST_CAMERA);

                    }
                });
        alertDialog.show();
    }

    private void showSettingsAlert() {
        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("App needs to access the Camera.");
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "DONT ALLOW",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        //finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "SETTINGS",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        startInstalledAppDetailsActivity(getActivity());

                    }
                });
        alertDialog.show();
    }

    public static void startInstalledAppDetailsActivity(final Activity context) {
        if (context == null) {
            return;
        }
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + context.getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        context.startActivity(i);
    }

    private void openCamera() {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        startActivity(intent);
    }

    public static void saveToPreferences(Context context, String key,
                                         Boolean allowed) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = myPrefs.edit();
        prefsEditor.putBoolean(key, allowed);
        prefsEditor.commit();
    }

    public static Boolean getFromPref(Context context, String key) {
        SharedPreferences myPrefs = context.getSharedPreferences
                (CAMERA_PREF, Context.MODE_PRIVATE);
        return (myPrefs.getBoolean(key, false));
    }
}
