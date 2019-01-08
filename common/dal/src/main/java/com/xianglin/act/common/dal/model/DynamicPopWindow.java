package com.xianglin.act.common.dal.model;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author
 */
@Table(name = "act_dynamic_pop_window")
public class DynamicPopWindow implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 关联的弹窗code，类型为FROM_ACT
     */
    private String eventCode;

    /**
     * partyId
     */
    private Long partyId;

    /**
     * 弹窗消息内容
     */
    private String content;

    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String comments;

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }

    public String getEventCode() {

        return eventCode;
    }

    public void setEventCode(String eventCode) {

        this.eventCode = eventCode;
    }

    public Long getPartyId() {

        return partyId;
    }

    public void setPartyId(Long partyId) {

        this.partyId = partyId;
    }

    public String getContent() {

        return content;
    }

    public void setContent(String content) {

        this.content = content;
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

    public LocalDateTime getCreateDate() {

        return createDate;
    }

    public void setCreateDate(LocalDateTime createDate) {

        this.createDate = createDate;
    }

    public LocalDateTime getUpdateDate() {

        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {

        this.updateDate = updateDate;
    }

    public String getComments() {

        return comments;
    }

    public void setComments(String comments) {

        this.comments = comments;
    }
}