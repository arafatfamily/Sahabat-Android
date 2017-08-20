package com.sahabat.mobile.register;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.sahabat.mobile.R;
import com.sahabat.mobile.helper.AppConfig;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class DataRegister extends Fragment implements View.OnClickListener, Spinner.OnItemSelectedListener {

    public DataRegister() {
        // Required empty public constructor
    }

    private Spinner spinnerPropinsi;
    private Spinner spinnerKabupaten;
    private Spinner spinnerKecamatan;
    private Spinner spinnerKelurahan;

    private ArrayList<String> propinsi;
    private ArrayList<String> kabupaten;
    private ArrayList<String> kecamatan;
    private ArrayList<String> kelurahan;

    private JSONArray respProvinsi;
    private JSONArray respKabupaten;
    private JSONArray respKecamatan;
    private JSONArray respKelurahan;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_data_register, container, false);
        spinnerPropinsi = (Spinner) view.findViewById(R.id.spinner_propinsi);
        spinnerKabupaten = (Spinner) view.findViewById(R.id.spinner_kabupaten);
        spinnerKecamatan = (Spinner) view.findViewById(R.id.spinner_kecamatan);
        spinnerKelurahan = (Spinner) view.findViewById(R.id.spinner_kelurahan);
        spinnerPropinsi.setOnItemSelectedListener(this);;
        propinsi = new ArrayList<String>();
        AppConfig.GETDOMISILI(null, "PROPINSI", new AppConfig.onResponse() {
            @Override
            public void onSuccess(JSONArray result) {
                for (int i = 0; i < result.length(); i++) {
                    try {
                        JSONObject json = result.getJSONObject(i);
                        propinsi.add(json.getString("nama"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                respProvinsi = result;
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, propinsi);
                spinnerPropinsi.setAdapter(adapter);
            }
        });
        return view;

    }

    @Override
    public void onClick(View v) {
        Log.d("FRAGMENT_REGISTER", String.valueOf(v));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.spinner_propinsi:
                spinnerKabupaten.setOnItemSelectedListener(this);
                kabupaten = new ArrayList<String>();
                AppConfig.GETDOMISILI(getPosition(parent, position), "KABUPATEN", new AppConfig.onResponse() {
                    @Override
                    public void onSuccess(JSONArray result) {
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                JSONObject json = result.getJSONObject(i);
                                kabupaten.add(json.getString("nama"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        respKabupaten = result;
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, kabupaten);
                        spinnerKabupaten.setAdapter(adapter);
                    }
                });
                Log.d("SPINNER_PROPINSI", getPosition(parent, position));
                break;
            case R.id.spinner_kabupaten:
                spinnerKecamatan.setOnItemSelectedListener(this);
                kecamatan = new ArrayList<String>();
                AppConfig.GETDOMISILI(getPosition(parent, position), "KECAMATAN", new AppConfig.onResponse() {
                                @Override
                    public void onSuccess(JSONArray result) {
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                JSONObject json = result.getJSONObject(i);
                                kecamatan.add(json.getString("nama"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        respKecamatan = result;
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, kecamatan);
                        spinnerKecamatan.setAdapter(adapter);
                    }
                });
                Log.d("SPINNER_Kabupaten", getPosition(parent, position));
                break;
            case R.id.spinner_kecamatan:
                spinnerKelurahan.setOnItemSelectedListener(this);
                kelurahan = new ArrayList<String>();
                AppConfig.GETDOMISILI(getPosition(parent, position), "KELURAHAN", new AppConfig.onResponse() {
                    @Override
                    public void onSuccess(JSONArray result) {
                        for (int i = 0; i < result.length(); i++) {
                            try {
                                JSONObject json = result.getJSONObject(i);
                                kelurahan.add(json.getString("nama"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        respKelurahan = result;
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, kelurahan);
                        spinnerKelurahan.setAdapter(adapter);
                    }
                });
                Log.d("SPINNER_KECAMATAN", getPosition(parent, position));
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private String getPosition(AdapterView<?> parent, int position){
        String dataId = null;
        try {
            JSONObject jsonObject;
            switch (parent.getId()) {
                case R.id.spinner_propinsi:
                    jsonObject = respProvinsi.getJSONObject(position);
                    dataId = jsonObject.getString("id_prov");
                    break;
                case R.id.spinner_kabupaten:
                    jsonObject = respKabupaten.getJSONObject(position);
                    dataId = jsonObject.getString("id_kab");
                    break;
                case R.id.spinner_kecamatan:
                    jsonObject = respKecamatan.getJSONObject(position);
                    dataId = jsonObject.getString("id_kec");
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return dataId;
    }

}
