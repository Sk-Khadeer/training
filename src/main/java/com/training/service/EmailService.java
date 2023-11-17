package com.training.service;

import java.time.LocalDate;
import java.util.Properties;

import org.springframework.stereotype.Service;

import com.training.entity.PurchasedCourse;
import com.training.entity.SignUp;

import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

@Service
public class EmailService {

	public String sendEmail(SignUp register) {
		String subject = "Login Link";
		String from = "944shaikkhadeer@gmail.com";
		String host = "smtp.gmail.com";

		Properties properties = System.getProperties();

		// setting important information to properties object

		// host set
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
//		hhmnhtfcskczulik -------------- MAIL PWD
		// Step 1: to get the session object..
		Session session = Session.getInstance(properties, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, "hhmnhtfcskczulik");
			}

		});

		session.setDebug(true);

		// Step 2 : compose the message [text,multi media]
		MimeMessage m = new MimeMessage(session);
		// Create a MimeMultipart object to hold both text and image

		try {
			// from email
			m.setFrom(from);

			// adding recipient to message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(register.getEmail()));

			// adding subject to message
			m.setSubject(subject);

			// Pre-existing token and from variables
			String confirmationUrl = "http://127.0.0.1:5500/AK_TRAINEE/index.html?token="
					+ register.getConfirmationTokenForLogin()+"&email="+register.getEmail();

			Multipart multipart = new MimeMultipart("related");

			// Create the text part of the email
			MimeBodyPart textPart = new MimeBodyPart();

			String courseBodyContent = "<div style='font-family: Arial, sans-serif; padding: 20px; background-color: #f7f7f7;'>";
			courseBodyContent += "<h2 style='color: #2C3E50;'>Welcome to AK Trainee Institute!</h2>";
			courseBodyContent += "<p style='line-height: 1.5;'>Congratulations on taking the first step towards enhancing your skills with our comprehensive courses. At AK Trainee Institute, we ensure high-quality training tailored to fit the modern tech industry needs.</p>";

			courseBodyContent += "<div style='margin-top: 20px; margin-bottom: 20px; text-align: center;'>";
			courseBodyContent += "<img src='cid:image' alt='Institute Image' style='max-width: 100%; border-radius: 10px; box-shadow: 0 4px 8px rgba(0,0,0,0.1);'/>";
			courseBodyContent += "<div style='margin-top: 10px;'>AK Trainee Institute Campus</div>"; // Caption below
																										// the image
			courseBodyContent += "</div>";

			courseBodyContent += "<h2 style='color: #2C3E50;'>Click on this link to activate your account and start learning: </h2>"
					+ confirmationUrl;
			courseBodyContent += "<h3 style='color: #34495E;'>Courses We Offer:</h3>";
			courseBodyContent += "<ul style='list-style-type: circle; padding-left: 20px;'>";
			courseBodyContent += "<li style='margin-bottom: 10px;'>Cloud Solutions & Architecture</li>";
			courseBodyContent += "<li style='margin-bottom: 10px;'>Database Management & Security</li>";
			courseBodyContent += "<li style='margin-bottom: 10px;'>Web & Mobile Application Development</li>";
			courseBodyContent += "<li>AI & Machine Learning Innovations</li>";
			courseBodyContent += "</ul>";

			courseBodyContent += "<h3 style='color: #34495E; margin-top: 20px;'>Contact & Address:</h3>";
			courseBodyContent += "<p><strong>Email:</strong><a href='mailto:" + from + "'>" + from + "</a><br/>";
			courseBodyContent += "<strong>Address:</strong> 123 Tech Street, Silicon Valley, CA 94000<br/>";
			courseBodyContent += "<strong>Location:</strong> Located at the core of Silicon Valley, a place of tech brilliance.</p>";

			courseBodyContent += "<p>If you encounter any issues or have questions regarding our courses, please respond to this email. We're always here to assist you in your learning journey.</p>";

			courseBodyContent += "<h3 style='color: #34495E; margin-top: 20px;'>Stay Connected:</h3>";
			courseBodyContent += "<p>For the latest updates, course additions, and educational content, make sure to visit our <a href='http://127.0.0.1:5500//Project/index.html' style='color: #2980B9; text-decoration: none;'>Official Website</a>.</p>";
			courseBodyContent += "</div>";

			textPart.setContent(courseBodyContent, "text/html");
			multipart.addBodyPart(textPart);

			// Image setup is the same as before
			MimeBodyPart imagePart = new MimeBodyPart();
			DataSource source = new FileDataSource("C://Users//user181//Documents//images//AK.png"); // Replace with the actual
																								// image path
			imagePart.setDataHandler(new DataHandler(source));
			imagePart.setHeader("Content-ID", "<image>"); // The CID we use to refer to in the HTML
			imagePart.setDisposition(MimeBodyPart.INLINE); // Ensure it's treated as inline and not as attachment
			multipart.addBodyPart(imagePart);

			// Set the content of the message to be the multipart
			m.setContent(multipart);
			Transport.send(m);
			System.out.println("Sent success...................");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return register.getEmail();
	}

	public String sendPasswordResetEmail(String to, String token) {

		String subject = "forgotpassword";
		String from = "944shaikkhadeer@gmail.com";
		String host = "smtp.gmail.com";
		// Get properties object
		Properties props = new Properties();
		// host set
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");
		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, "hhmnhtfcskczulik");
			}

		});

		session.setDebug(true);

		try {
			// Compose the message
			MimeMessage m = new MimeMessage(session);

			// From email
			m.setFrom(new InternetAddress(from));

			// Adding recipient to the message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(to));

			// Adding subject to the message
			m.setSubject(subject);

			String confirmationUrl = "http://127.0.0.1:5500/AK_TRAINEE/resetPassword.html?token=" + token;
//	        http://127.0.0.1:5500/ResponsivePage/index.html
			String bodyContent = "Click on this link for login : " + confirmationUrl;

			// Set the content of the message
			m.setText(bodyContent);

			// Send the message
			Transport.send(m);

			System.out.println("Sent successfully...");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return to;
	}

	public String sendEmailReminder(SignUp user, PurchasedCourse course, LocalDate expiryDate) {
		String subject = "Course Expiry Reminder";
		String from = "944shaikkhadeer@gmail.com";
		String host = "smtp.gmail.com";

		// Get properties object
		Properties props = new Properties();
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.port", "465");
		props.put("mail.smtp.ssl.enable", "true");
		props.put("mail.smtp.auth", "true");

		Session session = Session.getInstance(props, new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(from, "hhmnhtfcskczulik");
			}
		});

		session.setDebug(true);

		try {
			// Compose the message
			MimeMessage m = new MimeMessage(session);

			// From email
			m.setFrom(new InternetAddress(from));

			// Adding recipient to the message
			m.addRecipient(Message.RecipientType.TO, new InternetAddress(user.getEmail()));

			// Adding subject to the message
			m.setSubject(subject);

//		        String bodyContent = "Hello! This is a friendly reminder that your course, " + course + ", is set to expire on " + expiryDate + ". If you wish to continue, please renew your course. Thank you for choosing us!";

			String bodyContent = "Dear " + user.getName()
					+ ",\n\n We hope you have enjoyed your journey with us thus far!"
					+ " As we value our learners, we'd like to remind you that your course, '" + course.getCourse()
					+ "', is going to expire on " + expiryDate
					+ "\n\nOver these months, we hope you've gained valuable insights and enriched "
					+ "your skills.If you wish to continue learning and diving deeper into the subject, "
					+ "please consider renewing the course before it expires. Our platform is continuously evolving, and there will be more content "
					+ "and updates coming your way!\n\n"
					+ "If you have any questions or need assistance, our support team is here to help."
					+ " Remember, continuous learning is the key to success in today's ever-changing world.\n\n"
					+ "Thank you for choosing us as your learning partner. Let's keep the momentum going!\n\n"
					+ "Warm regards,\n" + "Your AK .";

			// Set the content of the message
			m.setText(bodyContent);

			// Send the message
			Transport.send(m);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return user.getEmail();
	}

}
