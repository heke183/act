package com.xianglin.act.common.dal.support.pop;

import com.esotericsoftware.reflectasm.MethodAccess;

import java.lang.reflect.Method;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 11:39.
 */

public class SelectorInvokable {

    private Object target;

    private Method targetMethod;

    private MethodAccess methodAccess;

    private int methondIndex;

    private String[] paramNames;

    private Integer popTipType;

    private Integer returnType;

    public PopTipDO invoke(PopTipRequest popTipRequest) {

        Object[] params = resolveParameter(popTipRequest);
        Object invokeRetValue = methodAccess.invoke(target, methondIndex, params);
        return new PopTipDO(invokeRetValue, popTipType, returnType);
    }

    /**
     * @param popTipRequest
     * @return
     */
    private Object[] resolveParameter(PopTipRequest popTipRequest) {

        Object[] params = new Object[targetMethod.getParameterCount()];
        for (int i = 0; i < paramNames.length; i++) {
            params[i] = popTipRequest.get(paramNames[i]);
        }
        return params;
    }

    public Object getTarget() {

        return target;
    }

    public void setTarget(Object target) {

        this.target = target;
    }

    public MethodAccess getMethodAccess() {

        return methodAccess;
    }

    public void setMethodAccess(MethodAccess methodAccess) {

        this.methodAccess = methodAccess;
    }

    public Method getTargetMethod() {

        return targetMethod;
    }

    public void setTargetMethod(Method targetMethod) {

        this.targetMethod = targetMethod;
    }

    public int getMethondIndex() {

        return methondIndex;
    }

    public void setMethondIndex(int methondIndex) {

        this.methondIndex = methondIndex;
    }

    public String[] getParamNames() {

        return paramNames;
    }

    public void setParamNames(String[] paramNames) {

        this.paramNames = paramNames;
    }

    public Integer getPopTipType() {

        return popTipType;
    }

    public void setPopTipType(Integer popTipType) {

        this.popTipType = popTipType;
    }

    public Integer getReturnType() {

        return returnType;
    }

    public void setReturnType(Integer returnType) {

        this.returnType = returnType;
    }
}
