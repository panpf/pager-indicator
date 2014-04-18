#![Logo](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/res/drawable-mdpi/ic_launcher.png) Android-SlidingTabStrip

这是Android上的一个ViewPager滑动导航条组件，主要就是用于配合ViewPager实现滑动的导航条，以容纳无限多的Tab，最低兼容Android2.2

![smaple](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/docs/sample.png)

##Features
>* 当所有的Tab在横向上无法充满整个屏幕的时候，将会根据allowWidthFull参数决定是否调整所有的Item来充满。调整的规则是平均分配每个Tab的宽度，如果某个Tab的最小宽度大于平均宽度，那么其宽度保持不变
>* 当所有的Tab在横向上能够充满整个屏幕的时候，将会从左到右依次排列
>* 用法简单，支持在布局中直接包含View或者在代码中直接调用SlidingTabStrip.addTab()方法添加

##Usage Guide

####1.添加SlidingTabStrip.java到你的项目中

####2.拷贝属性定义到你的项目的attrs.xml中
```xml
<declare-styleable name="SlidingTabStrip">
    <attr name="slidingBlock" format="reference|color"/>
    <attr name="allowWidthFull" format="boolean"/>
</declare-styleable>
```
>* ``slidingBlock``：指定滑块图片，对应的方法是SlidingTabStrip.setSlidingBlockDrawable(Drawable)
>* ``allowWidthFull``：当所有Tab无法充满SlidingTabStrip时是否自动调整所有Tab的宽度来达到充满的目的，对应的方式是SlidingTabStrip.setAllowWidthFull(boolean)

####3.在布局中引用SlidingTabStrip
```xml
<me.xiaopan.android.slidingtabstrip.SlidingTabStrip
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
</me.xiaopan.android.slidingtabstrip.SlidingTabStrip>
```
效果图如下：

![smaple](https://github.com/xiaopansky/Android-SlidingTabStrip/raw/master/docs/sample2.png)

你还可以在代码中通过SlidingTabStrip.addTab()方法添加Tab

####4.设置ViewPager
```java
ViewPager moreViewPager = (ViewPager) findViewById(R.id.viewPager);
moreViewPager.setAdapter(...);
moreSlidingTabStrip.setViewPager(moreViewPager);
```
注意：
>* 在调用setViewPager(ViewPager)方法之前要先设置ViewPager的Adapter

你还可以直接调用ViewPager.setCurrentItem(int)方法来改变默认Tab（无论是在调用setViewPager(ViewPager)方法之前还是之后都可以）

##Change Log
###1.1.0
>* 增加slidingBlock和allowWidthFull自定义参数
>* 增加布局预览支持，如此一来之前的使用方式就作废了

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