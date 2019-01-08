package com.xianglin.core.model.vo;

import com.xianglin.act.common.dal.model.ActGroupInfoDetail;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Describe :
 * Created by xingyali on 2018/12/19 9:44.
 * Update reason :
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@ApiModel("团信息")
public class ActGroupInfoVo {

    @ApiModelProperty(value = "id")
    private Long id;

    /**
     * partyid
     */
    @ApiModelProperty(value = "发起人的partyid")
    private Long partyId;

    @ApiModelProperty(value = "version")
    private String version;

    /**
     *团样式 
     */
    @ApiModelProperty(value = "团主题 恭喜发财:GXFC,狗年运势:GNYS,恭贺祝福:GHZF,感谢:GX,有你真好:YNZH")
    private String style;

    /**
     *红包总金额
     */
    @ApiModelProperty(value = "红包总金额")
    private String totalBalance;

    /**
     * 过期时间
     */
    @ApiModelProperty(value = "过期时间")
    private Long expireTime;

    @ApiModelProperty(value = "团状态 I:进行中 S已成团 F已失效")
    private String status;

    @ApiModelProperty(value = "还差多少人可以成团")
    private int lackNumber;

    @ApiModelProperty(value = "是否是团长")
    private String isManager;

    @ApiModelProperty(value = "当前用户分得多少红包")
    private String cuBalance;

    private String isDeleted;

    private Date createTime;

    private Date updateTime;

    private String comments;
    
    private List<ActGroupInfoDetailVo> actGroupInfoDetailVoList;
    
}
