package com.jkb.mrcampus.fragment.function.special.function;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jkb.mrcampus.R;
import com.jkb.mrcampus.activity.MainActivity;
import com.jkb.mrcampus.activity.ToolsFunctionActivity;
import com.jkb.mrcampus.base.BaseFragment;

/**
 * 求学霸的V层
 * Created by JustKiddingBaby on 2016/11/15.
 */

public class SpecialWantedSavantFragment extends BaseFragment {

    public static SpecialWantedSavantFragment newInstance() {
        Bundle args = new Bundle();
        SpecialWantedSavantFragment fragment = new SpecialWantedSavantFragment();
        fragment.setArguments(args);
        return fragment;
    }

    //data
    private MainActivity mainActivity;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) mActivity;
        setRootView(R.layout.frg_special_wanted_savant);
        super.onCreateView(inflater, container, savedInstanceState);
        return rootView;
    }

    @Override
    protected void initListener() {

    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity = null;
    }
}
