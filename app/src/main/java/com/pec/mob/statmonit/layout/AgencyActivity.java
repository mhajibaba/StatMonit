package com.pec.mob.statmonit.layout;

import android.app.AlertDialog;
import android.content.Context;
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
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.object.Agency;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.ChartType;
import com.pec.mob.statmonit.object.ValueType;
import com.pec.mob.statmonit.util.DataSet;
import com.pec.mob.statmonit.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AgencyActivity extends AppCompatActivity {

    private static final String TAG = AgencyActivity.class.getSimpleName();
    private final int timeInterval=60000;
    private Timer timer;
    private boolean firstShow=true;

    private List<Agency> agencies = new ArrayList<>();

    private BarChart barChart;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agency);
        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        barChart = (BarChart) findViewById(R.id.barChartAgencies);
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

        String jsonData;
        if (needUpdate) {
            Log.i(TAG, "Connect to server to retrieve data");
            DataSet dataSet = new DataSet();
            jsonData = (String) dataSet.getItemValue(Agent.getItemidForChart(ChartType.Bar_Agency), ValueType._String);
            if (jsonData != null) {
                editor.putString(TAG+"data", jsonData);
                editor.putLong(TAG+"date", System.currentTimeMillis());
            }
        } else {
            Log.i(TAG, "Get data from shared preferences");
            jsonData = sharedPreferences.getString(TAG+"data", null);
        }

        Gson gson = new GsonBuilder().create();
        Agency[] _agencies = gson.fromJson(jsonData, Agency[].class);
        if (_agencies != null && _agencies.length > 0) {
            this.agencies = Arrays.asList(_agencies);
            editor.commit();
        }
    }

    private void initChart() throws Exception{
        ArrayList<BarEntry> entries = new ArrayList<>();
        List<String> lables = new ArrayList<>();
        Collections.sort(agencies);

        int i=0;
        for (Agency agency: agencies) {
            String label;
            if(agency.getTitle().length()<=5)
                label = agency.getTitle();
            else
                label= agency.getTitle().substring(0,5)+"...";
            lables.add(i, label);
            entries.add(new BarEntry(i++, agency.getTXNcnt()));
        }

        IBarDataSet dataset = new BarDataSet(entries, "# تراکنش ها");
        dataset.setValueFormatter(new LargeValueFormatter());
        dataset.setAxisDependency(YAxis.AxisDependency.RIGHT); //right is bottom in horizontal

        BarData data = new BarData(dataset);
        data.setDrawValues(true);
        data.setValueTextSize(10);

        if(agencies !=null && agencies.size()>0)
            barChart.setData(data);

        barChart.setTouchEnabled(true);
        barChart.getAxisLeft().setEnabled(false);
        barChart.setScaleMinima(1f, 1.5f);
        barChart.centerViewToY(100,YAxis.AxisDependency.RIGHT);
        barChart.getDescription().setText("نمایندگی ها");
        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(lables));
        barChart.getXAxis().setLabelCount(lables.size());
        barChart.getAxisRight().setAxisMinimum(0);
        barChart.setDrawValueAboveBar(false);

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
                initiateDialogWindow(agencies.get((int)e.getX()));
            }

            @Override
            public void onNothingSelected() {

            }
        });
    }

    private void initiateDialogWindow(Agency data) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("اطلاعات تفکیکی");
            final CharSequence[] items = {
                    getString(R.string.agency_id)+ " : " + data.getIdentifier(),
                    getString(R.string.agency_title)+ " : " + data.getTitle(),
                    getString(R.string.agency_count)+ " : " + data.getTXNcnt()
            };

            builder.setItems(items, null);
            builder.setPositiveButton(getString(R.string.exit), null);

            AlertDialog dialog = builder.create();
            dialog.show();

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
