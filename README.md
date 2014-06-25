#![Logo](https://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/app/src/main/res/drawable-mdpi/ic_launcher.png) PagerSlidingTabStrip

这是Android上的一个ViewPager滑动导航条组件，主要就是用于配合ViewPager实现滑动的导航条，以容纳无限多的Tab

![smaple](https://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/docs/sample_nexus.png)

##Features
>* 当所有的Tab在横向上无法充满整个屏幕的时候，将会根据allowWidthFull参数决定是否调整所有的Item来充满。调整的规则是平均分配每个Tab的宽度，如果某个Tab的最小宽度大于平均宽度，那么其宽度保持不变
>* 当所有的Tab在横向上能够充满整个屏幕的时候，将会从左到右依次排列
>* 用法简单，支持在布局中直接包含View或者在代码中直接调用PagerSlidingTabStrip.addTab()方法添加

## Sample App
>* [Get it on Google Play](http://play.google.com/store/apps/details?id=me.xiaopan.android.pagerslidingtabstrip)
>* [Download it on Github](http://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/releases/PagerSlidingTabStrip-1.1.0.apk)

##Usage Guide
####1.添加[PagerSlidingTabStrip.java](http://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/app/src/main/java/me/xiaopan/android/pagerslidingtabstrip/PagerSlidingTabStrip.java)到你的项目中

####2.添加[attrs.xml](http://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/app/src/main/res/values/attrs.xml)到你的项目的res/values目录下
如果你的项目中已经存在attrs.xml文件，那么你可以拷贝属性定义部分代码到你的attrs.xml文件中，代码如下：
```xml
<declare-styleable name="PagerSlidingTabStrip">
    <attr name="slidingBlock" format="reference|color"/>
    <attr name="allowWidthFull" format="boolean"/>
    <attr name="disableViewPager" format="boolean"/>
</declare-styleable>
```
属性释义：
>* ``slidingBlock``：指定滑块图片，对应的方法是PagerSlidingTabStrip.setSlidingBlockDrawable(Drawable)
>* ``allowWidthFull``：当所有Tab无法充满PagerSlidingTabStrip时是否自动调整所有Tab的宽度来达到充满的目的，对应的方式是PagerSlidingTabStrip.setAllowWidthFull(boolean)
>* ``disableViewPager``：禁用ViewPager，禁用后不会有初始选中状态效果，也不会绘制滑块，你调用setViewPager()方法也不会起作用

####3.在布局中引用PagerSlidingTabStrip
```xml
<me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip
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
</me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip>
```
效果图如下：

![smaple](https://github.com/xiaopansky/PagerSlidingTabStrip/raw/master/docs/sample2.png)

你还可以在代码中通过PagerSlidingTabStrip.addTab()方法添加Tab

####4.设置ViewPager
```java
ViewPager moreViewPager = (ViewPager) findViewById(R.id.viewPager);
moreViewPager.setAdapter(...);
pagerSlidingTabStrip.setViewPager(moreViewPager);
```
注意：
>* 在调用setViewPager(ViewPager)方法之前要先设置ViewPager的Adapter

你还可以直接调用ViewPager.setCurrentItem(int)方法来改变默认Tab（无论是在调用setViewPager(ViewPager)方法之前还是之后都可以）

####5.示例图源码：
```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res/me.xiaopan.android.pagerslidingtabstrip"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical" >
    <LinearLayout 
        android:layout_width="match_parent" 
        android:layout_height="0dp" 
        android:layout_weight="1" 
        android:orientation="vertical">
        <me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/slidingTabStrip_1"
            app:slidingBlock="@drawable/image_sliding_block"
            android:background="@drawable/sliding_tab_strip_background">
            <LinearLayout 
                android:layout_width="wrap_content" 
                android:layout_height="wrap_content">
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="穿越火线"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="剑灵"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="跑跑卡丁车"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="劲舞团"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="梦幻西游"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="诛仙"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="剑侠情缘"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="征途"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="植物大战僵尸"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="英雄联盟"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="传奇"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="魔兽世界"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="地下城与勇士"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="我叫MT"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="使命召唤"/>
            </LinearLayout>
        </me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip>

        <FrameLayout 
            android:layout_width="match_parent" 
            android:layout_height="0dp" android:layout_weight="1">
            <android.support.v4.view.ViewPager 
                android:id="@+id/viewPager_1" 
                android:layout_width="match_parent" 
                android:layout_height="match_parent"/>
            <include layout="@layout/include_shadow_down"/>
            <include layout="@layout/include_shadow_up"/>
        </FrameLayout>
    </LinearLayout>

    <LinearLayout 
        android:layout_width="match_parent" 
        android:layout_height="0dp" 
        android:layout_weight="1" 
        android:orientation="vertical">
        <me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:id="@+id/slidingTabStrip_2"
            app:allowWidthFull="true"
            app:slidingBlock="@drawable/image_sliding_block"
            android:background="@drawable/sliding_tab_strip_background">
            <LinearLayout 
                android:layout_width="wrap_content" 
                android:layout_height="wrap_content">
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="精品"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="排行"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="分类"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="管理"/>
            </LinearLayout>
        </me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip>

        <FrameLayout 
            android:layout_width="match_parent" 
            android:layout_height="0dp" 
            android:layout_weight="1">
            <android.support.v4.view.ViewPager 
                android:id="@+id/viewPager_2" 
                android:layout_width="match_parent" 
                android:layout_height="match_parent"/>
            <include layout="@layout/include_shadow_down"/>
            <include layout="@layout/include_shadow_up"/>
        </FrameLayout>
    </LinearLayout>

    <LinearLayout 
        android:layout_width="match_parent" 
        android:layout_height="0dp" 
        android:layout_weight="1" 
        android:orientation="vertical">
        <me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/slidingTabStrip_3"
            app:slidingBlock="@drawable/image_sliding_block">
            <LinearLayout 
                android:layout_width="wrap_content" 
                android:layout_height="wrap_content">
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="详情"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="评论"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="攻略"/>
            </LinearLayout>
        </me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip>

        <FrameLayout 
            android:layout_width="match_parent" 
            android:layout_height="0dp" 
            android:layout_weight="1">
            <android.support.v4.view.ViewPager 
                android:id="@+id/viewPager_3" 
                android:layout_width="match_parent" 
                android:layout_height="match_parent"/>
            <include layout="@layout/include_shadow_down"/>
            <include layout="@layout/include_shadow_up"/>
        </FrameLayout>
    </LinearLayout>

    <LinearLayout 
        android:layout_width="match_parent" 
        android:layout_height="0dp" 
        android:layout_weight="1" 
        android:orientation="vertical">
        <me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:id="@+id/slidingTabStrip_4"
            android:layout_gravity="center_horizontal"
            app:slidingBlock="@drawable/image_sliding_block">
            <LinearLayout 
                android:layout_width="wrap_content" 
                android:layout_height="wrap_content">
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="聊天"/>
                <TextView 
                    style="@style/text_slidingTabTitle" 
                    android:text="发现"/>
            </LinearLayout>
        </me.xiaopan.android.pagerslidingtabstrip.PagerSlidingTabStrip>

        <FrameLayout 
            android:layout_width="match_parent" 
            android:layout_height="0dp" 
            android:layout_weight="1">
            <android.support.v4.view.ViewPager 
                android:id="@+id/viewPager_4" 
                android:layout_width="match_parent" 
                android:layout_height="match_parent"/>
            <include layout="@layout/include_shadow_down"/>
            <include layout="@layout/include_shadow_up"/>
        </FrameLayout>
    </LinearLayout>
</LinearLayout>
```

更详尽的代码请下载源码请使用Android Studio导入查看

##Change Log
###1.3.0
>* 增加disableViewPager参数

###1.2.0
>* 修改调整Tab宽度的时机，以前是在onLayout()方法中，现在是在onMeasure()方法中

###1.1.1
>* 增肌对Tab以及TabLayout内边距或外边距的支持
>* 增加Tab点击监听器，通过setClickTabListener()方法即可监听Tab点击

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