package com.xianglin.act.common.dal.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Date;

/**
 * @author
 */
@Table(name = "act_pop_window_history")
public class PopWindowHistory implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 弹框ID
     */
    private Long popWindowId;

    /**
     * 用户partyId
     */
    private Long partyId;

    /**
     * 弹框时间
     */
    private Date popDate;

    /**
     * 弹框类型
     */
    private String templateCode;

    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private Date createDate;

    private Date updateDate;

    private String comments;

    private static final long serialVersionUID = 1L;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public Long getPopWindowId() {

        return popWindowId;
    }

    public void setPopWindowId(Long popWindowId) {

        this.popWindowId = popWindowId;
    }

    public Long getPartyId() {

        return partyId;
    }

    public void setPartyId(Long partyId) {

        this.partyId = partyId;
    }

    public Date getPopDate() {

        return popDate;
    }

    public void setPopDate(Date popDate) {

        this.popDate = popDate;
    }

    public String getTemplateCode() {

        return templateCode;
    }

    public void setTemplateCode(String templateCode) {

        this.templateCode = templateCode;
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
}