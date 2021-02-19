package com.twt.dabao.sample;

import com.twt.dabao.mapping.RouterMapping_123;

import java.util.HashMap;
import java.util.Map;

public class RouterMapping {
    public static Map<String,String> get(){
        Map<String,String> map=new HashMap<>();

        map.putAll(RouterMapping_123.get());

        return map;
    }
}
