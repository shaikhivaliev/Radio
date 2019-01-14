package com.elegion.radio.common;

import com.arellomobile.mvp.MvpAppCompatFragment;

public abstract class PresenterFragment extends MvpAppCompatFragment {

    protected abstract BasePresenter getPresenter();

    @Override
    public void onDetach() {
        if (getPresenter() != null){
            getPresenter().disposeAll();
        }
        super.onDetach();
    }

}
