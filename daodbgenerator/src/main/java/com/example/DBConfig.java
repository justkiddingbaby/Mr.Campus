package com.example;

/**
 * 数据库配置常量类
 * Created by JustKiddingBaby on 2016/8/6.
 */

public class DBConfig {
    //数据库的版本号
    public static final int VERSION_NUM = 1;
    //实体生成的包名
    public static final String ENTITY_PACKAGE_NAME = "jkb.mrcampus.db.entity";
    //指定dao层模版的包
    public static final String DAO_PACKAGE_NAME = "jkb.mrcampus.db.dao";
    //自动生成模版类存放的绝对路径，即java-gen所在的路径
    public static final String AUTO_GENERATE_JAVA_PATH = "D:\\App\\JustKiddingBaby\\AndroidStudioProjects\\Mr.Campus\\lib_db\\src\\main\\java-gen";

    //用户实体的名称
    public static final String ENTITY_USERAUTHS = "UserAuths";
    public static final String ENTITY_USERS = "Users";
    public static final String ENTITY_STATUS = "Status";
    public static final String ENTITY_SCHOOLS = "Schools";
    public static final String ENTITY_MESSAGES = "Messages";

    //数据库表名称
    public static final String TABLE_USERAUTHS = "userAuths";
    public static final String TABLE_USERS = "users";
    public static final String TABLE_STATUS = "status";
    public static final String TABLE_SCHOOLS = "schools";
    public static final String TABLE_MESSAGES = "messages";

    public static final String USER_ID = "user_id";
    public static final String IDENTITY_TYPE = "identity_type";//账户类型
    public static final String CREDENTIAL = "credential";//密码
    public static final String IDENTIFIER = "identifier";//账号
    public static final String UPDATED_AT = "updated_at";//更新时间
    public static final String CREATED_AT = "created_at";//创建时间
    public static final String UID = "UID";
    public static final String NICKNAME = "nickname";
    public static final String AVATAR = "avatar";
    public static final String SEX = "sex";
    public static final String NAME = "name";
    public static final String TOKEN = "token";
    public static final String RONG_IM_TOKEN = "rong_im_token";
    public static final String BREF_INTRODUCTION = "bref_introduction";
    public static final String BACKGROUND = "background";
    public static final String SCHOOL_ID = "school_id";
    public static final String SCHOOL_NAME = "school_name";
    public static final String VERSION = "version";//缓存的版本号
    public static final String FLAG_LOGIN = "flag_login";//是否登录系统
    public static final String FLAG_SELECT_SEHOOL = "flag_select_school";//是否选择学校
    public static final String SNAME = "sname";
    public static final String BADGE = "badge";//校徽图片地址
    public static final String SUMMARY = "summary";
    public static final String AVATAR_LOCAL_PATH = "avatarLocalPath";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String SENDER_ID = "senderId";
    public static final String TARGET_ID = "targetId";
    public static final String TARGET_TYPE = "targetType";
    public static final String TARGET_NAME = "targetName";
    public static final String TARGET_PICTURE = "targetPicture";
    public static final String SENDER_TYPE = "senderType";
    public static final String SENDER_NAME = "senderName";
    public static final String SENDER_PICTURE = "senderPicture";
    public static final String RELATION_CONTENT = "relationContent";
    public static final String ACTION = "action";
    public static final String MSG_CONTENT = "msg_content";
    public static final String IS_READED = "is_read";

    public static final String ATTENTIONCOUNT = "attentionCount";//关注圈子总数
    public static final String ATTENTIONUSERCOUNT = "attentionUserCount";//关注用户总数
    public static final String FANSCOUNT = "fansCount";//粉丝总数
    public static final String VISITORCOUNT = "visitorCount";//访客总数
}
