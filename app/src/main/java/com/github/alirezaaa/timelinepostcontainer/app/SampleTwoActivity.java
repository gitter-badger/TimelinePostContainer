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

import com.github.alirezaaa.timelinepostcontainer.InitClass;
import com.github.alirezaaa.timelinepostcontainer.utils.RecyclerPauseOnScrollListener;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SampleTwoActivity extends AppCompatActivity {
    @Bind(R.id.recyclerView)
    public RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_two);
        ButterKnife.bind(this);

        FastItemAdapter<SampleItem> adapter = new FastItemAdapter<>();

        SampleItem item = new SampleItem();
        for (int i = 0; i < 10; i++) {
            adapter.add(item.setThumbnail("https://i.imgur.com/7OGKVPn.jpg").setVideoPath("http://collectup.blob.core.windows.net/videos/ce39daa3-cada-4e21-9342-1aa062d39324.mp4"));
        }

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnScrollListener(new RecyclerPauseOnScrollListener(InitClass.imageLoader(this), false, true));
        recyclerView.setAdapter(adapter);
    }
}
