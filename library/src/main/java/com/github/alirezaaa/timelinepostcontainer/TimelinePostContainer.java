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
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.alirezaaa.timelinepostcontainer.interfaces.IImageClickListener;
import com.github.alirezaaa.timelinepostcontainer.interfaces.IImageLoadingListener;
import com.github.alirezaaa.timelinepostcontainer.interfaces.IListener;
import com.github.alirezaaa.timelinepostcontainer.interfaces.ITapListener;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;
import com.todddavies.components.progressbar.ProgressWheel;
import com.wang.avi.AVLoadingIndicatorView;

public class TimelinePostContainer extends FrameLayout implements View.OnClickListener, View.OnTouchListener, MediaPlayer.OnBufferingUpdateListener, MediaPlayer.OnCompletionListener {

    private static final String TAG = TimelinePostContainer.class.getSimpleName();
    private static VideoView mCurrentVideoView;
    private static VideoView mPreviousVideoView;
    private static ProgressWheel mImageLoadingView;
    private static IImageLoadingListener mImageLoadingListener;
    private int lastPlaybackPosition;
    private String mImagePath;
    private String mVideoPath;
    private Type mType;
    private Drawable mForeground;
    private boolean mLooping;
    private IImageClickListener mImageClickListener;
    private GestureDetector mGestureDetector;
    private ITapListener mTapListener;
    private ImageLoader mImageLoader;
    private IListener mListener;
    private ImageView mImageView;
    private AVLoadingIndicatorView mVideoLoadingView;
    private boolean mKeepScreenOnWhilePlaying = true;

    public TimelinePostContainer(Context context) {
        super(context);
        initProperties();
    }

    private void initProperties() {
        mImageLoader = InitClass.imageLoader(getContext());
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());

