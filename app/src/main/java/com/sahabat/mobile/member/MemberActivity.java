package com.sahabat.mobile.member;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.sahabat.mobile.R;
import com.sahabat.mobile.helper.AppConfig;

import java.util.HashMap;
import java.util.Map;

public class MemberActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member);

        Map<String, Object> postParam = new HashMap<>();
        postParam.put("uid", "1");
        postParam.put("lat", "1");
        postParam.put("lng", "1");

        AppConfig.GETJSON("POST", AppConfig.API_TRACKER_V2, postParam, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                Log.e("TEST FUNCTION", result);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bar_main, menu);
        getMenuInflater().inflate(R.menu.bar_member, menu);
        MenuItem menuItemSearch = menu.findItem(R.id.action_search);

        SearchView searchView = (SearchView)
                MenuItemCompat.getActionView(menuItemSearch);
        return true;
        /*getMenuInflater().inflate(R.menu.bar_member, menu);*/
        //return true;
    }

}
