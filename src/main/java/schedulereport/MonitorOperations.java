package schedulereport;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

public class MonitorOperations{
	private static Connection dbConnection 	= null;
	private static Properties properties 	= new Properties();
	
	private static String configPath, monitorTypesConfigString, highLightConfig;
	private static List<String> monitorTypes, highLightStrings;
	
	
	private String hostname, portString, sid, username, password, sqlFilePath;
	private String environment, smtpHost, smtpUser, toEmail, ccEmail, subject; 
	private boolean sendEmail;
	private static String DB_CONNECTION;

	private void init() throws Exception{
		initializeProperties();
		initEmail();
	}

	private void initEmail(){
		this.environment 	= getProperty(Constants.PROPERTY_ENVIRONMENT);
		this.smtpHost 		= getProperty(Constants.PROPERTY_SMTP_HOST);
		this.smtpUser 		= getProperty(Constants.PROPERTY_SMTP_USER);
		this.toEmail 		= getProperty(Constants.PROPERTY_TO_EMAIL);
		this.ccEmail 		= getProperty(Constants.PROPERTY_CC_EMAIL);
		this.subject 		= getProperty(Constants.PROPERTY_SUBJECT);
		this.sendEmail 		= "true".equalsIgnoreCase(getProperty(Constants.PROPERTY_BOOL_EMAIL));
	}

	private void initializeProperties() throws Exception{
		configPath = configPath == null ? System.getProperty(Constants.PROPERTY_CONFIG_PATH) : configPath;
		if((configPath != null) &&(!configPath.isEmpty())){
			InputStream input = null;
			try{
				input = new FileInputStream(configPath);
				properties.load(input);
			}catch(Exception e){
				throw new IllegalArgumentException(Constants.PROPERTY_CONFIG_ERR1, e);
			}finally{
				if(input != null){
					input.close();
				}
			}
		}else{
			throw new IllegalArgumentException(Constants.PROPERTY_CONFIG_ERR1);
		}

		monitorTypesConfigString = monitorTypesConfigString == null ? System.getProperty(Constants.PROPERTY_MONITOR_TYPES) : monitorTypesConfigString;
		monitorTypes = (monitorTypesConfigString != null) &&(!monitorTypesConfigString.isEmpty()) 
				? Arrays.asList(monitorTypesConfigString.split(Constants.COMMA)) : new ArrayList<String>();

		highLightConfig = highLightConfig == null ? getProperty(Constants.PROPERTY_HIGHLIGHT_STR) : highLightConfig;
		highLightStrings =(highLightConfig != null) &&(!highLightConfig.isEmpty()) ? Arrays.asList(highLightConfig.split(Constants.COMMA)) : new ArrayList<String>();
	}

	private String getProperty(String keyword){
		String propertyValue = null;
		if(properties != null){
			propertyValue = properties.getProperty(keyword);
		}
		return propertyValue;
	}

	private void initDatabaseConnection(String database) throws Exception{
		this.hostname 		= getProperty(database + Constants.PROPERTY_HOSTNAME);
		this.portString 	= getProperty(database + Constants.PROPERTY_PORTSTRING);
		this.sid 			= getProperty(database + Constants.PROPERTY_SID);
		this.username 		= getProperty(database + Constants.PROPERTY_DB_USERNAME);
		this.password 		= getProperty(database + Constants.PROPERTY_DB_PASSWORD);
		this.sqlFilePath 	= getProperty(database + Constants.PROPERTY_SQLFILEPATH);
		DB_CONNECTION 		= "jdbc:oracle:thin:@" + this.hostname + ":" + this.portString + ":" + this.sid;
		
		Class.forName(Constants.DB_DRIVER);
		dbConnection 		= DriverManager.getConnection(DB_CONNECTION, this.username, this.password);
	}

