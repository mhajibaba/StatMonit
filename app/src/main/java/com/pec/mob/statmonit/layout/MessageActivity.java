package com.pec.mob.statmonit.layout;

import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;

import com.pec.mob.statmonit.R;
import com.pec.mob.statmonit.object.Message;
import com.pec.mob.statmonit.util.DataSet;

import java.util.List;

public class MessageActivity extends AppCompatActivity {

    private static String TAG = NotificationFragment.class.getSimpleName();
    final Context context = this;

    private List<Message> messages;

    private RecyclerView mRecyclerView;
    private LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        layout = (LinearLayout) findViewById(R.id.progressbar_view);
        mRecyclerView = (RecyclerView) findViewById(R.id.messages_recycler_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        new Task().execute();
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
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                mRecyclerView.setLayoutManager(mLayoutManager);
                RecyclerView.Adapter mAdapter = new MessageRecyclerViewAdapter(messages);
                mRecyclerView.setAdapter(mAdapter);

                ((MessageRecyclerViewAdapter) mAdapter).setOnItemClickListener(new MessageRecyclerViewAdapter
                        .NotificationClickListener() {
                    @Override
                    public void onItemClick(int position, View v) {

                        try {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("");
                            builder.setMessage(messages.get(position).getText());
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
                if (messages == null) {
                    DataSet dataSet = new DataSet();
                    messages = dataSet.getMessages();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
