package com.xianglin.act.common.dal.model;

import io.swagger.annotations.ApiModel;
import lombok.*;

import javax.persistence.Table;
import java.time.LocalDateTime;

/**
 * @author yefei
 * @date 2018-06-05 14:06
 */
@Setter
@Getter
@Builder
@ToString
@ApiModel("联系信息")
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "act_customer_acquire_contactinfo")
public class ContactInfo {

    private Long id;

    private Long partyId;

    private String mobilePhone;

    private String name;

    private String address;
    
    /**
     * 统一删除标记
     */
    private String isDeleted;

    private String creator;

    private String updater;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String comments;

    public void initDateOfInsert() {
        this.createDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.isDeleted = "0";
    }

}
