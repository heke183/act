package com.xianglin.act.common.util;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jodd.util.Base64;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.AlgorithmParameters;
import java.security.Security;
import java.util.*;
import java.util.concurrent.TimeUnit;


/**
 * 微信小程序工具类
 *
 * @author wanglei
 */
public class WxAppletApiUtils {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());


    private final static String OPEN_ID = "https://api.weixin.qq.com/sns/jscode2session?appid=APPID&secret=SECRET&js_code=JSCODE&grant_type=authorization_code";

    private String appid;

    private String secret;

    private RedissonClient redissonClient;

    private final String WX_APPLET = "WX_APPLET-";

    /**
     * 静默登陆根据 code 获得openId和unionId
     */
    public String getSessionId(String code, String sessionId) {
        String requestUrl = OPEN_ID;
        requestUrl = requestUrl.replace("APPID", appid)
                .replace("SECRET", secret)
                .replace("JSCODE", code);

        String s = HttpUtils.executeGet(requestUrl);
        JSONObject jsonObject = JSON.parseObject(s);
        if (StrUtil.isEmpty(jsonObject.getString("openid"))) {
            logger.info("check code info {}",jsonObject);
            throw new BizException("通过code获取session失败");
        }
        RMap<String, String> map;
        if (StringUtils.isNotEmpty(sessionId)) {
            map = redissonClient.<String, String>getMap(WX_APPLET + sessionId);
            if (!StringUtils.equals(map.get("openid"), jsonObject.getString("openid"))) {
                throw new BizException("session非法或已过期!");
            }
        } else {
            sessionId = UUID.randomUUID().toString();
            map = redissonClient.<String, String>getMap(WX_APPLET + sessionId);
        }
        map.put("openid", jsonObject.getString("openid"));
        map.put("session_key", jsonObject.getString("session_key"));
        map.expire(7, TimeUnit.DAYS);
        return sessionId;
    }

    /**
     * 保存用户图片信息
     *
     * @param paras
     * @return
     */
    public String getUserInfo(Map<String, String> paras) {
        String sessionId = paras.get("sessionId");
        RMap<String, String> map = redissonClient.<String, String>getMap(WX_APPLET + sessionId);
        JSONObject jsonObject = JSON.parseObject(paras.get("userInfo"));
        String sessionKey = Optional.ofNullable(map.get("session_key"))
                .orElseThrow(() -> new BizException("sessionId :" + sessionId + "已过期或不存在！"));

        jsonObject = JSON.parseObject(getEncrypInfo(paras.get("encryptedData"), sessionKey, paras.get("iv")));
        if (!jsonObject.isEmpty()) {
            map.put("nickName", jsonObject.getString("nickName"));
            map.put("avatarUrl", jsonObject.getString("avatarUrl"));
            map.put("unionid", jsonObject.getString("unionId"));

        }
        return sessionId;
    }

    /**
     * 获取用户全部信息（包含手机号，姓名，头型，openId，unionId）
     *
     * @param sessionId
     * @param encryptedData
     * @param iv
     * @return
     */
    public Map<String, String> getAllInfo(String sessionId, String encryptedData, String iv) {
        RMap<String, String> map = redissonClient.<String, String>getMap(WX_APPLET + sessionId);
        String sessionKey = Optional.ofNullable(map.get("session_key"))
                .orElseThrow(() -> new BizException("sessionId :" + sessionId + "已过期或不存在！"));
        String mobileInfo = Optional.ofNullable(getEncrypInfo(encryptedData, sessionKey, iv))
                .orElseThrow(() -> new BizException("获取手机号信息失败！"));
        String mobilePhone = JSON.parseObject(mobileInfo).getString("purePhoneNumber");
        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("sessionId", sessionId);
        resultMap.put("mobilePhone", mobilePhone);
        resultMap.put("nickName", map.get("nickName"));
        resultMap.put("avatarUrl", map.get("avatarUrl"));
        resultMap.put("openid", map.get("openid"));
        resultMap.put("unionid", map.get("unionid"));
        return resultMap;
    }

    private String decodeMobile() {
        return null;
    }

    public String getEncrypInfo(String encryptedData, String sessionKey, String iv) {
        String result = null;
        // 被加密的数据
        byte[] dataByte = Base64.decode(encryptedData);
        // 加密秘钥
        byte[] keyByte = Base64.decode(sessionKey);
        // 偏移量
        byte[] ivByte = Base64.decode(iv);

        try {
            // 如果密钥不足16位，那么就补足.  这个if 中的内容很重要
            int base = 16;
            if (keyByte.length % base != 0) {
                int groups = keyByte.length / base + (keyByte.length % base != 0 ? 1 : 0);
                byte[] temp = new byte[groups * base];
                Arrays.fill(temp, (byte) 0);
                System.arraycopy(keyByte, 0, temp, 0, keyByte.length);
                keyByte = temp;
            }
            // 初始化
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding", "BC");
            SecretKeySpec spec = new SecretKeySpec(keyByte, "AES");
            AlgorithmParameters parameters = AlgorithmParameters.getInstance("AES");
            parameters.init(new IvParameterSpec(ivByte));
            cipher.init(Cipher.DECRYPT_MODE, spec, parameters);
            byte[] resultByte = cipher.doFinal(dataByte);
            if (null != resultByte && resultByte.length > 0) {
                result = new String(resultByte, "UTF-8");
            }
        } catch (Exception e) {
            logger.warn("getEncrypInfo fail encryptedData, String sessionKey, String iv " + encryptedData + " " + sessionKey + " " + iv, e);
            throw new BizException("解密失败");
        }
        return result;
    }


    public void setAppid(String appid) {
        this.appid = appid;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public void setRedissonClient(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }
}
