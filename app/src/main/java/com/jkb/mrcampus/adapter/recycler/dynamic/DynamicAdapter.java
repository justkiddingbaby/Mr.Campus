package com.jkb.mrcampus.adapter.recycler.dynamic;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkb.api.config.Config;
import com.jkb.core.contract.function.data.dynamic.CircleData;
import com.jkb.core.contract.function.data.dynamic.DynamicBaseData;
import com.jkb.core.contract.function.data.dynamic.DynamicData;
import com.jkb.model.net.ImageLoaderFactory;
import com.jkb.model.utils.StringUtils;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.utils.BitmapUtil;
import com.jkb.mrcampus.utils.ClassUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 动态的适配器
 * Created by JustKiddingBaby on 2016/8/23.
 */

public class DynamicAdapter extends RecyclerView.Adapter<DynamicAdapter.ViewHolder> {

    private static final String TAG = "DynamicAdapter";
    private Context context;
    private int colorWhite;
    private int colorGravy;
    private Random random;

    public List<DynamicBaseData> dynamicBaseDatas;

    //回调
    private OnUserClickListener onUserClickListener;//用户点击的回调
    private OnLikeActionClickListener onLikeActionClickListener;//点击喜欢的回调

    //用到的常量
    private static final int ORIGINAL_TYPE_NORMAL = 1001;
    private static final int ORIGINAL_TYPE_TOPIC = 1002;
    private static final int ORIGINAL_TYPE_ARTICLE = 1003;
    private static final int UNORIGINAL_TYPE_SUBSCRIBE_CIRCLE = 2001;
    private static final int UNORIGINAL_TYPE_FAVORITE_NORMAL = 2010;//喜欢的动态
    private static final int UNORIGINAL_TYPE_FAVORITE_TOPIC = 2011;//喜欢的话题动态
    private static final int UNORIGINAL_TYPE_FAVORITE_ARTICLE = 2012;//喜欢的文章动态


    public DynamicAdapter(Context context, List<DynamicBaseData> dynamicBaseDatas) {
        this.context = context;
        if (dynamicBaseDatas == null) {
            dynamicBaseDatas = new ArrayList<>();
        }
        this.dynamicBaseDatas = dynamicBaseDatas;

        colorWhite = context.getResources().getColor(R.color.white);
        colorGravy = context.getResources().getColor(R.color.background_general);

        random = new Random();
    }

    @Override
    public int getItemViewType(int position) {
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        int itemType = 0;
        String target_type = dynamicBaseData.getTarget_type();
        String action = dynamicBaseData.getAction();
        switch (action) {//判断动作类型
            case Config.ACTION_TYPE_POST://发表
                //判断是否发表
                itemType = judgePostAction(position, target_type, dynamicBaseData);
                break;
            case Config.ACTION_TYPE_SUBSCRIBE://订阅
                itemType = judgeSubscribeAction(position, target_type, dynamicBaseData);
                break;
            case Config.ACTION_TYPE_FAVORITE://喜欢
                itemType = judgeFavoriteAction(position, target_type, dynamicBaseData);
                break;
        }
        if (itemType == -1) {
            notifyDataSetChanged();
        }
        return itemType;
    }

    /**
     * 判断喜欢类型
     */
    private int judgeFavoriteAction(
            int position, String target_type, DynamicBaseData dynamicBaseData) {
        int itemType = -1;
        switch (target_type) {
            case Config.TARGET_TYPE_DYNAMIC://动态
                if (dynamicBaseData instanceof DynamicData) {//是否是动态类
                    //设置动态数据
                    DynamicData dynamicData = (DynamicData) dynamicBaseData;
                    String dType = dynamicData.getDtype();
                    if (dType.equals(Config.D_TYPE_NORMAL)) {
                        itemType = UNORIGINAL_TYPE_FAVORITE_NORMAL;
                    }
                } else {
                    dynamicBaseDatas.remove(position);
                }
                break;
        }
        return itemType;
    }

    /**
     * 判断订阅类型
     */
    private int judgeSubscribeAction(
            int position, String target_type, DynamicBaseData dynamicBaseData) {
        int itemType = -1;
        switch (target_type) {
            case Config.TARGET_TYPE_CIRCLE://圈子
                if (dynamicBaseData instanceof CircleData) {//判断是否是圈子类型
                    itemType = UNORIGINAL_TYPE_SUBSCRIBE_CIRCLE;
                } else {
                    dynamicBaseDatas.remove(position);
                }
                break;
        }
        return itemType;
    }


