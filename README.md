#![Logo](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/res/drawable-mdpi/ic_launcher.png) Android-SlidingTabStrip

这是Android上的一个ViewPager滑动导航条组件，主要就是用于配合ViewPager实现滑动的导航条，以容纳无限多的Tab，最低兼容Android2.2

![smaple](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/docs/sample.png)

##Features
>* 当所有的Tab在横向上无法充满整个屏幕的时候，将会平均分配每个Tab的宽度，如果某个Tab的最小宽度大于平均宽度，那么其宽度保持不变
>* 当所有的Tab在横向上能够充满整个屏幕的时候，将会从左到右依次排列
>* 用法简单，支持在布局中直接包含View或者在代码中直接调用SlidingTabStrip.addTab()方法添加

##Usage Guide
### 1.在布局中添加SlidingTabStrip
```java
<me.xiaopan.android.slidingtabstrip.SlidingTabStrip 
    android:id="@+id/slidingTabStrip_more" 
    android:layout_width="match_parent" 
    android:layout_height="wrap_content" 
    android:background="@drawable/image_backg" >
    <LinearLayout android:layout_width="match_parent" android:layout_height="wrap_content">
		<TextView style="@style/text_slidingTabTitle" android:text="游戏娱乐"/>            
		<TextView style="@style/text_slidingTabTitle" android:text="工具"/>            
    </LinearLayout>
</me.xiaopan.android.slidingtabstrip.SlidingTabStrip>
```

### 2.在代码中设置滑块或继续添加Tab
```java
SlidingTabStrip moreSlidingTabStrip = (SlidingTabStrip) findViewById(R.id.slidingTabStrip_more);
moreSlidingTabStrip.setSlidingBlockDrawable(getResources().getDrawable(R.drawable.image_sliding_block));

//你还可以在代码中通过addTab()方法继续添加Tab
```

### 3.在代码中设置ViewPager
```java
ViewPager moreViewPager = (ViewPager) findViewById(R.id.viewPager_more);
moreSlidingTabStrip.setViewPager(moreViewPager);
```

##Downloads
**[android-sliding-tab-strip-1.0.5.jar](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/releases/android-sliding-tab-strip-1.0.5.jar)**

**[android-sliding-tab-strip-1.0.5-with-src.jar](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/releases/android-sliding-tab-strip-1.0.5-with-src.jar)**

##Change Log
###1.0.5
>* 更新版权信息

##License
```java
/*
 * Copyright (C) 2013 Peng fei Pan <sky@xiaopan.me>
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
```
