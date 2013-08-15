package me.xiaopan.android.slidingtabstrip;

import java.lang.reflect.Field;
import java.util.List;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

/**
 * 带有滑动标题的ViewPager，你只需调用setTabs
 */
public class SlidingTabStrip extends HorizontalScrollView implements OnPageChangeListener, OnGlobalLayoutListener{
	private int centerPosition;	//中间位置
	private ViewGroup tabsLayout;	//标题项布局
	private ViewPager viewPager;	//ViewPager
	private View currentSelectedTabView;	//当前标题项
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

	/**
	 * 添加Tab
	 * @param tabs 可以一次添加多个Tab
	 */
	public void addTabs(View... tabs) {
		if(tabs != null && tabs.length > 0 && getTabsLayout() != null){
			for(int w = 0; w < tabs.length; w++){
				getTabsLayout().addView(tabs[w]);
			}
			getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
	}

	/**
	 * 添加Tab
	 * @param tabs 可以一次添加多个Tab
	 */
	public void addTabs(List<View> tabs) {
		if(tabs != null && tabs.size() > 0 && getTabsLayout() != null){
			for(int w = 0; w < tabs.size(); w++){
				getTabsLayout().addView(tabs.get(w));
			}
			getViewTreeObserver().addOnGlobalLayoutListener(this);
		}
	}
	
	@Override
	@SuppressWarnings("deprecation")
	public void onGlobalLayout() {
		if(getTabsLayout() != null && getTabsLayout().getChildCount() > 0){
			//初始化第一个选中的
			if(currentSelectedTabView == null){
				currentSelectedTabView = getTabsLayout().getChildAt(0);
				currentSelectedTabView.setSelected(true);
			}
			
			//给每一个tab设置点击事件，当点击的时候切换Pager
			for(int w = 0; w < getTabsLayout().getChildCount(); w++){
				getTabsLayout().getChildAt(w).setOnClickListener(new TabClickListener(w));
			}
		}
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
			getViewTreeObserver().removeGlobalOnLayoutListener(this);
		} else {
			getViewTreeObserver().removeOnGlobalLayoutListener(this);
		}
	}
	
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
		centerPosition = (r + l) / 2;	//初始化中间位置
		
		/* 如果当前的标题内容在横向上不能够将SlideTitlebar充满，那么就试图更改所有标题的宽度，来充满SlideTitlebar */
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
				//设置测量宽度和宽度
				tabView = getTabsLayout().getChildAt(w);
				if(tabView.getMeasuredWidth() < averageWidth){
					setMeasuredWidth(tabView, newAverageWidth);
				}
				ViewGroup.LayoutParams layoutParams = tabView.getLayoutParams();
				layoutParams.width = tabView.getMeasuredWidth();
				tabView.setLayoutParams(layoutParams);
				
				//更新完宽度之后再测量一次，让新的宽度生效，要不然文字不会居中
				measure(tabView);
			}
			
			/* 将TabsLayout的宽度设为同SlidingTabStrip一样 */
			setMeasuredWidth(getTabsLayout(), r-l);
			ViewGroup.LayoutParams layoutParams =  getTabsLayout().getLayoutParams();
			layoutParams.width = getTabsLayout().getMeasuredWidth();
			getTabsLayout().setLayoutParams(layoutParams);
		}
		super.onLayout(changed, l, t, r, b);
	}
	
	private void setMeasuredWidth(View view, int measuredWidth){
		try {
			Field mMeasuredWidthField = getField(view.getClass(), "mMeasuredWidth", true, true);
			mMeasuredWidthField.setAccessible(true);
			mMeasuredWidthField.set(view, measuredWidth);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onPageSelected(int position) {
		if(position > -1 && getTabsLayout() != null && position < getTabsLayout().getChildCount()){
			//切换选中状态
			if(currentSelectedTabView != null){
				currentSelectedTabView.setSelected(false);
			}
			currentSelectedTabView = getTabsLayout().getChildAt(position);
			currentSelectedTabView.setSelected(true);
			
			//处理滑动
			smoothScrollTo((currentSelectedTabView.getLeft() + currentSelectedTabView.getRight())/2 - centerPosition, currentSelectedTabView.getTop());
		}
		
		if(onPageChangeListener != null){
			onPageChangeListener.onPageSelected(position);
		}
	}
	
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if(onPageChangeListener != null){
			onPageChangeListener.onPageScrolled(arg0, arg1, arg2);
		}
	}
	
	@Override
	public void onPageScrollStateChanged(int arg0) {
		if(onPageChangeListener != null){
			onPageChangeListener.onPageScrollStateChanged(arg0);
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
	 * 获取Page切换监听器
	 * @return Page切换监听器
	 */
	public OnPageChangeListener getOnPageChangeListener() {
		return onPageChangeListener;
	}

	/**
	 * 设置Page切换监听器
	 * @param onPageChangeListener Page切换监听器
	 */
	public void setOnPageChangeListener(OnPageChangeListener onPageChangeListener) {
		this.onPageChangeListener = onPageChangeListener;
	}
	
	/**
	 * 执行测量，执行完成之后只需调用View的getMeasuredXXX()方法即可获取测量结果
	 * @param view
	 * @return
	 */
	private static final View measure(View view){
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
	
	public static Field getField(Class<?> sourceClass, String fieldName, boolean isFindDeclaredField, boolean isUpwardFind){
		Field field = null;
		try {
			field = isFindDeclaredField ? sourceClass.getDeclaredField(fieldName) : sourceClass.getField(fieldName);
		} catch (NoSuchFieldException e1) {
			if(isUpwardFind){
				Class<?> classs = sourceClass.getSuperclass();
				while(field == null && classs != null){
					try {
						field = isFindDeclaredField ? classs.getDeclaredField(fieldName) : classs.getField(fieldName);
					} catch (NoSuchFieldException e11) {
						classs = classs.getSuperclass();
					}
				}
			}
		}
		return field;
	}
}