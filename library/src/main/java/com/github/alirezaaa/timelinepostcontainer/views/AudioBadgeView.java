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

package com.github.alirezaaa.timelinepostcontainer.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.TextView;

import com.github.alirezaaa.timelinepostcontainer.AndroidUtils;
import com.github.alirezaaa.timelinepostcontainer.R;

public class AudioBadgeView extends TextView {
    private final Paint mPaint = new Paint();
    private final RectF mRectBounds = new RectF();
    private boolean mMute;
    private boolean mHasSound;

    public AudioBadgeView(Context context) {
        super(context);
        initPaint();
    }

    public AudioBadgeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public AudioBadgeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public AudioBadgeView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initPaint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setupRectBounds();
        init();

        if (mHasSound) {
            canvas.drawCircle(getWidth() / 2.0f, getHeight() / 2.0f, getWidth() / 2.0f, mPaint);
        } else {
            canvas.drawRoundRect(mRectBounds, (getWidth() - (getPaddingLeft() + getPaddingRight())) / 2.0f, (getWidth() - (getPaddingBottom() + getPaddingTop())) / 2.0f, mPaint);
        }
        super.onDraw(canvas);
    }

    private void init() {
        if (mHasSound) {
            if (mMute) {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_volume_off_black_24dp, 0, 0, 0);
            } else {
                setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_volume_up_black_24dp, 0, 0, 0);
            }
        } else {
            setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_volume_off_black_24dp, 0, 0, 0);
            setCompoundDrawablePadding(12);
            setText(R.string.video_has_no_audio);
        }
    }

    private void setupRectBounds() {
        mRectBounds.left = 0;
        mRectBounds.top = 0;
        mRectBounds.right = getWidth();
        mRectBounds.bottom = getHeight();
    }

    private void initPaint() {
        mPaint.setColor(AndroidUtils.getColor(getResources(), android.R.color.black));
        mPaint.setAntiAlias(true);
        mPaint.setAlpha(170);
        mPaint.setStyle(Paint.Style.FILL);
    }

    public void setMute(boolean mute, boolean hasSound) {
        mMute = mute;
        mHasSound = hasSound;
        requestLayout();
    }
}
