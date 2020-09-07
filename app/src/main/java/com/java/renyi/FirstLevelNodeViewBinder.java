package com.java.renyi;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.List;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;

public class FirstLevelNodeViewBinder extends BaseNodeViewBinder {

    private TextView tvLabel;
    private ImageView ivHot1, ivHot2;

    public FirstLevelNodeViewBinder(View itemView) {
        super(itemView);
        tvLabel = itemView.findViewById(R.id.tv_label);
        ivHot1 = itemView.findViewById(R.id.iv_hot1);
        ivHot2 = itemView.findViewById(R.id.iv_hot2);
    }

    /**
     * 实现同一level中的多种布局
     * @return
     */
    @Override
    public int getLayoutId() {
        return R.layout.entity_item_first;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        /**
         * 第一层
         * List<Object>
         * List[0]: text
         * List[1]: hotness
         */
        Object value = treeNode.getValue();
        if (value instanceof List) {
            tvLabel.setText((String)((List)value).get(0));
            float hotness = (Float) ((List)value).get(1);   // 转换是否安全？
            if (hotness >= 0.5) {
                ivHot2.setVisibility(View.VISIBLE);
            }
        }
    }
}
