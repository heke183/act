package com.xianglin.act.biz.shared;

import org.junit.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * @author yefei
 * @date 2018-01-19 9:25
 */
public class RandomTest {

    public static void main(String[] args) throws UnknownHostException {

        InetAddress localHost = InetAddress.getLocalHost();
        System.out.println(localHost.getHostName());
        System.out.println(localHost.getHostAddress());
        InetAddress[] allName = InetAddress.getAllByName(localHost.getHostName());
        System.out.println(Arrays.toString(allName));
        InetAddress[] allName2 = InetAddress.getAllByName("XL9LYGZ32");
        System.out.println(Arrays.toString(allName2));

    }
    @Test
    public void testParseTime() {

        LocalTime parse = LocalTime.parse("9:00",DateTimeFormatter.ofPattern("H:m"));
        System.out.println(parse);

    }
}
