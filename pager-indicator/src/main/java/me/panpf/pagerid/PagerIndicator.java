/*
 * Copyright (C) 2017 Peng fei Pan <sky@panpf.me>
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

package me.panpf.pagerid;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 专为 ViewPager 定制的页面指示器
 */
public class PagerIndicator extends HorizontalScrollView {
    private int currentPosition;    //当前位置
    private int lastOffset;
    private int lastScrollX = 0;
    private float currentPositionOffset;    //当前位置偏移量
    private boolean start;
    private boolean allowWidthFull;    // 内容宽度无法充满时，允许自动调整Item的宽度以充满
    private boolean disableViewPager;   // 禁用ViewPager
    private Drawable slidingBlockDrawable;    //滑块
    private ViewPager viewPager;    //ViewPager
    private ViewGroup tabsLayout;    //标题项布局
    private ViewPager.OnPageChangeListener onPageChangeListener;    //页面改变监听器
    private OnClickTabListener onClickTabListener;
    private OnDoubleClickTabListener onDoubleClickTabListener;
    private List<View> tabViews;
    private boolean disableTensileSlidingBlock; // 禁止拉伸滑块图片
    private TabViewFactory tabViewFactory;
    private Paint bottomLinePaint;
    private int bottomLineColor = -1;
    private int bottomLineHeight = -1;
    private final PageChangedListener pageChangedListener = new PageChangedListener();
    private final TabViewClickListener tabViewClickListener = new TabViewClickListener();
    private final SetSelectedTabListener setSelectedTabListener = new SetSelectedTabListener();
    private final DoubleClickGestureDetector tabViewDoubleClickGestureDetector;

