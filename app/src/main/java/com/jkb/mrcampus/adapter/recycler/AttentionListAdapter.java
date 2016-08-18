package com.jkb.mrcampus.adapter.recycler;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jkb.core.presenter.usersList.data.UserData;
import com.jkb.model.net.ImageLoaderFactory;
import com.jkb.model.utils.StringUtils;
import com.jkb.mrcampus.R;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * 关注用户列表的适配器
 * Created by JustKiddingBaby on 2016/8/17.
 */

public class AttentionListAdapter extends RecyclerView.Adapter<AttentionListAdapter.ViewHolder> {

    private Context context;
    private int colorWhite;
    private int colorGravy;
    public List<UserData> userDatas;


    public AttentionListAdapter(Context context, List<UserData> userDatas) {
        this.context = context;
        colorWhite = context.getResources().getColor(R.color.white);
        colorGravy = context.getResources().getColor(R.color.background_general);

        if (userDatas == null) {
            userDatas = new ArrayList<>();
        }
        this.userDatas = userDatas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_userslist, parent, false);
        AttentionListAdapter.ViewHolder holder = new AttentionListAdapter.ViewHolder(view);
        //初始化控件
        holder.ivHeadImg = (CircleImageView) view.findViewById(R.id.iul_iv_headImg);
        holder.tvAttention = (TextView) view.findViewById(R.id.iul_tv_attentionStatus);
        holder.tvNickName = (TextView) view.findViewById(R.id.iul_tv_nickName);
        holder.tvSign = (TextView) view.findViewById(R.id.iul_tv_sign);
        holder.tvTime = (TextView) view.findViewById(R.id.iul_tv_time);
        holder.contentView = view.findViewById(R.id.iul_ll_content);
        //初始化监听器
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tvTime.setVisibility(View.GONE);
        //绑定数据
        if (position % 2 == 0) {//偶数
            holder.contentView.setBackgroundColor(colorGravy);
        } else {//奇数
            holder.contentView.setBackgroundColor(colorWhite);
        }
        UserData userData = userDatas.get(position);
        holder.tvNickName.setText(userData.getNickname());
        holder.tvSign.setText(userData.getBref_introduction());
        String headImgUrl = userData.getAvatar();
        if (!StringUtils.isEmpty(headImgUrl)) {
            setImageLoad(holder.ivHeadImg, headImgUrl);
        } else {
            holder.ivHeadImg.setImageResource(R.drawable.ic_user_head);
        }
        //设置头像
        if (!userData.isAttentioned()) {
            holder.tvAttention.setText("关注");
        } else {

        }
    }

    /**
     * 加载头像
     */
    private void setImageLoad(final ImageView tvHeadImg, String headImgUrl) {
        ImageLoaderFactory.getInstance().loadImage(headImgUrl, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                tvHeadImg.setImageResource(R.drawable.ic_user_head);
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                tvHeadImg.setImageResource(R.drawable.ic_user_head);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                tvHeadImg.setImageBitmap(bitmap);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                tvHeadImg.setImageResource(R.drawable.ic_user_head);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userDatas.size();
    }

    /**
     * ViewHolder类
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }

        public TextView tvNickName;//昵称
        public TextView tvTime;//时间
        public TextView tvSign;//签名
        public TextView tvAttention;//是否关注
        public CircleImageView ivHeadImg;//头像
        public View contentView;//包裹的背景
    }
}