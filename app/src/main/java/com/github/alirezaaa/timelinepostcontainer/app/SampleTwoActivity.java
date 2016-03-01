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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.mikepenz.fastadapter.adapters.FastItemAdapter;

public class SampleTwoActivity extends AppCompatActivity {

    RecyclerView mRecyclerView;
    String[] mLinks = {
            "http://collectup.blob.core.windows.net/videos/ce39daa3-cada-4e21-9342-1aa062d39324.mp4",
            "http://collectup.blob.core.windows.net/videos/fc16f22a-e458-4056-8218-1d17ddbadb37.mp4",
            "http://collectup.blob.core.windows.net/videos/1ac93067-d67c-47ed-aa39-cce4491b4bf9.mp4",
            "http://collectup.blob.core.windows.net/videos/eac3e2e3-5eea-407b-9179-e1d73db5d4c3.mp4",
            "http://collectup.blob.core.windows.net/videos/4c72fc6e-bd9e-4484-b05b-4f8935cd71dc.mp4",
            "http://collectup.blob.core.windows.net/videos/82b93c7d-f9e7-4e3b-b091-ccc79a277e5c.mp4",
            "http://collectup.blob.core.windows.net/videos/ac532f3e-2939-4038-a5e8-56def4fc675b.mp4",
            "http://collectup.blob.core.windows.net/videos/eae78c1a-d5b5-4163-b5b5-14563f816752.mp4",
            "http://collectup.blob.core.windows.net/videos/d67102c7-0091-4630-87e1-63e1f4be8f1b.mp4",
            "http://collectup.blob.core.windows.net/videos/ef389d3e-bda5-4ad9-b454-012f5ab7bde6.mp4",
            "http://collectup.blob.core.windows.net/videos/e55d634d-72ba-45f9-ac19-c00005baf094.mp4",
            "http://collectup.blob.core.windows.net/videos/46b9b717-6d9b-4940-8831-51bab4f9be6a.mp4",
            "http://collectup.blob.core.windows.net/videos/088c1fda-e64e-48eb-92e6-32c9c73bf331.mp4",
            "http://collectup.blob.core.windows.net/videos/49689fa9-6010-4d49-aa8f-751313228868.mp4"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.sample_two);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        FastItemAdapter<SampleItem> adapter = new FastItemAdapter<>();

        for (int i = 0; i < mLinks.length; i++) {
            adapter.add(new SampleItem(this).setThumbnail("https://i.imgur.com/7OGKVPn.jpg").setVideoPath(mLinks[i]));
        }

        mRecyclerView.setAdapter(adapter);
    }
}
