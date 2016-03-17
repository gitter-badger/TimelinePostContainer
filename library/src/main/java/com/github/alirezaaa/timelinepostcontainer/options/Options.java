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
    public boolean looping = true;
    public boolean keepScreenOnWhilePlaying = true;
    public boolean debug;
    public Drawable playDrawable;
    public Drawable pauseDrawable;
    public Animation drawablesAnimation;
    public AVLoadingIndicatorView videoLoadingView;
    public ImageLoader imageLoader;
    public ProgressWheel imageLoadingView;

    public Options(Context context) {
        mContext = context;
    }

    public Options setVideoLoadingView(ViewGroup viewGroup, @LayoutRes int videoLoadingLayout) {
        View view = LayoutInflater.from(mContext).inflate(videoLoadingLayout, viewGroup, false);
        if (AndroidUtils.isInstanceOf(view, AVLoadingIndicatorView.class, mContext.getResources())) {
            videoLoadingView = (AVLoadingIndicatorView) view;
        }
        return this;
    }

    public Options setDrawablesAnimation(@AnimRes int res) {
        drawablesAnimation = AnimationUtils.loadAnimation(mContext, res);
        return this;
    }

    public Options setPauseDrawable(@DrawableRes int res) {
        pauseDrawable = AndroidUtils.getDrawable(mContext.getResources(), res);
        return this;
    }

    public Options setPlayDrawable(@DrawableRes int res) {
        playDrawable = AndroidUtils.getDrawable(mContext.getResources(), res);
        return this;
    }

    public Options setImageLoadingView(ViewGroup viewGroup, @LayoutRes int layout) {
        View view = LayoutInflater.from(mContext).inflate(layout, viewGroup, false);
        if (AndroidUtils.isInstanceOf(view, ProgressWheel.class, mContext.getResources())) {
            imageLoadingView = (ProgressWheel) view;
        }

        return this;
    }
}
