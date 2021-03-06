package com.jkb.mrcampus.fragment.function.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkb.api.config.Config;
import com.jkb.core.contract.function.setting.FunctionSettingContract;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.activity.MainActivity;
import com.jkb.mrcampus.base.BaseFragment;
import com.jkb.mrcampus.fragment.dialog.HintDetermineFloatFragment;
import com.jkb.mrcampus.helper.cropphoto.CropHelper;

/**
 * 设置的页面视图
 * Created by JustKiddingBaby on 2016/7/25.
 */
public class SettingFragment extends BaseFragment implements
        FunctionSettingContract.View,
        View.OnClickListener {

    public SettingFragment() {
    }

    /**
     * 获得一个实例化的HomePageFragment对象
     */
    public static SettingFragment newInstance() {
        return new SettingFragment();
    }


    //data
    private MainActivity mainActivity;
    private FunctionSettingContract.Presenter mPresenter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainActivity = (MainActivity) mActivity;
        setRootView(R.layout.frg_function_setting);
        super.onCreateView(inflater, container, savedInstanceState);
        init(savedInstanceState);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.start();
        }
    }

    @Override
    protected void initListener() {
        rootView.findViewById(R.id.ts4_ib_left).setOnClickListener(this);
        //内容
        rootView.findViewById(R.id.ffs_tv_push).setOnClickListener(this);
        rootView.findViewById(R.id.fss_tv_recommend).setOnClickListener(this);
        rootView.findViewById(R.id.fss_tv_praise).setOnClickListener(this);
        rootView.findViewById(R.id.fss_ll_clearCache).setOnClickListener(this);
        rootView.findViewById(R.id.fss_tv_aboutSoftWear).setOnClickListener(this);
        rootView.findViewById(R.id.fss_tv_feedBack).setOnClickListener(this);
        rootView.findViewById(R.id.fss_tv_question).setOnClickListener(this);
        rootView.findViewById(R.id.fss_tv_logout).setOnClickListener(this);
    }

    @Override
    protected void initData(Bundle savedInstanceState) {

    }

    @Override
    protected void initView() {
        //初始化标题栏
        ((TextView) (rootView.findViewById(R.id.ts4_tv_name))).setText("设置");
        ((ImageView) rootView.findViewById(R.id.ts4_ib_left)).setImageResource(R.drawable.ic_drawer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mainActivity = null;
        Log.v(TAG, "onDestroy");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ts4_ib_left://返回
                mainActivity.showLeftMenuView();
                break;
            case R.id.ffs_tv_push://推送
                break;
            case R.id.fss_tv_recommend://推荐
                showShareView();
                break;
            case R.id.fss_tv_praise://评价
                break;
            case R.id.fss_ll_clearCache://清除缓存
                showHintDetermineView();
                break;
            case R.id.fss_tv_aboutSoftWear://关于菌菌
                startAboutSoft();
                break;
            case R.id.fss_tv_question://常见问题
                startCommonQuestion();
                break;
            case R.id.fss_tv_logout://退出登录
                mPresenter.onLogin$LogoutClick();
                break;
        }
    }

    @Override
    public void showLoginView() {
        ((TextView) rootView.findViewById(R.id.fss_tv_logout)).setText("退出当前帐号");
    }

    @Override
    public void showLogoutView() {
        ((TextView) rootView.findViewById(R.id.fss_tv_logout)).setText("登录/注册");
    }

    @Override
    public void setCacheSize(double cacheSize) {
        ((TextView) rootView.findViewById(R.id.fss_tv_cacheSize)).setText(cacheSize + "M");
    }

    @Override
    public void setCurrentSystemVision(String currentSystemVision) {
        ((TextView) rootView.findViewById(R.id.fss_tv_vision)).setText(currentSystemVision);
    }

    @Override
    public void showAboutSoftwareView() {
    }

    @Override
    public void showShareView() {
        mainActivity.share("菌菌", Config.APP_DOWNLOAD_ADDRESS, "校园菌菌，当前校园最火爆社交APP",
                null, Config.APP_DOWNLOAD_ADDRESS, "校园菌菌", Config.APP_DOWNLOAD_ADDRESS);
    }

    @Override
    public void showHintDetermineView() {
        mainActivity.showHintDetermineFloatView("是否清除缓存",
                "（注：缓存是平时浏览和裁剪的图片缓存，清除后会影响浏览效率）",
                "确定", "取消", onDetermineItemClickListener);
    }

    @Override
    public void clearCache() {
        CropHelper.clearCacheDir();
        mPresenter.onClearCacheClick();
    }

    @Override
    public void startLoginActivity() {
        mainActivity.startLoginActivity();
    }

    @Override
    public void startAboutSoft() {
        mainActivity.startWebBrowser(Config.URL_SETTING_URL + Config.SETTING_MODULE_ABOUT, "关于菌菌");
    }

    @Override
    public void startCommonQuestion() {
        mainActivity.startWebBrowser(Config.URL_SETTING_URL + Config.SETTING_MODULE_COMMONQUESTION,
                "常见问题");
    }

    @Override
    public void setPresenter(FunctionSettingContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading(String value) {
        mainActivity.showLoading(value, this);
    }

    @Override
    public void dismissLoading() {
        mainActivity.dismissLoading();
    }

    @Override
    public void showReqResult(String value) {
        mainActivity.showShortToast(value);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    /**
     * 是否清楚缓存的提示框回调
     */
    private HintDetermineFloatFragment.OnDetermineItemClickListener onDetermineItemClickListener =
            new HintDetermineFloatFragment.OnDetermineItemClickListener() {
                @Override
                public void onFirstItemClick() {
                    mainActivity.dismiss();
                    clearCache();
                }

                @Override
                public void onSecondItemClick() {
                    mainActivity.dismiss();
                }
            };
}