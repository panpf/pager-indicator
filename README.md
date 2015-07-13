#![Logo](docs/logo.png) PagerSlidingTabStrip

这是Android上的一个ViewPager滑动导航条组件，主要就是用于配合ViewPager实现滑动的导航条，以容纳无限多的Tab

![smaple](docs/sample.gif)

##Features
>* 当所有的Tab在横向上无法充满整个屏幕的时候，将会根据allowWidthFull参数决定是否调整所有的Item来充满。调整的规则是平均分配每个Tab的宽度，如果某个Tab的最小宽度大于平均宽度，那么其宽度保持不变
>* 当所有的Tab在横向上能够充满整个屏幕的时候，将会从左到右依次排列
>* 用法简单，支持在布局中直接包含View或者在代码中直接调用PagerSlidingTabStrip.addTab()方法添加
>* Tab View支持设置外边距
>* 支持通过disableTensileSlidingBlock属性设置是否拉伸滑块图片

## Sample App
[Download it on Github](https://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/docs/sample.apk)

![download](/docs/qr_download.png)

##Usage Guide
####1. 导入PagerSlidingTabStrip（Import PagerSlidingTabStrip to your project）

#####使用Gradle（Use Gradle）
``从JCenter仓库导入（Import from jcenter）``

```groovy
dependencies{
	compile 'me.xiaopan:pagerslidingtabstrip:1.6.0'
}
```

``离线模式（Offline work）``

到[releases](https://github.com/xiaopansky/PagerSlidingTabStrip/releases)页面下载最新版本的aar包（这里以pagerslidingtabstrip-1.6.0.aar为例，具体请以你下载的版本为准），并放到你module的libs目录下

然后在你module的build.gradle文件中添加以下代码：
```groovy
repositories{
    flatDir(){
        dirs 'libs'
    }
}

dependencies{
    compile(name:'pagerslidingtabstrip-1.6.0', ext:'aar')
}
```
最后同步一下Gradle即可

#####使用Eclipse（Use Eclipse）
请参考文章[1分钟不用改任何代码在Eclipse中使用AAR](http://blog.csdn.net/u013308121/article/details/46717499)

####2. 配置最低版本（Configure min sdk version）
PagerSlidingTabStrip最低兼容API v7

#####使用Gradle（Use Gradle）
在app/build.gradle文件文件中配置最低版本为7
```groovy
android {
	...

    defaultConfig {
        minSdkVersion 7
        ...
    }
}
```

#####使用Eclipse（Use Eclipse）
在AndroidManifest.xml文件中配置最低版本为7
```xml
<manifest
	...
	>
    <uses-sdk android:minSdkVersion="7"/>
    <application>
    ...
    </application>
</manifest>
```

####3.在布局中引用PagerSlidingTabStrip
```xml
<me.xiaopan.psts.PagerSlidingTabStrip
    xmlns:app="http://schemas.android.com/apk/res/你的包名"
    android:id="@+id/slidingTabStrip_2"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/sliding_tab_strip_background"
    app:allowWidthFull="true"
    app:slidingBlock="@drawable/image_sliding_block">
    <LinearLayout android:layout_width="wrap_content" android:layout_height="wrap_content">
        <TextView style="@style/text_slidingTabTitle" android:text="精品"/>
        <TextView style="@style/text_slidingTabTitle" android:text="排行"/>
        <TextView style="@style/text_slidingTabTitle" android:text="分类"/>
        <TextView style="@style/text_slidingTabTitle" android:text="管理"/>
    </LinearLayout>
</me.xiaopan.psts.PagerSlidingTabStrip>
```
效果图如下：

![smaple](https://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/docs/sample2.png)

你还可以在代码中通过PagerSlidingTabStrip.setTabViewFactory()方法添加Tab

自定义属性释义：
>* ``slidingBlock``：指定滑块图片，对应的方法是PagerSlidingTabStrip.setSlidingBlockDrawable(Drawable)
>* ``allowWidthFull``：当所有Tab无法充满PagerSlidingTabStrip时是否自动调整所有Tab的宽度来达到充满的目的，对应的方式是PagerSlidingTabStrip.setAllowWidthFull(boolean)
>* ``disableViewPager``：禁用ViewPager，禁用后不会有初始选中状态效果，也不会绘制滑块，你调用setViewPager()方法也不会起作用
>* ``disableTensileSlidingBlock``：禁止拉伸滑块图片

####4.设置ViewPager
```java
ViewPager moreViewPager = (ViewPager) findViewById(R.id.viewPager);
moreViewPager.setAdapter(...);
pagerSlidingTabStrip.setViewPager(moreViewPager);
```
注意：
>* 在调用setViewPager(ViewPager)方法之前要先设置ViewPager的Adapter

你还可以直接调用ViewPager.setCurrentItem(int)方法来改变默认Tab（无论是在调用setViewPager(ViewPager)方法之前还是之后都可以）

更详尽的代码请下载源码请使用Android Studio导入查看

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
