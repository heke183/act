package com.xianglin.act.common.dal.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;
import java.util.Date;

/**
 */
@Table(name = "act_pop_window")
public class PopWindow implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 活动名称
     */
    private String activityName;

    /**
     * 弹窗标识，唯一索引
     */
    private String eventCode;

    /**
     * 弹窗类型： 活动产生的和top上配置的
     */
    private String type;

    /**
     * 弹框类型： 弹窗样式，有好几种类型
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
     * 弹窗频率
     */
    private Integer frequency;

    /**
     * 活动LOGO
     */
    private String activityLogo;

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

    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;

    private String comments;

    @Transient
    private String content;

    private static final long serialVersionUID = 1L;

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

    public String getIsDeleted() {

        return isDeleted;
    }

    public void setIsDeleted(String isDeleted) {

        this.isDeleted = isDeleted;
    }

    public String getCreator() {

        return creator;
    }

    public void setCreator(String creator) {

        this.creator = creator;
    }

    public String getUpdater() {

        return updater;
    }

    public void setUpdater(String updater) {

        this.updater = updater;
    }

    public Date getCreateDate() {

        return createDate;
    }

    public void setCreateDate(Date createDate) {

        this.createDate = createDate;
    }

    public Date getUpdateDate() {

        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {

        this.updateDate = updateDate;
    }

    public String getComments() {

        return comments;
    }

    public void setComments(String comments) {

        this.comments = comments;
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

    public String getEventCode() {

        return eventCode;
    }

    public void setEventCode(String eventCode) {

        this.eventCode = eventCode;
    }

    public String getType() {

        return type;
    }

    public void setType(String type) {

        this.type = type;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
    }
}