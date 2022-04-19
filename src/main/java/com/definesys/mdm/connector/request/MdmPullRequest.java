package com.definesys.mdm.connector.request;

import lombok.Builder;
import lombok.Value;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/6 12:02
 */

@Value
@Builder
public class MdmPullRequest {
    private String esbUrl;
    private String formId;
    private String apiManageId;
    private String sourceId;
}
