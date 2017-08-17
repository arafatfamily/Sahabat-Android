package com.sahabat.mobile.berita;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sahabat.mobile.R;
import com.sahabat.mobile.helper.AppConfig;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReadingActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener {
    private static final int PERCENTAGE_TO_SHOW_IMAGE = 75;
    private View mFab;
    private int mMaxScrollSize;
    private boolean mIsImageHidden;
    CollapsingToolbarLayout ctTitle;
    ImageView ivHeader;
    TextView tvHeader, tvContent;
    String idNews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reading);

        mFab = findViewById(R.id.share_fab);
        ctTitle = (CollapsingToolbarLayout) findViewById(R.id.news_collapsing);
        ivHeader = (ImageView)findViewById(R.id.news_images);
        tvHeader = (TextView) findViewById(R.id.txt_contributor);
        tvContent = (TextView) findViewById(R.id.txt_html_content);
        Log.d("IV", String.valueOf(ivHeader));
        Log.d("IV", String.valueOf(tvHeader));
        Log.d("IV", String.valueOf(tvContent));
        idNews = getIntent().getExtras().getString("news");
        Map<String, Object> params = new HashMap<>();
        params.put("data", "detail");
        params.put("id", idNews);

        AppConfig.GETJSON("GET", AppConfig.API_BERITA_V2, params, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject res = new JSONObject(result);
                    if(res.getBoolean("success")) {
                        JSONArray data = res.getJSONArray("data");
                        displayNews(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.news_toolbar);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onBackPressed();
            }
        });

        AppBarLayout appbar = (AppBarLayout) findViewById(R.id.news_appbar);
        appbar.addOnOffsetChangedListener(this);
    }

    public void displayNews(JSONArray json) throws JSONException {
        JSONObject data = json.getJSONObject(0);
        ctTitle.setTitle(data.getString("judul"));
        Picasso.with(this).load(data.getString("img_url")).into(ivHeader);
        tvHeader.setText(AppConfig.DATEID("1986-09-12 00:00:00"));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            tvContent.setText(Html.fromHtml(data.getString("isi_berita"), Html.FROM_HTML_MODE_COMPACT));
        } else {
            tvContent.setText(Html.fromHtml(data.getString("isi_berita")));
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        if (mMaxScrollSize == 0)
            mMaxScrollSize = appBarLayout.getTotalScrollRange();
        int currentScrollPercentage = (Math.abs(i)) * 100 / mMaxScrollSize;
        if (currentScrollPercentage >= PERCENTAGE_TO_SHOW_IMAGE) {
            if (!mIsImageHidden) {
                mIsImageHidden = true;
                ViewCompat.animate(mFab).scaleY(0).scaleX(0).start();
            }
        }
        if (currentScrollPercentage < PERCENTAGE_TO_SHOW_IMAGE) {
            if (mIsImageHidden) {
                mIsImageHidden = false;
                ViewCompat.animate(mFab).scaleY(1).scaleX(1).start();
            }
        }
    }

    public static void start(Context c) {
        c.startActivity(new Intent(c, ReadingActivity.class));
    }
}