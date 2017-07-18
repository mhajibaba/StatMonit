package com.pec.mob.statmonit.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Notification;
import com.pec.mob.statmonit.util.DataSet;

import java.util.List;

public class NotificationFragment extends Fragment {
    private static String TAG = NotificationFragment.class.getSimpleName();
    private List<Notification> notifications;

    private RecyclerView mRecyclerView;
    private LinearLayout layout;

    public NotificationFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);
        layout = (LinearLayout) rootView.findViewById(R.id.progressbar_view);
        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.notifications_recycler_view);
        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        new Task().execute();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


    class Task extends AsyncTask<String, Integer, Boolean> {
        @Override
        protected void onPreExecute() {
            try {
                layout.setVisibility(View.VISIBLE);
                mRecyclerView.setVisibility(View.GONE);
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            try {
                layout.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);

                mRecyclerView.setHasFixedSize(true);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(mLayoutManager);
                RecyclerView.Adapter mAdapter = new NotificationRecyclerViewAdapter(notifications);
                mRecyclerView.setAdapter(mAdapter);

                ((NotificationRecyclerViewAdapter) mAdapter).setOnItemClickListener(new NotificationRecyclerViewAdapter
                        .NotificationClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("");
                            builder.setMessage(notifications.get(position).getText());
                            builder.setPositiveButton(getString(R.string.exit), null);
                            AlertDialog dialog = builder.create();
                            dialog.show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

        @Override
        protected Boolean doInBackground(String... params) {
            try {
                if (notifications == null) {
                    DataSet dataSet = new DataSet();
                    notifications = dataSet.getNotifications();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
