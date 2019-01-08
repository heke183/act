package com.xianglin.act.common.util;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 生成和识别QRcode
 *
 * @author wanglei 2016年12月10日下午4:05:09
 */
public class QRUtils {

    private static final Logger Logger = LoggerFactory.getLogger(QRUtils.class);

    private static final String FILEDIR = "/tmp/";

    public static String qrCreate(String content) throws RuntimeException {
        File file = null;
        try {
            int width = 400; // 图像宽度
            int height = 400; // 图像高度
            String format = "png";// 图像类型
            Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.MARGIN, "1");
            BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);// 生成矩阵
            String fileName = UUID.randomUUID().toString() + ".png";
            file = new File(FILEDIR + fileName);
            Path tempQrFile = file.toPath();
            MatrixToImageWriter.writeToPath(bitMatrix, format, tempQrFile);
        } catch (WriterException |IOException e) {
            Logger.warn("",e);
            throw new BizException("生成二维码失败");
        }
        return AliyunUtil.uploadSearchFile(file);
    }

    public static void main(String[] args) throws Exception {
        String s = QRUtils.qrCreate("https://h5-dev.xianglin.cn/act/page/sendMoney/sendOne.html?partyId=1000076847");
        System.out.println(s);
    }
}
