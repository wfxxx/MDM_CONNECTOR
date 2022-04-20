package com.definesys.mdm.connector.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.map.MapUtil;
import com.definesys.mdm.connector.dto.*;
import com.definesys.mdm.connector.entity.MdmIdMap;
import com.definesys.mdm.connector.entity.MdmPullMap;
import com.definesys.mdm.connector.entity.MdmSubIdMap;
import com.definesys.mdm.connector.entity.User;
import com.definesys.mdm.connector.mapper.MdmIdMapMapper;
import com.definesys.mdm.connector.mapper.MdmPullMapper;
import com.definesys.mdm.connector.mapper.MdmSubIdMapMapper;
import com.definesys.mdm.connector.mapper.UserMapper;
import com.definesys.mdm.connector.request.*;
import com.definesys.mdm.connector.util.*;
import com.definesys.mdm.connector.vo.EsbRtnVo;
import com.definesys.mdm.connector.vo.MdmChildVo;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/29 16:58
 */


@Service
@Slf4j
public class MdmServiceService {

    private static final String pullDate = "1970-01-01 00:00:00";


    @Autowired
    private UserMapper userMapper;
    @Autowired
    private MdmIdMapMapper idMapMapper;
    @Autowired
    private MdmPullMapper pullMapper;
    @Autowired
    private MdmSubIdMapMapper subIdMapMapper;


    public User Sel(String mdmCode){

        return userMapper.Sel(mdmCode);
    }

    public String getUser(String mdmCode){
        String UserId = MdmAuthUtil.getUserIdByUserCode(mdmCode);
        MdmChildVo mdmVo = new MdmChildVo();
        mdmVo.setCode("ok");
        mdmVo.setMessgae("成功");
        Map<String,String> rtnObject = new HashMap<String, String>();
        rtnObject.put("id",UserId);
        mdmVo.setData(rtnObject);
        return new Gson().toJson(mdmVo);
    }

    /**
     * 新增更新接口
     * @param mdmRequest
     * @return
     */
    public String saveUpdateApi(MdmRequest mdmRequest) throws IOException {
        String rtnString = "";
        //根据第三方ID查询MDM_ID
        MdmIdMap mdmIdMap = idMapMapper.getMdmId(mdmRequest.getFormId(), mdmRequest.getSourceId());
        if(mdmIdMap == null || mdmIdMap.getMdmid() == null){
            //新增
            String insertId = MdmAuthUtil.getUserIdByUserCode(mdmRequest.getCreateBy());
            MdmInsertDto insertDto = MdmInsertDto.builder().formId(mdmRequest.getFormId()).apiManageId(mdmRequest.getApiManageId()).data(mdmRequest.getData()).createdId(insertId).build();
            rtnString = HttpUtil.sendPostDataByJson(MdmAuthUtil.getSaveApi(), MdmAuthUtil.getMdmRequestHeader(), new Gson().toJson(insertDto));
            MdmChildVo mdmRtnDto = new Gson().fromJson(rtnString, MdmChildVo.class);
            if(!mdmRtnDto.isRtnOk()){
                return rtnString;
            }
            //反写MdmId至数据库
            String mdmId = (String) mdmRtnDto.getData();
            System.out.println("MdmId："+mdmId);
            idMapMapper.insertMdmId(StringUtil.getUuid(),mdmRequest.getFormId(),mdmId,mdmRequest.getSourceId());
            return rtnString;
        }else{
            //修改
            //根据工号获取主数据ID
            String updateId = MdmAuthUtil.getUserIdByUserCode(mdmRequest.getUpdateBy());
            //获取中间映射表的主数据ID
            String primaryKey = mdmIdMap.getMdmid();
            MdmUpdateRequest updateDto = MdmUpdateRequest.builder().formId(mdmRequest.getFormId()).apiManageId(mdmRequest.getApiManageId()).primaryKey(primaryKey).data(mdmRequest.getData()).updatedId(updateId).build();
            rtnString = HttpUtil.sendPostDataByJson(MdmAuthUtil.getUpdateApi(), MdmAuthUtil.getMdmRequestHeader(), new Gson().toJson(updateDto));
        }

        return rtnString;
    }

