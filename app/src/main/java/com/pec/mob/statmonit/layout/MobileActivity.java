package com.pec.mob.statmonit.layout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.Item;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MobileActivity extends AppCompatActivity {

    private static final String TAG = MobileActivity.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;
    private List<PieEntry> entriesPieTotal = new ArrayList<>();
    private List<Entry> entriesLineStar1 = new ArrayList<>();
    private List<Entry> entriesLineStar7 = new ArrayList<>();
    private List<Entry> entriesLineStarTop = new ArrayList<>();
    ArrayList<String> labelsLineChart = new ArrayList<>();
    private PieChart pieChart;
    private LineChart lineChart;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mobile);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        pieChart = (PieChart) findViewById(R.id.pieChartMobile);
        lineChart = (LineChart) findViewById(R.id.lineChartMobile);
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
                    initPieChart();
                    initLineChart();
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
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long updateDate = sharedPreferences.getLong(TAG+"date", 0);
        boolean needUpdate = false;
        if (updateDate != 0) {
            if (System.currentTimeMillis() - updateDate > timeInterval) {
                needUpdate = true;
            }
        } else {
            needUpdate = true;
        }

        Long pie_star1, pie_star7, pie_starTop;
        Item[] line_star1, line_star7, line_starTop;
        Gson gson = new GsonBuilder().create();
        if (needUpdate) {
            Log.i(TAG, "Connect to server to retrieve data");
            DataSet dataSet = new DataSet();
            pie_star1 = (Long) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Pie_Mob_1), ValueType._Integer);
            pie_star7 = (Long) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Pie_Mob_7), ValueType._Integer);
            pie_starTop = (Long) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Pie_Mob_top), ValueType._Integer);
            if (pie_star1 != null && pie_star7 != null && pie_starTop!=null) {
                editor.putLong(TAG+"pie1", pie_star1);
                editor.putLong(TAG+"pie7", pie_star7);
                editor.putLong(TAG+"pieTop", pie_starTop);
                editor.putLong(TAG+"date", System.currentTimeMillis());
                editor.commit();
            }
            line_star1 = dataSet.getItemValues(Agent.getItemidForChart(ChartType.Bar_Mob_1), 10);
            line_star7 = dataSet.getItemValues(Agent.getItemidForChart(ChartType.Bar_Mob_7), 10);
            line_starTop = dataSet.getItemValues(Agent.getItemidForChart(ChartType.Bar_Mob_top), 10);
            if (line_star1 != null && line_starTop != null && line_starTop!=null) {
                editor.putString(TAG+"line1", gson.toJson(line_star1));
                editor.putString(TAG+"line7", gson.toJson(line_star7));
                editor.putString(TAG+"lineTop", gson.toJson(line_starTop));
                editor.commit();
            }
        } else {
            Log.i(TAG, "Get data from shared preferences");
            pie_star1 = sharedPreferences.getLong(TAG+"pie1", 0);
            pie_star7 = sharedPreferences.getLong(TAG+"pie7", 0);
            pie_starTop = sharedPreferences.getLong(TAG+"pieTop", 0);

            line_star1 = gson.fromJson(sharedPreferences.getString(TAG+"line1", null), Item[].class);
            line_star7 = gson.fromJson(sharedPreferences.getString(TAG+"line7", null), Item[].class);
            line_starTop = gson.fromJson(sharedPreferences.getString(TAG+"lineTop", null), Item[].class);
        }
        if(pie_star1!=null && pie_star7!=null && pie_starTop!=null) {
            entriesPieTotal.clear();
            entriesPieTotal.add(new PieEntry(pie_star1,0));
            entriesPieTotal.add(new PieEntry(pie_star7, 1));
            entriesPieTotal.add(new PieEntry(pie_starTop, 2));
        }
        if(line_star1!=null && line_star7!=null && line_starTop!=null) {
            entriesLineStar1.clear();
            entriesLineStar7.clear();
            entriesLineStarTop.clear();
            labelsLineChart.clear();
            int length = line_star1.length;
            for (int i=0; i<length; i++) {
                int index = length - i - 1;
                String label = line_starTop[index].getTimeString();
                entriesLineStar1.add(new Entry(i, (Long)line_star1[index].getBintvalue()));
                entriesLineStar7.add(new Entry(i, (Long)line_star7[index].getBintvalue()));
                entriesLineStarTop.add(new Entry(i, (Long)line_starTop[index].getBintvalue()));
                labelsLineChart.add(i,label);
            }
        }
    }

    private void initPieChart() throws Exception{

        PieDataSet dataset = new PieDataSet(entriesPieTotal, null);
        PieData data = new PieData(dataset);
        data.setValueTextSize(10);
        dataset.setColors(new int[]{ContextCompat.getColor(this, R.color.blue1), ContextCompat.getColor(this, R.color.accent), ContextCompat.getColor(this, R.color.orange)});
        dataset.setValueFormatter(new LargeValueFormatter());

        pieChart.setData(data);
        pieChart.getLegend().setEnabled(false);
        pieChart.getDescription().setText("تعداد تراکنش های امروز");

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                if(firstShow) {
                    pieChart.animateY(3000);
                }

                pieChart.getData().notifyDataChanged();
                pieChart.notifyDataSetChanged();
                pieChart.invalidate();
            }
        });
    }

    private void initLineChart() throws Exception{
        List<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet dataSetStar1 = new LineDataSet(entriesLineStar1, "*1#");
        LineDataSet dataSetStar7 = new LineDataSet(entriesLineStar7, "*7#");
        LineDataSet dataSetStarTop = new LineDataSet(entriesLineStarTop, "تاپ");
        dataSets.add(dataSetStar1);
        dataSets.add(dataSetStar7);
        dataSets.add(dataSetStarTop);

        LineData data = new LineData(dataSets);
        data.setDrawValues(false);

        dataSetStar1.setColor(ContextCompat.getColor(this, R.color.blue1));
        dataSetStar1.setCircleColor(ContextCompat.getColor(this, R.color.blue1));
        dataSetStar7.setColor(ContextCompat.getColor(this, R.color.accent));
        dataSetStar7.setCircleColor(ContextCompat.getColor(this, R.color.accent));
        dataSetStarTop.setColor(ContextCompat.getColor(this, R.color.orange));
        dataSetStarTop.setCircleColor(ContextCompat.getColor(this, R.color.orange));

        lineChart.setData(data);
        lineChart.getDescription().setText("تعداد تراکنش های یک دقیقه اخیر");
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsLineChart));
        lineChart.getAxisRight().setEnabled(false);
        lineChart.setTouchEnabled(true);
        lineChart.getAxisLeft().setAxisMinimum(0);
        CustomMarkerView mv = new CustomMarkerView (this, R.layout.marker_view);
        lineChart.setMarkerView(mv);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                lineChart.getData().notifyDataChanged();
                lineChart.notifyDataSetChanged();
                lineChart.invalidate();
                if(firstShow) {
                    lineChart.animateY(3000);
                    firstShow=false;
                }
            }
        });
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {

            try {
                layout.setVisibility(View.VISIBLE);
                pieChart.setVisibility(View.GONE);
                lineChart.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                layout.setVisibility(View.GONE);
                pieChart.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                initPieChart();
                initLineChart();
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
