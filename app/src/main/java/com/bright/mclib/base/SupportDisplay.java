package com.bright.mclib.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by tci_mi on 16/9/1.
 */
public class SupportDisplay {

    /**
     * 基准屏幕宽
     */
    private static final float BASIC_SCREEN_WIDTH = 640f;
    /**
     * 基准屏幕高
     */
    private static final float BASIC_SCREEN_HEIGHT = 960f;

    /**
     * 基准屏幕的dpi
     */
    private static final float BASIC_DENSITY = 2.0f;

    /**
     * 补充值
     */
    private static final float COMPLEMENT_VALUE = 0.5f;

    /**
     * 当前手机的屏幕宽
     */
    private static int mDisplayWidth;
    /**
     * 当前手机的屏幕高
     */
    private static int mDisplayHeight;
    /**
     * 水平比率
     */
    private static float mLayoutScale;
    /**
     * 垂直比率
     */
    private static float mVerticalScale;
    /**
     * dpi比
     */
    private static float mDensityScale;

    public static void initLayoutSetParams(Context context) {
        if (context == null) {
            return;
        }
        final WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        final Display disp = wm.getDefaultDisplay();
        final Point point = new Point();
        getSize(disp, point);
        mDisplayWidth = point.x;
        mDisplayHeight = point.y;
        mLayoutScale = mDisplayWidth / BASIC_SCREEN_WIDTH;
        mVerticalScale = mDisplayHeight / BASIC_SCREEN_HEIGHT;
        float density = context.getResources().getDisplayMetrics().density;
        mDensityScale = BASIC_DENSITY / density;
    }

    public static int getScreenWidth() {
        return Math.min(mDisplayWidth, mDisplayHeight);
    }

