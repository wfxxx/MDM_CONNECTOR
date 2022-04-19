package com.definesys.mdm.connector.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/8 12:47
 */

@Data
public class ChildTableData {
    private String uuid;
    private List<String> childTableDataId;
}

