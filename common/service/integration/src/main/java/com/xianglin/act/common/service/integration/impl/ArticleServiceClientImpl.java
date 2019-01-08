package com.xianglin.act.common.service.integration.impl;

import com.xianglin.act.common.service.integration.ArticleServiceClient;
import com.xianglin.appserv.common.service.facade.app.ArticleService;
import com.xianglin.appserv.common.service.facade.model.Response;
import com.xianglin.appserv.common.service.facade.model.vo.ArticleTipVo;
import com.xianglin.appserv.common.service.facade.model.vo.ArticleVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author yefei
 * @date 2018-06-05 10:25
 */
@Service
public class ArticleServiceClientImpl implements ArticleServiceClient {

    @Autowired
    private ArticleService articleService;

    @Override
    public Response<Boolean> publishArticleV1(ArticleVo vo) {
        return articleService.publishArticleV1(vo);
    }

    @Override
    public Response<Boolean> publishArticleTip(ArticleTipVo articleTipVo) {
        return articleService.publishArticleTip(articleTipVo);
    }
}
