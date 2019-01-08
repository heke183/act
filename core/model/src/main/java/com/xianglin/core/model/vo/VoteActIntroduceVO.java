package com.xianglin.core.model.vo;

import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/6/4 21:19.
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("活动说明")
public class VoteActIntroduceVO {

    private String key;

    private String value;

}
