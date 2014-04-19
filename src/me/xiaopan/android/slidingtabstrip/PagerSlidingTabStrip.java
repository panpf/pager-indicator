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

package me.xiaopan.android.slidingtabstrip;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 专为ViewPager定制的滑动选项卡 HOME URL：http://github.com/xiaopansky/Android-PagerSlidingTabStrip
 * @version 1.1.0
 * @author Peng fei Pan
 */
public class PagerSlidingTabStrip extends HorizontalScrollView{
    private int currentPosition;	//当前位置
	private int lastOffset;
	private int lastScrollX = 0;
	private float currentPositionOffset;	//当前位置偏移量
	private boolean start;
    private boolean allowWidthFull;    // 内容宽度无法充满时，允许自动调整Item的宽度以充满
	private View currentSelectedTabView;	//当前标题项
	private Drawable slidingBlockDrawable;	//滑块
	private ViewPager viewPager;	//ViewPager
	private ViewGroup tabsLayout;	//标题项布局
	private OnPageChangeListener onPageChangeListener;	//页面改变监听器

	public PagerSlidingTabStrip(Context context) {
		this(context, null);
	}
	
	public PagerSlidingTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
        setHorizontalScrollBarEnabled(false);	//隐藏横向滑动提示条

        if(attrs != null){
            TypedArray attrsTypedArray = context.obtainStyledAttributes(attrs, R.styleable.PagerSlidingTabStrip);
            if(attrsTypedArray != null){
                allowWidthFull = attrsTypedArray.getBoolean(R.styleable.PagerSlidingTabStrip_allowWidthFull, false);
                slidingBlockDrawable = attrsTypedArray.getDrawable(R.styleable.PagerSlidingTabStrip_slidingBlock);
                attrsTypedArray.recycle();
            }
        }
	}

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        // 如果有内容
        if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0){
			// 如果宽度未充满就调整所有Item的宽度以充满
            if((measure(getTabsLayout()).getMeasuredWidth() < r - l) && allowWidthFull){
                // 计算平均宽度
                int viewWidth = r - l;
                int averageWidth = viewWidth /getTabsLayout().getChildCount();
                int bigTabCount = 0;    // 记录宽度超过平均宽度的tab的个数，待会儿算平均值的时候要减去此数
                View tabView;
                for(int w = 0; w < getTabsLayout().getChildCount(); w++){
                    tabView = getTabsLayout().getChildAt(w);
                    if(tabView != null && tabView.getMeasuredWidth() > averageWidth){   // 如果当前视图的宽度大于平均宽度，就从总宽度中减去当前视图的宽度
                        viewWidth -= tabView.getMeasuredWidth();
                        bigTabCount++;
                    }
                }
                averageWidth = viewWidth /(getTabsLayout().getChildCount() - bigTabCount);				//计算新的平均宽度

                // 修改宽度小于平均宽度的Item的宽度
                for(int w = 0; w < getTabsLayout().getChildCount(); w++){
                    //更新宽度并再次测量让新的宽度生效，要不然文字不会居中
                    tabView = getTabsLayout().getChildAt(w);
                    if(tabView != null){
                        ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
                        if(layoutParams != null){
                            layoutParams.width = tabView.getMeasuredWidth()<averageWidth?averageWidth:tabView.getMeasuredWidth();
                            tabView.setLayoutParams(layoutParams);
                            measure(tabView);
                        }
                    }
                }
                measure(getTabsLayout());
            }

            // 初始化滑块位置以及选中状态
            currentPosition = viewPager != null?viewPager.getCurrentItem():0;
            scrollToChild(currentPosition, 0);	//移动滑块到指定位置
            selectedTab(currentPosition);	//选中指定位置的TAB

            //给每一个tab设置点击事件，当点击的时候切换Pager
            for(int w = 0; w < getTabsLayout().getChildCount(); w++){
                final int index = w;
                View itemView = getTabsLayout().getChildAt(index);
                if(itemView != null){
                    itemView.setOnClickListener(new OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            if(viewPager != null){
                                viewPager.setCurrentItem(index, true);
                            }
                        }
                    });
                }
            }
        }

        super.onLayout(changed, l, t, r, b);
    }
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/* 绘制滑块 */
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0 && slidingBlockDrawable != null){
			View currentTab = getTabsLayout().getChildAt(currentPosition);
			if(currentTab != null){
                float slidingBlockLeft = currentTab.getLeft();
                float slidingBlockRight = currentTab.getRight();
                if (currentPositionOffset > 0f && currentPosition < getTabsLayout().getChildCount() - 1) {
                    View nextTab = getTabsLayout().getChildAt(currentPosition + 1);
                    if(nextTab != null){
                        final float nextTabLeft = nextTab.getLeft();
                        final float nextTabRight = nextTab.getRight();
                        slidingBlockLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * slidingBlockLeft);
                        slidingBlockRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * slidingBlockRight);
                    }
                }
                slidingBlockDrawable.setBounds((int)slidingBlockLeft, 0, (int)slidingBlockRight, getHeight());
                slidingBlockDrawable.draw(canvas);
            }
		}
	}

    /**
     * 获取布局
     */
    private ViewGroup getTabsLayout(){
        if(tabsLayout == null){
            if(getChildCount() > 0){
                tabsLayout = (ViewGroup) getChildAt(0);
            }else{
                removeAllViews();
                tabsLayout = new LinearLayout(getContext());
                addView(tabsLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
        return tabsLayout;
    }
	
	/**
	 * 滚动到指定的位置
	 */
	private void scrollToChild(int position, int offset) {
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0 && position< getTabsLayout().getChildCount()){
			View view = getTabsLayout().getChildAt(position);
            if(view != null){
                //计算新的X坐标
                int newScrollX = view.getLeft() + offset;
                if (position > 0 || offset > 0) {
                    newScrollX -= 240 - getOffset(view.getWidth())/2;
                }

                //如果同上次X坐标不一样就执行滚动
                if (newScrollX != lastScrollX) {
                    lastScrollX = newScrollX;
                    scrollTo(newScrollX, 0);
                }
            }
		}
	}
	
	/**
	 * 获取偏移量
	 */
	private int getOffset(int newOffset){
		if(lastOffset < newOffset){
            if(start){
                lastOffset += 1;
                return lastOffset;
            }else{
                start = true;
                lastOffset += 1;
                return lastOffset;
            }
        }if(lastOffset > newOffset){
            if(start){
                lastOffset -= 1;
                return lastOffset;
            }else{
                start = true;
                lastOffset -= 1;
                return lastOffset;
            }
        }else{
            start = true;
            lastOffset = newOffset;
            return lastOffset;
        }
	}

    /**
     * 执行测量，执行完成之后只需调用View的getMeasuredXXX()方法即可获取测量结果
     */
    private View measure(View view){
        ViewGroup.LayoutParams p = view.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight, MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        }
        view.measure(childWidthSpec, childHeightSpec);
        return view;
    }

    /**
     * 选中指定位置的TAB
     */
    private void selectedTab(int currentSelectedTabPosition){
        if(currentSelectedTabPosition > -1 && getTabsLayout() != null && currentSelectedTabPosition < getTabsLayout().getChildCount()){
            if(currentSelectedTabView != null){
                currentSelectedTabView.setSelected(false);
            }
            currentSelectedTabView = getTabsLayout().getChildAt(currentSelectedTabPosition);
            if(currentSelectedTabView != null){
                currentSelectedTabView.setSelected(true);
            }
        }
    }

    /**
     * 添加Tab
     */
    public void addTab(View tabView, int index){
        if(tabView != null){
            getTabsLayout().addView(tabView, index);
            requestLayout();
        }
    }

    /**
     * 添加Tab
     */
    public void addTab(View tabView){
        addTab(tabView, -1);
    }

    /**
     * 添加Tab
     * @param tabViews 可以一次添加多个Tab
     */
    public void addTab(View... tabViews) {
        if(tabViews != null && getTabsLayout() != null){
            for(View view : tabViews){
                getTabsLayout().addView(view);
            }
            requestLayout();
        }
    }

    /**
     * 添加Tab
     */
    public void addTab(List<View> tabViews) {
        if(tabViews != null && tabViews.size() > 0 && getTabsLayout() != null){
            for(View view : tabViews){
                getTabsLayout().addView(view);
            }
            requestLayout();
        }
    }

	/**
	 * 设置ViewPager
	 * @param viewPager ViewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		this.viewPager.setOnPageChangeListener(new OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                selectedTab(position);
                if(onPageChangeListener != null){
                    onPageChangeListener.onPageSelected(position);
                }
            }

            @Override
            public void onPageScrolled(int nextPagePosition, float positionOffset, int positionOffsetPixels) {
                if(nextPagePosition < getTabsLayout().getChildCount()){
                    View view = getTabsLayout().getChildAt(nextPagePosition);
                    if(view != null){
                        currentPosition = nextPagePosition;
                        currentPositionOffset = positionOffset;
                        scrollToChild(nextPagePosition, (int) (positionOffset * view.getWidth()));
                        invalidate();
                        if(onPageChangeListener != null){
                            onPageChangeListener.onPageScrolled(nextPagePosition, positionOffset, positionOffsetPixels);
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
                if(onPageChangeListener != null){
                    onPageChangeListener.onPageScrollStateChanged(arg0);
                }
            }
        });
        requestLayout();
	}
	
	/**
	 * 设置Page切换监听器
	 * @param onPageChangeListener Page切换监听器
	 */
	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}

    /**
     * 设置是否充满屏幕
     * @param allowWidthFull true：当内容的宽度无法充满屏幕时，自动调整每一个Item的宽度以充满屏幕
     */
    public void setAllowWidthFull(boolean allowWidthFull) {
        this.allowWidthFull = allowWidthFull;
        requestLayout();
    }

    /**
	 * 设置滑块图片
	 */
	public void setSlidingBlockDrawable(Drawable slidingBlockDrawable) {
		this.slidingBlockDrawable = slidingBlockDrawable;
        requestLayout();
	}

    /**
     * 获取Tab总数
     */
    public int getTabCount(){
        return getTabsLayout()!=null?getTabsLayout().getChildCount():0;
    }
}