package edu.utulsa.forbes.evocomp.logging;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

/**
 * This class handles logging for the SGA
 * 
 * @author Evan Forbes
 *
 */
public class Log {
	private BufferedWriter log;
	private DateFormat formatter;

	private boolean printLog = true;
	private Level level;
	
	private boolean enabled;

	public enum Level {
		DEBUG, INFO, ERROR, NOTHING;
	}

	/**
	 * Creates a log
	 * @param level the minimum level of messages to be shown.
	 * @param printLog If true, log messages will be printed to System.out
	 */
	public Log(Level level, boolean printLog, boolean enabled) {
		this.level = level;
		this.printLog = printLog;
		this.enabled = enabled;

		if(!enabled) return;
		formatter= new SimpleDateFormat("MM.dd.yy HH:mm:ss.SSS");

		try {
			FileWriter output = new FileWriter("logs/log "+System.currentTimeMillis()+".txt");
			log = new BufferedWriter(output);
			String initMessage = "Genetic Algorithm Log ["+level.toString()+" level]: "+getFormatedDate()+"\n\n";
			log.append(initMessage);
			if(printLog) System.out.print(initMessage);
		} catch(IOException e) {
			System.err.println("Log error: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * Writes a debug level message to the log
	 * @param message
	 */
	public void d(String message) {
		if(!enabled) return;
		if (level==Level.DEBUG) {
			try {
				String infoMessage = "[D] "+getFormatedDate()+": "+message+"\n";
				log.append(infoMessage);
				if(printLog) System.out.print(infoMessage);
			} catch (IOException e) {
				System.err.println("Log error: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes an info level message to the log
	 * @param message
	 */
	public void i(String message) {
		if(!enabled) return;
		if (level==Level.INFO||level==Level.DEBUG) {
			try {
				String infoMessage = "[I] "+getFormatedDate()+": "+message+"\n";
				log.append(infoMessage);
				if(printLog) System.out.print(infoMessage);
			} catch (IOException e) {
				System.err.println("Log error: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Writes an error level message to the log
	 * @param message
	 */
	public void e(String message) {
		if(!enabled) return;
		if(level!=Level.NOTHING) {
			try {
				String errorMessage = "[E] "+getFormatedDate()+": "+message+"\n";
				log.append(errorMessage);
				if(printLog) System.err.print(errorMessage);
			} catch (IOException e) {
				System.err.println("Log error: "+e.getMessage());
				e.printStackTrace();
			}
		}
	}

	/**
	 * Closes the log. This must be called before the program exits.
	 */
	public void close() {
		if(!enabled) return;
		try {
			String closeMessage = "\nGenetic Algorithm Log has been closed: "+getFormatedDate()+"\n";
			log.append(closeMessage);
			if(printLog) System.out.print(closeMessage);
			
			log.close();
		} catch (IOException e) {
			System.err.println("Log error: "+e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * 
	 * @return the current time, formatted
	 */
	private String getFormatedDate() {
		return formatter.format(new Date(System.currentTimeMillis()));
	}
}
