package com.jkb.core.contract.function.index;

import android.support.annotation.NonNull;

import com.jkb.core.base.BasePresenter;
import com.jkb.core.base.BaseView;
import com.jkb.core.data.dynamic.dynamic.DynamicBaseData;

import java.util.List;

/**
 * 动态的页面协议类
 * Created by JustKiddingBaby on 2016/8/23.
 */

public interface DynamicContract {

    interface View extends BaseView<Presenter> {

        /**
         * 滚动到顶部
         */
        void scrollToTop();

        /**
         * 显示登录了的视图
         */
        void showLoginedView();

        /**
         * 显示未登录状态下的视图
         */
        void showUnLoginView();

        /**
         * 显示加载的下拉刷新控件
         */
        void showRefreshingView();

        /**
         * 隐藏加载的下拉刷新控件
         */
        void hideRefreshingView();

        /**
         * 显示书写动态视图
         */
        void showWriteDynamicView();

        /**
         * 显示评论的页面
         */
        void showCommentView();

        /**
         * 显示分享的页面
         */
        void showShareView();

        /**
         * 打开动态详情页面
         */
        void startDynamicDetailView();

        /**
         * 点击喜欢
         */
        void like();

        /**
         * 设置动态数据到视图中
         */
        void setDynamicDataToView(List<DynamicBaseData> dynamicBaseDatas);

        /**
         * 打开动态详情页面
         *
         * @param dynamic_id  动态id
         * @param dynamicType 动态类型
         */
        void startDynamicActivity(@NonNull int dynamic_id, @NonNull String dynamicType);

        /**
         * 打开评论页面
         *
         * @param dynamic_id 动态id
         */
        void startCommentActivity(@NonNull int dynamic_id);

        /**
         * 打开动态消息的页面
         */
        void startDynamicMessageActivity();
    }

    interface Presenter extends BasePresenter {

        /**
         * 刷新数据
         */
        void onRefresh();

        /**
         * 加载更多
         */
        void onLoadMore();

        /**
         * 初始化动态数据
         */
        void initDynamicData();

        /**
         * 点击喜欢按钮
         *
         * @param position 相应的条目
         */
        void likeDynamic(int position);

        /**
         * 绑定数据到视图中
         */
        void bindDataToView();

        /**
         * 得到创建者id
         */
        int getCreator_id(int position);

        /**
         * 得到原创用户的Id
         */
        int getOriginator_user_id(int position);

        /**
         * 得到圈子的id
         */
        int getCircleId(int position);

        /**
         * 打开动态详情页面
         */
        void startDynamicDetailView(int position);

        /**
         * 点击评论的时候
         */
        void onCommentClick(int position);

        /**
         * 设置缓存过期
         */
        void setCacheExpired();
    }
}
