package com.bright.mclib.base;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bright.mclib.R;
import com.bright.mclib.core.SystemBarTintManager;

import me.drakeet.materialdialog.MaterialDialog;

/**
 * Created by tci_mi on 16/10/8 上午11:34.
 */
public abstract class BaseActivity extends AppCompatActivity {

    public boolean isLandscape = false;
    // ****** MD 风格对话框 ******//
    public OnMDialogClickListener mOnMdialogPositiveClickListener = null;
    protected BaseActivity mBaseActivity = null;
    protected BaseApplication mApplication = null;
    protected boolean mIsCancelAction = false;
    protected boolean mIsLogoutAction = false;
    Toast mToast = null;
    MaterialDialog mMaterialDialog;
    private long waitTime = 2000;

    //**** rx ****//

    //    private CompositeSubscription mCompositeSubscription;
    private long touchTime = 0;
    private ProgressDialog dialog;

    /**
     * 解决Subscription内存泄露问题
     */
//    protected void addSubscription(Subscription s) {
//        if (this.mCompositeSubscription == null) {
//            this.mCompositeSubscription = new CompositeSubscription();
//        }
//        this.mCompositeSubscription.add(s);
//    }
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (this.mCompositeSubscription != null) {
//            this.mCompositeSubscription.unsubscribe();
//        }
//    }
    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mApplication = (BaseApplication) this.getApplication();
        mApplication.add(this);
        mBaseActivity = this;

        initSystemBarTint();
        SupportDisplay.initLayoutSetParams(BaseActivity.this);
        int layout = getContentViewLayoutResources();
        if (layout != 0) {
            setContentView(layout);
        }
        initResource(savedInstanceState);
    }

    /**
     * 获取内容布局
     *
     * @return 布局资源ID
     */
    protected abstract int getContentViewLayoutResources();

    /**
     * 初始化
     */
    protected abstract void initResource(Bundle savedInstanceState);

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        SupportDisplay.initLayoutSetParams(BaseActivity.this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        SupportDisplay.initLayoutSetParams(BaseActivity.this);
        super.onResume();
    }

