package com.blade.mvc.http;

import com.blade.kit.CollectionKit;

import java.util.Map;

/**
 * @author biezhi
 *         2017/6/3
 */
public class SessionManager {

    private Map<String, Session> sessionMap;

    public SessionManager() {
        this.sessionMap = CollectionKit.newConcurrentMap();
    }

    public Map<String, Session> getSessions() {
        return sessionMap;
    }

    public Session getSession(String id) {
        return sessionMap.get(id);
    }

    public void addSession(Session session) {
        sessionMap.put(session.id(), session);
    }

    public void clear() {
        sessionMap.clear();
    }

    public void remove(Session session) {
        sessionMap.remove(session.id());
    }
}
