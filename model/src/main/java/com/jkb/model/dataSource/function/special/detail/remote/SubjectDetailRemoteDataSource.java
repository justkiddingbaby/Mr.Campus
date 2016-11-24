package com.jkb.model.dataSource.function.special.detail.remote;

import android.support.annotation.NonNull;

import com.google.gson.reflect.TypeToken;
import com.jkb.api.ApiCallback;
import com.jkb.api.ApiEngine;
import com.jkb.api.ApiFactoryImpl;
import com.jkb.api.ApiResponse;
import com.jkb.api.config.Config;
import com.jkb.api.entity.comment.CommentListEntity;
import com.jkb.api.entity.comment.CommentReplyEntity;
import com.jkb.api.entity.comment.CommentSendEntity;
import com.jkb.api.entity.operation.OperationActionEntity;
import com.jkb.api.entity.subject.SubjectEntity;
import com.jkb.api.net.comment.CommentApi;
import com.jkb.api.net.dynamic.DynamicApi;
import com.jkb.api.net.operation.OperationApi;
import com.jkb.model.dataSource.function.special.detail.SubjectDetailDataSource;

import java.lang.reflect.Type;

import retrofit2.Call;

/**
 * 专题详情的数据仓库类
 * Created by JustKiddingBaby on 2016/11/22.
 */

public class SubjectDetailRemoteDataSource implements SubjectDetailDataSource {

    private SubjectDetailRemoteDataSource() {
    }

    private static SubjectDetailRemoteDataSource INSTANCE = null;

    public static SubjectDetailRemoteDataSource newInstance() {
        if (INSTANCE == null) {
            INSTANCE = new SubjectDetailRemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public void getSubjectDynamic(
            @NonNull int user_id, @NonNull int dynamic_id,
            @NonNull ApiCallback<ApiResponse<SubjectEntity>> apiCallback) {
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        apiFactory.setHttpClient(apiFactory.getHttpClient());
        apiFactory.initRetrofit();
        DynamicApi dynamicApi = apiFactory.createApi(DynamicApi.class);
        Call<ApiResponse<SubjectEntity>> call;
        call = dynamicApi.getSubjectSingle(user_id, dynamic_id);
        Type type = new TypeToken<ApiResponse<SubjectEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<SubjectEntity>>(apiCallback, call, type);
    }


    @Override
    public void getComment$Apply(
            String Authorization, @NonNull int dynamicId, @NonNull int page,
            String order,
            ApiCallback<ApiResponse<CommentListEntity>> apiCallback) {
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        apiFactory.setHttpClient(apiFactory.getHttpClient());
        apiFactory.initRetrofit();
        CommentApi commentApi = apiFactory.createApi(CommentApi.class);
        Call<ApiResponse<CommentListEntity>> call;
        call = commentApi.getComment$Reply(Authorization, dynamicId, page, order);
        Type type = new TypeToken<ApiResponse<CommentListEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<CommentListEntity>>(apiCallback, call, type);
    }

    @Override
    public void likeComment(
            @NonNull String Authorization, @NonNull int user_id, @NonNull int target_id,
            @NonNull ApiCallback<ApiResponse<OperationActionEntity>> apiCallback) {
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        apiFactory.setHttpClient(apiFactory.genericClient());
        apiFactory.initRetrofit();
        OperationApi operationApi = apiFactory.createApi(OperationApi.class);
        Call<ApiResponse<OperationActionEntity>> call;
        call = operationApi.like(Authorization, Config.ACTION_LIKE, user_id, target_id);
        Type type = new TypeToken<ApiResponse<OperationActionEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<OperationActionEntity>>(apiCallback, call, type);
    }

    @Override
    public void favorite(
            @NonNull String Authorization, @NonNull int user_id, @NonNull int target_id,
            @NonNull ApiCallback<ApiResponse<OperationActionEntity>> apiCallback) {
        ApiFactoryImpl factory = ApiFactoryImpl.newInstance();
        factory.setHttpClient(factory.genericClient());
        factory.initRetrofit();
        OperationApi operationApi = factory.createApi(OperationApi.class);
        Call<ApiResponse<OperationActionEntity>> call;
        call = operationApi.like(Authorization, Config.ACTION_FAVORITE, user_id, target_id);
        Type type = new TypeToken<ApiResponse<OperationActionEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<OperationActionEntity>>(apiCallback, call, type);
    }

    @Override
    public void sendComment(
            @NonNull String Authorization, @NonNull int user_id, @NonNull int dynamic_id,
            @NonNull String comment,
            @NonNull ApiCallback<ApiResponse<CommentSendEntity>> apiCallback) {
        ApiFactoryImpl factory = ApiFactoryImpl.newInstance();
        factory.setHttpClient(factory.genericClient());
        factory.initRetrofit();
        CommentApi commentApi = factory.createApi(CommentApi.class);
        Call<ApiResponse<CommentSendEntity>> call;
        call = commentApi.sendComment(Authorization, user_id, dynamic_id, comment);
        Type type = new TypeToken<ApiResponse<CommentSendEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<CommentSendEntity>>(apiCallback, call, type);
    }

    @Override
    public void sendReply(
            @NonNull String Authorization, @NonNull int target_user_id, @NonNull int comment_id,
            @NonNull int dynamic_id, @NonNull String comment,
            @NonNull ApiCallback<ApiResponse<CommentReplyEntity>> apiCallback) {
        ApiFactoryImpl factory = ApiFactoryImpl.newInstance();
        factory.setHttpClient(factory.genericClient());
        factory.initRetrofit();
        CommentApi commentApi = factory.createApi(CommentApi.class);
        Call<ApiResponse<CommentReplyEntity>> call;
        call = commentApi.sendReplyComment(Authorization, target_user_id,
                comment_id, dynamic_id, comment);
        Type type = new TypeToken<ApiResponse<CommentReplyEntity>>() {
        }.getType();
        new ApiEngine<ApiResponse<CommentReplyEntity>>(apiCallback, call, type);
    }
}
