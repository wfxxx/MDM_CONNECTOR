package com.definesys.mdm.connector.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/8 9:51
 */

@Data
@Builder
public class AdAutho {
    private String loginName;
    private String password;

}
