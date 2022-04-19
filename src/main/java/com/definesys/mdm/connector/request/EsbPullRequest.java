package com.definesys.mdm.connector.request;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/6 12:37
 */

@Data
@Builder
public class EsbPullRequest {
    private String time;
}