    /**
     *  新增更新带子表接口(字表数据全删，再写入)
     * @param mdmRequest
     * @return
     */
    public String saveUpdateWithSubApi(MdmRequest mdmRequest) throws IOException {
        String rtnString = GsonUtil.toJsonString(new MdmChildVo().getErrObj("未知错误"));
        if(mdmRequest.getSourceId() != null && !mdmRequest.getSourceId().isEmpty()) {
            //根据第三方ID查询MDM_ID
            MdmIdMap mdmIdMap = idMapMapper.getMdmId(mdmRequest.getFormId(), mdmRequest.getSourceId());
            if (mdmIdMap == null || mdmIdMap.getMdmid() == null) {
                //新增
                // 1.获取子表ESB_SUB_ID集合
                List<Map<String, Object>> esbSubIdMapList = getEsbSubIdMapList(mdmRequest, true).get("esbSubIdMapList");
                log.info("esbSubIdMapList"+esbSubIdMapList);
                MdmSubApiRequest subApiDto = MdmSubApiRequest.builder().formId(mdmRequest.getFormId()).apiManageId(mdmRequest.getApiManageId())
                        .createdId(MdmAuthUtil.getUserIdByUserCode(mdmRequest.getCreateBy())).data(mdmRequest.getData()).childData(mdmRequest.getChildData()).build();
                String mdmSaveApiRtnStr = HttpUtil.sendPostDataByJson(MdmAuthUtil.getSaveApi(), MdmAuthUtil.getMdmRequestHeader(), GsonUtil.toJsonString(subApiDto));
                //调用写入接口返回
                MdmChildVo mdmRtnDto = GsonUtil.toJsonObject(mdmSaveApiRtnStr, MdmChildVo.class);
                if(mdmRtnDto.isRtnOk()){
                    String mdmId = "";
                    //保存主表映射
                    if(mdmRtnDto.getData()!= null){
                        mdmId = (String) mdmRtnDto.getData();
                        idMapMapper.insertMdmId(StringUtil.getUuid(),mdmRequest.getFormId(),mdmId,mdmRequest.getSourceId());
                    }
                    //反写MDMID
                    List<Map<String, Object>> mdmSubIdMapList = getMdmSubIdMapList(mdmRtnDto);
                    log.info("mdmSubIdMapList"+mdmSubIdMapList);
                    for (int i = 0; i < esbSubIdMapList.size(); i++) {
                        Map<String, Object> esbSubIdMap = esbSubIdMapList.get(i);
                        Map<String, Object> mdmSubIdMap = mdmSubIdMapList.get(i);
                        subIdMapMapper.insertSubIdMap(StringUtil.getUuid(),mdmRequest.getFormId(),mdmId,MapUtil.getStr(esbSubIdMap, "uuid"),MapUtil.getStr(mdmSubIdMap, "mdmSubId"),mdmRequest.getSourceId(),MapUtil.getStr(esbSubIdMap, "esbSubId"));
                    }
                }
                return mdmSaveApiRtnStr;
            } else {
                //修改(修改数据的主数据ID没有，所以主数据将原有数据全部删除再新增，故没有保存主数据返回的MDM_ID)
                MdmSubApiRequest subApiDto = MdmSubApiRequest.builder().formId(mdmRequest.getFormId()).apiManageId(mdmRequest.getApiManageId()).primaryKey(mdmIdMap.getMdmid())
                        .updatedId(MdmAuthUtil.getUserIdByUserCode(mdmRequest.getUpdateBy())).data(mdmRequest.getData()).childData(mdmRequest.getChildData()).build();
                String mdmSaveApiRtnStr = HttpUtil.sendPostDataByJson(MdmAuthUtil.getUpdateApi(), MdmAuthUtil.getMdmRequestHeader(), GsonUtil.toJsonString(subApiDto));
                //调用写入接口返回
                rtnString = mdmSaveApiRtnStr;

            }
        }
        return rtnString;
    }

