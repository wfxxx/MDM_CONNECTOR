package com.definesys.mdm.connector.mapper;


import com.definesys.mdm.connector.entity.MdmIdMap;
import org.springframework.stereotype.Repository;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/31 12:29
 */
@Repository
public interface MdmIdMapMapper {

    MdmIdMap getMdmId(String formId, String esbId);

    void insertMdmId(String Id,String formId, String mdmId, String esbId);

}
