/*
 * Copyright (c) 2015 [1076559197@qq.com | tchen0707@gmail.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License”);
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.grgbanking.ruralbank.main.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.grgbanking.ruralbank.R;
import com.netease.nim.uikit.common.activity.UI;


public class ImagesDetailActivity extends UI {

    public static final String INTENT_IMAGE_URL_TAG = "INTENT_IMAGE_URL_TAG";

    private String mImageUrl;
    private ProgressBar progressBar;
    ImageView mSmoothImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images_detail);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        Bundle extras = getIntent().getExtras();
        if (null != extras) {
            getBundleExtras(extras);
        }
        initViewsAndEvents();
    }

    protected void getBundleExtras(Bundle extras) {
        mImageUrl = extras.getString(INTENT_IMAGE_URL_TAG);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    protected void initViewsAndEvents() {
        mSmoothImageView = (ImageView) findViewById(R.id.images_detail_smooth_image);
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
        Glide.with(ImagesDetailActivity.this)
                .load(mImageUrl)
                .into(new GlideDrawableImageViewTarget(mSmoothImageView) {
                    @Override
                    public void onResourceReady(GlideDrawable drawable, GlideAnimation anim) {
                        super.onResourceReady(drawable, anim);
                        mSmoothImageView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
//            }
//        });

    }
}
