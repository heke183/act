package com.xianglin.act.common.service.integration;

import com.xianglin.cif.common.service.facade.model.BusinessDTO;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.model.Response;
import com.xianglin.cif.common.service.facade.model.RoleDTO;

import java.util.List;

/**
 * @author yefei
 * @date 2018-01-18 15:34
 */
public interface CustomersInfoServiceClient {

    /**
     * 含糊条件（根据身份证或者手机号查询已经实名认证的客户）
     *
     * @param customers
     * @return
     */
    Response<CustomersDTO> selectByCredentialsNumOrPhoneNum(CustomersDTO customers);

    /**
     * 判断该身份证号或者手机号是否在CIF的cif_customer表中存在
     *
     * @param num  身份证号或者手机号
     * @return  true 存在
     *          false 不存在
     */
    Response<Boolean> isExistCredentialsNumOrPhoneNum(String num);

    /**
     * 根据partyId查询用户信息
     *
     * @param partyId
     * @return
     */
    Response<CustomersDTO> selectByPartyId(Long partyId);

    /**
     * 开户
     *
     * @param customers
     * @param application
     *            调用者
     * @return
     */
    Response<CustomersDTO> openAccount(CustomersDTO customers, String application);

    /**
     *
     * @param mobilePhone
     * @return
     */
    Response<CustomersDTO> selectByMobilePhone(String mobilePhone);

    /**同步推荐人信息
     * @param var1
     * @return
     */
    Response<Boolean> syncInvitationCustomer(CustomersDTO var1);
}
