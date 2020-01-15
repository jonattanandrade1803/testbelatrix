package  belatrix_test;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

public class JobLoggerTest {
    static JobLogger instance = null;
    
    public JobLoggerTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
        try {
            Map databasedata = new HashMap<>();
            databasedata.put("userName", "testbelatrix");
            databasedata.put("password", "testbelatrix");
            databasedata.put("dbms", "postgresql");
            databasedata.put("serverName", "aresdbinstance.cpuookbpofy0.us-east-1.rds.amazonaws.com");
            databasedata.put("portNumber", "5432");
            databasedata.put("dbName", "postgres");
            databasedata.put("logFileFolder", "C:\\test");
            instance = new JobLogger(true, true,true,true,true,true,databasedata);
        } catch (Exception ex) {
            Logger.getLogger(JobLoggerTest.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of LogError method, of class JobLogger.
     */
    @Test
    public void testLogError() throws Exception {
        System.out.println("LogError");
        String messageText_in = "Error message";
        
        try{
            instance.LogError(messageText_in);
        }
        catch(Exception e){
            fail("The test case fail. Exception: "+e.getMessage());
        }
        
    }

    /**
     * Test of LogMessage method, of class JobLogger.
     */
    @Test
    public void testLogMessage() throws Exception {
        System.out.println("LogMessage");
        String messageText_in = "log message";
        try{
            instance.LogMessage(messageText_in);
        }
        catch(Exception e){
            fail("The test case fail. Exception: "+e.getMessage());
        }
    }

    /**
     * Test of LogWarning method, of class JobLogger.
     */
    @Test
    public void testLogWarning() throws Exception {
        System.out.println("LogWarning");
        String messageText_in = "Warning message";
        try{
            instance.LogWarning(messageText_in);
        }
        catch(Exception e){
            fail("The test case fail. Exception: "+e.getMessage());
        }
    }

 
    
}