    /**
     * Todo
     * 新增更新带子表接口(做更新)
     * @param mdmRequest
     * @return
     * @throws IOException
     */
    public String saveUpdateWithSubApi2(MdmRequest mdmRequest) throws IOException {
        String rtnString = GsonUtil.toJsonString(MdmChildVo.getErrObj("未知错误"));
        if(mdmRequest.getSourceId() != null && !mdmRequest.getSourceId().isEmpty()) {
            //根据第三方ID查询MDM_ID
            MdmIdMap mdmIdMap = idMapMapper.getMdmId(mdmRequest.getFormId(), mdmRequest.getSourceId());
            if (mdmIdMap == null || mdmIdMap.getMdmid() == null) {
                //新增
                // 1.获取子表ESB_SUB_ID集合
                List<Map<String, Object>> esbSubIdMapList = getEsbSubIdMapList(mdmRequest, true).get("esbSubIdMapList");
                log.info("esbSubIdMapList"+esbSubIdMapList);
                MdmSubApiRequest subApiDto = MdmSubApiRequest.builder().formId(mdmRequest.getFormId()).apiManageId(mdmRequest.getApiManageId())
                        .createdId(MdmAuthUtil.getUserIdByUserCode(mdmRequest.getCreateBy())).data(mdmRequest.getData()).childData(mdmRequest.getChildData()).build();
                String mdmSaveApiRtnStr = HttpUtil.sendPostDataByJson(MdmAuthUtil.getSaveApi(), MdmAuthUtil.getMdmRequestHeader(), GsonUtil.toJsonString(subApiDto));
                //调用写入接口返回
                MdmChildVo mdmRtnDto = GsonUtil.toJsonObject(mdmSaveApiRtnStr, MdmChildVo.class);
                if(mdmRtnDto.isRtnOk()){
                    String mdmId = "";
                    //保存主表映射
                    if(mdmRtnDto.getData()!= null){
                        mdmId = (String) mdmRtnDto.getData();
                        idMapMapper.insertMdmId(StringUtil.getUuid(),mdmRequest.getFormId(),mdmId,mdmRequest.getSourceId());
                    }
                    //反写MDMID
                    List<Map<String, Object>> mdmSubIdMapList = getMdmSubIdMapList(mdmRtnDto);
                    log.info("mdmSubIdMapList"+mdmSubIdMapList);
                    for (int i = 0; i < esbSubIdMapList.size(); i++) {
                        Map<String, Object> esbSubIdMap = esbSubIdMapList.get(i);
                        Map<String, Object> mdmSubIdMap = mdmSubIdMapList.get(i);
                        subIdMapMapper.insertSubIdMap(StringUtil.getUuid(),mdmRequest.getFormId(),mdmId,MapUtil.getStr(esbSubIdMap, "uuid"),MapUtil.getStr(mdmSubIdMap, "mdmSubId"),mdmRequest.getSourceId(),MapUtil.getStr(esbSubIdMap, "esbSubId"));
                    }
                }
                return mdmSaveApiRtnStr;
            } else {
                //修改
                Map<String, List<Map<String, Object>>> stringListMap = getEsbSubIdMapList(mdmRequest, false);
                //获取ESB子表数组
                List<Map<String, Object>> esbSubIdMapList = stringListMap.get("esbSubIdMapList");
                //需要删除的字表ID
                List<Map<String, Object>> notDeleteSubIdMapList = stringListMap.get("notDeleteSubIdMapList");
                //不需要新增的字表ID
                List<Map<String, Object>> filterRtnSubIdMapList = stringListMap.get("filterRtnSubIdMapList");
                MdmSubApiRequest subApiDto = MdmSubApiRequest.builder().formId(mdmRequest.getFormId()).apiManageId(mdmRequest.getApiManageId()).primaryKey(mdmIdMap.getMdmid())
                        .updatedId(MdmAuthUtil.getUserIdByUserCode(mdmRequest.getUpdateBy())).data(mdmRequest.getData()).childData(mdmRequest.getChildData()).build();
                String mdmSaveApiRtnStr = HttpUtil.sendPostDataByJson(MdmAuthUtil.getUpdateApi(), MdmAuthUtil.getMdmRequestHeader(), GsonUtil.toJsonString(subApiDto));
                //调用写入接口返回
                rtnString = mdmSaveApiRtnStr;
                MdmChildVo mdmRtnDto = GsonUtil.toJsonObject(mdmSaveApiRtnStr, MdmChildVo.class);
                if(mdmRtnDto.isRtnOk()){
                    String mdmId = "";
                    //保存主表映射
                    if(mdmRtnDto.getData()!= null){
                        mdmId = (String) mdmRtnDto.getData();
                    }
                    //反写MDMID
                    List<Map<String, Object>> mdmSubIdMapList = getMdmSubIdMapList(mdmRtnDto);
                    filterMdmSubIdMapList(filterRtnSubIdMapList,mdmSubIdMapList,esbSubIdMapList);
                    System.out.println("过滤后的新增的主数据："+mdmSubIdMapList);

                    System.out.println("esbSubIdMapList："+esbSubIdMapList);
                    for (int i = 0; i < esbSubIdMapList.size(); i++) {
                        Map<String, Object> esbSubIdMap = esbSubIdMapList.get(i);
                        Map<String, Object> mdmSubIdMap = mdmSubIdMapList.get(i);
                        subIdMapMapper.insertSubIdMap(StringUtil.getUuid(),mdmRequest.getFormId(),mdmId,MapUtil.getStr(esbSubIdMap, "uuid"),MapUtil.getStr(mdmSubIdMap, "mdmSubId"),mdmRequest.getSourceId(),MapUtil.getStr(esbSubIdMap, "esbSubId"));
                    }
                    //整理不需要删除的uuid和esbSubId
                    System.out.println(notDeleteSubIdMapList);
                    Map<String,List<String>> deleteMap = getDeletMapByList(notDeleteSubIdMapList);
                    System.out.println("不需要删除的字表信息："+deleteMap);
                    Iterator<String> iterator = deleteMap.keySet().iterator();
                    while (iterator.hasNext()){
                        String key = iterator.next();
                        List<String> value = deleteMap.get(key);
                        subIdMapMapper.deleteNotInValues(mdmRequest.getFormId(),mdmId,key,value);
                    }
                }
            }
        }
        return rtnString;
    }

