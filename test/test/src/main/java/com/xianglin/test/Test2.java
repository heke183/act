package com.xianglin.test;

public class Test2 {

    public static void main(String[] args) {
        String address;
        String provinceName = "省";
        String cityName = "市";
        String countyName = "县";

        address = provinceName == null ? "" : provinceName
                + cityName == null ? "" : cityName
                + countyName == null ? "" : countyName;

        System.out.println(address);

    }
}