package com.zay.springsecurity.payload.response;

import java.util.Set;

public class LoginResponse {
    private String name;

    private String username;

    private final String jwtToken;

    private String type = "Bearer";

    private Set<String> roles;

    public LoginResponse(String jwtToken) {
        this.jwtToken = jwtToken;
    }

    public LoginResponse(String name, String username, String jwtToken, Set<String> roles) {
        super();
        this.name = name;
        this.username = username;
        this.jwtToken = jwtToken;
        this.roles = roles;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Set<String> getRoles() {
        return roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles;
    }

    public String getToken() {
        return this.jwtToken;
    }
}
