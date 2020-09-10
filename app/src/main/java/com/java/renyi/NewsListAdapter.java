package com.java.renyi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;

import java.util.List;

/**
 * RecyclerView的适配器
 */
// 参考：https://www.jianshu.com/p/368ac961808d
// 参考：https://blog.csdn.net/zhuchenglin830/article/details/82286109
public class NewsListAdapter extends RecyclerView.Adapter<NewsListAdapter.ViewHolder> {
    private Context context;
    private List<Entry> newsEntityList;
    private LayoutInflater layoutInflater;

    public NewsListAdapter(Context context) {
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
        if (news.viewed) {
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
    }

    @Override
    public int getItemCount() {
        if (newsEntityList == null)
            return 0;
        return newsEntityList.size();
    }

    public void setNewsEntityList(List<Entry> newsEntityList) {
        this.newsEntityList = newsEntityList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView title;
        private TextView time;
        private TextView source;
        private TextView content;

//        设置字段
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.news_list_title);
            content = itemView.findViewById(R.id.news_list_content);
            time = itemView.findViewById(R.id.news_list_time);
            source = itemView.findViewById(R.id.news_list_source);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 再次回调
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, newsEntityList.get(getLayoutPosition()));
                    }
                }
            });

        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, Entry news);
    }

    private OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
