package com.pnlorf.util;

import org.apache.ibatis.session.defaults.DefaultSqlSession;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.sound.midi.Track;
import java.util.Date;
import java.util.Properties;

/**
 * Created by 冰诺莫语 on 2015/10/27.
 */
public class EmailUtils {

    /**
     * 发送邮件(暂时只支持163邮箱发送)
     *
     * @param fromEmail     发送邮箱
     * @param toEmail       接收邮箱
     * @param emailName     163邮箱登录名
     * @param emailPassword 密码
     * @param title         发送主题
     * @param content       发送内容
     * @throws Exception
     */
    public static void sendMail(String fromEmail, String toEmail, String emailName, String emailPassword, String title, String content) throws Exception {
        Properties properties = new Properties(); //创建Properties对象
        properties.setProperty("mail.transport.protocol", "smtp");//设置传输协议
        properties.put("mail.smtp.host", "smtp.163.com"); //设置发信邮箱的smtp地址
        properties.setProperty("mail.smtp.auth", "true"); //验证
        Authenticator auth = new AJavaAuthenticator(emailName, emailPassword); //使用验证，创建一个Authenticator
        Session session = Session.getDefaultInstance(properties, auth); //根据Properties，Authenticator创建Session
        Message message = new MimeMessage(session); //Message存储发送的电子邮件信息
        message.setFrom(new InternetAddress(fromEmail));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(toEmail));//设置收信邮箱
        // 指定邮件内容及ContentType和编码方式
        message.setContent(content, "text/html;charset=utf-8");
        message.setSubject(title);//设置主题
        message.setSentDate(new Date()); // 设置发信时间
        Transport.send(message); //发送

    }

    static class AJavaAuthenticator extends Authenticator {
        private String user;
        private String password;

        public AJavaAuthenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(user, password);
        }
    }
}