    /**
     * 根据一对一的关系建立成一对多的关系(saveUpdateWithSubApi2使用)
     * @param notDeleteSubIdMapList
     * @return
     */
    private Map<String, List<String>> getDeletMapByList(List<Map<String ,Object>> notDeleteSubIdMapList) {
        Map<String, List<String>> map = new HashMap<String, List<String>>(10);
        for (Map<String,Object> notDeleteMap : notDeleteSubIdMapList){
            String esbSubId = notDeleteMap.get("esbSubId").toString();
            String uuid = notDeleteMap.get("uuid").toString();
            if(map.containsKey(uuid)){
                //追加esbSubId
                List<String> list = map.get(uuid);
                list.add(list.size(),esbSubId);
                map.put(uuid,list);
                continue;
            }
            List<String> list = new ArrayList<>(10);
            list.add(0,esbSubId);
            map.put(uuid,list);
        }
        return map;
    }

    /**
     * 将主数据返回的集合过滤不需要更新的uuid和MDM字表ID，还有ESB的ID集合也要过滤
     * @param filterRtnSubIdMapList
     * @param mdmSubIdMapList
     */
    private void filterMdmSubIdMapList(List<Map<String, Object>> filterRtnSubIdMapList, List<Map<String, Object>> mdmSubIdMapList, List<Map<String, Object>> esbSubIdMapList) {
        System.out.println(filterRtnSubIdMapList);
        System.out.println(mdmSubIdMapList);
        System.out.println(esbSubIdMapList);
        List<Map<String, Object>> newMdmSubIdMapList = ObjectMapUtils.copyListMap(mdmSubIdMapList);
        List<Map<String, Object>> newFilterRtnSubIdMapList = ObjectMapUtils.copyListMap(filterRtnSubIdMapList);
        int deletIndex = 0;
        //遍历主数据返回的信息
        for (int i = 0 ; i < newMdmSubIdMapList.size(); i++ ){
            Map<String, Object> mdmRtnMap = newMdmSubIdMapList.get(i);
            //遍历需要过滤的条件
            for (Map<String, Object> map : newFilterRtnSubIdMapList){
                //主数据返回信息需要过滤的
                if (Objects.equals(mdmRtnMap.get("mdmSubId"),map.get("mdmSubId")) && Objects.equals(mdmRtnMap.get("uuid"),map.get("uuid"))){
                    mdmSubIdMapList.remove(i-deletIndex);
                    //过滤ESB的ID
                    esbSubIdMapList.remove(i-deletIndex);
                    deletIndex++;
                    break;
                }
            }
        }
    }

