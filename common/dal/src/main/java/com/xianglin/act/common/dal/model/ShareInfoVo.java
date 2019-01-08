package com.xianglin.act.common.dal.model;

/**
 * Created by wanglei on 2017/3/13.
 */

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author lipf
 * @create 2018-07-17 18:26
 **/
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ShareInfoVo extends BaseVo {

    private String title;

    private String titieImg;
    /**
     * 分享到好友
     */
    private String content;

    /**
     * url地址
     */
    private String url;
    /**
     * 分享到朋友圈
     */
    private String contentPYQ;
}
