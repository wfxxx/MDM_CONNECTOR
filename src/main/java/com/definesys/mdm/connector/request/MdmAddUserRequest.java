package com.definesys.mdm.connector.request;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/2 16:49
 */

@Data
@Builder
public class MdmAddUserRequest {
    private String loginName;
    private String userId;
    private String password;
    private String phone;
    private String email;

}
