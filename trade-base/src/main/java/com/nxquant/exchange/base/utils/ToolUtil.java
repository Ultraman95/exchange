package com.nxquant.exchange.base.utils;

import tools.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.nxquant.exchange.base.entity.JsonType;

public class ToolUtil {

    private static Gson gson = new Gson();

    private static ObjectMapper mapper = new ObjectMapper();

    public static String toJson(Object obj , JsonType jsonType){
        if(jsonType == JsonType.JT_GSON) {
            return gson.toJson(obj);
        }else {
            try {
                return mapper.writeValueAsString(obj);
            }catch (Exception exp){
                return null;
            }
        }
    }
}
