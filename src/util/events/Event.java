package util.events;

/**
 * Abstract event class.
 * 
 * @author Amir Eslampanah
 * 
 */
public abstract class Event {

    /**
     * How long ago since we last ran this event in Unix milliseconds
     */
    private long lastRun;

    /**
     * Determines whether this event is currently running
     */
    protected boolean isRunning;

    /**
     * Runs the event.
     */
    public void run() {
	this.lastRun = System.currentTimeMillis();
	execute();
    }

    /**
     * Subclasses of the event class can put their own code here.
     * 
     * An event should return true upon success
     * 
     * If an event is not successful; it should log an error using the logging
     * system.
     * 
     * @return Whether or not this event executed successfully.
     */
    public abstract boolean execute();

    /**
     * @return Whether this event is currently running or not.
     */
    public boolean isRunning() {
	return this.isRunning;
    }

    /**
     * @return The Unix time stamp of when this event was last run.
     */
    public long lastRun() {
	return this.lastRun;
    }
}
