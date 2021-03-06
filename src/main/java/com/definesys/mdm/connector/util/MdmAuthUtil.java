package com.definesys.mdm.connector.util;

import com.definesys.mdm.connector.dto.MdmUserDto;
import com.definesys.mdm.connector.request.MdmAddUserRequest;
import com.definesys.mdm.connector.vo.MdmChildVo;
import com.google.gson.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/30 15:06
 */
@PropertySource("classpath:application.yml")
@Service
@Slf4j
public class MdmAuthUtil {


    private static String app_Id;

    private static String app_Key;

    private static String mdmUri;

    private static String getTokenPath;

    private static String getUserByCode;

    private static String getUserByMdmCode;

    private static String saveApi;

    private static String updateApi;

    private static String addUser;

    private static String enableOrDisableApi;


    @Value(value = "${mdm.app_id}")
    public void setApp_Id(String app_Id) {
        MdmAuthUtil.app_Id = app_Id;
    }
    @Value(value = "${mdm.app_key}")
    public void setApp_Key(String app_Key) {
        MdmAuthUtil.app_Key = app_Key;
    }
    @Value(value = "${mdm.uri}")
    public void setMdmUri(String mdmUri) {
        MdmAuthUtil.mdmUri = mdmUri;
    }
    @Value(value = "${mdm.getTokenPath}")
    public void setGetTokenPath(String getTokenPath) {
        MdmAuthUtil.getTokenPath = getTokenPath;
    }
    @Value(value = "${mdm.getUserByCode}")
    public void setGetUserByCode(String getUserByMdmCode) {
        MdmAuthUtil.getUserByCode = getUserByMdmCode;
    }
    @Value(value = "${mdm.getUserByMdmCode}")
    public void setGetUserByMdmCode(String getUserByMdmCode) { MdmAuthUtil.getUserByMdmCode = getUserByMdmCode; }
    @Value(value = "${mdm.saveApi}")
    public void setSaveApi(String saveApi) { MdmAuthUtil.saveApi = saveApi; }
    @Value(value = "${mdm.updateApi}")
    public void setUpdateApi(String updateApi) { MdmAuthUtil.updateApi = updateApi; }
    @Value(value = "${mdm.addUser}")
    public void setAddUser(String addUser){ MdmAuthUtil.addUser = addUser;}
    @Value(value = "${mdm.enableOrDisableApi}")
    public void setEnableOrDisableApi(String enableOrDisableApi){
        MdmAuthUtil.enableOrDisableApi = enableOrDisableApi;
    }

    public static String getMDMToken(){
        String token = "";
        String uri = mdmUri+getTokenPath+"?APP_ID="+app_Id+"&APP_KEY="+app_Key;
        MdmChildVo mdmRtn = null;
        try {
            token = HttpUtil.sendGetData(uri,null);
            mdmRtn = new Gson().fromJson(token, MdmChildVo.class);
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        JsonObject jsonObject = (JsonObject)new JsonParser().parse(mdmRtn.getData().toString());
        return jsonObject.get("token").toString().replace("\"","");

    }

    /**
     * ??????????????????????????????ID
     * @param userCode
     * @return
     */
    public static String getUserIdByUserCode(String userCode) {
        String userId = "";
        if (userCode == null){
            return null;
        }
        try {
            String jobNumber = HttpUtil.sendGetData(HttpUtil.addFirstParam(getGetUserByCodeUri(), "jobNumber", userCode), getMdmRequestHeader());
            MdmChildVo rtMap = new Gson().fromJson(jobNumber,MdmChildVo.class);
            Map<String, Object> rtnData = (Map<String, Object>) rtMap.getData();
            if(rtnData != null && !rtnData.isEmpty()){
                userId = (String) rtnData.get("id");
            }else{
                //?????????????????????????????????
                //?????????????????????
                MdmUserDto userDto = getGetUserByMdmCodeUri(userCode);
                //????????????,?????????????????????ID
                userId = createUserByMdmUserDto(userDto);
            }
        } catch (IOException e) {
            log.info(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userId;
    }

    /**
     * ???????????????????????????????????????????????????ID
     * @param userDto
     * @return
     */
    private static String createUserByMdmUserDto(MdmUserDto userDto) throws IOException {
        MdmAddUserRequest addUserDto = MdmAddUserRequest.builder().build();
        addUserDto.setLoginName(userDto.getMdmCode());
        addUserDto.setUserId(userDto.getId());
        addUserDto.setPassword("welcome1");
        addUserDto.setPhone(userDto.getPhone());
        addUserDto.setEmail(userDto.getEmail()==null? StringUtil.getUuid() + "@hanslaser.com" :userDto.getEmail());
        String reqJson = new Gson().toJson(addUserDto);
        String addUserRtnString = HttpUtil.sendPostDataByJson(getAddUserUri(), getMdmRequestHeader(), reqJson);
        MdmUserDto userRtnDto = new Gson().fromJson(new Gson().fromJson(addUserRtnString,MdmChildVo.class).getData().toString(),MdmUserDto.class);
        return userRtnDto.getId();
    }


    public static Map<String,String> getMdmRequestHeader(){
        Map<String,String> mdmHeader = new HashMap<>();
        mdmHeader.put("demdmtoken",getMDMToken());
        return mdmHeader;
    }

    /**
     * ?????????????????????????????????
     * @param mdmCode
     * @return
     * @throws IOException
     */
    public static MdmUserDto getGetUserByMdmCodeUri(String mdmCode) throws IOException {
        MdmUserDto userDto = MdmUserDto.builder().build();
        String MdmRtnString = HttpUtil.sendPostDataByJson(HttpUtil.addFirstParam(getGetUserByMdmCodeUri(), "mdmCode", mdmCode), getMdmRequestHeader(), "");
        MdmChildVo mdmRtn = new Gson().fromJson(MdmRtnString, MdmChildVo.class);
        JsonArray userInfos = (JsonArray) new JsonParser().parse(mdmRtn.getData().toString());
        if(userInfos.size()>0) {
            userDto = new Gson().fromJson(userInfos.get(0).toString(), MdmUserDto.class);
        }
        return userDto;
    }

    /**????????????uri*/
    public static String getTokenPathUri(){
        return mdmUri+getTokenPath;
    }

    /**????????????uri*/
    public static String getGetUserByCodeUri(){
        return mdmUri+getUserByCode;
    }

    /**????????????uri*/
    public static String getGetUserByMdmCodeUri(){
        return mdmUri+getUserByMdmCode;
    }

    /**??????MDM saveAPI*/
    public static String getSaveApi() { return mdmUri+saveApi; }

    /**??????MDM UpdateAPI*/
    public static String getUpdateApi() { return mdmUri+updateApi; }

    /**??????MDM??????????????? API*/
    public static String getEnableOrDisableApi(){
        return mdmUri+enableOrDisableApi;

    }

    public static String getAddUserUri(){ return mdmUri+addUser; }

}
