package com.sahabat.mobile;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
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

import java.util.HashMap;
import java.util.Map;

/**
 * Created by GILBERT on 29/07/2017.
 */

public class LoginActivity extends Activity {
    private static final String TAG = RegisterActivity.class.getSimpleName();
    private Button btnLogin, btnLinkToRegister, btnRequestOTP;
    private EditText OTP1, OTP2, OTP3, OTP4, OTP5, OTP6;
    private ProgressDialog pDialog;
    private SessionManager session;
    private SQLiteHandler db;
    private EditText inputHandphone;
    SharedPreferences sharedPreferences;
    AlertDialog.Builder OTPAlert;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        inputHandphone = (EditText) findViewById(R.id.handphone);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnLinkToRegister = (Button) findViewById(R.id.btnregister);
        sharedPreferences = getSharedPreferences("number", Context.MODE_PRIVATE);
        String restoredText = sharedPreferences.getString("number", null);

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        db = new SQLiteHandler(getApplicationContext());
        session = new SessionManager(getApplicationContext());
        if (session.isLoggedIn()) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            if (restoredText != null) {
                confirmOTPDialog(restoredText);
                inputHandphone.setText(restoredText);
            }
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String phone = inputHandphone.getText().toString().trim();
                if (phone.isEmpty()) {
                    Toast.makeText(getApplicationContext(), R.string.empty_phone, Toast.LENGTH_LONG).show();
                } else {
                    pDialog.setMessage("Memeriksa Otontikasi !");
                    showDialog();
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

    private void confirmOTPDialog(final String nomerHP) {
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        View confirmDialog = layoutInflater.inflate(R.layout.dialog_confirm, null);

        SharedPreferences pref = getApplicationContext().getSharedPreferences(AppConfig.SHARED_PREF, 0);
        final String firebase = pref.getString("regId", null);

        OTP1 = (EditText)confirmDialog.findViewById(R.id.otp_digit1);
        OTP2 = (EditText)confirmDialog.findViewById(R.id.otp_digit2);
        OTP3 = (EditText)confirmDialog.findViewById(R.id.otp_digit3);
        OTP4 = (EditText)confirmDialog.findViewById(R.id.otp_digit4);
        OTP5 = (EditText)confirmDialog.findViewById(R.id.otp_digit5);
        OTP6 = (EditText)confirmDialog.findViewById(R.id.otp_digit6);
        AppConfig.NEXTFORM(OTP1, OTP2, 1);
        AppConfig.NEXTFORM(OTP2, OTP3, 1);
        AppConfig.NEXTFORM(OTP3, OTP4, 1);
        AppConfig.NEXTFORM(OTP4, OTP5, 1);
        AppConfig.NEXTFORM(OTP5, OTP6, 1);
        btnRequestOTP = (Button)confirmDialog.findViewById(R.id.btnRequestOtp);

        OTPAlert = new AlertDialog.Builder(this);
        OTPAlert.setView(confirmDialog);
        OTPAlert.setCancelable(false);
        final AlertDialog alertDialog = OTPAlert.create();
        alertDialog.show();

        btnRequestOTP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLogin(nomerHP);
            }
        });

        OTP6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                pDialog.setMessage("Memeriksa Token OTP !");
                showDialog();

                final String otpInt1 = OTP1.getText().toString();
                final String otpInt2 = OTP2.getText().toString();
                final String otpInt3 = OTP3.getText().toString();
                final String otpInt4 = OTP4.getText().toString();
                final String otpInt5 = OTP5.getText().toString();
                final String otpInt6 = OTP6.getText().toString();
                String otp = otpInt1.trim() + otpInt2.trim() + otpInt3.trim() + otpInt4.trim() + otpInt5.trim() + otpInt6.trim();
                Map<String, Object> OTPparams = new HashMap<>();
                OTPparams.put("token", otp);
                OTPparams.put("firebaseId", firebase);

                AppConfig.GETJSON("GET", AppConfig.URL_LOGIN_V2 + "/" + nomerHP, OTPparams, new AppConfig.onRespOK() {
                    @Override
                    public void onSuccessResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getBoolean("success")) {
                                pDialog.setMessage("Mempersiapkan data Kader !");
                                JSONObject data = jsonObject.getJSONObject("data");
                                savingDataUser(data);
                                alertDialog.dismiss();
                            } else {
                                hideDialog();
                                Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void checkLogin(final String phone) {
        final String tag_string_req = "req_login";

        AppConfig.GETJSON("GET", AppConfig.URL_LOGIN_V2 + "/" + phone, null, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (jsonObject.getBoolean("success")) {
                        confirmOTPDialog(phone);
                        SharedPreferences.Editor seluler = sharedPreferences.edit();
                        seluler.putString("number", phone);
                        seluler.commit();
                        Toast.makeText(getApplicationContext(), jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                    }else {
                        String errorMsg = jsonObject.getString("message");
                        Toast.makeText(getApplicationContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                    hideDialog();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void savingDataUser(JSONObject data) throws JSONException {
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