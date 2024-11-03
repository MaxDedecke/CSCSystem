package com.css.one.services;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StreamUtils;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
	
	@Autowired
	private ResourceLoader resourceLoader;
	
	JavaMailSenderImpl mailSender;
	
	String propertyHost;
	String propertyPort;
	String propertyUserName;
	String propertyPassword;
	String propertySmtpAuth;
	String propertyStartTls;
	String propertyCapabilitiesAfterAuth;
	
	public EmailService() {
		
		mailSender = new JavaMailSenderImpl();
    	
		Properties properties = PropertyService.getProperties();		
		mailSender.setJavaMailProperties(properties);
		mailSender.setHost(properties.getProperty("spring.mail.host"));
		mailSender.setPort(Integer.parseUnsignedInt(properties.getProperty("spring.mail.port")));
		mailSender.setUsername(properties.getProperty("spring.mail.username"));
		mailSender.setPassword(properties.getProperty("spring.mail.password"));
		Properties prop = new Properties();
		prop.setProperty("mail.smtp.auth", properties.getProperty("spring.mail.properties.mail.smtp.auth"));
		prop.setProperty("mail.smtp.starttls.enable",  properties.getProperty("spring.mail.properties.mail.smtp.starttls.enable"));
		mailSender.setJavaMailProperties(prop);
	}
	
    public void sendSimpleMessage(String to, String subject, String text) {

		Properties properties = PropertyService.getProperties();            
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(properties.getProperty("spring.mail.username"));
        message.setSubject(subject);
        message.setText(text);
        message.setSentDate(Date.valueOf(LocalDate.now()));
        mailSender.send(message);
    }

    public void sendHtmlMessageWithTemplate(String to, String subject) throws MessagingException, IOException {
        // HTML-Vorlage laden
        Resource resource = resourceLoader.getResource("classpath:templates/welcome_template.html");
        String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
//      TODO replace with user data in template
//      htmlContent = htmlContent.replace("${userName}", userName);

        // E-Mail senden
        sendHtmlMessage(to, subject, htmlContent);
    }

    public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper message = new MimeMessageHelper(mimeMessage, "utf-8");
		Properties properties = PropertyService.getProperties();

        message.setTo(to);
        message.setFrom(properties.getProperty("spring.mail.username"));
        message.setSubject(subject);
        message.setText(htmlBody, true);
        message.setSentDate(Date.valueOf(LocalDate.now()));
        
        mailSender.send(message.getMimeMessage());

    }
}
