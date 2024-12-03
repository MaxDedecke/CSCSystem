package com.css.one.services;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Base64;
import java.util.Properties;

import org.apache.xmlgraphics.image.loader.util.ImageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.util.StreamUtils;

import com.css.one.data.enums.EmailType;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;

import jakarta.mail.MessagingException;

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

    public void sendHtmlMessageWithTemplate(String to, String subject, String userName, EmailType type) throws MessagingException, IOException {
        // HTML-Vorlage laden
    	resourceLoader =  new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(type.getHtml());
        
		if (resource.exists()) {
			String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		    htmlContent = htmlContent.replace("${memberName}", userName);
		    
		    if(type.equals(EmailType.ONBOARING)) {
//		    	htmlContent = htmlContent.replace("${onboardingLink}", "https://cl-os.code-green-systems.de/onboarding/?token=" + generateJwtToken(to));
//		    	htmlContent = htmlContent.replace("${onboardingLink}", "http://localhost:8080/onboarding?token=" + generateToken(to));

		            try (InputStream imageStream = ImageUtil.class.getClassLoader().getResourceAsStream("logoCodeGreen.png")) {
		                if (imageStream == null) {
		                    throw new IOException("Bild konnte nicht geladen werden: " + "logoCodeGreen");
		                }
		                byte[] imageBytes = imageStream.readAllBytes();
		                htmlContent = htmlContent.replace("logoCodeGreen.png","data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes));
		            }
		   
		    }
			// E-Mail senden
			sendHtmlMessage(to, subject, htmlContent);
		} else {
			Notification show = Notification.show("Resource could not be loaded");
			show.addThemeVariants(NotificationVariant.LUMO_ERROR);			
		}
    }
    
    public String sendOnboardingEmail(String to, String subject, String userName, EmailType type, String token) throws MessagingException, IOException {
        // HTML-Vorlage laden
    	resourceLoader =  new DefaultResourceLoader();
        Resource resource = resourceLoader.getResource(type.getHtml());
        
		if (resource.exists()) {
			String htmlContent = StreamUtils.copyToString(resource.getInputStream(), StandardCharsets.UTF_8);
		    htmlContent = htmlContent.replace("${memberName}", userName);
		    
		    if(type.equals(EmailType.ONBOARING)) {
//		    	htmlContent = htmlContent.replace("${onboardingLink}", "https://cl-os.code-green-systems.de/onboarding/?token=" + token);
		    	htmlContent = htmlContent.replace("${onboardingLink}", "http://localhost:8080/onboarding?token=" + token);

		            try (InputStream imageStream = ImageUtil.class.getClassLoader().getResourceAsStream("logoCodeGreen.png")) {
		                if (imageStream == null) {
		                    throw new IOException("Bild konnte nicht geladen werden: " + "logoCodeGreen");
		                }
		                byte[] imageBytes = imageStream.readAllBytes();
		                htmlContent = htmlContent.replace("logoCodeGreen.png","data:image/png;base64," + Base64.getEncoder().encodeToString(imageBytes));
		            }
		   
		    }
			// E-Mail senden
			sendHtmlMessage(to, subject, htmlContent);
		} else {
			Notification show = Notification.show("Resource could not be loaded");
			show.addThemeVariants(NotificationVariant.LUMO_ERROR);			
		}
		return token;
    }

	public void sendHtmlMessage(String to, String subject, String htmlBody) throws MessagingException {
        MimeMessageHelper message = new MimeMessageHelper(mailSender.createMimeMessage(), "utf-8");
		Properties properties = PropertyService.getProperties();

        message.setTo(to);
        message.setFrom(properties.getProperty("spring.mail.username"));
        message.setSubject(subject);
        message.setText(htmlBody, true);
        message.setSentDate(Date.valueOf(LocalDate.now()));
        
        mailSender.send(message.getMimeMessage());
    }
}
