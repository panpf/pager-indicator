package me.xiaopan.pstssample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.xiaopan.psts.PagerSlidingTabStrip;

/**
 * 带滑动标题的ViewPager
 */
public class SlidingTabStripActivity extends Activity {
    private static final int[] colors = new int[]{Colors.SKYBLUE, Colors.CHOCOLATE, Colors.CYAN, Colors.FUCHSIA, Colors.GOLD, Colors.BLUE, Colors.GREEN, Colors.RED, Colors.YELLOW, Colors.GRAY};

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sliding_tab_strip);

        PagerSlidingTabStrip pagerSlidingTabStrip1 = (PagerSlidingTabStrip) findViewById(R.id.slidingTabStrip_1);
        PagerSlidingTabStrip pagerSlidingTabStrip2 = (PagerSlidingTabStrip) findViewById(R.id.slidingTabStrip_2);
        PagerSlidingTabStrip pagerSlidingTabStrip3 = (PagerSlidingTabStrip) findViewById(R.id.slidingTabStrip_3);
        PagerSlidingTabStrip pagerSlidingTabStrip4 = (PagerSlidingTabStrip) findViewById(R.id.slidingTabStrip_4);
        ViewPager viewPager1 = (ViewPager) findViewById(R.id.viewPager_1);
        ViewPager viewPager2 = (ViewPager) findViewById(R.id.viewPager_2);
        ViewPager viewPager3 = (ViewPager) findViewById(R.id.viewPager_3);
        ViewPager viewPager4 = (ViewPager) findViewById(R.id.viewPager_4);

        pagerSlidingTabStrip3.setTabViewFactory(new PagerSlidingTabStrip.TabViewFactory() {
            @Override
            public void addTabs(ViewGroup parent, int currentItemPosition) {
                parent.removeAllViews();

                TextView textView1 = (TextView) LayoutInflater.from(SlidingTabStripActivity.this).inflate(R.layout.tab, parent, false);
                textView1.setText("详情");
                parent.addView(textView1);

                TextView textView2 = (TextView) LayoutInflater.from(SlidingTabStripActivity.this).inflate(R.layout.tab, parent, false);
                textView2.setText("评论");
                parent.addView(textView2);

                TextView textView3 = (TextView) LayoutInflater.from(SlidingTabStripActivity.this).inflate(R.layout.tab, parent, false);
                textView3.setText("攻略");
                parent.addView(textView3);
            }
        });

        pagerSlidingTabStrip4.setTabViewFactory(new PagerSlidingTabStrip.TabViewFactory() {
            @Override
            public void addTabs(ViewGroup parent, int currentItemPosition) {
                parent.removeAllViews();

                TextView textView1 = (TextView) LayoutInflater.from(SlidingTabStripActivity.this).inflate(R.layout.tab, parent, false);
                textView1.setText("聊天");
                parent.addView(textView1);

                TextView textView2 = (TextView) LayoutInflater.from(SlidingTabStripActivity.this).inflate(R.layout.tab, parent, false);
                textView2.setText("发现");
                parent.addView(textView2);
            }
        });

        init(0, pagerSlidingTabStrip1, viewPager1);
        init(1, pagerSlidingTabStrip2, viewPager2);
        init(2, pagerSlidingTabStrip3, viewPager3);
        init(3, pagerSlidingTabStrip4, viewPager4);
	}

    private void init(int index, PagerSlidingTabStrip pagerSlidingTabStrip, ViewPager viewPager){
        int length = pagerSlidingTabStrip.getTabCount();
        List<View> views = new ArrayList<View>(length);
        Random random = new Random();
        for(int w = 0; w < length; w++){
            views.add(getContentView(colors[Math.abs(random.nextInt())%colors.length]));
        }
        viewPager.setAdapter(new ViewPagerAdapter(views));
        viewPager.setCurrentItem(index < length ? index : length);
        pagerSlidingTabStrip.setViewPager(viewPager);

        pagerSlidingTabStrip.setOnClickTabListener(new PagerSlidingTabStrip.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {
                Toast.makeText(getBaseContext(), "点了第" + (index + 1) + "个TAB", Toast.LENGTH_SHORT).show();
            }
        });

        pagerSlidingTabStrip.setOnDoubleClickTabListener(new PagerSlidingTabStrip.OnDoubleClickTabListener() {
            @Override
            public void onDoubleClickTab(View view, int index) {
                Toast.makeText(getBaseContext(), "双击了第" + (index + 1) + "个TAB", Toast.LENGTH_SHORT).show();
            }
        });
    }

	private View getContentView(int color){
		View view = new View(getBaseContext());
		view.setBackgroundColor(color);
		return view;
	}

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@SuppressLint({ "NewApi", "AlwaysShowAction" })
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Github");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB){
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("http://github.com/xiaopansky/PagerSlidingTabStrip"));
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}