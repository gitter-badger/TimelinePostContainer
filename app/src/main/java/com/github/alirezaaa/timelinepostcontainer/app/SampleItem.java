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

package com.github.alirezaaa.timelinepostcontainer.app;

import android.animation.Animator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;

import com.github.alirezaaa.timelinepostcontainer.TimelinePostContainer;
import com.github.alirezaaa.timelinepostcontainer.Type;
import com.github.alirezaaa.timelinepostcontainer.interfaces.ICallback;
import com.mikepenz.fastadapter.items.AbstractItem;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SampleItem extends AbstractItem<SampleItem, SampleItem.ViewHolder> {
    public String thumbnail;
    public String videoPath;
    public Context mContext;

    public SampleItem(Context context) {
        mContext = context;
    }

    public SampleItem setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;

        return this;
    }

    public SampleItem setVideoPath(String videoPath) {
        this.videoPath = videoPath;

        return this;
    }

    @Override
    public int getType() {
        return 0;
    }

    @Override
    public int getLayoutRes() {
        return R.layout.sample_one;
    }

    @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);

        holder.timelinePostContainer.setImagePath(thumbnail);
        holder.timelinePostContainer.setVideoPath(videoPath);
        holder.timelinePostContainer.build(Type.VIDEO);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.timelinePostContainer)
        TimelinePostContainer timelinePostContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            timelinePostContainer.setImageLoader(MyApplication.getInstance().getImageLoader());
        }
    }
}
