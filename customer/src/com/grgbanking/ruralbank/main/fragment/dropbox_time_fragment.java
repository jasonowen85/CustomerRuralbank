package com.grgbanking.ruralbank.main.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.common.util.widget.DoubleDatePickerDialog;

import java.util.Calendar;

public class dropbox_time_fragment extends BaseFragment implements OnClickListener {
    private TextView tv_time;
    private ImageView iv_time;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_dropbox_time, container, false);
        init(rootView);
        return rootView;
    }

    private void init(View rootView) {

        tv_time = (TextView) rootView.findViewById(R.id.tv_time);
        tv_time.setOnClickListener(this);
        iv_time = (ImageView) rootView.findViewById(R.id.iv_time);
        iv_time.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Calendar c = Calendar.getInstance();
        if (getActivity() != null) {
            // 最后一个false表示不显示日期，如果要显示日期，最后参数可以是true或者不用输入
            new DoubleDatePickerDialog(getActivity(), 0, new DoubleDatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker startDatePicker, int startYear, int startMonthOfYear,
                                      int startDayOfMonth, DatePicker endDatePicker, int endYear, int endMonthOfYear,
                                      int endDayOfMonth) {
                    String startTime = String.format("%d-%d-%d", startYear, startMonthOfYear + 1, startDayOfMonth);
                    String endTime = String.format("%d-%d-%d", endYear, endMonthOfYear + 1, endDayOfMonth);
                    Intent intent = new Intent();
                    intent.setAction("ACTION_NAME");
                    intent.putExtra("startTime", startTime);
                    intent.putExtra("endTime", endTime);
                    getActivity().sendBroadcast(intent);//发送广播
                }
            }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DATE), true).show();
        }
    }
}
