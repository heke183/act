package com.xianglin.act.web.home.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
public class HeartBeatController {

    @RequestMapping(method = {RequestMethod.GET}, value = "/stat/alive")
    public void heartBeat(HttpServletRequest req, HttpServletResponse resp) {
        PrintWriter writer = null;
        try {
            resp.setStatus(200);
            resp.setContentType("text/html;charset=UTF-8");
            writer = resp.getWriter();
            writer.write("act");
            writer.write("访问来源" + req.getRemoteAddr());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }
}