    /**
     * @param display System display.
     * @param outSize Target size.
     */
    @SuppressLint("NewApi")
    public static void getSize(Display display, Point outSize) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            display.getSize(outSize);
        } else {
            //noinspection deprecation
            outSize.x = display.getWidth();
            //noinspection deprecation
            outSize.y = display.getHeight();
        }
    }

    public static int calculateActualControlerSize(float sizedp) {
        return (int) (sizedp * mLayoutScale + COMPLEMENT_VALUE);
    }

    public static int calculateActualControlerSizeY(float sizedp) {
        return (int) (sizedp * mVerticalScale + COMPLEMENT_VALUE);
    }

    public static void resetContrlerTextSize(TextView textView, float textSizedp) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizedp
                * mLayoutScale * mDensityScale);

    }

    public static void resetContrlerTextSizeY(TextView textView,
                                              float textSizedp) {
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSizedp
                * mVerticalScale * mDensityScale);

    }

    public static void resetContrlerTextSize(Button button, float textSizedp) {
        resetContrlerTextSize((TextView) button, textSizedp);

    }

    /**
     * 获取屏幕的宽
     *
     * @return mDisplayWidth
     */
    public static int getmDisplayWidth() {
        return mDisplayWidth;
    }

    /**
     * 获取屏幕的高
     *
     * @return mDisplayHeight
     */
    public static int getmDisplayHeight() {
        return mDisplayHeight;
    }

    public static void resetAllChildViewParam(ViewGroup rootView,
                                              boolean isLandscape) {
        int childCount = rootView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = rootView.getChildAt(i);
            if (childView instanceof ViewGroup) {
                SupportDisplay.setChildViewParam((ViewGroup) childView,
                        isLandscape);
            } else {
                SupportDisplay.setViewParam(childView, isLandscape);
            }
        }
    }

    public static void resetAllChildViewParam(ViewGroup rootView) {
        resetAllChildViewParam(rootView, false);
    }

    public static void setChildViewParam(ViewGroup parentView,
                                         boolean isLandscape) {
        if (parentView == null) {
            return;
        }
        setViewParam(parentView, isLandscape);
        int childCount = parentView.getChildCount();
        if (childCount == 0) {
            return;
        }
        for (int i = 0; i < childCount; i++) {
            View childView = parentView.getChildAt(i);
            if (childView instanceof ViewGroup) {
                setChildViewParam((ViewGroup) childView, isLandscape);
            } else {
                setViewParam(childView, isLandscape);
            }
        }
    }

    public static void setViewParam(View view, boolean isLandscape) {
        ViewGroup.LayoutParams lp = view.getLayoutParams();
        int height = lp.height;
        if (height != -1 && height != -2) {
            if (isLandscape) {
                lp.height = SupportDisplay
                        .calculateActualControlerSizeY(height);
            } else {
                lp.height = SupportDisplay.calculateActualControlerSize(height);
            }
        }
        int width = lp.width;
        if (width != -1 && width != -2) {
            if (isLandscape) {
                lp.width = SupportDisplay.calculateActualControlerSizeY(width);
            } else {
                lp.width = SupportDisplay.calculateActualControlerSize(width);
            }
        }

        if (!(lp instanceof RelativeLayout.LayoutParams || lp instanceof LinearLayout.LayoutParams)) {
            return;
        }
        if (lp instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams param = (RelativeLayout.LayoutParams) view
                    .getLayoutParams();
            int leftMargin = param.leftMargin;
            if (leftMargin != -1) {
                if (isLandscape) {
                    param.leftMargin = SupportDisplay
                            .calculateActualControlerSizeY(leftMargin);
                } else {
                    param.leftMargin = SupportDisplay
                            .calculateActualControlerSize(leftMargin);
                }
            }
            int rightMargin = param.rightMargin;
            if (rightMargin != -1) {
                if (isLandscape) {
                    param.rightMargin = SupportDisplay
                            .calculateActualControlerSizeY(rightMargin);
                } else {
                    param.rightMargin = SupportDisplay
                            .calculateActualControlerSize(rightMargin);
                }
            }
            int topMargin = param.topMargin;
            if (topMargin != -1) {
                if (isLandscape) {
                    param.topMargin = SupportDisplay
                            .calculateActualControlerSizeY(topMargin);
                } else {
                    param.topMargin = SupportDisplay
                            .calculateActualControlerSize(topMargin);
                }
            }
            int bottomMargin = param.bottomMargin;
            if (bottomMargin != -1) {
                if (isLandscape) {
                    param.bottomMargin = SupportDisplay
                            .calculateActualControlerSizeY(bottomMargin);
                } else {
                    param.bottomMargin = SupportDisplay
                            .calculateActualControlerSize(bottomMargin);
                }
            }
        } else if (lp instanceof LinearLayout.LayoutParams) {
            LinearLayout.LayoutParams param = (LinearLayout.LayoutParams) view
                    .getLayoutParams();

            int leftMargin = param.leftMargin;
            if (leftMargin != -1) {
                if (isLandscape) {
                    param.leftMargin = SupportDisplay
                            .calculateActualControlerSizeY(leftMargin);
                } else {
                    param.leftMargin = SupportDisplay
                            .calculateActualControlerSize(leftMargin);
                }
            }
            int rightMargin = param.rightMargin;
            if (rightMargin != -1) {
                if (isLandscape) {
                    param.rightMargin = SupportDisplay
                            .calculateActualControlerSizeY(rightMargin);
                } else {
                    param.rightMargin = SupportDisplay
                            .calculateActualControlerSize(rightMargin);
                }
            }
            int topMargin = param.topMargin;
            if (topMargin != -1) {
                if (isLandscape) {
                    param.topMargin = SupportDisplay
                            .calculateActualControlerSizeY(topMargin);
                } else {
                    param.topMargin = SupportDisplay
                            .calculateActualControlerSize(topMargin);
                }
            }
            int bottomMargin = param.bottomMargin;
            if (bottomMargin != -1) {
                if (isLandscape) {
                    param.bottomMargin = SupportDisplay
                            .calculateActualControlerSizeY(bottomMargin);
                } else {
                    param.bottomMargin = SupportDisplay
                            .calculateActualControlerSize(bottomMargin);
                }
            }

        }
        int leftPadding = view.getPaddingLeft();
        if (leftPadding != -1) {
            if (isLandscape) {
                leftPadding = SupportDisplay
                        .calculateActualControlerSizeY(leftPadding);
            } else {
                leftPadding = SupportDisplay
                        .calculateActualControlerSize(leftPadding);
            }

        }
        int rightPadding = view.getPaddingRight();
        if (rightPadding != -1) {
            if (isLandscape) {
                rightPadding = SupportDisplay
                        .calculateActualControlerSizeY(rightPadding);
            } else {
                rightPadding = SupportDisplay
                        .calculateActualControlerSize(rightPadding);
            }

        }
        int topPadding = view.getPaddingTop();
        if (topPadding != -1) {
            if (isLandscape) {
                topPadding = SupportDisplay
                        .calculateActualControlerSizeY(topPadding);
            } else {
                topPadding = SupportDisplay
                        .calculateActualControlerSize(topPadding);
            }
        }
        int bottomPadding = view.getPaddingBottom();
        if (bottomPadding != -1) {
            if (isLandscape) {
                bottomPadding = SupportDisplay
                        .calculateActualControlerSizeY(bottomPadding);
            } else {
                bottomPadding = SupportDisplay
                        .calculateActualControlerSize(bottomPadding);
            }
        }
        view.setPadding(leftPadding, topPadding, rightPadding, bottomPadding);
        if (view instanceof EditText || view instanceof TextView) {
            TextView tv = (TextView) view;
            float textSize = tv.getTextSize();
            if (isLandscape) {
                SupportDisplay.resetContrlerTextSizeY(tv, textSize);
            } else {
                SupportDisplay.resetContrlerTextSize(tv, textSize);
            }

        }

    }
}