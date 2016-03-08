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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.todddavies.components.progressbar.ProgressWheel;
import com.wang.avi.AVLoadingIndicatorView;

final class AndroidUtils {

    static ProgressWheel createImageLoading(Context context, ViewGroup viewGroup) {
        return (ProgressWheel) LayoutInflater.from(context).inflate(R.layout.image_loading, viewGroup, false);
    }

    static AVLoadingIndicatorView createVideoLoading(Context context, ViewGroup viewGroup) {
        return (AVLoadingIndicatorView) LayoutInflater.from(context).inflate(R.layout.video_loading, viewGroup, false);
    }

    static boolean isInstanceOf(View view, Class instance, Resources resources) {
        if (!instance.isInstance(view)) {
            throw new IllegalArgumentException(String.format(resources.getString(R.string.not_instance_of), instance.getSimpleName()));
        }

        return true;
    }
}
