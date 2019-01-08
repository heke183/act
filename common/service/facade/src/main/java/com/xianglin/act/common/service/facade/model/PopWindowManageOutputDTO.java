package com.xianglin.act.common.service.facade.model;

import java.io.Serializable;
import java.util.Date;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/10 11:09.
 */

public class PopWindowManageOutputDTO implements Serializable {

    private static final long serialVersionUID = 5527554580298163187L;

    /**
     * 自增主键
     */
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 弹框类型
     */
    private String templateCode;

    /**
     * 弹窗开始时间
     */
    private Date showStartTime;

    /**
     * 弹窗结束时间
     */
    private Date showExpireTime;

    /**
     * 活动LOGO
     */
    private String activityLogo;

    /**
     * 弹窗频率
     */
    private Integer frequency;

    /**
     * 活动LOGO跳转URL
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
     * 排序
     */
    private String orderNum;

    /**
     * 状态
     */
    private String status;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getActivityName() {

        return activityName;
    }

    public void setActivityName(String activityName) {

        this.activityName = activityName;
    }

    public String getTemplateCode() {

        return templateCode;
    }

    public void setTemplateCode(String templateCode) {

        this.templateCode = templateCode;
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

    public String getOrderNum() {

        return orderNum;
    }

    public void setOrderNum(String orderNum) {

        this.orderNum = orderNum;
    }

    public String getStatus() {

        return status;
    }

    public void setStatus(String status) {

        this.status = status;
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

    public Integer getFrequency() {

        return frequency;
    }

    public void setFrequency(Integer frequency) {

        this.frequency = frequency;
    }
}
