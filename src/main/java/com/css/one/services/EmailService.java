package com.css.one.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Properties;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import com.css.one.views.warenlager.WarenlagerView;

public class EmailService {
	
	JavaMailSenderImpl mailSender;
	String propertyHost;
	String propertyPort;
	String propertyUserName;
	String propertyPassword;
	String propertySmtpAuth;
	String propertyStartTls;
	
	public EmailService() {
		
		mailSender = new JavaMailSenderImpl();
    	
		Properties properties = getProperties();
		propertyHost = properties.getProperty("spring.mail.host");
		propertyPort = properties.getProperty("spring.mail.port");
		propertyUserName = properties.getProperty("spring.mail.username");
		propertyPassword = properties.getProperty("spring.mail.password");
		propertySmtpAuth = properties.getProperty("spring.mail.properties.mail.smtp.auth");
		propertyStartTls = properties.getProperty("spring.mail.properties.mail.smtp.starttls.enable");	
		
        mailSender.setHost(propertyHost);
        mailSender.setPort(Integer.parseUnsignedInt(propertyPort));
        mailSender.setUsername(propertyUserName);
        mailSender.setPassword(propertyPassword);
	}
	
    public void sendSimpleMessage(String to, String subject, String text) {

        Properties props = mailSender.getJavaMailProperties();
		Properties properties = getProperties();

        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", propertySmtpAuth);
        props.put("mail.smtp.starttls.enable", propertyStartTls);
        props.put("mail.debug", "true");
                
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(properties.getProperty("spring.mail.username"));
        message.setSubject(subject);
        message.setText(text);
        message.setSentDate(Date.valueOf(LocalDate.now()));
        mailSender.send(message);
    }
    
    private Properties getProperties() {
    	
    	final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(new File("/application.properties"))) {

			// Load the properties file
			properties.load(input);
		} catch (IOException ex) {
			try (InputStream input = WarenlagerView.class.getClassLoader()
					.getResourceAsStream("application.properties")) {
				if (input == null) {
					System.out.println("Sorry, unable to find application.properties");
					System.exit(1);
				}

				// Load the properties file
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return properties;
    }
}
