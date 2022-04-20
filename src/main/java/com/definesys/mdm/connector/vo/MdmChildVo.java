package com.definesys.mdm.connector.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/30 23:59
 */

@Data
@Builder
public class MdmChildVo<T> {

    private String code;
    private String messgae;
    private T data;
    private T childtables;

    public MdmChildVo() {
    }

    public MdmChildVo(String code, String messgae, T data, T childtables) {
        this.code = code;
        this.messgae = messgae;
        this.data = data;
        this.childtables = childtables;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessgae() {
        return messgae;
    }

    public void setMessgae(String messgae) {
        this.messgae = messgae;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getChildtables() {
        return childtables;
    }

    public void setChildtables(T childtables) {
        this.childtables = childtables;
    }

    public boolean isRtnOk(){
        return "ok".equals(code)? true : false;
    }

    public void setErroRtn(String message){
        this.code = "error";
        this.messgae = message;
    }

    public void setOkRtn(String messgae){
        this.code = "ok";
        this.messgae = messgae;
    }

    public static MdmChildVo getErrObj(String messgae){
        MdmChildVo<Object> error = MdmChildVo.builder().code("error").messgae(messgae).build();
        return error;
    }

    public static MdmChildVo getOkObj(String messgae){
        MdmChildVo<Object> ok = MdmChildVo.builder().code("ok").messgae(messgae).build();
        return ok;
    }

    @Override
    public String toString() {
        return "MdmRtn{" +
                "code='" + code + '\'' +
                ", messgae='" + messgae + '\'' +
                ", data=" + data +
                '}';
    }
}
