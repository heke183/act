package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Describe :
 * Created by xingyali on 2018/10/30 15:05.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActPlantShareDTO implements Serializable {
    private String image;

    private String title;

    private String content;

    private String urlWX;

    private String urlWB;

    private String urlQQ;

    private String openId;
}
