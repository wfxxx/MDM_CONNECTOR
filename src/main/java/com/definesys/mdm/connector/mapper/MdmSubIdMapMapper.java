package com.definesys.mdm.connector.mapper;

import com.definesys.mdm.connector.entity.MdmSubIdMap;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/4/7 15:39
 */

@Repository
public interface MdmSubIdMapMapper {

    /**查询字表映射*/
    List<MdmSubIdMap> getSubIdMap(String formId,String uuId,String esbId,String esbSubId);

    /**写入字表映射*/
    void insertSubIdMap(String id,String formId,String mdmId,String uuId,String mdmSubId,String esbId,String esbSubId);

    /**删除字表id映射*/
    void deleteNotInValues(String fromId, String mdmId, String uuid,List<String> esbSubIds);

}
