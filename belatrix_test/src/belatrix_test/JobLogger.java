package belatrix_test;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.text.DateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
    * Class to log different messages throughout an application
    *
    * @author Jonattan Andrade
    * @version 1.0
*/
public class JobLogger {
    private final boolean logToFile;
    private final boolean logToConsole;
    private final boolean logMessage;
    private final boolean logWarning;
    private final boolean logError;
    private final boolean logToDatabase;
    private final Map dbParams;
    private final Logger logger;
    private Connection connection;

    /**
     * JobLogger Class Builder
     * @param logToFileParam It defines if the object can log in a file
     * @param logToConsoleParam It defines if the object can log in console
     * @param logToDatabaseParam It defines if the object can log in database
     * @param logMessageParam It defines if the object can log a message
     * @param logWarningParam It defines if the object can log a warning
     * @param logErrorParam It defines if the object can log a error
     * @param dbParamsMap A Map object with userName, password, dbms, serverName and portNumber params to connect with a database
    */
    public JobLogger(boolean logToFileParam, boolean logToConsoleParam, boolean logToDatabaseParam,
       boolean logMessageParam, boolean logWarningParam, boolean logErrorParam, Map dbParamsMap) throws Exception {

        logger = Logger.getLogger("MyLog");  
        logError = logErrorParam;
        logMessage = logMessageParam;
        logWarning = logWarningParam;
        logToDatabase = logToDatabaseParam;
        logToFile = logToFileParam;
        logToConsole = logToConsoleParam;
        dbParams = dbParamsMap;

        if (!logToConsole && !logToFile && !logToDatabase) {
                throw new Exception("Invalid configuration");
        }

        if ((!logError && !logMessage && !logWarning)) {
                throw new Exception("Error or Warning or Message must be specified");
        }
        
        if(logToFile){
            if (dbParams == null) {
                    throw new Exception("dbParamsMap param must be specified");
            }
        }

        connection = null;
        if(logToDatabase){
            Properties connectionProps = new Properties();
            connectionProps.put("user", dbParams.get("userName"));
            connectionProps.put("password", dbParams.get("password"));
            connection = DriverManager.getConnection("jdbc:" + dbParams.get("dbms") + "://" + dbParams.get("serverName")
                            + ":" + dbParams.get("portNumber") + "/"+ dbParams.get("dbName"), connectionProps);
        }

        if(logToFile) {
            File logFile = new File(dbParams.get("logFileFolder") + "/logFile.txt");
            if (!logFile.exists()) {
                    logFile.createNewFile();
            }
            FileHandler fh = new FileHandler(dbParams.get("logFileFolder") + "/logFile.txt");
            logger.addHandler(fh);
        }
    }
       
    /**
     * Function to log an error
     * @param messageText_in Message that will be saved
    */
    public void LogError(String messageText_in) throws Exception {
        Log(messageText_in,false,false,true);
    }
      
    /**
     * Function to log a Message
     * @param messageText_in Message that will be saved
    */
    public void LogMessage(String messageText_in) throws Exception {
        Log(messageText_in,true,false,false);
    }
     
    /**
     * Function to log a Warning
     * @param messageText_in Message that will be saved
    */
    public void LogWarning(String messageText_in) throws Exception {
        Log(messageText_in,false,true,false);
    }

    /**
     * Function to log a Message, Error or Warning
     * @param messageText_in Message that will be saved
     * @param message It defines if it will log a message
     * @param warning It defines if it will log a warning
     * @param error It defines if it will log an error
    */
    void Log(String messageText_in, boolean message, boolean warning, boolean error) throws Exception {
            String messageText = messageText_in.trim();

        //Message validation
        if (messageText == null || messageText.length() == 0) {
                throw new Exception("Message must not be null");
        }

        //Log type validation
        if (!message && !warning && !error) {
                throw new Exception("Error or Warning or Message must be specified");
        }

        //Message builder
        String l = null;
        if (error && logError) {
                l = "error " + DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) +" "+ messageText;
        }

        if (warning && logWarning) {
                l = "warning " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) +" "+ messageText;
        }

        if (message && logMessage) {
                l = "message " +DateFormat.getDateInstance(DateFormat.LONG).format(new Date()) +" "+ messageText;
        }

        //Logging in to console and file
        logger.log(Level.INFO, l);

        //Logging in to database
        if(logToDatabase) {
            try{
            int t = 0;
            if (message && logMessage) {
                t = 1;
            }
            if (error && logError) {
                t = 2;
            }
            if (warning && logWarning) {
                t = 3;
            }
            Statement stmt = connection.createStatement();
            String sentence = "insert into \"Log_Values\" values ('" + l + "', " + String.valueOf(t) + ")";
            stmt.executeUpdate(sentence);
            stmt.close();
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}

//comentarios:
/*
- El metodo LogMessage no puede ser estático ya que requiere de parámetros enviados en el contructor del objeto
- La variable initialized no se usa
- La linea messageText.trim(); no hace ninguna operación, se debe cambiar por messageText= messageText.trim();
- La validación que produce la excepción new Exception("Invalid configuration"); debería hacerse en el constructor, ya que es donde se ingresan los datos validados
- Se debe documentar el constructor y el método para que sea utilizado correctamente


- la línea stmt.executeUpdate("insert into Log_Values('" + message + "', " + String.valueOf(t) + ")"); invoca erroneamente la variable message, ya que esta es de tipo boolean, debe ser reemplazada pormessageText
- De la forma que esta planteado el método LogMessage, si se invoca dos tipos de log por ejemplo error  message, en la base de datos se almacenacomo solo warning y el log quedaria con el mensaje duplicado
- La inicialización de la conexión a la base de datos se recomineda realizarla en el constructor de la clase, ya que de esta forma se ahorran recursos al memoento de ejecutar varias veces el metodo LogMessage.
- (!logError && !logMessage && !logWarning) se debe validar en el constructor, ya que es donde se ingresa la información
- Agregar espacio en el log, ya que como esta actualmente quedaria pegado el mensaje a la fecha
- la construccion del mensaje deberia se de la siguiente l = "message ", ya de si se vuelve a pegar el contenido de l, quedaria el mensaje duplicado en algunas ocasiones
- Adicionalmente, la clase quedaría un poco mas transparente para el usuario si se crean 3 metodos, uno para registrar error, mensajes y alertas
- Excepción si el mensaje es nulo
- Si logToDatabaseParam es null, y se envian los parametros de conección nulos, se egneraría un error
- Se deberia registrar la variable l, que contiene el mensaje enviado y datos de fecha
- Handle Logger debe ser declarado en el constructor
- Se debería incluir el parametro del nombre de la base de datos, por si no se desea tomar la bd por defecto
- El nombre de la tabla debe ir "Log_Values" ya que discrimina mayusculas y minusculas
- La sentencia insert es incorrecta, reemplazar por "insert into \"Log_Values\" values ('" + l + "', " + String.valueOf(t) + ")";


*/

