package com.java.renyi;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Color;
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
import java.util.Iterator;
import java.util.List;

// TODO: toolbar and backpressed
public class DataviewActivity extends AppCompatActivity {

    private BarChart barChart;
    private List<String> internationalX;
    private List<BarEntry> internationalConfirmed, internationalCured, internationalDead;
    private RecyclerView recyclerView;
    private DataviewAdapter dataviewAdapter;
    EntryViewModel mEntryViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dataview);

        initChart();
        initView();
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

    private void getDataCivilY() {
//        internationalConfirmed = new ArrayList<>();
//        internationalCured = new ArrayList<>();
//        internationalDead = new ArrayList<>();
//
//        internationalConfirmed.add(new BarEntry(935, 0));
//        internationalCured.add(new BarEntry(926, 0));
//        internationalDead.add(new BarEntry(6, 0));
//
//        internationalConfirmed.add(new BarEntry(355, 1));
//        internationalCured.add(new BarEntry(323, 1));
//        internationalDead.add(new BarEntry(11, 1));
//
//        internationalConfirmed.add(new BarEntry(150, 2));
//        internationalCured.add(new BarEntry(122, 2));
//        internationalDead.add(new BarEntry(15, 2));
//
//        internationalConfirmed.add(new BarEntry(230, 3));
//        internationalCured.add(new BarEntry(228, 3));
//        internationalDead.add(new BarEntry(2, 3));
//
//
//        internationalConfirmed.add(new BarEntry(68139, 4));
//        internationalCured.add(new BarEntry(63627, 4));
//        internationalDead.add(new BarEntry(4512, 4));
//
//
//        internationalConfirmed.add(new BarEntry(1019, 5));
//        internationalCured.add(new BarEntry(1015, 5));
//        internationalDead.add(new BarEntry(4, 5));
//
//        internationalConfirmed.add(new BarEntry(4850, 6));
//        internationalCured.add(new BarEntry(4456, 6));
//        internationalDead.add(new BarEntry(94, 6));
//
//        internationalConfirmed.add(new BarEntry(46, 7));
//        internationalCured.add(new BarEntry(46, 7));
//        internationalDead.add(new BarEntry(0, 7));
//
//        internationalConfirmed.add(new BarEntry(490, 8));
//        internationalCured.add(new BarEntry(471, 8));
//        internationalDead.add(new BarEntry(7, 8));
    }

    public void initChart() {
        barChart = findViewById(R.id.chart_international);

        XAxis xAxis = barChart.getXAxis();
        // XAxis位置默认在上方
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis left = barChart.getAxisLeft();
        left.setDrawLabels(false); // 不设置坐标轴数据标签
        left.setDrawAxisLine(false); // 不绘制坐标轴线
        left.setDrawGridLines(false); // 不绘制网格线
        left.setDrawZeroLine(true); // 绘制零线
        barChart.getAxisRight().setEnabled(false); // 不绘制右边Y轴

//        BarDataSet civilConfirmedSet = new BarDataSet(internationalConfirmed, "确诊人数");
//        BarDataSet civilCuredSet = new BarDataSet(internationalCured, "治愈人数");
//        BarDataSet civilDeadSet = new BarDataSet(internationalDead, "死亡人数");


//        dataSets.add(civilConfirmedSet);
//        dataSets.add(civilCuredSet);
//        dataSets.add(civilDeadSet);
//        BarData barData = new BarData(internationalX, dataSets);     // 多条曲线
//        barChart.setData(barData);
        barChart.animateY(2000);

        mEntryViewModel = new ViewModelProvider(this).get(EntryViewModel.class);

        final Observer<List<PandemicStatus>> nowGlobalObserver = entries -> {
            Log.e("onChanged", entries.size()+"");
            barChart.setData(generateData(entries));
            // TODO：这两句的位置是否正确？
            barChart.notifyDataSetChanged();
            barChart.invalidate();
        };
        mEntryViewModel.getGlobalStatus().observe(this, nowGlobalObserver);
        mEntryViewModel.askGlobalStatus();

    }

    // 由返回的entries生成图表需要的BarData
    private BarData generateData(List<PandemicStatus> entries) {
        internationalX = new ArrayList<>();
        internationalConfirmed = new ArrayList<>();
//        internationalSuspected = new ArrayList<>();
        internationalCured = new ArrayList<>();
        internationalDead = new ArrayList<>();

        int cnt = 0;
        for (PandemicStatus entry: entries) {
            internationalX.add(entry.region);
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
        confirmedSet.setColors(Collections.singletonList(ContextCompat.getColor(this, R.color.datatext_confirmed)));
        curedSet.setColors(Collections.singletonList(ContextCompat.getColor(this, R.color.datatext_cured)));
        deadSet.setColors(Collections.singletonList(ContextCompat.getColor(this, R.color.datatext_dead)));
        List<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(confirmedSet);
        dataSets.add(curedSet);
        dataSets.add(deadSet);

        return new BarData(internationalX, dataSets);
    }
}