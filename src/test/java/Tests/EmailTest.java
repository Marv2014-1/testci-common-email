package Tests;
import static org.junit.Assert.*;

import org.apache.commons.mail.EmailException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.Properties;

import javax.mail.Session;
import javax.mail.internet.InternetAddress;


public class EmailTest {

	private final String[] TEST_EMAILS = { "ab@bc.com", "a.b@c.org", "asdfwqesad@asdfasgasd.com.bd" };
	private final String[] TEST_EMPTY_EMAILS = new String[0];
	
	private final String SINGLE_EMAIL = "ab@bc.com";

    private final String[] TEST_CHARS = { " ", "a", "A", "/uc5ec", "0123456789", "214812512ldnaslnfa" };
    private final String[] TEST_EMPTY_CHARS = new String[0];
    
    private final String SUBJECT = "TEST";
    private final String HOST = "HOST";
    private final String CONTENT = "CONTECNT";
    private final String CONTENT_TYPE = "text/plain";

    private ConcreteEmail email;

    /**
     * this is the setup method for future tests
     * @throws Exception
     */
    @Before
    public void setUpEmailTest() throws Exception {
    	System.out.println("Test setup");
        email = new ConcreteEmail();
    }

    /**
     * this is the tear down method for future tests
     * @throws Exception
     */
    @After
    public void tearDownEmail() throws Exception {
    	System.out.println("Test teardown");
    	email = null;
    	assertNull(email);
    }

    /**
     * Testing addBcc(String... emails) method with contents in array
     * 
     * @throws Exception
     */
    @Test
    public void testAddBcc() throws Exception {
        email.addBcc(TEST_EMAILS);
        assertEquals(3, email.getBccAddresses().size());
    }
    
    /**
     * Testing the addBcc(String... emails) method with empty array
     * 
     * @throws Exception
     */
    @Test
    public void testAddEmptyBcc() throws Exception {
    	
    	EmailException thrown = assertThrows(EmailException.class, () -> {
    		email.addBcc(TEST_EMPTY_EMAILS);
        });

    	assertTrue(thrown.getMessage().contains("Address List provided was invalid"));
    }

    /**
     * Testing the addCc(String email) method
     * 
     * @throws Exception
     */
    @Test
    public void testAddCc() throws Exception {
        email.addCc(SINGLE_EMAIL);

        assertTrue(email.getCcAddresses().size() == 1);
    }

    /**
     * Testing the addHeader(String name, String value) method with correct input
     * 
     */
    @Test
    public void testAddHeaderCorrectly() {
        email.addHeader("name", "value");
    }

