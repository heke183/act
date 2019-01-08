package com.xianglin.act.common.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/17 16:40.
 */
public class EcApis {

    private static final Logger logger = LoggerFactory.getLogger(EcApis.class);

    private static final String PREFIX = "xianglin";

    private static final String SUFFIX = "@#_$&";

    private String ecHostUrl = ConfigPropertyUtils.getEcServerHost();

    /**
     * 注册用户发放奖励
     *
     * @param partyId
     * @return
     */
    public String getRegisterUserAward(String partyId) {

        String requestUri = "/index.php/wap/lottery-registerSendCoupons.html";
        HashMap<String, String> params = Maps.newHashMap();
        params.put("party_id", partyId);
        String sign = getSign(params);
        params.put("sign", sign);
        String jsonResp = HttpUtils.executePost(ecHostUrl + requestUri, params);
        JSONObject responseObj = JSON.parseObject(jsonResp);
        int error_code = responseObj.getIntValue("error_code");
        if (error_code == 1) {
            return responseObj.getJSONObject("data").getString("coupons_amount");
        } else {
            throw new EcAwardGenException(responseObj.getString("error") + ":partyId " + partyId);
        }
    }

    /**
     * 发放电商奖励
     *
     * @param partyId
     * @param type
     * @param amount 金额
     * @param key 优惠券名称
     * @return
     */
    public static String getRegisterUserAward(String partyId, EshopType type, BigDecimal amount,String key) {
        String requestUri = "/index.php/wap/lottery-luckyCoupons.html";
        HashMap<String, String> params = Maps.newHashMap();
        params.put("party_id", partyId);
        params.put("type", type.code);
        params.put("amount", amount.toPlainString());
        params.put("key",key);
        String sign = getSign(params);
        params.put("sign", sign);
        String jsonResp = HttpUtils.executePost(ConfigPropertyUtils.getEcServerHost() + requestUri, params);
        JSONObject responseObj = JSON.parseObject(jsonResp);
        int error_code = responseObj.getIntValue("error_code");
        if (error_code == 1) {
            return responseObj.getJSONObject("data").getString("coupons_amount");
        } else {
            throw new EcAwardGenException(responseObj.getString("error") + ":partyId " + partyId);
        }
    }

    /** 查询用户优惠券数量和地址
     * @param partyId 用户partId
     * @return
     */
    public static Map<String,Object> getEcCountAndUrl(Long partyId){
        String couponCountUrl = "/index.php/wap/lottery-getCouponsNumbers.html";
        String coupon_count_url = "/wap/index.html#/member/newcoupon";
        Map<String, Object> result = new HashMap<>();
        try {
            Map<String, String> param = new HashMap<>();
            String app_key = PREFIX.concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO))
                    .concat(SUFFIX);

            param.put("party_id",partyId + "");
            app_key = app_key.concat(SHAUtil.getSortString(param));
            param.put("sign", getSign(param));
            param.put("app_key", app_key);

            String json = HttpUtils
                    .executePost(ConfigPropertyUtils.getEcServerHost() + couponCountUrl, param);
            logger.info("Coupon:{}", json);
            JSONObject object = JSONObject.parseObject(json);
            if (object != null) {
                if ("".equals(object.getString("error"))) {
                    String coupontotal = object.getJSONObject("data").getString("total");
                    result.put("couponCount",Integer.valueOf(coupontotal));
                    result.put("couponCountUrl",ConfigPropertyUtils.getEcServerHost() + coupon_count_url);
                } else {
                    String error = object.getString("error");
                    logger.info("get couponCount" + error);
                    result.put("couponCount",0);
                    result.put("couponCountUrl",ConfigPropertyUtils.getEcServerHost() + coupon_count_url);
                }
            } else {
                logger.info("get json null");
                result.put("couponCount",0);
                result.put("couponCountUrl",ConfigPropertyUtils.getEcServerHost() + coupon_count_url);
            }
        } catch (Exception e) {
            logger.warn("getJSON error", e);
        }
        return result;
    }

    public static JSONArray queryCouponList(Long partyId){

        JSONArray array = new JSONArray();
        String couponCountUrl = "/index.php/wap/lottery-getCouponsList.html";
        try {
            String app_key = PREFIX.concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO))
                    .concat(SUFFIX);
            Map<String, String> param = new HashMap<>();
            param.put("party_id",partyId + "");
            app_key = app_key.concat(SHAUtil.getSortString(param));
            param.put("sign", getSign(param));
            param.put("app_key", app_key);

            String json = HttpUtils
                    .executePost(ConfigPropertyUtils.getEcServerHost() + couponCountUrl, param);
            logger.info("Coupon:{}", json);
            JSONObject object = JSONObject.parseObject(json);
            if (Integer.valueOf(object.get("error_code").toString()) == 1){
                if (object.get("data") != null){
                    Map obj = (Map)object.get("data");
                    array = JSONArray.parseArray(obj.get("couponList").toString());
                }
            }
        }catch (Exception e){
            logger.warn("getJSON error", e);
        }
            return array;
    }

    /**
     * 返回优惠券列表URL
     * @return
     */
    public static String queryCouponListUrl(){
        String couponCountUrl = "/wap/index.html#/member/newcoupon";
        String url = ConfigPropertyUtils.getEcServerHost() + couponCountUrl;
        return url;
    }


    /**
     * 只获取appkey
     *
     * @param parameters
     * @return
     */
    private static String getAppKey(Map<String, String> parameters) {

        String appKey = PREFIX.concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO).concat(SUFFIX));
        return appKey.concat(SHAUtil.getSortString(parameters));
    }

    /**
     * 会同时获取appkey和sign，
     *
     * @param parameters
     * @return
     */
    private static String getSign(Map<String, String> parameters) {

        String appKey = getAppKey(parameters);
        //如果要同时获取appKey和sign，要先获取appKey
        parameters.put("app_key", appKey);
        try {
            return SHAUtil.shaEncode(MD5.encode(appKey));
        } catch (Exception e) {
            logger.info("===========[[ {签名生成异常} ]]===========", parameters);
            throw new SignGenerateException(e);
        }
    }

    /**
     * 电商奖励类型
     */
    public enum EshopType {
        TICKET("1", "话费券"),
        COUPON("2", "优惠券");
        private String code;
        private String desc;

        EshopType(String code, String desc) {
            this.code = code;
            this.desc = desc;
        }
    }

    public String getEcHostUrl() {

        return ecHostUrl;
    }

    public void setEcHostUrl(String ecHostUrl) {

        this.ecHostUrl = ecHostUrl;
    }

    public static class SignGenerateException extends RuntimeException {

        public SignGenerateException() {

        }

        public SignGenerateException(String message) {

            super(message);
        }

        public SignGenerateException(String message, Throwable cause) {

            super(message, cause);
        }

        public SignGenerateException(Throwable cause) {

            super(cause);
        }

        public SignGenerateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static class EcAwardGenException extends RuntimeException {

        public EcAwardGenException() {

        }

        public EcAwardGenException(String message) {

            super(message);
        }

        public EcAwardGenException(String message, Throwable cause) {

            super(message, cause);
        }

        public EcAwardGenException(Throwable cause) {

            super(cause);
        }

        public EcAwardGenException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {

            super(message, cause, enableSuppression, writableStackTrace);
        }
    }

    public static void main(String[] args) {
//        System.out.println(EcApis.getRegisterUserAward("1000000000002000",EshopType.COUPON,BigDecimal.valueOf(5L),""));

    }
}
