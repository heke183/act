package com.xianglin.act.common.dal.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author
 */
@Table(name = "act_sign_times")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExtendProp implements Serializable {

    /**
     * 自增主键
     */
    @Id
    @GeneratedValue(generator = "JDBC")
    private Long id;

    /**
     * 关联
     */
    private Long relationId;

    /**
     * 属性类型
     */
    private String propType;

    /**
     * 属性Key
     */
    private String propKey;

    /**
     * 属性Value
     */
    private String propValue;

    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String comments;

    private static final long serialVersionUID = 1L;

}