    /**
     * Testing the addHeader(String name, String value) method with empty name
     * 
     */
    @Test
    public void testAddHeaderWithoutName() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            email.addHeader("", "value");
        });

        // Assert what the exception to be thrown is
        assertTrue(thrown.getMessage().contains("name can not be null or empty"));
    }

    /**
     * Testing the addHeader(String name, String value) method with empty value
     * 
     */
    @Test
    public void testAddHeaderWithoutValue() {
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            email.addHeader("name", "");
        });

        // Assert what the exception to be thrown is
        assertTrue(thrown.getMessage().contains("value can not be null or empty"));
    }

    /**
     * this method tests the addReplyTo(String email, String name) method
     * 
     * @throws EmailException 
     */
    @Test
    public void testAddReplyTo() throws EmailException {
        email.addReplyTo("email@test.com", "Marvin");
        
        assertEquals(1, email.getReplyToAddresses().size());
    }
    
    /**
     * tests the buildMimeMessage() without a from address
     * 
     */
    @Test
    public void testBuildMimeMessageNoFromAddress() {
    	EmailException thrown = assertThrows(EmailException.class, () -> {
    		email.setSubject(SUBJECT);
    		email.setHostName(HOST);
    		email.setContent(CONTENT, CONTENT_TYPE);
    		
            email.buildMimeMessage();
        });
    	
    	assertTrue(thrown.getMessage().contains("From address required"));
    }
    
    /**
     * tests the buildMimeMessage() by not having a receiver address
     * 
     */
    @Test
    public void testBuildMimeMessageWithoutReciver() {
    	EmailException thrown = assertThrows(EmailException.class, () -> {
    		email.setContent(CONTENT, CONTENT);
    		email.setSubject(SUBJECT);
    		email.setHostName(HOST);
    		email.setFrom(SINGLE_EMAIL);
    		
            email.buildMimeMessage();
        });
    	
    	assertTrue(thrown.getMessage().contains("At least one receiver address required"));
    }
    
    /**
     * tests the buildMimeMessage() by attempting to use PopBeforeSmtp
     * 
     */
    @Test
    public void testBuildMimeMessagePopBeforeSmtp() {
    	EmailException thrown = assertThrows(EmailException.class, () -> {
    		email.addBcc(TEST_EMAILS);
    		email.addCc(SINGLE_EMAIL);
    		email.addHeader(SUBJECT, SUBJECT);
    		
    		email.setContent(CONTENT, CONTENT);
    		email.setSubject(SUBJECT);
    		email.setHostName(HOST);
    		email.setFrom(SINGLE_EMAIL);
    		email.setPopBeforeSmtp(true, HOST, CONTENT_TYPE, CONTENT);
    		
            email.buildMimeMessage();
        });
    }
    
    /**
     * tests the buildMimeMessage() by calling it twice
     * 
     */
    @Test
    public void testBuildMimeMessage() {
    	IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
    		email.addBcc(TEST_EMAILS);
    		email.addCc(SINGLE_EMAIL);
    		email.addHeader(SUBJECT, SUBJECT);
    		
    		email.setContent(CONTENT, CONTENT);
    		email.setSubject(SUBJECT);
    		email.setHostName(HOST);
    		email.setFrom(SINGLE_EMAIL);
    		
            email.buildMimeMessage();
            email.buildMimeMessage();
        });
    	
    	assertTrue(thrown.getMessage().contains("The MimeMessage is already built."));
    }
    
    /**
     * tests the getHostName() method with provided hostName
     * 
     */
    @Test
    public void testGetHostName() {
    	email.setHostName(HOST);
    	assertEquals(HOST, email.getHostName());
    }
    
    /**
     * tests the getHostName() method without proper set up
     * 
     */
    @Test
    public void testGetHostNameWithSession() {
    	Properties props = new Properties();
    	Session ses = Session.getInstance(props);
    	
    	email.setMailSession(ses);
    	assertEquals(null, email.getHostName());
    }
    
    
    /**
     * tests the getMailSession() method
     * 
     * @throws EmailException 
     */
    @Test
    public void getMailSession() throws EmailException {
    	email.setBounceAddress(SINGLE_EMAIL);
    	email.setSSLOnConnect(true);
    	email.setHostName(HOST);
    	
    	Session sesTest1 = email.getMailSession();
    	Session sestest2 = email.getMailSession();
    	
    	boolean test = sesTest1.equals(sestest2);
    	assertTrue(test);
    }
    
    /**
     * test the getSentDate() method
     * 
     */
    @Test
    public void TestGetSentDate() {
    	//just here for code coverage :)
    	email.getSentDate();
    	
    	Date date1 = new Date();
    	email.setSentDate(date1);
    	Date date2 = email.getSentDate();
    	boolean test = date1.equals(date2);
    	
    	assertTrue(test);
    }
    
    /**
     * test the getSocketConnectionTimeout() method
     * 
     */
    @Test
    public void getSocketConnectionTimeout() {
    	email.setSocketConnectionTimeout(404);
    
    	assertEquals(404, email.getSocketConnectionTimeout());
    }
    
    /**
     * tests the setFrom(String email) method
     * 
     * @throws EmailException 
     */
    @Test
    public void setFrom() throws EmailException {
    	email.setFrom(SINGLE_EMAIL);
    	InternetAddress add = email.getFromAddress();
    	
    	assertTrue(add.getAddress().equals(SINGLE_EMAIL));
    }
}
