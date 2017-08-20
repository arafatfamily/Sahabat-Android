package com.sahabat.mobile.register;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sahabat.mobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DokumenRegister extends Fragment{

    public DokumenRegister() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_dokumen_register, container, false);

        return inflated;

    }

}
