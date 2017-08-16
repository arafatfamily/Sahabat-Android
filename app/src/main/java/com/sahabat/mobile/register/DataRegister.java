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
    /*private Spinner spinner_kabupaten;
    private Spinner spinner_kecamatan;
    private Spinner spinner_kelurahan;*/

    private ArrayList<String> propinsi;
    /*private ArrayList<String> kabupaten;
    private ArrayList<String> kecamatan;
    private ArrayList<String> kelurahan;*/

    private JSONArray response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*kabupaten = new ArrayList<String>();
        kecamatan = new ArrayList<String>();
        kelurahan = new ArrayList<String>();*/

        /*spinner_kabupaten = (Spinner) getView().findViewById(R.id.spinner_kabupaten);
        spinner_kecamatan = (Spinner) getView().findViewById(R.id.spinner_kecamatan);
        spinner_kelurahan = (Spinner) getView().findViewById(R.id.spinner_kelurahan);

        spinner_kabupaten.setOnItemSelectedListener(this);
        spinner_kecamatan.setOnItemSelectedListener(this);
        spinner_kelurahan.setOnItemSelectedListener(this);*/
    }

    public void getSpinnerData() {
        String tag_string_req = "req_dropdown";

        AppConfig.GETJSON("GET", AppConfig.API_PROPINSI_V2, null, new AppConfig.onRespOK() {
            @Override
            public void onSuccessResponse(String result) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(result);
                    response = jsonObject.getJSONArray("data");
                    getProvinsi(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_data_register, container, false);
        propinsi = new ArrayList<String>();
        spinnerPropinsi = (Spinner) view.findViewById(R.id.spinner_propinsi);
        spinnerPropinsi.setOnItemSelectedListener(this);
        getSpinnerData();
        return view;

    }

    private void getProvinsi(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            try {
                JSONObject json = jsonArray.getJSONObject(i);
                propinsi.add(json.getString("nama"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //Setting adapter to show the items in the spinner
        spinnerPropinsi.setAdapter(new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, propinsi));
    }

    private String getId(int position){
        String idProv="";
        try {
            JSONObject json = response.getJSONObject(position);
            idProv = json.getString("id_prov");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return idProv;
    }

    @Override
    public void onClick(View v) {
        Log.d("FRAGMENT_REGISTER", String.valueOf(v));
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Log.d("SPINNER", getId(position));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
