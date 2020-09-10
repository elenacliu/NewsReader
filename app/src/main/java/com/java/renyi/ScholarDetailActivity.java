package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.java.renyi.db.Scholar;

public class ScholarDetailActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TextView tvName, tvPosition, tvAffiliation;
    private TextView tvBio, tvEdu, tvWork, tvMem;
    private ImageView ivAvatar;
    private CardView cardViewBio, cardViewEdu, cardViewWork, cardViewMem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scholar_detail);

        initView();
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar_scholar_detail);
        toolbar.setTitle("学者详情");
        setSupportActionBar(toolbar);

        ivAvatar = findViewById(R.id.iv_detail_avatar);
        tvName = findViewById(R.id.tv_scholar_detail_name);
        tvPosition = findViewById(R.id.tv_scholar_detail_position);
        tvAffiliation = findViewById(R.id.tv_detail_affiliation);
        tvBio = findViewById(R.id.tv_detail_biocontent);
        tvEdu = findViewById(R.id.tv_detail_educontent);
        tvWork = findViewById(R.id.tv_detail_workcontent);
        tvMem = findViewById(R.id.tv_detail_memorialcontent);

        cardViewBio = findViewById(R.id.bio);
        cardViewEdu = findViewById(R.id.edu);
        cardViewWork = findViewById(R.id.work);
        cardViewMem = findViewById(R.id.memorial);

        Intent intent = getIntent();
        Scholar scholar = (Scholar) intent.getSerializableExtra("scholar");

        String img = scholar.getAvatar();
        if (img != null && !img.equals("")) {
            if (scholar.getIs_passedaway()) {
                ColorMatrix colorMatrix = new ColorMatrix();
                colorMatrix.setSaturation(0);
                ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
                ivAvatar.setColorFilter(filter);
            }
                Glide.with(this).load(img).override(200,300).into(ivAvatar);
        }

        String name_zh = scholar.getName_zh();
        if (name_zh == null || name_zh.equals(""))
            tvName.setText(scholar.getName());
        else tvName.setText(name_zh);


        tvPosition.setText(scholar.getProfile().get("position"));

        if (scholar.getName_zh().equals("李文亮")) {
            tvPosition.setText("武汉市中心医院医生");
        }

        tvAffiliation.setText(scholar.getProfile().get("affiliation"));

        String bio = scholar.getProfile().get("bio");
        if (bio != null && !bio.equals("")) {
            tvBio.setText(scholar.getProfile().get("bio"));
        }
        else {
            cardViewBio.setVisibility(View.GONE);
        }

        String edu = scholar.getProfile().get("edu");
        if (edu == null || edu.equals("")) {
            cardViewEdu.setVisibility(View.GONE);
        }
        else
            tvEdu.setText(scholar.getProfile().get("edu"));

        String work = scholar.getProfile().get("work");
        if (work == null || work.equals("")) {
            cardViewWork.setVisibility(View.GONE);
        }
        else
            tvWork.setText(scholar.getProfile().get("work"));

        if (scholar.getIs_passedaway()) {
            String passawayReason = scholar.getProfile().get("passaway_reason");
            if (passawayReason != null && !passawayReason.equals("")) {
                cardViewMem.setVisibility(View.VISIBLE);
                tvMem.setText(passawayReason);
            }
        }
    }
}