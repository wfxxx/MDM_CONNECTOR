package com.definesys.mdm.connector.util;

import cn.hutool.core.codec.Base64Encoder;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/6 12:04
 */

@Data
@PropertySource("classpath:application.yml")
@Service
public class EsbUtil {

    public static String esbUri;

    private static String user;

    private static String pass;
    @Value(value = "${esb.uri}")
    public void setEsbUri(String esbUri) {
        EsbUtil.esbUri = esbUri;
    }
    @Value(value = "${esb.user}")
    public void setUser(String user) { EsbUtil.user = user; }
    @Value(value = "${esb.pass}")
    public void setPass(String pass) { EsbUtil.pass = pass; }

    public static Map<String,String> getEsbHeader(){
        Map<String,String> map = new HashMap<>();
        map.put("Authorization","Basic "+Base64Encoder.encode(user+":"+pass));
        return map;
    }


}