    /**
     * mdm拉取不带字表接口
     * @param mdmPullRequest
     * @return
     */
    public String pullCommonData(MdmPullRequest mdmPullRequest) throws IOException {
        //定义主数据返回对象
        String mdmRtnDtoJson = "";
        MdmChildVo mdmRtnDto = new MdmChildVo();
        //获取上次拉取时间
        String lastPullDate = getLastPullDate(mdmPullRequest.getFormId());

        EsbPullRequest esbPullDto = EsbPullRequest.builder().time(lastPullDate).build();
        String pullString = new Gson().toJson(esbPullDto);
        //调用ESB拉取接口
        String EsbRtn = HttpUtil.sendPostDataByJson(EsbUtil.esbUri + mdmPullRequest.getEsbUrl(), EsbUtil.getEsbHeader(), pullString);
        EsbRtnVo esbRtnDto = new Gson().fromJson(EsbRtn, EsbRtnVo.class);
        if(esbRtnDto.isOkRtn()){
            //esb返回成功
            List listdata = (List) esbRtnDto.getRtnData();
            //循环写入主数据
            for (Object obj : listdata){
                MdmRequest requestBodyDto = MdmRequest.builder().formId(mdmPullRequest.getFormId()).apiManageId(mdmPullRequest.getApiManageId()).sourceId(mdmPullRequest.getSourceId()).data(obj).build();
                MdmChildVo mdmRtn = GsonUtil.toJsonObject(saveUpdateApi(requestBodyDto), MdmChildVo.class);
                if("error".equals(mdmRtn.getCode())){
                    throw new MdmHttpExceprion("调用主数据接口异常");
                }
            }
            //写入主数据成功，反写当前时间
            if(pullDateIsNotEmpty(mdmPullRequest.getFormId())){
                pullMapper.updatePullMap(DateUtil.now(),mdmPullRequest.getFormId());
            }
            else{
                pullMapper.insertPullMap(StringUtil.getUuid(),DateUtil.now(),mdmPullRequest.getFormId());
            }
            mdmRtnDto.setOkRtn("拉取数据成功");
            return GsonUtil.toJsonString(mdmRtnDto);
        }
        //将ESB返回的异常信息返回给主数据
        mdmRtnDto.setErroRtn(esbRtnDto.getRtnMsg());
        mdmRtnDtoJson = new Gson().toJson(mdmRtnDto);
        return mdmRtnDtoJson;
    }

