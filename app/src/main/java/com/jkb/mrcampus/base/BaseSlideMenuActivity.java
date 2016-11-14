package com.jkb.mrcampus.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.jkb.core.contract.dynamicCreate.data.CategoryTypeData;
import com.jkb.core.control.userstate.LoginContext;
import com.jkb.model.utils.LogUtils;
import com.jkb.mrcampus.Config;
import com.jkb.mrcampus.R;
import com.jkb.mrcampus.activity.CircleActivity;
import com.jkb.mrcampus.activity.CircleListActivity;
import com.jkb.mrcampus.activity.CommentActivity;
import com.jkb.mrcampus.activity.CreateCircleActivity;
import com.jkb.mrcampus.activity.DynamicCreateActivity;
import com.jkb.mrcampus.activity.DynamicDetailActivity;
import com.jkb.mrcampus.activity.MessageActivity;
import com.jkb.mrcampus.activity.MessageCenterActivity;
import com.jkb.mrcampus.activity.MyOriginalDynamicActivity;
import com.jkb.mrcampus.activity.MyUnOriginalDynamicActivity;
import com.jkb.mrcampus.activity.PersonCenterActivity;
import com.jkb.mrcampus.activity.UsersListActivity;
import com.jkb.mrcampus.fragment.dialog.ChoosePictureFragment;
import com.jkb.mrcampus.fragment.dialog.CircleFilterFloatFragment;
import com.jkb.mrcampus.fragment.dialog.GifLoadingView2;
import com.jkb.mrcampus.fragment.dialog.HintDetermineFloatFragment;
import com.jkb.mrcampus.fragment.dialog.InputTextFloatFragment;
import com.jkb.mrcampus.fragment.dialog.SelectSchoolFloatFragment;
import com.jkb.mrcampus.fragment.dialog.SexFilterFloatFragment;
import com.jkb.mrcampus.fragment.dialog.ShareDynamicDialogFragment;
import com.jkb.mrcampus.fragment.dialog.TagFloatFragment;
import com.jkb.mrcampus.fragment.dialog.TextFloatFragment;
import com.jkb.mrcampus.fragment.dialog.WriteDynamicDialogFragment;
import com.jkb.mrcampus.helper.ActivityUtils;
import com.jkb.mrcampus.net.ShareFactory;
import com.jkb.mrcampus.singleton.ActivityStackManager;
import com.jkb.mrcampus.utils.ClassUtils;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.rong.imkit.RongIM;

/**
 * 含有主体菜单的Activity基类
 * Created by JustKiddingBaby on 2016/11/7.
 */

