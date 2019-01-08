package com.xianglin.act.common.dal.support.pop;

import com.esotericsoftware.reflectasm.MethodAccess;
import com.google.common.base.Strings;
import com.xianglin.fala.session.Session;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/12 10:03.
 */

public class PopTipRequest {

    private static final MethodAccess METHOD_ACCESS = MethodAccess.get(PopTipRequest.class);

    public static final String GET = "get";

    private Long partyId;

    private String deviceId;

    private String clientVersion;

    private String loginName;

    public PopTipRequest() {

    }

    public Long getPartyId() {

        return partyId;
    }

    public void setPartyId(Long partyId) {

        this.partyId = partyId;
    }

    public String getDeviceId() {

        return deviceId;
    }

    public void setDeviceId(String deviceId) {

        this.deviceId = deviceId;
    }

    public String getClientVersion() {

        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {

        this.clientVersion = clientVersion;
    }

    public String getLoginName() {

        return loginName;
    }

    public void setLoginName(String loginName) {

        this.loginName = loginName;
    }

    public Object get(String fieldName) {

        if (Strings.isNullOrEmpty(fieldName)) {
            return null;
        }

        String upperCase = fieldName.substring(0, 1).toUpperCase();
        String substring = fieldName.substring(1);
        String methodName = GET + upperCase + substring;
        int index = METHOD_ACCESS.getIndex(methodName);
        return METHOD_ACCESS.invoke(this, index, null);
    }

    public static PopTipRequest ofSession(Session session) {

        //String deviceId = session.getAttribute(AppSessionConstants.DEVICE_ID, String.class);
        Long partyId = session.getAttribute("partyId", Long.class);
        //String loginName = session.getAttribute(AppSessionConstants.LOGIN_NAME, String.class);

        PopTipRequest request = new PopTipRequest();
        request.setDeviceId(null);
        request.setPartyId(partyId);
        request.setLoginName(null);
        return request;
    }
}
