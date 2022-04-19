package com.definesys.mdm.connector.request;

import lombok.*;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/31 13:44
 */

@Data
@Builder
public class MdmUpdateRequest <T> {

    private String formId;
    private String apiManageId;
    private T data;
    private String primaryKey;
    private String updatedId;

}
