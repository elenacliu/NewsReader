package com.java.renyi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.java.renyi.db.Scholar;

import java.text.DecimalFormat;
import java.util.List;


/**
 * 知疫学者Adapter
 */
public class ScholarListAdapter extends RecyclerView.Adapter<ScholarListAdapter.ViewHolder> {

    private Context context;
    private List<Scholar> scholarList;
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Scholar scholar = scholarList.get(position);

        String img = scholar.getAvatar();

        if (img != null && !img.equals("")) {
            Glide.with(holder.ivAvatar.getContext()).load(img)
                    .override(200,300).into(holder.ivAvatar);
            if (scholar.getIs_passedaway()) {
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                holder.ivAvatar.setColorFilter(filter);
            }
        }

        if (scholar.getName_zh() != null && !scholar.getName_zh().equals(""))
            holder.tvName.setText(scholar.getName_zh());
        else {
            holder.tvName.setText(scholar.getName());
        }
        holder.tvPosition.setText(scholar.getProfile().get("position"));
        holder.tvAffiliation.setText(scholar.getProfile().get("affiliation"));

        DecimalFormat decimalFormat=new DecimalFormat("#.00");

        // H-index
        holder.tvH.setText(Integer.toString(scholar.getHindex()));
        // newStar
        Float fA = scholar.getNewStar();
        if (fA >= 1) {
            holder.tvA.setText(decimalFormat.format(fA));
        }
        else {
            holder.tvA.setText("0"+decimalFormat.format(fA));
        }
        // sociability
        Float fS = scholar.getSociability();
        if (fS >= 1) {
            holder.tvS.setText(decimalFormat.format(fS));
        }
        else {
            holder.tvS.setText("0"+decimalFormat.format(fS));
        }
        // citations
        holder.tvc.setText(Integer.toString(scholar.getCitations()));
        // publications
        holder.tvP.setText(Integer.toString(scholar.getPubs()));
    }

    @Override
    public int getItemCount() {
        if (scholarList == null)
            return 0;
        return scholarList.size();
    }


    public void setScholarList(List<Scholar> scholarList) {
        this.scholarList = scholarList;
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tvName, tvH, tvA, tvS, tvc, tvP;
        private TextView tvPosition, tvAffiliation;
        private ImageView ivAvatar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.scholar1_name);
            ivAvatar = itemView.findViewById(R.id.scholar1_avatar);

            tvH = itemView.findViewById(R.id.tv_h_num);
            tvA = itemView.findViewById(R.id.tv_a_num);
            tvS = itemView.findViewById(R.id.tv_s_num);
            tvc = itemView.findViewById(R.id.tv_c_num);
            tvP = itemView.findViewById(R.id.tv_p_num);

            tvPosition = itemView.findViewById(R.id.tv_position);
            tvAffiliation = itemView.findViewById(R.id.tv_affiliation);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // 再次回调
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(v, scholarList.get(getLayoutPosition()));
                    }
                }
            });
        }
    }

    /**
     * 设置item的监听事件的接口
     */
    public interface OnItemClickListener {
        void OnItemClick(View view, Scholar scholar);
    }

    private ScholarListAdapter.OnItemClickListener onItemClickListener;
    public void setOnItemClickListener(ScholarListAdapter.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
