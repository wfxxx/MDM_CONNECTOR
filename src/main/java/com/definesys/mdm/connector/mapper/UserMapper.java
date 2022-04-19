package com.definesys.mdm.connector.mapper;

import com.definesys.mdm.connector.entity.User;
import org.springframework.stereotype.Repository;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/29 16:59
 */

@Repository
public interface UserMapper {

    User Sel(String mdmCode);



}
