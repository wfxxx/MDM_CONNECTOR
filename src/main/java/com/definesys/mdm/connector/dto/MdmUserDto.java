package com.definesys.mdm.connector.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/1 15:24
 */

@Data
@Builder
public class MdmUserDto {

    private String id;
    private String loginName;
    private String userId;
    private String password;
    private String phone;
    private String email;
    private String enableStatus;
    private String userName;
    private String headPortrait;
    private String mdmCode;

}
