package com.definesys.mdm.connector.vo;

import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/6 13:11
 */
@Data
public class EsbRtnVo<T> {
    private String rtnCode;
    private String rtnMsg;
    private T rtnData;

    public boolean isOkRtn(){
        return "S".equals(this.rtnCode)? true:false;
    }
}
