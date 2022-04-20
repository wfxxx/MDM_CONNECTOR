package com.definesys.mdm.connector.request;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/20 13:33
 */

@Data
@Builder
public class MdmEnableDisableRequest {
    private String formId;
    private String apiManageId;
    private String documentId;
    private String status;


}
