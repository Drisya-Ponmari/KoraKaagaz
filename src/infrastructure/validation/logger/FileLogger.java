package infrastructure.validation.logger;

import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;

/**
 * FileLogger class that is part of loggerManager,
 * and used for writing log entries to a log file
 * Implements ILogger interface
 * 
 * @author Navaneeth M Nambiar
 */
public class FileLogger implements ILogger {

	/** stores the format specified for the time-stamp */
	private DateTimeFormatter timeStampFormat;
		
	/** string that holds the location of the log file */
	private static String logFile;
	
	/** */
	private boolean enableErrorLog = false;
	
	/** */
	private boolean enableWarnLog = false;

	/** */
	private boolean enableSuccessLog = false;

	/** */
	private boolean enableInfoLog = false;

	/**
	 *  constructor for FileLogger
	 *  protected type since it needs to be only invoked by LoggerManager class
	 *  @see logger.LoggerManager
	 */
	protected FileLogger() {
		
		// sets DateTime format as per the spec
		timeStampFormat = DateTimeFormatter.ofPattern("yyyy:MM:dd HH:mm:ss");
		
		// sets the path to the file
		String home = System.getProperty("user.home");
		String logFilePath = home+"/.config/";
		
		// sets the logFilename as per the spec
		DateTimeFormatter logFilenameFormat;
		logFilenameFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
		
		LocalDateTime now = LocalDateTime.now();
		String logTimeStamp = now.format(logFilenameFormat);
		
		// set the path to the log file
		logFile = logFilePath+logTimeStamp+"-release.log";
	}

	@Override
	synchronized public void log(ModuleID moduleIdentifier, LogLevel level, String message) {

		LocalDateTime now = LocalDateTime.now();
		String formatDateTime = now.format(timeStampFormat);
		
		String logTimeStamp = "["+formatDateTime+"] ";
		
		String logModulePart = "["+moduleIdentifier.toString()+"] ";
		
		String logLevelPart = "["+level.toString()+"] ";
		
		String logMessage = logTimeStamp+logModulePart+logLevelPart+message;

		writeToFile(logMessage);
	}

	/** private helper method that returns an object that can write content into a file 
	 *  throws IOException error upon failure which is to be handled by the parent
	 */
	private static PrintWriter openFile(String filename) throws IOException {
		
		FileWriter fileWriter = new FileWriter(filename, true);
		PrintWriter printWriter = new PrintWriter(fileWriter);
		return printWriter;
	}

	/** private helper method that opens the logFile, 
	 *  appends the message to the file and closes the file
	 *  Handles IOException errors
	 */
	private static void writeToFile(String logMessage) {
		
		PrintWriter printWriter = null;
		try {
			printWriter = openFile(logFile);
			printWriter.printf(logMessage);
		} catch (IOException e) {
			// check for console to print to
			// if unavailable, do nothing
			if(System.console() != null) {
				System.err.println(" Caught IOException: " + e.getMessage());
			}
		}
		finally {
			closeFile(printWriter);
		}
	}
	
	/** private helper method that is used to properly close a file,
	 *  if it is open.
	 */
	private static void closeFile(PrintWriter p) {
		
		if(p != null) {
			p.close();
		}
		else {
			// check for console to print to
			// if unavailable, do nothing
			if(System.console() != null) {
				System.out.println(" PrintWriter not open ");
			}
		}
	}
}
