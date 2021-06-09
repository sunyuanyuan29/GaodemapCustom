package com.android.orangetravel.ui.widgets.view;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.orangetravel.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;

/**
 * @author Mr Xiong
 * @date 2021/6/9
 */

public class CustomRefreshHead extends LinearLayout implements RefreshHeader {
    private ImageView mImage;
    private AnimationDrawable pullDownAnim;
    private AnimationDrawable refreshingAnim;

    private boolean hasSetPullDownAnim = false;

    public CustomRefreshHead(Context context) {
        this(context, null, 0);
    }

    public CustomRefreshHead(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomRefreshHead(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }


    private void initView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_customrefresh_head, this);
        mImage = (ImageView) view.findViewById(R.id.iv_refresh_header);
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(@NonNull RefreshKernel kernel, int height, int maxDragHeight) {

    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {
        Log.e("orange", "onMoving------" + percent);
        // 下拉的百分比小于100%时，不断调用 setScale 方法改变图片大小
        if (percent < 1) {
            mImage.setScaleX(percent);
            mImage.setScaleY(percent);
            //是否执行过翻跟头动画的标记
            if (hasSetPullDownAnim) {
                hasSetPullDownAnim = false;
            }
        }
        //当下拉的高度达到Header高度100%时，开始加载正在下拉的初始动画，即翻跟头
        if (percent >= 1.0) {
            //因为这个方法是不停调用的，防止重复
            if (!hasSetPullDownAnim) {
                mImage.setImageResource(R.drawable.anim_pull_end);
                pullDownAnim = (AnimationDrawable) mImage.getDrawable();
                pullDownAnim.start();
                hasSetPullDownAnim = true;
            }
        }

    }

    @Override
    public void onReleased(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        Log.e("orange", height + "onReleased" + maxDragHeight);
    }

    @Override
    public void onStartAnimator(@NonNull RefreshLayout refreshLayout, int height, int maxDragHeight) {
        Log.e("orange", height + "onStartAnimator" + maxDragHeight);
    }

    @Override
    public int onFinish(@NonNull RefreshLayout refreshLayout, boolean success) {
        // 结束动画
        if (pullDownAnim != null && pullDownAnim.isRunning()) {
            pullDownAnim.stop();
        }
        if (refreshingAnim != null && refreshingAnim.isRunning()) {
            refreshingAnim.stop();
        }
        //重置状态
        hasSetPullDownAnim = false;
        return 0;

    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {
        Log.e("orange", percentX + "onHorizontalDrag" + offsetX);
    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(@NonNull RefreshLayout refreshLayout, @NonNull RefreshState oldState,
                               @NonNull RefreshState newState) {
        switch (newState) {
            case PullDownToRefresh: //下拉刷新开始。正在下拉还没松手时调用
                //每次重新下拉时，将图片资源重置为小人的大脑袋
                mImage.setImageResource(R.mipmap.commonui_pull_image);
                break;
            case Refreshing: //正在刷新。只调用一次
                //状态切换为正在刷新状态时，设置图片资源为小人卖萌的动画并开始执行
                mImage.setImageResource(R.drawable.anim_pull_refreshing);
                refreshingAnim = (AnimationDrawable) mImage.getDrawable();
                refreshingAnim.start();
                break;
            case ReleaseToRefresh:

                break;
        }
        Log.e("orange", oldState.isFooter + "onStateChanged" + newState.isFooter);
    }
}
