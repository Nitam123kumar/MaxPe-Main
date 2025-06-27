package com.vuvrecharge.modules.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;

public class PlanItemData implements Serializable {

    @SerializedName("rs")
    private String rs;
    @SerializedName("desc")
    private String desc;
    @SerializedName("validity")
    private String validity;
    @SerializedName("data")
    private String data;
    @SerializedName("last_update")
    private String last_update;
    @SerializedName("talktime")
    private String talktime;
    @SerializedName("sms_value")
    private String sms_value;
    @SerializedName("subscription")
    private String subscription;

    public String getRs() {
        return rs;
    }

    public void setRs(String rs) {
        this.rs = rs;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getValidity() {
        return validity;
    }

    public void setValidity(String validity) {
        this.validity = validity;
    }

    public String getLast_update() {
        return last_update;
    }

    public void setLast_update(String last_update) {
        this.last_update = last_update;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getTalktime() {
        return talktime;
    }

    public void setTalktime(String talktime) {
        this.talktime = talktime;
    }

    public String getSubscription() {
        return subscription;
    }

    public void setSubscription(String subscription) {
        this.subscription = subscription;
    }

    public String getSms_value() {
        return sms_value;
    }

    public void setSms_value(String sms_value) {
        this.sms_value = sms_value;
    }
}
