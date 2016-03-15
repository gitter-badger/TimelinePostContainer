/*
 * Copyright 2016 Alireza Eskandarpour Shoferi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.alirezaaa.timelinepostcontainer.options;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.AnimRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.github.alirezaaa.timelinepostcontainer.AndroidUtils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.todddavies.components.progressbar.ProgressWheel;
import com.wang.avi.AVLoadingIndicatorView;

public class Options {
    private final Context mContext;
    public boolean mLooping = true;
    public boolean mKeepScreenOnWhilePlaying = true;
    public boolean mDebug;
    public Drawable mPlayDrawable;
    public Drawable mPauseDrawable;
    public Animation mDrawablesAnimation;
    public AVLoadingIndicatorView mVideoLoadingView;
    public ImageLoader mImageLoader;
    public ProgressWheel mImageLoadingView;

    public Options(Context context) {
        mContext = context;
    }

    public Options setVideoLoadingView(ViewGroup viewGroup, @LayoutRes int videoLoadingLayout) {
        View view = LayoutInflater.from(mContext).inflate(videoLoadingLayout, viewGroup, false);
        if (AndroidUtils.isInstanceOf(view, AVLoadingIndicatorView.class, mContext.getResources())) {
            mVideoLoadingView = (AVLoadingIndicatorView) view;
        }
        return this;
    }

    public Options setDrawablesAnimation(@AnimRes int res) {
        mDrawablesAnimation = AnimationUtils.loadAnimation(mContext, res);
        return this;
    }

    public Options setPauseDrawable(@DrawableRes int res) {
        mPauseDrawable = AndroidUtils.getDrawable(mContext.getResources(), res);
        return this;
    }

    public Options setPlayDrawable(@DrawableRes int res) {
        mPlayDrawable = AndroidUtils.getDrawable(mContext.getResources(), res);
        return this;
    }

    public Options setImageLoadingView(ViewGroup viewGroup, @LayoutRes int imageLoadingLayout) {
        View view = LayoutInflater.from(mContext).inflate(imageLoadingLayout, viewGroup, false);
        if (AndroidUtils.isInstanceOf(view, ProgressWheel.class, mContext.getResources())) {
            mImageLoadingView = (ProgressWheel) view;
        }

        return this;
    }
}
