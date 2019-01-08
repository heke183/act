package com.xianglin.test;

import org.springframework.web.client.RestTemplate;

public class RestTest {

    public static void main(String[] args) {
        RestTemplate restTemplate = new RestTemplate();
        String str = restTemplate.getForObject("http://www.weather.com.cn/data/sk/101110412.html", String.class);

        System.out.println(str);
    }
}
