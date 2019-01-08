package com.xianglin.act.common.util;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import jodd.util.PropertiesUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** 
 * 采用SHAA加密
 * @author Xingxing,Xie
 * @datetime 2014-6-1 
 */
public class SHAUtil {
	private static final Logger logger = LoggerFactory.getLogger(SHAUtil.class);
	/*** 
     * SHA加密 生成40位SHA码
     * @param 待加密字符串
     * @return 返回40位SHA码
     */
    public static String shaEncode(String inStr) throws Exception {
    	logger.debug("#加密字符串,{}",inStr);
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }

        byte[] byteArray = inStr.getBytes("UTF-8");
        byte[] md5Bytes = sha.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) { 
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
    
    public static String shaEncode(String inStr,String pwd) throws Exception {
         return shaEncode(inStr+pwd+inStr);
    }
    

    
    public static String getSortString(Map<String, String> params){
    	List<String> keys = new ArrayList<>(params.keySet());
    	Collections.sort(keys);
    	StringBuilder sbuilder = new StringBuilder();
    	for (String key : keys) {
			String value = params.get(key);
			if(!("sign".equals(key)||"signature".equals(key))){
				if(StringUtils.isEmpty(value)){
					value = "";
				}
				sbuilder.append(key).append(value);
			}
		}
    	logger.info("sort data :{}",sbuilder.toString());
    	return sbuilder.toString();
    }
    
    public static String getSignatureString(Map<String, Object> params,String signkey){
    	List<String> keys = new ArrayList<>(params.keySet());
    	Collections.sort(keys);
    	StringBuilder sbuilder = new StringBuilder();
    	boolean first = true;
    	for (String key : keys) {
			Object value = params.get(key);
			if(!("sign".equals(key)||"signature".equals(key)) && value!=null && StringUtils.isNotEmpty(String.valueOf(value))){
			
				if(first){
					sbuilder.append(key).append("=").append(value);
					first = false;
				}else{
					sbuilder.append("&").append(key).append("=").append(value);
				}
				
			}
		}
    	logger.info("to sha data :{}",sbuilder.toString());
    	try {
			String signature = shaEncode(sbuilder.toString(),signkey);
		logger.info(" sha data :{}",signature);
			return signature;
    	} catch (Exception e) {
			// Auto-generated catch block
			e.printStackTrace();
			logger.error("", e);
		}
    	return null;
    }

    public final static String Sha1(String s) {
        char hexDigits[]={'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
        try {
            byte[] btInput = s.getBytes();
            MessageDigest mdInst = MessageDigest.getInstance("sha-1");
            mdInst.update(btInput);
            byte[] md = mdInst.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    /**
     * 测试主函数
     * @param args
     * @throws Exception
     */
    public static void main(String args[]) throws Exception {
       
        String shaEncode = shaEncode("name=zhangs&nodePartyId=11000685","4f7eaa8382f44a47af6471052903779b");
		System.out.println("SHA后：" + shaEncode);
		System.out.println("6f13804e4fd724f725d9dc282c77bf5f93751ec2".equals(shaEncode));
        
        
    }
}
