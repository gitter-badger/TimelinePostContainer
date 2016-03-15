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

package com.github.alirezaaa.timelinepostcontainer;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.todddavies.components.progressbar.ProgressWheel;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.concurrent.atomic.AtomicInteger;

public final class AndroidUtils {

    private static final AtomicInteger sNextGeneratedId = new AtomicInteger(1);

    private static ProgressWheel createImageLoading(Context context, ViewGroup viewGroup) {
        return (ProgressWheel) LayoutInflater.from(context).inflate(R.layout.image_loading, viewGroup, false);
    }

    private static AVLoadingIndicatorView createVideoLoading(Context context, ViewGroup viewGroup) {
        return (AVLoadingIndicatorView) LayoutInflater.from(context).inflate(R.layout.video_loading, viewGroup, false);
    }

    public static boolean isInstanceOf(View view, Class instance, Resources resources) {
        if (!instance.isInstance(view)) {
            throw new IllegalArgumentException(String.format(resources.getString(R.string.not_instance_of), instance.getSimpleName()));
        }

        return true;
    }

    public static Drawable getDrawable(Resources resources, @DrawableRes int res) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return resources.getDrawable(res, null);
        }
        return resources.getDrawable(res);
    }

    /**
     * Generate a value suitable for use in {@link View#setId(int)}.
     * This value will not collide with ID values generated at build time by aapt for R.id.
     *
     * @return a generated ID value
     */
    private static int generateViewId() {
        while (true) {
            int result = sNextGeneratedId.get();
            // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
            int newValue = result + 1;
            if (newValue > 0x00FFFFFF) {
                newValue = 1; // Roll over to 1, not 0.
            }
            if (sNextGeneratedId.compareAndSet(result, newValue)) {
                return result;
            }
        }
    }
}
