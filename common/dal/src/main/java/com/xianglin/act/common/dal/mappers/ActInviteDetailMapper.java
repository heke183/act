package com.xianglin.act.common.dal.mappers;

import com.xianglin.act.common.dal.model.ActInviteDetail;
import org.apache.ibatis.annotations.Update;
import tk.mybatis.mapper.common.Mapper;

/**
 * Describe :
 * Created by xingyali on 2018/8/23 14:17.
 * Update reason :
 */
public interface ActInviteDetailMapper extends Mapper<ActInviteDetail> {

    /**同步邀请数量
     * @return
     */
    @Update("update act_invite A,(select REC_PARTY_ID,count(*) as COUNT from act_invite_detail where IS_DELETED = 'N' and `STATUS` = 'S' GROUP BY REC_PARTY_ID) B,(select REC_PARTY_ID,count(*) as COUNT from act_invite_detail where IS_DELETED = 'N' GROUP BY REC_PARTY_ID) C set A.REGISTER_NUM = B.COUNT,A.INVITE_NUM = C.COUNT where A.PARTY_ID = B.REC_PARTY_ID and A.PARTY_ID = C.REC_PARTY_ID")
    Integer updateInviteDetail();
}
