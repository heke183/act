package com.xianglin.act.common.service.facade.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Describe :
 * Created by xingyali on 2018/7/20 16:41.
 * Update reason :
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActStepDetailShareInfo {
    /**
     * 立即分享标题
     */
    private String shareTitle;

    /**
     * 邀请好友一起赚标题
     */
    private String inviteTitle;

    /**
     * 图片
     */
    private String titieImg;
    /**
     *立即分享内容
     */
    private String shareContent;
    
    /**
     * 邀请好友一起赚内容
     */
    private String inviteContent;

    /**
     * url地址
     */
    private String url;
}
