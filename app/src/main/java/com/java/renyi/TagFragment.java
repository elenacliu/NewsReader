package com.java.renyi;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.ClassicsHeader;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;

/**
 * 每个单独标签的新闻列表Fragment
 */

public class TagFragment extends BaseFragment {
    // 下拉刷新，上拉加载
    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;

    String currentTag;

    RecyclerView recyclerView;
    NewsListAdapter newsListAdapter;
    List<Entry> newsEntityList;
    EntryViewModel mEntryViewModel;

    public TagFragment() {
        this.currentTag = "其他";
    }

    public TagFragment(String currentTag) {
        this.currentTag = currentTag;
    }

    public static TagFragment newInstance(String currentTag) {
        return new TagFragment(currentTag);
    }

    @Override
    protected int inflateLayoutId() {
        return R.layout.fragment_tag;
    }

    // TODO: get op1，will it be called again when the fragment is showed again?
    // No, it will only be called when created, but it is also ok.
//    private void getNewsEntityList() {
//        newsEntityList = new ArrayList<>();
//        newsEntityList.add(
//                new NewsEntity(	"Estimates of the severity of coronavirus disease 2019: a model-based analysis",
//                        "Background：In the face of rapidly changing data, a range of case fatality ratio estimates for coronavirus disease 2019 (COVID-19) have been produced that differ substantially in magnitude. We aimed to provide robust estimates, accounting for censoring and ascertainment biases.\nMethods：We collected individual-case data for patients who died from COVID-19 in Hubei, mainland China (reported by national and provincial health commissions to Feb 8, 2020), and for cases outside of mainland China (from government or ministry of health websites and media reports for 37 countries, as well as Hong Kong and Macau, until Feb 25, 2020). These individual-case data were used to estimate the time between onset of symptoms and outcome (death or discharge from hospital). We next obtained age-stratified estimates of the case fatality ratio by relating the aggregate distribution of cases to the observed cumulative deaths in China, assuming a constant attack rate by age and adjusting for demography and age-based and location-based under-ascertainment. We also estimated the case fatality ratio from individual line-list data on 1334 cases identified outside of mainland China. Using data on the prevalence of PCR-confirmed cases in international residents repatriated from China, we obtained age-stratified estimates of the infection fatality ratio. Furthermore, data on age-stratified severity in a subset of 3665 cases from China were used to estimate the proportion of infected individuals who are likely to require hospitalisation.\nFindings：Using data on 24 deaths that occurred in mainland China and 165 recoveries outside of China, we estimated the mean duration from onset of symptoms to death to be 17·8 days (95% credible interval [CrI] 16·9–19·2) and to hospital discharge to be 24·7 days (22·9–28·1). In all laboratory confirmed and clinically diagnosed cases from mainland China (n=70 117), we estimated a crude case fatality ratio (adjusted for censoring) of 3·67% (95% CrI 3·56–3·80). However, after further adjusting for demography and under-ascertainment, we obtained a best estimate of the case fatality ratio in China of 1·38% (1·23–1·53), with substantially higher ratios in older age groups (0·32% [0·27–0·38] in those aged <60 years vs 6·4% [5·7–7·2] in those aged ≥60 years), up to 13·4% (11·2–15·9) in those aged 80 years or older. Estimates of case fatality ratio from international cases stratified by age were consistent with those from China (parametric estimate 1·4% [0·4–3·5] in those aged <60 years [n=360] and 4·5% [1·8–11·1] in those aged ≥60 years [n=151]). Our estimated overall infection fatality ratio for China was 0·66% (0·39–1·33), with an increasing profile with age. Similarly, estimates of the proportion of infected individuals likely to be hospitalised increased with age up to a maximum of 18·4% (11·0–7·6) in those aged 80 years or older.\nInterpretation：These early estimates give an indication of the fatality ratio across the spectrum of COVID-19 disease and show a strong age gradient in risk of death.",
//                        "2020/03/31", null, false, null));
//        newsEntityList.add(
//                new NewsEntity("What is required to prevent a second major outbreak of the novel coronavirus SARS-CoV-2 upon lifting the metropolitan-wide quarantine of Wuhan city, China",
//                        "Background: The Chinese government implemented a metropolitan-wide quarantine of Wuhan city on 23rd January 2020 to curb the epidemic of the coronavirus COVID-19. Lifting of this quarantine is imminent. We modelled the effects of two key health interventions on the epidemic when the quarantine is lifted. Method: We constructed a compartmental dynamic model to forecast the trend of the COVID-19 epidemic at different quarantine lifting dates and investigated the impact of different rates of public contact and facial mask usage on the epidemic. Results: We estimated that at the end of the epidemic, a total of 65,572 (46,156-95,264) individuals would be infected by the virus, among which 16,144 (14,422-23,447, 24.6%) would be infected through public contacts, 45,795 (32,390-66,395, 69.7%) through household contact, 3,633 (2,344-5,865, 5.5%) through hospital contacts (including 783 (553-1,134) non-COVID-19 patients and 2,850 (1,801-4,981) medical staff members). A total of 3,262 (1,592-6,470) would die of COVID-19 related pneumonia in Wuhan. For an early lifting date (21st March), facial mask needed to be sustained at a relatively high rate (≥85%) if public contacts were to recover to 100% of the pre-quarantine level. In contrast, lifting the quarantine on 18th April allowed public person-to-person contact adjusted back to the pre-quarantine level with a substantially lower level of facial mask usage (75%). However, a low facial mask usage (<50%) combined with an increased public contact (>100%) would always lead a significant second outbreak in most quarantine lifting scenarios. Lifting the quarantine on 25th April would ensure a smooth decline of the epidemics regardless of the combinations of public contact rates and facial mask usage. Conclusion: The prevention of a second epidemic is viable after the metropolitan-wide quarantine is lifted but requires a sustaining high facial mask usage and a low public contact rate.",
//                        "2020/03/31", null, false, null));
//
//        newsEntityList.add(
//                new NewsEntity(	"Antihypertensive treatment with ACEI",
//                        "Currently, the novel coronavirus (COVID-19) has spread to many countries around the world. Due to the increasing number of confirmed cases and public hazards, COVID-19 has become a public health emergency of international concern and has received much attention from health organizations worldwide.At present, the pathogenesis of COVID-19 has not been elucidated [1]. However, a preliminary study speculated that it might enter the human body via angiotensin-converting enzyme 2 (ACE2) on the surfaces of type II alveolar cells[2]. Analysis of the clinical characteristics of patients with COVID-19 suggested that patients with hypertension comprised 20–30% of all patients and up to 58.3% of patients in the intensive care unit and have been responsible for 60.9%of deaths caused by COVID-19. The renin-angiotensin system (RAS) plays an important role in the occurrence and development of hypertension, and ACE inhibitors (ACEIs) and angiotensin receptor antagonists (ARBs) are the main antihypertensive drugs recommended by the current guidelines. Therefore, what should be done in regard to ACEI/ARB for the antihypertensive treatment of patients with COVID-19 complicated by hypertension? We will conduct a specific analysis as follows.",
//                        "2020/03/31", null, true, null));
//    }

