package com.java.renyi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * 搜索新闻列表界面RecyclerView的适配器
 * 前端实现点击变灰效果并通知后端数据库更改viewed字段
 */
// 参考：https://www.jianshu.com/p/368ac961808d
// 参考：https://blog.csdn.net/zhuchenglin830/article/details/82286109
public class SearchNewsListAdapter extends RecyclerView.Adapter<SearchNewsListAdapter.ViewHolder> {
    private Context context;
    // newsEntity的列表
    private List<Entry> newsEntityList;
    private LayoutInflater layoutInflater;
    // 记录控件是否被点击，默认为当前新闻列表的viewed; 如果被点击就显示为灰色
    private List<Boolean> isClicked;

    public SearchNewsListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.news_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Entry news = newsEntityList.get(position);

        // 如果该新闻被看过
        if (isClicked.get(position)) {
            holder.title.setText(news.title);
            holder.content.setText(news.content);
            holder.time.setText(news.time);
            holder.source.setText(news.source);
            holder.title.setTextColor(context.getColor(R.color.news_item_isViewed));
            holder.content.setTextColor(context.getColor(R.color.news_item_isViewed));
            holder.time.setTextColor(context.getColor(R.color.news_item_isViewed));
            holder.source.setTextColor(context.getColor(R.color.news_item_isViewed));
        }
        else {
            holder.title.setText(news.title);
            holder.content.setText(news.content);
            holder.time.setText(news.time);
            holder.source.setText(news.source);
            holder.title.setTextColor(context.getColor(R.color.news_item_unViewed));
            holder.content.setTextColor(context.getColor(R.color.news_item_unViewed));
            holder.time.setTextColor(context.getColor(R.color.news_item_unViewed));
            holder.source.setTextColor(context.getColor(R.color.news_item_unViewed));
        }

        if (onItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // 将其置为true
                    isClicked.set(position, true);
                    notifyDataSetChanged();
                    onItemClickListener.OnItemClick(view, newsEntityList.get(position));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        if (newsEntityList == null)
            return 0;
        return newsEntityList.size();
    }

    // 每次搜索set函数只会调用一次，因而在这里初始化isClicked数组没有问题
    public void setNewsEntityList(List<Entry> newsEntityList) {
        this.newsEntityList = newsEntityList;
        this.isClicked = new ArrayList<>();
        if (newsEntityList != null)
            for (int i = 0; i < newsEntityList.size(); i++)
                isClicked.add(newsEntityList.get(i).viewed);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView time;
        private TextView source;
        private TextView content;
//        private View itemView;

//        设置字段
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_list_title);
            content = itemView.findViewById(R.id.news_list_content);
            time = itemView.findViewById(R.id.news_list_time);
            source = itemView.findViewById(R.id.news_list_source);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    // 再次回调
//                    if (onItemClickListener != null) {
//                        onItemClickListener.OnItemClick(v, newsEntityList.get(getLayoutPosition()));
//                    }
//                }
//            });

        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, Entry news);
    }

    // 监听点击事件的接口
    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
