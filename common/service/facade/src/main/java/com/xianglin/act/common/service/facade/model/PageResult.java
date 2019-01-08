package com.xianglin.act.common.service.facade.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * 分页响应
 *
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/10 11:28.
 */

public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 4145787098078564358L;

    /**
     * 总条数
     */
    private int count;

    private List<T> result;

    public int getCount() {

        return count;
    }

    public void setCount(int count) {

        this.count = count;
    }

    public List<T> getResult() {

        if (result == null) {
            //默认返回空集合
            return Collections.emptyList();
        }
        return result;
    }

    public void setResult(List<T> result) {

        this.result = result;
    }

    public PageResult() {

    }

    public PageResult(int count, List<T> result) {

        this.count = count;
        this.result = result;
    }

    public static <T> PageResult<T> of(int count, List<T> result) {

        return new PageResult<>(count, result);
    }
}
