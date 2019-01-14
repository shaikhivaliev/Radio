package com.elegion.radio.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.elegion.radio.R;
import com.elegion.radio.common.ContainerActivity;
import com.elegion.radio.ui.player.PlayerFragment;

public class AuthFragment extends Fragment {

    private Button mSignIn;
    private Button mRegistration;

    public static AuthFragment newInstance() {

        Bundle args = new Bundle();
        AuthFragment fragment = new AuthFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_auth, container, false);
        mSignIn = view.findViewById(R.id.btn_sign_in);
        mRegistration = view.findViewById(R.id.btn_registration);
        mSignIn.setOnClickListener(v -> {
            //todo login
        });
        mRegistration.setOnClickListener(v -> ((ContainerActivity) getActivity()).changeFragment(RegistrationFragment.newInstance()));
        return view;

    }
}
