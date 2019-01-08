package com.xianglin.act.common.service.integration.impl;

import com.xianglin.act.common.service.integration.CustomersInfoServiceClient;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.cif.common.service.facade.model.Response;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author yefei
 * @date 2018-01-18 15:36
 */
@Service
public class CustomersInfoServiceClientImpl implements CustomersInfoServiceClient {

    @Resource
    private CustomersInfoService customersInfoService;

    @Override
    public Response<CustomersDTO> selectByCredentialsNumOrPhoneNum(CustomersDTO customersDTO) {
        return customersInfoService.selectByCredentialsNumOrPhoneNum(customersDTO);
    }

    @Override
    public Response<Boolean> isExistCredentialsNumOrPhoneNum(String application) {
        return customersInfoService.isExistCredentialsNumOrPhoneNum(application);
    }

    @Override
    public Response<CustomersDTO> selectByPartyId(Long aLong) {
        return customersInfoService.selectByPartyId(aLong);
    }

    @Override
    public Response<CustomersDTO> openAccount(CustomersDTO customersDTO, String application) {
        return customersInfoService.openAccount(customersDTO, application);
    }

    @Override
    public Response<CustomersDTO> selectByMobilePhone(String mobilePhone) {
        return customersInfoService.selectByMobilePhone(mobilePhone);
    }

    @Override
    public Response<Boolean> syncInvitationCustomer(CustomersDTO customersDTO) {
        return customersInfoService.syncInvitationCustomer(customersDTO);
    }
}
