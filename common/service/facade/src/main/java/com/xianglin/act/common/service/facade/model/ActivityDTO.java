package com.xianglin.act.common.service.facade.model;

import com.xianglin.act.common.service.facade.constant.PopTipTypeEnum;

import java.io.Serializable;
import java.util.Date;

/**
 * @author yefei
 * @date 2018-04-02 13:14
 */
public class ActivityDTO implements Serializable {

    private static final long serialVersionUID = 2261670098381435399L;

    /**
     * id
     */
    private Long id;

    /**
     * partyId
     */
    private Long partyId;


    /**
     * 是否显示
     */
    private boolean show;

    /**
     * 弹窗类型
     */
    private Integer showType;

    /**
     * 弹窗类型枚举
     */
    private PopTipTypeEnum popTipType;

    /**
     * 弹窗消息
     */
    private String showMessage;

    /**
     * 弹框开始时间
     */
    private Date showStartTime;

    /**
     * 弹窗结束时间
     */

    private Date showExpireTime;

    /**
     * 弹窗主图 url
     */
    private String activityLogo;

    /**
     * 主图跳转url
     */
    private String activityLogoDestUrl;

    /**
     * 左边按钮url
     */
    private String leftButtonUrl;

    /**
     * 右边按钮url
     */
    private String rightButtonUrl;

    /**
     * 弹窗频率
     */
    private Integer frequency;

    /**
     * 排序
     */
    private Integer orderNum;

    public boolean isShow() {

        return show;
    }

    public void setShow(boolean show) {

        this.show = show;
    }

    public Integer getShowType() {

        return showType;
    }

    public void setShowType(Integer showType) {

        this.showType = showType;
    }

    public String getShowMessage() {

        return showMessage;
    }

    public void setShowMessage(String showMessage) {

        this.showMessage = showMessage;
    }

    public Date getShowStartTime() {

        return showStartTime;
    }

    public void setShowStartTime(Date showStartTime) {

        this.showStartTime = showStartTime;
    }

    public Date getShowExpireTime() {

        return showExpireTime;
    }

    public void setShowExpireTime(Date showExpireTime) {

        this.showExpireTime = showExpireTime;
    }

    public String getActivityLogo() {

        return activityLogo;
    }

    public void setActivityLogo(String activityLogo) {

        this.activityLogo = activityLogo;
    }

    public String getActivityLogoDestUrl() {

        return activityLogoDestUrl;
    }

    public void setActivityLogoDestUrl(String activityLogoDestUrl) {

        this.activityLogoDestUrl = activityLogoDestUrl;
    }

    public String getLeftButtonUrl() {

        return leftButtonUrl;
    }

    public void setLeftButtonUrl(String leftButtonUrl) {

        this.leftButtonUrl = leftButtonUrl;
    }

    public String getRightButtonUrl() {

        return rightButtonUrl;
    }

    public void setRightButtonUrl(String rightButtonUrl) {

        this.rightButtonUrl = rightButtonUrl;
    }

    public PopTipTypeEnum getPopTipType() {

        return popTipType;
    }

    public void setPopTipType(PopTipTypeEnum popTipType) {

        this.popTipType = popTipType;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Integer getFrequency() {

        return frequency;
    }

    public void setFrequency(Integer frequency) {

        this.frequency = frequency;
    }

    public Integer getOrderNum() {

        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {

        this.orderNum = orderNum;
    }

    public Long getPartyId() {

        return partyId;
    }

    public void setPartyId(Long partyId) {

        this.partyId = partyId;
    }
}
