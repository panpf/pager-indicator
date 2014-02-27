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

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class SlidingTabStrip extends HorizontalScrollView implements OnPageChangeListener, OnGlobalLayoutListener{
	private int currentPosition;	//当前位置
	private int lastOffset;
	private int lastScrollX = 0;
	private float currentPositionOffset;	//当前位置偏移量
	private boolean start;
	private ViewGroup tabsLayout;	//标题项布局
	private ViewPager viewPager;	//ViewPager
	private View currentSelectedTabView;	//当前标题项
	private Drawable slidingBlockDrawable;	//滑块
	private OnPageChangeListener onPageChangeListener;	//页面改变监听器
	
	public SlidingTabStrip(Context context) {
		super(context);
		init();
	}
	
	public SlidingTabStrip(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}
	
	/**
	 * 初始化
	 */
	private void init(){
		setHorizontalScrollBarEnabled(false);	//隐藏横向滑动提示条
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		/* 绘制滑块 */
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0 && slidingBlockDrawable != null){
			View currentTab = getTabsLayout().getChildAt(currentPosition);
			float slidingBlockLeft = currentTab.getLeft();
			float slidingBlockRight = currentTab.getRight();
			if (currentPositionOffset > 0f && currentPosition < getTabsLayout().getChildCount() - 1) {
				View nextTab = getTabsLayout().getChildAt(currentPosition + 1);
				final float nextTabLeft = nextTab.getLeft();
				final float nextTabRight = nextTab.getRight();
				slidingBlockLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * slidingBlockLeft);
				slidingBlockRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * slidingBlockRight);
			}
			slidingBlockDrawable.setBounds((int)slidingBlockLeft, 0, (int)slidingBlockRight, getHeight());
			slidingBlockDrawable.draw(canvas);
		}
	}
	
	/**
	 * 添加Tab
	 * @param tabView
	 * @param index
	 */
	public void addTab(View tabView, int index){
		if(tabView != null){
			getTabsLayout().addView(tabView, index);
			getViewTreeObserver().addOnGlobalLayoutListener(this);
			requestLayout();
		}
	}
	
	/**
	 * 添加Tab
	 * @param tabView
	 */
	public void addTab(View tabView){
		addTab(tabView, -1);
	}

	/**
	 * 添加Tab
	 * @param tabViews 可以一次添加多个Tab
	 */
	public void addTab(View... tabViews) {
		if(tabViews != null && tabViews.length > 0 && getTabsLayout() != null){
			for(int w = 0; w < tabViews.length; w++){
				getTabsLayout().addView(tabViews[w]);
			}
			getViewTreeObserver().addOnGlobalLayoutListener(this);
			requestLayout();
		}
	}

	/**
	 * 添加Tab
	 * @param tabViews
	 */
	public void addTab(List<View> tabViews) {
		if(tabViews != null && tabViews.size() > 0 && getTabsLayout() != null){
			for(int w = 0; w < tabViews.size(); w++){
				getTabsLayout().addView(tabViews.get(w));
			}
			getViewTreeObserver().addOnGlobalLayoutListener(this);
			requestLayout();
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	@Override
	@SuppressWarnings("deprecation")
	public void onGlobalLayout() {
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		} else {
			getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
		
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0){
			/* 初始化滑块位置以及选选中状态 */
			currentPosition = viewPager != null?viewPager.getCurrentItem():0;
			scrollToChild(currentPosition, 0);	//移动滑块到指定位置
			selectedTab(currentPosition);	//选中指定位置的TAB
			
			//给每一个tab设置点击事件，当点击的时候切换Pager
			for(int w = 0; w < getTabsLayout().getChildCount(); w++){
				getTabsLayout().getChildAt(w).setOnClickListener(new TabClickListener(w));
			}
		}
	}
	
	/**
	 * 获取布局
	 * @return
	 */
	private ViewGroup getTabsLayout(){
		if(tabsLayout == null){
			if(getChildCount() > 0){
				tabsLayout = (ViewGroup) getChildAt(0);
			}else{
				removeAllViews();
				tabsLayout = new LinearLayout(getContext());
				addView(tabsLayout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
			}
		}
		return tabsLayout;
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		/* 如果当前的标题内容在横向上不能够将SlidingTabStrip充满，那么就试图更改所有标题的宽度，来充满SlidingTabStrip */
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0 && measure(getTabsLayout()).getMeasuredWidth() < r - l){
			/* 先计算出除去宽度大于平均宽度的tab之后的平均宽度 */
			int canDivideWidth = r - l;		//初始化可平分宽度，默认为总宽度
			int averageWidth = canDivideWidth/getTabsLayout().getChildCount();										//计算平均宽度
			int bigTabCount = 0;																															//记录宽度超过平均宽度的tab的个数，待会儿算平均值的时候要减去此数
			View tabView;
			for(int w = 0; w < getTabsLayout().getChildCount(); w++){
				tabView = getTabsLayout().getChildAt(w);
				if(tabView.getMeasuredWidth() > averageWidth){																		//如果当前视图的宽度大于平均宽度，就从总宽度中减去当前视图的宽度
					canDivideWidth -= tabView.getMeasuredWidth();
					bigTabCount++;
				}
			}
			int newAverageWidth = canDivideWidth/(getTabsLayout().getChildCount() - bigTabCount);				//计算新的平均宽度
			
			/* 将宽度小于平均宽度的tab的宽度和测量宽度设为新的平均宽度 */
			for(int w = 0; w < getTabsLayout().getChildCount(); w++){
				//更新宽度并再次测量让新的宽度生效，要不然文字不会居中
				tabView = getTabsLayout().getChildAt(w);
				ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
				layoutParams.width = tabView.getMeasuredWidth() < averageWidth?newAverageWidth:tabView.getMeasuredWidth();
				tabView.setLayoutParams(layoutParams);
				measure(tabView);
			}
			measure(getTabsLayout());
		}
		super.onLayout(changed, l, t, r, b);
	}
	
	@Override
	public void onPageSelected(int position) {
		selectedTab(position);
		if(onPageChangeListener != null){
			onPageChangeListener.onPageSelected(position);
		}
	}
	
	@Override
	public void onPageScrolled(int nextPagePosition, float positionOffset, int positionOffsetPixels) {
		currentPosition = nextPagePosition;
		currentPositionOffset = positionOffset;
		scrollToChild(nextPagePosition, (int) (positionOffset * getTabsLayout().getChildAt(nextPagePosition).getWidth()));
		invalidate();
		if(onPageChangeListener != null){
			onPageChangeListener.onPageScrolled(nextPagePosition, positionOffset, positionOffsetPixels);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(onPageChangeListener != null){
			onPageChangeListener.onPageScrollStateChanged(arg0);
		}
	}
	
	/**
	 * 滚动到指定的位置
	 * @param position
	 * @param offset
	 */
	private void scrollToChild(int position, int offset) {
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0){
			//计算新的X坐标
			int newScrollX = getTabsLayout().getChildAt(position).getLeft() + offset;
			if (position > 0 || offset > 0) {
				newScrollX -= 240 - getOffset(getTabsLayout().getChildAt(position).getWidth())/2;
			}
			
			//如果同上次X坐标不一样就执行滚动
			if (newScrollX != lastScrollX) {
				lastScrollX = newScrollX;
				scrollTo(newScrollX, 0);
			}
		}
	}
	
	/**
	 * 获取偏移量
	 * @param newOffset
	 * @return
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
	 * 设置ViewPager
	 * @param viewPager
	 */
	public void setViewPager(ViewPager viewPager) {
		this.viewPager = viewPager;
		this.viewPager.setOnPageChangeListener(this);
	}
	
	/**
	 * 设置Page切换监听器
	 * @param onPageChangeListener Page切换监听器
	 */
	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}
	
	/**
	 * 设置滑块Drawable
	 * @param slidingBlockDrawable
	 */
	public void setSlidingBlockDrawable(Drawable slidingBlockDrawable) {
		this.slidingBlockDrawable = slidingBlockDrawable;
	}
	
	/**
	 * 执行测量，执行完成之后只需调用View的getMeasuredXXX()方法即可获取测量结果
	 * @param view
	 * @return
	 */
	private final View measure(View view){
		ViewGroup.LayoutParams p = view.getLayoutParams();
	    if (p == null) {
	        p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
	    }
	    int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
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
	 * @param currentSelectedTabPosition
	 */
	private void selectedTab(int currentSelectedTabPosition){
		if(currentSelectedTabPosition > -1 && getTabsLayout() != null && currentSelectedTabPosition < getTabsLayout().getChildCount()){
			if(currentSelectedTabView != null){
				currentSelectedTabView.setSelected(false);
			}
			currentSelectedTabView = getTabsLayout().getChildAt(currentSelectedTabPosition);
			currentSelectedTabView.setSelected(true);
		}
	}
	
	/**
	 * Tab点击监听器
	 */
	private class TabClickListener implements View.OnClickListener{
		private int tabPosition;
		
		public TabClickListener(int tabPosition){
			this.tabPosition = tabPosition;
		}
		
		@Override
		public void onClick(View v) {
			if(viewPager != null){
				viewPager.setCurrentItem(tabPosition, true);
			}
		}
	}
}