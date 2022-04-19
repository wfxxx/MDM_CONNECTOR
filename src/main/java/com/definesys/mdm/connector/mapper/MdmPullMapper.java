package com.definesys.mdm.connector.mapper;

import com.definesys.mdm.connector.entity.MdmPullMap;
import org.springframework.stereotype.Repository;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/6 12:11
 */

@Repository
public interface MdmPullMapper {
    MdmPullMap getLastPullDate(String formId);

    void insertPullMap(String id,String now, String formId);

    void updatePullMap(String now, String formId);
}
