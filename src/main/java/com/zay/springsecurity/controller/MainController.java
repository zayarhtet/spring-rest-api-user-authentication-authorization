package com.zay.springsecurity.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin
public class MainController {
    @Value("${app.version}")
    private String appVersion;

    @GetMapping
    public Map<String,String> getInfo(){
        Map<String,String> info= new HashMap<>();
        info.put("name","Securing Rest API with JSON WEB TOKEN");
        info.put("description","A raw backend rest api and powered by Zay");
        info.put("version",appVersion);
        return info;
    }
}
