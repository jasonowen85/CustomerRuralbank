package com.grgbanking.ruralbank.main.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.grgbanking.ruralbank.R;
import com.grgbanking.ruralbank.main.model.SupplierBean.AddressEntity.SupListEntity;
import com.netease.nim.uikit.common.ui.imageview.HeadImageView;

import java.util.List;

/**
 * 供应商二级列表
 */
public class MyExpandableListViewAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<SupListEntity> group_list;

    public MyExpandableListViewAdapter(Context context,List<SupListEntity> group_list)
    {
        this.context = context;
        this.group_list = group_list;
    }

    /**
     *
     * 获取组的个数
     */
    @Override
    public int getGroupCount()
    {
        return group_list.size();
    }

    /**
     *
     * 获取指定组中的子元素个数
     */
    @Override
    public int getChildrenCount(int groupPosition)
    {
        return group_list.get(groupPosition).getCustomerList().size() + 1;
    }

    /**
     *
     * 获取指定组中的数据
     */
    @Override
    public Object getGroup(int groupPosition)
    {
        return group_list.get(groupPosition);
    }

    /**
     * 获取指定组中的指定子元素数据。
     */
    @Override
    public Object getChild(int groupPosition, int childPosition)
    {
        //需要修改地方
        return group_list.get(groupPosition).getCustomerList().get(childPosition-1).getPhone();
    }

    /**
     *
     * 获取指定组的ID，这个组ID必须是唯一的
     */
    @Override
    public long getGroupId(int groupPosition)
    {
        return groupPosition;
    }

    /**
     *
     * 获取指定组中的指定子元素ID
     */
    @Override
    public long getChildId(int groupPosition, int childPosition)
    {
        return childPosition;
    }

    /**
     *
     * 组和子元素是否持有稳定的ID,也就是底层数据的改变不会影响到它们。
     */
    @Override
    public boolean hasStableIds()
    {
        return true;
    }

    /**
     *
     * 获取显示指定组的视图对象
     *
     * @param groupPosition 组位置
     * @param isExpanded 该组是展开状态还是伸缩状态
     * @param convertView 重用已有的视图对象
     * @param parent 返回的视图对象始终依附于的视图组
     * @see android.widget.ExpandableListAdapter#getGroupView(int, boolean, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)
    {
        GroupHolder groupHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_classify_morelist, null);
            groupHolder = new GroupHolder();
            groupHolder.txt = (TextView)convertView.findViewById(R.id.moreitem_txt);
            groupHolder.img = (ImageView) convertView.findViewById(R.id.moreitem_img);
            convertView.setTag(groupHolder);
        }
        else
        {
            groupHolder = (GroupHolder)convertView.getTag();
        }

        if (!isExpanded)
        {
            groupHolder.img.setBackgroundResource(R.drawable.jiantou_right);
        }
        else
        {
            groupHolder.img.setBackgroundResource(R.drawable.jiantou_down);
        }

        groupHolder.txt.setText(group_list.get(groupPosition).getName());
        return convertView;
    }

    /**
     *
     * 获取一个视图对象，显示指定组中的指定子元素数据。
     *
     * @param groupPosition 组位置
     * @param childPosition 子元素位置
     * @param isLastChild 子元素是否处于组中的最后一个
     * @param convertView 重用已有的视图(View)对象
     * @param parent 返回的视图(View)对象始终依附于的视图组
     * @see android.widget.ExpandableListAdapter#getChildView(int, int, boolean, android.view.View,
     *      android.view.ViewGroup)
     */
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent)
    {
        ItemHolder itemHolder;
        if (convertView == null)
        {
            convertView = LayoutInflater.from(context).inflate(R.layout.forward_list, null);
            itemHolder = new ItemHolder();
            itemHolder.txt = (TextView)convertView.findViewById(R.id.tv_name);
            itemHolder.num = (TextView)convertView.findViewById(R.id.content);
            itemHolder.iv_head_portrait = (HeadImageView) convertView.findViewById(R.id.iv_head_portrait);
            convertView.setTag(itemHolder);
        }
        else
        {
            itemHolder = (ItemHolder)convertView.getTag();
        }
        if(childPosition==0){
            itemHolder.iv_head_portrait.setVisibility(View.GONE);
            //itemHolder.txt.setText("地址: " + group_list.get(groupPosition).getAddress());
            itemHolder.txt.setText(String.format(context.getResources().getString(R.string.address),group_list.get(groupPosition).getAddress()));
            itemHolder.num.setTextColor(context.getResources().getColor(R.color.color_black_ff666666));
            //itemHolder.num.setText("电话: "+group_list.get(groupPosition).getTell());
            itemHolder.num.setText(String.format(context.getResources().getString(R.string.supplier_phone),group_list.get(groupPosition).getTell()));
        }else{
            itemHolder.iv_head_portrait.setVisibility(View.VISIBLE);
            itemHolder.txt.setText(group_list.get(groupPosition).getCustomerList().get(childPosition-1).getName());
            //itemHolder.num.setText(String.format(context.getResources().getString(R.string.supplier_job_num),group_list.get(groupPosition).getCustomerList().get(childPosition-1).getJobOrderNum()));
            itemHolder.num.setTextColor(context.getResources().getColor(R.color.gray));
            itemHolder.num.setText(Html.fromHtml(String.format(context.getResources().getString(R.string.supplier_job_num),group_list.get(groupPosition).getCustomerList().get(childPosition-1).getJobOrderNum())));
        }
        return convertView;
    }

    /**
     *
     * 是否选中指定位置上的子元素。
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition)
    {
        return true;
    }

    class GroupHolder
    {
        public TextView txt;

        public ImageView img;
    }

    class ItemHolder
    {
        public TextView num;

        public TextView txt;

        public HeadImageView iv_head_portrait;
    }
}
