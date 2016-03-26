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
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.github.alirezaaa.timelinepostcontainer.options.Listeners;
import com.github.alirezaaa.timelinepostcontainer.options.Options;
import com.nostra13.universalimageloader.core.assist.FailReason;

public class TimelinePostContainer extends FrameLayout implements IListener, View.OnClickListener, View.OnTouchListener {

    // previous and current video view fields must be static
    private static VideoView mPreviousVideoView;
    private static VideoView mCurrentVideoView;
    private final AudioManager mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
    private GestureDetector mGestureDetector;
    private Options mOptions = new Options(getContext(), this);
    private int lastPlaybackPosition;
    private String mImagePath;
    private String mVideoPath;
    private ImageView mImageView;
    private Listeners mListeners = new Listeners();
    private Type mType;
    @IdRes
    private int mImageId;
    private MediaPlayer mCurrentVideoViewMediaPlayer;

    //TODO agar pausePreviousVideo ro to halate instagram bardaram onvaght momkene do video play beshan!
    //TODO audioBadge auto hide beshe
    //animation baraye namayeshe badge bezar va option ham bezar vasash

    public TimelinePostContainer(Context context) {
        super(context);
        initProperties();
    }

    public TimelinePostContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initProperties();
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

    public TimelinePostContainer setListeners(Listeners listeners) {
        mListeners = listeners;
        return this;
    }

    private void initProperties() {
        mGestureDetector = new GestureDetector(getContext(), new GestureListener());

        if ((mOptions.playStyle == PlayStyle.INSTAGRAM) && mOptions.muted && !AndroidUtils.isMuted(mAudioManager)) {
            AndroidUtils.mute(mAudioManager);
        }
    }

    private void initAttrs(AttributeSet attrs) {
        TypedArray customTypedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TimelinePostContainer);

        mOptions.drawablesAnimation = AnimationUtils.loadAnimation(getContext(), customTypedArray.getResourceId(R.styleable.TimelinePostContainer_tpc_drawablesAnim, R.anim.foreground));
        mOptions.looping = customTypedArray.getBoolean(R.styleable.TimelinePostContainer_tpc_looping, true);
        mOptions.keepScreenOnWhilePlaying = customTypedArray.getBoolean(R.styleable.TimelinePostContainer_tpc_keepOnScreen, true);
        mOptions.debug = customTypedArray.getBoolean(R.styleable.TimelinePostContainer_tpc_debug, false);
        mOptions.setPlayDrawable(customTypedArray.getResourceId(R.styleable.TimelinePostContainer_tpc_playDrawable, R.drawable.ic_play_circle_filled_black_24dp));
        mOptions.setPauseDrawable(customTypedArray.getResourceId(R.styleable.TimelinePostContainer_tpc_pauseDrawable, R.drawable.ic_pause_circle_filled_black_24dp));
        mOptions.setVideoLoadingView(customTypedArray.getResourceId(R.styleable.TimelinePostContainer_tpc_videoLoading, R.layout.video_loading));
        mOptions.setImageLoadingView(customTypedArray.getResourceId(R.styleable.TimelinePostContainer_tpc_imageLoading, R.layout.image_loading));

