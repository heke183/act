package com.xianglin.act.common.dal.model;

/**
 * @author yefei
 * @date 2018-06-01 16:19
 */
public class Party {

    private Long partyId;

    public Party() {
    }

    private Party(Long partyId) {
        this.partyId = partyId;
    }

    public static Party crateParty(Long partyId) {
        return new Party(partyId);
    }

    public Long getPartyId() {
        return partyId;
    }

    public void setPartyId(Long partyId) {
        this.partyId = partyId;
    }
}


