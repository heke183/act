package com.xianglin.act.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

/**
 * 通过html地址生成图片工具
 *
 * @author wanglei
 */
public class Html2ImageUtil {

    private final static Logger logger = LoggerFactory.getLogger(RedPacketImages.class);

    /**
     * 按照链接生成图片并上传到阿里云
     *
     * @param htmlUrl 地址
     * @return 返回阿里云上对应的图片地址
     */
    public static String createImage(String htmlUrl)  {

        try {
            String imageName = UUID.randomUUID().toString() + ".jpg";
            // 调用 x 生成图片
            StringBuilder stringBuilder = new StringBuilder("sh wkh.sh ");
            stringBuilder.append(htmlUrl + " ");
            stringBuilder.append("/tmp/" + imageName);
            logger.info("generateShareImage exec: {}", stringBuilder.toString());

            Process exec = Runtime.getRuntime().exec(stringBuilder.toString());
            exec.waitFor();

            BufferedReader br = new BufferedReader(new InputStreamReader(exec.getInputStream()));
            StringBuffer sb = new StringBuffer();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            logger.info("generateShareImage result: {}", sb.toString());

            // 上传图片
            File file = new File("/tmp/" + imageName);
            return AliyunUtil.uploadSearchFile(file);
        } catch (Exception e) {
            logger.warn("html转图片失败,"+htmlUrl,e);
            throw new RuntimeException("html转图片失败");
        }
    }

}
