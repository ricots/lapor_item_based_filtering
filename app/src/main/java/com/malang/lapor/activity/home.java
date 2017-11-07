package com.malang.lapor.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.malang.lapor.R;
import com.malang.lapor.fragments.OneFragment;
import com.malang.lapor.fragments.ThreeFragment;
import com.malang.lapor.fragments.TwoFragment;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.malang.lapor.koneksi.LoadProfileImage;
import com.malang.lapor.koneksi.config;

public class home extends AppCompatActivity {

    private ImageView imgProfilePic;
    private TextView txtName, txtEmail,txtAboutme,txtalamat;

    String personName,email,personPhotoUrl,aboutMe,alamat;
    private ConnectionResult mConnectionResult;

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.starrekom,
            R.drawable.camerarekom,
            R.drawable.listrekom
    };

//    camera tag location
    private int PICK_IMAGE_REQUEST = 1;
    Bitmap bitmap;
    File resultingFile;
    private int REQUEST_TAKE_PHOTO = 2;

    SharedPreferences sp;
    SharedPreferences.Editor spe;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        /*initialize();
        getResult();
        loadData();
        debug();*/

        txtName = (TextView) findViewById(R.id.txtName);

        sp = getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        String npm = sp.getString(config.EMAIL_SHARED_PREF, "Not Available");
        Toast.makeText(getApplicationContext(),npm,Toast.LENGTH_LONG).show();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();



        //belum_daftar();
    }

    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new OneFragment(), "rating");
        adapter.addFrag(new TwoFragment(), "lapor");
        adapter.addFrag(new ThreeFragment(), "rekomendasi");
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(adapter);

    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void belum_daftar(){
        Intent regis = new Intent(this, regis.class);
        startActivity(regis);
    }


    /**
     * Method to resolve any signin errors
     * */


    private void initialize() {
        imgProfilePic = (ImageView) findViewById(R.id.imgProfilePic);
        txtName = (TextView) findViewById(R.id.txtName);
        txtAboutme = (TextView) findViewById(R.id.txt_about);


    }

    private void debug() {
        Log.e("Message --> ","Name: "+personName+"Email: "+email+"Image URI: "+personPhotoUrl+"About Me: "+aboutMe + "alamat " + alamat);
    }

    public void loadData() {
        txtName.setText(personName);
        txtEmail.setText(email);
        txtAboutme.setText(aboutMe);
        txtalamat.setText(alamat);
        new LoadProfileImage(imgProfilePic).execute(personPhotoUrl);

    }


    public void getResult() {

        personName = getIntent().getStringExtra("Name");
        email = getIntent().getStringExtra("Email");
        personPhotoUrl = getIntent().getStringExtra("Image");
        aboutMe = getIntent().getStringExtra("About Me");
        alamat = getIntent().getStringExtra("alamat");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // moveTaskToBack(true);;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            onBackPressed();
            return true;
        }

        if (item.getItemId() == R.id.btn_sign_out1){
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage("apakah anda yakin ingin keluar ?");
            alertDialogBuilder.setPositiveButton("Yes",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                            //Getting out sharedpreferences
                            SharedPreferences preferences = getSharedPreferences(config.SHARED_PREF_NAME, Context.MODE_PRIVATE);
                            //Getting editor
                            SharedPreferences.Editor editor = preferences.edit();

                            //Puting the value false for loggedin
                            editor.putBoolean(config.LOGGEDIN_SHARED_PREF, false);

                            //Putting blank value to email
                            editor.putString(config.EMAIL_SHARED_PREF, "");

                            //Saving the sharedpreferences
                            editor.commit();

                            //Starting login activity
                            Intent intent = new Intent(home.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

            alertDialogBuilder.setNegativeButton("No",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {

                        }
                    });

            //Showing the alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}