        setForegroundGravity(Gravity.CENTER);
    }

    public TimelinePostContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initProperties();
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
        initProperties();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TimelinePostContainer(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initAttrs(attrs);
        initProperties();
    }

    public boolean isKeepScreenOnWhilePlaying() {
        return mKeepScreenOnWhilePlaying;
    }

    public TimelinePostContainer setKeepScreenOnWhilePlaying(boolean keepScreenOnWhilePlaying) {
        mKeepScreenOnWhilePlaying = keepScreenOnWhilePlaying;
        return this;
    }

    public TimelinePostContainer setVideoLoadingView(@LayoutRes int videoLoadingLayout) {
        View view = LayoutInflater.from(getContext()).inflate(videoLoadingLayout, this, false);
        if (AndroidUtils.isInstanceOf(view, AVLoadingIndicatorView.class, getResources())) {
            mVideoLoadingView = (AVLoadingIndicatorView) view;
        }

        return this;
    }

    public TimelinePostContainer setVideoLoadingView(AVLoadingIndicatorView videoLoadingLayout) {
        mVideoLoadingView = videoLoadingLayout;
        return this;
    }

    public TimelinePostContainer setTapListener(ITapListener listener) {
        mTapListener = listener;
        return this;
    }

    public TimelinePostContainer setImageClickListener(IImageClickListener listener) {
        mImageClickListener = listener;
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

        removeAllViews();

        ImageView view;
        if (mType == Type.IMAGE) {
            view = createImageView();
            addView(createImageView(), 0);
            setOnClickListener(null);
        } else {
            view = createImageView();
            addView(view, 0);
        }

        if (mListener != null) {
            mListener.onImageCreate(view);
        }
    }

    private ImageView createImageView() {
        mImageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.image_view, this, false);

        displayImage();

        return mImageView;
    }

    private void addTryAgainView() {
        final TextView view = createExplanatoryView(R.string.unable_load_image);
        view.setClickable(true);
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeView(view);
                displayImage();
            }
        });
        addView(view);
    }

    private TextView createExplanatoryView(@StringRes int text) {
        removeImageLoadingView();

        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.unable_load_image, this, false);
        textView.setText(text);

        return textView;
    }

    private void addImageLoadingView() {
        if (mImageLoadingView == null) {
            mImageLoadingView = AndroidUtils.createImageLoading(getContext(), this);
        }

        if (mImageLoadingView.getParent() == null) {
            removeForeground();
            addView(mImageLoadingView);
        }
    }

    private void addErrorView() {
        addView(createExplanatoryView(R.string.unable_load_image));
    }

    private void displayImage() {
        mImageLoader.displayImage(mImagePath, mImageView, null, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {
                addImageLoadingView();
            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                removeImageLoadingView();
                addTryAgainView();
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                if (mType == Type.VIDEO) {
                    mImageView.setClickable(true);
                    mImageView.setOnClickListener(TimelinePostContainer.this);

                    setPlayForeground();
                } else {
                    // we should set clickable to false and pass null to the click listener,
                    // because the listener exists withing RecyclerView.
                    mImageView.setClickable(false);
                    mImageView.setOnClickListener(TimelinePostContainer.this);
                    mImageView.setOnTouchListener(TimelinePostContainer.this);

                    removeForeground();
                }
                removeImageLoadingView();
            }

            @Override
            public void onLoadingCancelled(String s, View view) {
                // empty, intentional
            }
        }, new TimelinePostContainer.MyImageLoadingProgressListener());
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
        // FIXME https://github.com/danikula/AndroidVideoCache/issues/60
        /*HttpProxyCacheServer proxy = MyApplication.getProxy(getContext());
        this.mVideoPath = proxy.getProxyUrl(mVideoPath);*/
        mVideoPath = videoPath;

        if (BuildConfig.DEBUG) {
            Log.d(TAG, videoPath);
        }

        return this;
    }

    public ProgressWheel getImageLoadingView() {
        return mImageLoadingView;
    }

    public TimelinePostContainer setImageLoadingView(@LayoutRes int imageLoadingLayout) {
        View view = LayoutInflater.from(getContext()).inflate(imageLoadingLayout, this, false);
        if (AndroidUtils.isInstanceOf(view, ProgressWheel.class, getResources())) {
            mImageLoadingView = (ProgressWheel) view;
        }

        return this;
    }

    public TimelinePostContainer setImageLoadingView(ProgressWheel imageLoadingLayout) {
        mImageLoadingView = imageLoadingLayout;
        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        removeAllViews();
        addView(createImageView(), 0);
    }

    public TimelinePostContainer setListener(IListener listener) {
        mListener = listener;
        return this;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ImageView) {
            return (mType == Type.IMAGE) && mGestureDetector.onTouchEvent(event);
        }

        if ((v instanceof VideoView) && (event.getAction() == MotionEvent.ACTION_UP)) {
            if (((VideoView) v).isPlaying()) {
                ((VideoView) v).pause();
                removeImageLoadingView();
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

    private void removeImageLoadingView() {
        if (mImageLoadingView != null) {
            removeView(mImageLoadingView);
        }
    }

    /**
     * Sets the foreground
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
            throw new IllegalArgumentException(getContext().getString(R.string.image_loader_not_null));
        }

        mImageLoader = imageLoader;
        return this;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // this is a workaround because API 16 doesn't support setOnInfoListener()
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) && isImageViewExists()) {
            removeForeground();
            removeVideoLoadingView();
            removeImage();
        }

        if (percent == 100) {
            removeVideoLoadingView();
            return;
        }

        int duration = mp.getCurrentPosition();

        if ((duration == lastPlaybackPosition) && mp.isPlaying()) {
            addVideoLoading();
        } else if (mp.isPlaying()) {
            removeVideoLoadingView();
        }
        lastPlaybackPosition = duration;
    }

    private boolean isImageViewExists() {
        int childCounts = getChildCount();
        for (int child = 0; child < childCounts; child++) {
            View view = getChildAt(child);
            if (view instanceof ImageView) {
                return true;
            }
        }

        return false;
    }

    private void removeVideoLoadingView() {
        if (mVideoLoadingView != null) {
            removeView(mVideoLoadingView);
        }
    }

    /**
     * Removes the image.
     */
    private void removeImage() {
        int childCounts = getChildCount();
        for (int child = 0; child < childCounts; child++) {
            final View view = getChildAt(child);
            if (view instanceof ImageView) {
                // animate removing image.
                view.animate()
                        .alpha(0.0f)
                        .setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);

                                removeView(view);

                                if (mListener != null) {
                                    mListener.onImageRemove(animation);
                                }
                            }
                        });
                break;
            }
        }
    }

    private void addVideoLoading() {
        if (mVideoLoadingView == null) {
            mVideoLoadingView = AndroidUtils.createVideoLoading(getContext(), this);
        }

        if (mVideoLoadingView.getParent() == null) {
            removeForeground();
            addView(mVideoLoadingView);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        removeImageLoadingView();
        if (!mLooping) {
            setPlayForeground();
        }
    }

    public TimelinePostContainer setImageLoadingListener(IImageLoadingListener listener) {
        mImageLoadingListener = listener;
        return this;
    }

    private static class MyImageLoadingProgressListener implements ImageLoadingProgressListener {
        @Override
        public void onProgressUpdate(String s, View view, int i, int i1) {
            int progress = (360 * i) / i1;
            mImageLoadingView.setProgress(progress);

            if (mImageLoadingListener != null) {
                mImageLoadingListener.onProgressUpdate(s, view, i, i1);
            }
        }
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mTapListener != null) {
                mTapListener.onDoubleTap(e, mType);
            }

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mTapListener != null) {
                mTapListener.onSingleTap(e, mType);
            }

            return super.onSingleTapConfirmed(e);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof ImageView) {
            // clicking on try again plays the video, this workaround prevents that.
            if (((ImageView) v).getDrawable() == null) {
                return;
            }

            if (mType == Type.VIDEO) {
                // prevents from preparing the video multiple times by multiple clicking on the image.
                v.setOnClickListener(null);

                addVideoLoading();

                final VideoView videoView = (VideoView) LayoutInflater.from(getContext()).inflate(R.layout.video_view, this, false);
                videoView.setVideoPath(mVideoPath);
                videoView.setKeepScreenOn(mKeepScreenOnWhilePlaying);
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
                                removeImageLoadingView();
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

                if (mListener != null) {
                    mListener.onVideoCreate(videoView);
                }
            } else if ((mType == Type.IMAGE) && (mImageClickListener != null)) {
                mImageClickListener.onImageClick(v);
            }
        }
    }
}
