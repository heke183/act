package com.xianglin.test;

import java.math.BigDecimal;

/**
 * @author yefei
 * @date 2018-02-01 16:43
 */
public class RandomTest {

    public static void main(String[] args) {
        BigDecimal maxValue = new BigDecimal(1.2);
        BigDecimal minValue = new BigDecimal(1.0);

        BigDecimal add = new BigDecimal(Math.random()).multiply(maxValue.subtract(minValue));
        System.out.println(add.add(minValue).divide(new BigDecimal("1")).setScale(1, BigDecimal.ROUND_HALF_UP));

        BigDecimal xx = new BigDecimal(Math.random())
                .multiply(new BigDecimal("4500").subtract(new BigDecimal("4000")));
        System.out.println(xx = xx.add(new BigDecimal("4000")));

        System.out.println(xx.divide(new BigDecimal("1000")).setScale(0, BigDecimal.ROUND_HALF_UP));


    }
}