    /**
     * 判断发表类型
     */
    private int judgePostAction(
            int position, String target_type, DynamicBaseData dynamicBaseData) {
        int itemType = -1;
        switch (target_type) {
            case Config.TARGET_TYPE_DYNAMIC://动态
                if (dynamicBaseData instanceof DynamicData) {//是否是动态类
                    //设置动态数据
                    DynamicData dynamicData = (DynamicData) dynamicBaseData;
                    String dType = dynamicData.getDtype();
                    if (dType.equals(Config.D_TYPE_ARTICLE)) {
                        itemType = ORIGINAL_TYPE_ARTICLE;
                    } else if (dType.equals(Config.D_TYPE_TOPIC)) {
                        itemType = ORIGINAL_TYPE_TOPIC;
                    } else {
                        itemType = ORIGINAL_TYPE_NORMAL;
                    }
                } else {
                    dynamicBaseDatas.remove(position);
                }
                break;
            case Config.TARGET_TYPE_CIRCLE://圈子
                itemType = UNORIGINAL_TYPE_SUBSCRIBE_CIRCLE;
                break;
        }
        return itemType;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = null;
        LayoutInflater inflater = LayoutInflater.from(context);
        DynamicAdapter.ViewHolder holder = null;
        switch (viewType) {
            case ORIGINAL_TYPE_NORMAL:
                view = inflater.inflate(R.layout.item_dynamic_orginal_normal,
                        parent, false);
                holder = new DynamicAdapter.ViewHolder(view);
                initOriginalNormal(view, holder);//初始化原创——文章组件
                break;
            case ORIGINAL_TYPE_ARTICLE:
                view = inflater.inflate(R.layout.item_dynamic_orginal_article,
                        parent, false);
                holder = new DynamicAdapter.ViewHolder(view);
                initOriginalArticle(view, holder);//初始化原创——文章组件
                break;
            case ORIGINAL_TYPE_TOPIC:
                view = inflater.inflate(R.layout.item_dynamic_original_topic,
                        parent, false);
                holder = new DynamicAdapter.ViewHolder(view);
                initOriginalTopic(view, holder);//初始化原创——文章组件
                break;
            case UNORIGINAL_TYPE_SUBSCRIBE_CIRCLE:
                //随机方向
                int randomInt = random.nextInt(2);
                if (randomInt % 2 == 0) {
                    view = inflater.inflate(R.layout.item_dynamic_subscribe_circle_left,
                            parent, false);
                } else {
                    view = inflater.inflate(R.layout.item_dynamic_subscribe_circle_right,
                            parent, false);
                }
                holder = new DynamicAdapter.ViewHolder(view);
                initUnOriginalSubscribeCircle(view, holder);//初始化非原创——订阅圈子的相关组件
                break;
            case UNORIGINAL_TYPE_FAVORITE_NORMAL://喜欢的普通动态
                view = inflater.inflate(R.layout.item_dynamic_favorite_normal,
                        parent, false);
                holder = new DynamicAdapter.ViewHolder(view);
                initUnOriginalFavoriteNormal(view, holder);//初始化非原创——普通组件
                break;
            case UNORIGINAL_TYPE_FAVORITE_TOPIC://喜欢的话题动态
                view = inflater.inflate(R.layout.item_dynamic_favorite_topic,
                        parent, false);
                holder = new DynamicAdapter.ViewHolder(view);
                initUnOriginalFavoriteTopic(view, holder);//初始化非原创——话题组件
                break;
            case UNORIGINAL_TYPE_FAVORITE_ARTICLE://喜欢的文章动态
                view = inflater.inflate(R.layout.item_dynamic_favorite_article,
                        parent, false);
                holder = new DynamicAdapter.ViewHolder(view);
                initUnOriginalFavoriteArticle(view, holder);//初始化非原创——文章组件
                break;
        }
        //初始化id
        holder.contentView = view.findViewById(R.id.dynamic_content);
        //初始化其他
        return holder;
    }

    /**
     * 初始化非原创——喜欢-文章动态相关组件
     */
    private void initUnOriginalFavoriteArticle(View view, ViewHolder holder) {
        if (holder.unOriginalFavoriteArticle == null) {
            holder.unOriginalFavoriteArticle = new UnOriginalFavoriteArticle();
        }
        holder.viewType = UNORIGINAL_TYPE_FAVORITE_ARTICLE;
        //初始化作者信息
        holder.unOriginalFavoriteArticle.iv_headImg = (ImageView) view.findViewById(R.id.iidu_iv_headImg);
        holder.unOriginalFavoriteArticle.tvName = (TextView) view.findViewById(R.id.iidu_tv_name);
        holder.unOriginalFavoriteArticle.tvTime = (TextView) view.findViewById(R.id.iidu_tv_time);
        holder.unOriginalFavoriteArticle.tvAction = (TextView) view.findViewById(R.id.iidu_tv_action);
        //原创作者信息
        holder.unOriginalFavoriteArticle.tvOriginalNickName =
                (TextView) view.findViewById(R.id.idfa_tv_originator_nickname);
        //作品信息
        holder.unOriginalFavoriteArticle.tvArticleTitle =
                (TextView) view.findViewById(R.id.idfa_tv_articleName);//话题名称
        holder.unOriginalFavoriteArticle.ivPic =
                (ImageView) view.findViewById(R.id.idfa_iv_contentPic);
        holder.unOriginalFavoriteArticle.tvContent =
                (TextView) view.findViewById(R.id.idfa_tv_contentText);
        holder.unOriginalFavoriteArticle.tvLikeNum =
                (TextView) view.findViewById(R.id.idfa_tv_likeNum);
        holder.unOriginalFavoriteArticle.tvCommentNum =
                (TextView) view.findViewById(R.id.idfa_tv_commentNum);
    }

    /**
     * 初始化非原创——喜欢-话题动态相关组件
     */
    private void initUnOriginalFavoriteTopic(View view, ViewHolder holder) {
        if (holder.unOriginalFavoriteTopic == null) {
            holder.unOriginalFavoriteTopic = new UnOriginalFavoriteTopic();
        }
        holder.viewType = UNORIGINAL_TYPE_FAVORITE_TOPIC;
        //初始化作者信息
        holder.unOriginalFavoriteTopic.iv_headImg = (ImageView) view.findViewById(R.id.iidu_iv_headImg);
        holder.unOriginalFavoriteTopic.tvName = (TextView) view.findViewById(R.id.iidu_tv_name);
        holder.unOriginalFavoriteTopic.tvTime = (TextView) view.findViewById(R.id.iidu_tv_time);
        holder.unOriginalFavoriteTopic.tvAction = (TextView) view.findViewById(R.id.iidu_tv_action);
        //原创作者信息
        holder.unOriginalFavoriteTopic.tvOriginalNickName =
                (TextView) view.findViewById(R.id.idft_tv_originator_nickname);
        //作品信息
        holder.unOriginalFavoriteTopic.tvTopicTitle =
                (TextView) view.findViewById(R.id.idft_tv_topicName);//话题名称
        holder.unOriginalFavoriteTopic.tvPartNum =
                (TextView) view.findViewById(R.id.idft_tv_partInNum);//参与人数
        holder.unOriginalFavoriteTopic.ivPic =
                (ImageView) view.findViewById(R.id.idft_iv_contentPic);
        holder.unOriginalFavoriteTopic.tvContent =
                (TextView) view.findViewById(R.id.idft_tv_contentText);
        holder.unOriginalFavoriteTopic.tvLikeNum =
                (TextView) view.findViewById(R.id.idft_tv_likeNum);
        holder.unOriginalFavoriteTopic.tvCommentNum =
                (TextView) view.findViewById(R.id.idft_tv_commentNum);
    }

