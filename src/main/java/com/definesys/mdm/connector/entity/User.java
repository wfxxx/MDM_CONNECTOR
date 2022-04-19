package com.definesys.mdm.connector.entity;


/**
 * @Author: wf
 * @Description: TODO
 * @DateTime: 2022/3/29 17:03
 */

public class User {
    private String ID;
    private String NAME;
    private String CREDENTIAL_NO;
    private String PHONE;
    private String EMAIL;

    public User(String ID, String NAME, String CREDENTIAL_NO, String PHONE, String EMAIL) {
        this.ID = ID;
        this.NAME = NAME;
        this.CREDENTIAL_NO = CREDENTIAL_NO;
        this.PHONE = PHONE;
        this.EMAIL = EMAIL;
    }

    public User() {
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getNAME() {
        return NAME;
    }

    public void setNAME(String NAME) {
        this.NAME = NAME;
    }

    public String getCREDENTIAL_NO() {
        return CREDENTIAL_NO;
    }

    public void setCREDENTIAL_NO(String CREDENTIAL_NO) {
        this.CREDENTIAL_NO = CREDENTIAL_NO;
    }

    public String getPHONE() {
        return PHONE;
    }

    public void setPHONE(String PHONE) {
        this.PHONE = PHONE;
    }

    public String getEMAIL() {
        return EMAIL;
    }

    public void setEMAIL(String EMAIL) {
        this.EMAIL = EMAIL;
    }

    @Override
    public String toString() {
        return "User{" +
                "ID='" + ID + '\'' +
                ", NAME='" + NAME + '\'' +
                ", CREDENTIAL_NO='" + CREDENTIAL_NO + '\'' +
                ", PHONE='" + PHONE + '\'' +
                ", EMAIL='" + EMAIL + '\'' +
                '}';
    }
}