    /**
     * MDM拉取带子表接口
     * @param mdmPullRequest
     * @return
     */
    public String pullCommonSubData(MdmPullRequest mdmPullRequest) throws IOException {
        //定义主数据返回对象
        String mdmRtnDtoJson = "";
        MdmChildVo mdmRtnDto = new MdmChildVo();
        //获取上次拉取时间
        String lastPullDate = getLastPullDate(mdmPullRequest.getFormId());
        //拼装ESB请求
        EsbPullRequest esbPullRequest = EsbPullRequest.builder().time(lastPullDate).build();
        String pullString = new Gson().toJson(esbPullRequest);
        //调用ESB拉取接口
        String EsbRtn = HttpUtil.sendPostDataByJson(EsbUtil.esbUri + mdmPullRequest.getEsbUrl(), EsbUtil.getEsbHeader(), pullString);
        EsbRtnVo esbRtnDto = new Gson().fromJson(EsbRtn, EsbRtnVo.class);
        if(esbRtnDto.isOkRtn()){
            //esb返回成功
            List listdata = (List) esbRtnDto.getRtnData();
            //循环写入主数据
            for (Object obj : listdata){
                Map childData = MapUtil.getAny((Map) obj,"rtnChildData");
                String createBy = MapUtil.getStr((Map) obj,"createBy");
                String updateBy = MapUtil.getStr((Map) obj,"updateBy");
                String sourceId = MapUtil.getStr((Map) obj,mdmPullRequest.getSourceId());
                MdmRequest requestBodyDto = MdmRequest.builder().formId(mdmPullRequest.getFormId()).apiManageId(mdmPullRequest.getApiManageId()).sourceId(sourceId).createBy(createBy).updateBy(updateBy).data(obj).childData(childData).build();
                MdmChildVo mdmRtn = GsonUtil.toJsonObject(saveUpdateWithSubApi(requestBodyDto), MdmChildVo.class);
                if(!mdmRtn.isRtnOk()){
                    throw new MdmHttpExceprion("调用主数据接口异常");
                }
            }
            //写入主数据成功，反写当前时间
            if(pullDateIsNotEmpty(mdmPullRequest.getFormId())){
                pullMapper.updatePullMap(DateUtil.now(),mdmPullRequest.getFormId());
            }
            else{
                pullMapper.insertPullMap(StringUtil.getUuid(),DateUtil.now(),mdmPullRequest.getFormId());
            }
            mdmRtnDto.setOkRtn("拉取数据成功");
            return GsonUtil.toJsonString(mdmRtnDto);
        }
        //将ESB返回的异常信息返回给主数据
        mdmRtnDto.setErroRtn(esbRtnDto.getRtnMsg());
        mdmRtnDtoJson = new Gson().toJson(mdmRtnDto);
        return mdmRtnDtoJson;
    }

    /**
     * MDM拉取带子表接口，子表不为全删除
     * @param mdmPullRequest
     * @return
     */
    public String pullCommonSubData2(MdmPullRequest mdmPullRequest) throws IOException {
        //定义主数据返回对象
        String mdmRtnDtoJson = "";
        //上次拉取时间
        String lastPullDateString = getLastPullDate(mdmPullRequest.getFormId());
        EsbPullRequest esbPullRequest = EsbPullRequest.builder().time(lastPullDateString).build();
        //调用ESB接口
        String esbRtnString = HttpUtil.sendPostDataByJson(EsbUtil.esbUri + mdmPullRequest.getEsbUrl(), EsbUtil.getEsbHeader(), GsonUtil.toJsonString(esbPullRequest));
        //处理ESB返回
        EsbRtnVo esbRtnVo = GsonUtil.toJsonObject(esbRtnString,EsbRtnVo.class);
        if(esbRtnVo.isOkRtn()){
            //查询成功，循环rtnData
            List listdata = (List) esbRtnVo.getRtnData();
            for(Object obj: listdata){
                Map childData = (Map) MapUtil.getAny((Map) obj,"rtnChildData").get("rtnChildData");
                String createBy = MapUtil.getStr((Map) obj,"createBy");
                String updateBy = MapUtil.getStr((Map) obj,"updateBy");
                String sourceId = MapUtil.getStr((Map) obj,mdmPullRequest.getSourceId());
                System.out.println(childData);
                deleteKeyOfMap((Map) obj,"rtnChildData");
                //循环请求子表
                MdmRequest mdmRequest = MdmRequest.builder().formId(mdmPullRequest.getFormId()).apiManageId(mdmPullRequest.getApiManageId()).sourceId(sourceId).createBy(createBy).updateBy(updateBy).data(obj).childData(childData).build();
                String s = saveUpdateWithSubApi2(mdmRequest);
                MdmChildVo mdmChildVo = GsonUtil.toJsonObject(s,MdmChildVo.class);
                if(!mdmChildVo.isRtnOk()){
                    throw new MdmHttpExceprion("调用主数据接口异常");
                }
            }
            //写入主数据成功，反写当前时间
            if(pullDateIsNotEmpty(mdmPullRequest.getFormId())){
                pullMapper.updatePullMap(DateUtil.now(),mdmPullRequest.getFormId());
            }
            else{
                pullMapper.insertPullMap(StringUtil.getUuid(),DateUtil.now(),mdmPullRequest.getFormId());
            }
            MdmChildVo ok = MdmChildVo.getOkObj("拉取数据成功");
            return GsonUtil.toJsonString(ok);
        }
        //返回异常信息
        mdmRtnDtoJson = GsonUtil.toJsonString(MdmChildVo.getErrObj(esbRtnVo.getRtnMsg()));
        return mdmRtnDtoJson;
    }