    /**
     * 初始化非原创——喜欢-普通动态相关组件
     * 已设置监听事件：头像、喜欢
     */
    private void initUnOriginalFavoriteNormal(View view, ViewHolder holder) {
        if (holder.unOriginalFavoriteNormal == null) {
            holder.unOriginalFavoriteNormal = new UnOriginalFavoriteNormal();
        }
        holder.viewType = UNORIGINAL_TYPE_FAVORITE_NORMAL;
        //初始化作者信息
        holder.unOriginalFavoriteNormal.iv_headImg = (ImageView) view.findViewById(R.id.iidu_iv_headImg);
        holder.unOriginalFavoriteNormal.tvName = (TextView) view.findViewById(R.id.iidu_tv_name);
        holder.unOriginalFavoriteNormal.tvTime = (TextView) view.findViewById(R.id.iidu_tv_time);
        holder.unOriginalFavoriteNormal.tvAction = (TextView) view.findViewById(R.id.iidu_tv_action);
        //原创作者信息
        holder.unOriginalFavoriteNormal.tvOriginalNickName =
                (TextView) view.findViewById(R.id.idfn_tv_originator_nickname);
        //作品信息
        holder.unOriginalFavoriteNormal.ivPic =
                (ImageView) view.findViewById(R.id.idfn_iv_contentPic);
        holder.unOriginalFavoriteNormal.tvContent =
                (TextView) view.findViewById(R.id.idfn_tv_contentText);
        holder.unOriginalFavoriteNormal.tvLikeNum =
                (TextView) view.findViewById(R.id.idfn_tv_likeNum);
        holder.unOriginalFavoriteNormal.tvCommentNum =
                (TextView) view.findViewById(R.id.idfn_tv_commentNum);
        holder.unOriginalFavoriteNormal.ivHeart =
                (ImageView) view.findViewById(R.id.idfn_iv_heart);
        //设置监听事件
        holder.unOriginalFavoriteNormal.iv_headImg.setOnClickListener(clickUserListener);
        holder.unOriginalFavoriteNormal.ivHeart.setOnClickListener(clickLikeListener);
    }

    /**
     * 初始化非原创——订阅圈子的相关组件
     * 已设置监听事件：头像
     */
    private void initUnOriginalSubscribeCircle(View view, ViewHolder holder) {
        if (holder.unOriginalSubscribeCircle == null) {
            holder.unOriginalSubscribeCircle = new UnOriginalSubscribeCircle();
        }
        holder.viewType = UNORIGINAL_TYPE_SUBSCRIBE_CIRCLE;
        //圈子相关
        holder.unOriginalSubscribeCircle.ivCirclePicBg =
                (ImageView) view.findViewById(R.id.idsc_iv_picBg);
        holder.unOriginalSubscribeCircle.ivCirclePicture =
                (ImageView) view.findViewById(R.id.idsc_iv_picture);
        holder.unOriginalSubscribeCircle.tvCircleName =
                (TextView) view.findViewById(R.id.idsc_tv_name);
        holder.unOriginalSubscribeCircle.tvCircleContent =
                (TextView) view.findViewById(R.id.idsc_tv_content);
        holder.unOriginalSubscribeCircle.tvCircleType =
                (TextView) view.findViewById(R.id.idsc_tv_circleType);
        holder.unOriginalSubscribeCircle.tvCircleOperationNum =
                (TextView) view.findViewById(R.id.idsc_tv_operationCount);
        holder.unOriginalSubscribeCircle.tvCircleSubscribeNum =
                (TextView) view.findViewById(R.id.idsc_tv_dynamicsCount);
        //作者相关
        holder.unOriginalSubscribeCircle.ivHeadImg = (ImageView) view.findViewById(R.id.ius_iv_headImg);
        holder.unOriginalSubscribeCircle.tvName = (TextView) view.findViewById(R.id.ius_tv_name);
        holder.unOriginalSubscribeCircle.tvTime = (TextView) view.findViewById(R.id.ius_tv_time);
        holder.unOriginalSubscribeCircle.tvAction = (TextView) view.findViewById(R.id.ius_tv_action);

        //设置监听事件
        holder.unOriginalSubscribeCircle.ivHeadImg.setOnClickListener(clickUserListener);
    }

