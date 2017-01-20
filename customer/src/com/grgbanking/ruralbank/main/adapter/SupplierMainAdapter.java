package com.grgbanking.ruralbank.main.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.main.model.SupplierBean;

import java.util.List;

/**
 * Created by LiuPeng on 2016/8/4.
 * 供应商一级列表
 */
public class SupplierMainAdapter extends BaseAdapter{
    private Context context;
    private List<SupplierBean.AddressEntity> list;
    private int position = 0;
    private Holder hold;

    public SupplierMainAdapter(Context context, List<SupplierBean.AddressEntity> list) {
        this.context = context;
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    public Object getItem(int position) {
        return list.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int arg0, View view, ViewGroup viewGroup) {

        if (view == null) {
            view = View.inflate(context, R.layout.item_classify_mainlist, null);
            hold = new Holder(view);
            view.setTag(hold);
        } else {
            hold = (Holder) view.getTag();
        }
        //hold.img.setImageResource(Integer.parseInt(list.get(arg0).getCustId()));
        hold.txt.setText(list.get(arg0).getName());
        hold.txt.setTextColor(context.getResources().getColor(R.color.white));
        hold.layout.setBackgroundColor(context.getResources().getColor(R.color.color_blue_00acee));
        if (arg0 == position) {
            hold.layout.setBackgroundColor(0xFFFFFFFF);
            hold.txt.setTextColor(context.getResources().getColor(R.color.color_blue_06b3ec));
        }
        return view;
    }

    public void setSelectItem(int position) {
        this.position = position;
    }

    public int getSelectItem() {
        return position;
    }

    private static class Holder {
        LinearLayout layout;
        //ImageView img;
        TextView txt;

        public Holder(View view) {
            txt = (TextView) view.findViewById(R.id.mainitem_txt);
            //img = (ImageView) view.findViewById(R.id.mainitem_img);
            layout = (LinearLayout) view.findViewById(R.id.mainitem_layout);
        }
    }
}
