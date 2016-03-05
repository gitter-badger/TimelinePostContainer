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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.alirezaaa.timelinepostcontainer.TimelinePostContainer;
import com.github.alirezaaa.timelinepostcontainer.Type;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SampleOneActivity extends AppCompatActivity {

    @Bind(R.id.timelinePostContainer)
    public TimelinePostContainer timelinePostContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_one);
        ButterKnife.bind(this);

        timelinePostContainer.setImagePath("http://collectup.blob.core.windows.net/images/420e969d-3fef-48cf-a968-f79945a5ed85.jpg");
        timelinePostContainer.setVideoPath("http://collectup.blob.core.windows.net/videos/ff59ab2e-d14e-4f0e-b585-e36bd72649bd.mp4");
        timelinePostContainer.build(Type.VIDEO);
    }
}
