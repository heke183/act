package com.xianglin.act.common.service.integration;

import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.ArticleTipVo;
import com.xianglin.appserv.common.service.facade.model.vo.ArticleVo;

/**
 * @author yefei
 * @date 2018-06-05 10:25
 */
public interface ArticleServiceClient {

    Response<Boolean> publishArticleV1(ArticleVo vo);

    Response<Boolean> publishArticleTip(ArticleTipVo var1);

}
