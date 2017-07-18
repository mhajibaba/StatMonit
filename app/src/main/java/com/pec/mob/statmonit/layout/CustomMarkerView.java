package com.pec.mob.statmonit.layout;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.pec.mob.statmonit.R;

public class CustomMarkerView extends MarkerView {

    private TextView tvContent;
    private View layout;
    private int xOffset=0;

    public CustomMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);
        // this markerview only displays a textview
        tvContent = (TextView) findViewById(R.id.tvContent);
        layout = findViewById(R.id.markerLayout);
    }

    // callbacks every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        tvContent.setText(String.format("%d", (long)e.getY())); // set the entry-value as the display text

        if(e.getX()>5) {
            xOffset = -getWidth()+5;
            layout.setBackgroundResource(R.drawable.ic_mode_comment_black_24dp);
        }else {
            xOffset = -5;
            layout.setBackgroundResource(R.drawable.ic_chat_bubble_black_24dp);
        }
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(xOffset,-getHeight());
    }
}