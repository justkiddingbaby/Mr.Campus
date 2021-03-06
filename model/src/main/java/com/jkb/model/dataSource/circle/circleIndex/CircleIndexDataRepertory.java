package com.jkb.model.dataSource.circle.circleIndex;

import android.support.annotation.NonNull;

import com.jkb.api.ApiCallback;
import com.jkb.api.ApiResponse;
import com.jkb.api.entity.circle.CircleActionEntity;
import com.jkb.api.entity.circle.CircleInfoEntity;
import com.jkb.api.entity.circle.DynamicInCircleListEntity;
import com.jkb.api.entity.dynamic.DynamicActionEntity;
import com.jkb.api.entity.operation.OperationActionEntity;
import com.jkb.model.intfc.BitmapLoadedCallback;

/**
 * 圈子首页的远程数据来源类
 * Created by JustKiddingBaby on 2016/8/29.
 */

public class CircleIndexDataRepertory implements CircleIndexDataSource {

    private static CircleIndexDataRepertory INSTANCE = null;
    private CircleIndexDataSource localDataSource;
    private CircleIndexDataSource remoteDataSource;


    private CircleIndexDataRepertory(
            CircleIndexDataSource localDataSource,
            CircleIndexDataSource remoteDataSource) {
        this.localDataSource = localDataSource;
        this.remoteDataSource = remoteDataSource;
    }

    public static CircleIndexDataRepertory getInstance(
            @NonNull CircleIndexDataSource localDataSource, @NonNull CircleIndexDataSource remoteDataSource) {
        if (INSTANCE == null) {
            INSTANCE = new CircleIndexDataRepertory(localDataSource, remoteDataSource);
        }
        return INSTANCE;
    }

    @Override
    public void getCircleInfo(
            @NonNull int userId, @NonNull int id,
            @NonNull ApiCallback<ApiResponse<CircleInfoEntity>> apiCallback) {
        remoteDataSource.getCircleInfo(userId, id, apiCallback);
    }

    @Override
    public void loadBitmapByUrl(@NonNull String url, @NonNull BitmapLoadedCallback callback) {
        remoteDataSource.loadBitmapByUrl(url, callback);
    }

    @Override
    public void circleSubscribeOrNot(
            @NonNull int user_id, @NonNull int target_id, @NonNull String Authorization,
            @NonNull ApiCallback<ApiResponse<OperationActionEntity>> apiCallback) {
        remoteDataSource.circleSubscribeOrNot(user_id, target_id, Authorization, apiCallback);
    }

    @Override
    public void getAllDynamicInCircle(
            String Authorization, @NonNull int circleId, @NonNull int page,
            @NonNull ApiCallback<ApiResponse<DynamicInCircleListEntity>> apiCallback) {
        remoteDataSource.getAllDynamicInCircle(Authorization, circleId, page, apiCallback);
    }

    @Override
    public void favorite(
            @NonNull String Authorization, @NonNull int user_id, @NonNull int target_id,
            @NonNull ApiCallback<ApiResponse<OperationActionEntity>> apiCallback) {
        remoteDataSource.favorite(Authorization, user_id, target_id, apiCallback);
    }

    @Override
    public void putDynamicInBlackList(
            @NonNull String Authorization, @NonNull int dynamic_id, @NonNull int user_id,
            @NonNull ApiCallback<ApiResponse<CircleActionEntity>> apiCallback) {
        remoteDataSource.putDynamicInBlackList(Authorization, dynamic_id, user_id, apiCallback);
    }
}