    /**
     * 初始化原创——文章的相关组件
     * 已设置监听事件：头像,喜欢
     */
    private void initOriginalArticle(View view, ViewHolder holder) {
        if (holder.originalArticle == null) {
            holder.originalArticle = new OriginalArticle();
        }
        holder.viewType = ORIGINAL_TYPE_ARTICLE;
        holder.originalArticle.iv_headImg = (ImageView) view.findViewById(R.id.idoa_iv_headImg);
        holder.originalArticle.iv_picture = (ImageView) view.findViewById(R.id.idoa_iv_pic);
        holder.originalArticle.tvName = (TextView) view.findViewById(R.id.idoa_tv_name);
        holder.originalArticle.tvTime = (TextView) view.findViewById(R.id.idoa_tv_time);
        holder.originalArticle.tvTitle = (TextView) view.findViewById(R.id.idoa_tv_title);
        holder.originalArticle.tvContent = (TextView) view.findViewById(R.id.idoa_tv_content);
        holder.originalArticle.tvLikeNum = (TextView) view.findViewById(R.id.idoa_tv_likeNum);
        holder.originalArticle.tvCommentNum = (TextView) view.findViewById(R.id.idoa_tv_commentNum);
        holder.originalArticle.tvAction = (TextView) view.findViewById(R.id.idoa_tv_action);
        holder.originalArticle.ivHeart =
                (ImageView) view.findViewById(R.id.idoa_iv_heart);

        //设置监听事件
        holder.originalArticle.iv_headImg.setOnClickListener(clickUserListener);
        holder.originalArticle.ivHeart.setOnClickListener(clickLikeListener);
    }

    /**
     * 初始化原创——话题的相关组件
     * 已设置监听事件：头像,喜欢
     */
    private void initOriginalTopic(View view, ViewHolder holder) {
        if (holder.originalTopic == null) {
            holder.originalTopic = new OriginalTopic();
        }
        holder.viewType = ORIGINAL_TYPE_TOPIC;
        holder.originalTopic.iv_headImg = (ImageView) view.findViewById(R.id.idot_iv_headImg);
        holder.originalTopic.iv_picture = (ImageView) view.findViewById(R.id.idot_iv_pic);
        holder.originalTopic.tvName = (TextView) view.findViewById(R.id.idot_tv_name);
        holder.originalTopic.tvTime = (TextView) view.findViewById(R.id.idot_tv_time);
        holder.originalTopic.tvTitle = (TextView) view.findViewById(R.id.idot_tv_title);
        holder.originalTopic.tvContent = (TextView) view.findViewById(R.id.idot_tv_content);
        holder.originalTopic.tvPartNum = (TextView) view.findViewById(R.id.idot_tv_partInNum);
        holder.originalTopic.tvLikeNum = (TextView) view.findViewById(R.id.idot_tv_likeNum);
        holder.originalTopic.tvAction = (TextView) view.findViewById(R.id.idot_tv_action);
        holder.originalTopic.ivHeart =
                (ImageView) view.findViewById(R.id.idot_iv_heart);

        //设置监听事件
        holder.originalTopic.iv_headImg.setOnClickListener(clickUserListener);
        holder.originalTopic.ivHeart.setOnClickListener(clickLikeListener);
    }

    /**
     * 初始化原创——普通的相关组件
     */
    private void initOriginalNormal(View view, ViewHolder holder) {
        if (holder.originalNormal == null) {
            holder.originalNormal = new OriginalNormal();
        }
        holder.viewType = ORIGINAL_TYPE_NORMAL;
        //初始化图片相关
        holder.originalNormal.pictures = view.findViewById(R.id.idon_ll_pictures);
        holder.originalNormal.pic1To3 = view.findViewById(R.id.idon_ll_pic1To3);
        holder.originalNormal.pic2To3 = view.findViewById(R.id.idon_ll_pic2To3);
        holder.originalNormal.pic4To6 = view.findViewById(R.id.idon_ll_pic4To6);
        holder.originalNormal.ivPic1 = (ImageView) view.findViewById(R.id.idon_iv_pic1);
        holder.originalNormal.ivPic2 = (ImageView) view.findViewById(R.id.idon_iv_pic2);
        holder.originalNormal.ivPic3 = (ImageView) view.findViewById(R.id.idon_iv_pic3);
        holder.originalNormal.ivPic4 = (ImageView) view.findViewById(R.id.idon_iv_pic4);
        holder.originalNormal.ivPic5 = (ImageView) view.findViewById(R.id.idon_iv_pic5);
        holder.originalNormal.ivPic6 = (ImageView) view.findViewById(R.id.idon_iv_pic6);
        //初始化其它组件
        holder.originalNormal.tvName = (TextView) view.findViewById(R.id.idon_tv_name);
        holder.originalNormal.tvTime = (TextView) view.findViewById(R.id.idon_tv_time);
        holder.originalNormal.ivHeadImg = (ImageView) view.findViewById(R.id.idon_iv_headImg);
        holder.originalNormal.tvContent = (TextView) view.findViewById(R.id.idon_tv_content);
        holder.originalNormal.tvCommentNum = (TextView) view.findViewById(R.id.idon_tv_commentNum);
        holder.originalNormal.tvLikeNum = (TextView) view.findViewById(R.id.idon_tv_likeNum);
        holder.originalNormal.tvAction = (TextView) view.findViewById(R.id.idon_tv_action);
        holder.originalNormal.ivHeart =
                (ImageView) view.findViewById(R.id.idon_iv_heart);

        //设置监听事件
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //绑定数据
        if (position % 2 == 0) {//偶数
            holder.contentView.setBackgroundColor(colorWhite);
        } else {//奇数
            holder.contentView.setBackgroundColor(colorWhite);
        }
        //设置作者信息
        handleDynamicData(holder, position);
    }

    /**
     * 处理用户数据
     */
    private void handleDynamicData(ViewHolder holder, int position) {
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        if (dynamicBaseData == null) {
            dynamicBaseDatas.remove(position);
            return;
        }
        //设置作者信息
        switch (holder.viewType) {
            case ORIGINAL_TYPE_NORMAL:
                handleDynamicNormalData(holder, position);//绑定普通数据
                break;
            case ORIGINAL_TYPE_ARTICLE:
                handleDynamicArticleData(holder, position);//绑定文章数据
                break;
            case ORIGINAL_TYPE_TOPIC:
                handleDynamicTopicData(holder, position);//绑定话题数据
                break;
            case UNORIGINAL_TYPE_SUBSCRIBE_CIRCLE:
                handleCircleSubscribe(holder, position);//绑定圈子数据
                break;
            case UNORIGINAL_TYPE_FAVORITE_NORMAL:
                handleFavoriteNormal(holder, position);//绑定喜欢普通动态数据
                break;
        }
    }

