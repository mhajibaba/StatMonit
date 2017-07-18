package com.pec.mob.statmonit.util;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.pec.mob.statmonit.object.Agent;
import com.pec.mob.statmonit.object.AgentItem;
import com.pec.mob.statmonit.object.Item;
import com.pec.mob.statmonit.object.Notification;
import com.pec.mob.statmonit.object.ValueType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataSet{
    private static final String TAG = DataSet.class.getSimpleName();
    private final Gson gson = new GsonBuilder().create();
    private final String restUri = "https://report.pec.ir:444/api/agent/";


    public String authenticateAndGetAgentItems(String username, String password) throws Exception{
        try {
            String request = restUri + "getagentitems";
            Log.d(TAG,request);
            String json = Rest.get(request, username, password);
            Log.d(TAG,json);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void getAgentInfo() throws Exception {
        AgentItem[] agentItems;
        try {
            String request = restUri + "getagentitems";
            String json = Rest.get(request);
            Log.d(TAG,json);
            agentItems = gson.fromJson(json, AgentItem[].class);
            if(agentItems!=null) {
                Agent.setItems(agentItems);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Object getItemValue(int itemId, ValueType valueType) throws Exception{
        Object retVal=null;
        try {
            String request = restUri + "getitems?itemid="+itemId+"&count=1";
            String json = Rest.get(request);
            Log.d(TAG,json);
            Item[] item = gson.fromJson(json, Item[].class);
            if(item!=null) {
                if(valueType == ValueType._Integer) {
                    retVal = item[0].getBintvalue();
                }else {
                    retVal = item[0].getNvrvalue();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }


    public List<Object> getItemValues(int itemId, ValueType valueType, int count) throws Exception{
        List<Object> retVal= new ArrayList<>();
        try {
            String request = restUri + "getitems?itemid="+itemId+"&count="+count;
            String json = Rest.get(request);
            Log.d(TAG,json);
            Item[] item = gson.fromJson(json, Item[].class);
            if(item!=null) {
                for(int i=0; i<item.length; i++) {
                    if(valueType == ValueType._Integer) {
                        retVal.add(item[i].getBintvalue());
                    }else {
                        retVal.add(item[i].getNvrvalue());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public List<Item> getItemList(int itemId, int count) throws Exception{
        List<Item> retVal= new ArrayList<>();
        try {
            String request = restUri + "getitems?itemid="+itemId+"&count="+count;
            String json = Rest.get(request);
            Log.d(TAG,json);
            Item[] item = gson.fromJson(json, Item[].class);
            if(item!=null) {
                retVal = Arrays.asList(item);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }

    public Long getItemValue(int itemId) throws Exception{
        Long retVal=null;
        try {
            String request = restUri + "getitems?itemid="+itemId+"&count=1";
            String json = Rest.get(request);
            Log.d(TAG,json);
            Item[] item = gson.fromJson(json, Item[].class);
            if(item!=null) {
                retVal = item[0].getBintvalue();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retVal;
    }


    public Item[] getItemValuesInDate(int itemId, String from, String to) throws Exception{
        Item[] items = null;
        try {
            String request = restUri + "getitemresult?itemid="+itemId+"&datefrom="+from+"&dateto="+to;
            String json = Rest.get(request);
            Log.d(TAG,json);
            items = gson.fromJson(json, Item[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public Item[] getItemValues(int itemId, int count) throws Exception{
        Item[] items = null;
        try {
            String request = restUri + "getitems?itemid="+itemId+"&count="+count;
            String json = Rest.get(request);
            Log.d(TAG,json);
            items = gson.fromJson(json, Item[].class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return items;
    }

    public List<Notification> getNotifications() throws Exception{
        try {
            String request = restUri + "getusernotification?count=10";
            String json = Rest.get(request);
            Log.d(TAG,json);
            return Arrays.asList(gson.fromJson(json, Notification[].class));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
