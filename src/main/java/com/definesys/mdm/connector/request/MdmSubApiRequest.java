package com.definesys.mdm.connector.request;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/7 15:19
 */
@Data
@Builder
public class MdmSubApiRequest<T> {
    private String formId;
    private String apiManageId;
    private T data;
    private T childData;
    private String primaryKey;
    private String createdId;
    private String updatedId;
}
