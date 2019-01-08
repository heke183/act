package com.xianglin.act.common.dal.model.redpacket;

import com.alibaba.fastjson.annotation.JSONField;
import com.xianglin.act.common.dal.model.Party;

/**
 * @author yefei
 * @date 2018-04-03 13:42
 */
public class User extends Party {

    /**
     * 是否是分享者自己打开链接
     */
    @JSONField(name = "isSharer")
    protected boolean sharer;

    private String mobilePhone;
    private String wxOpenId;
    private String url;

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public boolean isSharer() {
        return false;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
