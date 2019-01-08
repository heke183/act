package com.xianglin.core.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xianglin.act.common.dal.enums.PrizeEnum;
import com.xianglin.act.common.dal.mappers.*;
import com.xianglin.act.common.dal.model.*;
import com.xianglin.act.common.dal.model.redpacket.User;
import com.xianglin.act.common.service.integration.GoldcoinServiceClient;
import com.xianglin.act.common.util.*;
import com.xianglin.appserv.common.service.facade.model.enums.Constant;
import com.xianglin.cif.common.service.facade.vo.GoldcoinRecordVo;
import com.xianglin.core.model.enums.ActPlantEnum;
import com.xianglin.core.model.enums.Constants;
import com.xianglin.act.common.util.BizException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.util.*;

/**
 * @author yefei
 * @date 2018-04-08 15:03
 */
public class PrizeAwardUtils {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(PrizeAwardUtils.class);

    private final static String API = "https://api.mch.weixin.qq.com/mmpaymkttransfers/sendredpack";

    private String ecLuckyCouponsUrl;
    @Resource
    private SequenceMapper sequenceMapper;

    @Resource
    private GoldcoinServiceClient goldcoinServiceClient;

    @Resource
    private ActPlantMapper actPlantMapper;

    @Resource
    private ActPlantLvTranMapper actPlantLvTranMapper;

    @Resource
    private ConfigMapper configMapper;

    @Resource
    private ActPlantTipMapper actPlantTipMapper;
    

    private String appId;
    private String mchId;
    private String apiSecret;
    private String clineIp;

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public void setMchId(String mchId) {
        this.mchId = mchId;
    }

    public void setApiSecret(String apiSecret) {
        this.apiSecret = apiSecret;
    }

    public void setClineIp(String clineIp) {
        this.clineIp = clineIp;
    }

    public void award(Party party, Prize prize) {
        //
        if (prize.getPrizeType() != null) {
            switch (prize.getPrizeTypeEnum()) {
                case WX_RED_PACKET:
                    addWxRedPacket(party, prize);
                    break;
                case XL_GOLD_COIN:
                    addGold(party, prize);
                    break;
                case EC_COUPON:
                    addCoupon(party, prize);
                    break;
                case EC_PHONE_COUPON:
                    addPhoneCoupon(party, prize);
                    break;
                case ACT_PLANT_LV:
                    addPlantLv(party, prize);
                    break;
                default:
                    logger.warn("prize not support award: " + prize.getPrizeTypeEnum().name());
            }
            return;
        }

        // 老版本 红包活动
        switch (prize.getPrizeEnum()) {
            case EC_COUPON:
                addCoupon(party, prize);
                break;
            case XL_GOLD_COIN:
                addGold(party, prize);
                break;
            case WX_RED_PACKET:
                addWxRedPacket(party, prize);
                break;
            default:
                logger.warn("prize not support award: " + prize.getPrizeEnum().name());
        }
    }

