package com.sahabat.mobile;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.sahabat.mobile.helper.AppConfig;
import com.sahabat.mobile.helper.SQLiteHandler;
import com.sahabat.mobile.helper.SessionManager;
import com.sahabat.mobile.register.RegisterActivity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by GILBERT on 29/07/2017.
 */

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin;
    private Button btnLinkToRegister;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private EditText inputHandphone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputHandphone = (EditText) findViewById(R.id.handphone);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnregister);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String phone = inputHandphone.getText().toString().trim();
                if (phone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.empty_phone, Toast.LENGTH_LONG).show();
                } else {
                    checkLogin(phone);
                }
            }
        });

        btnLinkToRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        RegisterActivity.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void checkLogin(final String phone) {
        final String tag_string_req = "req_login";
        pDialog.setMessage("Memeriksa Otorisasi !");
        showDialog();

        AppConfig.GETJSON("GET", AppConfig.URL_LOGIN_V2 + "/" + phone, null, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONObject data = jsonObject.getJSONObject("data");
                    if (jsonObject.getBoolean("success")) {
                        int member_id = data.getInt("id");
                        String membership_id = data.getString("membership_id");
                        String member_name = data.getString("member_name");
                        String gender = data.getString("gender");
                        String birth_place = data.getString("birth_place");
                        String date_of_birth = data.getString("date_of_birth");
                        String is_married = data.getString("is_married");
                        String blood_type = data.getString("blood_type");
                        String occupation = data.getString("occupation");
                        String religion = data.getString("religion");
                        int last_education_id = data.getInt("last_education_id");
                        String address = data.getString("address");
                        String home_number = data.getString("home_number");
                        String rt = data.getString("rt");
                        String rw = data.getString("rw");
                        String sub_district_id = data.getString("sub_district_id");
                        int postal_code = data.getInt("postal_code");
                        String is_domisili = data.getString("is_domisili");
                        String couple_name = data.getString("couple_name");
                        String children_name = data.getString("children_name");
                        String cellular_phone_number = data.getString("cellular_phone_number");
                        String home_phone_number = data.getString("home_phone_number");
                        String email = data.getString("email");
                        String facebook = data.getString("facebook");
                        String twitter = data.getString("twitter");
                        String registered_time = data.getString("registered_time");
                        String reference = data.getString("reference");
                        String last_print = data.getString("last_print");
                        String is_have_position = data.getString("is_have_position");
                        String is_other_position = data.getString("is_other_position");

                        db.addUser(member_id, membership_id, member_name, gender, birth_place, date_of_birth, is_married, blood_type, occupation, religion, last_education_id, address, home_number, rt, rw, sub_district_id, postal_code, is_domisili, couple_name, children_name, cellular_phone_number, home_phone_number, email, facebook, twitter, registered_time, reference, last_print, is_have_position, is_other_position);
                        checkAutostartPermission();
                    }else {
                        String errorMsg = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    private void redirectMain() {
        session.setLogin(true);
        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
        startActivity(intent);
        hideDialog();
        finish();
    }

    private void checkAutostartPermission() {
        int result = ContextCompat.checkSelfPermission(LoginActivity.this, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        if (result == PackageManager.PERMISSION_GRANTED) {
            redirectMain();
        } else {
            db.deleteUsers("user");
            //requestAutostartPermission();
        }
    }

    private void requestAutostartPermission() {
        Log.d("SAHABAT", "PERMISSION REQUESTED");
        ActivityCompat.requestPermissions(LoginActivity.this, new String[] {
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED
        }, PackageManager.PERMISSION_GRANTED);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PackageManager.PERMISSION_GRANTED:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
            case PackageManager.PERMISSION_DENIED:
                Log.e("value", "Permission Denied, You cannot use local drive .");
                break;
        }
    }
}