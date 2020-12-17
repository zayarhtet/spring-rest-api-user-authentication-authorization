package com.zay.springsecurity.util;

import com.zay.springsecurity.model.User;
import org.springframework.context.ApplicationEvent;

public class OnCreateAccountEvent extends ApplicationEvent {
    private String appUrl;
    private User user;

    public OnCreateAccountEvent(User user, String appUrl) {
        super(user);

        this.user=user;
        this.appUrl=appUrl;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public User getUser() {
        return user;
    }
}
