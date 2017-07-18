package com.pec.mob.statmonit.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class AgentItemContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<AgentItem> ITEMS = new ArrayList<AgentItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<Integer, AgentItem> ITEM_MAP = new HashMap<>();

    private static final int COUNT = Agent.getItems().length;

    static AgentItem[] agents;
    static {

        agents = Agent.getItems().clone();
        // Add some sample items.
        for (AgentItem item: agents) {
            addItem(item);
        }
    }

    private static void addItem(AgentItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.getId(), item);
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        builder.append("\n"+Agent.getItems()[position].toString());

        return builder.toString();
    }
}
