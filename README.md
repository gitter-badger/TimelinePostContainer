# TimelinePostContainer

[![Platform](https://img.shields.io/badge/platform-android-brightgreen.svg)](http://developer.android.com/index.html)
[![Compatibility](https://img.shields.io/badge/compatibility-API%2016%2B-green.svg)](https://android-arsenal.com/api?level=16)
[![Release](https://jitpack.io/v/alirezaaa/TimelinePostContainer.svg)](https://jitpack.io/#alirezaaa/TimelinePostContainer)
[![Codacy](https://api.codacy.com/project/badge/grade/ffa490a98def457e8cac302b33c9d89c)](https://www.codacy.com/app/aesshoferi/TimelinePostContainer)
[![License](https://img.shields.io/badge/license-apache%202-blue.svg)](https://www.apache.org/licenses/LICENSE-2.0)

Timeline is a famous and user-friendly feature these days; If you need to implement one of them in your projects, consider using this library. Tried to have simple methods, currently you just need to write three lines of codes!

## A Quick Overview What's In
* Compatible down to API Level 16
* Try loading images again if there's a problem with internet connection
* Native `VideoView`
* Listeners
* Two kinds of posts supported (only images and videos with a thumbnail)
* Determinate progress view while loading images
* Caching images
* Plays only one video at a time
* Play and/or pause videos with a drawable
* Compatible with using inside `RecyclerView`
* Appropriate animations
* Good performance
* Customizable

## Include to Project
### Provide the Gradle Dependency
#### Step 1
Add the JitPack in your root `build.gradle`:
```gradle
allprojects {
    repositories {
        ...
        maven { url "https://jitpack.io" }
    }
}
```
#### Step 2
Add the dependency:
```gradle
dependencies {
    compile 'com.github.alirezaaa:TimelinePostContainer:x.y.z'
}
```
### Provide the Maven Dependency
#### Step 1
Add the JitPack in your `pom.xml`:
```xml
<repositories>
	<repository>
		<id>jitpack.io</id>
		<url>https://jitpack.io</url>
	</repository>
</repositories>
```
#### Step 2
Add the dependency:
```xml
<dependency>
	<groupId>com.github.alirezaaa</groupId>
	<artifactId>TimelinePostContainer</artifactId>
	<version>x.y.z</version>
</dependency>
```
**Note:** Replace `x.y.z` with the latest version which can be found at [releases section](../../releases).
### Clone or Download `.zip` file
Clone this repository or download the compressed file, then extract to your computer. Simply import the `library` module to your project.

## Usage
You just have to provide the `ImageLoader` and your desired image and/or video paths as I did below or simply compile and try the sample `app` provided:
```java
public class SampleActivity extends AppCompatActivity {
    @Bind(R.id.timelinePostContainer)
    TimelinePostContainer timelinePostContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sample_one);
		ButterKnife.bind(this);

        timelinePostContainer.setImagePath("image path");
        timelinePostContainer.setVideoPath("video path if you need the video implementation");
        timelinePostContainer.build(Type.VIDEO); // or Type.IMAGE if you need the image implementation
    }
}
```

### Useful Tips
- To avoid scrolling lags you can use `PauseOnScrollListener` or `RecyclerPauseOnScrollListener`:
```java
recyclerView.addOnScrollListener(new RecyclerPauseOnScrollListener(InitClass.imageLoader(this), false, true));
```

## Customization
* You can use good configured default `ImageLoader` or pass your customized one:
```java
timelinePostContainer.setImageLoader(ImageLoader.getInstance());
```

* If you prefer customized image and video loadings, you must write following lines before building the view:
```java
timelinePostContainer.setImageLoadingView(R.layout.customized_image_loading); // or setImageLoadingView(ProgressWheel)
timelinePostContainer.setVideoLoadingView(R.layout.customized_video_loading); // or setVideoLoadingView(AVLoadingIndicatorView)
timelinePostContainer.build(Type.VIDEO); // or Type.IMAGE if you need the image implementation
```

## Listeners
- `IListener` includes (`onImageRemove(Animator)`, `onImageCreate(ImageView)`, `onVideoCreate(VideoView)`)
- `IDoubleTapListener` includes (`onImageDoubleTap(MotionEvent)`)
- `IImageLoadingListener` includes (`onProgressUpdate(String, View, int, int)`)
- `IImageClickListener` includes (`onImageClick(MotionEvent)`)

## Libraries Used
- [Universal Image Loader](https://github.com/nostra13/Android-Universal-Image-Loader) - loading and caching images
- [ProgressWheel](https://github.com/Todd-Davies/ProgressWheel) - customized and similar progress view in API 16+
- [AVLoadingIndicatorView](https://github.com/81813780/AVLoadingIndicatorView) - animated video loading view

## Apps Using the TimelinePostContainer
*feel free to send me new projects.*
- [Collect-Up](http://collect-up.com)

## Contributors
- [Alireza Eskandarpour Shoferi](https://twitter.com/enormoustheory) (developer)
- Farzad Nadiri (thanks to)

## License
    Copyright 2016 Alireza Eskandarpour Shoferi
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
		http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
