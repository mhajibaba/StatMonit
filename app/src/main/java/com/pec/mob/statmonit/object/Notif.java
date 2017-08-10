package com.pec.mob.statmonit.object;

import com.google.gson.annotations.SerializedName;
import com.pec.mob.statmonit.util.JalaliCalendar;

public class Notif {

    @SerializedName("AgentID")
    private String agentId;

    @SerializedName("CreationDate")
    private String date;

    @SerializedName("EventID")
    private String eventId;

    @SerializedName("EventLevel")
    private String level;

    @SerializedName("GroupID")
    private String GroupID;

    @SerializedName("ID")
    private String id;

    @SerializedName("ItemID")
    private String itemId;

    @SerializedName("ItemName")
    private String itemName;

    @SerializedName("Media")
    private String media;

    @SerializedName("Message")
    private String message;

    @SerializedName("Title")
    private String title;

    @SerializedName("TriggerID")
    private String triggerId;

    @SerializedName("UserID")
    private String userId;

    @SerializedName("Value")
    private String value;

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getGroupID() {
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getMedia() {
        return media;
    }

    public void setMedia(String media) {
        this.media = media;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(String triggerId) {
        this.triggerId = triggerId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
