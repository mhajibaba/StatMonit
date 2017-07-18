package com.pec.mob.statmonit.object;

import java.util.Arrays;

public class Agent {

    private static AgentItem[] items;

    public static AgentItem[] getItems() {
        return items;
    }

    public static void setItems(AgentItem[] itemList) {
        items = itemList;
    }

    public static int getItemidForChart(ChartType charts) {
        for (AgentItem item:Arrays.asList(items)) {
            if (item.getChartType()==charts.getId()) {
                return item.getId();
            }
        }
        return -1;
    }
}
