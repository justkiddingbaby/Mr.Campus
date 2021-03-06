package com.jkb.model.dataSource.comment.singleAll;

import android.support.annotation.NonNull;

import com.jkb.api.ApiCallback;
import com.jkb.api.ApiResponse;
import com.jkb.api.entity.comment.Comment$ReplyEntity;
import com.jkb.api.entity.comment.CommentListEntity;
import com.jkb.api.entity.comment.CommentReplyEntity;
import com.jkb.api.entity.comment.CommentSendEntity;
import com.jkb.api.entity.operation.OperationActionEntity;

/**
 * 评论列表的数据来源类
 * Created by JustKiddingBaby on 2016/9/20.
 */

public interface CommentSingleAllDataSource {

    /**
     * 获取评论和回复
     *
     * @param Authorization 头，可为空
     * @param commentId     评论id
     * @param apiCallback   回调
     */
    void getSingleComment$Apply(
            String Authorization, @NonNull int commentId,
            ApiCallback<ApiResponse<Comment$ReplyEntity>> apiCallback);

    /**
     * 点赞/取消点赞评论
     *
     * @param Authorization 头
     * @param user_id       用户id
     * @param target_id     目标id
     * @param apiCallback   回调
     */
    void likeComment(
            @NonNull String Authorization, @NonNull int user_id, @NonNull int target_id,
            @NonNull ApiCallback<ApiResponse<OperationActionEntity>> apiCallback);

    /**
     * 发送评论
     *
     * @param Authorization token
     * @param user_id       用户id
     * @param dynamic_id    动态id
     * @param comment       评论内容
     * @param apiCallback   回调
     */
    void sendComment(
            @NonNull String Authorization, @NonNull int user_id, @NonNull int dynamic_id,
            @NonNull String comment,
            @NonNull ApiCallback<ApiResponse<CommentSendEntity>> apiCallback);

    /**
     * 发送评论
     *
     * @param Authorization  token
     * @param target_user_id 用户id
     * @param comment_id     评论id
     * @param dynamic_id     动态id
     * @param comment        评论内容
     * @param apiCallback    回调
     */
    void sendReply(
            @NonNull String Authorization, @NonNull int target_user_id,
            @NonNull int comment_id, @NonNull int dynamic_id,
            @NonNull String comment,
            @NonNull ApiCallback<ApiResponse<CommentReplyEntity>> apiCallback);
}
