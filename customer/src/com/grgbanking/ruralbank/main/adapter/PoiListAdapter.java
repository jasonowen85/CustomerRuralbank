package com.grgbanking.ruralbank.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.grgbanking.ruralbank.R;

import java.util.List;

/**
 * Created by LiuPeng on 2016/8/4.
 * 附近的写字楼
 */
public class PoiListAdapter extends BaseAdapter{
    private Context context;
    private int position = 0;
    Holder hold;
    private List<PoiInfo> lists;

    public PoiListAdapter(Context context, List<PoiInfo> lists) {
        this.context = context;
        this.lists = lists;
    }

    public int getCount() {
        return lists.size();
    }

    public Object getItem(int position) {
        return lists.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int arg0, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(context, R.layout.item_classify_morelist, null);
            hold = new Holder(view);
            view.setTag(hold);
        } else {
            hold = (Holder) view.getTag();
        }
        hold.txt.setText(lists.get(arg0).name.toString());
        hold.txt.setTextColor(0xFF666666);
        if (arg0 == position) {
            hold.txt.setTextColor(0xFFFF8C00);
        }
        return view;
    }

    public void setSelectItem(int position) {
        this.position = position;
    }

    private static class Holder {
        TextView txt;

        public Holder(View view) {
            txt = (TextView) view.findViewById(R.id.moreitem_txt);
        }
    }
}
