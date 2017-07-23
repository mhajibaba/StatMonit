package com.pec.mob.statmonit.layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IBubbleDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.TransStat;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class TransactionActivity extends AppCompatActivity {

    private static final String TAG = TransactionActivity.class.getSimpleName();
    private Map<Integer, List<TransStat>> stats;
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;
    private BubbleChart bubbleChart;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        bubbleChart = (BubbleChart) findViewById(R.id.bubbleChartTransStat);
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Task().execute();
        timer = new Timer(true);
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    getData();
                    initChart();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, timeInterval, timeInterval);

    }

    @Override
    public void onPause() {
        super.onPause();
        if(timer!=null) {
            timer.cancel();
        }
        firstShow = true;
    }

    private void getData() throws Exception{
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);        SharedPreferences.Editor editor = sharedPreferences.edit();
        long updateDate = sharedPreferences.getLong(TAG+"date",0);
        boolean needUpdate = false;
        if(updateDate!=0) {
            if(System.currentTimeMillis()-updateDate>timeInterval) {
                needUpdate = true;
            }
        }else {
            needUpdate = true;
        }

        Gson gson = new GsonBuilder().create();
        List<Object> jsonDataList;

        if(needUpdate) {
            Log.i(TAG, "Connect to server to retrieve data");
            DataSet dataSet = new DataSet();
            jsonDataList = dataSet.getItemValues(Agent.getItemidForChart(ChartType.Bubble_Trans), ValueType._String, 1);
            if (jsonDataList != null) {
                editor.putString(TAG+"data", gson.toJson(jsonDataList));
                editor.putLong(TAG+"date", System.currentTimeMillis());
            }
        }else {
            Log.i(TAG, "Get data from shared preferences");
            String jsonSavedDataSet = sharedPreferences.getString(TAG+"data",null);
            jsonDataList = gson.fromJson(jsonSavedDataSet,(new ArrayList<>().getClass()));
        }

        int i=0;
        if(stats==null)
            stats = new HashMap<>();
        else
            stats.clear();
        //we may have multiple bubble data-set in the diagram
        for(Object obj:jsonDataList) {
            TransStat[] transStet = gson.fromJson((String) obj, TransStat[].class);
            if(transStet!=null && transStet.length>0) {
                stats.put(i, Arrays.asList(transStet));
                i++;
            }
            editor.commit();
        }
    }

    private void initChart() throws Exception{

        ArrayList<String> labels = new ArrayList<>();

        List<IBubbleDataSet> dataSets = new ArrayList<>();

        int index;
        for(int i=0; i<stats.size(); i++) {
            index=0;
            List<BubbleEntry> entries = new ArrayList<>();
            labels.clear();
            for (TransStat stat : stats.get(i)) {
                entries.add(new BubbleEntry(index, stat.getCnt(), stat.getAvgtime()));
                labels.add(index,stat.getTxntitle().trim());
                index++;
            }
            BubbleDataSet bubbleDataSet = new BubbleDataSet(entries, "مدت زمان اجرای تراکنش");
            bubbleDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
            bubbleDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); //right is bottom in horizontal
            bubbleDataSet.setDrawValues(false);
            dataSets.add(bubbleDataSet);
        }

        BubbleData data = new BubbleData(dataSets);
        data.setValueTextSize(10);
        data.setDrawValues(true);

        bubbleChart.setData(data);
        bubbleChart.setPinchZoom(true);
        bubbleChart.setTouchEnabled(true);
        bubbleChart.getAxisRight().setEnabled(false);
        bubbleChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        bubbleChart.getXAxis().setLabelCount(labels.size());
        bubbleChart.getXAxis().setAxisMinimum(-1);
        bubbleChart.getXAxis().setAxisMaximum(labels.size());
        bubbleChart.getDescription().setText("تعداد تراکنش های نمایندگی ها");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(firstShow) {
                    bubbleChart.animateY(3000);
                    firstShow = false;
                }
                bubbleChart.notifyDataSetChanged();
                bubbleChart.getData().notifyDataChanged();
                bubbleChart.invalidate();
            }
        });

        XAxis xAxis=bubbleChart.getXAxis();
        xAxis.setLabelRotationAngle(-90);
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                layout.setVisibility(View.VISIBLE);
                bubbleChart.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                layout.setVisibility(View.GONE);
                bubbleChart.setVisibility(View.VISIBLE);
                initChart();
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                getData();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
