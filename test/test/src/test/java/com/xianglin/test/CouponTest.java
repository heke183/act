package com.xianglin.test;

import com.alibaba.fastjson.JSONObject;
import com.xianglin.act.common.util.DateUtils;
import com.xianglin.act.common.util.HttpUtils;
import com.xianglin.act.common.util.MD5;
import com.xianglin.act.common.util.SHAUtil;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * @author yefei
 * @date 2018-01-26 14:13
 */
public class CouponTest {

    @Test
    public void test1() {
        Map<String, String> param = new HashMap<>();
        String app_key = "xianglin".concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO).concat("@#_$&"));
        param.put("party_id", "5199881");
        param.put("type", "1");// 话费券
        param.put("amount", "2");

        app_key = app_key.concat(SHAUtil.getSortString(param));
        param.put("app_key", app_key);
        try {
            param.put("sign", SHAUtil.shaEncode(MD5.encode(app_key)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = HttpUtils.executePost("https://mai-dev2.xianglin.cn/index.php/wap/lottery-luckyCoupons.html", param);
        System.out.println("------->" + json);
    }


    @Test
    public void test2() {
        Map<String, String> param = new HashMap<>();
        String app_key = "xianglin".concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO).concat("@#_$&"));
        param.put("party_id", "5199881");
        param.put("type", "2");// 话费券
        param.put("amount", "0.9");
        param.put("key", "大彩电");
        app_key = app_key.concat(SHAUtil.getSortString(param));
        param.put("app_key", app_key);
        try {
            param.put("sign", SHAUtil.shaEncode(MD5.encode(app_key)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        String json = HttpUtils.executePost("https://mai-dev2.xianglin.cn/index.php/wap/lottery-luckyCoupons.html", param);
        System.out.println("------->" + json);
    }

}
