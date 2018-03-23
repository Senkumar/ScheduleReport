package schedulereport;

public class Constants{
	public static final String DB_DRIVER				= "oracle.jdbc.driver.OracleDriver";

	public static final String TRUE_STRING 				= "true";
	public static final String DB_COMMENT 				= "--";
	public static final String DB_COMMENT_TITLE			= "--Title:";

	public static final String UTF_ENCODING 			= "UTF-8";
	public static final String EMPTY_STRING 			= "";
	public static final String FAIL_LOWER_STR 			= "fail";
	public static final String FAIL_UPPER_STR 			= "FAIL";
	public static final String SEMI_COLON 				= ";";
	public static final String COMMA 					= ",";
	public static final String DOT 						= ".";

	public static final String CONTENT_TYPE_KEY 		= "Content-type";
	public static final String CONTENT_TYPE_VAL 		= "text/HTML; charset=UTF-8";
	public static final String CONTENT_TYPE_HTML		= "text/html";

	public static final String FORMAT_KEY 				= "format";
	public static final String FORMAT_VAL 				= "flowed";

	public static final String CONTENT_TRFR_ENCDG_KEY 	= "Content-Transfer-Encoding";
	public static final String CONTENT_TRFR_ENCDG_VAL 	= "8bit";

	public static final String MAIL_SMTP_HOST 			= "mail.smtp.host";
	public static final String MAIL_SMTP_USER 			= "mail.smtp.user";
	public static final String MAIL_SMTP_FROM 			= "mail.smtp.from";

	public static final String STREAM_TYPE 				= "application/octet-stream";

	public static final String PROPERTY_CONFIG_PATH		= "com.schedulereport.monitorconfig";
	public static final String PROPERTY_MONITOR_TYPES 	= "monitor.types";
	public static final String PROPERTY_CONFIG_ERR1		= "Property File not configured properly.";
	public static final String PROPERTY_ENVIRONMENT		= "env";
	public static final String PROPERTY_TITLE 			= "title";
	public static final String PROPERTY_HIGHLIGHT_STR 	= "highlight.string";

	public static final String PROPERTY_NEXTGPREFIX		= "nextg.";
	public static final String PROPERTY_CMDBPREFIX 		= "cmdb.";

	public static final String PROPERTY_HOSTNAME 		= "hostname";
	public static final String PROPERTY_PORTSTRING 		= "port";
	public static final String PROPERTY_SID 			= "sid";
	public static final String PROPERTY_DB_USERNAME 	= "username";
	public static final String PROPERTY_DB_PASSWORD 	= "password";

	public static final String PROPERTY_ATTACHMENT 		= "isattach";
	public static final String PROPERTY_RESULTFILENAME 	= "resultfilename";
	public static final String PROPERTY_SQLFILEPATH 	= "sqlfilepath";

	public static final String HTML_HEADER_START 		= "<html><head><style>html *{  font-size: 10px;  color: #000;  font-family: Tahoma !important;}table, th, td {    font-size: 10px;    font-family: Tahoma !important;    border: 1px solid black;    border-collapse: collapse;}th {    background-color:DodgerBlue;    color:white;}</style></head><body><b><u><span style='font-family: Tahoma; font-size:16px;'>";
	public static final String HTML_HEADER_END 			= "</span></u></b><br/><br/>";
	public static final String HTML_FOOTER 				= "</body></html>";

	public static final String FAILURE_RED_SPAN_START 	= "<b><span style='color:red'>";
	public static final String FAILURE_RED_SPAN_END 	= "</span></b>";

	public static final String TITLE_SPAN_START 		= "<b><u><span style='font-size:14px; font-family: Tahoma;'>";
	public static final String TITLE_SPAN_END 			= "</span></u></b><br/>";

	public static final String SUBTITLE_SPAN_START 		= "<b><u><span style='font-size:12px; font-family: Tahoma;'>";
	public static final String SUBTITLE_SPAN_END 		= "</span></u></b><br/>";

	public static final String QUERY_SPAN_START 		= "<i><span style='font-size:8px;font-family: Tahoma;'>";
	public static final String QUERY_SPAN_END			= "</span></i><br/>";

	public static final String PROPERTY_SMTP_HOST 		= "smtp.host";
	public static final String PROPERTY_SMTP_USER 		= "smtp.user";
	public static final String PROPERTY_TO_EMAIL 		= "toemail";
	public static final String PROPERTY_CC_EMAIL 		= "ccemail";
	public static final String PROPERTY_SUBJECT 		= "subject";
	public static final String PROPERTY_BOOL_EMAIL 		= "sendEmail";
}