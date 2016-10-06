package com.jkb.core.data.dynamic.hot.dynamic.circle;

import com.jkb.api.entity.dynamic.DynamicPopularListEntity;
import com.jkb.core.data.dynamic.hot.HotDynamic;
import com.jkb.core.data.circle.CircleInfo;
import com.jkb.core.data.user.UserInfo;

/**
 * 原创的话题热门动态数据
 * Created by JustKiddingBaby on 2016/9/30.
 */

public class CircleTopicDynamic extends DynamicInCircleDynamic {
    private boolean isDataEffective;

    private TopicContent topicContent;
    private int count_of_participation;

    public CircleTopicDynamic(DynamicPopularListEntity.DataBean.DynamicInCircleBean dynamic) {
        handleTopicDynamic(dynamic);
    }


    @Override
    public HotDynamic getHotDynamic() {
        if (isDataEffective) {
            return this;
        }
        return null;
    }

    /**
     * 解析话题动态数据
     */
    private void handleTopicDynamic(DynamicPopularListEntity.DataBean.DynamicInCircleBean dynamic) {
        if (dynamic == null) {
            isDataEffective = false;
            return;
        }
        isDataEffective = true;
        setDynamic_id(dynamic.getDynamic_id());
        setDtype(dynamic.getDtype());
        setTitle(dynamic.getTitle());
        //设置话题内容数据
        DynamicPopularListEntity.DataBean.DynamicInCircleBean.ContentBean content = dynamic.getContent();
        DynamicPopularListEntity.DataBean.DynamicInCircleBean.ContentBean.TopicBean topic = content.getTopic();
        if (topic == null) {
            setTopicContent(null);
        } else {
            TopicContent topicContent = new TopicContent();
            topicContent.setImg(topic.getImg());
            topicContent.setDoc(topic.getDoc());
            setTopicContent(topicContent);
        }
        setTag(dynamic.getTag());
        setCount_of_comment(dynamic.getCount_of_comment());
        setCount_of_favorite(dynamic.getCount_of_favorite());
        setDynamic_created_at(dynamic.getDynamic_created_at());
        setCount_of_participation(dynamic.getCount_of_participation());
        if (dynamic.getHas_favorite() == 0) {
            setHas_favorite(false);
        } else {
            setHas_favorite(true);
        }
        //设置用户数据
        UserInfo userInfo = new UserInfo();
        userInfo.setId(dynamic.getCreator_id());
        userInfo.setAvatar(dynamic.getCreator_avatar());
        userInfo.setNickName(dynamic.getCreator_nickname());
        setUser(userInfo);
        //设置圈子数据
        CircleInfo circle = new CircleInfo();
        circle.setCircleId(dynamic.getCircle_id());
        circle.setCircleName(dynamic.getCircle_name());
        circle.setCircleType(dynamic.getCircle_type());
        circle.setPictureUrl(dynamic.getCircle_picture());
        circle.setIntroduction(dynamic.getCircle_introduction());
        setCircle(circle);
    }

    public static class TopicContent {
        String doc;
        String img;

        public String getDoc() {
            return doc;
        }

        public void setDoc(String doc) {
            this.doc = doc;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }

    public TopicContent getTopicContent() {
        return topicContent;
    }

    public void setTopicContent(TopicContent topicContent) {
        this.topicContent = topicContent;
    }

    public int getCount_of_participation() {
        return count_of_participation;
    }

    public void setCount_of_participation(int count_of_participation) {
        this.count_of_participation = count_of_participation;
    }
}
