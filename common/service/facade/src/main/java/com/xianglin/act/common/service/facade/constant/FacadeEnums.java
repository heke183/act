package com.xianglin.act.common.service.facade.constant;

public enum FacadeEnums {

    OK(1000, "正常", "返回正常"),
    FAIL(40000, "失败", "操作失败"),

    SURE(300000, "确定", "返回确定"),
    UPDATE_FAIL(300001, "异常", "修改失败"),
    UPDATE_INVALID(300002, "异常", "修改无效"),
    DELETE_FAIL(300003, "异常", "删除失败"),
    INSERT_FAIL(300004, "异常", "添加失败"),
    INSERT_INVALID(300005, "异常", "添加无效"),
    INSERT_DUPLICATE(300006, "异常", "要插入的数据已存在"),
    UPDATE_DUPLICATE(300007, "异常", "要更新的数据已存在");

    public int code;

    public String msg;

    public String tip;

    FacadeEnums(int code, String msg) {

        this.code = code;
        this.msg = msg;
    }

    FacadeEnums(int code, String msg, String tip) {

        this.code = code;
        this.msg = msg;
        this.tip = tip;
    }

    public int getCode() {

        return code;
    }

    public void setCode(int code) {

        this.code = code;
    }

    public String getMsg() {

        return msg;
    }

    public void setMsg(String msg) {

        this.msg = msg;
    }

    public String getTip() {

        return tip;
    }

    public void setTip(String tip) {

        this.tip = tip;
    }
}