    /**
     * 解析喜欢——普通动态数据
     * 绑定Tag对象：头像、喜欢
     */
    private void handleFavoriteNormal(ViewHolder holder, int position) {
        Log.d(TAG, "handleFavoriteNormal");
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        if (!(dynamicBaseData instanceof DynamicData)) {
            dynamicBaseDatas.remove(position);
            return;
        }

        //绑定Tag对象
        ClassUtils.bindViewsTag(position, holder.unOriginalFavoriteNormal.iv_headImg,
                holder.unOriginalFavoriteNormal.ivHeart);

        //设置用户数据
        holder.unOriginalFavoriteNormal.tvName.setText(dynamicBaseData.getCreator_nickname());
        holder.unOriginalFavoriteNormal.tvTime.setText(dynamicBaseData.getCreated_at());
        holder.unOriginalFavoriteNormal.tvAction.setText(dynamicBaseData.getActionTitle());
        //设置头像
        String headImgUrl = dynamicBaseData.getCreator_avatar();
        if (!StringUtils.isEmpty(headImgUrl)) {
            bindImageViewAndUrl(holder.unOriginalFavoriteNormal.iv_headImg, headImgUrl);
        }
        //设置原创作者信息
//        if (!((DynamicData) dynamicBaseData).is_orginal()) {
        DynamicData.Originator originator = ((DynamicData) dynamicBaseData).getOriginator();
        if (originator != null) {
            holder.unOriginalFavoriteNormal.tvOriginalNickName.setVisibility(View.VISIBLE);
            holder.unOriginalFavoriteNormal.tvOriginalNickName.
                    setText(originator.getOriginator_nickname());
        } else {
            holder.unOriginalFavoriteNormal.tvOriginalNickName.setVisibility(View.GONE);
        }
//        } else {
//            holder.unOriginalFavoriteNormal.tvOriginalNickName.setVisibility(View.GONE);
//        }
        //设置作品信息
        DynamicData.Normal normal = ((DynamicData) dynamicBaseData).getNormal();
        if (normal == null) {
            dynamicBaseDatas.remove(position);
            notifyDataSetChanged();
        } else {
            List<String> image = normal.getImg();
            if (image != null && image.size() > 0) {
                //绑定图片
                bindImageViewAndUrl(holder.unOriginalFavoriteNormal.ivPic, image.get(0));
            } else {
                holder.unOriginalFavoriteNormal.ivPic.setVisibility(View.GONE);
            }
            holder.unOriginalFavoriteNormal.tvContent.setText(normal.getDoc());
        }
        holder.unOriginalFavoriteNormal.tvCommentNum.setText(
                ((DynamicData) dynamicBaseData).getComments_count() + "");
        holder.unOriginalFavoriteNormal.tvLikeNum.setText(
                ((DynamicData) dynamicBaseData).getOperation_count() + "");

        //动态是否被关注
        if (((DynamicData) dynamicBaseData).isHasFavorite()) {
            holder.unOriginalFavoriteNormal.ivHeart.setImageResource(R.drawable.ic_heart_red);
        } else {
            holder.unOriginalFavoriteNormal.ivHeart.setImageResource(R.drawable.ic_heart_list_white);
        }
    }

    /**
     * 解析普通數據
     * 绑定Tag对象：头像、喜欢
     */
    private void handleDynamicNormalData(ViewHolder holder, int position) {
        Log.d(TAG, "handleDynamicNormalData");
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        if (!(dynamicBaseData instanceof DynamicData)) {
            dynamicBaseDatas.remove(position);
            return;
        }
        //绑定Tag对象
        ClassUtils.bindViewsTag(position, holder.originalNormal.ivHeadImg,
                holder.originalNormal.ivHeart);

        //设置用户数据
        holder.originalNormal.tvName.setText(dynamicBaseData.getCreator_nickname());
        holder.originalNormal.tvTime.setText(dynamicBaseData.getCreated_at());
        holder.originalNormal.tvAction.setText(dynamicBaseData.getActionTitle());
        //设置头像
        String headImgUrl = dynamicBaseData.getCreator_avatar();
        if (!StringUtils.isEmpty(headImgUrl)) {
            bindImageViewAndUrl(holder.originalNormal.ivHeadImg, headImgUrl);
        }
        DynamicData.Normal normal = ((DynamicData) dynamicBaseData).getNormal();
        int imageNum = 0;
        if (normal == null) {
            //設置圖片
            initOriginalPicturesView(imageNum, holder);
        } else {
            List<String> image = normal.getImg();
            if (image == null || image.size() == 0) {
                imageNum = 0;
            } else {
                imageNum = image.size();
                bindNormalImage(holder, image);
            }
            //设置内容和图片
            holder.originalNormal.tvContent.setText(normal.getDoc());
            //設置圖片
            initOriginalPicturesView(imageNum, holder);
        }
        //设置其他
        holder.originalNormal.tvCommentNum.setText(
                ((DynamicData) dynamicBaseData).getComments_count() + "");
        holder.originalNormal.tvLikeNum.setText(
                ((DynamicData) dynamicBaseData).getOperation_count() + "");

        //动态是否被关注
        if (((DynamicData) dynamicBaseData).isHasFavorite()) {
            holder.originalNormal.ivHeart.setImageResource(R.drawable.ic_heart_red);
        } else {
            holder.originalNormal.ivHeart.setImageResource(R.drawable.ic_heart_list_white);
        }
    }

