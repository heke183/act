package com.xianglin.act.common.service.facade.model;

import java.io.Serializable;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/11 12:41.
 */

public class PageParam<T> implements Serializable {

    private static final long serialVersionUID = 1604192717721580125L;

    private int pageSize;

    private int curPage;

    private T param;

    public int getPageSize() {

        return pageSize;
    }

    public void setPageSize(int pageSize) {

        this.pageSize = pageSize;
    }

    public int getCurPage() {

        return curPage;
    }

    public void setCurPage(int curPage) {

        this.curPage = curPage;
    }

    public T getParam() {

        return param;
    }

    public void setParam(T param) {

        this.param = param;
    }
}
