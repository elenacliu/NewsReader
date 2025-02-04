package com.java.renyi;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.Gravity;
import android.view.View;
import android.widget.GridLayout;
import android.widget.TextView;

import java.util.List;

/**
 * ============================================================
 * Copyright：${TODO}有限公司版权所有 (c) 2017
 * Author：   AllenIverson
 * Email：    815712739@qq.com
 * GitHub：   https://github.com/JackChen1999
 * 博客：     http://blog.csdn.net/axi295309066
 * 微博：     AndroidDeveloper
 * GitBook：  https://www.gitbook.com/@alleniverson
 * <p>
 * Project_Name：DragGridLayout
 * Package_Name：com.github.draggridlayout
 * Version：1.0
 * time：2017/3/26 9:26
 * des ：1.定义出一个setItems，动态地将每个频道的标题数据集合传递进来之后，按照数据进行展示即可
 *       2.定义出一个setAllowDrag方法，控制控件是否可以进行拖拽操作
 * gitVersion：2.12.0.windows.1
 * updateAuthor：$Author$
 * updateDate：$Date$
 * updateDes：${TODO}
 * ============================================================
 */

public class DragGridLayout extends GridLayout{

    private  static final int columnCount = 4;//列数
    private boolean isAllowDrag;//记录当前控件是否可以进行拖拽操作

    public DragGridLayout(Context context) {
        this(context,null);
    }

