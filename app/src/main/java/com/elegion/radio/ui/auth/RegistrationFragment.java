package com.elegion.radio.ui.auth;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elegion.radio.R;

public class RegistrationFragment extends Fragment {
    private Button mRegistration;


    public static RegistrationFragment newInstance() {

        Bundle args = new Bundle();
        RegistrationFragment fragment = new RegistrationFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup
            container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_registration, container, false);
        mRegistration = view.findViewById(R.id.btn_new_user);
        mRegistration.setOnClickListener(v -> {
            //todo registration
        });
        return view;
    }

}
