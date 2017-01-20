package com.grgbanking.ruralbank.main.fragment;

import android.support.v4.app.Fragment;
import android.support.v4.util.ArrayMap;
import android.util.Log;

/**
 * fragment工厂
 */
public class OrderFragmentFactory {

    private static final String TAG = OrderFragmentFactory.class.getSimpleName();
    private static ArrayMap<Integer, Fragment> fragmentArrayMap = new ArrayMap<>();

    public static Fragment createFragment(int position) {
        Fragment fragment;
        fragment = fragmentArrayMap.get(position);
        if (fragment == null) {

            Log.e(TAG, "createFragment " + "Fragment为null执行");
            
            if (position == 0) {
                fragment = new OrderStateFragment();
            } else if (position == 1) {
                fragment = new OrderDetailsFragment();
            }
            if (fragment != null) {
                fragmentArrayMap.put(position, fragment);
            }
        }
        return fragment;
    }
}
