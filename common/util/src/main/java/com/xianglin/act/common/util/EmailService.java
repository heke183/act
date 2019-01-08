package com.xianglin.act.common.util;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.io.Charsets;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;
import java.util.Set;

/**
 * @author Yungyu
 * @description Created by Yungyu on 2018/5/2 18:02.
 */
public class EmailService implements InitializingBean {

    private JavaMailSender mailSender;

    private String host;

    private String port;

    private String username;

    private String password;

    /**
     * 发送简单邮件
     *
     * @param simpleEmail 简单邮件详情
     */
    @Async
    public void sendEmail(SimpleEmail simpleEmail) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, simpleEmail.isAttachment());
            /**
             * 添加发送者
             */
            helper.setFrom(username);
            Set<String> toSet = simpleEmail.getToSet();
            /**
             * 添加接收者
             */
            helper.setTo(toSet.toArray(new String[toSet.size()]));
            /**
             * 添加主题
             */
            helper.setSubject(simpleEmail.getSubject());
            /**
             * 添加正文
             */
            helper.setText(simpleEmail.getContent(), simpleEmail.isHtml());
            /**
             * 添加附件
             */
            if (simpleEmail.isAttachment()) {
                Map<String, File> attachments = simpleEmail.getAttachments();
                if (attachments != null) {
                    for (Map.Entry<String, File> attach : attachments.entrySet()) {
                        helper.addAttachment(attach.getKey(), attach.getValue());
                    }
                }
            }
            mailSender.send(message);  //发送
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        Preconditions.checkArgument(!Strings.isNullOrEmpty(host));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(port));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(username));
        Preconditions.checkArgument(!Strings.isNullOrEmpty(password));

        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setDefaultEncoding(Charsets.UTF_8.displayName());
        javaMailSender.setHost(host);
        javaMailSender.setPassword(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        mailSender = javaMailSender;
    }

    public JavaMailSender getMailSender() {

        return mailSender;
    }

    public void setMailSender(JavaMailSender mailSender) {

        this.mailSender = mailSender;
    }

    public String getHost() {

        return host;
    }

    public void setHost(String host) {

        this.host = host;
    }

    public String getPort() {

        return port;
    }

    public void setPort(String port) {

        this.port = port;
    }

    public String getUsername() {

        return username;
    }

    public void setUsername(String username) {

        this.username = username;
    }

    public String getPassword() {

        return password;
    }

    public void setPassword(String password) {

        this.password = password;
    }
}
