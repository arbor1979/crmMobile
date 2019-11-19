package com.yujieshipin.crm.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.yujieshipin.crm.R;
import com.yujieshipin.crm.util.BadgeView;


/**
 * Created by lyd10892 on 2016/8/23.
 */

public class DescHolder extends RecyclerView.ViewHolder {
    public TextView descView;
    public ImageView itemIcon;
    public BadgeView badge;

    public DescHolder(View itemView) {
        super(itemView);
        initView();
    }

    private void initView() {
        descView = (TextView) itemView.findViewById(R.id.tv_school_work_name);
        itemIcon = (ImageView) itemView.findViewById(R.id.ib_school_work_pic);
    }
}