public abstract class BaseSlideMenuActivity extends SlidingFragmentActivity
        implements BaseActivityAction {

    protected String TAG = this.getClass().getSimpleName();
    protected Context context;
    protected View rootView;
    //页面是否发生内存重启并且有数据
    protected boolean savedInstanceStateValued = false;
    private static boolean isExit = false;//连按两次退出程序的标识值

    protected FragmentManager fm;

    //展示视图
    protected GifLoadingView2 gifLoadingView;
    private ChoosePictureFragment choosePictureFragment;
    private TextFloatFragment textFloatFragment;
    private InputTextFloatFragment inputTextFloatFragment;
    private SexFilterFloatFragment sexFilterFloatFragment;
    private WriteDynamicDialogFragment writeDynamicDialogFragment;
    private ShareDynamicDialogFragment shareDynamicDialogFragment;
    private TagFloatFragment tagFloatFragment;
    private SelectSchoolFloatFragment selectSchoolFloatFragment;
    private HintDetermineFloatFragment hintDetermineFloatFragment;
    private HintDetermineFloatFragment newHintDetermineFloatFragment;
    private CircleFilterFloatFragment circleFilterFloatFragment;

    //单例类
    protected ActivityStackManager activityManager;

    private ShareFactory shareFactory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //添加Activity到管理者
        activityManager = ActivityStackManager.getInstance();
        activityManager.addActivity(this);
        //初始化是否内存重启的标识
        if (savedInstanceState != null) {
            savedInstanceStateValued = true;
        } else {
            savedInstanceStateValued = false;
        }
    }

    /**
     * 初始化方法
     */
    protected void init(Bundle savedInstanceState) {
        context = this;
        fm = getSupportFragmentManager();
        initView();
        initData(savedInstanceState);
        initListener();
    }

    /**
     * 初始化监听器
     */
    protected abstract void initListener();

    /**
     * 初始化数据
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 初始化View
     */
    protected abstract void initView();

    /**
     * 显示Fragment
     *
     * @param fragmentName 类的Name
     */
    public abstract void showFragment(String fragmentName);

    /**
     * 设置父布局的ID
     */
    protected void setRootView(@NonNull int rootViewId) {
//        checkNotNull(rootViewId, "传入布局id不能为空！");
        this.rootView = LayoutInflater.from(this).inflate(rootViewId, null);
        setContentView(this.rootView);
    }

    /**
     * 恢复各个View层的Presenter
     */
    protected void restoreFragments() {
        List<Fragment> fragments = fm.getFragments();
        if (fragments == null || fragments.size() == 0) {
            return;
        }
        for (Fragment fragment : fragments) {
            if (fragment == null) {
                continue;
            }
            restoreFragments(fragment.getClass().getName());
        }
    }

    /**
     * 恢复添加过的Presenter
     */
    protected abstract void restoreFragments(String fragmentTAG);

    /**
     * 初始化展示的Fragment步骤1
     */
    protected void initFragmentStep1(Class<?> fragmentClass) {
        //判断是否被添加过
        if (!ActivityUtils.isFragmentAdded(fm, fragmentClass.getName())) {
            initFragmentStep2(fragmentClass);
        } else {
            if (savedInstanceStateValued) {//判断是否发生了内存重启
                Log.i(TAG, "发生了内存重启需要初始化fragment----------------");
                initFragmentStep2(fragmentClass);
            }
        }
    }

    /**
     * 初始化Fragment步骤2
     */
    protected abstract void initFragmentStep2(Class<?> fragmentClass);

    /**
     * 开启新的Activity使用左侧进入动画
     */
    protected void startActivityWithPushLeftAnim(Intent intent) {
//        ActivityOptionsCompat compat = ActivityOptionsCompat.makeCustomAnimation(this,
//                R.animator.push_left_in, R.animator.push_left_out);
//        ActivityCompat.startActivity(this, intent, compat.toBundle());
        startActivity(intent);
        overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);
    }

    /**
     * 关闭当前Activity并使用右侧滑出动画
     */
    public void activitySwithPushRightAnim() {
        overridePendingTransition(R.animator.push_right_in, R.animator.push_right_out);
//        ActivityCompat.finishAfterTransition(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        finish();
        activitySwithPushRightAnim();
    }

    /**
     * 顯示Toast信息：短的
     */
    public void showShortToast(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
    }

    /**
     * 显示Toast信息：长的
     */
    public void showLongToast(String value) {
        if (value == null || value.isEmpty()) {
            return;
        }
        Toast.makeText(context, value, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityManager.removeActivity(this);
        rootView = null;
        //销毁所有的Fragment
        ActivityUtils.removeAllFragment(fm);
        System.gc();
    }

    /**
     * 连续按两次退出
     */
    public void exitBy2Click() {
        Timer tExit = null;
        if (isExit == false) {
            isExit = true;
            // 准备退出
            showShortToast("再按一次退出程序");
            tExit = new Timer();
            tExit.schedule(new TimerTask() {
                @Override
                public void run() {
                    isExit = false; // 取消退出
                }
            }, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
        } else {
            //退出程序
            activityManager.exit();
        }
    }

    @Override
    public void startDynamicActivity(@NonNull int dynamic_id, @NonNull String dynamicType) {
        if (dynamic_id <= 0) {
            showShortToast("动态不存在");
            return;
        }
        Intent intent = new Intent(this, DynamicDetailActivity.class);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_ID, dynamic_id);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_TYPE, dynamicType);
        startActivityWithPushLeftAnim(intent);
    }

    /**
     * 显示圈子页面
     */
    @Override
    public void startCircleActivity(@NonNull int circle_id) {
        if (circle_id <= 0) {
            showShortToast("圈子不存在");
            return;
        }
        Intent intent = new Intent(this, CircleActivity.class);
        intent.putExtra(Config.INTENT_KEY_CIRCLE_ID, circle_id);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startPersonalCenterActivity(int user_id) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Log.d(TAG, "startPersonalCenterActivity");
        Intent intent = new Intent(this, PersonCenterActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startUsersListActivity(int user_id, String action) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Log.d(TAG, "startUsersListActivity");
        Intent intent = new Intent(this, UsersListActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        intent.putExtra(Config.INTENT_KEY_SHOW_USERSLIST, action);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startCommentListActivity(@NonNull int dynamicId) {
        if (dynamicId <= 0) {
            showShortToast("动态不存在");
            return;
        }
        Log.d(TAG, "startCommentActivity");
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(Config.INTENT_KEY_TARGET_ID, dynamicId);
        intent.putExtra(Config.INTENT_KEY_SHOW_COMMENT,
                CommentActivity.ACTION_SHOW_VIEW_COMMENT_LIST);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startCommentSingleAllActivity(@NonNull int commentId, @NonNull int dynamicId) {
        if (dynamicId <= 0) {
            showShortToast("动态不存在");
            return;
        }
        Log.d(TAG, "startCommentActivity");
        Intent intent = new Intent(this, CommentActivity.class);
        intent.putExtra(Config.INTENT_KEY_TARGET_ID, commentId);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_ID, dynamicId);
        intent.putExtra(Config.INTENT_KEY_SHOW_COMMENT,
                CommentActivity.ACTION_SHOW_VIEW_COMMENT_SINGLE_ALL);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startDynamicCreateActivity(@NonNull String dynamicCreateType, int circle_id) {
        Log.d(TAG, "startCommentActivity");
        if (!LoginContext.getInstance().isLogined()) {
            showShortToast("请登录后再进行操作");
            return;
        }
        Intent intent = new Intent(this, DynamicCreateActivity.class);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_CREATE_TYPE, dynamicCreateType);
        if (circle_id > 0) {
            intent.putExtra(Config.INTENT_KEY_CIRCLE_ID, circle_id);
        }
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startCircleListActivity(@NonNull int user_id) {
        Intent intent = new Intent(this, CircleListActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startCreateCircleActivity() {
        //显示创建圈子视图
        Intent intent = new Intent(this, CreateCircleActivity.class);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMessageCenterActivity() {
        Log.d(TAG, "startCommentActivity");
        Intent intent = new Intent(this, MessageCenterActivity.class);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMessageActivity(@NonNull int messageType) {
        Log.d(TAG, "startCommentActivity");
        if (messageType != MessageActivity.MESSAGE_TYPE_SYSTEM) {
            if (!LoginContext.getInstance().isLogined()) {
                showShortToast("请登录后再进行操作");
                return;
            }
        }
        Intent intent = new Intent(this, MessageActivity.class);
        intent.putExtra(Config.INTENT_KEY_MESSAGE_TYPE, messageType);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMyDynamicArticleActivity(@NonNull int user_id) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Intent intent = new Intent(this, MyOriginalDynamicActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_TYPE, MyOriginalDynamicActivity.MY_DYNAMIC_TYPE_ARTICLE);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMyDynamicCircleActivity(@NonNull int user_id) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Intent intent = new Intent(this, MyOriginalDynamicActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_TYPE,
                MyOriginalDynamicActivity.MY_DYNAMIC_TYPE_CIRCLE);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMyDynamicNormalActivity(@NonNull int user_id) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Intent intent = new Intent(this, MyOriginalDynamicActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_TYPE, MyOriginalDynamicActivity.MY_DYNAMIC_TYPE_NORMAL);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMyDynamicTopicActivity(@NonNull int user_id) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Intent intent = new Intent(this, MyOriginalDynamicActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_TYPE, MyOriginalDynamicActivity.MY_DYNAMIC_TYPE_TOPIC);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startMyFavoriteActivity(@NonNull int user_id) {
        if (user_id <= 0) {
            showShortToast("用户不存在");
            return;
        }
        Intent intent = new Intent(this, MyUnOriginalDynamicActivity.class);
        intent.putExtra(Config.INTENT_KEY_USER_ID, user_id);
        intent.putExtra(Config.INTENT_KEY_DYNAMIC_TYPE, MyUnOriginalDynamicActivity.TYPE_MY_FAVORITE);
        startActivityWithPushLeftAnim(intent);
    }

    @Override
    public void startPrivateConversation(@NonNull int user_id) {
        if (!LoginContext.getInstance().isLogined()) {
            showShortToast("请您先去登录再进行操作");
            return;
        }
        if (RongIM.getInstance() != null) {
            RongIM.getInstance().startPrivateChat(this, user_id + "", "私聊");
        } else {
            showShortToast("未连接聊天服务器");
        }
    }

    @Override
    public void showSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.showSoftInput(getCurrentFocus(),
                        InputMethodManager.SHOW_FORCED);
        }
    }

    @Override
    public void hideSoftInputView() {
        InputMethodManager manager = ((InputMethodManager) this
                .getSystemService(Activity.INPUT_METHOD_SERVICE));
        if (getWindow().getAttributes().softInputMode !=
                WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null)
                manager.hideSoftInputFromWindow(getCurrentFocus()
                        .getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            getWindow().setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        }
    }

    @Override
    public void showSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
//        if (isOpen) {
//            return;
//        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    @Override
    public void hideSoftKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
//        if (!isOpen) {
//            return;
//        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0); //强制隐藏键盘
    }

    @Override
    public boolean isKeyboardShown(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        boolean isOpen = imm.isActive();//isOpen若返回true，则表示输入法打开
        return isOpen;
    }

    /**
     * 显示Loading加载效果
     */
    public synchronized void showLoading(String value) {
        if (gifLoadingView == null) {
            gifLoadingView = GifLoadingView2.newInstance();
            gifLoadingView.setImageResource(R.drawable.num31);
        }
        LogUtils.d(TAG, "showLoading");
        if (!gifLoadingView.isAdded()) {
            gifLoadingView.show(getFragmentManager(),
                    ClassUtils.getClassName(GifLoadingView2.class));
        }
    }

    /**
     * 取消加载的loading效果
     */
    public void dismissLoading() {
        if (gifLoadingView != null) {
            if (gifLoadingView.isAdded()) {
                gifLoadingView.dismiss();
            }
        }
    }

    /**
     * 显示加载图片的Dialog
     */
    public void showChoosePictureDialog() {
        if (choosePictureFragment == null) {
            choosePictureFragment = new ChoosePictureFragment();
        }
        if (!choosePictureFragment.isAdded()) {
            choosePictureFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(ChoosePictureFragment.class));
        }
    }

    /**
     * 选择图片方式的监听器
     */
    public void setChoosePictureWayListener(ChoosePictureFragment.PictureChooseWayListener listener) {
        if (choosePictureFragment != null) {
            choosePictureFragment.setPictureSelectedListener(listener);
        }
    }

    /**
     * 取消所有的显示子视图
     */
    @Override
    public void dismiss() {
        if (choosePictureFragment != null && choosePictureFragment.isAdded()) {
            choosePictureFragment.dismiss();
        }
        if (textFloatFragment != null && textFloatFragment.isAdded()) {
            textFloatFragment.dismiss();
        }
        if (inputTextFloatFragment != null && inputTextFloatFragment.isAdded()) {
            inputTextFloatFragment.dismiss();
        }
        if (sexFilterFloatFragment != null && sexFilterFloatFragment.isAdded()) {
            sexFilterFloatFragment.dismiss();
        }
        //取消写动态视图的加载
        if (writeDynamicDialogFragment != null && writeDynamicDialogFragment.isAdded()) {
            writeDynamicDialogFragment.dismiss();
        }
        //取消分享动态视图的加载
        if (shareDynamicDialogFragment != null && shareDynamicDialogFragment.isAdded()) {
            shareDynamicDialogFragment.dismiss();
        }
        //取消展示TAG的显示
        if (tagFloatFragment != null && tagFloatFragment.isAdded()) {
            tagFloatFragment.dismiss();
        }
        //取消选择学校的显示
        if (selectSchoolFloatFragment != null && selectSchoolFloatFragment.isAdded()) {
            selectSchoolFloatFragment.dismiss();
        }
        //取消提示框的显示
        if (hintDetermineFloatFragment != null && hintDetermineFloatFragment.isAdded()) {
            hintDetermineFloatFragment.dismiss();
        }
        if (newHintDetermineFloatFragment != null && newHintDetermineFloatFragment.isAdded()) {
            newHintDetermineFloatFragment.dismiss();
        }
        if (circleFilterFloatFragment != null && circleFilterFloatFragment.isAdded()) {
            circleFilterFloatFragment.dismiss();
        }
        dismissLoading();
    }

    /**
     * 显示文本悬浮
     */
    public void showTextFloatView(String value) {
        if (textFloatFragment == null) {
            textFloatFragment = new TextFloatFragment(value);
        }
        if (!textFloatFragment.isAdded()) {
            textFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(TextFloatFragment.class));
        }
    }

    /**
     * 显示浮动的输入文本视图
     */
    public void showInputTextFloatView(
            String value,
            InputTextFloatFragment.OnSubmitClickListener listener) {
//        if (inputTextFloatFragment == null) {
        inputTextFloatFragment = new InputTextFloatFragment(listener, value);
//        }
        if (!inputTextFloatFragment.isAdded()) {
            inputTextFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(InputTextFloatFragment.class));
        }
    }

    /**
     * 显示性别的筛选视图
     *
     * @param sex      性别
     * @param listener 监听器
     */
    public void showSexFilterFloatView(
            String sex, SexFilterFloatFragment.SexFilterListener listener) {
        int sexType = -1;
        switch (sex) {
            case "男":
                sexType = SexFilterFloatFragment.SEX_TYPE_MAN;
                break;
            case "女":
                sexType = SexFilterFloatFragment.SEX_TYPE_FEMALE;
                break;
        }
        sexFilterFloatFragment = new SexFilterFloatFragment(sexType, listener);
        if (!sexFilterFloatFragment.isAdded()) {
            sexFilterFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(SexFilterFloatFragment.class));
        }
    }

    @Override
    public void showWriteDynamicView(WriteDynamicDialogFragment.OnWriteDynamicClickListener listener) {
        if (writeDynamicDialogFragment == null) {
            writeDynamicDialogFragment = new WriteDynamicDialogFragment(listener);
        }
        if (!writeDynamicDialogFragment.isAdded()) {
            writeDynamicDialogFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(WriteDynamicDialogFragment.class));
        }
    }

    @Override
    public void showShareDynamicView(ShareDynamicDialogFragment.OnShareItemClickListener listener) {
        if (shareDynamicDialogFragment == null) {
            shareDynamicDialogFragment = new ShareDynamicDialogFragment();
            shareDynamicDialogFragment.setOnShareItemClickListener(listener);
        }
        if (!shareDynamicDialogFragment.isAdded()) {
            shareDynamicDialogFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(ShareDynamicDialogFragment.class));
        }
    }

    @Override
    public void showTagFloatView(
            List<CategoryTypeData> categoryTypeDatas,
            TagFloatFragment.OnTagItemClickListener listener) {
        if (tagFloatFragment == null) {
            tagFloatFragment = new TagFloatFragment(categoryTypeDatas, listener);
        }
        if (!tagFloatFragment.isAdded()) {
            tagFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(TagFloatFragment.class));
        }
    }

    @Override
    public void showSelectSchoolView() {
        if (selectSchoolFloatFragment == null) {
            selectSchoolFloatFragment = new SelectSchoolFloatFragment();
        }
        if (!selectSchoolFloatFragment.isAdded()) {
            selectSchoolFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(SelectSchoolFloatFragment.class));
        }
    }

    @Override
    public void share(String title, String titleUrl, String text, String imageUrl, String url,
                      String site, String siteUrl) {
        if (shareFactory == null) {
            shareFactory = new ShareFactory(getApplicationContext());
        }
        shareFactory.share(title, titleUrl, text, imageUrl, url, site, siteUrl);
    }

    @Override
    public void showHintDetermineFloatView(
            String title, String content, String bt1Content, String bt2Content,
            HintDetermineFloatFragment.OnDetermineItemClickListener onDetermineItemClickListener) {
        if (hintDetermineFloatFragment == null) {
            hintDetermineFloatFragment = new HintDetermineFloatFragment(title, content,
                    bt1Content, bt2Content, onDetermineItemClickListener);
        }
        if (!hintDetermineFloatFragment.isAdded()) {
            hintDetermineFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(HintDetermineFloatFragment.class));
        }
    }

    @Override
    public void showNewHintDetermineFloatView(
            String title, String content, String bt1Content, String bt2Content,
            HintDetermineFloatFragment.OnDetermineItemClickListener onDetermineItemClickListener) {
        newHintDetermineFloatFragment = new HintDetermineFloatFragment(title, content,
                bt1Content, bt2Content, onDetermineItemClickListener);
        if (!newHintDetermineFloatFragment.isAdded()) {
            newHintDetermineFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(HintDetermineFloatFragment.class));
        }
    }

    @Override
    public void showCircleFilterFloatView(
            CircleFilterFloatFragment.OnCircleFilterItemClickListener listener) {
        if (circleFilterFloatFragment == null) {
            circleFilterFloatFragment = CircleFilterFloatFragment.newInstance(listener);
        }
        if (!circleFilterFloatFragment.isAdded()) {
            circleFilterFloatFragment.show(getFragmentManager(),
                    ClassUtils.getClassName(CircleFilterFloatFragment.class));
        }
    }
}
