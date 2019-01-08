package com.xianglin.act.biz.shared;

/**
 * @author yefei
 * @date 2018-04-11 10:25
 */
public class SharerQrCode {

    private String nickName;

    private String headImage;

    private String url;

    private Long partyId;

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }
}
