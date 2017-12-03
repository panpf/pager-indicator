package me.panpf.pagerid.sample;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

import me.panpf.pagerid.PagerIndicator;

public class MainActivity extends AppCompatActivity {
    private static final int[] colors = new int[]{Colors.SKYBLUE, Colors.CHOCOLATE, Colors.CYAN, Colors.FUCHSIA, Colors.GOLD, Colors.BLUE, Colors.GREEN, Colors.RED, Colors.YELLOW, Colors.GRAY};

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sliding_tab_strip);

        PagerIndicator pagerIndicator1 = (PagerIndicator) findViewById(R.id.slidingTabStrip_1);
        PagerIndicator pagerIndicator2 = (PagerIndicator) findViewById(R.id.slidingTabStrip_2);
        PagerIndicator pagerIndicator3 = (PagerIndicator) findViewById(R.id.slidingTabStrip_3);
        PagerIndicator pagerIndicator4 = (PagerIndicator) findViewById(R.id.slidingTabStrip_4);
        ViewPager viewPager1 = (ViewPager) findViewById(R.id.viewPager_1);
        ViewPager viewPager2 = (ViewPager) findViewById(R.id.viewPager_2);
        ViewPager viewPager3 = (ViewPager) findViewById(R.id.viewPager_3);
        ViewPager viewPager4 = (ViewPager) findViewById(R.id.viewPager_4);

        pagerIndicator3.setTabViewFactory(new PagerIndicator.TabViewFactory() {
            @Override
            public void addTabs(ViewGroup parent, int currentItemPosition) {
                parent.removeAllViews();

                TextView textView1 = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.tab, parent, false);
                textView1.setText("详情");
                parent.addView(textView1);

                TextView textView2 = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.tab, parent, false);
                textView2.setText("评论");
                parent.addView(textView2);

                TextView textView3 = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.tab, parent, false);
                textView3.setText("攻略");
                parent.addView(textView3);
            }
        });

        pagerIndicator4.setTabViewFactory(new PagerIndicator.TabViewFactory() {
            @Override
            public void addTabs(ViewGroup parent, int currentItemPosition) {
                parent.removeAllViews();

                TextView textView1 = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.tab, parent, false);
                textView1.setText("聊天");
                parent.addView(textView1);

                TextView textView2 = (TextView) LayoutInflater.from(MainActivity.this).inflate(R.layout.tab, parent, false);
                textView2.setText("发现");
                parent.addView(textView2);
            }
        });

        init(0, pagerIndicator1, viewPager1);
        init(1, pagerIndicator2, viewPager2);
        init(2, pagerIndicator3, viewPager3);
        init(3, pagerIndicator4, viewPager4);
    }

    private void init(int index, PagerIndicator pagerIndicator, ViewPager viewPager) {
        int length = pagerIndicator.getTabCount();
        List<View> views = new ArrayList<View>(length);
        Random random = new Random();
        for (int w = 0; w < length; w++) {
            views.add(getContentView(colors[Math.abs(random.nextInt()) % colors.length]));
        }
        viewPager.setAdapter(new ViewPagerAdapter(views));
        viewPager.setCurrentItem(index < length ? index : length);
        pagerIndicator.setViewPager(viewPager);

        pagerIndicator.setOnClickTabListener(new PagerIndicator.OnClickTabListener() {
            @Override
            public void onClickTab(View tab, int index) {
                Toast.makeText(getBaseContext(), "点了第" + (index + 1) + "个TAB", Toast.LENGTH_SHORT).show();
            }
        });

        pagerIndicator.setOnDoubleClickTabListener(new PagerIndicator.OnDoubleClickTabListener() {
            @Override
            public void onDoubleClickTab(View view, int index) {
                Toast.makeText(getBaseContext(), "双击了第" + (index + 1) + "个TAB", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private View getContentView(int color) {
        View view = new View(getBaseContext());
        view.setBackgroundColor(color);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint({"NewApi", "AlwaysShowAction"})
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("GitHub");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://github.com/panpf/pager-indicator"));
        startActivity(intent);
        return super.onOptionsItemSelected(item);
    }
}