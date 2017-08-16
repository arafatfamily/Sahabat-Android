package com.sahabat.mobile.helper;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by GILBERT on 30/07/2017.
 */

public class AppConfig {

    public static String HTTP_DOMAIN = "http://sahabatdemokrat.org";
    public static String HTTPS_DOMAIN = "https://sahabatdemokrat.org";
    private static String AUTH_USERPW = "superadmin:Arafat120614!";
    public static String AUTH_HEADER = "Basic " + Base64.encodeToString(AUTH_USERPW.getBytes(), Base64.NO_WRAP);
    public static String URL_LOGIN = "/_api/auth";
    public static String URL_LOGIN_V2 = "/api/v2/auth";
    public static String URL_IMAGES_V2 = HTTP_DOMAIN + "/api/v2/images";
    public static String API_PROPINSI_V2 = "/api/v2/provinsi";
    public static String API_KABUPATEN_V2 = "/api/v2/kabupaten";
    public static String API_KECAMATAN_V2 = "/api/v2/kecamatan";
    public static String API_KELURAHAN_V2 = "/api/v2/kelurahan";
    public static String API_REGION_V2 = "/api/v2/region";
    public static String API_BERITA_V2 = "/api/v2/berita";
    public static String API_VALIDATE_V2 = "/api/v2/validate";
    public static String API_PENDIDIKAN_V2 = "/api/v2/pendidikan";
    public static String API_TRACKER_V2 = "/api/v2/tracker";

    public static final String TEXT_DPD = "DPD. ";
    public static final String TOPIC_GLOBAL = "global";
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";
    public static final String SHARED_PREF = "sahabat";

    private static Integer methodes(String verb) {
        switch (verb) {
            case "DELETE":
                return Request.Method.DELETE;
            case "POST":
                return Request.Method.POST;
            case "PUT":
                return Request.Method.PUT;
            default:
                return Request.Method.GET;
        }
    }

    private static String passingGETParams(String URL, Map<String, Object> params) {
        StringBuilder builder = new StringBuilder();
        for (String key : params.keySet()) {
            Object value = params.get(key);
            if (value != null) {
                try {
                    value = URLEncoder.encode(String.valueOf(value), "UTF-8");
                    if (builder.length() > 0)
                        builder.append("&");
                    builder.append(key).append("=").append(value);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        return (URL += "?" + builder.toString());
    }

    private static String Request_url(String verb, String url, Map<String, Object> params) {
        switch (verb) {
            case "GET":
                if (params != null) {
                    return passingGETParams(HTTP_DOMAIN + url, params);
                }
            default:
                return HTTP_DOMAIN + url;
        }
    }

    public interface onRespOK {
        void onSuccessResponse(String result);
    }

    public static void GETJSON(final String verb, final String url, final Map<String, Object> parameters, final onRespOK callback) {

        final String tag = "VolleyReq: " + url;

        StringRequest stringRequest = new StringRequest(methodes(verb), Request_url(verb, url, parameters), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccessResponse(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                if (error instanceof TimeoutError) {
                    Log.e(tag, "Waktu Koneksi Habis !");
                } else if (error instanceof NoConnectionError) {
                    Log.e(tag, "Jaringan Tidak Terhubung Ke Server !");
                } else if (error instanceof AuthFailureError) {
                    Log.e(tag, "Otorisasi Gagal! Silahkan Hubungi IT BPOKK PD!");
                } else if (error instanceof ServerError) {
                    Log.e(tag, "Maaf! Nomer Seluler Tidak Terdaftar!");
                } else if (error instanceof NetworkError) {
                    Log.e(tag, "Periksa Jaringan Anda!");
                } else if (error instanceof ParseError) {
                    Log.e(tag, "Pemecahan Data Gagal! Silahkan Hubungi IT BPOKK PD!");
                } else {
                    Log.e(tag, error.getMessage());
                }
            }

        }) {
            @Override //use this for multiple POST/PUT
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                for (String key : parameters.keySet()) {
                    Object value = parameters.get(key);
                    params.put(key, String.valueOf(value));
                }
                return params;
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", AUTH_HEADER);
                return headers;
            }
        };
        AppController.getInstance().addToRequestQueue(stringRequest, tag);
    }
}
