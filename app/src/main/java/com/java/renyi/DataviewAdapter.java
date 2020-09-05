package com.java.renyi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.PandemicStatus;

import java.util.List;
import java.util.zip.Inflater;

/**
 * 国内疫情数据的recyclerview的adapter
 */
public class DataviewAdapter extends RecyclerView.Adapter<DataviewAdapter.ViewHolder> {
    private Context context;
    private List<PandemicStatus> statusList;
    private LayoutInflater layoutInflater;

    public DataviewAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.civil_item, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PandemicStatus pandemicStatus = statusList.get(position);

        holder.region.setText(pandemicStatus.region);
        Integer confirmed = pandemicStatus.status[0];
        Integer suspected = pandemicStatus.status[1];
        Integer cured = pandemicStatus.status[2];
        Integer dead = pandemicStatus.status[3];
        if (confirmed != null) {
            holder.confirmed.setText(Integer.toString(confirmed));
        }
        else {
            holder.confirmed.setText("---");
        }
        if (suspected != null) {
            holder.suspected.setText(Integer.toString(suspected));
        }
        else {
            holder.suspected.setText("---");
        }
        if (cured != null) {
            holder.cured.setText(Integer.toString(cured));
        }
        else {
            holder.cured.setText("---");
        }
        if (dead != null) {
            holder.dead.setText(Integer.toString(dead));
        }
        else {
            holder.dead.setText("---");
        }
    }

    @Override
    public int getItemCount() {
        if (statusList == null)
            return 0;
        return statusList.size();
    }

    public void setDataList(List<PandemicStatus> pandemicStatusList) {
        this.statusList = pandemicStatusList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView region;
        private TextView confirmed;
        private TextView suspected;
        private TextView cured;
        private TextView dead;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            region = itemView.findViewById(R.id.tv_region);
            confirmed = itemView.findViewById(R.id.tv_confirmed);
            suspected = itemView.findViewById(R.id.tv_suspected);
            cured = itemView.findViewById(R.id.tv_cured);
            dead = itemView.findViewById(R.id.tv_dead);
            // 无点击事件的响应
        }
    }
}
