package com.project.smashlink.notification.observer;

import com.project.smashlink.notification.event.UrlEvent;

public interface UrlEventObserver {
    void onEvent(UrlEvent event);
}
