# ![logo_image] PagerIndicator

![Platform][platform_image]
[![API][min_api_image]][min_api_link]
[![Android Arsenal][android_arsenal_image]][android_arsenal-link]
[![Release Version][release_version_image]][release_version-link]

这是 Android 上的一个 ViewPager 页面指示器组件，用于标识当前显示的页面

![sample_image]

## 特性

* 当所有的 Tab 在横向上无法充满整个屏幕的时候，将会根据 allowWidthFull 参数决定是否调整所有的 Item 来充满。调整的规则是平均分配每个 Tab 的宽度，如果某个 Tab 的最小宽度大于平均宽度，那么其宽度保持不变
* 当所有的 Tab 在横向上能够充满整个屏幕的时候，将会从左到右依次排列
* 用法简单，支持在布局中直接包含 View 或者在代码中直接调用 PagerIndicator.addTab() 方法添加
* Tab View 支持设置外边距
* 支持通过 disableTensileSlidingBlock 属性设置是否拉伸滑块图片

## 开始使用

### 1. 导入 PagerIndicator

在 app 的 build.gradle 文件的 dependencies 节点中加入依赖

```groovy
dependencies {
	implementation 'me.panpf:pager-indicator:$lastVersionName'
}
```

请自行替换 `$lastVersionName` 为最新的版本：[![Release Version][release_version_image]][release_version-link] `（不要v）`

### 2. 在布局中使用

```xml
<me.panpf.pagerid.PagerIndicator
    xmlns:app="http://schemas.android.com/apk/res/你的包名"
    android:id="@+id/slidingTabStrip_2"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:background="@drawable/sliding_tab_strip_background"
    app:pi_allowWidthFull="true"
    app:pi_slidingBlock="@drawable/image_sliding_block">
    <LinearLayout
	android:layout_width="wrap_content"
	android:layout_height="wrap_content">
        <TextView style="@style/text_slidingTabTitle" android:text="精品"/>
        <TextView style="@style/text_slidingTabTitle" android:text="排行"/>
        <TextView style="@style/text_slidingTabTitle" android:text="分类"/>
        <TextView style="@style/text_slidingTabTitle" android:text="管理"/>
    </LinearLayout>
</me.panpf.pagerid.PagerIndicator>
```

效果图如下：

![sample_image2]

你还可以在代码中通过 PagerIndicator.setTabViewFactory() 方法添加Tab

支持的布局属性：

* ``pi_slidingBlock``：指定滑块图片，对应的方法是 PagerIndicator.setSlidingBlockDrawable(Drawable)
* ``pi_allowWidthFull``：当所有 Tab 无法充满 PagerIndicator 时是否自动调整所有 Tab 的宽度来达到充满的目的，对应的方式是 PagerIndicator.setAllowWidthFull(boolean)
* ``pi_disableViewPager``：禁用 ViewPager，禁用后不会有初始选中状态效果，也不会绘制滑块，你调用 setViewPager() 方法也不会起作用
* ``pi_disableTensileSlidingBlock``：禁止拉伸滑块图片

### 3. 绑定 ViewPager

```java
ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
viewPager.setAdapter(...);

PagerIndicator pagerIndicator = (PagerIndicator) findViewById(R.id.slidingTabStrip);
pagerIndicator.setViewPager(viewPager);
```

注意：

* 在调用 setViewPager(ViewPager) 方法之前要先设置 ViewPager 的 Adapter

你还可以直接调用 ViewPager.setCurrentItem(int) 方法来改变默认 Tab，无论是在调用 setViewPager(ViewPager) 方法之前还是之后都可以

更多内容请参见 sample 源码

## License
    Copyright (C) 2017 Peng fei Pan <sky@panpf.me>

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[logo_image]: docs/logo.png
[platform_image]: https://img.shields.io/badge/Platform-Android-brightgreen.svg
[min_api_image]: https://img.shields.io/badge/API-9%2B-orange.svg
[min_api_link]: https://android-arsenal.com/api?level=9
[android_arsenal_image]: https://img.shields.io/badge/Android%20Arsenal-PagerIndicator-green.svg?style=true
[android_arsenal-link]: https://android-arsenal.com/details/1/4176
[release_version_image]: https://img.shields.io/github/release/panpf/pager-indicator.svg
[release_version-link]: https://github.com/panpf/pager-indicator/releases
[sample_image]: docs/sample.gif
[sample_image2]: docs/sample2.png
