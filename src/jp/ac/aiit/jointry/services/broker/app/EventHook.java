/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.ac.aiit.jointry.services.broker.app;

import java.util.HashMap;
import java.util.Map;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;

public abstract class EventHook implements JointryCommon {

    private Map<EventType, EventHandler> eventMap = new HashMap();

    protected void putHookEvent(EventType event, EventHandler listener) {
        eventMap.put(event, listener);
    }

    protected EventHandler getHookEvent(EventType event) {
        return eventMap.get(event);
    }

    protected void callHandle(Event event, EventHandler handler) {
        if (handler != null) handler.handle(event);
    }

    public abstract void enableHook();
    public abstract void sendMessage(int event);
}
