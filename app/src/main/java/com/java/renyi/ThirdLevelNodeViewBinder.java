package com.java.renyi;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

import me.texy.treeview.TreeNode;
import me.texy.treeview.base.BaseNodeViewBinder;


/**
 * 问题：如何区分？在node添加额外信息区分
 */
public class ThirdLevelNodeViewBinder extends BaseNodeViewBinder {

    /**
     * 第三层内可能的内容
     */
    private TextView tv_intro;
    private ImageView iv_img;

    private TextView tv_relation_type, tv_relation_entity;
    private ImageView iv_relation;

    private TextView tv_property_name, tv_property_content;

    private CardView cardView1, cardView2, cardView3;

    public ThirdLevelNodeViewBinder(View itemView) {
        super(itemView);

        tv_intro = itemView.findViewById(R.id.tv_intro);
        iv_img = itemView.findViewById(R.id.iv_img);

        tv_relation_type = itemView.findViewById(R.id.tv_relation_type);
        tv_relation_entity = itemView.findViewById(R.id.tv_relation_entity);
        iv_relation = itemView.findViewById(R.id.iv_relation);          // set src

        tv_property_name = itemView.findViewById(R.id.tv_property_name);
        tv_property_content = itemView.findViewById(R.id.tv_property_content);

        // viewgroup设为不可见是否可行？可行
        cardView1 = itemView.findViewById(R.id.entity_item_third_descrip);
        cardView2 = itemView.findViewById(R.id.entity_item_third_relation);
        cardView3 = itemView.findViewById(R.id.entity_item_third_property);
    }

    @Override
    public int getLayoutId() {
        return R.layout.entity_item_third;
    }

    @Override
    public void bindView(TreeNode treeNode) {
        Object value = treeNode.getValue();
        // descrip
        if (value instanceof List) {
            // 事实上，如果能有这样的TreeNode, 则一定有一个元素不为空
            if (((List)value).size() == 2) {
                cardView1.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.GONE);
                cardView3.setVisibility(View.GONE);

                String intro = (String) ((List)value).get(0);
                if (intro != null)
                    tv_intro.setText(intro);

                String img = (String) ((List)value).get(1);
                if (img != null) {
                    // 有就显示
                    Glide.with(iv_img.getContext()).load(img).into(iv_img);
                }
                else {
                    // 没有就不显示
                    iv_img.setVisibility(View.GONE);
                }
            }
            // 手动添加第三个元素
            else {
                cardView3.setVisibility(View.VISIBLE);
                cardView2.setVisibility(View.GONE);
                cardView1.setVisibility(View.GONE);

                String propertyName = (String) ((List)value).get(0);
                String propertyContent = (String) ((List)value).get(1);
                if (propertyName != null)
                    tv_property_name.setText(propertyName);
                if (propertyContent != null)
                    tv_property_content.setText(propertyContent);
            }
        }
        // No! 对于每个item，还是一个单独的Hashmap
        else if (value instanceof HashMap) {
            cardView2.setVisibility(View.VISIBLE);
            cardView1.setVisibility(View.GONE);
            cardView3.setVisibility(View.GONE);

            String relation = (String) ((HashMap)value).get("relation");
            String relationLabel = (String) ((HashMap)value).get("label");
            String forward = (String) ((HashMap)value).get("forward");
            if (relation != null)
                tv_relation_type.setText(relation);
            if (relationLabel != null)
                tv_relation_entity.setText(relationLabel);


            if (forward != null && forward.equals("false")) {
                iv_relation.setImageResource(R.drawable.ic_from);
            }
            else {
                iv_relation.setImageResource(R.drawable.ic_to);
            }
        }

    }
}
