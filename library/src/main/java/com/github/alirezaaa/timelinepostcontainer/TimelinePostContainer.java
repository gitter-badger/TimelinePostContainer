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

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.android.volley.toolbox.ImageLoader;
import com.github.alirezaaa.timelinepostcontainer.interfaces.ICallback;
import com.github.alirezaaa.timelinepostcontainer.interfaces.IDoubleTapListener;
import com.github.alirezaaa.timelinepostcontainer.interfaces.IImageTypeClickListener;

public class TimelinePostContainer extends FrameLayout implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private static VideoView mCurrentVideoView;
    private static VideoView mPreviousVideoView;
    int lastPlaybackPosition;
    boolean isFirstTime;
    @IdRes
    int imageViewId;
    private String mImagePath;
    private String mVideoPath;
    private Type mType;
    private ProgressBar mProgressBar;
    private Drawable mForeground;
    private boolean mLooping;
    private IImageTypeClickListener mImageTypeClickListener;
    private GestureDetector gestureDet;
    private IDoubleTapListener mDoubleTapListener;
    private boolean videoIsPrepared;
    private ImageLoader mImageLoader;
    private ICallback mCallback;
    private ImageVolleyView mImageView;

    public TimelinePostContainer(Context context) {
        super(context);
        initConstructors();
    }

    private void initConstructors() {
        gestureDet = new GestureDetector(getContext(), new GestureListener());
        setForegroundGravity(Gravity.CENTER);
    }

    public TimelinePostContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initConstructors();
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray customTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimelinePostContainer);

        mForeground = customTypedArray.getDrawable(R.styleable.TimelinePostContainer_tpc_foreground);
        mLooping = customTypedArray.getBoolean(R.styleable.TimelinePostContainer_tpc_looping, false);

        customTypedArray.recycle();
    }

    public TimelinePostContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(attrs);
        initConstructors();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimelinePostContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initConstructors();
    }

    public TimelinePostContainer setDoubleTapListener(IDoubleTapListener listener) {
        mDoubleTapListener = listener;
        return this;
    }

    public TimelinePostContainer setImageTypeClickListener(IImageTypeClickListener listener) {
        mImageTypeClickListener = listener;
        return this;
    }

    public boolean isLooping() {
        return mLooping;
    }

    public TimelinePostContainer setLooping(boolean looping) {
        mLooping = looping;
        return this;
    }

    public void build(Type type) {
        mType = type;

        if (mImageLoader == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.image_loader_not_null));
        }

        if (mType == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.type_must_defined));
        }

        videoIsPrepared = false;
        mProgressBar = createProgressBar();

        removeAllViews();

        ImageVolleyView view;
        if (mType == Type.IMAGE) {
            view = createImageView();
            addView(createImageView(), 0);
            setOnClickListener(null);
        } else {
            view = createImageView();
            addView(view, 0);
        }

        if (mCallback != null) {
            mCallback.onImageCreate(view);
        }
    }

    private ProgressBar createProgressBar() {
        ProgressBar progressBar = new ProgressBar(getContext());
        progressBar.setIndeterminate(true);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.TOP | Gravity.RIGHT;
        layoutParams.topMargin = AndroidUtils.dpToPx(10);
        layoutParams.rightMargin = AndroidUtils.dpToPx(10);
        layoutParams.width = AndroidUtils.dpToPx(42);
        layoutParams.height = AndroidUtils.dpToPx(42);
        progressBar.setLayoutParams(layoutParams);

        return progressBar;
    }

    private void addTryAgainView() {
        final TextView view = createExplanatoryView(R.string.try_again);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProgressBar();
                mImageView.tryAgain();
                removeView(view);
            }
        });
        addView(view);
    }

    private TextView createExplanatoryView(@StringRes int text) {
        removeProgressBar();

        TextView textView = new TextView(getContext());
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        textView.setLayoutParams(params);
        textView.setTextColor(getContext().getResources().getColor(android.R.color.white));
        textView.setText(text);
        textView.setTextSize(20.0f);

        return textView;
    }

    private void addErrorView() {
        addView(createExplanatoryView(R.string.unable_to_play));
    }

    private ImageVolleyView createImageView() {
        mImageView = new ImageVolleyView(getContext());
        imageViewId = AndroidUtils.generateViewId();
        mImageView.setId(imageViewId);
        addProgressBar();

        mImageView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if ((mType == Type.VIDEO) && !isFirstTime) {
                    if (mImageView.getDrawable() != null) {
                        isFirstTime = true;
                        setForeground(mForeground);
                    } else {
                        removeForeground();
                        addProgressBar();
                    }
                }
            }
        });

        mImageView.setResponseObserver(new ImageVolleyView.ResponseObserver() {
            @Override
            public void onError() {
                addTryAgainView();
            }

            @Override
            public void onSuccess() {
                if (mType == Type.VIDEO) {
                    mImageView.setClickable(true);
                    mImageView.setOnClickListener(TimelinePostContainer.this);
                } else {
                    // We should set clickable to false and pass null to the click listener,
                    // because the listener exists withing RecyclerView.
                    mImageView.setClickable(false);
                    mImageView.setOnClickListener(TimelinePostContainer.this);
                    mImageView.setOnTouchListener(TimelinePostContainer.this);
                }
                removeProgressBar();
            }
        });

        mImageView.setImageUrl(mImagePath, mImageLoader);

        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mImageView.setLayoutParams(layoutParams);
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        mImageView.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                setForeground((mType == Type.IMAGE) ? null : mForeground);
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
                //nothing
            }
        });

        return mImageView;
    }

    public Type getType() {
        return mType;
    }

    @SuppressWarnings("SuspiciousNameCombination")
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);

        setMeasuredDimension(View.MeasureSpec.getSize(widthMeasureSpec), View.MeasureSpec.getSize(widthMeasureSpec));
    }

    public String getImagePath() {
        return mImagePath;
    }

    public TimelinePostContainer setImagePath(String imagePath) {
        mImagePath = imagePath;
        return this;
    }

    public String getVideoPath() {
        return mVideoPath;
    }

    public TimelinePostContainer setVideoPath(String videoPath) {
        //FIXME https://github.com/danikula/AndroidVideoCache/issues/60
        /*HttpProxyCacheServer proxy = MyApplication.getProxy(getContext());
        this.mVideoPath = proxy.getProxyUrl(mVideoPath);*/
        mVideoPath = videoPath;

        if (BuildConfig.DEBUG) {
            Log.d("Video path", videoPath);
        }

        return this;
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    public TimelinePostContainer setProgressBar(ProgressBar progressBar) {
        mProgressBar = progressBar;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        removeAllViews();
        videoIsPrepared = false;
        addView(createImageView(), 0);
    }

    public TimelinePostContainer setCallback(ICallback callback) {
        mCallback = callback;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ImageVolleyView) {
            return (mType == Type.IMAGE) && gestureDet.onTouchEvent(event);
        }

        if ((v instanceof VideoView) && (event.getAction() == MotionEvent.ACTION_UP)) {
            if (((VideoView) v).isPlaying()) {
                ((VideoView) v).pause();
                removeProgressBar();
                setPlayForeground();
            } else {
                mPreviousVideoView = mCurrentVideoView;
                mCurrentVideoView = ((VideoView) v);
                stopPreviousVideo();

                removeForeground();
                mCurrentVideoView.start();
            }
        }

        return true;
    }

    private void removeProgressBar() {
        if (mProgressBar != null) {
            removeView(mProgressBar);
        }
    }

    /**
     * Sets the set foreground
     */
    private void setPlayForeground() {
        setForeground(mForeground);
    }

    private void stopPreviousVideo() {
        if (mPreviousVideoView != null) {
            mPreviousVideoView.pause();
            FrameLayout parentLayout = (FrameLayout) mPreviousVideoView.getParent();
            if (parentLayout != null) {
                parentLayout.setForeground(mForeground);
            }
        }
    }

    /**
     * Removes the foreground
     */
    private void removeForeground() {
        setForeground(null);
    }

    @Override
    public void setForeground(Drawable foreground) {
        super.setForeground(foreground);
    }

    public TimelinePostContainer setImageLoader(ImageLoader imageLoader) {
        if (imageLoader == null) {
            throw new NullPointerException(getContext().getString(R.string.image_loader_not_null));
        }

        mImageLoader = imageLoader;
        return this;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) && isRemovingImageNeeded()) {
            removeForeground();
            removeProgressBar();
            removeImage();
        }

        if (percent == 100) {
            removeProgressBar();
            return;
        }

        int duration = mp.getCurrentPosition();

        if ((duration == lastPlaybackPosition) && mp.isPlaying()) {
            addProgressBar();
        } else if (mp.isPlaying()) {
            removeProgressBar();
        }
        lastPlaybackPosition = duration;
    }

    private boolean isRemovingImageNeeded() {
        return findViewById(imageViewId) != null;
    }

    /**
     * Removes the image
     */
    private void removeImage() {
        int childCounts = getChildCount();
        for (int child = 0; child < childCounts; child++) {
            final View view = getChildAt(child);
            if (view instanceof ImageVolleyView) {
                // Animate removing image.
                view.animate()
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                removeView(view);

                                if (mCallback != null) {
                                    mCallback.onImageRemove(animation);
                                }
                            }
                        });
                break;
            }
        }
    }

    private void addProgressBar() {
        if (mProgressBar.getParent() == null) {
            removeForeground();
            addView(mProgressBar);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        removeProgressBar();
        if (!mLooping) {
            setPlayForeground();
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mDoubleTapListener != null) {
                mDoubleTapListener.doubleTapOnImageType(e, getTag());
            }

            return true;
        }
    }

    @Override
    public void onClick(View v) {
        if ((v instanceof ImageVolleyView) && (mType == Type.VIDEO)) {
            if (!videoIsPrepared) {
                videoIsPrepared = true;
                addProgressBar();
                final VideoView videoView = new VideoView(getContext());
                FrameLayout.LayoutParams videoParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                videoView.setLayoutParams(videoParams);
                videoView.setVideoPath(mVideoPath);
                videoView.setKeepScreenOn(true);
                videoView.setOnTouchListener(this);

                videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        addErrorView();
                        return true;
                    }
                });

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                        @Override
                        public boolean onInfo(MediaPlayer mp, int what, int extra) {
                            if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                                removeForeground();
                                removeProgressBar();
                                removeImage();
                            }
                            return false;
                        }
                    });
                }

                videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setOnBufferingUpdateListener(TimelinePostContainer.this);
                        mp.setOnCompletionListener(TimelinePostContainer.this);
                        mp.setLooping(mLooping);

                        mPreviousVideoView = mCurrentVideoView;
                        mCurrentVideoView = videoView;

                        stopPreviousVideo();

                        mp.start();
                    }
                });

                addView(videoView, 0);

                if (mCallback != null) {
                    mCallback.onVideoCreate(videoView);
                }
            }

        } else if ((v instanceof ImageVolleyView) && (mType == Type.IMAGE) && (mImageTypeClickListener != null)) {
            mImageTypeClickListener.onImageTypeClickListener(v, getTag());
        }
    }


}
