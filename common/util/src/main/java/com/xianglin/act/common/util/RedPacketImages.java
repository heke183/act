package com.xianglin.act.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 红包活动 生成图片/上传图片cdn 工具类
 *
 * @author yefei
 * @date 2018-04-09 16:18
 */
public class RedPacketImages {

    /**
     * logger
     */
    private final static Logger logger = LoggerFactory.getLogger(RedPacketImages.class);

    /**
     * 生成图片的原链接
     */
    private List<String> sourceUrl;

    public String generateShareImage(long partyId) throws Exception {
        int index = (int) (ThreadLocalRandom.current().nextDouble() * sourceUrl.size());
        final String source = sourceUrl.get(index);

        String imageName = UUID.randomUUID().toString() + ".jpg";
        // 调用 x 生成图片
        StringBuilder stringBuilder = new StringBuilder("sh wkh.sh ");
        stringBuilder.append(source + "?partyId=" + partyId + " ");
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
    }

    public void setSourceUrl(List<String> sourceUrl) {
        this.sourceUrl = sourceUrl;
    }
}
