package test;

import java.util.ArrayList;
import java.util.List;

import me.xiaopan.android.slidingtabstrip.R;
import me.xiaopan.android.slidingtabstrip.SlidingTabStrip;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 带滑动标题的ViewPager
 */
public class SlidingTabStripActivity extends Activity {
	private SlidingTabStrip moreSlidingTabStrip;
	private SlidingTabStrip shaoSlidingTabStrip;
	private ViewPager moreViewPager;
	private ViewPager shaoViewPager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sliding_tab_strip);
		moreSlidingTabStrip = (SlidingTabStrip) findViewById(R.id.slidingTabStrip_more);
		shaoSlidingTabStrip = (SlidingTabStrip) findViewById(R.id.slidingTabStrip_shao);
		moreViewPager = (ViewPager) findViewById(R.id.viewPager_more);
		shaoViewPager = (ViewPager) findViewById(R.id.viewPager_shao);
		
		/* 初始化标题 */
		List<View> tabs = new ArrayList<View>();
		tabs.add(createTitle("游戏娱乐"));
		tabs.add(createTitle("工具"));
		tabs.add(createTitle("天涯海角"));
		tabs.add(createTitle("学习"));
		tabs.add(createTitle("美化"));
		tabs.add(createTitle("图书"));
		tabs.add(createTitle("体育"));
		tabs.add(createTitle("机械化"));
		tabs.add(createTitle("医学"));
		tabs.add(createTitle("艺术"));
		tabs.add(createTitle("地理"));
		moreSlidingTabStrip.addTabs(tabs);
		
		/* 初始化内容 */
		List<View> views = new ArrayList<View>();
		views.add(getContentView(Colors.SKYBLUE));
		views.add(getContentView(Colors.CHOCOLATE));
		views.add(getContentView(Colors.CYAN));
		views.add(getContentView(Colors.FUCHSIA));
		views.add(getContentView(Colors.GOLD));
		views.add(getContentView(Colors.BLUE));
		views.add(getContentView(Colors.GRAY));
		views.add(getContentView(Colors.GREEN));
		views.add(getContentView(Colors.RED));
		views.add(getContentView(Colors.YELLOW));
		views.add(getContentView(Colors.GRAY));
		moreSlidingTabStrip.setViewPager(moreViewPager);
		moreViewPager.setAdapter(new ViewPagerAdapter(views));
		
		/* 初始化标题 */
		List<View> tabs2 = new ArrayList<View>();
		tabs2.add(createTitle("游戏娱乐"));
		tabs2.add(createTitle("工具"));
		tabs2.add(createTitle("天涯海角"));
		tabs2.add(createTitle("学习"));
		shaoSlidingTabStrip.addTabs(tabs2);
		
		/* 初始化内容 */
		List<View> views2 = new ArrayList<View>();
		views2.add(getContentView(Colors.SKYBLUE));
		views2.add(getContentView(Colors.CHOCOLATE));
		views2.add(getContentView(Colors.CYAN));
		views2.add(getContentView(Colors.FUCHSIA));
		shaoViewPager.setAdapter(new ViewPagerAdapter(views2));
		shaoSlidingTabStrip.setViewPager(shaoViewPager);
	}

	private TextView createTitle(String title){
		TextView textView = (TextView) LayoutInflater.from(getBaseContext()).inflate(R.layout.text_slide_title, null);
		textView.setText(title);
		textView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
		return textView;
	}
	
	private View getContentView(int color){
		View view = new View(getBaseContext());
		view.setBackgroundColor(color);
		return view;
	}
}