    /**
     * 绑定普通动态的图片
     */
    private void bindNormalImage(ViewHolder holder, List<String> image) {
        ImageView imageView[] = new ImageView[6];
        imageView[0] = holder.originalNormal.ivPic1;
        imageView[1] = holder.originalNormal.ivPic2;
        imageView[2] = holder.originalNormal.ivPic3;
        imageView[3] = holder.originalNormal.ivPic4;
        imageView[4] = holder.originalNormal.ivPic5;
        imageView[5] = holder.originalNormal.ivPic6;
        for (int i = 0; i < image.size(); i++) {
            bindImageViewAndUrl(imageView[i], image.get(i));
        }
    }

    /**
     * 解析话题数据
     * 绑定Tag对象：头像、喜欢
     */
    private void handleDynamicTopicData(ViewHolder holder, int position) {
        Log.d(TAG, "handleDynamicTopicData");
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        if (!(dynamicBaseData instanceof DynamicData)) {
            dynamicBaseDatas.remove(position);
            return;
        }

        //绑定Tag对象
        ClassUtils.bindViewsTag(position, holder.originalTopic.iv_headImg,
                holder.originalTopic.ivHeart);

        //设置用户数据
        holder.originalTopic.tvName.setText(dynamicBaseData.getCreator_nickname());
        holder.originalTopic.tvTime.setText(dynamicBaseData.getCreated_at());
        holder.originalTopic.tvAction.setText(dynamicBaseData.getActionTitle());
        //设置头像
        String headImgUrl = dynamicBaseData.getCreator_avatar();
        if (!StringUtils.isEmpty(headImgUrl)) {
            bindImageViewAndUrl(holder.originalTopic.iv_headImg, headImgUrl);
        }
        //解析话题的数据
        holder.originalTopic.tvTitle.setText(((DynamicData) dynamicBaseData).getTitle());
        holder.originalTopic.tvPartNum.setText(((DynamicData) dynamicBaseData).getParticipation() + "");
        //设置图片和内容
        DynamicData.Topic topic = ((DynamicData) dynamicBaseData).getTopic();
        if (topic != null) {
            DynamicData.Topic.TopicBean bean = topic.getTopic();
            if (bean != null) {
                holder.originalTopic.tvContent.setText(bean.getDoc());
                bindImageViewAndUrl(holder.originalTopic.iv_picture, bean.getImg());
            }
        }

        //动态是否被关注
        if (((DynamicData) dynamicBaseData).isHasFavorite()) {
            holder.originalTopic.ivHeart.setImageResource(R.drawable.ic_heart_red);
        } else {
            holder.originalTopic.ivHeart.setImageResource(R.drawable.ic_heart_list_white);
        }
    }

    /**
     * 解析订阅圈子操作的数据
     * 绑定Tag对象：头像
     */
    private void handleCircleSubscribe(ViewHolder holder, int position) {
        Log.d(TAG, "handleCircleSubscribe");
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        if (!(dynamicBaseData instanceof CircleData)) {
            dynamicBaseDatas.remove(position);
            return;
        }

        //绑定Tag对象
        ClassUtils.bindViewsTag(position, holder.unOriginalSubscribeCircle.ivHeadImg);

        //设置用户数据
        holder.unOriginalSubscribeCircle.tvName.setText(dynamicBaseData.getCreator_nickname());
        holder.unOriginalSubscribeCircle.tvTime.setText(dynamicBaseData.getCreated_at());
        holder.unOriginalSubscribeCircle.tvAction.setText(dynamicBaseData.getActionTitle());
        //设置头像
        String headImgUrl = dynamicBaseData.getCreator_avatar();
        if (!StringUtils.isEmpty(headImgUrl)) {
            bindImageViewAndUrl(holder.unOriginalSubscribeCircle.ivHeadImg, headImgUrl);
        }
        //设置圈子数据
        CircleData circleData = (CircleData) dynamicBaseData;
        holder.unOriginalSubscribeCircle.tvCircleName.setText(circleData.getName());
        holder.unOriginalSubscribeCircle.tvCircleContent.setText(circleData.getIntroduction());
        holder.unOriginalSubscribeCircle.tvCircleOperationNum.setText(circleData.getOperation_count() + "");
        holder.unOriginalSubscribeCircle.tvCircleSubscribeNum.setText(circleData.getDynamics_count() + "");
        holder.unOriginalSubscribeCircle.tvCircleType.setText(circleData.getType());
        //设置图片
        bindImageViewAndUrl(holder.unOriginalSubscribeCircle.ivCirclePicture,
                circleData.getPicture());
        bindBlurImageViewAndUrl(holder.unOriginalSubscribeCircle.ivCirclePicBg,
                circleData.getPicture(), 25, 5);
    }

