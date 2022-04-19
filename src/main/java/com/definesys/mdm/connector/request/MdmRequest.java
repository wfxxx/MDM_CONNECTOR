package com.definesys.mdm.connector.request;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

/**
 * 第三方请求MDM代理接口的实体类
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/31 12:05
 */

@Data
@Builder
public class MdmRequest<T> {

    private String formId;
    private String sourceId;
    private String apiManageId;
    private String primaryKey;
    private T data;
    private T childData;
    private String createBy;
    private String updateBy;




    @Override
    public String toString() {
        return "MdmRequestBody{" +
                "formId='" + formId + '\'' +
                ", apiManageId='" + apiManageId + '\'' +
                ", data=" + data +
                ", createBy='" + createBy + '\'' +
                '}';
    }
}
