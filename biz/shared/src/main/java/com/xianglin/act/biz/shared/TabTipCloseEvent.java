package com.xianglin.act.biz.shared;

import org.springframework.context.ApplicationEvent;

public class TabTipCloseEvent extends ApplicationEvent {

    private Long partyId;

    private Integer popTipTYpe;

    private Long id;

    public TabTipCloseEvent(Object source, Long partyId, Integer popTipTYpe, Long id) {

        super(source);
        this.partyId = partyId;
        this.popTipTYpe = popTipTYpe;
        this.id = id;
    }

    public Long getPartyId() {

        return partyId;
    }

    public void setPartyId(Long partyId) {

        this.partyId = partyId;
    }

    public Integer getPopTipTYpe() {

        return popTipTYpe;
    }

    public void setPopTipTYpe(Integer popTipTYpe) {

        this.popTipTYpe = popTipTYpe;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {

        this.id = id;
    }
}