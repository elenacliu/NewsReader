package com.java.renyi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;

import java.util.List;


/**
 * 知疫学者Adapter
 */
public class ScholarListAdapter extends RecyclerView.Adapter<ScholarListAdapter.ViewHolder> {

    private Context context;
    private List<Object> scholarList;
    private LayoutInflater layoutInflater;

    public ScholarListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.scholar1_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Object scholar = scholarList.get(position);

//        holder
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName;
//        private

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