    public PagerIndicator(Context context) {
        this(context, null);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);    //隐藏横向滑动提示条
        removeAllViews();
        if (attrs != null) {
            TypedArray attrsTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerIndicator);
            if (attrsTypedArray != null) {
                allowWidthFull = attrsTypedArray.getBoolean(R.styleable.PagerIndicator_pi_allowWidthFull, false);
                slidingBlockDrawable = attrsTypedArray.getDrawable(R.styleable.PagerIndicator_pi_slidingBlock);
                disableViewPager = attrsTypedArray.getBoolean(R.styleable.PagerIndicator_pi_disableViewPager, false);
                disableTensileSlidingBlock = attrsTypedArray.getBoolean(R.styleable.PagerIndicator_pi_disableTensileSlidingBlock, false);
                bottomLineColor = attrsTypedArray.getColor(R.styleable.PagerIndicator_pi_bottomLineColor, -1);
                bottomLineHeight = (int) attrsTypedArray.getDimension(R.styleable.PagerIndicator_pi_bottomLineHeight, -1);
                attrsTypedArray.recycle();
            }
        }
        tabViewDoubleClickGestureDetector = new DoubleClickGestureDetector(context);

        getViewTreeObserver().addOnGlobalLayoutListener(setSelectedTabListener);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (allowWidthFull && tabsLayout != null) {
            View childView;
            for (int w = 0, size = tabsLayout.getChildCount(); w < size; w++) {
                childView = tabsLayout.getChildAt(w);
                ViewGroup.LayoutParams params = childView.getLayoutParams();
                params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                childView.setLayoutParams(params);
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (!allowWidthFull) {
            return;
        }
        ViewGroup tabsLayout = getTabsLayout();
        if (tabsLayout == null) {
            return;
        }
        if (tabsLayout.getChildCount() <= 0) {
            return;
        }

        if (tabViews == null) {
            tabViews = new ArrayList<View>();
        } else {
            tabViews.clear();
        }
        for (int w = 0; w < tabsLayout.getChildCount(); w++) {
            tabViews.add(tabsLayout.getChildAt(w));
        }

        adjustChildWidthWithParent(tabViews, getMeasuredWidth() - tabsLayout.getPaddingLeft() - tabsLayout.getPaddingRight(), widthMeasureSpec, heightMeasureSpec);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    /**
     * 调整views集合中的View，让所有View的宽度加起来正好等于parentViewWidth
     *
     * @param views                   子View集合
     * @param parentViewWidth         父Vie的宽度
     * @param parentWidthMeasureSpec  父View的宽度规则
     * @param parentHeightMeasureSpec 父View的高度规则
     */
    private void adjustChildWidthWithParent(List<View> views, int parentViewWidth, int parentWidthMeasureSpec, int parentHeightMeasureSpec) {
        // 先去掉所有子View的外边距
        for (View view : views) {
            if (view.getLayoutParams() instanceof MarginLayoutParams) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) view.getLayoutParams();
                parentViewWidth -= lp.leftMargin + lp.rightMargin;
            }
        }

        // 去掉宽度大于平均宽度的View后再次计算平均宽度
        int averageWidth = parentViewWidth / views.size();
        int bigTabCount = views.size();
        while (true) {
            Iterator<View> iterator = views.iterator();
            while (iterator.hasNext()) {
                View view = iterator.next();
                if (view.getMeasuredWidth() > averageWidth) {
                    parentViewWidth -= view.getMeasuredWidth();
                    bigTabCount--;
                    iterator.remove();
                }
            }
            if (bigTabCount <= 0) {
                break;
            }
            averageWidth = parentViewWidth / bigTabCount;
            boolean end = true;
            for (View view : views) {
                if (view.getMeasuredWidth() > averageWidth) {
                    end = false;
                }
            }
            if (end) {
                break;
            }
        }

        // 修改宽度小于新的平均宽度的View的宽度
        for (View view : views) {
            if (view.getMeasuredWidth() < averageWidth) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
                layoutParams.width = averageWidth;
                view.setLayoutParams(layoutParams);
                // 再次测量让新宽度生效
                if (layoutParams instanceof MarginLayoutParams) {
                    measureChildWithMargins(view, parentWidthMeasureSpec, 0, parentHeightMeasureSpec, 0);
                } else {
                    measureChild(view, parentWidthMeasureSpec, parentHeightMeasureSpec);
                }
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (bottomLineColor != -1 && bottomLineHeight != -1) {
            if (bottomLinePaint == null) {
                bottomLinePaint = new Paint();
                bottomLinePaint.setColor(bottomLineColor);
            }
            canvas.drawRect(0, getBottom() - bottomLineHeight, getRight(), getBottom(), bottomLinePaint);
        }

        if (disableViewPager) return;
        /* 绘制滑块 */
        ViewGroup tabsLayout = getTabsLayout();
        if (tabsLayout != null && tabsLayout.getChildCount() > 0 && slidingBlockDrawable != null) {
            View currentTab = tabsLayout.getChildAt(currentPosition);
            if (currentTab != null) {
                float slidingBlockLeft = currentTab.getLeft();
                float slidingBlockRight = currentTab.getRight();
                if (currentPositionOffset > 0f && currentPosition < tabsLayout.getChildCount() - 1) {
                    View nextTab = tabsLayout.getChildAt(currentPosition + 1);
                    if (nextTab != null) {
                        final float nextTabLeft = nextTab.getLeft();
                        final float nextTabRight = nextTab.getRight();
                        slidingBlockLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * slidingBlockLeft);
                        slidingBlockRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * slidingBlockRight);
                    }
                }

                // 不拉伸
                if (disableTensileSlidingBlock) {
                    int center = (int) (slidingBlockLeft + (slidingBlockRight - slidingBlockLeft) / 2);
                    slidingBlockLeft = center - slidingBlockDrawable.getIntrinsicWidth() / 2;
                    slidingBlockRight = center + slidingBlockDrawable.getIntrinsicWidth() / 2;
                }

                slidingBlockDrawable.setBounds((int) slidingBlockLeft, getHeight() - slidingBlockDrawable.getIntrinsicHeight(), (int) slidingBlockRight, getHeight());
                slidingBlockDrawable.draw(canvas);
            }
        }
    }

    /**
     * 获取布局
     */
    private ViewGroup getTabsLayout() {
        if (tabsLayout == null) {
            if (getChildCount() > 0) {
                tabsLayout = (ViewGroup) getChildAt(0);
            } else {
                removeAllViews();
                LinearLayout tabsLayout = new LinearLayout(getContext());
                tabsLayout.setGravity(Gravity.CENTER_VERTICAL);
                this.tabsLayout = tabsLayout;
                addView(tabsLayout, new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL));
            }
        }
        return tabsLayout;
    }

    /**
     * 重置，清除所有tab
     */
    public void reset() {
        if (tabViewFactory != null) {
            ViewGroup tabViewGroup = getTabsLayout();
            tabViewFactory.addTabs(tabViewGroup, viewPager != null ? viewPager.getCurrentItem() : 0);
            setTabClickEvent();
        }
    }

    private void setTabClickEvent(){
        ViewGroup tabViewGroup = getTabsLayout();
        if(tabViewGroup != null && tabViewGroup.getChildCount() > 0){
            //给每一个tab设置点击事件，当点击的时候切换Pager
            for (int w = 0; w < tabViewGroup.getChildCount(); w++) {
                View itemView = tabViewGroup.getChildAt(w);
                itemView.setTag(w);
                itemView.setOnClickListener(tabViewClickListener);
                itemView.setOnTouchListener(tabViewDoubleClickGestureDetector);
            }
        }
    }

    /**
     * 获取Tab
     *
     * @param position 位置
     * @return Tab的View
     */
    @SuppressWarnings("unused")
    public View getTab(int position) {
        if (tabsLayout != null && tabsLayout.getChildCount() > position) {
            return tabsLayout.getChildAt(position);
        } else {
            return null;
        }
    }

    /**
     * 滚动到指定的位置
     */
    private void scrollToChild(int position, int offset) {
        ViewGroup tabsLayout = getTabsLayout();
        if (tabsLayout != null && tabsLayout.getChildCount() > 0 && position < tabsLayout.getChildCount()) {
            View view = tabsLayout.getChildAt(position);
            if (view != null) {
                //计算新的X坐标
                int newScrollX = view.getLeft() + offset - getLeftMargin(view);
                if (position > 0 || offset > 0) {
                    newScrollX -= getWidth() / 2 - getOffset(view.getWidth()) / 2;
                }

                //如果同上次X坐标不一样就执行滚动
                if (newScrollX != lastScrollX) {
                    lastScrollX = newScrollX;
                    scrollTo(newScrollX, 0);
                }
            }
        }
    }

    private int getLeftMargin(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams marginParams = (MarginLayoutParams) params;
            return marginParams.leftMargin;
        }
        return 0;
    }

    private int getRightMargin(View view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (params instanceof MarginLayoutParams) {
            MarginLayoutParams marginParams = (MarginLayoutParams) params;
            return marginParams.rightMargin;
        }
        return 0;
    }

    /**
     * 获取偏移量
     */
    private int getOffset(int newOffset) {
        if (lastOffset < newOffset) {
            if (start) {
                lastOffset += 1;
                return lastOffset;
            } else {
                start = true;
                lastOffset += 1;
                return lastOffset;
            }
        }
        if (lastOffset > newOffset) {
            if (start) {
                lastOffset -= 1;
                return lastOffset;
            } else {
                start = true;
                lastOffset -= 1;
                return lastOffset;
            }
        } else {
            start = true;
            lastOffset = newOffset;
            return lastOffset;
        }
    }

    /**
     * 选中指定位置的TAB
     */
    private void selectedTab(int newSelectedTabPosition) {
        ViewGroup tabsLayout = getTabsLayout();
        if (newSelectedTabPosition > -1 && tabsLayout != null && newSelectedTabPosition < tabsLayout.getChildCount()) {
            for (int w = 0, size = tabsLayout.getChildCount(); w < size; w++) {
                View tabView = tabsLayout.getChildAt(w);
                tabView.setSelected(w == newSelectedTabPosition);
            }
        }
    }

    /**
     * 设置ViewPager
     *
     * @param viewPager ViewPager
     */
    public void setViewPager(ViewPager viewPager) {
        if (disableViewPager) return;
        this.viewPager = viewPager;
        this.viewPager.addOnPageChangeListener(pageChangedListener);
        setTabClickEvent();
        requestLayout();
    }

    /**
     * 设置Page切换监听器
     *
     * @param onPageChangeListener Page切换监听器
     */
    @SuppressWarnings("unused")
    public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
    }

    /**
     * 设置是否充满屏幕
     *
     * @param allowWidthFull true：当内容的宽度无法充满屏幕时，自动调整每一个Item的宽度以充满屏幕
     */
    public void setAllowWidthFull(boolean allowWidthFull) {
        this.allowWidthFull = allowWidthFull;
        requestLayout();
    }

    /**
     * 设置滑块图片
     */
    @SuppressWarnings("unused")
    public void setSlidingBlockDrawable(Drawable slidingBlockDrawable) {
        this.slidingBlockDrawable = slidingBlockDrawable;
        requestLayout();
    }

    /**
     * 获取滑块图片
     */
    @SuppressWarnings("unused")
    public Drawable getSlidingBlockDrawable() {
        return slidingBlockDrawable;
    }

    /**
     * 设置是否禁止拉伸滑块图片
     *
     * @param disableTensileSlidingBlock 是否禁止拉伸滑块图片
     */
    @SuppressWarnings("unused")
    public void setDisableTensileSlidingBlock(boolean disableTensileSlidingBlock) {
        this.disableTensileSlidingBlock = disableTensileSlidingBlock;
        invalidate();
    }

    /**
     * 获取Tab总数
     */
    public int getTabCount() {
        ViewGroup tabsLayout = getTabsLayout();
        return tabsLayout != null ? tabsLayout.getChildCount() : 0;
    }

    /**
     * 设置Tab点击监听器
     *
     * @param onClickTabListener Tab点击监听器
     */
    @SuppressWarnings("unused")
    public void setOnClickTabListener(OnClickTabListener onClickTabListener) {
        this.onClickTabListener = onClickTabListener;
    }

    /**
     * 设置TAB双击监听器
     *
     * @param onDoubleClickTabListener TAB双击监听器
     */
    public void setOnDoubleClickTabListener(OnDoubleClickTabListener onDoubleClickTabListener) {
        this.onDoubleClickTabListener = onDoubleClickTabListener;
    }

    /**
     * 设置不使用ViewPager
     *
     * @param disableViewPager 不使用ViewPager
     */
    @SuppressWarnings("unused")
    public void setDisableViewPager(boolean disableViewPager) {
        this.disableViewPager = disableViewPager;
        if (viewPager != null) {
            viewPager.removeOnPageChangeListener(onPageChangeListener);
            viewPager = null;
        }
        requestLayout();
    }

    /**
     * 设置TabView生成器
     *
     * @param tabViewFactory TabView生成器
     */
    @SuppressWarnings("unused")
    public void setTabViewFactory(TabViewFactory tabViewFactory) {
        this.tabViewFactory = tabViewFactory;

        reset();

        getViewTreeObserver().addOnGlobalLayoutListener(setSelectedTabListener);
    }

    /**
     * 设置底线的颜色
     *
     * @param bottomLineColor 底线的颜色
     */
    @SuppressWarnings("unused")
    public void setBottomLineColor(int bottomLineColor) {
        this.bottomLineColor = bottomLineColor;
        if (bottomLinePaint != null) {
            bottomLinePaint.setColor(bottomLineColor);
        }
        postInvalidate();
    }

    /**
     * 设置底线的高度
     *
     * @param bottomLineHeight 底线的高度
     */
    @SuppressWarnings("unused")
    public void setBottomLineHeight(int bottomLineHeight) {
        this.bottomLineHeight = bottomLineHeight;
        postInvalidate();
    }

    /**
     * Tab点击监听器
     */
    public interface OnClickTabListener {
        void onClickTab(View tab, int index);
    }

    /**
     * Tab双击监听器
     */
    public interface OnDoubleClickTabListener {
        void onDoubleClickTab(View view, int index);
    }

    /**
     * TabView生成器
     */
    public interface TabViewFactory {
        /**
         * 添加tab
         *
         * @param parent              父View
         * @param currentItemPosition 当前选中的位置
         */
        void addTabs(ViewGroup parent, int currentItemPosition);
    }

    private class PageChangedListener implements ViewPager.OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            selectedTab(position);
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageSelected(position);
            }
        }

        @Override
        public void onPageScrolled(int nextPagePosition, float positionOffset, int positionOffsetPixels) {
            ViewGroup tabsLayout = getTabsLayout();
            if (nextPagePosition < tabsLayout.getChildCount()) {
                View view = tabsLayout.getChildAt(nextPagePosition);
                if (view != null) {
                    currentPosition = nextPagePosition;
                    currentPositionOffset = positionOffset;
                    scrollToChild(nextPagePosition, (int) (positionOffset * (view.getWidth() + getLeftMargin(view) + getRightMargin(view))));
                    invalidate();
                }
            }
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrolled(nextPagePosition, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
            if (onPageChangeListener != null) {
                onPageChangeListener.onPageScrollStateChanged(arg0);
            }
        }
    }

    private class TabViewClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            int index = (Integer) v.getTag();
            if (onClickTabListener != null) {
                onClickTabListener.onClickTab(v, index);
            }
            if (viewPager != null) {
                viewPager.setCurrentItem(index, true);
            }
        }
    }

    private class SetSelectedTabListener implements ViewTreeObserver.OnGlobalLayoutListener {
        @Override
        public void onGlobalLayout() {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
            } else {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }

            ViewGroup tabViewGroup = getTabsLayout();
            if (tabViewGroup != null) {
                currentPosition = viewPager != null ? viewPager.getCurrentItem() : 0;
                if (!disableViewPager) {
                    scrollToChild(currentPosition, 0);
                    selectedTab(currentPosition);
                }
            }
        }
    }

    private class DoubleClickGestureDetector extends GestureDetector.SimpleOnGestureListener implements OnTouchListener {
        private GestureDetector gestureDetector;
        private View currentView;

        public DoubleClickGestureDetector(Context context) {
            gestureDetector = new GestureDetector(context, this);
            gestureDetector.setOnDoubleTapListener(this);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (onDoubleClickTabListener != null) {
                onDoubleClickTabListener.onDoubleClickTab(currentView, (Integer) currentView.getTag());
                return true;
            } else {
                return false;
            }
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            currentView = v;
            return gestureDetector.onTouchEvent(event);
        }
    }
}