    protected void initView(View view) {
//        getNewsEntityList();
        // 获取recylerView
        recyclerView = view.findViewById(R.id.recycler_view);
        // 创建adapter
        newsListAdapter = new NewsListAdapter(getActivity());
        // 先创建完adapter再设置（更改）newsEntityList
//        newsListAdapter.setNewsEntityList(newsEntityList);
        // recyclerView设置adapter
        recyclerView.setAdapter(newsListAdapter);
        // 设置layoutManager
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        if (currentTag.equals("paper")) {
            final Observer<List<Entry>> nowPaperEntryObserver = entries -> {
                Log.e("onChanged", entries.size()+"");
                // TODO: Print entries, when content is null
                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
            };
            mEntryViewModel.getCurrentPaperEntrys().observe(this, nowPaperEntryObserver);
        }
        else if (currentTag.equals("news")) {
            final Observer<List<Entry>> nowNewsEntryObserver = entries -> {
                Log.e("onChanged", entries.size()+"");
                Iterator<Entry> iterator = entries.iterator();
                while (iterator.hasNext()) {
                    Entry entry = iterator.next();
                    if (entry.content == null) {
                        iterator.remove();
                    }
                }
                newsListAdapter.setNewsEntityList(entries);
            };
            mEntryViewModel.getCurrentNewsEntrys().observe(this, nowNewsEntryObserver);
        }


        // 监听点击事件
        newsListAdapter.setOnItemClickListener(new NewsListAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(View view, Entry news) {
                System.out.println("------------------------");
                Log.d("click", news.toString());
                System.out.println("------------------------");
                Intent intent = new Intent(getActivity(), NewsDetailActivity.class);
                intent.putExtra("news", news);
                // 直接把值传进MainActivity ?
                startActivityForResult(intent, Constants.UPDATE_REQUEST);
            }
        });


        refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        refreshLayout.setRefreshFooter(new ClassicsFooter(getActivity()));
        // 注意每次实施onfresh判断一下网络连接状态
        refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (!NetworkUtil.isNetworkAvailable(getActivity())) {
                    Toast.makeText(getActivity(), "offline",Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(getActivity(), "online", Toast.LENGTH_SHORT).show();
                    if (currentTag.equals("paper")) {
                        mEntryViewModel.refresh("paper");
                    }
                    else if (currentTag.equals("news")) {
                        mEntryViewModel.refresh("news");
                    }
                }
                refreshlayout.finishRefresh(2000/*,false*/);//传入false表示刷新失败
            }
        });

        // TODO: get op3，上拉加载，获取更多新闻
        refreshLayout.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore(RefreshLayout refreshlayout) {
                if (currentTag.equals("paper")) {
                    mEntryViewModel.addMorePaper();
                }
                else if (currentTag.equals("news")) {
                    mEntryViewModel.addMoreNews();
                }
                refreshlayout.finishLoadMore(2000/*,false*/);//传入false表示加载失败
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // 请求变灰
        if (requestCode == Constants.UPDATE_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                assert data != null;
                mEntryViewModel.setViewed(data.getStringExtra("id"), currentTag);
            }
        }
    }


    public void onResume() {
        super.onResume();
        Log.e("onResume", currentTag +" onResume");
    }

    @Override
    public String toString() {
        return "TagFragment{" +
                "refreshLayout=" + refreshLayout +
                ", currentTag='" + currentTag + '\'' +
                ", recyclerView=" + recyclerView +
                ", newsListAdapter=" + newsListAdapter +
                ", newsEntityList=" + newsEntityList +
                ", mEntryViewModel=" + mEntryViewModel +
                '}';
    }
}
