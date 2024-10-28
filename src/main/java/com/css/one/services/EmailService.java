package com.css.one.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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

import com.css.one.views.warenlager.WarenlagerView;

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

		Properties properties = getProperties();            
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
		Properties properties = getProperties();

        message.setTo(to);
        message.setFrom(properties.getProperty("spring.mail.username"));
        message.setSubject(subject);
        message.setText(htmlBody, true);
        message.setSentDate(Date.valueOf(LocalDate.now()));
        
        mailSender.send(message.getMimeMessage());

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