	private String processQueries(String database) throws Exception{
		database = !database.endsWith(Constants.DOT) ? database+Constants.DOT : database;
		initDatabaseConnection(database);
		String emailBodyString = Constants.EMPTY_STRING;
		BufferedReader reader = null;
		try{
			File sqlFile = new File(this.sqlFilePath);
			reader = new BufferedReader(new FileReader(sqlFile));
			
			String readLine = Constants.EMPTY_STRING;
			while((readLine = reader.readLine()) != null){
				if((readLine != null) &&(!readLine.isEmpty())){
					System.out.println("Executing " + readLine);
					if(readLine.startsWith(Constants.DB_COMMENT_TITLE)){
						readLine = readLine.replace(Constants.DB_COMMENT_TITLE, Constants.EMPTY_STRING);
						emailBodyString = emailBodyString + Constants.TITLE_SPAN_START + readLine + Constants.TITLE_SPAN_END;
					}else if(readLine.startsWith(Constants.DB_COMMENT)){
						readLine = readLine.replace(Constants.DB_COMMENT, Constants.EMPTY_STRING);
						emailBodyString = emailBodyString + Constants.SUBTITLE_SPAN_START + readLine + Constants.SUBTITLE_SPAN_END;
					}else{
						if(readLine.endsWith(Constants.SEMI_COLON)){
							readLine = readLine.replace(Constants.SEMI_COLON, Constants.EMPTY_STRING);
						}
						emailBodyString = emailBodyString + Constants.QUERY_SPAN_START + readLine + Constants.QUERY_SPAN_END;
						Statement statement = null;
						ResultSet result = null;
						try{
							statement = dbConnection.createStatement();
							result = statement.executeQuery(readLine);
							emailBodyString = emailBodyString + write(result);
						}catch(Exception e){
							e.printStackTrace();
						}finally{
							if((statement != null) &&(!statement.isClosed())){
								statement.close();
							}
							if((result != null) &&(!result.isClosed())) {
								result.close();
							}
						}
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			reader.close();
			if((dbConnection != null) &&(!dbConnection.isClosed())){
				dbConnection.close();
			}
		}
		return emailBodyString;
	}

	private String write(ResultSet rs) throws IOException, SQLException{
		String resultTable = Constants.EMPTY_STRING;
		ResultSetMetaData md = rs.getMetaData();
		int count = md.getColumnCount();
		resultTable = resultTable + "<table border=1>";
		resultTable = resultTable + "<tr>";
		for(int i = 1; i <= count; i++){
			resultTable = resultTable + "<th>";
			resultTable = resultTable + md.getColumnLabel(i);
			resultTable = resultTable + "</th>";
		}
		resultTable = resultTable + "</tr>";
		
		while(rs.next()){
			resultTable = resultTable + "<tr>";
			for(int i = 1; i <= count; i++){
				String resultString = rs.getString(i);
				resultString =(resultString != null) &&(isHighLight(resultString)) ? 
						"<b><span style='color:red'>" + resultString + "</span></b>" : resultString;
				resultTable = resultTable + "<td>";
				resultTable = resultTable + resultString;
				resultTable = resultTable + "</td>";
			}
			resultTable = resultTable + "</tr>";
		}
		resultTable = resultTable + "</table>";
		resultTable = resultTable + "<br/>";
		return resultTable;
	}

	private boolean isHighLight(String resultString){
		boolean isHighLightResult = false;
		if((highLightStrings != null) &&(!highLightStrings.isEmpty())){
			for(String highLightString : highLightStrings){
				if((highLightString != null) && (resultString.contains(highLightString) || resultString.contains(highLightString.toLowerCase()) || resultString.contains(highLightString.toUpperCase()))){
					return true;
				}
			}
		}
		return isHighLightResult;
	}

	private void checkAndSendEmail(String bodyString, Map<String, String> attachmentFiles) throws UnsupportedEncodingException, MessagingException{
		if((bodyString != null) &&(!bodyString.isEmpty()) &&(this.sendEmail)){
			Properties props = System.getProperties();
			props.put(Constants.MAIL_SMTP_HOST, this.smtpHost);
			props.put(Constants.MAIL_SMTP_USER, this.smtpUser);
			props.put(Constants.MAIL_SMTP_FROM, this.smtpUser);
			Session session = Session.getInstance(props, null);

			MimeMessage msg = new MimeMessage(session);
			msg.addHeader(Constants.CONTENT_TYPE_KEY, Constants.CONTENT_TYPE_VAL);
			msg.addHeader(Constants.FORMAT_KEY, Constants.FORMAT_VAL);
			msg.addHeader(Constants.CONTENT_TRFR_ENCDG_KEY, Constants.CONTENT_TRFR_ENCDG_VAL);
			msg.setFrom(new InternetAddress(this.smtpUser, this.environment));
			msg.setReplyTo(InternetAddress.parse(this.smtpUser, false));
			msg.setSubject(this.subject, Constants.UTF_ENCODING);
			msg.setSentDate(new Date());
			if(this.toEmail != null){
				msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(this.toEmail, false));
			}
			if(this.ccEmail != null){
				msg.setRecipients(Message.RecipientType.CC, InternetAddress.parse(this.ccEmail, false));
			}

			Multipart multipart = new MimeMultipart();
			if((attachmentFiles != null) &&(!attachmentFiles.isEmpty())){
				for(String fileName : attachmentFiles.keySet()){
					String attachString 		=(String)attachmentFiles.get(fileName);
					BodyPart messageBodyPart 	= new MimeBodyPart();
					DataSource source 			= new ByteArrayDataSource(attachString.getBytes(Constants.UTF_ENCODING), Constants.STREAM_TYPE);
					messageBodyPart.setDataHandler(new DataHandler(source));
					messageBodyPart.setFileName(fileName);
					multipart.addBodyPart(messageBodyPart);
				}
			}

			if((bodyString != null) &&(!bodyString.isEmpty())){
				BodyPart messageBodyPart = new MimeBodyPart();
				messageBodyPart.setContent(bodyString, Constants.CONTENT_TYPE_HTML);
				multipart.addBodyPart(messageBodyPart);
			}
			
			msg.setContent(multipart);
			Transport.send(msg);
		}
	}

	private String getAttachFileName(String database){
		database = !database.endsWith(Constants.DOT) ? database+Constants.DOT : database;
		return Constants.TRUE_STRING.equalsIgnoreCase(getProperty(database+ Constants.PROPERTY_ATTACHMENT)) ? getProperty(database + Constants.PROPERTY_RESULTFILENAME) : null;
	}

	public static void main(String[] args) throws Exception{
		try{
			MonitorOperations monitor = new MonitorOperations();
			monitor.init();

			Map<String, String> attachmentFiles = new HashMap<String, String>();
			String emailBodyString = Constants.HTML_HEADER_START + monitor.getProperty(Constants.PROPERTY_TITLE) + Constants.HTML_HEADER_END;
			if((monitorTypes != null) &&(!monitorTypes.isEmpty())){
				for(String monitorType : monitorTypes){
					String attachmentFileName = monitor.getAttachFileName(monitorType);
					if(attachmentFileName != null){
						String emailAttachString = Constants.HTML_HEADER_START + monitor.getProperty(Constants.PROPERTY_TITLE) + Constants.HTML_HEADER_END + monitor.processQueries(monitorType) + Constants.HTML_FOOTER;
						attachmentFiles.put(attachmentFileName, emailAttachString);
					}else{
						emailBodyString = emailBodyString + monitor.processQueries(monitorType);
					}
				}
			}
			emailBodyString = emailBodyString + Constants.HTML_FOOTER;
			System.out.println("Sending email ... ");
			monitor.checkAndSendEmail(emailBodyString, attachmentFiles);
		}catch(Exception e){
			e.printStackTrace();
		}finally{
			if((dbConnection != null) &&(!dbConnection.isClosed())){
				dbConnection.close();
			}
		}
	}
}