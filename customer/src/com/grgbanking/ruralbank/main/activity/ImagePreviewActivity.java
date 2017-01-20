package com.grgbanking.ruralbank.main.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.api.ApiHttpClient;
import com.grgbanking.ruralbank.main.adapter.ImageViewPagerAdapter;
import com.grgbanking.ruralbank.main.widget.HackyViewPager;
import com.netease.nim.uikit.common.activity.UI;

import java.util.ArrayList;
import java.util.List;

/**
 * 图片预览界面
 */
public class ImagePreviewActivity extends UI implements
        OnPageChangeListener {

    public static final String BUNDLE_KEY_IMAGES = "bundle_key_images";
    private static final String BUNDLE_KEY_INDEX = "bundle_key_index";
    //private HackyViewPager mViewPager;
    //private SamplePagerAdapter mAdapter;
    private TextView mTvImgIndex;
    private ImageView mIvMore;
    private int mCurrentPostion = 0;
    private String[] mImageUrls;
    private Context mContext;
    ImageViewPagerAdapter adapter;
    HackyViewPager pager;

    public static void showImagePrivew(Context context, int index,
                                       String[] images) {
        Intent intent = new Intent(context, ImagePreviewActivity.class);

        intent.putExtra(BUNDLE_KEY_IMAGES, images);
        intent.putExtra(BUNDLE_KEY_INDEX, index);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);
        mContext = this;

        mImageUrls = getIntent().getStringArrayExtra(BUNDLE_KEY_IMAGES);
        int index = getIntent().getIntExtra(BUNDLE_KEY_INDEX, 0);

        mTvImgIndex = (TextView) findViewById(R.id.tv_img_index);
        pager = (HackyViewPager) findViewById(R.id.view_pager);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < mImageUrls.length; i++) {
            list.add(String.format(ApiHttpClient.API_URL_IMG, mImageUrls[i]));
        }
        adapter = new ImageViewPagerAdapter(getSupportFragmentManager(), list);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
        pager.setCurrentItem(index);
        onPageSelected(index);
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    @Override
    public void onPageSelected(int idx) {
        mCurrentPostion = idx;
        if (mImageUrls != null && mImageUrls.length > 1) {
            if (mTvImgIndex != null) {
                mTvImgIndex.setText((mCurrentPostion + 1) + "/"
                        + mImageUrls.length);
            }
        }
    }

}