    /**
     * 获取主数据拉取时间
     * @param formId
     * @return
     */
    private String getLastPullDate(String formId){
        //获取上一次拉取时间
        MdmPullMap mdmPullMap = pullMapper.getLastPullDate(formId);
        String lastPullDate = pullDate;
        if(mdmPullMap!=null || mdmPullMap.getLastPullDate() != null || !"".equals(mdmPullMap.getLastPullDate())){
            lastPullDate = mdmPullMap.getLastPullDate();
        }
        return lastPullDate;
    }

    /**
     * 主数据拉取时间是否是非空
     * @param formId
     * @return
     */
    private boolean pullDateIsNotEmpty(String formId){
        MdmPullMap mdmPullMap = pullMapper.getLastPullDate(formId);
        if(mdmPullMap!=null || mdmPullMap.getLastPullDate() != null || !"".equals(mdmPullMap.getLastPullDate())){
            return true;
        }
        return false;
    }

    /**
     * 修改传入参数的childData，并返回ESB的字表sourceID集合
     * @param vo
     * @param isSave
     * @return
     */
    private Map<String,List<Map<String, Object>>> getEsbSubIdMapList(MdmRequest vo, boolean isSave) {
        Map<String,List<Map<String, Object>>> returnObject = new HashMap<>();
        //需要添加到主数据库的关系
        List<Map<String, Object>> esbSubIdMapList = new ArrayList<>();
        //从主数据返回的ID中过滤的集合
        List<Map<String, Object>> filterRtnSubIdMapList = new ArrayList<>();
        //需要从数据库中删除映射关系
        List<Map<String, Object>> notDeleteSubIdMapList = new ArrayList<>();

        Map<String, Object> childtableIds = (Map<String, Object>) vo.getChildData();
        System.out.println(childtableIds);

        if (childtableIds != null) {
            Iterator<String> iter = childtableIds.keySet().iterator();
            //遍历字表uuid
            while (iter.hasNext()) {
                String uuid = iter.next();
                System.out.println(uuid);
                System.out.println(childtableIds.get(uuid));
                List<Map> uuidMapList = (List<Map>) childtableIds.get(uuid);
                //遍历某个字表里面的数据
                for (int i = 0 ; i<uuidMapList.size(); i++) {
                    Map<String, Object> esbSubIdMap = new HashMap<>();
                    Map<String, Object> filterRtnSubIdMap = new HashMap<>();
                    Map<String, Object> notDeleteSubIdMap = new HashMap<>();
                    Map uuidMap = uuidMapList.get(i);
                    esbSubIdMap.put("uuid", uuid);
                    notDeleteSubIdMap.put("uuid",uuid);
                    if (uuidMap.containsKey("sourceId") && uuidMap.get("sourceId") != null) {
                        String esbSubId = (String) uuidMap.get("sourceId");
                        esbSubIdMap.put("esbSubId", esbSubId);
                        notDeleteSubIdMap.put("esbSubId", esbSubId);
                        // 修改ID
                        List<MdmSubIdMap> listSubPage = subIdMapMapper.getSubIdMap(vo.getFormId(), uuid, vo.getSourceId(), esbSubId);
                        //查询到了主数据字表ID
                        if (listSubPage != null && listSubPage.size() > 0) {
                            //修改
                            // 修改原如记录
                            if(!uuidMap.containsKey("id")) {
                                uuidMap.put("id", listSubPage.get(0).getMdmSubId());
                            }
                            //将uuid和主数据字表id加入过滤集合里面

                            filterRtnSubIdMap.put("mdmSubId",listSubPage.get(0).getMdmSubId());
                            filterRtnSubIdMap.put("uuid",listSubPage.get(0).getSubTbPkId());
                        }


                        if (isSave) {
                            if(uuidMap.containsKey("id")) {
                                deleteKeyOfMap(uuidMap, "id");
                                deleteKeyOfMap(childtableIds, "id");
                            }
                        }

                        deleteKeyOfMap(uuidMap, "sourceId");
                    }
                    if(!filterRtnSubIdMap.isEmpty()) {
                        filterRtnSubIdMapList.add(filterRtnSubIdMap);
                    }
                    esbSubIdMapList.add(esbSubIdMap);
                    notDeleteSubIdMapList.add(notDeleteSubIdMap);
                }
            }
            vo.setChildData(childtableIds);
        }
        returnObject.put("esbSubIdMapList",esbSubIdMapList);
        returnObject.put("filterRtnSubIdMapList",filterRtnSubIdMapList);
        returnObject.put("notDeleteSubIdMapList",notDeleteSubIdMapList);
        return returnObject;
    }

