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

package io.github.meness.timelinepostcontainer.app;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.mikepenz.fastadapter.items.AbstractItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.github.meness.timelinepostcontainer.TimelinePostContainer;
import io.github.meness.timelinepostcontainer.Type;

public class SampleItem extends AbstractItem<SampleItem, SampleItem.ViewHolder> {
    public String thumbnail;
    public String videoPath;

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
        return R.layout.item;
    }

    @Override
    public void bindView(ViewHolder holder) {
        super.bindView(holder);

        holder.timelinePostContainer.setImagePath(thumbnail);
        holder.timelinePostContainer.setVideoPath(videoPath);
        holder.timelinePostContainer.build(Type.VIDEO);
    }

    protected static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.timelinePostContainer)
        public TimelinePostContainer timelinePostContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
