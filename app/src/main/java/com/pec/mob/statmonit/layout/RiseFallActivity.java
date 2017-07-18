package com.pec.mob.statmonit.layout;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.RiseFall;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class RiseFallActivity extends AppCompatActivity {

    private static final String TAG = RiseFallActivity.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;

    private List<RiseFall> rises = new ArrayList<>();
    private List<RiseFall> falls = new ArrayList<>();
    private List<BarEntry> entries = new ArrayList<>();

    private BarChart barChart;
    private LinearLayout layout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rise_fall);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        barChart = (BarChart) findViewById(R.id.barChartRiseFall);
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
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        long updateDate = sharedPreferences.getLong("date", 0);
        boolean needUpdate = false;
        if (updateDate != 0) {
            if (System.currentTimeMillis() - updateDate > timeInterval) {
                needUpdate = true;
            }
        } else {
            needUpdate = true;
        }

        String jsonDataRise,jsonDataFall;
        if (needUpdate) {
            Log.i(TAG, "Connect to server to retrieve data");
            DataSet dataSet = new DataSet();
            jsonDataRise = (String) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Bar_Rise), ValueType._String);
            jsonDataFall = (String) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Bar_Fall), ValueType._String);
            if (jsonDataRise != null || jsonDataFall != null) {
                editor.putString("dataRise", jsonDataRise);
                editor.putString("dataFall", jsonDataFall);
                editor.putLong("date", System.currentTimeMillis());
            }
        } else {
            Log.i(TAG, "Get data from shared preferences");
            jsonDataRise = sharedPreferences.getString("dataRise", null);
            jsonDataFall = sharedPreferences.getString("dataFall", null);
        }

        Gson gson = new GsonBuilder().create();
        RiseFall[] risesInf = gson.fromJson(jsonDataRise, RiseFall[].class);
        RiseFall[] fallsInf = gson.fromJson(jsonDataFall, RiseFall[].class);
        if (risesInf != null && risesInf.length > 0) {
            rises = Arrays.asList(risesInf);
            editor.commit();
        }
        if (fallsInf != null && fallsInf.length > 0) {
            falls = Arrays.asList(fallsInf);
            editor.commit();
        }
    }

    private void initChart() throws Exception{
        List<String> lables = new ArrayList<>();
        Collections.sort(rises);
        Collections.sort(falls);

        int i=0;
        for(RiseFall riseFall: rises) {
            //==============Rises================//
            if(riseFall.getAdvantage()>15000) { continue;}
            String labelR = riseFall.getName();
            if (labelR.length() > 5)
                labelR = labelR.substring(0, 5) + "...";

            lables.add(i, labelR);
            entries.add(new BarEntry(i, riseFall.getAdvantage(),riseFall));
            i++;
        }

        for(RiseFall riseFall: falls) {
            //==============Falls================//
            String labelF = riseFall.getName();
            if(labelF.length()>5)
                labelF = labelF.substring(0,5)+"...";

            lables.add(i,labelF);
            entries.add(new BarEntry(i, riseFall.getAdvantage(),riseFall));
            i++;
        }

        BarDataSet dataset = new BarDataSet(entries, null);
        dataset.setColors(ColorTemplate.JOYFUL_COLORS);
        //dataset.setAxisDependency(YAxis.AxisDependency.RIGHT); //right is bottom in horizontal

        BarData data = new BarData(dataset);
        data.setDrawValues(true);
        data.setValueTextSize(10);

        if(rises!=null && rises.size()>0)
            barChart.setData(data);

        barChart.getDescription().setText("# تراکنش ها");
        barChart.setPinchZoom(true);
        barChart.setTouchEnabled(true);
        barChart.setScaleMinima(1f, 1.1f);
        barChart.centerViewToY(100,YAxis.AxisDependency.RIGHT);
        barChart.getAxisLeft().setEnabled(false);  //up axis
        barChart.getLegend().setEnabled(false);

        barChart.getAxisLeft().setDrawLabels(true);
        barChart.getAxisRight().setDrawLabels(true);

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(lables));
        barChart.getXAxis().setLabelCount(lables.size());

        if(!(barChart instanceof HorizontalBarChart)) {
            XAxis xAxis=barChart.getXAxis();
            xAxis.setLabelRotationAngle(-90);
        }

        final BarChart finalBarChart = barChart;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(firstShow) {
                    finalBarChart.animateY(3000);
                    firstShow = false;
                }
                finalBarChart.notifyDataSetChanged();
                finalBarChart.getData().notifyDataChanged();
                finalBarChart.invalidate();
            }
        });

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                /*int index = (int) e.getX();
                if(index<falls.size()) {
                    initiateDialogWindow(falls.get(index));
                }else {
                    index -= falls.size();
                    initiateDialogWindow(rises.get(index));
                }*/
                initiateDialogWindow((RiseFall) entries.get((int)e.getX()).getData());
            }

            @Override
            public void onNothingSelected() {
            }
        });
    }

    private void initiateDialogWindow(RiseFall data) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("اطلاعات تفکیکی  "+data.getName());
            final CharSequence[] items = {
                    getString(R.string.rf_number)+ " : " + data.getNumber(),
                    getString(R.string.rf_cnt)+ " : " + data.getNowCount(),
                    getString(R.string.rf_oneDayCnt)+ " : " + data.getDayCount(),
                    getString(R.string.rf_twoDayCnt)+ " : " + data.getTwoDayCount(),
                    getString(R.string.rf_threeDayCnt)+ " : " + data.getThreeDayCount(),
                    getString(R.string.rf_weekCnt)+ " : " +data.getWeekCount(),
                    getString(R.string.rf_monthCnt)+ " : " +data.getMonthCount(),
                    getString(R.string.rf_average)+ " : " + data.getAverage(),
                    getString(R.string.rf_tolerance)+ " : " + new DecimalFormat("#.##").format(data.getTolerance())+"%"
                    /*getString(R.string.rf_advantage)+ " : " + data.getAdvantage()*/
                    };

            builder.setItems(items, null);
            builder.setPositiveButton(getString(R.string.exit), null);

            AlertDialog dialog = builder.create();
            dialog.show();  //<-- See This!

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                layout.setVisibility(View.VISIBLE);
                barChart.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                layout.setVisibility(View.GONE);
                barChart.setVisibility(View.VISIBLE);

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
