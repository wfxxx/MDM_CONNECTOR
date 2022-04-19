package com.definesys.mdm.connector.util;

import com.google.gson.Gson;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/6 13:45
 */

public class GsonUtil {

    public static <T> T toJsonObject(String json,Class<T> classOfT ){
        return new Gson().fromJson(json,classOfT);
    }

    public static String toJsonString(Object src){
        return new Gson().toJson(src);
    }


}