    private void addPlantLv(Party party, Prize prize) {
        ActPlant actPlant = actPlantMapper.findByPartyId(party.getPartyId());
        if(actPlant == null){
            logger.error("查询爱心值失败：{}");
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        //加当前用户的爱心值
        actPlant.setLv(actPlant.getLv() + prize.getAmount().intValue());
        actPlant.setTotalLv(actPlant.getTotalLv() + prize.getAmount().intValue());
        actPlant.setUpdateTime(new Date());
        actPlantMapper.updateByPrimaryKeySelective(actPlant);

        ActPlant plant = actPlantMapper.selectOne(ActPlant.builder().partyId(party.getPartyId()).build());
        long tipCount = JSON.parseArray(configMapper.selectConfig("PLANT_TIP_LEVEL"), Integer.class).stream().filter(v -> v <= plant.getLv()).count();
        int count = actPlantTipMapper.selectCount(ActPlantTip.builder().partyId(party.getPartyId()).type(ActPlantEnum.TipType.LEVEL.name()).isDeleted(Constant.Delete_Y_N.N.name()).build());
        while (count++ < tipCount) {
            actPlantTipMapper.insertSelective(ActPlantTip.builder().partyId(party.getPartyId()).type(ActPlantEnum.TipType.LEVEL.name())
                    .status(ActPlantEnum.StatusType.I.name()).tip("我的树又长大了一些").build());
        }


        //同时保存爱心交易明细一条记录
        actPlantLvTranMapper.insertSelective(ActPlantLvTran.builder().partyId(party.getPartyId())
                .lv(prize.getAmount().intValue())
                .lvId(null)
                .isDeleted("N").status(ActPlantEnum.StatusType.S.name()).type(ActPlantEnum.TranType.PRIZE.name()).build());
        
    }

    /**
     * 添加电商优惠卷
     */
    private void addPhoneCoupon(Party user, Prize prize) {
        Map<String, String> param = new HashMap<>();
        String app_key = "xianglin".concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO).concat("@#_$&"));
        param.put("party_id", user.getPartyId() + "");
        param.put("type", "1");// 话费券
        param.put("amount", prize.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP) + "");
        param.put("key", prize.getCouponName());
        addCoupon0(prize, param, app_key);
    }

    /**
     * 添加电商优惠卷
     */
    private void addCoupon(Party user, Prize prize) {
        Map<String, String> param = new HashMap<>();
        String app_key = "xianglin".concat(DateUtils.formatDate(DateUtils.getNow(), DateUtils.DATE_TPT_TWO).concat("@#_$&"));
        param.put("party_id", user.getPartyId() + "");

        if (PrizeEnum.FIFTH_PRIZE.name().equals(prize.getPrizeCode())) {
            param.put("type", "1");// 话费券
            param.put("amount", prize.getAmount().stripTrailingZeros().toPlainString() + "");
        } else {
            param.put("type", "2");// 优惠券
            param.put("amount", prize.getAmount().stripTrailingZeros().toPlainString() + "");
            param.put("key", prize.getCouponName());
        }

        addCoupon0(prize, param, app_key);
    }

    private void addCoupon0(Prize prize, Map<String, String> param, String app_key) {
        app_key = app_key.concat(SHAUtil.getSortString(param));
        param.put("app_key", app_key);
        try {
            param.put("sign", SHAUtil.shaEncode(MD5.encode(app_key)));
        } catch (Exception e) {
            logger.error("sign error!", e);
        }
        String json = HttpUtils.executePost(ecLuckyCouponsUrl, param);
        logger.debug("调用电商添加优惠卷: {}", json);
        if (StringUtils.isNotEmpty(json)) {
            JSONObject object = JSONObject.parseObject(json);
            // 成功
            if ("1".equals(object.getString("error_code"))) {
                String memcCode = object.getJSONObject("data").getString("memc_code");
                prize.setMemcCode(memcCode);
            } else {
                logger.error("添加电商优惠卷 失败", json);
                throw new BizException(ActPreconditions.ResponseEnum.ERROR);
            }
        }
    }

    /**
     * 添加金币
     */
    private void addGold(Party user, Prize prize) {
        GoldcoinRecordVo goldcoinRecordVo = new GoldcoinRecordVo();
        goldcoinRecordVo.setSystem("act");
        goldcoinRecordVo.setAmount(prize.getAmount().intValue());
        goldcoinRecordVo.setFronPartyId(Constants.GOLD_SYS_ACCOUNT);
        goldcoinRecordVo.setType(prize.getActivityCode());
        goldcoinRecordVo.setRemark(prize.getCouponName());
        goldcoinRecordVo.setToPartyId(user.getPartyId());
        String sequence = GoldSequenceUtil.getSequence(user.getPartyId(), sequenceMapper.getSequence());
        goldcoinRecordVo.setRequestId(sequence);

        com.xianglin.cif.common.service.facade.model.Response<GoldcoinRecordVo> goldcoinRecordVoResponse = goldcoinServiceClient.doRecord(goldcoinRecordVo);
        logger.debug("发放金币, result: {}", goldcoinRecordVoResponse.getTips());
        if (!com.google.common.base.Objects.equal(goldcoinRecordVoResponse.getCode(), 1000)) {
            logger.error("添加金币失败！", JSON.toJSONString(goldcoinRecordVoResponse));
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        prize.setMemcCode(sequence);
    }

    /**
     * 微信支付商户号: 1488885812
     * 商户名称: 上海乡邻网络科技有限公司
     * appid: wx0c1a1664441c4dd7
     * opendid: oLFoYv1weCMg4jh5BPACJflvqpWk
     */
    private void addWxRedPacket(Party party, Prize prize) {
        String mch_billno = XmlTool.getOrderNum();
        SendRedPack sendRedPack = new SendRedPack(
                UUID.randomUUID().toString().replace("-", "").toString(), //"随机字符串不超过32位",
                mch_billno, //"随机订单号，不超过32位",
                mchId, //"商户号",
                appId, //"公众号appid",
                "乡邻app",//"桑博",
                ((User) party).getWxOpenId(),//"填写接受人的openid",
                prize.getAmount().multiply(new BigDecimal(100)).intValue(),
                1,
                "恭喜发财",
                clineIp,
                "乡邻现金大派送活动",
                "乡邻现金大派送活动",
                "PRODUCT_5"
        );

        //将实体类转换为url形式
        String urlParamsByMap = XmlTool.getUrlParamsByMap(XmlTool.toMap(sendRedPack));
        //拼接我们再前期准备好的API密钥，前期准备第5条
        urlParamsByMap += "&key=" + apiSecret;
        //进行签名，需要说明的是，如果内容包含中文的话，要使用utf-8进行md5签名，不然会签名错误
        String sign = XmlTool.parseStrToMd5L32(urlParamsByMap).toUpperCase();
        sendRedPack.setSign(sign);
        //微信要求按照参数名ASCII字典序排序，这里巧用treeMap进行字典排序
        TreeMap treeMap = new TreeMap(XmlTool.toMap(sendRedPack));
        //然后转换成xml格式
        String soapRequestData = XmlTool.getSoapRequestData(treeMap);
        logger.info("微信红包发送参数：{}", soapRequestData);
        String result = HttpUtils.executePostXml(
                API,
                soapRequestData,
                getSSL());
        //得到输出内容
        logger.info("微信红包响应 ：{}", result);
        Map<String, String> responseData = XmlTool.getResponseData(result);
        if (!"SUCCESS".equals(responseData.get("return_code"))) {
            logger.error("微信红包发送失败！：{}", result);
            throw new BizException(ActPreconditions.ResponseEnum.ERROR);
        }
        prize.setMemcCode(mch_billno);
    }

    private SSLContext getSSL() {
        try {
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //证书位置自己定义
            FileInputStream instream = new FileInputStream(new File("/data/cert/apiclient_cert.p12"));
            try {
                keyStore.load(instream, mchId.toCharArray());
            } finally {
                instream.close();
            }
            SSLContext sslcontext = SSLContexts.custom()
                    .loadKeyMaterial(keyStore, mchId.toCharArray())
                    .build();
            return sslcontext;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return null;
    }

    public void setEcLuckyCouponsUrl(String ecLuckyCouponsUrl) {
        this.ecLuckyCouponsUrl = ecLuckyCouponsUrl;
    }
}
