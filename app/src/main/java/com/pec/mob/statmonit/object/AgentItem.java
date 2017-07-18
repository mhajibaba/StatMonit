package com.pec.mob.statmonit.object;

import com.google.gson.annotations.SerializedName;

public class AgentItem {
    @SerializedName("itemid") private int id;
    @SerializedName("charttype") private int chartType;
    @SerializedName("interval") private int interval;
    @SerializedName("Description") private String description;
    @SerializedName("Name") private String name;

    public AgentItem(int id, int chartType, int interval, String description, String name) {
        this.id = id;
        this.chartType = chartType;
        this.interval = interval;
        this.description = description;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int itemId) {
        this.id = itemId;
    }

    public int getChartType() {
        return chartType;
    }

    public void setChartType(int chartType) {
        this.chartType = chartType;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return description;
    }
}