    /**
     * 解析文章数据
     * 绑定Tag对象：头像、喜欢
     */
    private void handleDynamicArticleData(ViewHolder holder, int position) {
        DynamicBaseData dynamicBaseData = dynamicBaseDatas.get(position);
        if (!(dynamicBaseData instanceof DynamicData)) {
            dynamicBaseDatas.remove(position);
            return;
        }

        //绑定Tag对象
        ClassUtils.bindViewsTag(position, holder.originalArticle.iv_headImg,
                holder.originalArticle.ivHeart);

        //设置用户数据
        holder.originalArticle.tvName.setText(dynamicBaseData.getCreator_nickname());
        holder.originalArticle.tvTime.setText(dynamicBaseData.getCreated_at());
        holder.originalArticle.tvAction.setText(dynamicBaseData.getActionTitle());
        //设置头像
        String headImgUrl = dynamicBaseData.getCreator_avatar();
        if (!StringUtils.isEmpty(headImgUrl)) {
            bindImageViewAndUrl(holder.originalArticle.iv_headImg, headImgUrl);
        }
        //设置文章数据
        DynamicData.Article article = ((DynamicData) dynamicBaseData).getArticle();
        if (article != null) {
            List<DynamicData.Article.ArticleBean> articleBeen = article.getArticle();
            if (articleBeen != null && articleBeen.size() > 0) {
                DynamicData.Article.ArticleBean bean = articleBeen.get(0);
                holder.originalArticle.tvContent.setText(bean.getDoc());
                String picUrl = bean.getImg();
                bindImageViewAndUrl(holder.originalArticle.iv_picture, picUrl);
            }
        }
        //设置文章标题
        holder.originalArticle.tvTitle.setText(((DynamicData) dynamicBaseData).getTitle());
        holder.originalArticle.tvCommentNum.setText(
                ((DynamicData) dynamicBaseData).getComments_count() + "");
        holder.originalArticle.tvLikeNum.setText(
                ((DynamicData) dynamicBaseData).getOperation_count() + "");

        //动态是否被关注
        if (((DynamicData) dynamicBaseData).isHasFavorite()) {
            holder.originalArticle.ivHeart.setImageResource(R.drawable.ic_heart_red);
        } else {
            holder.originalArticle.ivHeart.setImageResource(R.drawable.ic_heart_list_white);
        }
    }


    /**
     * 绑定图片到网址上
     */
    private void bindImageViewAndUrl(ImageView iv_headImg, String headImgUrl) {
        ImageLoaderFactory.getInstance().displayImage(iv_headImg, headImgUrl);
    }

    /**
     * 绑定图片到网址上并设置高斯模糊效果
     */
    private void bindBlurImageViewAndUrl(
            ImageView iv_headImg, String headImgUrl, int radius, int downSampling) {
        ImageLoaderFactory.getInstance().displayBlurImage(iv_headImg, headImgUrl, radius, downSampling);
    }

