package com.definesys.mdm.connector.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/31 13:37
 */

@Data
@Builder
public class MdmInsertDto<T> {
    private String formId;
    private String apiManageId;
    private T data;
    private String createdId;

}