        customTypedArray.recycle();
    }

    public void build(Type type) {
        mType = type;

        if (mOptions.imageLoader == null) {
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

        if (mListeners.listener != null) {
            mListeners.listener.onImageCreate(view);
        }

        if (mOptions.debug) {
            Log.d(TimelinePostContainer.class.getSimpleName(), mVideoPath);
        }
    }

    private ImageView createImageView() {
        mImageView = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.image_view, this, false);
        mImageId = AndroidUtils.generateViewId();
        mImageView.setId(mImageId);

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

        TextView textView = (TextView) LayoutInflater.from(getContext()).inflate(R.layout.explanatory_view, this, false);
        textView.setText(text);

        return textView;
    }

    private void showImageLoadingView() {
        if (mOptions.imageLoadingView == null) {
            mOptions.imageLoadingView = AndroidUtils.createImageLoading(getContext(), this);
        }

        if (mOptions.imageLoadingView.getParent() == null) {
            addView(mOptions.imageLoadingView);
        }
    }

    private void unablePlayVideo() {
        addView(createExplanatoryView(R.string.unable_play_video));
    }

    private void displayImage() {
        mOptions.imageLoader.displayImage(mImagePath, mImageView, null, this, this);
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
        // ISSUE: https://github.com/danikula/AndroidVideoCache/issues/60
        /*HttpProxyCacheServer proxy = MyApplication.getProxy(getContext());
        this.mVideoPath = proxy.getProxyUrl(mVideoPath);*/
        mVideoPath = videoPath;

        return this;
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();

        removeAllViews();
        addView(createImageView(), 0);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (v instanceof ImageView) {
            return onImageTouch(v, event);
        }

        if ((v instanceof VideoView) && (event.getAction() == MotionEvent.ACTION_UP)) {
            onVideoTouch(v, event);
        }

        return true;
    }

    private void removeImageLoadingView() {
        if (mOptions.imageLoadingView != null) {
            removeView(mOptions.imageLoadingView);
        }
    }

    private void showPlayDrawable() {
        ImageView view = (ImageView) findViewById(R.id.foreground);
        if (view == null) {
            view = (ImageView) LayoutInflater.from(getContext()).inflate(R.layout.foreground, this, false);
            view.setImageDrawable(mOptions.playDrawable);
            addView(view);
        } else {
            view.setImageDrawable(mOptions.playDrawable);
            view.startAnimation(mOptions.drawablesAnimation);
        }
    }

    private void stopPreviousVideo() {
        if (mPreviousVideoView != null) {
            mPreviousVideoView.pause();
            TimelinePostContainer parentLayout = (TimelinePostContainer) mPreviousVideoView.getParent();
            if (parentLayout != null) {
                parentLayout.showPauseDrawable();
            }
        }
    }

    private void showPauseDrawable() {
        ImageView view = (ImageView) findViewById(R.id.foreground);
        if (view != null) {
            view.setImageDrawable(mOptions.pauseDrawable);
            view.startAnimation(mOptions.drawablesAnimation);
        }
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {
        // this is a workaround because API 16 doesn't support setOnInfoListener()
        if ((Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) && isImageViewExists()) {
            removeVideoLoadingView();
            removeImage();
        }

        if (percent == 100) {
            removeVideoLoadingView();
            return;
        }

        int duration = mp.getCurrentPosition();

        if ((duration == lastPlaybackPosition) && mp.isPlaying()) {
            showVideoLoading();
        } else if (mp.isPlaying()) {
            removeVideoLoadingView();
        }
        lastPlaybackPosition = duration;
    }

    private boolean isImageViewExists() {
        return findViewById(mImageId) != null;
    }

    private void removeVideoLoadingView() {
        if (mOptions.videoLoadingView != null) {
            removeView(mOptions.videoLoadingView);
        }
    }

    /**
     * Remove the image with fading effect.
     */
    private void removeImage() {
        final View view = findViewById(mImageId);
        if (view != null) {
            view.animate()
                    .alpha(0.0f)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);

                            removeView(view);

                            if (mListeners.listener != null) {
                                mListeners.listener.onImageRemove(animation);
                            }
                        }
                    });
        }
    }

    private void showVideoLoading() {
        if (mOptions.videoLoadingView == null) {
            mOptions.videoLoadingView = AndroidUtils.createVideoLoading(getContext(), this);
        }

        if (mOptions.videoLoadingView.getParent() == null) {
            addView(mOptions.videoLoadingView);
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        removeImageLoadingView();
        if (!mOptions.looping) {
            showPlayDrawable();
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
                prepareVideo(v);
            }

            if (mListeners.imageClick != null) {
                mListeners.imageClick.onImageClick(v, mType);
            }
        }
    }

    @Override
    public void onProgressUpdate(String s, View view, int i, int i1) {
        int progress = (360 * i) / i1;
        mOptions.imageLoadingView.setProgress(progress);

        if (mListeners.imageLoading != null) {
            mListeners.imageLoading.onProgressUpdate(s, mOptions.imageLoadingView, view, i, i1);
        }
    }

    @Override
    public boolean onImageTouch(View v, MotionEvent event) {
        return (mType == Type.IMAGE) && mGestureDetector.onTouchEvent(event);
    }

    public Options getOptions() {
        return mOptions;
    }

    public TimelinePostContainer setOptions(Options options) {
        mOptions = options;
        return this;
    }

    @Override
    public void prepareVideo(View v) {
        // prevents from preparing the video multiple times by multiple clicking on the image.
        v.setOnClickListener(null);

        if (mOptions.playStyle != PlayStyle.INSTAGRAM) {
            showPlayDrawable();
        }
        showVideoLoading();

        final VideoView videoView = (VideoView) LayoutInflater.from(getContext()).inflate(R.layout.video_view, this, false);
        videoView.setVideoPath(mVideoPath);
        videoView.setKeepScreenOn(mOptions.keepScreenOnWhilePlaying);
        videoView.setOnTouchListener(this);

        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                unablePlayVideo();
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            videoView.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
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
                mp.setLooping((mOptions.playStyle == PlayStyle.INSTAGRAM) || mOptions.looping);

                mPreviousVideoView = mCurrentVideoView;
                mCurrentVideoView = videoView;
                mCurrentVideoViewMediaPlayer = mp;

                stopPreviousVideo();

                mp.start();
            }
        });

        addView(videoView, 0);

        if (mListeners.listener != null) {
            mListeners.listener.onVideoCreate(videoView);
        }
    }

    @Override
    public void onVideoTouch(View v, MotionEvent event) {
        if (mOptions.playStyle == PlayStyle.WITH_DRAWABLES) {
            if (((MediaController.MediaPlayerControl) v).isPlaying()) {
                ((MediaController.MediaPlayerControl) v).pause();
                removeImageLoadingView();
                showPauseDrawable();
            } else {
                mPreviousVideoView = mCurrentVideoView;
                mCurrentVideoView = ((VideoView) v);
                stopPreviousVideo();

                showPlayDrawable();
                mCurrentVideoView.start();
            }
        } else if (mOptions.playStyle == PlayStyle.INSTAGRAM) {
            if (AndroidUtils.videoHasSound(mCurrentVideoViewMediaPlayer)) {
                if (AndroidUtils.isMuted(mAudioManager)) {
                    AndroidUtils.unmute(mAudioManager);
                    mOptions.audioBadgeView.setMute(false, true);
                } else {
                    AndroidUtils.mute(mAudioManager);
                    mOptions.audioBadgeView.setMute(true, true);

                    if (mListeners.listener != null) {
                        mListeners.listener.onSoundMuted((VideoView) v);
                    }
                }
                showAudioBadge();
            } else {
                mOptions.audioBadgeView.setMute(true, false);
                showAudioBadge();

                if (mListeners.listener != null) {
                    mListeners.listener.onSoundMuted((VideoView) v);
                }
            }
        }
    }

    private void showAudioBadge() {
        View view = findViewById(R.id.audioBadge);
        if (view == null) {
            addView(mOptions.audioBadgeView);
        }
    }

    @Override
    public void onLoadingStarted(String s, View view) {
        showImageLoadingView();
    }

    @Override
    public void onLoadingFailed(String s, View view, FailReason failReason) {
        removeImageLoadingView();
        addTryAgainView();
    }

    @Override
    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
        if (mType == Type.VIDEO) {
            mImageView.setOnClickListener(this);
            if (mOptions.playStyle == PlayStyle.INSTAGRAM) {
                //FIXME auto-play it should not be called here for better performance
                mImageView.callOnClick();
            } else {
                showPlayDrawable();
            }
        } else {
            mImageView.setOnClickListener(this);
            mImageView.setOnTouchListener(this);
        }

        removeImageLoadingView();
    }

    @Override
    public void onLoadingCancelled(String s, View view) {
        // empty, intentional
    }

    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (mListeners.tap != null) {
                mListeners.tap.onDoubleTap(e, mType);
            }

            return super.onDoubleTap(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (mListeners.tap != null) {
                mListeners.tap.onSingleTap(e, mType);
            }

            return super.onSingleTapConfirmed(e);
        }
    }
}
