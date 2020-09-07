package com.java.renyi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.java.renyi.db.Entry;
import com.java.renyi.db.EntryViewModel;
import com.java.renyi.db.PandemicStatus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DataviewActivity extends AppCompatActivity {

    private BarChart barChart;
    private List<String> internationalX;
    private List<BarEntry> internationalConfirmed, internationalCured, internationalDead;
    private RecyclerView recyclerView;
    private DataviewAdapter dataviewAdapter;
    EntryViewModel mEntryViewModel;
    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);

        initToolbar();
        initChart();
        initView();
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.toolbar_dataview);
        toolbar.setTitle("疫情数据");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//左侧添加一个默认的返回图标
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    /**
     * initialize recyclerview & adapter
     */
    private void initView() {
        recyclerView = findViewById(R.id.recycler_view_dataview);
        dataviewAdapter = new DataviewAdapter(this);
        recyclerView.setAdapter(dataviewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setNestedScrollingEnabled(false);

        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        final Observer<List<PandemicStatus>> nowDomesticObserver = entries -> {
            Log.e("onChanged", entries.size()+"");
            dataviewAdapter.setDataList(entries);
            System.out.println("----------");
            Log.e("domestic", entries.toString());
            System.out.println("----------");
        };
        mEntryViewModel.getDomesticStatus().observe(this, nowDomesticObserver);

        mEntryViewModel.askDomesticStatus();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void initChart() {
        barChart = findViewById(R.id.chart_international);

        XAxis xAxis = barChart.getXAxis();
        // XAxis位置默认在上方
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);

        YAxis left = barChart.getAxisLeft();
        left.setDrawLabels(false); // 不设置坐标轴数据标签
        left.setDrawAxisLine(false); // 不绘制坐标轴线
        left.setDrawGridLines(false); // 不绘制网格线
        left.setDrawZeroLine(true); // 绘制零线
        barChart.getAxisRight().setEnabled(false); // 不绘制右边Y轴
        barChart.animateY(2000);
        barChart.setDescription("");

        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        final Observer<List<PandemicStatus>> nowGlobalObserver = entries -> {
            Log.e("onChanged", entries.size()+"");
            barChart.setData(generateData(entries));
            barChart.notifyDataSetChanged();
            barChart.invalidate();
        };
        mEntryViewModel.getGlobalStatus().observe(this, nowGlobalObserver);
        mEntryViewModel.askGlobalStatus();
    }

    // 由返回的entries生成图表需要的BarData
    @RequiresApi(api = Build.VERSION_CODES.N)
    private BarData generateData(List<PandemicStatus> entries) {

        Collections.sort(entries, new Comparator<PandemicStatus>() {
            @Override
            public int compare(PandemicStatus t0, PandemicStatus t1) {
                return t1.status[0].compareTo(t0.status[0]);
            }
        });
        List<PandemicStatus> newEntries = entries.subList(1,6);

        System.out.println("----------");
        Log.e("newEntries", newEntries.toString());
        System.out.println("----------");

        internationalX = new ArrayList<>();
        internationalConfirmed = new ArrayList<>();
        internationalCured = new ArrayList<>();
        internationalDead = new ArrayList<>();

        HashMap<String, String> trans = new HashMap<>();
        trans.put("United States of America", "美国");
        trans.put("Brazil", "巴西");
        trans.put("India","印度");
        trans.put("Russia","俄罗斯");
        trans.put("Peru", "秘鲁");

        int cnt = 0;
        for (PandemicStatus entry: newEntries) {
            internationalX.add(trans.getOrDefault(entry.region, entry.region));
            Integer[] status = entry.status;
            int confirmed, cured, dead;
            if (status[0] == null) {
                confirmed = 0;
            }
            else confirmed = status[0];
            if (status[2] == null) {
                cured = 0;
            }
            else cured = status[2];
            if (status[3] == null) {
                dead = 0;
            }
            else dead = status[3];
            internationalConfirmed.add(new BarEntry(confirmed, cnt));
            internationalCured.add(new BarEntry(cured, cnt));
            internationalDead.add(new BarEntry(dead, cnt));
            cnt++;
        }
        BarDataSet confirmedSet = new BarDataSet(internationalConfirmed, "确诊人数");
        BarDataSet curedSet = new BarDataSet(internationalCured, "治愈人数");
        BarDataSet deadSet = new BarDataSet(internationalDead, "死亡人数");
//        confirmedSet.setColors(Collections.singletonList(ContextCompat.getColor(this, R.color.datatext_confirmed)));
//        curedSet.setColors(Collections.singletonList(ContextCompat.getColor(this, R.color.datatext_cured)));
//        deadSet.setColors(Collections.singletonList(ContextCompat.getColor(this, R.color.datatext_dead)));
        confirmedSet.setColor(Color.rgb(48,88,140));
        curedSet.setColor(Color.rgb(119,195,242));
        deadSet.setColor(Color.rgb(58,117,140));
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(confirmedSet);
        dataSets.add(curedSet);
        dataSets.add(deadSet);

        return new BarData(internationalX, dataSets);
    }
}