package com.xianglin.test;

import java.math.BigDecimal;

public class BigDecimalTest {

    public static void main(String[] args) {
        BigDecimal bigDecimal = BigDecimal.valueOf(2000.0000);
        System.out.println(bigDecimal.stripTrailingZeros().toPlainString());
    }
}
