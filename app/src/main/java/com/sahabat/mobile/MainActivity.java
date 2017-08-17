package com.sahabat.mobile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.sahabat.mobile.berita.ReadingActivity;
import com.sahabat.mobile.helper.AppConfig;
import com.sahabat.mobile.helper.CircleTransform;
import com.sahabat.mobile.helper.SQLiteHandler;
import com.sahabat.mobile.helper.SessionManager;
import com.sahabat.mobile.member.MemberActivity;
import com.sahabat.mobile.pengumuman.PengumumanActivity;
import com.sahabat.mobile.register.RegisterActivity;
import com.sahabat.mobile.services.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {

    SliderLayout sliderLayout;
    HashMap<String, String> stickyNews ;
    private TextView txtRegion;
    private StringBuilder region;
    String idNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SessionManager session = new SessionManager(getApplicationContext());
        SQLiteHandler db = new SQLiteHandler(getApplicationContext());
        if (!session.isLoggedIn()) {
            session.setLogin(false);
            db.deleteUsers("user");
            Intent login = new Intent(this, LoginActivity.class);
            startActivity(login);
            finish();
        } else {
            if (!session.isLocationServiceRunning(LocationServices.class)) {
                session.serviceStart(LocationServices.class);
            }
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sliderLayout = (SliderLayout)findViewById(R.id.slider);

        Map<String, Object> getParam = new HashMap<>();
        getParam.put("data", "slider");

        AppConfig.GETJSON("GET", AppConfig.API_BERITA_V2, getParam, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONArray data = jObj.getJSONArray("data");

                    setSliderContent(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        @SuppressLint("CutPasteId")
        NavigationView nV = (NavigationView) findViewById(R.id.nav_view);
        View header = nV.getHeaderView(0);
        ImageView imgMember = (ImageView) header.findViewById(R.id.photos);
        TextView txtName = (TextView) header.findViewById(R.id.user_name);
        txtRegion = (TextView) header.findViewById(R.id.user_regional);

        HashMap<String, String> user = db.getUserDetails();
        Picasso.with(this).load(AppConfig.URL_IMAGES_V2 + "/photo/" + user.get("member_id"))
                .transform(new CircleTransform()).into(imgMember);
        txtName.setText(user.get("member_name"));
        Map<String, Object> params = new HashMap<>();
        params.put("subdistrict", user.get("sub_district_id"));

        AppConfig.GETJSON("GET", AppConfig.API_REGION_V2, params, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jObj = new JSONObject(result);
                    JSONArray data = jObj.getJSONArray("data");
                    region = new StringBuilder();
                    region.append(AppConfig.TEXT_DPD);
                    region.append(data.getJSONObject(0).getString("provinsi"));
                    txtRegion.setText(region.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setSliderContent(JSONArray jsonArray) {
        try {
            stickyNews = new HashMap<String, String>();
            for (int i = 0; i < jsonArray.length(); i++) {
                String desc = jsonArray.getJSONObject(i).getString("judul");
                String images = jsonArray.getJSONObject(i).getString("img_url");
                stickyNews.put(desc, images);
            }

            for(String name : stickyNews.keySet()){
                TextSliderView textSliderView = new TextSliderView(MainActivity.this);
                textSliderView
                        .description(name)
                        .image(stickyNews.get(name))
                        .setScaleType(BaseSliderView.ScaleType.Fit)
                        .setOnSliderClickListener(this);
                textSliderView.bundle(new Bundle());
                textSliderView.getBundle()
                        .putString("extra", stickyNews.get(name).substring(stickyNews.get(name).lastIndexOf("/")+1));
                sliderLayout.addSlider(textSliderView);
            }
            sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
            sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
            sliderLayout.setCustomAnimation(new DescriptionAnimation());
            sliderLayout.setDuration(4000); // 4 Detik
            sliderLayout.addOnPageChangeListener(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int actionId = item.getItemId();
        switch (actionId) {
            case R.id.action_profile:
                Intent profile = new Intent(this,ProfileActivity.class);
                startActivity(profile);
                break;
            case R.id.action_pengumuman:
                Intent pengumuman = new Intent(this,PengumumanActivity.class);
                startActivity(pengumuman);
                break;
            case R.id.action_settings:
                Intent settings = new Intent(this,SettingsActivity.class);
                startActivity(settings);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_member) {
            Intent member = new Intent(this,MemberActivity.class);
            startActivity(member);
        } else if (id == R.id.nav_youtube) {
            Intent youtube = new Intent(this,YoutubeActivity.class);
            startActivity(youtube);
        } else if (id == R.id.nav_twitter) {
            Intent survei = new Intent(this,TwitterActivity.class);
            startActivity(survei);
        } else if (id == R.id.nav_survei) {
            Intent survei = new Intent(this,SurveiActivity.class);
            startActivity(survei);
        } else if (id == R.id.nav_pengaduan) {
            Toast.makeText(this,"Menu Pengaduan Klik",Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_register) {
            Intent register = new Intent(this,RegisterActivity.class);
            startActivity(register);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStop() {
        sliderLayout.stopAutoCycle();
        super.onStop();
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {

        Intent read = new Intent(this, ReadingActivity.class);
        idNews = slider.getBundle().getString("extra");
        read.putExtra("news", idNews);
        startActivity(read);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider", "Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
