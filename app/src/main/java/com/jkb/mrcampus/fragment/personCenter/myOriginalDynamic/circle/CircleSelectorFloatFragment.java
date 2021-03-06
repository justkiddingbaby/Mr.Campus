package com.jkb.mrcampus.fragment.personCenter.myOriginalDynamic.circle;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import com.jkb.core.Injection;
import com.jkb.core.contract.personCenter.original.myDynamic.circle.CircleSelectorContract;
import com.jkb.core.data.info.circle.CircleInfo;
import com.jkb.core.presenter.personCenter.original.myDynamic.circle.CircleSelectorPresenter;
import com.jkb.mrcampus.Config;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.activity.MyOriginalDynamicActivity;
import com.jkb.mrcampus.adapter.recycler.personCenter.original.myDynamic.circle.CircleSelectorAdapter;
import com.jkb.mrcampus.utils.ClassUtils;

import java.util.List;

/**
 * 圈子选择器的浮动View层
 * 只显示圈子数据
 * 只能用户获取我的圈子动态页面中
 * Created by JustKiddingBaby on 2016/10/18.
 */

public class CircleSelectorFloatFragment extends DialogFragment implements
        CircleSelectorContract.View, SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    //data
    private static final String TAG = ClassUtils.getClassName(CircleSelectorFloatFragment.class);
    private View rootView;
    private Activity mActivity;
    private Context context;
    private Dialog mDialog;

    //数据
    private int user_id = -1;
    private MyOriginalDynamicActivity myOriginalDynamicActivity;
    private CircleSelectorContract.Presenter mPresenter;
    //View
    private SwipeRefreshLayout refreshLayout;

    //要选择的圈子数据
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private CircleSelectorAdapter circleSelectorAdapter;
    private CircleSelectorAdapter.OnCircleSelectorListener onCircleSelectorListener;

    public static CircleSelectorFloatFragment newInstance(@NonNull int user_id) {
        CircleSelectorFloatFragment INSTANCE = new CircleSelectorFloatFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Config.INTENT_KEY_USER_ID, user_id);
        INSTANCE.setArguments(bundle);
        return INSTANCE;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mActivity = getActivity();
        context = mActivity.getApplicationContext();
        myOriginalDynamicActivity = (MyOriginalDynamicActivity) mActivity;
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        LayoutInflater inflater = LayoutInflater.from(context);
        rootView = inflater.inflate(R.layout.frg_float_select_circle, null);
        builder.setView(rootView);
        mDialog = builder.create();
        initConfig();//初始化配置
        //初始化操作
        init(savedInstanceState);
        return mDialog;
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mPresenter.start();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        mDialog.setCanceledOnTouchOutside(true);
        //设置动画
        Window window = mDialog.getWindow();
        window.setGravity(Gravity.CENTER);
        window.setWindowAnimations(R.style.animate_dialog_bottomIn);
        //设置宽度和高度为全屏
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
    }

    @Override
    public void onStart() {
        super.onStart();
        mDialog.setCanceledOnTouchOutside(true);
        //设置为全屏
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mDialog.getWindow().setLayout(dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    /**
     * 初始化
     */
    private void init(Bundle savedInstanceState) {
        initView();
        initData(savedInstanceState);
        initListener();
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        //初始化监听器
        refreshLayout.setOnRefreshListener(this);
        rootView.findViewById(R.id.ffsc_content).setOnClickListener(this);
        recyclerView.addOnScrollListener(onScrollListener);
        circleSelectorAdapter.setOnCircleSelectorListener(mOnCircleSelectorListener);
    }

    /**
     * 初始化数据
     */
    private void initData(Bundle savedInstanceState) {
        initPresenter();
        //初始化数据
        if (savedInstanceState != null) {
            user_id = savedInstanceState.getInt(Config.INTENT_KEY_USER_ID);
        } else {
            Bundle arguments = getArguments();
            user_id = arguments.getInt(Config.INTENT_KEY_USER_ID);
        }
        circleSelectorAdapter = new CircleSelectorAdapter(context, null);
        recyclerView.setAdapter(circleSelectorAdapter);
    }

    /**
     * 初始化P
     */
    private void initPresenter() {
        if (mPresenter == null) {
            mPresenter = new CircleSelectorPresenter(this,
                    Injection.provideCircleSelectorRepertory(context.getApplicationContext()));
        }
    }

    /**
     * 初始化View
     */
    private void initView() {
        //刷新空间
        refreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.ffsc_srl);
        //列表数据
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ffsc_lv);
        linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
    }

    @Override
    public void showRefreshingView() {
        refreshLayout.setRefreshing(true);
    }

    @Override
    public void hideRefreshingView() {
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void setCircleDatas(List<CircleInfo> circleDatas) {
        Log.d(TAG, "我收的到的总数是" + circleDatas.size());
        circleSelectorAdapter.circleInfos = circleDatas;
        circleSelectorAdapter.notifyDataSetChanged();
    }

    @Override
    public int bindUserId() {
        return user_id;
    }

    @Override
    public void setPresenter(CircleSelectorContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void showLoading(String value) {
        myOriginalDynamicActivity.showLoading(value, this);
    }

    @Override
    public void dismissLoading() {
        myOriginalDynamicActivity.dismissLoading();
    }

    @Override
    public void showReqResult(String value) {
        myOriginalDynamicActivity.showShortToast(value);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        myOriginalDynamicActivity = null;
        mActivity = null;
        mDialog = null;
        refreshLayout = null;
        recyclerView = null;
        linearLayoutManager = null;
        mOnCircleSelectorListener = null;
        circleSelectorAdapter = null;
        context = null;
        rootView = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(Config.INTENT_KEY_USER_ID, user_id);
    }

    @Override
    public void onRefresh() {
        mPresenter.onRefresh();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.ffsc_content:
                dismiss();
                break;
        }
    }

    /**
     * 滑动的监听器
     */
    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int lastVisibleItem = (linearLayoutManager).findLastVisibleItemPosition();
            int totalItemCount = linearLayoutManager.getItemCount();
            if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                mPresenter.onLoadMore();//设置下拉加载
            }
        }
    };

    /**
     * 设置圈子的点击监听器
     */
    public void setOnCircleSelectorListener(
            CircleSelectorAdapter.OnCircleSelectorListener onCircleSelectorListener) {
        this.onCircleSelectorListener = onCircleSelectorListener;
    }

    /**
     * 筛选圈子的条目点击监听器
     */
    private CircleSelectorAdapter.OnCircleSelectorListener mOnCircleSelectorListener =
            new CircleSelectorAdapter.OnCircleSelectorListener() {
                @Override
                public void onAllCircleClick() {
                    if (onCircleSelectorListener != null) {
                        onCircleSelectorListener.onAllCircleClick();
                    }
                }

                @Override
                public void onCircleSelected(CircleInfo circleInfo) {
                    if (onCircleSelectorListener != null) {
                        onCircleSelectorListener.onCircleSelected(circleInfo);
                    }
                }
            };
}
