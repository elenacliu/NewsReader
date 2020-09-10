package com.java.renyi;

import android.view.View;
import android.widget.TextView;

import java.util.List;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

/**
 * TODO: 如果二级标签下没有孩子节点，那么就不添加该分支了
 */
public class SecondLevelNodeViewBinder extends BaseNodeViewBinder {

    private TextView tvSecond;

    public SecondLevelNodeViewBinder(View itemView) {
        super(itemView);
        tvSecond = itemView.findViewById(R.id.tv_second_title);
    }

    @Override
    public int getLayoutId() {
        return R.layout.entity_item_second;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        if (treeNode.getValue() instanceof String) {
            tvSecond.setText((String) treeNode.getValue());
        }
    }
}