    protected abstract void resetLayout();

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        resetLayout();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        resetLayout();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        resetLayout();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:// 点击返回图标事件
                finish();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void baseStartActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        baseStartActivity(intent);
    }

    public void baseStartActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);

    }

    protected void baseAddFragment(int container, Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        if (fragment != null) {
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.replace(container, fragment);
            }
        }
        fragmentTransaction.commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onKeyCodeBackListener();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            onSearchButtonListener();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @SuppressWarnings("unchecked")
    public <T extends View> T findView(int id) {
        return (T) findViewById(id);
    }

    /**
     * 按下安卓系统返回键的监听
     *
     * @Title onKeyCodeBackListener
     */
    @SuppressLint("SimpleDateFormat")
    private void onKeyCodeBackListener() {
        // 子类如果不是直接finish（），想自己处理的话，首先赋值 mIsCancelAction=true
        if (mIsCancelAction) {
            // 子类Activity覆盖此方法可以拦截返回键监听，在自己的页面中处理操作
            onCancelButtonListener();

        } else if (mIsLogoutAction) {// 这个值在具体的主页面设定为true，可以从这点击两次退出，其他页面自行处理

            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, "请再按一次退出程序!!", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                // 默认提示日期 为当天日期
//                SimpleDateFormat format = new SimpleDateFormat(
//                        "yyyy/MM/dd HH:mm:ss");
//                String loginTime = format.format(new Date());
//                mLoginDataManager.setLastLoginTime(loginTime);
                mApplication.finishAll();
            }

        } else {// 其他情况，默认关闭页面
            finish();
        }

    }

    /**
     * android系统返回键按下后，各页面独自处理方法
     */
    protected void onCancelButtonListener() {
    }

    protected void onSearchButtonListener() {
    }

    protected void shakeLayout(View view) {
        Animation shake = AnimationUtils.loadAnimation(BaseActivity.this, R.anim.common_mi_shake_layout);
        if (view != null && view.getVisibility() == View.VISIBLE) {
            view.startAnimation(shake);
        }

    }

    public String getStringExtra(String key) {
        String result = "";
        if (getIntent().getStringExtra(key) != null) {
            result = getIntent().getStringExtra(key);
        }
        return result;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 拨打电话号码
     *
     * @param telNo 电话号码
     */
    public void callTelPhone(String telNo) {
        if (telNo != null && !telNo.equals("")) {
            Intent intent = new Intent("android.intent.action.CALL",
                    Uri.parse("tel:" + telNo));
            startActivity(intent);
        }
    }

    /**
     * 提示拨打电话对话框
     */
    public android.support.v7.app.AlertDialog telDialog(String message, final String telNo) {

        return showDialog(mBaseActivity, null, message, null, "确定", "取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callTelPhone(telNo);
            }
        }, null, true);


    }

    /**
     * 公用提示框
     */
    public void showToast(final String info) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast != null && !isFinishing()) {
                    mToast.setText(info);
                    mToast.show();
                    return;
                }
                mToast = Toast.makeText(getApplicationContext(), info,
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    // ************　ToolBar 相关　start *************** //

    /**
     * 子类可以重写改变状态栏颜色
     */
    protected int setStatusBarColor() {
        return getColorPrimary();
    }

    /**
     * 子类可以重写决定是否使用透明状态栏
     */
    protected boolean translucentStatusBar() {
        return false;
    }

    /**
     * 设置状态栏颜色
     */
    protected void initSystemBarTint() {
        Window window = getWindow();
        if (translucentStatusBar()) {
            // 设置状态栏全透明
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(Color.TRANSPARENT);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            }
            return;
        }
        // 沉浸式状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0以上使用原生方法
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(setStatusBarColor());
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4-5.0使用三方工具类
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(setStatusBarColor());
        }
    }

    /**
     * 获取主题色
     */
    public int getColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    /**
     * 获取深主题色
     */
    public int getDarkColorPrimary() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimaryDark, typedValue, true);
        return typedValue.data;
    }

    /**
     * 初始化 Toolbar
     */
    public void initToolBar(Toolbar toolbar, boolean homeAsUpEnabled, String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(homeAsUpEnabled);
    }
    /**
     * 设置Toolbar 为ActionBar
     *
     * @param toolbarId Toolbar资源ID
     * @param showTitle 是否显示标题
     */
    public void setSupportActionBar(@IdRes int toolbarId, boolean showTitle) {
        Toolbar mToolbar = (Toolbar) findViewById(toolbarId);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setNavigationOnClickListener(new NavigationOnClickListener());
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayShowTitleEnabled(showTitle);
            }
        }
    }

    /**
     * 设置Toolbar 为ActionBar
     *
     * @param toolbarId Toolbar资源ID
     */
    public void setSupportActionBar(@IdRes int toolbarId) {
        setSupportActionBar(toolbarId, false);
    }

    public void initToolBar(Toolbar toolbar, boolean homeAsUpEnabled, int resTitle) {
        initToolBar(toolbar, homeAsUpEnabled, getString(resTitle));
    }
    /**
     * Toolbar 返回按钮点击
     */
    protected void onNavigationClick() {
        onBackPressed();
    }

    // ************　对话框相关　start *************** //
    private android.support.v7.app.AlertDialog showDialog(Context context, String title, String message, View contentView,
                                                          String positiveBtnText, String negativeBtnText,
                                                          DialogInterface.OnClickListener positiveCallback,
                                                          DialogInterface.OnClickListener negativeCallback,
                                                          boolean cancelable) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context,
                R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title == null ? "提示" : title);
        if (message != null) {
            builder.setMessage(message);
        }

        if (contentView != null) {
            builder.setView(contentView);
        }

        if (positiveBtnText != null) {
            builder.setPositiveButton(positiveBtnText, positiveCallback);
        }

        if (negativeBtnText != null) {
            builder.setNegativeButton(negativeBtnText, negativeCallback);
        }

        builder.setCancelable(cancelable);
        return builder.show();
    }

    // ************　ToolBar相关　end *************** //

    //普通对话框
    public android.support.v7.app.AlertDialog showSimpleDialog(Context context, String title, String message,
                                                               String positiveBtnText, String negativeBtnText,
                                                               DialogInterface.OnClickListener positiveCallback,
                                                               DialogInterface.OnClickListener negativeCallback,
                                                               boolean cancelable) {

        return showDialog(context, title, message, null, positiveBtnText, negativeBtnText, positiveCallback, negativeCallback, cancelable);
    }

    public void showLoading() {
        if (dialog != null && dialog.isShowing()) return;
        dialog = new ProgressDialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage("加载中...");
        dialog.show();
    }

    public void dismissLoading() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    public void showMDDialog(String title, String message) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                    }
                });

        mMaterialDialog.show();

    }

    public void showMDDialog(String title, String message, final OnMDialogClickListener onMDialogPositiveClickListener) {
        mMaterialDialog = new MaterialDialog(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("确认", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();
                        if (onMDialogPositiveClickListener != null) {
                            onMDialogPositiveClickListener.onPositiveClick(v);
                        }
                    }
                })
                .setNegativeButton("取消", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mMaterialDialog.dismiss();

                        if (onMDialogPositiveClickListener != null) {
                            onMDialogPositiveClickListener.onNegativeClick(v);
                        }
                    }
                });

        mMaterialDialog.show();
    }

    public void setmOnMdialogPositiveClickListener(OnMDialogClickListener mOnMdialogPositiveClickListener) {
        this.mOnMdialogPositiveClickListener = mOnMdialogPositiveClickListener;
    }

    public interface OnMDialogClickListener {

        void onPositiveClick(View v);

        void onNegativeClick(View v);
    }

    private class NavigationOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            onNavigationClick();
        }
    }

    /**
     * 添加弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     */
    class popwindowOnDismissListener implements
            android.widget.PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

    // ************　对话框相关　end *************** //
}
