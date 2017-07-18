package com.pec.mob.statmonit.layout;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.AgentItemContent;
import com.pec.mob.statmonit.object.AgentItem;
import com.pec.mob.statmonit.object.Item;
import com.pec.mob.statmonit.util.DataSet;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A fragment representing a single AgentItem detail screen.
 * This fragment is either contained in a {@link AgentItemListActivity}
 * in two-pane mode (on tablets) or a {@link AgentItemDetailActivity}
 * on handsets.
 */
public class AgentItemDetailFragment extends Fragment {

    private static final String TAG = AgentItemDetailFragment.class.getSimpleName();

    private final int timeInterval=60000;
    private List<Entry> entriesLineChart = new ArrayList<>();
    private ArrayList<String> labelsLineChart = new ArrayList<>();

    private LineChart lineChart;
    private LinearLayout layout;

    private TextView fromDate, toDate;


    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The agent item content this fragment is presenting.
     */
    private AgentItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AgentItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the Agent item content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = AgentItemContent.ITEM_MAP.get(getArguments().getInt(ARG_ITEM_ID));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.getDescription());
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.agentitem_detail, container, false);

        layout = (LinearLayout) rootView.findViewById(R.id.progressbar_view);
        lineChart = (LineChart) rootView.findViewById(R.id.lineChartCustomQuery);
        Button btnShow = (Button) rootView.findViewById(R.id.btnShow);
        btnShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Task().execute();
            }
        });
        fromDate = (TextView) rootView.findViewById(R.id.txtFrom);
        toDate = (TextView) rootView.findViewById(R.id.txtTo);

        /*// Show the agent item content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.agentitem_detail)).setText(mItem.toString());
        }*/

        return rootView;
    }

    private void getData() throws Exception{
        Item[] line_data;
        Gson gson = new GsonBuilder().create();
        Log.i(TAG, "Connect to server to retrieve data");
        String fromDateText = (String) fromDate.getText();
        String toDateText = (String) toDate.getText();
        DataSet dataSet = new DataSet();
        try {
            DateFormat df = new SimpleDateFormat("yyyy-M-dd");
            Date resultFrom =  df.parse(toDateText);
            Date resultTo =  df.parse(fromDateText);
            line_data = dataSet.getItemValuesInDate(mItem.getId(), fromDateText, toDateText);
        }catch (ParseException ex) {
            line_data = dataSet.getItemValues(mItem.getId(), 10);
        }

        if(line_data!=null) {
            entriesLineChart.clear();

            labelsLineChart.clear();
            int length = line_data.length;
            for (int i=0; i<length; i++) {
                int index = length - i - 1;
                String label = line_data[index].getTimeString();
                entriesLineChart.add(new Entry(i, (Long)line_data[index].getBintvalue()));
                labelsLineChart.add(i,label);
            }
        }
    }

    private void initLineChart() throws Exception{

        List<ILineDataSet> dataSets = new ArrayList<>();
        LineDataSet dataset = new LineDataSet(entriesLineChart, "تراکنش ها");
        dataSets.add(dataset);

        dataset.setColor(ContextCompat.getColor(getActivity(), R.color.blue4));
        dataset.setCircleColor(ContextCompat.getColor(getActivity(), R.color.blue5));
        dataset.setFillColor(ContextCompat.getColor(getActivity(), R.color.blue_green));
        dataset.setDrawFilled(true);

        LineData data = new LineData(dataSets);
        data.setDrawValues(false);

        lineChart.setData(data);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setText(mItem.getDescription());
        lineChart.getAxisLeft().setAxisMinimum(0);
        lineChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labelsLineChart));
        lineChart.setTouchEnabled(true);
        CustomMarkerView mv = new CustomMarkerView (getActivity(), R.layout.marker_view);
        lineChart.setMarkerView(mv);

        // if disabled, scaling can be done on x- and y-axis separately
        lineChart.setPinchZoom(true);
        lineChart.animateY(3000);
    }

    class Task extends AsyncTask<String, Integer, Boolean> {

        @Override
        protected void onPreExecute() {

            try {
                layout.setVisibility(View.VISIBLE);
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
                lineChart.setVisibility(View.VISIBLE);
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
