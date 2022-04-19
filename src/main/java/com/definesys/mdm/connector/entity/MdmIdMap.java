package com.definesys.mdm.connector.entity;

/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/31 12:30
 */

public class MdmIdMap {

    private String mdmid;
    private String esbid;
    private String formid;

    public MdmIdMap() {
    }

    public MdmIdMap(String mdmid, String esbid, String formid) {
        this.mdmid = mdmid;
        this.esbid = esbid;
        this.formid = formid;
    }

    public String getMdmid() {
        return mdmid;
    }

    public void setMdmid(String mdmid) {
        this.mdmid = mdmid;
    }

    public String getEsbid() {
        return esbid;
    }

    public void setEsbid(String esbid) {
        this.esbid = esbid;
    }

    public String getFormid() {
        return formid;
    }

    public void setFormid(String formid) {
        this.formid = formid;
    }

    @Override
    public String toString() {
        return "IdMap{" +
                "mdmid='" + mdmid + '\'' +
                ", esbid='" + esbid + '\'' +
                ", formid='" + formid + '\'' +
                '}';
    }
}
