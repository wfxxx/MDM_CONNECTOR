package com.definesys.mdm.connector.controller;

import com.definesys.mdm.connector.dto.AdAutho;
import com.definesys.mdm.connector.request.MdmPullRequest;
import com.definesys.mdm.connector.request.MdmRequest;
import com.definesys.mdm.connector.service.MdmServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/29 16:27
 */

@Slf4j
@RestController
@RequestMapping("/demdm/connector")
public class MdmServiceController {

    @Autowired
    private MdmServiceService mdmServiceService;

    /**
     * 新增或更新接口
     * @param mdmRequest
     * @return
     */
    @RequestMapping(value = "/SaveUpdateApi",produces = "application/json;charset=UTF-8")
    public String saveUpdateApi(@RequestBody MdmRequest mdmRequest) throws IOException {
        return mdmServiceService.saveUpdateApi(mdmRequest);
    }

    /**
     * 新增修改字表接口（字表全删在写入）
     * @param mdmRequest
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/SaveUpdateWithSubApi",produces = "application/json;charset=UTF-8")
    public String saveUpdateWithSubApi(@RequestBody MdmRequest mdmRequest) throws IOException {
        return mdmServiceService.saveUpdateWithSubApi(mdmRequest);
    }

    /**
     * 新增修改字表接口(单独更新，新增，删除)
     * @param mdmRequest
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/SaveUpdateWithSubApi2",produces = "application/json;charset=UTF-8")
    public String saveUpdateWithSubApi2(@RequestBody MdmRequest mdmRequest) throws IOException {
        return mdmServiceService.saveUpdateWithSubApi2(mdmRequest);
    }

    /**
     * 通用拉取接口
     * @param mdmPullRequest
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/pull_common_data",produces = "application/json;charset=UTF-8")
    public String pullCommonData(@RequestBody MdmPullRequest mdmPullRequest) throws IOException {
        return mdmServiceService.pullCommonData(mdmPullRequest);
    }

    /**
     * 通用拉取带子表接口
     * @param mdmPullRequest
     * @return
     * @throws IOException
     */
    @RequestMapping(value = "/pull_common_sub_data",produces = "application/json;charset=UTF-8")
    public String pullCommonSubData(@RequestBody MdmPullRequest mdmPullRequest) throws IOException {
        return mdmServiceService.pullCommonSubData(mdmPullRequest);
    }




    @RequestMapping(value = "/GetUserInfoByMDMCode",produces = "application/json;charset=UTF-8")
    public String GetUser(@RequestParam String mdmCode) throws IOException {
        log.info(mdmCode);
        String uri = mdmServiceService.getUser(mdmCode);
        return uri;
    }

    @RequestMapping(value = "/adLogin",produces = "application/json;charset=UTF-8")
    public String adLogin(@RequestBody AdAutho autho){
        return mdmServiceService.adLogin(autho.getLoginName(),autho.getPassword());
    }





}
