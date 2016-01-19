package log;

/**
 * This class handles error/warning logging for the entire program.
 * 
 * @author Amir Eslampanah
 * 
 */
public class LoggingHandler {

    /**
     * Logging Levels dictate what kinds of messages will be written to the disk
     * log.
     * 
     * Level 0: Only Critical Errors Level 1: Only Errors Level 2: Serious
     * Warnings and Errors Level 3: Warnings and Errors. Level 4: Everything.
     * 
     * Default: 4. (It is good to leave this high at default given how new this
     * client is)
     */
    private final byte level = 4;

    /**
     * 
     */
    public LoggingHandler() {
    }

    /**
     * Writes the contents of the StringBuffer to disk, depending on the logging
     * level
     * 
     * @param toLog
     *            The contents we wish to write to disk
     * @param lvl
     *            What is the minimum level for writing this message to disk.
     */
    public void log(StringBuffer toLog, byte lvl) {

	if (lvl <= this.level) {
	    // We write to disk

	} // otherwise we ignore this

    }

}
