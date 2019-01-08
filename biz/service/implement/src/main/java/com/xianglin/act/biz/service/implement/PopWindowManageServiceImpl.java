package com.xianglin.act.biz.service.implement;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.base.Strings;
import com.xianglin.act.common.dal.mappers.PopWindowMapper;
import com.xianglin.act.common.dal.model.PopWindow;
import com.xianglin.act.common.service.facade.PopWindowManageService;
import com.xianglin.act.common.service.facade.constant.PopWindowSourceEnum;
import com.xianglin.act.common.service.facade.constant.RecordDeleteStatusEnum;
import com.xianglin.act.common.service.facade.constant.RecordStatusEnum;
import com.xianglin.act.common.service.facade.model.*;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/4/11 10:56.
 */
//注解配置dubbo服务
@Service
public class PopWindowManageServiceImpl implements PopWindowManageService {

    @Autowired
    PopWindowMapper popWindowMapper;

    @Autowired
    ModelMapper modelMapper;

    @Override
    public Response<PageResult<PopWindowManageOutputDTO>> queryActivities(PageParam<PopWindowManageInputDTO> inputParam) {

        PopWindow record = modelMapper.map(inputParam.getParam(), PopWindow.class);
        record.setIsDeleted(RecordDeleteStatusEnum.NO.getCode() + "");
        record.setType(PopWindowSourceEnum.BY_TIME.name());
        PageInfo<Object> pageInfo = PageHelper
                .startPage(inputParam.getCurPage(), inputParam.getPageSize())
                .doSelectPageInfo(() -> popWindowMapper.select(record));
        PageResult<PopWindowManageOutputDTO> pageResult = new PageResult<>();
        pageResult.setCount((int) pageInfo.getTotal());
        Type type = new TypeToken<List<PopWindowManageOutputDTO>>() {

        }.getType();
        pageResult.setResult(modelMapper.map(pageInfo.getList(), type));
        return Response.ofSuccess(pageResult);
    }

    @Override
    public Response<PopWindowManageOutputDTO> queryActivity(Long id) {

        checkArgument(id != null, "参数错误：id不能为空");
        PopWindow popWindow = popWindowMapper.selectByPrimaryKey(id);
        checkState(popWindow != null, "空结果集");
        return Response.ofSuccess(modelMapper.map(popWindow, PopWindowManageOutputDTO.class));
    }

    @Override
    public Response<Boolean> updateActivity(PopWindowManageInputDTO inputParam) {

        checkArgument(inputParam != null, "参数错误：参数不能为空");
        boolean flag;
        Date now = new Date();
        PopWindow sqlParam = modelMapper.map(inputParam, PopWindow.class);
        sqlParam.setUpdateDate(now);
        sqlParam.setType(PopWindowSourceEnum.BY_TIME.name());
        String status = sqlParam.getStatus();
        if (Strings.isNullOrEmpty(status)) {
            sqlParam.setStatus(RecordStatusEnum.VALID.getCode() + "");
        }
        Long id = sqlParam.getId();
        if (id == null) { //新增
            PopWindow popWindow = new PopWindow();
            popWindow.setIsDeleted(RecordDeleteStatusEnum.NO.getCode() + "");
            popWindow.setType(PopWindowSourceEnum.BY_TIME.name());
            int count = popWindowMapper.selectCount(popWindow);
            if (count >= 8) {
                return Response.ofFail("新增错误：最多只能有8个弹框");
            }
            sqlParam.setCreateDate(now);
            sqlParam.setCreator(inputParam.getUpdater());
            flag = popWindowMapper.insert(sqlParam) == 1;
        } else { //编辑
            // 字段无法被修改为空的问题
            flag = popWindowMapper.updateByPrimaryKeySelective(sqlParam) == 1;
        }
        return Response.ofSuccess(flag);
    }
}
