package com.xianglin.act.biz.shared.Impl;

import com.xianglin.act.biz.shared.ActService;
import com.xianglin.act.common.dal.mappers.ActivityConfigMapper;
import com.xianglin.act.common.dal.mappers.ActivityMapper;
import com.xianglin.act.common.dal.model.Activity;
import com.xianglin.act.common.dal.model.ActivityConfig;
import com.xianglin.act.common.util.ActPreconditions;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.cif.common.service.facade.CustomersInfoService;
import com.xianglin.cif.common.service.facade.model.CustomersDTO;
import com.xianglin.core.model.vo.UserAddressVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

import static com.xianglin.act.common.util.ActPreconditions.ResponseEnum.ACT_NOT_EXIST;

/**
 * @author yefei
 * @date 2018-04-10 9:59
 */
@Service("actServiceBiz")
public class ActServiceImpl implements ActService {

    @Resource
    private ActivityMapper activityMapper;

    @Autowired
    private ActivityConfigMapper activityConfigMapper;

    @Autowired
    private CustomersInfoService customersInfoService;

    /**
     * 精彩
     *
     * @return
     */
    @Override
    public List<Activity> actList() {
        return activityMapper.selectActList();
    }

    @Override
    public Activity selectAct(String activityCode) {
        Activity activity = activityMapper.selectActivity(activityCode);
        ActPreconditions.checkNotNull(activity, ACT_NOT_EXIST);
        return activity;
    }

    @Override
    public List<ActivityConfig> queryActConfigList(String activityCode) {
        Example example = new Example(ActivityConfig.class);
        example.and().andEqualTo("activityCode",activityCode).andEqualTo("isDeleted",Constant.Delete_Y_N.N.name());
        return activityConfigMapper.selectByExample(example);
    }

    @Override
    public Optional<String> queryActConfig(String activityCode, String key) {
        return Optional.ofNullable(activityConfigMapper.selectOne(ActivityConfig.builder().activityCode(activityCode)
        .configKey(key).build())).map(v -> v.getConfigValue());
    }

    @Override
    public boolean updateActConfig(String activityCode, String key, String value) {
        return activityConfigMapper.updateByCodeAndKey(activityCode,key,value) == 1;
    }

    @Override
    public Long insertActivityConfig(ActivityConfig activityConfig) {
        activityConfigMapper.insertSelective(activityConfig);
        return activityConfig.getId();
    }

    @Override
    public UserAddressVo userCertification(Long partyId) {
        UserAddressVo userAddressVo = new UserAddressVo();

        com.xianglin.cif.common.service.facade.model.Response<CustomersDTO> resp2 = customersInfoService.selectCustomsAlready2Auth(partyId);
        Boolean isAuth = false;
        if (resp2.getResult() != null) {
            CustomersDTO customersDTO = resp2.getResult();
            if (StringUtils.isNotEmpty(resp2.getResult().getAuthLevel())) {
                isAuth = true;
            }
            userAddressVo.setIsAuth(isAuth);
            userAddressVo.setMobile(customersDTO.getMobilePhone());
            userAddressVo.setUserName(customersDTO.getCustomerName());
        }
        return userAddressVo;
    }
}
