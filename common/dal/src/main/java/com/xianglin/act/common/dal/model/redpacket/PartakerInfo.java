package com.xianglin.act.common.dal.model.redpacket;

import java.util.ArrayList;
import java.util.List;

/**
 * 参与者包含参与者获得的奖励
 *
 * @author yefei
 * @date 2018-04-03 10:26
 */
public class PartakerInfo extends Partaker {

    private List mockFootList;

    private List mockTitleList;

    private String sharerQrCode;

    public PartakerInfo() {

    }

    public String getSharerQrCode() {
        return sharerQrCode;
    }

    public void setSharerQrCode(String sharerQrCode) {
        this.sharerQrCode = sharerQrCode;
    }

    public List getMockFootList() {
        return mockFootList;
    }

    public void setMockFootList(List mockFootList) {
        this.mockFootList = new ArrayList(mockFootList);
    }

    public List getMockTitleList() {
        return mockTitleList;
    }

    public void setMockTitleList(List mockTitleList) {
        this.mockTitleList = new ArrayList(mockTitleList);
    }
}