    /**
     * 获取主数据返回字表的MDMID集合
     * @param mdmResult
     * @return
     */
    private List<Map<String, Object>> getMdmSubIdMapList(MdmChildVo mdmResult) {
        List<Map<String, Object>> mdmSubIdMapList = new ArrayList<>();

        if (mdmResult.getChildtables() != null) {
            List<Map> childtables = (List<Map>) mdmResult.getChildtables();
            for (int i = 0; i < childtables.size(); i++) {
                Map childData = childtables.get(i);
                ChildTableData childTableData;
                try {
                    childTableData = (ChildTableData) ObjectMapUtils.mapToObject(childData, ChildTableData.class);
                    List<String> mdmSubIds = childTableData.getChildTableDataId();
                    for (String mdmSubId : mdmSubIds) {
                        Map<String, Object> mdmSubIdMap = new HashMap<>();
                        mdmSubIdMap.put("uuid", childTableData.getUuid());
                        mdmSubIdMap.put("mdmSubId", mdmSubId);
                        mdmSubIdMapList.add(mdmSubIdMap);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return mdmSubIdMapList;
    }

    /**
     * 删除Map里面的一对键值对
     * @param paramsMap
     * @param delKey
     */
    private static void deleteKeyOfMap(Map<String, Object> paramsMap, String delKey) {
        Iterator<String> iter = paramsMap.keySet().iterator();
        while (iter.hasNext()) {
            String key = iter.next();
            if (delKey.equals(key)) {
                iter.remove();
            }
        }
    }

    /**
     * 域账号登录
     * @param loginName
     * @param password
     * @return
     */
    public String adLogin(String loginName, String password) {
        boolean b = Ldap.domainAuthenticate(loginName, password);
        if(b){
            return GsonUtil.toJsonString(MdmChildVo.builder().build().getOkObj("登录成功"));
        }
        return GsonUtil.toJsonString(MdmChildVo.builder().build().getErrObj("登录失败"));
    }


}