    public DragGridLayout(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DragGridLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

/*  static SparseArray<String> dragEventType = new SparseArray<>();
    static{
        dragEventType.put(DragEvent.ACTION_DRAG_STARTED, "STARTED");
        dragEventType.put(DragEvent.ACTION_DRAG_ENDED, "ENDED");
        dragEventType.put(DragEvent.ACTION_DRAG_ENTERED, "ENTERED");
        dragEventType.put(DragEvent.ACTION_DRAG_EXITED, "EXITED");
        dragEventType.put(DragEvent.ACTION_DRAG_LOCATION, "LOCATION");
        dragEventType.put(DragEvent.ACTION_DROP, "DROP");
    }
    public static String getDragEventAction(DragEvent de){
        return dragEventType.get(de.getAction());
    }*/

    //初始化方法
    private void init() {
        //  android:columnCount="4"
        //  android:animateLayoutChanges="true"
        this.setColumnCount(columnCount);
        this.setLayoutTransition(new LayoutTransition());
    }

    public void setItems(List<String> items) {
        for (String item : items) {
            addItem(item);
        }
    }

    public void addItem(String content, int index) {
        TextView tv = newItemView();
        tv.setText(content);
        addView(tv,index);
    }

    public void addItem(String content) {
        TextView tv = newItemView();
        tv.setText(content);
        addView(tv);
    }

    private TextView newItemView() {
        TextView tv = new TextView(getContext());
        int margin = dip2px(5);
        tv.setTextSize(18);
        tv.setBackgroundResource(R.drawable.selector_tv_bg);
        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();
        layoutParams.width = getResources().getDisplayMetrics().widthPixels/4 - 2*margin;//宽为屏幕宽的4分之一
        layoutParams.height = dip2px(30);
        layoutParams.setMargins(margin,margin,margin,margin);
        tv.setGravity(Gravity.CENTER);
        tv.setLayoutParams(layoutParams);

        if (isAllowDrag) {
            //给条目设置长按点击事件
            tv.setOnLongClickListener(mLongClickListener);
        } else {
            tv.setOnLongClickListener(null);
        }

        //设置条目的点击事件
        tv.setOnClickListener(onClickListener);
        return tv;
    }

    /** dip转换px */
    public int dip2px(int dip) {
        final float scale = getResources().getDisplayMetrics().density;
        return (int) (dip * scale + 0.5f);
    }

    private View dragedView;//被拖拽的视图

    private View.OnLongClickListener mLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            //长按时，开始拖拽操作，显示出阴影
            //被拖拽的视图其实就是v参数
            dragedView = v;
            v.startDrag(null, new View.DragShadowBuilder(v), null, 0);
            v.setEnabled(false);
            //v.startDragAndDrop(null, new View.DragShadowBuilder(v), null, 0); // api24
            return true;
        }
    };

    private OnClickListener onClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if(onDragItemClickListener != null){
                onDragItemClickListener.onDragItemClick((TextView) v);
            }
        }
    };


    public void setAllowDrag(boolean isAllowDrag) {
        this.isAllowDrag = isAllowDrag;
        if (this.isAllowDrag) {
            this.setOnDragListener(mDragListener);
        } else {
            this.setOnDragListener(null);
        }
    }

    private View.OnDragListener mDragListener =  new View.OnDragListener() {
        /**
         * ACTION_DRAG_STARTED:当拖拽操作执行时，就会执行一次
         * DragEvent.ACTION_DRAG_ENDED：当拖拽事件结束，手指抬起时，就是执行一次
         * DragEvent.ACTION_DRAG_ENTERED：当手指进入设置了拖拽监听的控件范围内的瞬间执行一次
         * DragEvent.ACTION_DRAG_EXITED：当手指离开设置了拖拽监听的控件范围内的瞬间执行一次
         * DragEvent.ACTION_DRAG_LOCATION：当手指在设置了拖拽监听的控件范围内，移动时，实时会执行，执行N次
         * DragEvent.ACTION_DROP：当手指在设置了拖拽监听的控件范围内松开时，执行一次
         *
         * @param v 当前监听拖拽事件的view(其实就是mGridLayout)
         * @param event 拖拽事件
         * @return
         */
        @Override
        public boolean onDrag(View v, DragEvent event) {
            switch (event.getAction()) {
                //当拖拽事件开始时，创建出与子控件对应的矩形数组
                case DragEvent.ACTION_DRAG_STARTED:
                    initRects();
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    //手指移动时，实时判断触摸是否进入了某一个子控件
                    int touchIndex = getTouchIndex(event);
                    //说明触摸点进入了某一个子控件,判断被拖拽的视图与进入的子控件对象不是同一个的时候才进行删除添加操作

                    if (touchIndex > -1 && dragedView != null && dragedView != DragGridLayout.this.getChildAt(touchIndex)) {
                        DragGridLayout.this.removeView(dragedView);
                        DragGridLayout.this.addView(dragedView,touchIndex);
                    }
                    break;
                case DragEvent.ACTION_DRAG_ENDED:
                    //拖拽事件结束后，让被拖拽的view设置为可用，否则背景变红，并且长按事件会失效
                    if (dragedView != null) {
                        dragedView.setEnabled(true);
                    }
                    break;
            }

            return true;
        }
    };

    //手指移动时，实时判断触摸是否进入了某一个子控件
    private int getTouchIndex(DragEvent event) {
        //遍历所有的数组，如果包含了当前的触摸点返回索引即可
        for (int i = 0; i < mRects.length; i++) {
            Rect rect = mRects[i];
            if (rect.contains((int)event.getX(), (int)event.getY())) {
                return i;
            }
        }
        return -1;
    }


    //当拖拽事件开始时，创建出与子控件对应的矩形数组
    private Rect[] mRects;

    private void initRects() {
        mRects = new Rect[this.getChildCount()];
        for (int i = 0; i < this.getChildCount(); i++) {
            View childView = this.getChildAt(i);
            //创建与每个子控件对应矩形对象
            Rect rect = new Rect(childView.getLeft(), childView.getTop(), childView.getRight(), childView.getBottom());
            mRects[i] = rect;
        }
    }

    private OnDragItemClickListener onDragItemClickListener;

    public interface OnDragItemClickListener{
        public void onDragItemClick(TextView tv);
    }

    public void setOnDragItemClickListener(OnDragItemClickListener onDragItemClickListener) {
        this.onDragItemClickListener = onDragItemClickListener;
    }
}