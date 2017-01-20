package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.grgbanking.ruralbank.DemoCache;
import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.config.preference.Preferences;
import com.grgbanking.ruralbank.login.LoginActivity;
import com.grgbanking.ruralbank.main.adapter.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * @author dwtedx
 *         功能描述：主程序入口类
 */
public class GuideActivity extends AppCompatActivity implements OnClickListener, OnPageChangeListener {
    //定义ViewPager对象
    private ViewPager viewPager;

    private Button btn_welcome_guide;

    //定义ViewPager适配器
    private ViewPagerAdapter vpAdapter;

    //定义一个ArrayList来存放View
    private ArrayList<View> views;

    //引导图片资源
    private static final int[] pics = {R.drawable.guide1, R.drawable.guide2, R.drawable.guide3};

    //底部小点的图片
    private ImageView[] points;

    //记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        if (isFirstEnter(GuideActivity.this,GuideActivity.this.getClass().getName())) {
            initView();
            initData();
        } else if (Preferences.getUserid() == null) {
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
            finish();
        } else {
            startActivity(new Intent(getBaseContext(), MainActivity.class));
            finish();
        }
    }

    //****************************************************************
    // 判断应用是否初次加载，读取SharedPreferences中的guide_activity字段
    //****************************************************************
    private static final String SHAREDPREFERENCES_NAME = "my_pref";
    private static final String KEY_GUIDE_ACTIVITY = "guide_activity";
    private boolean isFirstEnter(Context context, String className){
        if(context==null || className==null||"".equalsIgnoreCase(className))return false;
        String mResultStr = context.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_WORLD_READABLE)
                .getString(KEY_GUIDE_ACTIVITY, "");//取得所有类名 如 com.my.MainActivity
        if(mResultStr.equalsIgnoreCase("false"))
            return false;
        else
            return true;
    }

    private void setGuided(){
        SharedPreferences settings = getSharedPreferences(SHAREDPREFERENCES_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(KEY_GUIDE_ACTIVITY, "false");
        editor.commit();
    }


    /**
     * 初始化组件
     */
    private void initView() {
        //实例化ArrayList对象
        views = new ArrayList<View>();

        //实例化ViewPager
        viewPager = (ViewPager) findViewById(R.id.viewpager);

        //实例化ViewPager适配器
        vpAdapter = new ViewPagerAdapter(views);

        btn_welcome_guide = (Button) findViewById(R.id.btn_welcome_guide);
        btn_welcome_guide.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isFirstEnter(GuideActivity.this,GuideActivity.this.getClass().getName())) {
                    setGuided();
                    startActivity(new Intent(getBaseContext(), Guide2Activity.class));
                    finish();
                } else {
                    setGuided();
                    if (DemoCache.getUserid() == null) {
                        startActivity(new Intent(getBaseContext(), LoginActivity.class));
                        finish();
                    } else {
                        startActivity(new Intent(getBaseContext(), MainActivity.class));
                        finish();
                    }
                }
            }
        });

        //点击图片 也可以切换下一个页面；
        vpAdapter.setNeedPageChanged(new ViewPagerAdapter.OnClickNeedPagerChange() {
            @Override
            public void pageChanged(int position) {
                if(position >= pics.length -1)
                    return;
                setCurView(position + 1);
                setCurDot(position + 1);
            }
        });

}

    /**
     * 初始化数据
     */
    private void initData() {
        //定义一个布局并设置参数
        LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);

        //初始化引导图片列表
        for (int i = 0; i < pics.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setLayoutParams(mParams);
            iv.setScaleType(ImageView.ScaleType.FIT_XY);
            iv.setImageResource(pics[i]);
            views.add(iv);
        }

        //设置数据
        viewPager.setAdapter(vpAdapter);
        //设置监听
        viewPager.setOnPageChangeListener(this);

        //初始化底部小点
        initPoint();
    }

    /**
     * 初始化底部小点
     */
    private void initPoint() {
        LinearLayout linearLayout = (LinearLayout) findViewById(R.id.ll);

        points = new ImageView[pics.length];

        //循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            //得到一个LinearLayout下面的每一个子元素
            points[i] = (ImageView) linearLayout.getChildAt(i);
            //默认都设为灰色
            points[i].setEnabled(true);
            //给每个小点设置监听
            points[i].setOnClickListener(this);
            //设置位置tag，方便取出与当前位置对应
            points[i].setTag(i);
        }

        //设置当面默认的位置
        currentIndex = 0;
        //设置为白色，即选中状态
        points[currentIndex].setEnabled(false);
    }

    /**
     * 当滑动状态改变时调用
     */
    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    /**
     * 当当前页面被滑动时调用
     */

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    /**
     * 当新的页面被选中时调用
     */

    @Override
    public void onPageSelected(int position) {
        //设置底部小点选中状态
        setCurDot(position);
        if (position == 2) {
            btn_welcome_guide.setVisibility(View.VISIBLE);
        } else {
            btn_welcome_guide.setVisibility(View.GONE);
        }
    }

    /**
     * 通过点击事件来切换当前的页面
     */
    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }

    /**
     * 设置当前页面的位置
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    /**
     * 设置当前的小点的位置
     */
    private void setCurDot(int positon) {
        if (positon < 0 || positon > pics.length - 1 || currentIndex == positon) {
            return;
        }
        points[positon].setEnabled(false);
        points[currentIndex].setEnabled(true);

        currentIndex = positon;
    }


}
