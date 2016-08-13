package com.jkb.mrcampus;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.jkb.api.ApiFactoryImpl;
import com.jkb.model.info.SingletonManager;
import com.jkb.model.info.UserInfoSingleton;
import com.jkb.model.net.ImageLoaderFactory;
import com.jkb.model.utils.Config;
import com.jkb.mrcampus.singleton.ActivityStackManager;

import cn.sharesdk.framework.ShareSDK;
import jkb.mrcampus.db.MrCampusDB;

/**
 * 该APP的Application类
 * Created by JustKiddingBaby on 2016/7/20.
 */
public class Mr_Campus extends Application {

    private MrCampusDB mrCampusDB = null;
    private static SingletonManager singletonManager = SingletonManager.newInstance();
    //shareSDK的appkey
    private static final String SHARE_SDK_APP_KEY = "15b05f956cc6c";

    @Override
    public void onCreate() {
        super.onCreate();// 多进程导致多次初始化Application,这里只初始化App主进程的Application
        initInNewThread();
    }

    /**
     * 在新线程中初始化
     */
    private void initInNewThread() {
        initSDK();
        initDb();
        initSingleton();
    }

    /**
     * 初始化数据库
     */
    private void initDb() {
        //初始化数据库的单例类
        mrCampusDB = MrCampusDB.getInstance();
        mrCampusDB.initDb(getApplicationContext());
    }

    /**
     * 初始化单例的实例类
     */
    private void initSingleton() {
        //初始化API层的工厂类
        ApiFactoryImpl apiFactory = ApiFactoryImpl.newInstance();
        //初始化图片加载器配置
        ImageLoaderFactory imageLoaderFactory = ImageLoaderFactory.getInstance();
        imageLoaderFactory.setApplicationContext(getApplicationContext());
        imageLoaderFactory.create();
        //初始化个人数据的单例类
        UserInfoSingleton userInfoSingleton = UserInfoSingleton.getInstance();
        //初始化Activity管理者
        ActivityStackManager activityStackManager = ActivityStackManager.getInstance();


        //添加到单例的管理类中
        SingletonManager.registerService(Config.SINGLETON_KEY_CAMPUSDB, mrCampusDB);
        SingletonManager.registerService(Config.SINGLETON_KEY_APIFACTORY, apiFactory);
        SingletonManager.registerService(Config.SINGLETON_KEY_IMAGELOADER, imageLoaderFactory);
        SingletonManager.registerService(Config.SINGLETON_KEY_USERINFO, userInfoSingleton);
        SingletonManager.registerService(Config.SINGLETON_KEY_ACTIVITYSTACKMANAGER, activityStackManager);
    }

    /**
     * 初始化SDK
     */
    private void initSDK() {
        //初始化百度地图
        SDKInitializer.initialize(getApplicationContext());
        //初始化ShareSDK
        ShareSDK.initSDK(getApplicationContext(), SHARE_SDK_APP_KEY);
    }

    /**
     * 返回单例管理类的对象
     */
    public static SingletonManager getSingletonManager() {
        return singletonManager;
    }

}