    /**
     * 初始化图片控件
     *
     * @param picNum 图片数量
     * @param holder holder
     */
    private void initOriginalPicturesView(int picNum, ViewHolder holder) {
        Log.d(TAG, "picNum=" + picNum);
        holder.originalNormal.pictures.setVisibility(View.GONE);
        holder.originalNormal.pic1To3.setVisibility(View.GONE);
        holder.originalNormal.pic2To3.setVisibility(View.GONE);
        holder.originalNormal.pic4To6.setVisibility(View.GONE);
        //如果没图片不执行下面动作
        if (picNum <= 0) {
            return;
        }
        //有图片时肯定至少一张
        holder.originalNormal.pictures.setVisibility(View.VISIBLE);
        holder.originalNormal.pic1To3.setVisibility(View.VISIBLE);
        if (picNum >= 1) {
            holder.originalNormal.ivPic1.setVisibility(View.VISIBLE);
        }
        if (picNum >= 2) {
            holder.originalNormal.pic2To3.setVisibility(View.VISIBLE);
            holder.originalNormal.ivPic2.setVisibility(View.VISIBLE);
            holder.originalNormal.ivPic3.setVisibility(View.INVISIBLE);
        }
        if (picNum >= 3) {
            holder.originalNormal.ivPic3.setVisibility(View.VISIBLE);
        }
        if (picNum >= 4) {
            holder.originalNormal.pic4To6.setVisibility(View.VISIBLE);
            holder.originalNormal.ivPic4.setVisibility(View.VISIBLE);
            holder.originalNormal.ivPic5.setVisibility(View.INVISIBLE);
            holder.originalNormal.ivPic6.setVisibility(View.INVISIBLE);
        }
        if (picNum >= 5) {
            holder.originalNormal.ivPic5.setVisibility(View.VISIBLE);
        }
        if (picNum >= 6) {
            holder.originalNormal.ivPic6.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return dynamicBaseDatas.size();
    }

    /**
     * ViewHolder类
     */
    class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);
        }

        View contentView;
        int viewType;
        //原创
        OriginalNormal originalNormal;//原创：普通
        OriginalTopic originalTopic;//原创：话题
        OriginalArticle originalArticle;//原创：文章
        //转载
        UnOriginalSubscribeCircle unOriginalSubscribeCircle;//订阅圈子
        UnOriginalFavoriteNormal unOriginalFavoriteNormal;//喜欢普通动态
        UnOriginalFavoriteTopic unOriginalFavoriteTopic;//喜欢话题动态
        UnOriginalFavoriteArticle unOriginalFavoriteArticle;//喜欢的文章动态
    }

    /**
     * 原创——普通
     */
    class OriginalNormal {
        //关于图片
        View pictures;
        View pic1To3;
        View pic2To3;
        View pic4To6;
        ImageView ivPic1, ivPic2, ivPic3, ivPic4, ivPic5, ivPic6;
        //其他
        TextView tvName;
        TextView tvTime;
        ImageView ivHeadImg;//头像
        TextView tvContent;//内容
        TextView tvCommentNum;//评论人数
        TextView tvLikeNum;//喜欢的人数
        TextView tvAction;//动作

        ImageView ivHeart;//是否被关注的心型图标
    }

    /**
     * 原创——话题
     */
    class OriginalTopic {
        ImageView iv_picture;
        ImageView iv_headImg;
        TextView tvName;
        TextView tvTime;
        TextView tvPartNum;//参与人数
        TextView tvTitle;//标题
        TextView tvContent;//内容
        TextView tvLikeNum;//喜欢的人数
        TextView tvAction;//动作

        ImageView ivHeart;//是否被关注的心型图标
    }

    /**
     * 原创——文章
     */
    class OriginalArticle {
        ImageView iv_picture;
        ImageView iv_headImg;
        TextView tvName;
        TextView tvTime;
        TextView tvTitle;//标题
        TextView tvContent;//内容
        TextView tvCommentNum;//评论人数
        TextView tvLikeNum;//喜欢的人数
        TextView tvAction;//动作
        ImageView ivHeart;//是否被关注的心型图标
    }

    /**
     * 非原创：订阅圈子
     */
    class UnOriginalSubscribeCircle {
        ImageView ivCirclePicBg;
        ImageView ivCirclePicture;
        TextView tvCircleName;
        TextView tvCircleContent;
        TextView tvCircleOperationNum;//动态数
        TextView tvCircleType;
        TextView tvCircleSubscribeNum;//订阅数
        ImageView ivHeadImg;
        TextView tvName;
        TextView tvTime;
        TextView tvAction;
    }

    /**
     * 非原创——喜欢：普通动态
     */
    class UnOriginalFavoriteNormal {
        //作者信息
        ImageView iv_headImg;
        TextView tvName;
        TextView tvTime;
        TextView tvAction;//动作
        //原创作者信息
        TextView tvOriginalNickName;//原创作者名称
        //相关作品信息
        ImageView ivPic;
        TextView tvContent;//内容
        TextView tvCommentNum;//评论人数
        TextView tvLikeNum;//喜欢的人数

        ImageView ivHeart;//是否被关注的心型图标
    }

    /**
     * 非原创——喜欢：话题动态
     */
    class UnOriginalFavoriteTopic {
        //作者信息
        ImageView iv_headImg;
        TextView tvName;
        TextView tvTime;
        TextView tvAction;//动作
        //原创作者信息
        TextView tvOriginalNickName;//原创作者名称
        //相关作品信息
        ImageView ivPic;
        TextView tvTopicTitle;
        TextView tvPartNum;//参与人数
        TextView tvContent;//内容
        TextView tvCommentNum;//评论人数
        TextView tvLikeNum;//喜欢的人数
    }

    /**
     * 非原创——喜欢：文章动态
     */
    class UnOriginalFavoriteArticle {
        //作者信息
        ImageView iv_headImg;
        TextView tvName;
        TextView tvTime;
        TextView tvAction;//动作
        //原创作者信息
        TextView tvOriginalNickName;//原创作者名称
        //相关作品信息
        ImageView ivPic;
        TextView tvArticleTitle;
        TextView tvContent;//内容
        TextView tvCommentNum;//评论人数
        TextView tvLikeNum;//喜欢的人数
    }


    /**
     * 图片点击回调类
     */
    public interface OnPictureClickLisnener {
        /**
         * 图片点击的回调方法
         *
         * @param picUrl 图片地址
         */
        void onPicClick(String picUrl);
    }

    /**
     * 喜欢的点击回调类
     */
    public interface OnLikeActionClickListener {
        /**
         * 点击喜欢的回调方法
         *
         * @param position 条目数
         */
        void onLikeClick(int position);
    }

    /**
     * 头像的点击回调接口
     */
    public interface OnUserClickListener {
        /**
         * 点击用户的回调类
         *
         * @param position 条目数
         */
        void onUserClick(int position);
    }

    /**
     * 分享的点击回调类
     */
    public interface OnShareClickListener {

        /**
         * 点击分享的回调类
         *
         * @param position 条目数
         */
        void onShareClick(int position);
    }

    /**
     * 分享的评论回调类
     */
    public interface OnCommentClickListener {

        /**
         * 点击分享的回调类
         *
         * @param position 条目数
         */
        void onCommentClick(int position);
    }

    /**
     * 用户头像点击的监听事件
     */
    private View.OnClickListener clickUserListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //判断各种控件id
            Bundle bundle = (Bundle) view.getTag();
            if (bundle == null) {
                return;
            }
            int viewId = bundle.getInt(com.jkb.mrcampus.Config.BUNDLE_KEY_VIEW_ID);
            int position = bundle.getInt(com.jkb.mrcampus.Config.BUNDLE_KEY_VIEW_POSITION);
            //判断id
            switch (viewId) {
                case R.id.idon_iv_headImg:
                case R.id.idoa_iv_headImg:
                case R.id.idot_iv_headImg:
                case R.id.iidu_iv_headImg:
                case R.id.ius_iv_headImg:
                    onUserClick(position);
                    break;
            }
        }
    };
    /**
     * 用户头像点击的监听事件
     */
    private View.OnClickListener clickLikeListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //判断各种控件id
            Bundle bundle = (Bundle) view.getTag();
            if (bundle == null) {
                return;
            }
            int viewId = bundle.getInt(com.jkb.mrcampus.Config.BUNDLE_KEY_VIEW_ID);
            int position = bundle.getInt(com.jkb.mrcampus.Config.BUNDLE_KEY_VIEW_POSITION);
            //判断id
            switch (viewId) {
                case R.id.idon_iv_heart:
                case R.id.idoa_iv_heart:
                case R.id.idot_iv_heart:
                case R.id.idfn_iv_heart:
                    OnLikeClick(position);
                    break;
            }
        }
    };

    /**
     * 用户点击回调
     *
     * @param position 条目数
     */
    private void onUserClick(int position) {
        if (onUserClickListener != null) {
            onUserClickListener.onUserClick(position);
        }
    }

    /**
     * 喜欢的点击回调
     *
     * @param position 条目数
     */
    private void OnLikeClick(int position) {
        if (onLikeActionClickListener != null) {
            onLikeActionClickListener.onLikeClick(position);
        }
    }

    /**
     * 设置用户头像的点击回调
     */
    public void setOnUserClickListener(@NonNull OnUserClickListener onUserClickListener) {
        this.onUserClickListener = onUserClickListener;
    }

    public void setOnLikeActionClickListener(OnLikeActionClickListener onLikeActionClickListener) {
        this.onLikeActionClickListener = onLikeActionClickListener;
    }
}
